package com.xltech.client.data;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by JooLiu on 2016/1/29.
 */
public class StructH264Frame {
    public static int MAX_LENGTH = (1024 * 1024);
    public static int ID_LENGTH = 5;

    public static int TYPE_UNKNOWN = 0;
    public static int TYPE_PPS = 1;
    public static int TYPE_SPS = 2;
    public static int TYPE_I_FRAME = 3;
    public static int TYPE_P_FRAME = 4;

    public static int PACK_PPS_SPS = 0xfc;
    public static int PACK_COMPLETE = 0xf8;
    public static int PACK_START = 0xfd;
    public static int PACK_CONTINE = 0xff;
    public static int PCAK_END = 0xfa;

    private static byte[] ID = new byte[]{ 0x00, 0x00, 0x00, 0x01 };

    private ByteBuffer packHeader = null;
    private int packLength = 0;
    private int packType = 0;
    private int frameLength = 0;
    private boolean bComplete = false;

    public StructH264Frame() {
        packHeader = ByteBuffer.allocate(MAX_LENGTH);
    }

    public void clear() {
        bComplete = false;
        frameLength = 0;
        packHeader.clear();
    }

    public boolean isComplete() {
        return bComplete;
    }

    public void setLength(int length) {
        packLength = length;
    }

    public void setType(int type) {
        packType = type;
    }

    public byte[] getFrame() {
        return packHeader.array();
    }

    public int getFrameLength() {
        return frameLength;
    }

    public int getType() {
        byte[] array = packHeader.array();
        for (int i=0; i<4; i++) {
            if (array[i] != ID[i]) {
                return TYPE_UNKNOWN;
            }
        }

        int type = TYPE_UNKNOWN;
        switch(array[4]) {
            case 0x67:
                type = TYPE_PPS; break;
            case 0x68:
                type = TYPE_SPS; break;
            case 0x65:
                type = TYPE_I_FRAME; break;
            case 0x61: case 0x41: case 0x21:
                type = TYPE_P_FRAME; break;
        }
        return type;
    }

    public boolean readFrame(DataInputStream in) {
        try {
            int readLen = in.read(packHeader.array(),frameLength, packLength);
            if (readLen != packLength) {
                return false;
            } else {
                frameLength += packLength;
            }

            if (packType == PACK_COMPLETE || packType == PCAK_END) {
                bComplete = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean getFrame(ByteBuffer buffer) {
        buffer.get(packHeader.array(), frameLength, packLength);
        frameLength += packLength;

        if (packType == PACK_COMPLETE || packType == PCAK_END) {
            bComplete = true;
        }

        if (getType() == TYPE_PPS || getType() == TYPE_SPS) {
            System.out.print("");
        }
        return true;
    }
}
