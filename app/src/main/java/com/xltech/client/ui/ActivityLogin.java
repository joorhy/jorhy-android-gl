package com.xltech.client.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.*;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.xltech.client.data.DataLogin;
import com.xltech.client.data.EnumMessage;
import com.xltech.client.service.NetProtocol;

public class ActivityLogin extends Activity {
    private TextView txtUserLable = null;
    private TextView txtPasswordLable = null;
    private Button btnLogin = null;
    private EditText txtUser = null;
    private EditText txtPassword = null;
    private TextView txtErrMessage = null;
    private CheckBox cbForceSignIn = null;
    public static Handler myHandler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        Initialize();
        txtUserLable = (TextView) findViewById(R.id.login_user_input);
        txtPasswordLable = (TextView) findViewById(R.id.login_password_input);
        btnLogin = (Button) findViewById(R.id.sign_in_button);
        txtUser = (EditText) findViewById(R.id.username_edit);
        txtPassword = (EditText) findViewById(R.id.password_edit);
        txtErrMessage = (TextView) findViewById(R.id.error_message_text);
        cbForceSignIn = (CheckBox) findViewById(R.id.check_force_login);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnLogin.setText("登陆中...");
                txtErrMessage.setText("");
                EnableLogin(false);
                myHandler.postDelayed(runnable, 5000);
                NetProtocol.getInstance().Login("222.214.218.237", 8502,
                        txtUser.getText().toString(), txtPassword.getText().toString(),
                        cbForceSignIn.isChecked() ? 1 : 0);
            }
        });

        // 实例化一个handler
        myHandler = new Handler() {
            //接收到消息后处理
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case EnumMessage.LOGIN_RETURN:
                        OnLoginReturn();
                        break;
                }
                super.handleMessage(msg);
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();

        btnLogin.setText(R.string.login_label_sign_in);
        EnableLogin(true);
        NetProtocol.getInstance().Logout();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void Initialize() {
        btnLogin = null;
        txtUser = null;
        txtPassword = null;
        txtErrMessage = null;
        cbForceSignIn = null;
        myHandler = null;
    }

    private void EnableLogin(boolean isEnable) {
        txtUserLable.setEnabled(isEnable);
        txtPasswordLable.setEnabled(isEnable);
        btnLogin.setEnabled(isEnable);
        txtUser.setEnabled(isEnable);
        txtPassword.setEnabled(isEnable);
        cbForceSignIn.setEnabled(isEnable);
    }

    private void OnLoginReturn() {
        myHandler.removeCallbacks(runnable);
        switch (DataLogin.getInstance().getResult()) {
            case 0:
                Intent intent = new Intent(this, ActivityImage.class);
                startActivity(intent);
                break;
            case 2:
                txtErrMessage.setText(R.string.login_re_user_pwd_error);
                btnLogin.setText(R.string.login_label_sign_in);
                EnableLogin(true);
                break;
            case 6:
                txtErrMessage.setText(R.string.login_re_login_error);
                btnLogin.setText(R.string.login_label_sign_in);
                EnableLogin(true);
                break;
            case 10:
                txtErrMessage.setText(R.string.login_network_error);
                btnLogin.setText(R.string.login_label_sign_in);
                EnableLogin(true);
                break;
            default:
                txtErrMessage.setText(R.string.login_error);
                btnLogin.setText(R.string.login_label_sign_in);
                EnableLogin(true);
                break;
        }

    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            //要做的事情
            txtErrMessage.setText(R.string.login_timeout);
            btnLogin.setText(R.string.login_label_sign_in);
            EnableLogin(true);
            NetProtocol.getInstance().Logout();
            //myHandler.postDelayed(this, 5000);
        }
    };
}