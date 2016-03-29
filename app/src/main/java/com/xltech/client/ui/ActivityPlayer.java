package com.xltech.client.ui;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.xltech.client.data.DataSelectedVehicle;
import com.xltech.client.data.EnumMessage;
import com.xltech.client.service.AppPlayer;
import com.xltech.client.service.GLFrameSurface;
import com.xltech.client.service.ManPictures;

/**
 * Created by JooLiu on 2016/1/13.
 */
public class ActivityPlayer extends Activity {
    public static Handler myHandler = null;

    private PopupCategory mPopupWindow = null;
    private Point point = new Point();
    private AppPlayer playerLeft = null;
    private AppPlayer playerRight = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_player);

        getWindowManager().getDefaultDisplay().getSize(point);
        mPopupWindow = new PopupCategory(getApplicationContext(), point.x);

        playerLeft = new AppPlayer((GLFrameSurface) findViewById(R.id.left_glsurface),
                AppPlayer.LEFT_PALER);
        playerRight = new AppPlayer((GLFrameSurface) findViewById(R.id.right_glsurface),
                AppPlayer.RIGHT_PLAYER);

        TextView btnMenu = (TextView)findViewById(R.id.player_menu);
        btnMenu.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPopupWindow != null) {
                    mPopupWindow.showPopupWindow(findViewById(R.id.video_player));
                }
            }
        });

        TextView btnShot = (TextView)findViewById(R.id.player_shot);
        btnShot.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (playerLeft != null && playerRight != null) {
                    ManPictures.getInstance().refreshTime();
                    playerLeft.Shot();
                    playerRight.Shot();
                }
            }
        });

        TextView btnNext = (TextView)findViewById(R.id.player_next);
        btnNext.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (playerLeft != null && playerRight != null) {
                    if(DataSelectedVehicle.getInstance().nextChannel()) {
                        playerLeft.Restart();
                        playerRight.Restart();
                    }
                }
            }
        });

        TextView btnPrev = (TextView)findViewById(R.id.player_prev);
        btnPrev.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (playerLeft != null && playerRight != null) {
                    if (DataSelectedVehicle.getInstance().prevChannel()) {
                        playerLeft.Restart();
                        playerRight.Restart();
                    }
                }
            }
        });

        myHandler = new Handler() {
            //接收到消息后处理
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case EnumMessage.CHANGE_VIDEO:
                        OnChangeVideo();
                        break;
                }
                super.handleMessage(msg);
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        PopupCategory.parentName = ActivityPlayer.class.getName();
        if (playerLeft != null && playerRight != null) {
            playerLeft.Play();
            playerRight.Play();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (playerLeft != null && playerRight != null) {
            playerLeft.Stop();
            playerRight.Stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        playerLeft = null;
        playerRight = null;
    }

    public void OnChangeVideo() {
        if (mPopupWindow != null) {
            mPopupWindow.showPopupWindow(null);
        }

        if (playerLeft != null && playerRight != null) {
            playerLeft.Restart();
            playerRight.Restart();
        }
    }
}
