package com.rokkystudio.fuse.menu;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.content.Context;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.rokkystudio.fuse.R;

public class NodeView extends LinearLayout implements
    View.OnClickListener, View.OnTouchListener
{
    private NodeItem mNodeItem = new NodeItem();

    public NodeView(Context context) {
        super(context);
        inflate(getContext(), R.layout.menu_item, this);
        ViewGroup header = findViewById(R.id.HeaderLayout);
        if (header != null) {
            header.setOnClickListener(this);
            header.setOnTouchListener(this);
        }
    }

    public void expand()
    {
        if (!mNodeItem.hasChilds()) return;
        mNodeItem.setExpanded(true);

        ViewGroup wrapper = getWrapperLayout();
        if (wrapper == null) return;
        wrapper.setVisibility(VISIBLE);
        wrapper.removeAllViews();
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
                }
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

    public void collapse()
    {
        if (!mNodeItem.hasChilds()) return;
        mNodeItem.setExpanded(false);

        ViewGroup wrapper = getWrapperLayout();
        if (wrapper == null) return;
        setIcon(R.drawable.arrow_down);

        // Измерение высоты, занимаемой дочерними элементами
        final int wrapperHeight = getWrapperHeight();

        Animation animation = new Animation()
        {
            @Override
            protected void applyTransformation(float time, Transformation trans) {
                if (time == 1)
                {

                    for (NodeItem child : mNodeItem.getChilds()) {
                        wrapper.addView(child.getView(getContext()));
                    }

                    wrapper.getLayoutParams().height = 0;
                    wrapper.setVisibility(GONE);
                    wrapper.removeAllViews();
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

        animation.setDuration(500);
        startAnimation(animation);
    }

    public void setNode(@NonNull NodeItem nodeItem)
    {
        // Имя элемента
        TextView itemName = ((TextView) findViewById(R.id.ItemName));
        if (itemName != null) itemName.setText(nodeItem.getName());

        // Скрываем корневой элемент без имени
        if (nodeItem.isRoot() && !nodeItem.hasName()) {
            ViewGroup header = findViewById(R.id.HeaderLayout);
            if (header != null) header.setVisibility(GONE);
            ViewGroup wrapper = findViewById(R.id.WrapperLayout);
            if (wrapper != null) wrapper.setBackgroundResource(0);
        }

        // Корневой элемент всегда развернут
        if (nodeItem.isRoot()) nodeItem.setExpanded(true);

        // Стрелочка элемента
        if (!nodeItem.hasChilds()) {
            setIcon(R.drawable.arrow_right);
        } else if (nodeItem.isExpanded()) {
            setIcon(R.drawable.arrow_up);
        } else {
            setIcon(R.drawable.arrow_down);
        }

        mNodeItem = nodeItem;
    }

    public NodeItem getNode() {
        return mNodeItem;
    }

    public void addChild(ViewGroup childView) {
        ViewGroup wrapper = getWrapperLayout();
        if (wrapper == null) return;
        getWrapperLayout().addView(childView);
        if (mNodeItem.isExpanded()) {
            wrapper.setVisibility(VISIBLE);
        }
    }

    private ViewGroup getWrapperLayout() {
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

    public void setIcon(int resId) {
        ImageView itemIcon = ((ImageView) findViewById(R.id.ItemIcon));
        if (itemIcon != null) itemIcon.setImageResource(resId);
    }

    @Override
    public void onClick(View view) {
        if (mNodeItem.getOnNodeClickListener() != null) {
            mNodeItem.getOnNodeClickListener().onNodeClick(mNodeItem);
        }
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
}
