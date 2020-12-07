package com.chrastly.myfinance;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class MonthlyExpenseFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private ExpenseGeneralData expenseGeneralData;
    private ArrayList<OverallExpenseData> overallExpenseDataArrayList;
    private ArrayList<String> generalDataCategoryArrayList;
    private BarChart barChart;
    private PieChart pieChart;
    private TextView chartTitle;
    private HashMap<String,ArrayList> yearMonthHashMap;
    private ArrayList<String> yearArraylist = new ArrayList<>();
    private ArrayList<String> monthArrayList = new ArrayList<>();
    private String yearQuery ="", monthQuery ="";
    private Spinner yearSpinner, monthSpinner;
    private Boolean barChartStatus = true;
    private ArrayList<OverallExpenseData> z;
    public MonthlyExpenseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle bundle = getArguments();

        if(bundle!= null && bundle.containsKey("OVERALL_DATA")){
            String loadedData = this.getArguments().getString("OVERALL_DATA");
            Gson loadedDataGson = new Gson();
            overallExpenseDataArrayList = loadedDataGson.fromJson(loadedData, new TypeToken<ArrayList<OverallExpenseData>>() {
            }.getType());;

        }

        View view = inflater.inflate(R.layout.fragment_monthly_expense, container, false);

        File file = new File(getActivity().getFilesDir(), "MyFinanceGeneralData");

        if(file.exists()){

            String loadedString = loadExpenseGeneralData("MyFinanceGeneralData");
            Gson returnedGson = new Gson();
            expenseGeneralData = returnedGson.fromJson(loadedString,
                    new TypeToken<ExpenseGeneralData>() {}.getType());

            yearMonthHashMap = expenseGeneralData.getYearMonthHashMap();
            generalDataCategoryArrayList = expenseGeneralData.getCategoryArrayList();

            dataQuery();
        }

        yearSpinner = view.findViewById(R.id.yearSpinner);
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, yearArraylist);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);
        yearSpinner.setOnItemSelectedListener(this);

        monthSpinner = view.findViewById(R.id.monthSpinner);

        Log.i("MONTH ARRAY", String.valueOf(!monthArrayList.isEmpty()));

        monthSpinner.setOnItemSelectedListener(this);


        chartTitle = view.findViewById(R.id.chartTitle);
        barChart = view.findViewById(R.id.monthlyExpenseBarChart);
        barChart.setNoDataText("");
        pieChart = view.findViewById(R.id.monthlyExpensePieChart);
        pieChart.setNoDataText("");

        Button monthlyExpenseReviewButton = view.findViewById(R.id.monthlyExpenseReviewQueryButton);
        monthlyExpenseReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!yearQuery.equals("") && !monthQuery.equals("") ){

                    if (barChartStatus == true){
                        showBarChart();
                    }else{
                        showPieChart();
                    }

                }
            }
        });

        Button overviewButton = view.findViewById(R.id.overviewButton);
        overviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBarChart();
            }
        });

        Button categoryButton = view.findViewById(R.id.categoryButton);
        categoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPieChart();
            }
        });

        // Inflate the layout for this fragment
        return view;
    }


    public void dataQuery(){
        for(String year : yearMonthHashMap.keySet()){

            yearArraylist.add(year);
            monthArrayList = yearMonthHashMap.get(year);

        }
    }

    public String loadExpenseGeneralData(String FILE_NAME) {

        Gson gson = new Gson();
        String stringResponse = "";

        File file = new File(getActivity().getFilesDir(), FILE_NAME);

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

    public void showBarChart(){
        barChartStatus = true;

        pieChart.clear();
        chartTitle.setText("");
        chartTitle.setText("Total Daily Expense For " + monthIdentifier(monthQuery) +" "+yearQuery);

        HashMap<String,ArrayList<Expense>> chosenExpenseHashMap = new HashMap<>();

        for(int i=0; i<overallExpenseDataArrayList.size();i++){
            if(overallExpenseDataArrayList.get(i).getYear().equals(yearQuery) &&
                    overallExpenseDataArrayList.get(i).getMonth().equals(monthQuery)){

                chosenExpenseHashMap = overallExpenseDataArrayList.get(i).getDailyTotalHashMap();
            }
        }

        ArrayList<String> xAxisLabel= new ArrayList<>();
        ArrayList<BarEntry> dataValue = new ArrayList<>();

        int i = 0;
        for (String key : chosenExpenseHashMap.keySet()){
            xAxisLabel.add(key);
            ArrayList<Expense> dailyExpense = chosenExpenseHashMap.get(key);
            Float expenseAmount = 0.00f;

            for(int j=0; j<dailyExpense.size(); j++){
                expenseAmount += Float.parseFloat(dailyExpense.get(j).getExpenseAmount());
            }

            dataValue.add(new BarEntry(Integer.parseInt(key), expenseAmount));
            i++;
        }


        BarDataSet barDataSet = new BarDataSet(dataValue, "Daily Expense");
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

    public ArrayList<Integer> monthIdentifierArray(ArrayList<String> monthArrayList){

        ArrayList monthArrayListName = new ArrayList();

        for (String month: monthArrayList){
            Integer monthInt = Integer.parseInt(month);
            monthArrayListName.add(monthInt);
        }

        return monthArrayListName;
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId() == R.id.yearSpinner){

            yearQuery = parent.getItemAtPosition(position).toString();

            if (!monthArrayList.isEmpty()) {
                ArrayAdapter<String> monthAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, monthArrayList);
                monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                monthSpinner.setAdapter(monthAdapter);
            }

        }else if(parent.getId() == R.id.monthSpinner){

            monthQuery = parent.getItemAtPosition(position).toString();
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void showPieChart(){
        barChartStatus = false;

        barChart.clear();
        chartTitle.setText("");
        chartTitle.setText("Monthly Expense For " + monthIdentifier(monthQuery) +" "+yearQuery);

        HashMap<String,String> chosenExpenseHashMap = new HashMap<>();

        for(int i=0; i<overallExpenseDataArrayList.size();i++){
            if(overallExpenseDataArrayList.get(i).getYear().equals(yearQuery) &&
                    overallExpenseDataArrayList.get(i).getMonth().equals(monthQuery)){

                chosenExpenseHashMap = overallExpenseDataArrayList.get(i).getExpenseHashMap();
            }
        }


        ArrayList<PieEntry> dataValue = new ArrayList<>();

        int i = 0;
        for (String key : chosenExpenseHashMap.keySet()){
            Float expenseAmount = Float.parseFloat(chosenExpenseHashMap.get(key));
            dataValue.add(new PieEntry(expenseAmount,key));
            i++;
        }

        PieDataSet pieDataSet = new PieDataSet(dataValue, "Monthly Expense");
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(16f);

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.getDescription().setText("Monthly Expense");
        pieChart.animateY(2000);

        pieChart.setDrawEntryLabels(true);
        pieChart.setUsePercentValues(true);
        pieChart.setCenterText("Monthly Expense Breakdown");
        pieChart.setCenterTextSize(10);
        pieChart.setCenterTextRadiusPercent(50);
        pieChart.setHoleRadius(40);
        pieChart.setTransparentCircleRadius(20);
        pieChart.setNoDataText("");

    }

    public Integer numOfDay(String month, String year){
        Integer monthInt = Integer.parseInt(month);
        Integer yearInt = Integer.parseInt(year);
        Integer returnInt = 0;

        if(monthInt <= 7){
            if((monthInt % 2) == 1){
                returnInt = 31;
            }else if((monthInt == 2) && (yearInt % 4 != 0)){
                returnInt = 28;
            }else if((monthInt == 2) && (yearInt % 4 == 0)) {
                returnInt = 29;
            }else
                returnInt = 30;
        }

        if(monthInt >= 8){
            if((monthInt % 2) == 0) {
                returnInt = 31;
            }else {
                returnInt = 30;
            }

        }

        return returnInt;
    }

}