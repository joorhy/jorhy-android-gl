package com.xltech.client.data;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by JooLiu on 2016/1/11.
 */
public class DataLogin {
    private final int LOGIN_LEN = 40;            ///< 16 + 16 + 4 + 4
    private final int PASSWORD_OFFSET = 16;
    private final int FLAG_OFFSET = 32;
    private final int VERSION_OFFSET = 36;
    private static DataLogin instance = null;

    private String strUsername = null;           ///< 账户名
    private String strPassword = null;           ///< 密码
    private int nForced = 0;                     ///< 1 强制登录；0 非强制登录
    private int nVersion = 0;			         ///< 客户端版本

    private int nResult = -1;                    ///< 返回值

    public static DataLogin getInstance() {
        if (instance == null) {
            instance = new DataLogin();
        }

        return instance;
    }

    public void setUsername(String strUsername) {
        this.strUsername = strUsername;
    }

    public void setPassword(String strPassword) {
        this.strPassword = strPassword;
    }

    public void setForced(int nForced) {
        this.nForced = nForced;
    }

    public int getResult() {
        return this.nResult;
    }

    public void setResult(int nResult) {
        this.nResult = nResult;
    }

    public int getBodyLen() {
        return LOGIN_LEN;
    }

    public byte[] getBody() {
        ByteBuffer result = ByteBuffer.allocate(LOGIN_LEN);
        result.order(ByteOrder.LITTLE_ENDIAN);

        result.put(strUsername.getBytes());
        result.position(PASSWORD_OFFSET);

        result.put(strPassword.getBytes());
        result.position(FLAG_OFFSET);

        result.putInt(nForced);
        result.position(VERSION_OFFSET);

        result.putInt(nVersion);

        return result.array();
    }

    public void setBody(byte[] body) {
        ByteBuffer result = ByteBuffer.wrap(body);
        result.order(ByteOrder.LITTLE_ENDIAN);
        nResult = result.getInt();
    }
}
