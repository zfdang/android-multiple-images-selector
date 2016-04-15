package com.zfdang.multiple_images_selector;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.zfdang.multiple_images_selector.models.FolderItem;
import com.zfdang.multiple_images_selector.models.ImageItem;

public class ImagesSelectorActivity extends AppCompatActivity implements OnImageRecyclerViewInteractionListener, OnFolderRecyclerViewInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images_selector);

        ImageRecyclerViewFragment gridFragment = ImageRecyclerViewFragment.newInstance(3);
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.selector_content, gridFragment).commit();
    }

    @Override
    public void onImageItemInteraction(ImageItem item) {

    }

    @Override
    public void onFolderItemInteraction(FolderItem item) {

    }
}
