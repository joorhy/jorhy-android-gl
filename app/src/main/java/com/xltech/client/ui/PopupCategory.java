package com.xltech.client.ui;

import com.xltech.client.service.ManActivitys;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.xltech.client.data.DataCategory;
import com.xltech.client.service.NetProtocol;


/**
 * Created by JooLiu on 2016/1/20.
 */
public class PopupCategory extends PopupWindow{
    private View contentView;
    public static TreeViewAdapter treeViewAdapter;

    public PopupCategory(Context context, int width) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        contentView = inflater.inflate(R.layout.category_list, null);

        // 设置SelectPicPopupWindow的View
        this.setContentView(contentView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(width / 2);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        // 刷新状态
        this.update();
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0000000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(dw);
        // mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimBottom);

        ListView treeView = (ListView) contentView.findViewById(R.id.tree_list);
        treeViewAdapter = new TreeViewAdapter(DataCategory.getInstance().getElements(),
                DataCategory.getInstance().getElementsData(), inflater);
        TreeViewItemClickListener treeViewItemClickListener = new TreeViewItemClickListener(treeViewAdapter);
        treeView.setAdapter(treeViewAdapter);
        treeView.setOnItemClickListener(treeViewItemClickListener);

        TextView btnBack = (TextView) contentView.findViewById(R.id.list_back);
        btnBack.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupWindow(null);
            }
        });

        TextView btnAll = (TextView) contentView.findViewById(R.id.list_all);
        btnAll.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataCategory.getInstance().showAll(true);
                refreshPopupWindow();

            }
        });

        TextView btnOnline = (TextView) contentView.findViewById(R.id.list_online);
        btnOnline.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataCategory.getInstance().showAll(false);
                refreshPopupWindow();
            }
        });

        TextView btnExit = (TextView) contentView.findViewById(R.id.exit);
        btnExit.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View view) {
            NetProtocol.getInstance().Logout();
            ManActivitys.getInstance().popAllActivityExceptOne();
            }
        });

        NetProtocol.getInstance().GetCategory();
    }

    public void showPopupWindow(View parent) {
        if (!this.isShowing() && parent != null) {
            this.showAtLocation(parent, Gravity.LEFT, 0, 0);
        } else {
            this.dismiss();
        }
    }

    public void refreshPopupWindow() {
        ListView treeView = (ListView) contentView.findViewById(R.id.tree_list);
        ((TreeViewItemClickListener)treeView.getOnItemClickListener()).refreshItems();
    }
}
