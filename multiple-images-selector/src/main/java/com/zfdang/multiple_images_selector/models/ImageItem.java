package com.zfdang.multiple_images_selector.models;

/**
 * Created by zfdang on 2016-4-12.
 */
public class ImageItem {
    public final String id;
    public final String content;
    public final String details;

    public ImageItem(String id, String content, String details) {
        this.id = id;
        this.content = content;
        this.details = details;
    }

    @Override
    public String toString() {
        return content;
    }
}