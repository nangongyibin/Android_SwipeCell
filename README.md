# Android_Android_SwipeCell

滑动删除效果
 
### 1.先写一个类SwipeLayout，继承自FrameLayout  ###

### 2.在onLayout方法中对contentView和deleteView进行摆放  ###

    protected void onLayout(boolean changed, int left, int top, int right,
        int bottom) {
	    contentView.layout(0, 0, contentWidth, contentHeight);
	    deleteView.layout(contentWidth, 0, contentWidth+deleteWidth, deleteHeight);
	}

### 3.结合上午所学知识，利用ViewDragHelper实现让SwipeLayout的2个子View进行拖拽移动，主要是Callback的实现，如下  ###

    private ViewDragHelper.Callback callback = new Callback() {
	@Override
	public boolean tryCaptureView(View child, int pointerId) {
	        return child==contentView || child==deleteView;
	}
	public int getViewHorizontalDragRange(View child) {
	        return deleteWidth;
	}
	public int clampViewPositionHorizontal(View child, int left, int dx) {
	        if(child==contentView){
	            //限定contentView
	            if(left>0)left = 0;
	            if(left<-deleteWidth)left = -deleteWidth;
	        }else if (child==deleteView) {
	            //限定deleteView
	            if(left>contentWidth)left = contentWidth;
	            if(left<(contentWidth-deleteWidth)){
	                left = contentWidth-deleteWidth;
	            }
	        }
	        return left;
	}
	/**
	 * 一般实现view的伴随移动
	 */
	public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
	        //如果contentView移动了，那么让deleteView伴随移动
	        if(changedView==contentView){
	            int newLeft = deleteView.getLeft()+dx;
	            deleteView.layout(newLeft,0, newLeft+deleteWidth,deleteHeight);
	        }else if (changedView==deleteView) {
	            //让contentView做伴随移动
	            int newLeft = contentView.getLeft()+dx;
	            contentView.layout(newLeft,0, newLeft+contentWidth,contentHeight);
	        }
	};
	/**
	 * 松开手指回调
	 */
	public void onViewReleased(View releasedChild, float xvel, float yvel) {
	        if(contentView.getLeft()<-deleteWidth/2){
	            //open
	            open();
	        }else {
	            //close
	            close();
	        }
	
	};
	};

### 4.然后将实现好的可滑动的SwipeLayout放入ListView的adapter的布局中，此时我们遇到2个bug  ###

当我们左右拖动item滑动时，再上下滑动会遇到事件被ListView捕获并处理，导致我们无法继续控制item的滑动；
 
我们可以同时滑动出多个item，而需求是只能允许一个item是打开的；
 
### 5.解决第一个bug的思路是：在onTouchEvent方法判断当前手指移动的方向到底是偏向于水平还是偏向于垂直，如果是偏向于水平那么就认为用户是希望滑动item，那么则请求父View不要去拦截事件  ###

    private float downX,downY;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
	    switch (event.getAction()) {
	    case MotionEvent.ACTION_DOWN:
	        downX = event.getX();
	        downY = event.getY();
	        break;
	    case MotionEvent.ACTION_MOVE:
	        float moveX = event.getX();
	        float moveY = event.getY();
	        //计算移动的距离
	        float deltaX = moveX - downX;
	        float deltaY = moveY - downY;
	        //判断手指移动的方向到底是偏向于水平还是垂直
	        if(Math.abs(deltaX)>Math.abs(deltaY)){
	            //说明偏向于水平，那么认为要滑动条目，则listview不应该拦截
	            requestDisallowInterceptTouchEvent(true);//请求父VIew不拦截
	        }
	
	        break;
	    case MotionEvent.ACTION_UP:
	        break;
	    }
	    viewDragHelper.processTouchEvent(event);
	    return true;
	}

### 6.定义滑动监听器  ###

    private OnSwipeListener onSwipeListener;

	    public OnSwipeListener getOnSwipeListener() {
	        return onSwipeListener;
	    }
	    public void setOnSwipeListener(OnSwipeListener onSwipeListener) {
	        this.onSwipeListener = onSwipeListener;
	    }
	
	    public interface OnSwipeListener{
	        void onOpen();
	        void onClose();
	    }

### 7.在Activity的adapter中设置监听器  ###

    //设置监听器
	holder.swipeLayout.setOnSwipeListener(new SwipeLayout.OnSwipeListener() {
	    @Override
	    public void onOpen(SwipeLayout swipeLayout) {
	        if(openLayout!=null && openLayout!=swipeLayout){
	            openLayout.close();
	        }
	        openLayout = swipeLayout;
	    }
	    @Override
	    public void onClose(SwipeLayout swipeLayout) {
	        if(openLayout==swipeLayout){
	            openLayout = null;
	        }
	    }
	});

### 8.最后，由于我们重写onTouchEvent处理了事件，导致ListView的条目点击无效了，此时最有效最简单的做法是自己去判断触摸事件实现点击行为，思路是：记录按下的坐标和时间，在抬起的时候计算整个按下抬起的时间和距离，如果时间小于400毫秒，并且距离小于touchSlop，则认为是点击事件，事实上系统也是这样实现点击事件的  ###

    case MotionEvent.ACTIONDOWN:
	    downX = event.getX();
	    downY = event.getY();
	    //记录按下的时间点
	    downTime = System.currentTimeMillis();
	    break;
	case MotionEvent.ACTIONUP:
	    //计算抬起的时间点
	    long upTime = System.currentTimeMillis();
	    //计算抬起的坐标
	    float upX = event.getX();
	    float upY = event.getY();
	    //计算按下和抬起的总时间
	    long touchDuration = upTime-downTime;
	    //计算按下点和抬起点的距离
	    float touchD = Utils.getDistanceBetween2Points(new PointF(downX, downY), new PointF(upX, upY));
	    if(touchDuration<400 && touchD<touchSlop){
	        if(listener!=null){
	            listener.onItemClick();
	        }
	    }
	    break;

![](https://github.com/nangongyibin/Android_SwipeCell/blob/master/example.gif?raw=true)

## Step 1. Add the JitPack repository to your build file ##


Add it in your root build.gradle at the end of repositories:

    allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

## Step 2. Add the dependency ##

    	dependencies {
		        implementation 'com.github.nangongyibin:Android_SwipeCell:1.0.1'
		}