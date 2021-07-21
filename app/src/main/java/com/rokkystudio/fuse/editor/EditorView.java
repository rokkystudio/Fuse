package com.rokkystudio.fuse.editor;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class EditorView extends FrameLayout
{
    public EditorView(@NonNull Context context) {
        super(context);
    }

    public EditorView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EditorView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public EditorView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public static View inflate(Context context, @LayoutRes int resource, ViewGroup root) {

    }
}
