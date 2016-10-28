package com.xltech.client.data;

import java.nio.ByteBuffer;

/**
 * Created by JooLiu on 2016/10/28.
 */
public class StructRecordFrame {
    private int frameType = 0;
    private int length = 0;
    private ByteBuffer frame = null;

    public void setLength(int len) {
        length = len;
    }

    public int getLength() {
        return length;
    }

    public void setFrameType(int type) {
        frameType = type;
    }

    public int getFrameType() {
        return frameType;
    }

    public void copyData(byte[] data) {
        if (frame == null) {
            frame = ByteBuffer.allocate(length);
        }
        frame.put(data);
    }

    public void destroy() {
        frame.clear();
        frame = null;
    }
}
