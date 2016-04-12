package com.zfdang.multiple_images_selector.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageListContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<ImageItem> ITEMS = new ArrayList<ImageItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, ImageItem> ITEM_MAP = new HashMap<String, ImageItem>();

    private static final int COUNT = 25;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createDummyItem(i));
        }
    }

    private static void addItem(ImageItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static ImageItem createDummyItem(int position) {
        return new ImageItem(String.valueOf(position), "Item " + position, makeDetails(position));
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }


}
