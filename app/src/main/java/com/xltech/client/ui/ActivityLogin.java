package com.xltech.client.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.*;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.xltech.client.config.Configer;
import com.xltech.client.data.DataLogin;
import com.xltech.client.service.ManActivitys;
import com.xltech.client.service.NetProtocol;

public class ActivityLogin extends Activity {
    private Button btn = null;
    private EditText txtServer = null;
    private EditText txtUser = null;
    private EditText txtPassword = null;
    private Handler myHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        btn = (Button) findViewById(R.id.sign_in_button);
        txtServer = (EditText) findViewById(R.id.server_edit);
        txtUser = (EditText) findViewById(R.id.username_edit);
        txtPassword = (EditText) findViewById(R.id.password_edit);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn.setText("登陆中...");
                txtServer = (EditText) findViewById(R.id.server_edit);
                txtUser = (EditText) findViewById(R.id.username_edit);
                txtPassword = (EditText) findViewById(R.id.password_edit);

                EnableLogin(false);
                NetProtocol.getInstance().Login(txtServer.getText().toString(), 8502,
                        txtUser.getText().toString(), txtPassword.getText().toString(), 1);
            }
        });
        ManActivitys.getInstance().pushActivity(this);

        // 实例化一个handler
        myHandler = new Handler() {
            //接收到消息后处理
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        btn.setText("登陆");
                        EnableLogin(true);
                        break;
                }
                super.handleMessage(msg);
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //ManActivitys.getInstance().popActivity(this);
    }

    private void EnableLogin(boolean isEnable) {
        btn.setClickable(isEnable);
        txtServer.setEnabled(isEnable);
        txtUser.setEnabled(isEnable);
        txtPassword.setEnabled(isEnable);
    }

    public void OnLoginReturn() {
        Message message = new Message();
        message.what = 1;
        //发送消息
        ActivityLogin.this.myHandler.sendMessage(message);
        //EnableLogin(true);
        //btn.setText("登陆");
        if (DataLogin.getInstance().getResult() == 0) {
            Intent intent = new Intent(this, ActivityImage.class);
            startActivity(intent);
        } else {
            LayoutInflater layoutInflater = LayoutInflater.from(this);
            View viewAddEmployee = layoutInflater.inflate(R.layout.notice_dialog, null);
            TextView txt = (TextView)viewAddEmployee.findViewById(R.id.message);
            txt.setText("登陆失败");

            /*new AlertDialog.Builder(this).setTitle("错误：").setMessage("登陆失败").setPositiveButton("确定",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).show();*/
        }
    }
}