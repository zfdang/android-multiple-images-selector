package com.zfdang.multiple_images_selector.models;

import java.util.ArrayList;
import java.util.List;

public class FolderListContent {

    public static final List<FolderItem> FOLDERS = new ArrayList<FolderItem>();

    public static void clear() {
        FOLDERS.clear();
    }

    public static void addItem(FolderItem item) {
        FOLDERS.add(item);
    }
}
