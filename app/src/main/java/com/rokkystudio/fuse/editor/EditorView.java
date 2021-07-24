package com.rokkystudio.fuse.editor;

import android.content.Context;
import android.os.Build;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.rokkystudio.fuse.R;
import com.rokkystudio.fuse.Tools;

public class EditorView extends FrameLayout implements
    View.OnClickListener, View.OnLongClickListener
{
    private OnRemoveClickListener mOnRemoveClickListener = null;
    private Toast mRemoveToast = null;

    public EditorView(@NonNull Context context) {
        super(context);
        init();
    }

    public EditorView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EditorView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public EditorView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.editor_view, this);
        View remove = findViewById(R.id.EditorViewRemove);
        remove.setOnClickListener(this);
        remove.setOnLongClickListener(this);
        mRemoveToast = Toast.makeText(getContext(), "Long click to remove", Toast.LENGTH_SHORT);
    }

    public ViewGroup getContainer() {
        return findViewById(R.id.EditorViewContainer);
    }

    public void setHeaderTitle(String title) {
        TextView text = findViewById(R.id.EditorViewTitle);
        if (text != null) text.setText(title);
    }

    public void setHeaderColor(int color) {
        ViewGroup header = findViewById(R.id.EditorViewHeader);
        if (header != null) header.setBackgroundColor(color);
    }

    public void setOnRemoveClickListener(OnRemoveClickListener listener) {
        mOnRemoveClickListener = listener;
    }

    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.EditorViewRemove) {
            Tools.vibrate(getContext(), 100);
            mRemoveToast.show();
        }
    }

    @Override
    public boolean onLongClick(View view)
    {
        if (view.getId() == R.id.EditorViewRemove)
        {
            if (mOnRemoveClickListener != null) {
                mOnRemoveClickListener.onRemoveClick(this);
            }
            return true;
        }
        return false;
    }

    public interface OnRemoveClickListener {
        void onRemoveClick(EditorView view);
    }
}
