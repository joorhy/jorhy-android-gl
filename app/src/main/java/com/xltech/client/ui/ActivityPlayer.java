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
    private final String PLAY = "play";
    private final String STOP = "stop";
    private final String REPLAY = "replay";

    private PopupCategory mPopupWindow = null;
    Point point = new Point();

    private AppPlayer playerLeft = null;
    private AppPlayer playerRight = null;

    private boolean isReplay = false;

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
                        PlayRequest request = new PlayRequest();
                        request.execute(REPLAY);
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
                        PlayRequest request = new PlayRequest();
                        request.execute(REPLAY);
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

            PlayRequest request = new PlayRequest();
            request.execute(PLAY);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (playerLeft != null && playerRight != null) {
            playerLeft.Stop();
            playerRight.Stop();

            playerLeft = null;
            playerRight = null;
        }
        ManActivitys.getInstance().popActivity(this);
    }

    public void HidePopupWindow() {
        if (mPopupWindow != null) {
            mPopupWindow.showPopupWindow(null);
        }
    }

    class PlayRequest extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... params) {
            String strParam = params[0];
            if (strParam.equals(PLAY)) {
                playerLeft.Play();
                playerRight.Play();
            } else if (strParam.equals(STOP)) {
                playerLeft.Stop();
                playerRight.Stop();
            } else if (strParam.equals(REPLAY)) {
                isReplay = true;
                playerLeft.Stop();
                playerRight.Stop();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progresses) {
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (isReplay) {
                isReplay = false;
                PlayRequest request = new PlayRequest();
                request.execute(PLAY);
            }
        }
    }
}
