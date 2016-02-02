package com.xltech.client.data;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by JooLiu on 2016/1/11.
 */
public class DataProtocol {
    private ByteBuffer headerBuffer = null;

    static public ByteBuffer MakeRequest(byte cmd, byte flag, int nSeq, byte[] data, int len) {
        int nLen = EnumProtocol.HEADER_LEN + len + EnumProtocol.TAIL_LEN;
        ByteBuffer result = ByteBuffer.allocate(nLen);
        result.order(ByteOrder.LITTLE_ENDIAN);
        /// 开始码
        result.put((byte) 0xFF);
        /// 协议类型
        result.put(EnumProtocol.xl_frame_request);
        /// 序列号
        result.putInt(nSeq);
        /// 指令类型
        result.put(cmd);
        /// 状态码 0x0表示开始, 0x1传输中, 0x2传输完成
        result.put(flag);
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

        result.flip();
        return result;
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

    public DataProtocol() {
        headerBuffer = ByteBuffer.allocate(EnumProtocol.HEADER_LEN);
    }

    public ByteBuffer getHeaderBuffer() {
        return headerBuffer;
    }

    public boolean isCorrect() {
        return true;
    }

    public byte getCommand() {
        return headerBuffer.get(EnumProtocol.COMMAND_OFFSET);
    }

    public byte getFlag() {
        return headerBuffer.get(EnumProtocol.FLAG_OFFSET);
    }

    public int getSequence() {
        headerBuffer.order(ByteOrder.LITTLE_ENDIAN);
        return headerBuffer.getInt(EnumProtocol.SEQUENCE_OFFSET);
    }

    public int getBodyLen() {
        headerBuffer.order(ByteOrder.LITTLE_ENDIAN);
        return headerBuffer.getInt(EnumProtocol.BODY_OFFSET);
    }
}
