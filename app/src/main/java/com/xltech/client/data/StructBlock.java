package com.xltech.client.data;

import com.xltech.client.config.Configer;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by JooLiu on 2016/1/29.
 */
public class StructBlock {
    public static final String REAL = "real";
    public static final String VOD = "vod";

    /**
     * unsigned char szID[32];					//设备ID
     * unsigned char bChannelID;				//通道号
     * //unsigned char szVehicleNum[32];        //车号
     * time_t tagTimeStamp;					    //时间戳
     * double dLatitude;						//维度
     * double dLongtude;						//经度
     * double dGPSSpeed;						//GPS速度
     * double dSpeed;							//车速
     * unsigned char bAlarm;					//报警值
     * unsigned long ulVideoSize;				//视频帧长度
     * unsigned char frameType;				    //帧类型  */
    public static int HEAD_SIZE = 79;
    private static int DATA_SIZE = (1024 * 1024);
    private ByteBuffer blockHeader = null;
    private ByteBuffer blockData = null;
    private ByteBuffer fileBlock = null;
    private StructProgramStreamHeader programStreamHeader = null;
    private StructH264Frame h264Frame = null;

    public StructBlock() {
        blockHeader = ByteBuffer.allocate(HEAD_SIZE);
        blockData = ByteBuffer.allocate(DATA_SIZE);
        fileBlock = ByteBuffer.allocate(DATA_SIZE);

        programStreamHeader = new StructProgramStreamHeader();
        h264Frame = new StructH264Frame();
    }

    public StructH264Frame getH264Frame() {
        if (h264Frame.isComplete()) {
            return h264Frame;
        }
        return null;
    }

    public int getChannel(String strFlag) {
        if (strFlag == VOD) {
            return (fileBlock.get(32) & 0xFF);
        }
        return (blockHeader.get(32) & 0xFF);
    }

    public int getLength(String strFlag) {
        if (strFlag == VOD) {
            fileBlock.order(ByteOrder.LITTLE_ENDIAN);
            return fileBlock.getInt(74);
        }
        blockHeader.order(ByteOrder.LITTLE_ENDIAN);
        return blockHeader.getInt(74);
    }

    public boolean readBlock(DataInputStream in) {
        try {
            fileBlock.clear();
            int readLen = in.read(fileBlock.array(), 0, HEAD_SIZE);
            if (readLen != HEAD_SIZE) {
                return false;
            }

            int dataLength = getLength(VOD);
            readLen = in.read(fileBlock.array(), HEAD_SIZE, dataLength);
            if (readLen != dataLength) {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public int blockSize() {
        return (getLength(VOD) + HEAD_SIZE);
    }

    public byte[] blockData() {
        return fileBlock.array();
    }

    public boolean ParseData(byte[] data, int len) {
        blockHeader.clear();
        blockData.clear();
        blockHeader.put(data, 0, HEAD_SIZE);
        blockData.put(data, HEAD_SIZE, len - HEAD_SIZE);

        int dataLength = getLength(REAL);
        blockData.clear();
        while (blockData.position() < dataLength) {
            programStreamHeader.clear();
            if (!programStreamHeader.getID(blockData)) {
                return false;
            } else {
                if (programStreamHeader.getType() == StructProgramStreamHeader.TYPE_PACK
                        || programStreamHeader.getType() == StructProgramStreamHeader.TYPE_PSM) {
                    if (!programStreamHeader.getPack(blockData)) {
                        return false;
                    }
                } else if (programStreamHeader.getType() == StructProgramStreamHeader.TYPE_VIDEO
                        || programStreamHeader.getType() == StructProgramStreamHeader.TYPE_AUDIO) {
                    if (!programStreamHeader.getPack(blockData)) {
                        return false;
                    }

                    if (programStreamHeader.getType() == StructProgramStreamHeader.TYPE_VIDEO) {
                        h264Frame.setLength(programStreamHeader.getDataLength());
                        h264Frame.setType(programStreamHeader.getDataType());
                        if (!h264Frame.getFrame(blockData)) {
                            return false;
                        }
                    } else {
                        break;
                    }
                }
            }
        }
        return true;
    }
}
