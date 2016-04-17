package com.zfdang.multiple_images_selector.utilities;

/**
 * Created by zfdang on 2016-4-17.
 */
public class StringUtils {
    // /nForum/article/RealEstate/5017593 ==> 5017593
    public static String getLastStringSegment(String content) {
        if(content == null || content.length() == 0){
            return "";
        }
        String[] segments = content.split("/");
        if(segments.length > 0) {
            return segments[segments.length - 1];
        }
        return "";
    }

}
