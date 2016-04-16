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
}
