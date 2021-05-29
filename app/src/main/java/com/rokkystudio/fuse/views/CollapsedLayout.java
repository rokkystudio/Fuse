package com.rokkystudio.fuse.views;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.rokkystudio.fuse.R;

/**
 *  Внутри CollapsedLayout должены находится 2 группы с ID "HeaderLayout" и ID "WrapperLayout".
 *  При нажатии на заголовок происходит сворачивание и разворачивание содержимого обвертки.
 */

public class CollapsedLayout extends LinearLayout implements View.OnClickListener, View.OnTouchListener
{
    private OnClickListener mOnClickListener = null;
    private int mOriginHeight = 0;
    private boolean mExpanded = true;

    public CollapsedLayout(Context context) {
        super(context);
    }

    public CollapsedLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CollapsedLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CollapsedLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ViewGroup header = findViewById(R.id.HeaderLayout);
        if (header != null) {
            header.setOnClickListener(this);
            header.setOnTouchListener(this);
        }
    }

    public void expand()
    {
        ViewGroup wrapper = findViewById(R.id.WrapperLayout);
        if (wrapper == null) return;

        if (isExpanded()) return;
        mExpanded = true;
        wrapper.setVisibility(VISIBLE);

        Animation animation = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation transformation) {
                wrapper.getLayoutParams().height = (int) (mOriginHeight * interpolatedTime);
                requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        animation.setDuration(500);
        startAnimation(animation);
    }

    public void collapse()
    {
        ViewGroup wrapper = findViewById(R.id.WrapperLayout);
        if (wrapper == null) return;

        if (!isExpanded()) return;
        mExpanded = false;

        if (mOriginHeight == 0) {
            mOriginHeight = wrapper.getHeight();
        }

        Animation animation = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation transformation) {
                if (interpolatedTime == 1) {
                    wrapper.setVisibility(GONE);
                    return;
                }

                wrapper.getLayoutParams().height = (int) (mOriginHeight * (1 - interpolatedTime));
                wrapper.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        animation.setDuration(500);
        startAnimation(animation);
    }

    public void setExpanded(boolean expanded)
    {
        mExpanded = expanded;
        ViewGroup wrapper = findViewById(R.id.WrapperLayout);
        if (wrapper == null) return;

        if (expanded) {
            wrapper.setVisibility(VISIBLE);
            if (mOriginHeight != 0) {
                wrapper.getLayoutParams().height = mOriginHeight;
            }
        } else {
            wrapper.setVisibility(GONE);
            if (mOriginHeight == 0) {
                mOriginHeight = wrapper.getHeight();
            }
        }
    }

    public boolean isExpanded() {
        return mExpanded;
    }

    @Override
    public void onClick(View view)
    {
        if (mOnClickListener != null) {
            mOnClickListener.onClick(this);
        }

        if (isExpanded()) {
            collapse();
        } else {
            expand();
        }
    }

    @Override
    public void setOnClickListener(OnClickListener listener) {
        mOnClickListener = listener;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event)
    {
        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                view.setBackgroundColor(0x770077AA);
                break;
            case MotionEvent.ACTION_UP:
                view.setBackground(null);
            case MotionEvent.ACTION_CANCEL:
                view.setBackground(null);
                break;
        }
        return false;
    }

    public interface HeaderClickListener {
        void onHeaderClick(CollapsedLayout layout);
    }
}
