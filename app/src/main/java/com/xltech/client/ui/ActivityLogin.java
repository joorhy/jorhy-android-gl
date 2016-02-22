package com.xltech.client.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.*;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
        }
    }
}