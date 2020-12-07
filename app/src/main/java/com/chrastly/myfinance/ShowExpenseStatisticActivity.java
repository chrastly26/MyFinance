package com.chrastly.myfinance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import com.github.mikephil.charting.charts.BarChart;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
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
import java.util.ArrayList;
import java.util.HashMap;

public class ShowExpenseStatisticActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private ExpenseGeneralData expenseGeneralData;
    private ArrayList<String> generalDataCategoryArrayList;
    private ArrayList<OverallExpenseData> overallExpenseDataArrayList = new ArrayList<OverallExpenseData>();
    private HashMap<String,ArrayList> yearMonthHashMap;
    private ArrayList<String> yearArraylist = new ArrayList<>();
    private ArrayList<String> monthArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_expense_statistic);

        File file = new File(this.getFilesDir(), "MyFinanceGeneralData");

        if(file.exists()){

            String loadedString = loadExpenseGeneralData("MyFinanceGeneralData");
            Gson returnedGson = new Gson();
            expenseGeneralData = returnedGson.fromJson(loadedString,
                    new TypeToken<ExpenseGeneralData>() {}.getType());

            yearMonthHashMap = expenseGeneralData.getYearMonthHashMap();
            generalDataCategoryArrayList = expenseGeneralData.getCategoryArrayList();

            dataQuery();

        }

        Bundle bundle = new Bundle();
        Gson gson = new Gson();
        String dataToBePassed = gson.toJson(overallExpenseDataArrayList);
        bundle.putString("OVERALL_DATA", dataToBePassed);

        MonthlyExpenseFragment monthlyExpenseFragment = new MonthlyExpenseFragment();
        monthlyExpenseFragment.setArguments(bundle);
       /* getSupportFragmentManager().beginTransaction().replace(R.id.tabLayout, monthlyExpenseFragment).commit();
       */ Log.i("BUNDLE", overallExpenseDataArrayList.toString());

        YearlyExpenseFragment yearlyExpenseFragment = new YearlyExpenseFragment();
        yearlyExpenseFragment.setArguments(bundle);
        /*getSupportFragmentManager().beginTransaction().replace(R.id.tabLayout, yearlyExpenseFragment).commit();
*/

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        TabItem tabMonthlyExpenseReview = findViewById(R.id.monthlyExpenseReview);
        TabItem tabYearlyExpenseReview = findViewById(R.id.yearlyExpenseReview);
        final ViewPager viewPager = findViewById(R.id.viewPager);

        ExpenseReviewPagerAdapter expenseReviewPagerAdapter = new ExpenseReviewPagerAdapter(getSupportFragmentManager());
        expenseReviewPagerAdapter.addFragment(monthlyExpenseFragment, "Monthly Expense Review");
        expenseReviewPagerAdapter.addFragment(yearlyExpenseFragment, "Yearly Expense Review");

        viewPager.setAdapter(expenseReviewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        bottomNavigationView = findViewById(R.id.bottomNavigator);
        bottomNavigationView.setSelectedItemId(R.id.overallExpense);
        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavigationMethod);


    }

    public BottomNavigationView.OnNavigationItemSelectedListener bottomNavigationMethod = new
            BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()) {
                        case R.id.dailyExpense:

                            Intent intent = new Intent(ShowExpenseStatisticActivity.this, MainActivity.class);
                            overridePendingTransition(0, 0);
                            startActivity(intent);
                            finish();
                            break;

                        case R.id.overallExpense:

                            break;

                    }

                    return true;
                }
            };

    public void expenseDataSort(String yearQuery, String monthQuery){

        HashMap<String,String> expenseHashMap = new HashMap<>();
        HashMap<String,ArrayList<Expense>> dailyTotalHashMap = new HashMap<>();

        File file = new File(this.getFilesDir().toString());
        File [] fileList = file.listFiles();

        for (int position = 0; position < fileList.length; position++){

            String fileName = fileList[position].getName();

            if(fileName.substring(0,16).equals("MyFinanceExpense")){

                String fileNameYear = fileName.substring(16,20);
                String fileNameMonth = fileName.substring(21,23);
                String fileNameDate = fileName.substring(24,26);

                if(fileNameYear.equals(yearQuery) ){

                    if(fileNameMonth.equals(monthQuery)){

                        String loadedString = statisticLoadExpense(fileName);
                        Gson returnedGson = new Gson();
                        ArrayList<Expense> existingExpenseArrayList = returnedGson.fromJson(loadedString,
                                new TypeToken<ArrayList<Expense>>() {}.getType());

                        Double totalDailyExpense = 0.00;

                        for(int i=0; i<existingExpenseArrayList.size();i++){
                            expenseAmountCategorySort(expenseHashMap, existingExpenseArrayList.get(i).getExpenseCategory(),
                                    existingExpenseArrayList.get(i).getExpenseAmount());

                            totalDailyExpense += Double.parseDouble(existingExpenseArrayList.get(i).getExpenseAmount());
                        }

                        dailyTotalHashMap.put(fileNameDate,existingExpenseArrayList);
                    }
                }
            }
        }

        OverallExpenseData overallExpenseData = new OverallExpenseData();
        overallExpenseData.setYear(yearQuery);
        overallExpenseData.setMonth(monthQuery);
        overallExpenseData.setExpenseHashMap(expenseHashMap);
        overallExpenseData.setDailyTotalHashMap(dailyTotalHashMap);

        overallExpenseDataArrayList.add(overallExpenseData);
    }

    public String statisticLoadExpense(String fileName) {

        Gson gson = new Gson();
        String stringResponse = "";

        final String FILE_NAME = fileName;
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
        } catch (FileNotFoundException e){
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringResponse;

    }

    public void expenseAmountCategorySort(HashMap<String,String> expenseHashMap, String category, String expense){

        if(!expenseHashMap.keySet().contains(category)){
            expenseHashMap.put(category,expense);
        }else{
            Double tempDouble = Double.parseDouble(expenseHashMap.get(category)) + Double.parseDouble(expense);
            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            expenseHashMap.put(category,String.valueOf(Double.valueOf(decimalFormat.format(tempDouble))));

        }

    }

    public void dataQuery(){
        for(String year : yearMonthHashMap.keySet()){

            yearArraylist.add(year);
            monthArrayList = yearMonthHashMap.get(year);

            for(int month=0; month<monthArrayList.size(); month++){

                expenseDataSort(year,monthArrayList.get(month));

            }
        }
    }

    public String loadExpenseGeneralData(String FILE_NAME) {

        Gson gson = new Gson();
        String stringResponse = "";

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
        } catch (FileNotFoundException e){
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringResponse;

    }


}