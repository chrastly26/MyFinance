package com.chrastly.myfinance;

public class Expense {

    private String expenseDate;
    private String expenseTitle;
    private String expenseAmount;
    private String expenseCategory;
    private String expenseDetail;

    public void setExpenseDate(String expenseDate) {
        this.expenseDate = expenseDate;
    }

    public void setExpenseTitle(String expenseTitle) {
        this.expenseTitle = expenseTitle;
    }

    public void setExpenseAmount(String expenseAmount) {
        this.expenseAmount = expenseAmount;
    }

    public void setExpenseCategory(String expenseCategory) {
        this.expenseCategory = expenseCategory;
    }

    public void setExpenseDetail(String expenseDetail) {
        this.expenseDetail = expenseDetail;
    }

    public String getExpenseDate() {
        return expenseDate;
    }

    public String getExpenseTitle() {
        return expenseTitle;
    }

    public String getExpenseAmount() {
        return expenseAmount;
    }

    public String getExpenseCategory() {
        return expenseCategory;
    }

    public String getExpenseDetail() {
        return expenseDetail;
    }


}
