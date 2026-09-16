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
    private OnContainerExpandedListener mOnContainerExpandedListener = null;
    private OnContainerCollapsedListener mOnContainerCollapsedListener = null;

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
        ViewGroup header = getHeader();
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

        ViewGroup container = getContainer();
        if (container == null) return;

        if (expanded) {
            container.setVisibility(VISIBLE);
            container.getLayoutParams().height = WRAP_CONTENT;
            setIcon(R.drawable.arrow_up);
        } else {
            container.setVisibility(GONE);
            container.getLayoutParams().height = 0;
            setIcon(R.drawable.arrow_right);
        }
    }

    public void expand()
    {
        if (mExpanded) return;
        mExpanded = true;

        ViewGroup container = getContainer();
        if (container == null) return;
        container.setVisibility(VISIBLE);
        setIcon(R.drawable.arrow_up);

        // Измерение высоты, занимаемой дочерними элементами
        final int containerHeight = getContainerHeight();
        container.getLayoutParams().height = 0;

        Animation animation = new Animation()
        {
            @Override
            protected void applyTransformation(float time, Transformation t) {
                container.getLayoutParams().height = (int) (containerHeight * time);
                if (time == 1) {
                    container.getLayoutParams().height = WRAP_CONTENT;
                    if (mOnContainerExpandedListener != null) {
                        mOnContainerExpandedListener.onContainerExpanded(CollapsedLayout.this);
                    }
                }
                container.requestLayout();
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

        ViewGroup container = getContainer();
        if (container == null) return;
        setIcon(R.drawable.arrow_right);

        // Измерение высоты, занимаемой дочерними элементами
        final int containerHeight = getContainerHeight();

        Animation animation = new Animation()
        {
            @Override
            protected void applyTransformation(float time, Transformation trans) {
                if (time == 1) {
                    container.getLayoutParams().height = 0;
                    container.setVisibility(GONE);
                    if (mOnContainerCollapsedListener != null) {
                        mOnContainerCollapsedListener.onContainerCollapsed(CollapsedLayout.this);
                    }
                    return;
                }

                container.getLayoutParams().height = (int) (containerHeight * (1 - time));
                container.requestLayout();
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

    public void addContainerView(ViewGroup view) {
        ViewGroup container = getContainer();
        if (container == null) return;
        container.addView(view);
    }

    public void clearContainer() {
        ViewGroup container = getContainer();
        if (container == null) return;
        container.removeAllViews();
        container.setVisibility(GONE);
    }

    public ViewGroup getHeader() {
        return findViewById(R.id.HeaderLayout);
    }

    public ViewGroup getContainer() {
        return findViewById(R.id.ContainerLayout);
    }

    private int getContainerHeight() {
        ViewGroup container = getContainer();
        if (container == null) return 0;
        container.measure(
            MeasureSpec.makeMeasureSpec(getWidth(), View.MeasureSpec.AT_MOST),
            MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        );
        return container.getMeasuredHeight();
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

    public void setOnHeaderClickListener(OnHeaderClickListener listener) {
        mOnHeaderClickListener = listener;
    }

    public interface OnHeaderTouchListener {
        boolean onHeaderTouch(CollapsedLayout layout, MotionEvent event);
    }

    public interface OnHeaderClickListener {
        void onHeaderClick(CollapsedLayout layout);
    }

    public interface OnContainerExpandedListener {
        void onContainerExpanded(CollapsedLayout layout);
    }

    public interface OnContainerCollapsedListener {
        void onContainerCollapsed(CollapsedLayout layout);
    }
}
