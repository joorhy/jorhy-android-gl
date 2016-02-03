package com.xltech.client.data;

import com.xltech.client.service.AppPlayer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by JooLiu on 2016/2/1.
 */
public class DataRealPlay {
    private final int REAL_PLAY_LEN = 40;
    private final int CHANNEL_OFFSET = 32;
    private String strHostID;		            ///< 设备ID
    private long  channel;		                ///< 通道号
    private int sequence;
    private byte flag;
    private AppPlayer player = null;

    public void setStrHostID() {
        this.strHostID = DataSelectedVehicle.getInstance().getSelectedVehicleId();
    }

    public void setChannel () {
        if (player.GetFlag() == AppPlayer.LEFT_PALER) {
            channel = DataSelectedVehicle.getInstance().getLeftChannel();
        } else {
            channel = DataSelectedVehicle.getInstance().getRightChannel();
        }
    }

    public void setPlayer (AppPlayer player) {
        this.player = player;
    }

    public AppPlayer getPlayer() {
        return player;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public int getSequence() {
        return sequence;
    }

    public void setFlag(byte flag) {
        this.flag = flag;
    }

    public byte getFlag() {
        return flag;
    }

    public int getBodyLen() {
        return REAL_PLAY_LEN;
    }

    public byte[] getBody() {
        int nBodyLen = REAL_PLAY_LEN;
        ByteBuffer result = ByteBuffer.allocate(nBodyLen);
        result.order(ByteOrder.LITTLE_ENDIAN);

        result.put(strHostID.getBytes());
        result.position(CHANNEL_OFFSET);
        result.putLong(channel);

        return result.array();
    }
}

