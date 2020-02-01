package com.test.mark;

public class HorizontalCategoryScrollModel {

    private int imageResource;
    private String categoryName;

    public HorizontalCategoryScrollModel(int imageResource, String categoryName) {
        this.imageResource = imageResource;
        this.categoryName = categoryName;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
