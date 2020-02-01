package com.test.mark;

public class HorizontalScrollTrendingModel {

    private int trendingImage;
    private String name, description, distance;

    public HorizontalScrollTrendingModel(int trendingImage, String name, String description, String distance) {
        this.trendingImage = trendingImage;
        this.name = name;
        this.description = description;
        this.distance = distance;
    }

    public int getTrendingImage() {
        return trendingImage;
    }

    public void setTrendingImage(int trendingImage) {
        this.trendingImage = trendingImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}
