package com.example.swipecell;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.widget.AbsListView;
import android.widget.ListView;

import com.ngyb.swipecell.SwipeLayout;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView lv;
    private String[] str = {"张三", "李四", "王五", "赵柳"};
    private SwipeLayout copenLayout;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv = findViewById(R.id.lv);
        lv.setAdapter(new myAdapter(this, R.layout.item, Arrays.asList(str)));
        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (copenLayout != null) {
                    copenLayout.close();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    class myAdapter extends CommonAdapter<String> {

        public myAdapter(Context context, int layoutId, List<String> datas) {
            super(context, layoutId, datas);
        }

        @Override
        protected void convert(ViewHolder viewHolder, String item, int position) {
            viewHolder.setText(R.id.tv, item);
            SwipeLayout sl = viewHolder.getView(R.id.sl);
            sl.setOnSwipeListener(new SwipeLayout.OnSwipeListener() {
                @Override
                public void onOpen(SwipeLayout openLayout) {
                    if (copenLayout != null && copenLayout != openLayout) {
                        copenLayout.close();
                    }
                    copenLayout = openLayout;
                }

                @Override
                public void onClose(SwipeLayout closeLayout) {
                    if (copenLayout == closeLayout) {
                        copenLayout = null;
                    }
                }
            });
        }
    }

}
