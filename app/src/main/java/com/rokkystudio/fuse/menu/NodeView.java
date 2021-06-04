package com.rokkystudio.fuse.menu;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.rokkystudio.fuse.R;

public class NodeView extends LinearLayout implements View.OnClickListener, View.OnTouchListener
{
    private OnHeaderClickListener mOnHeaderClickListener = null;
    private int mOriginHeight = 0;
    private NodeItem mNode = new NodeItem();

    public NodeView(Context context) {
        super(context);
        inflate(getContext(), R.layout.menu_item, this);
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
        ViewGroup wrapper = getWrapperLayout();
        if (wrapper == null) return;
        if (mNode.isExpanded()) return;
        mNode.setExpanded(true);
        wrapper.setVisibility(VISIBLE);

        wrapper.measure(
            MeasureSpec.makeMeasureSpec(getWidth(), View.MeasureSpec.AT_MOST),
            MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        );

        final int wrapperHeight = wrapper.getMeasuredHeight();
        /*
        Animation animation = new Animation()
        {
            @Override
            protected void applyTransformation(float time, Transformation trans) {
                //wrapper.getLayoutParams().height = 200; //(int) (wrapperHeight * time);
                if (time == 1) {
                    // wrapper.getLayoutParams().height = WRAP_CONTENT;
                }
                wrapper.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        animation.setDuration(5000);
        startAnimation(animation);
         */
    }

    public void collapse()
    {
        ViewGroup wrapper = getWrapperLayout();
        if (wrapper == null) return;
        if (!mNode.isExpanded()) return;
        mNode.setExpanded(false);

        if (mOriginHeight == 0) {
            mOriginHeight = wrapper.getHeight();
        }

        Animation animation = new Animation()
        {
            @Override
            protected void applyTransformation(float time, Transformation trans) {
                if (time == 1) {
                    wrapper.setVisibility(GONE);
                    detachChilds(mNode);
                    return;
                }

                wrapper.getLayoutParams().height = (int) (mOriginHeight * (1 - time));
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

    public void detachChilds()
    {
        NodeItem nodeItem = getNode();
        if (nodeItem == null) return;
        ViewGroup wrapper = getWrapperLayout();
        if (wrapper == null) return;
        wrapper.removeAllViews();

        for (NodeItem child : nodeItem.getChilds()) {
            child.setView(null);
            child.detachChilds();
        }
    }

    public void setExpanded(boolean expanded)
    {
        ViewGroup wrapper = getWrapperLayout();
        if (wrapper == null) return;
        mNode.setExpanded(expanded);

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
        ((TextView) findViewById(R.id.ItemName)).setText(node.getName());
        mNode = node;
    }

    public NodeItem getNode() {
        return mNode;
    }

    public void addChild(ViewGroup childView) {
        ViewGroup wrapper = findViewById(R.id.WrapperLayout);
        if (wrapper != null) wrapper.addView(childView);
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
            case MotionEvent.ACTION_CANCEL:
                view.setBackgroundColor(Color.TRANSPARENT);
                break;
        }
        return false;
    }

    public interface OnHeaderClickListener {
        void onHeaderClick(NodeView layout);
    }
}
