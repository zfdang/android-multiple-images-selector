package com.zfdang.multiple_images_selector;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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


/**
 * A fragment representing a list of Items.
 */
public class ImageRecyclerViewFragment extends Fragment {
    private static final String TAG = "ImageFragment";

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 3;
    private OnImageRecyclerViewInteractionListener mListener;
    private OnFolderRecyclerViewInteractionListener mFolderListener;

    private TextView mCategoryText;
    private FolderPopupWindow mFolderPopupWindow;
    private RecyclerView recyclerView;

    private boolean isFolderListGenerated;
    private String folderPath;
    private ContentResolver contentResolver;

    public ImageRecyclerViewFragment() {
    }

    public static ImageRecyclerViewFragment newInstance(int columnCount) {
        ImageRecyclerViewFragment fragment = new ImageRecyclerViewFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_selector_content, container, false);
        View rview = view.findViewById(R.id.image_recycerview);

        // Set the adapter
        if (rview instanceof RecyclerView) {
            Context context = rview.getContext();
            recyclerView = (RecyclerView) rview;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new ImageRecyclerViewAdapter(ImageListContent.IMAGES, mListener));
        }


        mCategoryText = (TextView) view.findViewById(R.id.selector_image_folder_button);
        mCategoryText.setText(R.string.select_folder_all);
        mCategoryText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                if (mFolderPopupWindow == null) {
                    mFolderPopupWindow = new FolderPopupWindow();
                    mFolderPopupWindow.initPopupWindow(getActivity());
                }

                if (mFolderPopupWindow.isShowing()) {
                    mFolderPopupWindow.dismiss();
                } else {
                    mFolderPopupWindow.showAtLocation(view, Gravity.BOTTOM, 10, 150);
//                    int index = mFolderAdapter.getSelectIndex();
                    int index = 0;
                    index = index == 0 ? index : index - 1;
//                    mFolderPopupWindow.getListView().setSelection(index);
                }
            }
        });

        isFolderListGenerated = false;
        LoadFolderAndImages();
        return view;
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
        Observable.just(folderPath)
                .flatMap(new Func1<String, Observable<ImageItem>>() {
                    @Override
                    public Observable<ImageItem> call(String s) {
                        List<ImageItem> results = new ArrayList<>();

                        contentResolver = getActivity().getContentResolver();
                        Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

                        String where = "mime_type in (\"image/jpeg\", \"image/png\")";
                        String sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC";

                        Cursor cursor = contentResolver.query(contentUri, projections, where, null, sortOrder);
                        if (cursor == null) {
                            Log.d(TAG, "call: " + "Empty images");
                        } else if (cursor.moveToFirst()) {
                            int pathCol = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                            int nameCol = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);
                            int DateCol = cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED);
                            do {
                                String path = cursor.getString(pathCol);
                                String name = cursor.getString(nameCol);
                                long dateTime = cursor.getLong(DateCol);

                                ImageItem item = new ImageItem(path, name, dateTime);
                                results.add(item);

                                if(!isFolderListGenerated) {
                                    // find the path for this image, and add it to folderList if necessary
                                    String folderPath = new File(path).getParentFile().getAbsolutePath();
                                    FolderItem folderItem = FolderListContent.getItem(folderPath);
                                    if (folderItem == null) {
                                        // does not exist, create it
                                        folderItem = new FolderItem(StringUtils.getLastStringSegment(folderPath), folderPath, path);
                                        FolderListContent.addItem(folderItem);
                                    } else {
                                        // increase image numbers
                                        folderItem.incNumOfImages();
                                    }
                                } // if(!isFolderListGenerated) {
                            } while (cursor.moveToNext());
                        }

                        return Observable.from(results);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ImageItem>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted: ");

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


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnImageRecyclerViewInteractionListener) {
            mListener = (OnImageRecyclerViewInteractionListener) context;
        } else if(context instanceof  OnFolderRecyclerViewInteractionListener) {
            mFolderListener = (OnFolderRecyclerViewInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mFolderListener = null;
    }
}
