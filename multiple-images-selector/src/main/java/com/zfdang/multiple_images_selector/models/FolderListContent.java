package com.zfdang.multiple_images_selector.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FolderListContent {

    public static final List<FolderItem> ITEMS = new ArrayList<FolderItem>();

    public static final Map<String, FolderItem> ITEM_MAP = new HashMap<String, FolderItem>();

    private static final int COUNT = 25;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createFolderItem(i));
        }
    }

    private static void addItem(FolderItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static FolderItem createFolderItem(int position) {
        return new FolderItem(String.valueOf(position), "Item " + position, makeDetails(position));
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
