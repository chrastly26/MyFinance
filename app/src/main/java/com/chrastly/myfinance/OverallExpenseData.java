package com.chrastly.myfinance;

import java.util.HashMap;

public class OverallExpenseData {

    private String year;
    private String month;
    private HashMap<String,String> expenseHashMap;

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public HashMap<String, String> getExpenseHashMap() {
        return expenseHashMap;
    }

    public void setExpenseHashMap(HashMap<String, String> expenseHashMap) {
        this.expenseHashMap = expenseHashMap;
    }
}