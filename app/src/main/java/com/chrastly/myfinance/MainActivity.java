package com.chrastly.myfinance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements ExpenseRecyclerViewAdapter.OnExpenseListener {


    private Calendar calendar;
    private String selectedDate, loadedString, totalExpense;
    private ArrayList<Expense> expenseArrayList;
    private RecyclerView expenseRecyclerView;
    private ExpenseRecyclerViewAdapter adapter;
    private BottomNavigationView bottomNavigationView;
    private TextView totalExpenseTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calendar = Calendar.getInstance();
        SimpleDateFormat sdfDateTime = new SimpleDateFormat("yyyy-MM-dd-hh-mm");
        selectedDate = sdfDateTime.format(calendar.getTime());
        selectedDate = selectedDate.substring(0, 10);

        loadedString = loadExpense(selectedDate);
        if (loadedString != "") {
            Gson gson = new Gson();
            expenseArrayList = gson.fromJson(loadedString, new TypeToken<ArrayList<Expense>>() {
            }.getType());

            showRecyclerView(expenseArrayList);
            totalExpense = totalExpenseCalculate(expenseArrayList);
            totalExpenseSetView(totalExpense);

        }


        CalendarView calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;

                loadedString = loadExpense(selectedDate);
                if (loadedString != "") {
                    Gson gson = new Gson();
                    expenseArrayList = gson.fromJson(loadedString, new TypeToken<ArrayList<Expense>>() {
                    }.getType());

                    showRecyclerView(expenseArrayList);
                    totalExpense = totalExpenseCalculate(expenseArrayList);
                    totalExpenseSetView(totalExpense);

                }

            }
        });

        bottomNavigationView = findViewById(R.id.bottomNavigator);
        bottomNavigationView.setSelectedItemId(R.id.dailyExpense);
        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavigationMethod);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addExpense:
                Intent addExpensesIntent = new Intent(this, AddExpenseActivity.class);
                addExpensesIntent.putExtra("com.chrastly.myfinance.SELECTEDDATE", selectedDate);

                Toast.makeText(getApplicationContext(), selectedDate.toString(), Toast.LENGTH_SHORT).show();

                startActivity(addExpensesIntent);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    public String loadExpense(String selectedDate) {

        Gson gson = new Gson();
        String stringResponse = "";

        final String FILE_NAME = "MyFinanceExpense" + selectedDate;
        File file = new File(this.getFilesDir(), FILE_NAME);

        try {
            InputStream inputStream = new FileInputStream(file);
            StringBuilder stringBuilder = new StringBuilder();

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                String receiveString = "";

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                stringResponse = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringResponse;

    }

    @Override
    public void OnExpenseClick(int position) {
        Toast.makeText(getApplicationContext(), "click success", Toast.LENGTH_SHORT).show();
    }

    public void showRecyclerView(ArrayList<Expense> expenseArrayList) {
        expenseRecyclerView = findViewById(R.id.expenseRecyclerView);
        ExpenseRecyclerViewAdapter adapter = new ExpenseRecyclerViewAdapter(expenseArrayList, this);
        adapter.setExpenseArrayList(expenseArrayList);

        expenseRecyclerView.setAdapter(adapter);
        expenseRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

    }

    public BottomNavigationView.OnNavigationItemSelectedListener bottomNavigationMethod = new
            BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {

                        case R.id.dailyExpense:
                            break;


                        case R.id.overallExpense:
                            Intent intent = new Intent(MainActivity.this, ShowExpenseStatisticActivity.class);
                            overridePendingTransition(0,0);
                            startActivity(intent);
                            finish();
                            break;

                    }

                    return true;
                }
            };

    public String totalExpenseCalculate(ArrayList<Expense> expenseArrayList) {

        Double total = Double.valueOf(0);

        for (int position = 0; position < expenseArrayList.size(); position++) {
            total = total + Double.parseDouble(expenseArrayList.get(position).getExpenseAmount());

        }

        DecimalFormat decimalFormat = new DecimalFormat("0.00");

        return String.valueOf(Double.valueOf(decimalFormat.format(total)));
    }

    public void totalExpenseSetView(String totalExpense){
        totalExpenseTextView = findViewById(R.id.totalExpense);
        totalExpenseTextView.setText(totalExpense);
    }

}

