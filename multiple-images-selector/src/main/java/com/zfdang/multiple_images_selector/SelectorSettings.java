package com.zfdang.multiple_images_selector;

import java.util.ArrayList;

/**
 * Created by zfdang on 2016-4-18.
 */
public class SelectorSettings {
    /**
     * 最大图片选择次数，int类型
     */
    public static final String SELECTOR_MAX_IMAGE_NUMBER = "selector_max_image_number";
    public static int mMaxImageNumber = 9;

    /**
     * 是否显示相机，boolean类型
     */
    public static final String SELECTOR_SHOW_CAMERA = "selector_show_camera";
    public static boolean isShowCamera = true;

    /**
     * 默认选择的数据集
     */
    public static final String SELECTOR_INITIAL_SELECTED_LIST = "selector_initial_selected_list";
    public static ArrayList<String> resultList = new ArrayList<>();

    public static final String SELECTOR_RESULTS = "selector_results";
}
