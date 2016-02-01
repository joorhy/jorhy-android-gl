package com.xltech.client.data;

import java.nio.ByteBuffer;

/**
 * Created by JooLiu on 2016/1/11.
 */
public class DataProtocol {
    private ByteBuffer dataBuffer = null;
    public DataProtocol(ByteBuffer buffer, int len) {
        dataBuffer = ByteBuffer.wrap(buffer.array(), 0, len);
    }

    static public byte[] MakeRequest(byte cmd, byte flag, int nSeq, byte[] data, int len) {
        int nLen = 12 + len + 2;
        ByteBuffer result = ByteBuffer.allocate(nLen);
        /// 开始码
        result.put((byte)0xFF);
        /// 协议类型
        result.put(EnumProtocol.xl_frame_request);
        /// 序列号
        result.putInt(nSeq);
        /// 媒体数据类型
        result.put(flag);
        /// 指令类型
        result.put(cmd);
        /// 数据长度
        result.putInt(len);

        if (data != null) {
            /// 数据拷贝
            result.put(data);
        }
        /// 校验码
        result.put(CheckNum(data));
        /// 结束码
        result.put((byte)0xFE);

        return result.array();
    }

    static public byte CheckNum(byte[] data) {
        long nCheckNum = 0xFE;
        if (data != null) {
            for (int i=0; i<data.length; i++) {
                nCheckNum += data[i];
            }
        }

        return (byte)(nCheckNum % 256);
    }

    public boolean isCorrect() {
        return true;
    }

    public byte getCommand() {
        return dataBuffer.get(7);
    }

    public byte getFlag() {
        return dataBuffer.get(6);
    }

    public int getSeq() {
        return dataBuffer.getInt(2);
    }

    public int getBodyLen() {
        return dataBuffer.getInt(8);
    }

    public byte[] getBody() {
        ByteBuffer result  = ByteBuffer.wrap(dataBuffer.array(), 13, getBodyLen());
        return result.array();
    }
}
