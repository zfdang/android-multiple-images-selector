package com.zfdang.multiple_images_selector;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zfdang.multiple_images_selector.models.FolderItem;
import com.zfdang.multiple_images_selector.models.FolderListContent;
import com.zfdang.multiple_images_selector.models.ImageItem;
import com.zfdang.multiple_images_selector.models.ImageListContent;
import com.zfdang.multiple_images_selector.utilities.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller;

public class ImagesSelectorActivity extends AppCompatActivity
        implements OnImageRecyclerViewInteractionListener, OnFolderRecyclerViewInteractionListener, View.OnClickListener{

    private static final String TAG = "ImageSelector";
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 3;

    // custom action bars
    private ImageView mButtonBack;
    private Button mButtonConfirm;

    private RecyclerView recyclerView;

    // folder selecting related
    private View mPopupAnchorView;
    private TextView mFolderSelectButton;
    private FolderPopupWindow mFolderPopupWindow;

    // flag to indicate whether we have generated folder list
    private boolean isFolderListGenerated;
    private String currentFolderPath;
    private ContentResolver contentResolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images_selector);

        // get parameters from bundle
        Intent intent = getIntent();
        SelectorSettings.mMaxImageNumber = intent.getIntExtra(SelectorSettings.SELECTOR_MAX_IMAGE_NUMBER, SelectorSettings.mMaxImageNumber);
        SelectorSettings.isShowCamera = intent.getBooleanExtra(SelectorSettings.SELECTOR_SHOW_CAMERA, SelectorSettings.isShowCamera);
        SelectorSettings.mMinImageSize = intent.getIntExtra(SelectorSettings.SELECTOR_MIN_IMAGE_SIZE, SelectorSettings.mMinImageSize);

        ArrayList<String> selected = intent.getStringArrayListExtra(SelectorSettings.SELECTOR_INITIAL_SELECTED_LIST);
        ImageListContent.SELECTED_IMAGES.clear();
        if(selected != null && selected.size() > 0) {
            ImageListContent.SELECTED_IMAGES.addAll(selected);
        }

        // initialize widgets in custom actionbar
        mButtonBack = (ImageView) findViewById(R.id.selector_button_back);
        mButtonBack.setOnClickListener(this);

        mButtonConfirm = (Button) findViewById(R.id.selector_button_confirm);
        mButtonConfirm.setOnClickListener(this);

        // initialize recyclerview
        View rview = findViewById(R.id.image_recycerview);
        // Set the adapter
        if (rview instanceof RecyclerView) {
            Context context = rview.getContext();
            recyclerView = (RecyclerView) rview;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new ImageRecyclerViewAdapter(ImageListContent.IMAGES, this));

            VerticalRecyclerViewFastScroller fastScroller = (VerticalRecyclerViewFastScroller) findViewById(R.id.recyclerview_fast_scroller);
            // Connect the recycler to the scroller (to let the scroller scroll the list)
            fastScroller.setRecyclerView(recyclerView);
            // Connect the scroller to the recycler (to let the recycler scroll the scroller's handle)
            recyclerView.setOnScrollListener(fastScroller.getOnScrollListener());
        }

        // popup windows will be anchored to this view
        mPopupAnchorView = findViewById(R.id.selector_footer);

        // initialize buttons in footer
        mFolderSelectButton = (TextView) findViewById(R.id.selector_image_folder_button);
        mFolderSelectButton.setText(R.string.selector_folder_all);
        mFolderSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                if (mFolderPopupWindow == null) {
                    mFolderPopupWindow = new FolderPopupWindow();
                    mFolderPopupWindow.initPopupWindow(ImagesSelectorActivity.this);
                }

                if (mFolderPopupWindow.isShowing()) {
                    mFolderPopupWindow.dismiss();
                } else {
                    mFolderPopupWindow.showAtLocation(mPopupAnchorView, Gravity.BOTTOM, 10, 150);
//                    int index = mFolderAdapter.getSelectIndex();
                    int index = 0;
                    index = index == 0 ? index : index - 1;
//                    mFolderPopupWindow.getListView().setSelection(index);
                }
            }
        });

        isFolderListGenerated = false;
        currentFolderPath = "";
        FolderListContent.clear();
        ImageListContent.clear();

        updateDoneButton();

        LoadFolderAndImages();
    }

    public void OnFolderChange() {
        mFolderPopupWindow.dismiss();

        FolderItem folder = FolderListContent.getSelectedFolder();
        String newFolderPath = folder.path;
        if( !newFolderPath.equals(this.currentFolderPath)) {
            this.currentFolderPath = newFolderPath;
            mFolderSelectButton.setText(folder.name);
            ImageListContent.clear();
            recyclerView.getAdapter().notifyDataSetChanged();
            LoadFolderAndImages();
        } else {
            Log.d(TAG, "OnFolderChange: " + "Same folder selected, skip loading.");
        }
    }

    private final String[] projections = {
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media._ID};

    // this method is to load images and folders for all
    public void LoadFolderAndImages() {
        Log.d(TAG, "LoadFolderAndImages: " + "loading images for folder " + this.currentFolderPath);
        Observable.just(this.currentFolderPath)
                .flatMap(new Func1<String, Observable<ImageItem>>() {
                    @Override
                    public Observable<ImageItem> call(String folder) {
                        List<ImageItem> results = new ArrayList<>();

                        Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        String where = MediaStore.Images.Media.SIZE + " > " + SelectorSettings.mMinImageSize;
                        if(currentFolderPath != null && currentFolderPath.length() > 0) {
                            where += " and " + MediaStore.Images.Media.DATA + " like '" + currentFolderPath + "/%'";
                            Log.d(TAG, "call: " + where);
                        }
                        String sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC";

                        contentResolver = getContentResolver();
                        Cursor cursor = contentResolver.query(contentUri, projections, where, null, sortOrder);
                        if (cursor == null) {
                            Log.d(TAG, "call: " + "Empty images");
                        } else if (cursor.moveToFirst()) {
                            FolderItem allFolderItem = null;
                            int pathCol = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                            int nameCol = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);
                            int DateCol = cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED);
                            do {
                                String path = cursor.getString(pathCol);
                                String name = cursor.getString(nameCol);
                                long dateTime = cursor.getLong(DateCol);

                                ImageItem item = new ImageItem(name, path, dateTime);
                                results.add(item);

                                if(!isFolderListGenerated) {
                                    // if FolderListContent is still empty, add "All Images" option
                                    if(FolderListContent.FOLDERS.size() == 0) {
                                        // add folder for all image
                                        FolderListContent.selectedFolderIndex = 0;
                                        allFolderItem = new FolderItem(getString(R.string.selector_folder_all), "", path);
                                        FolderListContent.addItem(allFolderItem);
                                    } else if (allFolderItem != null) {
                                        // "All Image" selection exists, increase its counter
                                        allFolderItem.incNumOfImages();
                                    }

                                    // find the path for this image, and add path to folderList if not existed
                                    String folderPath = new File(path).getParentFile().getAbsolutePath();
                                    FolderItem folderItem = FolderListContent.getItem(folderPath);
                                    if (folderItem == null) {
                                        // does not exist, create it
                                        folderItem = new FolderItem(StringUtils.getLastPathSegment(folderPath), folderPath, path);
                                        FolderListContent.addItem(folderItem);
                                    } else {
                                        // increase image numbers
                                        folderItem.incNumOfImages();
                                    }
                                } // if(!isFolderListGenerated) {
                            } while (cursor.moveToNext());
                            cursor.close();
                        } // } else if (cursor.moveToFirst()) {
                        return Observable.from(results);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ImageItem>() {
                    @Override
                    public void onCompleted() {
                        // folder list has been generated, don't generate it again
                        isFolderListGenerated = true;
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: " + Log.getStackTraceString(e));
                    }

                    @Override
                    public void onNext(ImageItem imageItem) {
                        Log.d(TAG, "onNext: " + imageItem.toString());
                        ImageListContent.addItem(imageItem);
                        recyclerView.getAdapter().notifyDataSetChanged();
                    }
                });
    }


    public void updateDoneButton() {
        if(ImageListContent.SELECTED_IMAGES.size() == 0) {
            mButtonConfirm.setEnabled(false);
        } else {
            mButtonConfirm.setEnabled(true);
        }

        String caption = getResources().getString(R.string.selector_action_done, ImageListContent.SELECTED_IMAGES.size(), SelectorSettings.mMaxImageNumber);
        mButtonConfirm.setText(caption);
    }

    @Override
    public void onFolderItemInteraction(FolderItem item) {
        // call fragment to hide popupwindow, and refresh images list
        OnFolderChange();
    }

    @Override
    public void onImageItemInteraction(ImageItem item) {
        if(ImageListContent.bReachMaxNumber) {
            String hint = String.format(getResources().getString(R.string.selector_reach_max_image_hint), SelectorSettings.mMaxImageNumber);
            Toast.makeText(ImagesSelectorActivity.this, hint, Toast.LENGTH_SHORT).show();
            ImageListContent.bReachMaxNumber = false;
        }
        updateDoneButton();
    }

    @Override
    public void onClick(View v) {
        if( v == mButtonBack) {
            setResult(Activity.RESULT_CANCELED);
            finish();
        } else if(v == mButtonConfirm) {
            Intent data = new Intent();
            data.putStringArrayListExtra(SelectorSettings.SELECTOR_RESULTS, ImageListContent.SELECTED_IMAGES);
            setResult(Activity.RESULT_OK, data);
            finish();
        }
    }
}
