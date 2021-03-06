package com.chrastly.myfinance;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class AddExpenseActivity extends AppCompatActivity implements CategoryRecyclerViewAdapter.OnCategoryListener {

    private String selectedDate, expenseDate, expenseTitle, expenseAmount, expenseCategory, expenseDetail;
    private EditText expenseDateEditText, expenseTitleEditText, expenseAmountEditText, expenseDetailEditText,
            otherExpenseCategoryTextInput;
    private RecyclerView categoryRecyclerView;
    private ArrayList<CategoryCardView> categoryCardViewArrayList = new ArrayList<CategoryCardView>();
    private TextInputLayout otherExpenseCategoryTextInputLayout;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expenses);

        Intent intent = getIntent();
        selectedDate = intent.getStringExtra("com.chrastly.myfinance.SELECTEDDATE");

        expenseDateEditText = findViewById(R.id.expenseDateEditText);
        expenseDateEditText.setText(selectedDate);

        expenseDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();

                Integer calendarDate = calendar.get(Calendar.DAY_OF_MONTH);
                Integer calendarMonth = calendar.get(Calendar.MONTH);
                Integer calendarYear = calendar.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog = new DatePickerDialog(AddExpenseActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                selectedDate = year + "-" + dateMonthConversion(month + 1) + "-" + dateMonthConversion(dayOfMonth);
                                expenseDateEditText.setText(selectedDate);
                            }

                        }, calendarYear, calendarMonth, calendarDate);

                datePickerDialog.show();

            }
        });

        expenseAmountEditText = findViewById(R.id.expenseAmountEditText);
        expenseTitleEditText = findViewById(R.id.expenseTitleEditText);
        expenseDetailEditText = findViewById(R.id.expenseDetailEditText);

        otherExpenseCategoryTextInputLayout = findViewById(R.id.otherExpenseCategoryEditTextLayout);
        otherExpenseCategoryTextInput = findViewById(R.id.otherExpenseCategoryEditText);
        showCategoryRecyclerView();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_expenses_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.saveExpense:
                Boolean dataValidation = dataValidation();


                if (dataValidation == true){
                    Expense expense = createExpenseClass();
                    String year = expenseDate.substring(0,4);
                    String month = expenseDate.substring(5,7);
                    String category = expenseCategory;
                    saveExpense(expense);
                    saveGeneralData(year,month,category);
                }

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean dataValidation(){

        expenseDate = expenseDateEditText.getText().toString();
        expenseTitle = expenseTitleEditText.getText().toString();
        expenseDetail = expenseDetailEditText.getText().toString();
        expenseAmount = expenseAmountEditText.getText().toString();

        if(expenseCategory == null){
            expenseCategory = "NO CATEGORY";
        }

        if(expenseCategory.equals("OTHERS")){

            expenseCategory = otherExpenseCategoryTextInput.getText().toString();

        }

        if (expenseDate.trim().equals("") || expenseTitle.trim().equals("") || expenseAmount.trim().equals("")){

            if(expenseDate.trim().equals("")){
                expenseDateEditText.setError("Please Select Date Of Expense");
            }

            if(expenseTitle.trim().equals("")){
                expenseTitleEditText.setError("Please Record The Expense");
            }

            if(expenseAmount.trim().equals("")){
                expenseAmountEditText.setError("Please Record Amount Of Expense");
            }

            return false;

        }else{

            Double expenseAmountTemp = Double.parseDouble(expenseAmountEditText.getText().toString());
            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            expenseAmountTemp = Double.valueOf(decimalFormat.format(expenseAmountTemp));
            expenseAmount = String.format("%.2f", expenseAmountTemp);

            return true;

        }

    }

    public void saveExpense(Expense expense){

        final String FILE_NAME = "MyFinanceExpense" + selectedDate;
        File file = new File(this.getFilesDir(), FILE_NAME);

        if(file.exists()){

            String loadedString = loadExpense(selectedDate);
            if(loadedString != "") {
                Gson returnedGson = new Gson();
                ArrayList<Expense> existingExpenseArrayList = returnedGson.fromJson(loadedString,
                        new TypeToken<ArrayList<Expense>>() {}.getType());
                existingExpenseArrayList.add(expense);

                Gson gson = new Gson();
                String expenseJSON = gson.toJson(existingExpenseArrayList);

                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    fileOutputStream.write(expenseJSON.getBytes());
                    fileOutputStream.flush();
                    fileOutputStream.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        else {

            ArrayList<Expense> expenseArrayList = new ArrayList();
            expenseArrayList.add(expense);

            Gson gson = new Gson();
            String expenseJSON = gson.toJson(expenseArrayList);

            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(expenseJSON.getBytes());
                fileOutputStream.flush();
                fileOutputStream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

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
        } catch (FileNotFoundException e){
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringResponse;

    }


    public Expense createExpenseClass(){

        Expense expense = new Expense();
        expense.setExpenseDate(expenseDate);
        expense.setExpenseTitle(expenseTitle);
        expense.setExpenseAmount(expenseAmount);
        expense.setExpenseCategory(expenseCategory);
        expense.setExpenseDetail(expenseDetail);

        return expense;

    }


    public void saveGeneralData(String year, String month, String category){

        final String FILE_NAME = "MyFinanceGeneralData";
        File file = new File(this.getFilesDir(), FILE_NAME);

        if(file.exists()){

            String loadedString = loadExpenseGeneralData(FILE_NAME);
            if(loadedString != "") {
                Gson returnedGson = new Gson();
                ExpenseGeneralData existingExpenseGeneralData = returnedGson.fromJson(loadedString,
                        new TypeToken<ExpenseGeneralData>() {}.getType());

                ArrayList<String> monthArrayList = new ArrayList<>();

                if(!existingExpenseGeneralData.getYearMonthHashMap().keySet().contains(year)){
                    monthArrayList.add(month);
                    existingExpenseGeneralData.getYearMonthHashMap().put(year, monthArrayList);
                }

                if(existingExpenseGeneralData.getYearMonthHashMap().keySet().contains(year) &&
                        !existingExpenseGeneralData.getYearMonthHashMap().get(year).contains(month)){

                    monthArrayList = existingExpenseGeneralData.getYearMonthHashMap().get(year);
                    monthArrayList.add(month);
                    existingExpenseGeneralData.getYearMonthHashMap().put(year, monthArrayList);
                }

                if(!existingExpenseGeneralData.getCategoryArrayList().contains(category)){
                    existingExpenseGeneralData.getCategoryArrayList().add(category);
                }

                Gson gson = new Gson();
                String expenseJSON = gson.toJson(existingExpenseGeneralData);

                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    fileOutputStream.write(expenseJSON.getBytes());
                    fileOutputStream.flush();
                    fileOutputStream.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        else {
            ExpenseGeneralData expenseGeneralData = createGeneralData(year, month, category);

            Log.i("expenseGD",expenseGeneralData.toString());

            Gson gson = new Gson();
            String expenseJSON = gson.toJson(expenseGeneralData);

            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(expenseJSON.getBytes());
                fileOutputStream.flush();
                fileOutputStream.close();

            } catch (IOException e) {
                e.printStackTrace();
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

    public ExpenseGeneralData createGeneralData(String year, String month, String category){
        ArrayList<String> monthArrayList = new ArrayList<>();
        monthArrayList.add(month);

        HashMap<String,ArrayList> yearMonthHashMap = new HashMap<>();
        yearMonthHashMap.put(year,monthArrayList);

        ArrayList<String> categoryArrayList = new ArrayList<String>();
        categoryArrayList.add(category);

        ExpenseGeneralData expenseGeneralData = new ExpenseGeneralData();
        expenseGeneralData.setYearMonthHashMap(yearMonthHashMap);
        expenseGeneralData.setCategoryArrayList(categoryArrayList);

        return expenseGeneralData;

    }

    public String dateMonthConversion(Integer dateMonth){
        if(dateMonth<10){
            String dateMonthConverted = "0" + dateMonth;
            return dateMonthConverted;
        }else{
            return String.valueOf(dateMonth);
        }

    }

    public void showCategoryRecyclerView() {

        categoryCardViewArrayList.add(new CategoryCardView("FOOD/DINING", R.drawable.ic_meal));
        categoryCardViewArrayList.add(new CategoryCardView("GROCERIES", R.drawable.ic_grocery));
        categoryCardViewArrayList.add(new CategoryCardView("ENTERTAINMENT", R.drawable.ic_entertainment));
        categoryCardViewArrayList.add(new CategoryCardView("SHOPPING", R.drawable.ic_shopping));
        categoryCardViewArrayList.add(new CategoryCardView("SPORT/GYM", R.drawable.ic_sport));
        categoryCardViewArrayList.add(new CategoryCardView("TRANSPORT", R.drawable.ic_transport));
        categoryCardViewArrayList.add(new CategoryCardView("RENTAL", R.drawable.ic_rental));
        categoryCardViewArrayList.add(new CategoryCardView("BILLS", R.drawable.ic_utility));
        categoryCardViewArrayList.add(new CategoryCardView("GIFTS", R.drawable.ic_gift));
        categoryCardViewArrayList.add(new CategoryCardView("TRAVEL", R.drawable.ic_travel));
        categoryCardViewArrayList.add(new CategoryCardView("TAXES", R.drawable.ic_tax));
        categoryCardViewArrayList.add(new CategoryCardView("OTHERS", R.drawable.ic_nocategory));

        categoryRecyclerView = findViewById(R.id.expenseCategoryRecyclerView);
        CategoryRecyclerViewAdapter adapter = new CategoryRecyclerViewAdapter(categoryCardViewArrayList, this);
        adapter.setCategoryCardViewArrayList(categoryCardViewArrayList);

        categoryRecyclerView.setAdapter(adapter);
        categoryRecyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(),2));

    }


    @Override
    public void OnCategoryClick(int position) {
        expenseCategory = categoryCardViewArrayList.get(position).getCategoryText();

        if(expenseCategory.equals("OTHERS")){
            otherExpenseCategoryTextInputLayout.setVisibility(View.VISIBLE);
        }else{
            otherExpenseCategoryTextInputLayout.setVisibility(View.INVISIBLE);
        }

    }
}