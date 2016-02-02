package com.xltech.client.data;

/**
 * Created by JooLiu on 2016/2/2.
 */
public class DataServerInfo {
    private static DataServerInfo instance = null;
    private String address = null;
    private int port = 8502;

    static public DataServerInfo getInstance() {
        if (instance == null) {
            instance = new DataServerInfo();
        }
        return instance;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }
}
