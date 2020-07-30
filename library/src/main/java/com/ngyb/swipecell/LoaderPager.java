package com.ngyb.swipecell;

import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import java.util.List;
import java.util.concurrent.Executors;

/**
 * 作者：南宫燚滨
 * 描述：
 * 邮箱：nangongyibin@gmail.com
 * 日期：2020/7/29 22:36
 */
public abstract class LoaderPager extends FrameLayout {
    private View loadingView;
    private View errorView;
    private View successView;
    // 定义状态
    public static final int state_load = 110;
    public static final int state_success = 120;
    public static final int state_error = 119;
    //当前的状态
    private int currentState = state_load;
    private Handler handler = new Handler();

    public LoaderPager(@NonNull Context context) {
        this(context, null);
    }

    public LoaderPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoaderPager(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLoaderPager();
    }

    private void initLoaderPager() {
        //把三个界面都加载进来，然后根据不同的状态去自动切换页面
        // 创建一个加载中界面
        if (loadingView == null) {
            loadingView = View.inflate(getContext(), R.layout.page_loading, null);
        }
        addView(loadingView);
        // 加载错误界面
        if (errorView == null) {
            errorView = View.inflate(getContext(), R.layout.page_error, null);
            Button reload = errorView.findViewById(R.id.btn_reload);
            reload.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //切换到加载状态
                    currentState = state_load;
                    // 切换页面
                    showPager();
                    Executors.newSingleThreadExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                            SystemClock.sleep(500);
                            //重新加载数据
                            loadData();
                        }
                    });
                }
            });
        }
        addView(errorView);
        //加载成功
        if (successView == null) {
            successView = createSuccess();
        }
        addView(successView);
        //切换界面的方法
        showPager();
        //根据加载的json数据切换页面
        loadData();
    }

    protected abstract View createSuccess();

    private void loadData() {
        //创建一个线程的线程池
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                //得到网路的数据，并解析完成的json对象
                Object obj = getNetData();
                //检查对象，根据对象切换状态
                currentState = check(obj);
                //切换界面
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        showPager();
                    }
                });
            }
        });
    }

    /**
     * 切换界面
     */
    private void showPager() {
        // 全部隐藏
        loadingView.setVisibility(GONE);
        errorView.setVisibility(GONE);
        successView.setVisibility(GONE);
        switch (currentState) {
            case state_load:
                loadingView.setVisibility(VISIBLE);
                break;
            case state_success:
                successView.setVisibility(VISIBLE);
                break;
            case state_error:
                errorView.setVisibility(VISIBLE);
                break;
            default:
                break;
        }
    }

    /**
     * @return 得到网络数据，json有几种型式，对象，集合
     */
    protected abstract Object getNetData();

    /**
     * @param obj
     * @return 根据我们的返回对象来检查 返回状态
     */
    private int check(Object obj) {
        if (obj == null) {
            //出错
            return state_error;
        } else {
            //成功状态，不一定，集合，如果为0个数据，那么也是错的
            if (obj instanceof List) {
                List list = (List) obj;
                if (list.size() > 0) {
                    return state_success;
                } else {
                    return state_error;
                }
            } else {
                return state_success;
            }
        }
    }
}
