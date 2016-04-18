package com.zfdang.multiple_images_selector;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.zfdang.multiple_images_selector.models.FolderItem;
import com.zfdang.multiple_images_selector.models.ImageItem;

public class ImagesSelectorActivity extends AppCompatActivity
        implements OnImageRecyclerViewInteractionListener, OnFolderRecyclerViewInteractionListener{

    private ImageRecyclerViewFragment imagesFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images_selector);

        imagesFragment = ImageRecyclerViewFragment.newInstance(3);
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.selector_content, imagesFragment).commit();
    }

    @Override
    public void onFolderItemInteraction(FolderItem item) {
        // call fragment to hide popupwindow, and refresh images list
        imagesFragment.OnFolderChange();
    }

    @Override
    public void onImageItemInteraction(ImageItem item) {

    }

}
