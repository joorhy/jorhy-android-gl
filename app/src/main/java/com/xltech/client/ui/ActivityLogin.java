package com.xltech.client.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.*;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
                EditText txtUser = (EditText) findViewById(R.id.username_edit);
                EditText txtPassword = (EditText) findViewById(R.id.password_edit);
                LoginParams params = new LoginParams();
                params.strAddress = "";
                params.nPort = 8052;
                params.strUsername = txtUser.getText().toString();
                params.strPassword = txtPassword.getText().toString();
                params.nForced = 1;

                LoginRequest loginRequest = new LoginRequest();
                loginRequest.execute(params);
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

    private class LoginParams {
        public String strAddress;
        public int nPort;
        public String strUsername;
        public String strPassword;
        public int nForced;
    }

    class LoginRequest extends AsyncTask<LoginParams, Integer, String> {
        @Override
        protected void onPreExecute() {
            Button btn = (Button)findViewById(R.id.sign_in_button);
            btn.setEnabled(false);
        }

        @Override
        protected String doInBackground(LoginParams... params) {
            LoginParams param = params[0];
            NetProtocol.getInstance().Login(param.strAddress, param.nPort, param.strUsername,
                    param.strPassword, param.nForced);
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progresses) {
            Log.i("", "onProgressUpdate(Progress... progresses) called");
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Button btn = (Button)findViewById(R.id.sign_in_button);
            btn.setEnabled(true);

            int nResult = DataLogin.getInstance().getResult();
            if (nResult == 0) {
                Intent intent = new Intent(ActivityLogin.this, ActivityImage.class);
                startActivity(intent);
            }
        }
    }
}