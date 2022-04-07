package ru.nedovizin.homeaccountancy;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import ru.nedovizin.homeaccountancy.database.BaseLab;
import ru.nedovizin.homeaccountancy.models.Operation;
import ru.nedovizin.homeaccountancy.models.TypeOperation;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {
    private PieChart diagram;
    private FloatingActionButton addBt;
    private TextView exposeButton;
    private TextView incomeButton;
    private ImageButton currentMonthPrevButton;
    private ImageButton currentMonthNextButton;
    private TextView currentMonthSelect;
    private Period mPeriod;
    private TypeOperation currentTypeOperation;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        exposeButton = view.findViewById(R.id.expose_button);
        incomeButton = view.findViewById(R.id.income_button);
        currentMonthPrevButton = view.findViewById(R.id.current_month_prev);
        currentMonthNextButton = view.findViewById(R.id.current_month_next);
        currentMonthSelect = view.findViewById(R.id.current_month_select);
        mPeriod = new Period();
        currentTypeOperation = TypeOperation.EXPOSE;

        diagram = view.findViewById(R.id.chart_diagram);
        addBt = view.findViewById(R.id.fab_diagram);

        addBt.setOnClickListener(v -> {
            // Добавить операцию
            Intent intent = new Intent(this.getContext(), OperationActivity.class);
            intent.putExtra("Period", mPeriod.getFormatLine());
            intent.putExtra("TypeOperation", currentTypeOperation.toString());
            startActivity(intent);
        });

        exposeButton.setOnClickListener(view1 -> {
            setActiveExpose();
        });

        incomeButton.setOnClickListener(view1 -> {
            setActiveIncome();
        });

        currentMonthPrevButton.setOnClickListener(view1 -> {
            mPeriod = mPeriod.getPrev();
            updateUI();
        });

        currentMonthNextButton.setOnClickListener(view1 -> {
            mPeriod = mPeriod.getNext();
            updateUI();
        });

        currentMonthSelect.setOnClickListener(view1 -> {
            // Date Select Listener.
            DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year,
                                      int monthOfYear, int dayOfMonth) {
                    mPeriod = new Period(monthOfYear + 1, year);
                    updateUI();
                }
            };
            // Create DatePickerDialog:
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    android.R.style.Theme_Material_Dialog_NoActionBar,
                    dateSetListener, mPeriod.getYear(), mPeriod.getMonth() - 1, 1);
            datePickerDialog.show();
        });

        setActiveExpose();
        updateUI();
        return view;
    }

    private void showDiagramExpose() {
        currentTypeOperation = TypeOperation.EXPOSE;
        String label = "Расходы";
        BaseLab baseLab = BaseLab.get(getActivity());
        List<Operation> operations = baseLab.getOperationByPeriod(mPeriod).stream().filter(operation ->
                operation.getCategory().getType() == TypeOperation.EXPOSE).collect(Collectors.toList());
        showDiargamm(label, operations);
    }

    private void showDiagramIncome() {
        currentTypeOperation = TypeOperation.INCOME;
        String label = "Доходы";
        BaseLab baseLab = BaseLab.get(getActivity());
        List<Operation> operations = baseLab.getOperationByPeriod(mPeriod).stream().filter(operation ->
                operation.getCategory().getType() == TypeOperation.INCOME).collect(Collectors.toList());
        showDiargamm(label, operations);
    }

    private void showDiargamm(String label, List<Operation> operations) {
        // Массив координат точек
        ArrayList<PieEntry> entries = new ArrayList<>();
        AtomicInteger sum = new AtomicInteger();
        operations.forEach(operation -> {
            entries.add(new PieEntry(operation.getValue(), operation.getCategory().getName()));
            sum.addAndGet(operation.getValue());
        });

        PieDataSet dataset = new PieDataSet(entries, label);
        dataset.setSliceSpace(3f);
        dataset.setSelectionShift(5f);
        dataset.setColors(ColorTemplate.COLORFUL_COLORS);

        PieData data = new PieData(dataset);
        data.setValueTextSize(20f);
        data.setValueTextColor(Color.YELLOW);

        diagram.setData(data);
        diagram.setCenterTextTypeface(Typeface.DEFAULT_BOLD);
        diagram.setCenterTextSize(30);
        diagram.setCenterText(sum.toString());
        diagram.setDrawCenterText(true);
        diagram.getLegend().setEnabled(false);
        diagram.getDescription().setEnabled(false);
        diagram.invalidate();
    }

    private void setActiveExpose() {
        exposeButton.setPaintFlags(exposeButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        incomeButton.setPaintFlags(exposeButton.getPaintFlags() ^ Paint.UNDERLINE_TEXT_FLAG);
        showDiagramExpose();
    }

    private void setActiveIncome() {
        incomeButton.setPaintFlags(incomeButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        exposeButton.setPaintFlags(incomeButton.getPaintFlags() ^ Paint.UNDERLINE_TEXT_FLAG);
        showDiagramIncome();
    }

    private void updateUI() {
        currentMonthSelect.setText(mPeriod.getFormatLine());
        if (currentTypeOperation == TypeOperation.EXPOSE) {
            setActiveExpose();
            showDiagramExpose();
        } else {
            setActiveIncome();
            showDiagramIncome();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }
}