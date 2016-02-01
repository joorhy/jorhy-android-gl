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

        CategoryRequest loginRequest = new CategoryRequest();
        loginRequest.execute("");

        TextView btnBack = (TextView) contentView.findViewById(R.id.list_back);
        btnBack.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupWindow(null);
            }
        });

        TextView btnExit = (TextView) contentView.findViewById(R.id.exit);
        btnExit.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogoutRequest loginRequest = new LogoutRequest();
                loginRequest.execute("");
            }
        });
    }

    public void showPopupWindow(View parent) {
        if (!this.isShowing() && parent != null) {
            this.showAtLocation(parent, Gravity.LEFT, 0, 0);
        } else {
            this.dismiss();
        }
    }

    class CategoryRequest extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... params) {
            NetProtocol.getInstance().GetCategory();
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progresses) {

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }

    class LogoutRequest extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... params) {
            NetProtocol.getInstance().Logout();
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progresses) {
            Log.i("", "onProgressUpdate(Progress... progresses) called");
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ManActivitys.getInstance().popAllActivityExceptOne();
            //System.exit(0);
        }
    }
}
