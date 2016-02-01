package com.xltech.client.data;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by JooLiu on 2016/1/29.
 */
public class StructProgramStreamHeader {
    public static int MAX_LENGTH = 512;
    public static int ID_LENGTH = 4;
    public static int PACK_LENGTH_OFFSET = 10;
    public static int PSM_LENGTH_OFFSET = 2;
    public static int AV_LENGTH_OFFSET = 5;

    public static final int TYPE_UNKNOWN = 0;
    public static final int TYPE_PACK = 1;
    public static final int TYPE_PSM = 2;
    public static final int TYPE_AUDIO = 3;
    public static final int TYPE_VIDEO = 4;
    private static byte[] ID = new byte[]{ 0x00, 0x00, 0x01};

    private ByteBuffer packHeader = null;
    private int arrayOffset = 0;
    public StructProgramStreamHeader() {
        packHeader = ByteBuffer.allocate(MAX_LENGTH);
    }

    public void clear() {
        packHeader.clear();
        arrayOffset = 0;
    }

    public int getType() {
        byte[] array = packHeader.array();
        for (int i=0; i<3; i++) {
            if (array[i] != ID[i]) {
                return TYPE_UNKNOWN;
            }
        }

        int type = TYPE_UNKNOWN;
        switch(array[3]) {
            case (byte)0xba:
                type = TYPE_PACK; break;
            case (byte)0xbc:
                type = TYPE_PSM; break;
            case (byte)0xc0:
                type = TYPE_AUDIO; break;
            case (byte)0xe0:
                type = TYPE_VIDEO; break;
        }
        return type;
    }

    public int getLength() {
        int length = 0;
        switch (getType()) {
            case TYPE_PACK:
                length = packHeader.array()[13] & 0x07;
                break;
            case TYPE_PSM:
                length = ((packHeader.array()[4] & 0xFF) << 8) + (packHeader.array()[5] & 0xFF);
                break;
            case TYPE_AUDIO:case TYPE_VIDEO:
                length = packHeader.array()[8] & 0xFF;
                break;
        }

        return length;
    }

    public int getDataLength() {
        int length = 0;
        int type = getType();
        if (type == TYPE_AUDIO || type == TYPE_VIDEO) {
            length = ((packHeader.array()[4] & 0xFF) << 8)
                    + (packHeader.array()[5] & 0xFF) - 3 - getLength();
        }

        return length;
    }

    public boolean readID(DataInputStream in) {
        try {
            int readLen = in.read(packHeader.array(), arrayOffset, ID_LENGTH);
            if (readLen != ID_LENGTH) {
                return false;
            } else {
                arrayOffset += ID_LENGTH;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean getID(ByteBuffer buffer) {
        buffer.get(packHeader.array(), arrayOffset, ID_LENGTH);
        arrayOffset += ID_LENGTH;
        return true;
    }

    public boolean readPack(DataInputStream in) {
        int length = 0;
        switch (getType()) {
            case TYPE_PACK:
                length = PACK_LENGTH_OFFSET;
                break;
            case TYPE_PSM:
                length = PSM_LENGTH_OFFSET;
                break;
            case TYPE_AUDIO:case TYPE_VIDEO:
                length = AV_LENGTH_OFFSET;
                break;
        }

        try {
            int readLen = in.read(packHeader.array(), arrayOffset, length);
            if (readLen != length) {
                return false;
            } else {
                arrayOffset += length;
            }

            length = getLength();
            readLen = in.read(packHeader.array(), arrayOffset, length);
            if (readLen != length) {
                return false;
            } else {
                arrayOffset += length;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    public boolean getPack(ByteBuffer buffer) {
        int length = 0;
        switch (getType()) {
            case TYPE_PACK:
                length = PACK_LENGTH_OFFSET;
                break;
            case TYPE_PSM:
                length = PSM_LENGTH_OFFSET;
                break;
            case TYPE_AUDIO:case TYPE_VIDEO:
                length = AV_LENGTH_OFFSET;
                break;
        }

        buffer.get(packHeader.array(), arrayOffset, length);
        arrayOffset += length;

        length = getLength();
        buffer.get(packHeader.array(), arrayOffset, length);
        arrayOffset += length;

        return true;
    }

    public int getDataType() {
        int index = ID_LENGTH + AV_LENGTH_OFFSET + getLength() - 1;
        return (packHeader.array()[index] & 0xFF);
    }
}
