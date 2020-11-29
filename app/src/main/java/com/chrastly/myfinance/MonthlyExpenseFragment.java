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
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
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
    private ArrayList<OverallExpenseData> overallExpenseDataArrayList = new ArrayList<OverallExpenseData>();
    private ArrayList<String> generalDataCategoryArrayList;
    private BarChart barChart;
    private TextView barChartTitle;
    private HashMap<String,ArrayList> yearMonthHashMap;
    private ArrayList<String> yearArraylist = new ArrayList<>();
    private ArrayList<String> monthArrayList = new ArrayList<>();
    private String yearQuery ="", monthQuery ="";
    private Spinner yearSpinner, monthSpinner;

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

        barChart = view.findViewById(R.id.monthlyExpenseBarChart);
        barChartTitle = view.findViewById(R.id.monthlyExpenseBarChartTitle);


        Button monthlyExpenseReviewButton = view.findViewById(R.id.monthlyExpenseReviewQueryButton);
        monthlyExpenseReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!yearQuery.equals("") && !monthQuery.equals("") ){
                    showBarChart(yearQuery,monthQuery);
                }
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    public void expenseDataSort(String yearQuery, String monthQuery){

        HashMap<String,String> expenseHashMap = new HashMap<>();

        File file = new File(getActivity().getFilesDir().toString());
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

    public void showBarChart(String yearQuery, String monthQuery){

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


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId() == R.id.yearSpinner){

            yearQuery = parent.getItemAtPosition(position).toString();
            Toast.makeText(getActivity(),yearQuery,Toast.LENGTH_SHORT).show();

            if (!monthArrayList.isEmpty()) {
                ArrayAdapter<String> monthAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, monthArrayList);
                monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                monthSpinner.setAdapter(monthAdapter);
            }

        }else if(parent.getId() == R.id.monthSpinner){

            monthQuery = parent.getItemAtPosition(position).toString();
            Toast.makeText(getActivity(),monthQuery,Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}