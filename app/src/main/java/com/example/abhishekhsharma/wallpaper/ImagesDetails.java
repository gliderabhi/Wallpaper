package com.example.abhishekhsharma.wallpaper;

public class ImagesDetails {

    private String name,category,urlImage;

    public ImagesDetails(String name, String category, String urlImage) {
        this.name = name;
        this.category = category;
        this.urlImage = urlImage;
    }

    public ImagesDetails() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }
}
