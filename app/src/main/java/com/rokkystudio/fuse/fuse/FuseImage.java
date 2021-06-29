package com.rokkystudio.fuse.fuse;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.rokkystudio.fuse.R;

import java.io.IOException;
import java.io.InputStream;

public class FuseImage extends FrameLayout implements View.OnClickListener
{
    private OnImageClickListener mOnImageClickListener = null;
    private String mAsset = null;

    public FuseImage(@NonNull Context context) {
        super(context);
        init();
    }

    public FuseImage(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FuseImage(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public FuseImage(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void init() {
        inflate(getContext(), R.layout.fuse_image, this);
        View view = findViewById(R.id.FuseImageText);
        if (view != null) view.setOnClickListener(this);
    }

    public void setAsset(String asset)
    {
        Context context = getContext();
        if (context == null) return;
        mAsset = asset;

        try {
            InputStream inputStream = context.getAssets().open(asset);
            Drawable drawable = BitmapDrawable.createFromStream(inputStream, null);
            ImageView image = findViewById(R.id.FuseImageView);
            if (image != null) image.setImageDrawable(drawable);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setOnImageClickListener(@Nullable OnImageClickListener listener) {
        mOnImageClickListener = listener;
    }

    @Override
    public void onClick(View view) {
        if (mOnImageClickListener != null && mAsset != null) {
            mOnImageClickListener.onImageClick(mAsset);
        }
    }

    public interface OnImageClickListener {
        void onImageClick(String asset);
    }
}
