package com.xltech.client.data;

import java.nio.ByteBuffer;

/**
 * Created by JooLiu on 2016/2/1.
 */
public class DataRealPlay {
    private final int REAL_PLAY_LEN = 40;
    private String strHostID;		            ///< 设备ID
    private long  channel;		                ///< 通道号

    public void setStrHostID(String strHostID) {
        this.strHostID = strHostID;
    }

    public void setChannel (long channel) {
        this.channel = channel;
    }

    public int getBodyLen() {
        return REAL_PLAY_LEN;
    }

    public byte[] getBody() {
        int nBodyLen = 32 + 8;
        ByteBuffer result = ByteBuffer.allocate(nBodyLen);

        result.put(strHostID.getBytes());
        result.position(33);
        result.putLong(channel);

        return result.array();
    }
}

