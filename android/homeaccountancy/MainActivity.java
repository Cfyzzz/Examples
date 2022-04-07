package ru.nedovizin.homeaccountancy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

import ru.nedovizin.homeaccountancy.database.BaseHelper;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Button menueBt;
    private Button detailBt;
    private ImageButton prevMonth;
    private ImageButton nextMoth;
    private TextView incomeBt;
    private TextView exposeBt;

    private TextView currentMonth;
    private RecyclerView listOperations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        BaseHelper databaseHelper = BaseHelper.getInstance(this);

        menueBt = findViewById(R.id.menue_button);
        detailBt = findViewById(R.id.detail_button);
        prevMonth = findViewById(R.id.current_month_prev);
        nextMoth = findViewById(R.id.current_month_next);
        incomeBt = findViewById(R.id.income_button);
        exposeBt = findViewById(R.id.expose_button);

        currentMonth = findViewById(R.id.current_month_select);
        listOperations = findViewById(R.id.main_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.draw_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer,
                toolbar,
                R.string.nav_open_drawer,
                R.string.nav_close_drawer
        );
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Fragment fragment = new MainFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.content_frame, fragment);
        ft.commit();
    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Fragment fragment = null;

        switch (id) {
            case R.id.nav_main:
                fragment = new MainFragment();
                break;
            case R.id.nav_categories:
                fragment = new CategoriesFragment();
                break;
            case R.id.nav_move_reserve:
                fragment = new MoveReserveFragment();
                break;
            case R.id.nav_piggy_bank:
                fragment = new PiggiBankFragment();
                break;
            case R.id.nav_planning:
                fragment = new PlanningFragment();
                break;
            case R.id.nav_report:
                fragment = new ReportFragment();
                break;
            case R.id.nav_synchronize:
                fragment = new SynchronizeFragment();
                break;
            default:
                fragment = new MainFragment();
        }

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

        DrawerLayout drawer = findViewById(R.id.draw_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.draw_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}