package com.zfdang.multiple_images_selector.models;

import java.util.ArrayList;
import java.util.List;

public class ImageListContent {

    public static final List<ImageItem> IMAGES = new ArrayList<ImageItem>();

    public static void clear()
    {
        IMAGES.clear();
    }
    public static void addItem(ImageItem item) {
        IMAGES.add(item);
    }

    public static final ArrayList<String> SELECTED_IMAGES = new ArrayList<>();

    public static boolean isImageSelected(String filename) {
        return SELECTED_IMAGES.contains(filename);
    }

    public static void toggleImageSelected(String filename) {
        if(SELECTED_IMAGES.contains(filename)) {
            SELECTED_IMAGES.remove(filename);
        } else {
            SELECTED_IMAGES.add(filename);
        }
    }
}
