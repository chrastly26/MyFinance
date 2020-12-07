package com.chrastly.myfinance;

public class CategoryCardView {
    private String categoryText;
    private int categoryIcon;

    public CategoryCardView(String categoryText, int categoryIcon){
        this.categoryText = categoryText;
        this.categoryIcon = categoryIcon;
    }

    public String getCategoryText() {
        return categoryText;
    }

    public void setCategoryText(String categoryText) {
        this.categoryText = categoryText;
    }

    public int getCategoryIcon() {
        return categoryIcon;
    }

    public void setCategoryIcon(int categoryIcon) {
        this.categoryIcon = categoryIcon;
    }
}
