package com.xltech.client.config;

import com.xltech.client.data.StructBlock;
import com.xltech.client.data.StructFileHeader;
import com.xltech.client.data.StructH264Frame;
import com.xltech.client.data.StructProgramStreamHeader;
import com.xltech.client.service.AppPlayer;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by JooLiu on 2016/1/13.
 */
public class ConfigTempData {
    private static ConfigTempData instance = null;
    private static String H264FileName = "/mnt/sdcard/test2.ps";
    private static String H264FileName2 = "/mnt/sdcard/test.ps";
    private static String XLFileName = "/mnt/sdcard/GZ_V_00014011_160116135705_0300_0.vdk";

    private File mFile = null;
    private DataInputStream mDataStream = null;

    private File mFile2 = null;
    private DataInputStream mDataStream2 = null;

    private File mXLFile = null;
    private DataInputStream mXLDataStream = null;

    public static ConfigTempData getInstance() {
        if (instance == null) {
            instance = new ConfigTempData();
        }

        return instance;
    }

    public ConfigTempData() {
        if (Configer.UseTemp()) {
            resetFile();
            resetFile2();
            resetXLFile();
        }
    }
    public static byte[] getLoginData() {
        byte[] tempBody = new byte[4];
        tempBody[0] = 0x00;
        tempBody[1] = 0x00;
        tempBody[2] = 0x00;
        tempBody[3] = 0x00;

        return tempBody;
    }

    public static byte[] getCategoryData() {
        String strTempCategory = "{\"id\":\"1\",\"name\":\"成都铁路局\"," +
                "\"items\":[" +
                "{\"id\":\"11\",\"name\":\"列车1\",\"items\":[" +
                "{\"id\":\"111\",\"name\":\"通道1\",\"items\":[]}," +
                "{\"id\":\"112\",\"name\":\"通道2\",\"items\":[]}," +
                "{\"id\":\"113\",\"name\":\"通道3\",\"items\":[]}," +
                "{\"id\":\"114\",\"name\":\"通道4\",\"items\":[]}," +
                "{\"id\":\"115\",\"name\":\"通道5\",\"items\":[]}," +
                "{\"id\":\"116\",\"name\":\"通道6\",\"items\":[]}]}," +
                "{\"id\":\"12\",\"name\":\"列车2\"," +
                "\"items\":[]}," +
                "{\"id\":\"13\",\"name\":\"列车3\",\"items\":[]}]}";

        byte[] tempCategory = null;
        try {
            tempCategory = strTempCategory.getBytes("GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return tempCategory;
    }

    public StructH264Frame getH264Frame(String strFlag) {
        if (strFlag == AppPlayer.RIGHT_PLAYER) {
            return getFileData2();
        }

        return getFileData();
    }

    public StructBlock getBlockData() {
        StructBlock blockHeader = new StructBlock();
        if (!blockHeader.readBlock(mXLDataStream)) {
            resetXLFile();
            return null;
        }
        return blockHeader;
    }

    private void resetFile() {
        mFile = new File(H264FileName);
        try {
            mDataStream = new DataInputStream(new FileInputStream(mFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void resetFile2() {
        mFile2 = new File(H264FileName2);
        try {
            mDataStream2 = new DataInputStream(new FileInputStream(mFile2));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void resetXLFile() {
        mXLFile = new File(XLFileName);
        try {
            mXLDataStream = new DataInputStream(new FileInputStream(mXLFile));
            try {
                mXLDataStream.skipBytes(StructFileHeader.SIZE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private StructH264Frame getFileData() {
        int readLen;
        StructProgramStreamHeader programStreamHeader = new StructProgramStreamHeader();
        StructH264Frame h264Header = new StructH264Frame();
        while (true) {
            try {
                programStreamHeader.clear();
                if (!programStreamHeader.readID(mDataStream)) {
                    resetFile();
                    return null;
                } else {
                    if (programStreamHeader.getType() == StructProgramStreamHeader.TYPE_PACK
                            || programStreamHeader.getType() == StructProgramStreamHeader.TYPE_PSM) {
                        if (!programStreamHeader.readPack(mDataStream)) {
                            resetFile();
                            return null;
                        }
                    } else if (programStreamHeader.getType() == StructProgramStreamHeader.TYPE_VIDEO
                            || programStreamHeader.getType() == StructProgramStreamHeader.TYPE_AUDIO) {
                        if (!programStreamHeader.readPack(mDataStream)) {
                            resetFile();
                            return null;
                        }

                        int data_len = programStreamHeader.getDataLength();
                        if (programStreamHeader.getType() == StructProgramStreamHeader.TYPE_AUDIO) {
                            readLen = mDataStream.skipBytes(data_len);
                            if (readLen != data_len) {
                                resetFile();
                                return null;
                            }
                        } else {
                            h264Header.setLength(programStreamHeader.getDataLength());
                            h264Header.setType(programStreamHeader.getDataType());
                            if (!h264Header.readFrame(mDataStream)) {
                                resetFile();
                                return null;
                            }

                            if (h264Header.isComplete()) {
                                return h264Header;
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private StructH264Frame getFileData2() {
        int readLen;
        StructProgramStreamHeader programStreamHeader = new StructProgramStreamHeader();
        StructH264Frame h264Header = new StructH264Frame();
        while (true) {
            try {
                programStreamHeader.clear();
                if (!programStreamHeader.readID(mDataStream2)) {
                    resetFile2();
                    return null;
                } else {
                    if (programStreamHeader.getType() == StructProgramStreamHeader.TYPE_PACK
                            || programStreamHeader.getType() == StructProgramStreamHeader.TYPE_PSM) {
                        if (!programStreamHeader.readPack(mDataStream2)) {
                            resetFile2();
                            return null;
                        }
                    } else if (programStreamHeader.getType() == StructProgramStreamHeader.TYPE_VIDEO
                            || programStreamHeader.getType() == StructProgramStreamHeader.TYPE_AUDIO) {
                        if (!programStreamHeader.readPack(mDataStream2)) {
                            resetFile2();
                            return null;
                        }

                        int data_len = programStreamHeader.getDataLength();
                        if (programStreamHeader.getType() == StructProgramStreamHeader.TYPE_AUDIO) {
                            readLen = mDataStream2.skipBytes(data_len);
                            if (readLen != data_len) {
                                resetFile2();
                                return null;
                            }
                        } else {
                            h264Header.setLength(programStreamHeader.getDataLength());
                            h264Header.setType(programStreamHeader.getDataType());
                            if (!h264Header.readFrame(mDataStream2)) {
                                resetFile2();
                                return null;
                            }

                            if (h264Header.isComplete()) {
                                return h264Header;
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getVehicleId() {
        return "12345";
    }

    public int getTotalChannels() {
        return 9;
    }
}
