package com.zfdang.multiple_images_selector.models;

public class FolderItem {
    public String name;
    public String path;
    public String coverImagePath;
    public int numOfImages;

    public FolderItem(String name, String path, String coverImagePath) {
        this.name = name;
        this.path = path;
        this.coverImagePath = coverImagePath;
        this.numOfImages = 1;
    }

    public void incNumOfImages() {
        this.numOfImages += 1;
    }

    public String getNumOfImages() {
        return String.format("%d", this.numOfImages);
    }

    @Override
    public String toString() {
        return "FolderItem{" +
                "coverImagePath='" + coverImagePath + '\'' +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", numOfImages=" + numOfImages +
                '}';
    }
}
