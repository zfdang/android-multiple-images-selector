package com.zfdang.multiple_images_selector;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.zfdang.multiple_images_selector.models.FolderItem;
import com.zfdang.multiple_images_selector.models.ImageItem;
import com.zfdang.multiple_images_selector.models.SelectorSettings;

public class ImagesSelectorActivity extends AppCompatActivity
        implements OnImageRecyclerViewInteractionListener, OnFolderRecyclerViewInteractionListener{

    private ImageRecyclerViewFragment imagesFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images_selector);

        // get parameters from bundle
        Intent intent = getIntent();
        SelectorSettings.mMaxImageNumber = intent.getIntExtra(SelectorSettings.SELECTOR_MAX_IMAGE_NUMBER, SelectorSettings.mMaxImageNumber);
        SelectorSettings.isShowCamera = intent.getBooleanExtra(SelectorSettings.SELECTOR_SHOW_CAMERA, SelectorSettings.isShowCamera);

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
