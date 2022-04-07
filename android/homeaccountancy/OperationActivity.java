package ru.nedovizin.homeaccountancy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.Observable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import javax.sql.PooledConnection;

import ru.nedovizin.homeaccountancy.database.BaseLab;
import ru.nedovizin.homeaccountancy.databinding.CardCategoryBinding;
import ru.nedovizin.homeaccountancy.models.Category;
import ru.nedovizin.homeaccountancy.models.Operation;
import ru.nedovizin.homeaccountancy.models.TypeOperation;

public class OperationActivity extends AppCompatActivity {
    private Operation mOperation;
    private CategoryAdapter adapter;
    private List<Category> categories;
    private TypeOperation mTypeOperation;
    private final String TAG = ".OperationActivity";
    private TextView selectedCategory;
    private Button operationAddButton;
    private EditText operationValue;
    private Period mPeriod;
    private TextView exposeButton;
    private TextView incomeButton;
    private BaseLab baseLab;
    private RecyclerView operationsRecyclerview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.operation_activity);
        selectedCategory = findViewById(R.id.operation_select_category_name);
        operationAddButton = findViewById(R.id.operation_add);
        operationValue = findViewById(R.id.operation_value);
        exposeButton = findViewById(R.id.expose_button);
        incomeButton = findViewById(R.id.income_button);

        Intent intent = getIntent();
        mOperation = new Operation();
        mPeriod = new Period(intent.getStringExtra("Period"));
        mTypeOperation = TypeOperation.valueOf(intent.getStringExtra("TypeOperation"));

        operationsRecyclerview = findViewById(R.id.operation_recycler_view);
        int numberOfColumns = 6;
        operationsRecyclerview.setLayoutManager(new GridLayoutManager(this, numberOfColumns));

        baseLab = BaseLab.get(getBaseContext());
        CategoriesPool.Create(mTypeOperation);

        updateUI();

        operationAddButton.setOnClickListener(view -> {
            Integer value = Integer.parseInt(operationValue.getText().toString());
            if (mOperation == null) {
                // TODO: 15.03.2022 Показать сообщение, что надо выбрать категорию
                return;
            }
            if (value == 0) {
                // TODO: 15.03.2022 Показать сообщение, что сумма не должна быть нулевой
                return;
            }
            mOperation.setValue(value);
            mOperation.setPeriod(mPeriod);
            mOperation.getCategory().setPeriod(mPeriod);
            baseLab.addCategory(mOperation.getCategory());
            baseLab.addOperation(mOperation);
            this.onBackPressed();
        });

        exposeButton.setOnClickListener(view1 -> {
            mTypeOperation = TypeOperation.EXPOSE;
            updateUI();
        });

        incomeButton.setOnClickListener(view1 -> {
            mTypeOperation = TypeOperation.INCOME;
            updateUI();
        });
    }

    private class CategoryHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private CardCategoryBinding mBinding;

        public CategoryHolder(CardCategoryBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
            mBinding.setViewModel(new CategoryViewModel(mOperation));
            mBinding.getViewModel().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(Observable sender, int propertyId) {
                    if (mOperation.getCategory() != null) {
                        selectedCategory.setText(mOperation.getCategory().getName());
                    }
                }
            });
        }

        public void bind(Category category) {
            mBinding.getViewModel().setCategory(category);

            //Вызывая этот метод, вы приказываете макету
            //обновить себя немедленно вместо того, чтобы ожидать одну-две миллисекунды.
            //Таким образом обеспечивается быстрота реакции RecyclerView
            mBinding.executePendingBindings();
        }

        @Override
        public void onClick(View view) {
            // TODO: 01.03.2022 выделить выбранную категорию
            Log.d(TAG, "Выбрана категория: ".concat(mBinding.getViewModel().getTitle()));
            mOperation.setCategory(mBinding.getViewModel().getCategory());
        }
    }

    private class CategoryAdapter extends RecyclerView.Adapter<CategoryHolder> {

        private List<Category> mCategories;

        public CategoryAdapter(List<Category> categories) {
            mCategories = categories;
        }

        @NonNull
        @Override
        public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = getLayoutInflater();
            CardCategoryBinding binding = DataBindingUtil
                    .inflate(inflater, R.layout.card_category, parent, false);
            return new CategoryHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull CategoryHolder categoryHolder, int position) {
            Category category = mCategories.get(position);
            categoryHolder.bind(category);
        }

        @Override
        public int getItemCount() {
            return mCategories.size();
        }
    }

    private void updateUI() {
        categories = baseLab.getAllCategories(mTypeOperation);
        adapter = new CategoryAdapter(categories);
        operationsRecyclerview.setAdapter(adapter);

        if (mTypeOperation == TypeOperation.EXPOSE) {
            setActiveExpose();
        } else {
            setActiveIncome();
        }
    }

    private void setActiveExpose() {
        exposeButton.setPaintFlags(exposeButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        incomeButton.setPaintFlags(exposeButton.getPaintFlags() ^ Paint.UNDERLINE_TEXT_FLAG);
    }

    private void setActiveIncome() {
        incomeButton.setPaintFlags(incomeButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        exposeButton.setPaintFlags(incomeButton.getPaintFlags() ^ Paint.UNDERLINE_TEXT_FLAG);
    }
}