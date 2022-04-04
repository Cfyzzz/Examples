package ru.nedovizin.testapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    final String SAVED_COUNTER = "saved_counter";
    final int NUMBER_COOL_START = 3;

    SharedPreferences sPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (checkCoolStart()) {
            Toast.makeText(this,
                    "Сделано ".concat(String.valueOf(NUMBER_COOL_START)).concat("  холодных запусков!"),
                    Toast.LENGTH_SHORT)
                    .show();
            resetCounter();
        } else {
            incrementCounter();
        }
    }

    private boolean checkCoolStart() {
        sPref = getPreferences(MODE_PRIVATE);
        return sPref.getInt(SAVED_COUNTER, 1) == NUMBER_COOL_START;
    }

    private void resetCounter() {
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sPref.edit();
        editor.putInt(SAVED_COUNTER, 1);
        editor.apply();
    }

    private void incrementCounter() {
        sPref = getPreferences(MODE_PRIVATE);
        int counter = sPref.getInt(SAVED_COUNTER, 1);
        SharedPreferences.Editor editor = sPref.edit();
        editor.putInt(SAVED_COUNTER, ++counter);
        editor.apply();
    }
}