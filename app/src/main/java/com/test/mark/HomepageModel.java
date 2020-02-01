package com.test.mark;

import java.util.List;

public class HomepageModel {

    public static final int CATEGORY_ITEMS = 0;
    public static final int HORIZONTAL_TRENDING_ITEMS = 1;

    private int type;

    //_______________________________CATEGORY PART_______________________________
    //___________________________________________________________________________
    private List<HorizontalCategoryScrollModel> horizontalCategoryScrollModelList;

    public HomepageModel(int type, List<HorizontalCategoryScrollModel> horizontalCategoryScrollModelList) {
        this.type = type;
        this.horizontalCategoryScrollModelList = horizontalCategoryScrollModelList;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<HorizontalCategoryScrollModel> getHorizontalCategoryScrollModelList() {
        return horizontalCategoryScrollModelList;
    }

    public void setHorizontalCategoryScrollModelList(List<HorizontalCategoryScrollModel> horizontalCategoryScrollModelList) {
        this.horizontalCategoryScrollModelList = horizontalCategoryScrollModelList;
    }

    //_______________________________TRENDING PART_______________________________
    //___________________________________________________________________________
    private List<HorizontalScrollTrendingModel> horizontalScrollTrendingModelList;

    public HomepageModel(int type, String title, List<HorizontalScrollTrendingModel> horizontalScrollTrendingModelList) {
        this.type = type;
        this.horizontalScrollTrendingModelList = horizontalScrollTrendingModelList;
    }

    public List<HorizontalScrollTrendingModel> getHorizontalScrollTrendingModelList() {
        return horizontalScrollTrendingModelList;
    }

    public void setHorizontalScrollTrendingModelList(List<HorizontalScrollTrendingModel> horizontalScrollTrendingModelList) {
        this.horizontalScrollTrendingModelList = horizontalScrollTrendingModelList;
    }
}
