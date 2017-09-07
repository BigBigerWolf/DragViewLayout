package com.yiche.fixpicture;

import android.content.Context;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by kongj on 2017/9/6.
 */

public class DragViewLayout extends FrameLayout {

    private static final String TAG = "DragViewLayout";
    private ViewDragHelper mViewDragHelper;
    private View dragView;
    private int states = ViewDragHelper.STATE_IDLE;
    private boolean isAllViewCanDrag;

    public DragViewLayout(Context context) {
        this(context, null);
    }

    public DragViewLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragViewLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mViewDragHelper = ViewDragHelper.create(this, 1f, new ViewDragCallBack());
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        Log.e("size", getChildCount() + "");
        //满足只有最后一个可拖动的需求
        if (getChildCount() > 0) {
            dragView = getChildAt(getChildCount() - 1);
        }
    }

    /**
     * 控制是否所有view可以拖动。默认只有最后一个可拖动
     *
     * @param isAllViewCanDrag
     */
    public void setAllViewCanDrag(boolean isAllViewCanDrag) {
        this.isAllViewCanDrag = isAllViewCanDrag;
    }

    private class ViewDragCallBack extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            if (isAllViewCanDrag) {
                return true;
            } else {
                return dragView == child;
            }
        }

        /**
         * 处理水平方向上的拖动
         *
         * @param child 拖动的View
         * @param left  移动到x轴的距离
         * @param dx    建议的移动的x距离
         */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            Log.e(TAG, "left:" + left + "++++dx:" + dx);
            //两个if主要是让view在ViewGroup中
            int value = left;
            if (left < getPaddingLeft()) {
                value = getPaddingLeft();
            }

            if (left > getWidth() - child.getMeasuredWidth()) {
                value = getWidth() - child.getMeasuredWidth();
            }
            Object tag = child.getTag();
            if (tag != null) {
                PositionEntry positionEntry = (PositionEntry) tag;
                positionEntry.x = value;//从新赋值 x坐标
            }
            return value;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            Log.e(TAG, "top:" + top + "++++dy:" + dy);
            //两个if主要是让view在ViewGroup中
            int value = top;
            if (top < getPaddingTop()) {
                value = getPaddingTop();
            }
            if (top > getHeight() - child.getMeasuredHeight()) {
                value = getHeight() - child.getMeasuredHeight();
            }

            Object tag = child.getTag();
            if (tag != null) {
                PositionEntry positionEntry = (PositionEntry) tag;
                positionEntry.y = value;//从新赋值 y坐标
            }
            return value;
        }

        @Override
        public void onViewDragStateChanged(int state) {
            Log.e("state", state + "");
            switch (state) {
                case ViewDragHelper.STATE_DRAGGING://正在拖动过程中
                    states = ViewDragHelper.STATE_DRAGGING;
                    break;
                case ViewDragHelper.STATE_IDLE://view没有被拖动
                    states = ViewDragHelper.STATE_IDLE;
                    refreshPosition();
                    break;
            }
            super.onViewDragStateChanged(state);
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return child.getWidth();
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return child.getHeight();
        }
    }

    /**
     * 添加子view的同时，初始化子view的位置，若不传PositionEntry默认为0,0位置
     * @param child
     */
    @Override
    public void addView(View child) {
        FrameLayout.LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
        Object tag = child.getTag();
        if (tag != null) {
            PositionEntry positionEntry = (PositionEntry) tag;
            layoutParams.setMargins(positionEntry.x, positionEntry.y, 0, 0);
            super.addView(child, layoutParams);
        } else {
            super.addView(child);
        }
    }

    /**
     * 这里需要注意的是重新设置layoutparams的时候会重新调用onLayout所以放到onLayout里面会造成递归
     */
    private void refreshPosition() {
        for (int i = 0; i < getChildCount(); i++) {
            View viewChild = getChildAt(i);
            Object tag = viewChild.getTag();
            if (tag != null) {
                PositionEntry positionEntry = (PositionEntry) tag;
                FrameLayout.LayoutParams layoutParams = (LayoutParams) viewChild.getLayoutParams();
                layoutParams.setMargins(positionEntry.x, positionEntry.y, 0, 0);
                viewChild.setLayoutParams(layoutParams);
            }
        }
    }

    /**
     * @return 当前触碰view的状态
     */
    public int getStates() {
        return states;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mViewDragHelper.cancel();
                break;
        }
        return mViewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mViewDragHelper.processTouchEvent(event);
        return true;
    }
}
