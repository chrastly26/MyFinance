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
import java.util.ArrayList;
import java.util.HashMap;


public class YearlyExpenseFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private ExpenseGeneralData expenseGeneralData;
    private ArrayList<OverallExpenseData> overallExpenseDataArrayList;
    private ArrayList<String> generalDataCategoryArrayList;
    private BarChart barChart;
    private PieChart pieChart;
    private TextView chartTitle;
    private HashMap<String,ArrayList> yearMonthHashMap;
    private ArrayList<String> yearArrayList = new ArrayList<>();
    private ArrayList<String> monthArrayList = new ArrayList<>();
    private String yearQuery ="";
    private Spinner yearSpinner;
    private Boolean barChartStatus = true;

    public YearlyExpenseFragment() {

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

            Log.i("BUN", overallExpenseDataArrayList.toString());

        }

        View view = inflater.inflate(R.layout.fragment_yearly_expense, container, false);

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
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, yearArrayList);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);
        yearSpinner.setOnItemSelectedListener(this);

        chartTitle = view.findViewById(R.id.chartTitle);
        barChart = view.findViewById(R.id.yearlyExpenseBarChart);
        barChart.setNoDataText("");
        pieChart = view.findViewById(R.id.yearlyExpensePieChart);
        pieChart.setNoDataText("");

        Button yearlyExpenseReviewButton = view.findViewById(R.id.yearlyExpenseReviewQueryButton);
        yearlyExpenseReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!yearQuery.equals("") && !monthArrayList.isEmpty()){

                    if (barChartStatus == true) {
                        showBarChart();
                    } else {
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

            yearArrayList.add(year);
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

        HashMap<String,Float> categoryTotalExpenseByYear = new HashMap<String, Float>();
        HashMap<String,Float> monthTotalExpenseByYear = new HashMap<String, Float>();

        chartTitle.setText("Monthly Expense For " + yearQuery);

        for(int i=0; i<overallExpenseDataArrayList.size();i++){
            if(overallExpenseDataArrayList.get(i).getYear().equals(yearQuery)){

                monthTotalExpenseByYear.put(overallExpenseDataArrayList.get(i).getMonth(),
                       Float.parseFloat(overallExpenseDataArrayList.get(i).totalExpense()));

            }
        }

        ArrayList<String> xAxisLabel= new ArrayList<>();
        ArrayList<BarEntry> dataValue = new ArrayList<>();
        int i = 0;

        for (String key : monthTotalExpenseByYear.keySet()){
            xAxisLabel.add(key);
            Float expenseAmount = monthTotalExpenseByYear.get(key);
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


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        yearQuery = parent.getItemAtPosition(position).toString();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void showPieChart(){
        barChartStatus = false;

        barChart.clear();
        chartTitle.setText("");
        chartTitle.setText("Monthly Expense For " + " "+yearQuery);

        HashMap<String,Float> categoryTotalExpenseByYear = new HashMap<String, Float>();
        HashMap<String,String> chosenExpenseHashMap = new HashMap<>();

        for(int i=0; i<overallExpenseDataArrayList.size();i++){
            if(overallExpenseDataArrayList.get(i).getYear().equals(yearQuery)){

                chosenExpenseHashMap = overallExpenseDataArrayList.get(i).getExpenseHashMap();
                for (String key : chosenExpenseHashMap.keySet()){

                    if(!categoryTotalExpenseByYear.containsKey(key)){
                        categoryTotalExpenseByYear.put(key,Float.parseFloat(chosenExpenseHashMap.get(key)));

                    }else{
                        Float tempFloat = (categoryTotalExpenseByYear.get(key)) + Float.parseFloat(chosenExpenseHashMap.get(key));
                        categoryTotalExpenseByYear.put(key,tempFloat);
                    }

                }

            }
        }

        ArrayList<PieEntry> dataValue = new ArrayList<>();

        int i = 0;
        for (String key : categoryTotalExpenseByYear.keySet()){
            Float expenseAmount = categoryTotalExpenseByYear.get(key);
            dataValue.add(new PieEntry(expenseAmount,key));
            i++;
        }

        PieDataSet pieDataSet = new PieDataSet(dataValue, "yearly Expense");
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(16f);

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.getDescription().setText("Yearly Expense");
        pieChart.animateY(2000);

        pieChart.setDrawEntryLabels(true);
        pieChart.setUsePercentValues(true);
        pieChart.setCenterText("Yearly Expense Breakdown");
        pieChart.setCenterTextSize(10);
        pieChart.setCenterTextRadiusPercent(50);
        pieChart.setHoleRadius(40);
        pieChart.setTransparentCircleRadius(20);
        pieChart.setNoDataText("");

    }

}