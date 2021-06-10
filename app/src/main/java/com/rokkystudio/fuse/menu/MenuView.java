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

public class MenuView extends LinearLayout implements
    View.OnClickListener, View.OnTouchListener
{
    private MenuItem mMenuItem = new MenuItem();

    public MenuView(Context context) {
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
        if (!mMenuItem.hasChilds()) return;
        mMenuItem.setExpanded(true);

        ViewGroup wrapper = getWrapperLayout();
        if (wrapper == null) return;
        wrapper.setVisibility(VISIBLE);
        wrapper.removeAllViews();
        setIcon(R.drawable.arrow_up);

        for (MenuItem childItem : mMenuItem.getChilds()) {
            MenuView childView = childItem.getView(getContext());
            addChildView(childView);
        }

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
        if (!mMenuItem.hasChilds()) return;
        mMenuItem.setExpanded(false);

        ViewGroup wrapper = getWrapperLayout();
        if (wrapper == null) return;
        setIcon(R.drawable.arrow_down);

        // Измерение высоты, занимаемой дочерними элементами
        final int wrapperHeight = getWrapperHeight();

        Animation animation = new Animation()
        {
            @Override
            protected void applyTransformation(float time, Transformation trans) {
                if (time == 1) {
                    wrapper.getLayoutParams().height = 0;
                    removeChildViews();
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

    public void setMenuItem(@NonNull MenuItem menuItem)
    {
        // Имя элемента
        TextView itemName = findViewById(R.id.ItemName);
        if (itemName != null) itemName.setText(menuItem.getName());

        // Скрываем корневой элемент без имени
        if (menuItem.isRoot() && !menuItem.hasName()) {
            ViewGroup header = findViewById(R.id.HeaderLayout);
            if (header != null) header.setVisibility(GONE);
            ViewGroup wrapper = findViewById(R.id.WrapperLayout);
            if (wrapper != null) wrapper.setBackgroundResource(0);
        }

        // Корневой элемент всегда развернут
        if (menuItem.isRoot()) menuItem.setExpanded(true);

        // Стрелочка элемента
        if (!menuItem.hasChilds()) {
            setIcon(R.drawable.arrow_right);
        } else if (menuItem.isExpanded()) {
            setIcon(R.drawable.arrow_up);
        } else {
            setIcon(R.drawable.arrow_down);
        }

        mMenuItem = menuItem;
    }

    public MenuItem getMenuItem() {
        return mMenuItem;
    }

    public void addChildView(ViewGroup childView) {
        ViewGroup wrapper = getWrapperLayout();
        if (wrapper == null) return;
        wrapper.addView(childView);
        if (mMenuItem.isExpanded()) {
            wrapper.setVisibility(VISIBLE);
        }
    }

    public void removeChildViews() {
        ViewGroup wrapper = getWrapperLayout();
        if (wrapper == null) return;
        wrapper.removeAllViews();
        wrapper.setVisibility(GONE);
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
        ImageView itemIcon = findViewById(R.id.ItemIcon);
        if (itemIcon != null) itemIcon.setImageResource(resId);
    }

    @Override
    public void onClick(View view) {
        if (mMenuItem.getOnItemClickListener() != null) {
            mMenuItem.getOnItemClickListener().onItemClick(mMenuItem);
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
