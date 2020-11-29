package com.chrastly.myfinance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Element;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShowExpenseStatisticActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private ExpenseGeneralData expenseGeneralData;
    private ArrayList<String> generalDataCategoryArrayList, generalDataYearArrayList,
            generalDataMonthArrayList;
    private ArrayList<OverallExpenseData> overallExpenseDataArrayList = new ArrayList<OverallExpenseData>();
    private BarChart barChart;
    private TextView barChartTitle;
    private Fragment selectedFragment;
    private HashMap<String,ArrayList> yearMonthHashMap;

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

            /*showBarChart("2020","11");*/

            Log.i("TAG",overallExpenseDataArrayList.get(0).getExpenseHashMap().get("No Category"));

        }

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        TabItem tabMonthlyExpenseReview = findViewById(R.id.monthlyExpenseReview);
        TabItem tabYearlyExpenseReview = findViewById(R.id.yearlyExpenseReview);
        final ViewPager viewPager = findViewById(R.id.viewPager);

        ExpenseReviewPagerAdapter expenseReviewPagerAdapter = new ExpenseReviewPagerAdapter(getSupportFragmentManager());
        expenseReviewPagerAdapter.addFragment(new MonthlyExpenseFragment(), "Monthly Expense Review");
        expenseReviewPagerAdapter.addFragment(new YearlyExpenseFragment(), "Yearly Expense Review");

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
                    switch (item.getItemId()){

                        case R.id.dailyExpense:
                            Intent intent = new Intent(ShowExpenseStatisticActivity.this, MainActivity.class);
                            overridePendingTransition(0,0);
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

        File file = new File(getFilesDir().toString());
        File [] fileList = file.listFiles();

        for (int position = 0; position < fileList.length; position++){

             String fileName = fileList[position].getName();

             if(fileName.substring(0,16).equals("MyFinanceExpense")){

                 String fileNameYear = fileName.substring(16,20);
                 String fileNameMonth = fileName.substring(21,23);

                     if(fileNameYear.equals(yearQuery) ){

                         if(fileNameMonth.equals(monthQuery)){

                             String loadedString = statisticLoadExpense(fileName);
                             Gson returnedGson = new Gson();
                             ArrayList<Expense> existingExpenseArrayList = returnedGson.fromJson(loadedString,
                                     new TypeToken<ArrayList<Expense>>() {}.getType());

                             for(int i=0; i<existingExpenseArrayList.size();i++){
                                 expenseAmountCategorySort(expenseHashMap, existingExpenseArrayList.get(i).getExpenseCategory(),
                                         existingExpenseArrayList.get(i).getExpenseAmount());
                             }
                         }
                     }
                 }
             }

       /* for(int i =0; i<generalDataCategoryArrayList.size();i++){
            String expense = expenseHashMap.get(generalDataCategoryArrayList.get(i));

            if(expense !=""){
                categoryArrayList.add(generalDataCategoryArrayList.get(i));
                amountArrayList.add(expense);
            }

        }*/

        OverallExpenseData overallExpenseData = new OverallExpenseData();
        overallExpenseData.setYear(yearQuery);
        overallExpenseData.setMonth(monthQuery);
        overallExpenseData.setExpenseHashMap(expenseHashMap);

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

            ArrayList<String> monthArrayList = yearMonthHashMap.get(year);

            for(int month=0; month<monthArrayList.size(); month++){

                expenseDataSort(year,monthArrayList.get(month));

            }
        }
    }


    public void showBarChart(String yearQuery, String monthQuery){

       /* barChart = findViewById(R.id.monthlyExpenseBarChart);
        barChartTitle = findViewById(R.id.monthlyExpenseBarChartTitle);*/

        barChartTitle.setText("Monthly Expense For " + monthIdentifier(monthQuery) +" "+yearQuery);

        HashMap<String,String> chosenExpenseHashMap = new HashMap<>();

        for(int i=0; i<overallExpenseDataArrayList.size();i++){
            if(overallExpenseDataArrayList.get(i).getYear().equals(yearQuery) &&
            overallExpenseDataArrayList.get(i).getMonth().equals(monthQuery)){

                chosenExpenseHashMap = overallExpenseDataArrayList.get(i).getExpenseHashMap();
            }
        }

        ArrayList<String> xAxisLabel= new ArrayList<>();
        ArrayList<BarEntry> dataValue = new ArrayList<>();
        int i = 0;


       for (String key : chosenExpenseHashMap.keySet()){
           xAxisLabel.add(key);
           Float expenseAmount = Float.parseFloat(chosenExpenseHashMap.get(key));
           dataValue.add(new BarEntry(i,expenseAmount));
           i++;
       }

        BarDataSet barDataSet = new BarDataSet(dataValue, "Monthly Expense");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);

        BarData barData = new BarData(barDataSet);
        barChart.setFitBars(true);
        barChart.setData(barData);
        barChart.getDescription().setText("Monthly Expense");
        barChart.animateY(2000);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisLabel));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularityEnabled(true);
        xAxis.setCenterAxisLabels(false);

        YAxis yAxisLeft = barChart.getAxisLeft();
        yAxisLeft.setAxisMinimum(0f);

        YAxis yAxisRight = barChart.getAxisRight();
        yAxisRight.setEnabled(false);

        Log.i("label", xAxisLabel.toString());

    }

    public String monthIdentifier(String month){
        Integer monthInt = Integer.parseInt(month);
        return new DateFormatSymbols().getMonths()[monthInt-1];

    }



}