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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        Button btn = (Button)findViewById(R.id.sign_in_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText txtServer = (EditText)findViewById(R.id.server_edit);
                EditText txtUser = (EditText) findViewById(R.id.username_edit);
                EditText txtPassword = (EditText) findViewById(R.id.password_edit);

                NetProtocol.getInstance().Login(txtServer.getText().toString(), 8502,
                        txtUser.getText().toString(), txtPassword.getText().toString(), 1);
            }
        });
        ManActivitys.getInstance().pushActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ManActivitys.getInstance().popActivity(this);
    }

    public void OnLoginReturn() {
        if (DataLogin.getInstance().getResult() == 0) {
            Intent intent = new Intent(this, ActivityImage.class);
            startActivity(intent);
        } else {
            /*LayoutInflater layoutInflater = LayoutInflater.from(this);
            View viewAddEmployee = layoutInflater.inflate(R.layout.notice_dialog, null);
            TextView txt = (TextView)viewAddEmployee.findViewById(R.id.message);
            txt.setText("登陆失败");*/

            new AlertDialog.Builder(this).setTitle("错误：").setMessage("登陆失败").setPositiveButton("确定",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).show();
        }
    }
}