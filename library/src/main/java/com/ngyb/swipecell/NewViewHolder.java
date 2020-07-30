package com.ngyb.swipecell;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;

/**
 * 作者：南宫燚滨
 * 描述：
 * 邮箱：nangongyibin@gmail.com
 * 日期：2020/7/29 23:04
 */
public class NewViewHolder extends RecyclerView.ViewHolder {
    private SparseArray<View> views = new SparseArray<>();

    public NewViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    /**
     * 根据ID自动查找控件
     *
     * @param id
     * @return
     */
    public View getViewById(int id) {
        View view = views.get(id);
        if (view == null) {
            view = this.itemView.findViewById(id);
            views.put(id, view);
        }
        return view;
    }
}
