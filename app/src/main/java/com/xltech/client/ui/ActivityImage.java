package com.xltech.client.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xltech.client.data.EnumMessage;
import com.xltech.client.service.AppPlayer;
import com.xltech.client.service.ManPictures;

public class ActivityImage extends Activity {
    private PopupCategory mPopupWindow = null;
    private Point point = new Point();
    public static Handler myHandler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_viewer);

        getWindowManager().getDefaultDisplay().getSize(point);
        mPopupWindow = new PopupCategory(getApplicationContext(), point.x);

        TextView btnMenu = (TextView)findViewById(R.id.image_menu);
        btnMenu.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPopupWindow != null) {
                    mPopupWindow.showPopupWindow(findViewById(R.id.image_viewer));
                }
            }
        });
        TextView btnNext = (TextView)findViewById(R.id.image_next);
        btnNext.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ManPictures.getInstance().nextPicture()) {
                    ShowPicture();
                }
            }
        });
        TextView btnPrev = (TextView)findViewById(R.id.image_prev);
        btnPrev.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ManPictures.getInstance().prevPicture()) {
                    ShowPicture();
                }
            }
        });

        myHandler = new Handler() {
            //接收到消息后处理
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case EnumMessage.OPEN_VIDEO:
                        OnOpenVideo();
                        break;
                }
                super.handleMessage(msg);
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        PopupCategory.parentName = ActivityImage.class.getName();
        ManPictures.getInstance().getPictureList();
        ShowPicture();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void OnOpenVideo() {
        if (mPopupWindow != null) {
            mPopupWindow.showPopupWindow(null);
        }

        Intent intent = new Intent(this, ActivityPlayer.class);
        startActivity(intent);
    }

    private void ShowPicture() {
        String strLeftPicture = ManPictures.getInstance().getPictureName(AppPlayer.LEFT_PALER);
        if (strLeftPicture != null) {
            Bitmap leftBitmap = BitmapFactory.decodeFile(strLeftPicture);
            ImageView leftImage = (ImageView) findViewById(R.id.left_image);
            leftImage.setImageBitmap(leftBitmap);
        }

        String strRightPicture = ManPictures.getInstance().getPictureName(AppPlayer.RIGHT_PLAYER);
        if (strRightPicture != null) {
            Bitmap rightBitmap = BitmapFactory.decodeFile(strRightPicture);
            ImageView rightImage = (ImageView) findViewById(R.id.right_image);
            rightImage.setImageBitmap(rightBitmap);
        }
    }
}
