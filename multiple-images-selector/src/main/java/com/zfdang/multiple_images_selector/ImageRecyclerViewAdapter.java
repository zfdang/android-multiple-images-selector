package com.zfdang.multiple_images_selector;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.zfdang.multiple_images_selector.models.ImageItem;
import com.zfdang.multiple_images_selector.utilities.FileUtils;

import java.io.File;
import java.util.List;

public class ImageRecyclerViewAdapter extends RecyclerView.Adapter<ImageRecyclerViewAdapter.ViewHolder> {

    private final List<ImageItem> mValues;
    private final OnImageRecyclerViewInteractionListener mListener;
    private static final String TAG = "ImageAdapter";

    public ImageRecyclerViewAdapter(List<ImageItem> items, OnImageRecyclerViewInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_image_item, parent, false);
        return new ViewHolder(view);
    }

    // http://www.jianshu.com/p/5364957dcf49
    public static void showThumb(Uri uri, SimpleDraweeView draweeView){

        Log.d(TAG, "showThumb: " + uri);
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(200, 200))
                .build();

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(draweeView.getController())
                .setControllerListener(new BaseControllerListener<ImageInfo>())
                .build();
        draweeView.setController(controller);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        ImageItem ii = mValues.get(position);
        holder.mItem = ii;

        Uri newURI;
        File imageFile = new File(ii.path);
        if (imageFile.exists()) {
            newURI = Uri.fromFile(imageFile);
        } else {
            newURI = FileUtils.getUriByResId(R.drawable.default_image);
        }

        showThumb(newURI, holder.mDrawee);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onImageItemInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final SimpleDraweeView mDrawee;
        public final ImageView mChecked;
        public ImageItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mDrawee = (SimpleDraweeView) view.findViewById(R.id.image_drawee);
            assert mDrawee != null;
            mChecked = (ImageView) view.findViewById(R.id.image_checked);
            assert mChecked != null;
        }



        @Override
        public String toString() {
            return super.toString();
        }
    }
}
