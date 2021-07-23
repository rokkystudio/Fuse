package com.rokkystudio.fuse.editor;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.rokkystudio.fuse.R;

public class EditorFuse extends EditorView
{
    public EditorFuse(@NonNull Context context) {
        super(context);
        init();
    }

    public EditorFuse(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EditorFuse(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public EditorFuse(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.editor_fuse, super.getContainer());
        setEditorTitle(getResources().getString(R.string.EditorTitleFuse));
        // TODO setEditorColor(); разные цвета заголовков для групп и картинок и предохранителей
    }
}
