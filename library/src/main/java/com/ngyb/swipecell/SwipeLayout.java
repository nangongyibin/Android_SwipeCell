package com.ngyb.swipecell;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * 作者：南宫燚滨
 * 描述：
 * 邮箱：nangongyibin@gmail.com
 * 日期：2020/7/29 23:08
 */
public class SwipeLayout extends FrameLayout {
    View content, delete;
    ViewDragHelper dragHelper;
    float downX, downY;
    OnSwipeListener listener;
    private static final String TAG = "SwipeLayout";

    public SwipeLayout(@NonNull Context context) {
        this(context, null);
    }

    public SwipeLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        dragHelper = ViewDragHelper.create(this, cb);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        content = getChildAt(0);
        delete = getChildAt(1);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        content.layout(0, 0, content.getMeasuredWidth(), content.getMeasuredHeight());
        delete.layout(content.getMeasuredWidth(), 0, content.getMeasuredWidth() + delete.getMeasuredWidth(), delete.getMeasuredHeight());
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean result = dragHelper.shouldInterceptTouchEvent(ev);
        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                //通过判断用户手指滑动的方向来猜测到底是要滑动listview还是swipLayout
                //如果是偏向于垂直，就是滑动listview，如果偏向于水平，就是滑动swipLayout
                float dx = event.getX() - downX;
                float dy = event.getY() - downY;
                if (Math.abs(dx) > Math.abs(dy)) {
                    requestDisallowInterceptTouchEvent(true);
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        dragHelper.processTouchEvent(event);
        return true;
    }

    ViewDragHelper.Callback cb = new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(@NonNull View view, int i) {
            return true;
        }

        @Override
        public void onViewCaptured(@NonNull View capturedChild, int activePointerId) {
            super.onViewCaptured(capturedChild, activePointerId);
        }

        @Override
        public int getViewHorizontalDragRange(@NonNull View child) {
            return 1;
        }

        @Override
        public int getViewVerticalDragRange(@NonNull View child) {
            return 0;
        }

        @Override
        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
            if (child == content) {
                if (left > 0) {
                    left = 0;
                } else if (left < -delete.getMeasuredWidth()) {
                    left = -delete.getMeasuredWidth();
                }
            } else if (child == delete) {
                if (left < content.getMeasuredWidth() - delete.getMeasuredWidth()) {
                    left = content.getMeasuredWidth() - delete.getMeasuredWidth();
                } else if (left > content.getMeasuredWidth()) {
                    left = content.getMeasuredWidth();
                }
            }
            return left;
        }

        @Override
        public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            if (changedView == content) {
                delete.offsetLeftAndRight(dx);
            } else if (changedView == delete) {
                content.offsetLeftAndRight(dx);
            }
            if (listener != null) {
                if (-content.getLeft() == delete.getMeasuredWidth()) {
                    listener.onOpen(SwipeLayout.this);
                } else if (content.getLeft() == 0) {
                    listener.onClose(SwipeLayout.this);
                }
            }
        }

        @Override
        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            Log.e(TAG, "onViewReleased: " + content.getLeft() + "====" + content.getRight());
            Log.e(TAG, "onViewReleased: " + delete.getMeasuredWidth());
            if (content.getLeft() < -delete.getMeasuredWidth() / 3) {
                //open
                open();
            } else {
                //close
                close();
            }
        }
    };

    public void close() {
        dragHelper.smoothSlideViewTo(content, 0, 0);
        ViewCompat.postInvalidateOnAnimation(SwipeLayout.this);
    }

    private void open() {
        dragHelper.smoothSlideViewTo(content, -delete.getMeasuredWidth(), 0);
        ViewCompat.postInvalidateOnAnimation(SwipeLayout.this);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (dragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(SwipeLayout.this);
        }
    }

    public interface OnSwipeListener {
        void onOpen(SwipeLayout openLayout);

        void onClose(SwipeLayout closeLayout);
    }

    public void setOnSwipeListener(OnSwipeListener listener) {
        this.listener = listener;
    }
}
