package com.rokkystudio.fuse.editor;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.rokkystudio.fuse.R;

import java.io.IOException;
import java.io.InputStream;

public class EditorImage extends FrameLayout
{
    private com.rokkystudio.fuse.fuse.FuseImage.OnImageClickListener mOnImageClickListener = null;
    private String mAsset = null;

    public EditorImage(@NonNull Context context) {
        super(context);
        init();
    }

    public EditorImage(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EditorImage(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public EditorImage(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void init() {
        inflate(getContext(), R.layout.editor_image, this);
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
}

