package com.zfdang.multiple_images_selector;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.zfdang.multiple_images_selector.models.ImageItem;

public class ImagesSelectActivity extends AppCompatActivity implements OnImageGridInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images_select);

        ImageGridFragment gridFragment = ImageGridFragment.newInstance(3);
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.image_grid, gridFragment).commit();
    }

    @Override
    public void onImageItemInteraction(ImageItem item) {

    }
}
