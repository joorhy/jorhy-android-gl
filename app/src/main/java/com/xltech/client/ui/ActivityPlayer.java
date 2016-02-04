package com.xltech.client.ui;

import android.app.Activity;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.xltech.client.data.DataSelectedVehicle;
import com.xltech.client.service.AppPlayer;
import com.xltech.client.service.GLFrameSurface;
import com.xltech.client.service.ManPictures;
import com.xltech.client.service.ManActivitys;

/**
 * Created by JooLiu on 2016/1/13.
 */
public class ActivityPlayer extends Activity {
    private PopupCategory mPopupWindow = null;
    Point point = new Point();

    private AppPlayer playerLeft = null;
    private AppPlayer playerRight = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_player);

        getWindowManager().getDefaultDisplay().getSize(point);
        mPopupWindow = new PopupCategory(getApplicationContext(), point.x);

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

        ManActivitys.getInstance().pushActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (playerLeft == null && playerRight == null) {
            playerLeft = new AppPlayer((GLFrameSurface) findViewById(R.id.left_glsurface),
                    AppPlayer.LEFT_PALER);
            playerRight = new AppPlayer((GLFrameSurface) findViewById(R.id.right_glsurface),
                    AppPlayer.RIGHT_PLAYER);

            playerLeft.Play();
            playerRight.Play();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (playerLeft != null && playerRight != null) {
            playerLeft.Stop();
            playerRight.Stop();

            /*playerLeft = null;
            playerRight = null;*/
        }
        ManActivitys.getInstance().popActivity(this);
    }

    public void HidePopupWindow() {
        if (mPopupWindow != null) {
            mPopupWindow.showPopupWindow(null);
        }
    }

    public void RefreshPopupWindow() {
        mPopupWindow.refreshPopupWindow();
    }
}
