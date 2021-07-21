package com.rokkystudio.fuse.editor;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.rokkystudio.fuse.R;

public class EditorGroup extends FrameLayout
{
    public EditorGroup(@NonNull Context context) {
        super(context);
        init();
    }

    public EditorGroup(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EditorGroup(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public EditorGroup(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.editor_group, this);
    }

    public void setTitle(String title) {
        EditText editText = findViewById(R.id.EditorGroupTitle);
        if (editText != null) {
            editText.setText(title);
        }
    }

    public ViewGroup getContainer() {
        return findViewById(R.id.EditorGroupContainer);
    }
}
