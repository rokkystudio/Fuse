package com.rokkystudio.fuse.menu;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.rokkystudio.fuse.R;

public class NodeView extends LinearLayout implements View.OnClickListener, View.OnTouchListener
{
    private OnHeaderClickListener mOnHeaderClickListener = null;
    private int mOriginHeight = 0;
    private NodeItem mNode = new NodeItem();

    private final ViewGroup mHeaderLayout;
    private final ViewGroup mWrapperLayout;

    public NodeView(Context context) {
        super(context);
        inflate(getContext(), R.layout.menu_item, this);
        mHeaderLayout = findViewById(R.id.HeaderLayout);
        mWrapperLayout = findViewById(R.id.WrapperLayout);
        if (mHeaderLayout == null) throw new Excep
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
        if (mNode == null) return;
        if (mNode.isExpanded()) return;
        mNode.setExpanded(true);
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

        if (!mNode.isExpanded()) return;
        mNode.setExpanded(false);

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
        mNode.setExpanded(expanded);

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

    public void setNode(@NonNull NodeItem node) {
        mNode = node;
    }

    public NodeItem getNode() {
        return mNode;
    }

    @Override
    public void removeAllViews() {
        // Удаляем представления только внутри обвертки
        ViewGroup wrapper = findViewById(R.id.WrapperLayout);
        if (wrapper != null) wrapper.removeAllViews();
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

    public interface OnHeaderClickListener {
        void onHeaderClick(NodeView layout);
    }
}
