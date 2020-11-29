package com.chrastly.myfinance;

import java.util.ArrayList;
import java.util.HashMap;

public class ExpenseGeneralData {

    private HashMap<String,ArrayList> yearMonthHashMap = new HashMap<>();

    private ArrayList<String> categoryArrayList = new ArrayList<String>();

    public HashMap<String, ArrayList> getYearMonthHashMap() {
        return yearMonthHashMap;
    }

    public void setYearMonthHashMap(HashMap<String, ArrayList> yearMonthHashMap) {
        this.yearMonthHashMap = yearMonthHashMap;
    }

    public ArrayList<String> getCategoryArrayList() {
        return categoryArrayList;
    }

    public void setCategoryArrayList(ArrayList<String> categoryArrayList) {
        this.categoryArrayList = categoryArrayList;
    }
}
