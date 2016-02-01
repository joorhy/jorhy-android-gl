package com.xltech.client.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xltech.client.service.AppPlayer;
import com.xltech.client.service.ManPictures;
import com.xltech.client.service.ManActivitys;


public class ActivityImage extends Activity {
    private PopupCategory mPopupWindow = null;
    Point point = new Point();

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
        ManActivitys.getInstance().pushActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ManPictures.getInstance().getPictureList();
        ShowPicture();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ManActivitys.getInstance().popActivity(this);
    }

    public void HidePopupWindow() {
        if (mPopupWindow != null) {
            mPopupWindow.showPopupWindow(null);
        }
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
