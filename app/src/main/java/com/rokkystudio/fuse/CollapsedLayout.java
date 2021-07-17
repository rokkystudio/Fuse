package com.rokkystudio.fuse;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class CollapsedLayout extends FrameLayout implements
    View.OnClickListener, View.OnTouchListener
{
    private OnHeaderTouchListener mOnHeaderTouchListener = null;
    private OnHeaderClickListener mOnHeaderClickListener = null;
    private OnWrapperExpandedListener mOnWrapperExpandedListener = null;
    private OnWrapperCollapsedListener mOnWrapperCollapsedListener = null;

    private int mDuration = 500; // ms
    private boolean mExpanded = false;

    public CollapsedLayout(@NonNull Context context) {
        super(context);
        init();
    }

    public CollapsedLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CollapsedLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CollapsedLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.collapsed_layout, this);
        ViewGroup header = getHeaderLayout();
        if (header != null) {
            header.setOnClickListener(this);
            header.setOnTouchListener(this);
        }
    }

    public void setTitle(String title) {
        TextView textView = findViewById(R.id.HeaderTitle);
        if (textView != null) {
            textView.setText(title);
        }
    }

    public void setDuration(int duration) {
        mDuration = duration;
    }

    public void setExpanded(boolean expanded) {
        if (mExpanded == expanded) return;
        mExpanded = expanded;

        ViewGroup wrapper = getWrapperLayout();
        if (wrapper == null) return;

        if (expanded) {
            wrapper.setVisibility(VISIBLE);
            wrapper.getLayoutParams().height = WRAP_CONTENT;
            setIcon(R.drawable.arrow_up);
        } else {
            wrapper.setVisibility(GONE);
            wrapper.getLayoutParams().height = 0;
            setIcon(R.drawable.arrow_right);
        }
    }

    public void expand()
    {
        if (mExpanded) return;
        mExpanded = true;

        ViewGroup wrapper = getWrapperLayout();
        if (wrapper == null) return;
        wrapper.setVisibility(VISIBLE);
        setIcon(R.drawable.arrow_up);

        // Измерение высоты, занимаемой дочерними элементами
        final int wrapperHeight = getWrapperHeight();
        wrapper.getLayoutParams().height = 0;

        Animation animation = new Animation()
        {
            @Override
            protected void applyTransformation(float time, Transformation t) {
                wrapper.getLayoutParams().height = (int) (wrapperHeight * time);
                if (time == 1) {
                    wrapper.getLayoutParams().height = WRAP_CONTENT;
                    if (mOnWrapperExpandedListener != null) {
                        mOnWrapperExpandedListener.onWrapperExpanded(CollapsedLayout.this);
                    }
                }
                wrapper.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        animation.setDuration(mDuration);
        startAnimation(animation);
    }

    public void collapse()
    {
        if (!mExpanded) return;
        mExpanded = false;

        ViewGroup wrapper = getWrapperLayout();
        if (wrapper == null) return;
        setIcon(R.drawable.arrow_right);

        // Измерение высоты, занимаемой дочерними элементами
        final int wrapperHeight = getWrapperHeight();

        Animation animation = new Animation()
        {
            @Override
            protected void applyTransformation(float time, Transformation trans) {
                if (time == 1) {
                    wrapper.getLayoutParams().height = 0;
                    wrapper.setVisibility(GONE);
                    if (mOnWrapperCollapsedListener != null) {
                        mOnWrapperCollapsedListener.onWrapperCollapsed(CollapsedLayout.this);
                    }
                    return;
                }

                wrapper.getLayoutParams().height = (int) (wrapperHeight * (1 - time));
                wrapper.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        animation.setDuration(mDuration);
        startAnimation(animation);
    }

    public boolean isExpanded() {
        return mExpanded;
    }

    public void addWrapperView(ViewGroup view) {
        ViewGroup wrapper = getWrapperLayout();
        if (wrapper == null) return;
        wrapper.addView(view);
    }

    public void removeWrapperViews() {
        ViewGroup wrapper = getWrapperLayout();
        if (wrapper == null) return;
        wrapper.removeAllViews();
        wrapper.setVisibility(GONE);
    }

    public ViewGroup getHeaderLayout() {
        return findViewById(R.id.HeaderLayout);
    }

    public ViewGroup getWrapperLayout() {
        return findViewById(R.id.WrapperLayout);
    }

    private int getWrapperHeight() {
        ViewGroup wrapper = getWrapperLayout();
        if (wrapper == null) return 0;
        wrapper.measure(
            MeasureSpec.makeMeasureSpec(getWidth(), View.MeasureSpec.AT_MOST),
            MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        );
        return wrapper.getMeasuredHeight();
    }

    public void setHeaderColor(int color) {
        ViewGroup header = getHeaderLayout();
        if (header != null) {
            header.setBackgroundColor(color);
        }
    }

    public void setIcon(int resId) {
        ImageView itemIcon = findViewById(R.id.HeaderIcon);
        if (itemIcon != null) itemIcon.setImageResource(resId);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if (mOnHeaderTouchListener != null) {
            return mOnHeaderTouchListener.onHeaderTouch(this, event);
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        if (mOnHeaderClickListener != null) {
            mOnHeaderClickListener.onHeaderClick(this);
        }
    }

    public void setOnHeaderTouchListener(OnHeaderTouchListener listener) {
        mOnHeaderTouchListener = listener;
    }

    public void setOnHeaderClickListener(OnHeaderClickListener listener) {
        mOnHeaderClickListener = listener;
    }

    public void setOnWrapperExpandedListener(OnWrapperExpandedListener listener) {
        mOnWrapperExpandedListener = listener;
    }

    public void setOnWrapperCollapsedListener(OnWrapperCollapsedListener listener) {
        mOnWrapperCollapsedListener = listener;
    }

    public interface OnHeaderTouchListener {
        boolean onHeaderTouch(CollapsedLayout layout, MotionEvent event);
    }

    public interface OnHeaderClickListener {
        void onHeaderClick(CollapsedLayout layout);
    }

    public interface OnWrapperExpandedListener {
        void onWrapperExpanded(CollapsedLayout layout);
    }

    public interface OnWrapperCollapsedListener {
        void onWrapperCollapsed(CollapsedLayout layout);
    }
}
