package com.rokkystudio.fuse.fuse;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.rokkystudio.fuse.R;

public class FuseView extends FrameLayout
{
    public FuseView(@NonNull Context context) {
        super(context);
        init();
    }

    public FuseView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FuseView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public FuseView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.fuse_view, this);
    }

    public void setFuseID(String id) {
        TextView text = findViewById(R.id.FuseID);
        if (text != null) text.setText(id);
    }

    public void setFuseIcon(int resource) {
        ImageView image = findViewById(R.id.FuseIcon);
        if (image != null) image.setImageResource(resource);
    }

    public void setFuseName(String name) {
        TextView text = findViewById(R.id.FuseName);
        if (text != null) text.setText(name);
    }
}
