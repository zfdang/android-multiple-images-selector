package com.zfdang.multiple_images_selector;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.zfdang.multiple_images_selector.models.FolderItem;
import com.zfdang.multiple_images_selector.models.ImageItem;

import java.util.ArrayList;

public class ImagesSelectorActivity extends AppCompatActivity
        implements OnImageRecyclerViewInteractionListener, OnFolderRecyclerViewInteractionListener, View.OnClickListener{

    private ImageRecyclerViewFragment imagesFragment;
    private ImageView mButtonBack;
    private Button mButtonConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images_selector);

        // get parameters from bundle
        Intent intent = getIntent();
        SelectorSettings.mMaxImageNumber = intent.getIntExtra(SelectorSettings.SELECTOR_MAX_IMAGE_NUMBER, SelectorSettings.mMaxImageNumber);
        SelectorSettings.isShowCamera = intent.getBooleanExtra(SelectorSettings.SELECTOR_SHOW_CAMERA, SelectorSettings.isShowCamera);

        mButtonBack = (ImageView) findViewById(R.id.selector_button_back);
        mButtonBack.setOnClickListener(this);

        mButtonConfirm = (Button) findViewById(R.id.selector_button_confirm);
        mButtonConfirm.setOnClickListener(this);

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

    @Override
    public void onClick(View v) {
        if( v == mButtonBack) {
            setResult(Activity.RESULT_CANCELED);
            finish();
        } else if(v == mButtonConfirm) {
            Intent data = new Intent();
            data.putStringArrayListExtra(SelectorSettings.SELECTOR_RESULTS, new ArrayList<String>());
            setResult(Activity.RESULT_OK, data);
            finish();
        }
    }
}
