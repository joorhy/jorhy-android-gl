package com.xltech.client.service;

import com.xltech.client.config.Configer;
import com.xltech.client.config.ConfigTempData;
import com.xltech.client.data.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.HashMap;

/**
 * Created by JooLiu on 2016/1/11.
 */
public class NetProtocol {
    private final int BUFFER_LEN = (1024 * 1024);
    private static NetProtocol instance = null;

    private Socket netSocket = null;
    private OutputStream sendStream = null;

    private ReceiveThread receiveThread = null;
    private ByteBuffer receiveBuffer = null;
    private int receiveLen = 0;
    private boolean isStop = false;
    private int sequenceNum = 0;

    private Object loginLocker = new Object();
    private Object logoutLocker = new Object();
    private Object categoryLocker = new Object();
    private Object mapLocker = new Object();

    private HashMap<String, AppPlayer> playerMap = null;

    /// for test
    private Thread testThread = null;
    private boolean testIsRunning = false;

    public static NetProtocol getInstance() {
        if (instance == null) {
            instance = new NetProtocol();
        }

        return instance;
    }

    public NetProtocol() {
        receiveBuffer = ByteBuffer.allocate(BUFFER_LEN);
        playerMap = new  HashMap<String, AppPlayer>();
    }

    public int Login(String strAddress, int nPort, String strUsername, String strPassword, int nForced) {
        if (Configer.UseTemp()) {
            DataLogin.getInstance().setBody(ConfigTempData.getLoginData());
        } else {
            DataLogin.getInstance().setUsername(strUsername);
            DataLogin.getInstance().setPassword(strPassword);
            DataLogin.getInstance().setForced(nForced);
            byte[] sendData = DataProtocol.MakeRequest(EnumProtocol.xl_login, EnumProtocol.xl_ctrl_start, sequenceNum++,
                    DataLogin.getInstance().getBody(), DataLogin.getInstance().getBodyLen());

            try {
                if (netSocket == null) {
                    netSocket = new Socket(strAddress, nPort);
                    receiveThread = new ReceiveThread(netSocket);
                    receiveThread.start();
                }

                sendStream = netSocket.getOutputStream();
                sendStream.write(sendData);
                loginLocker.wait();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return 0;
    }

    public int Logout() {
        if (Configer.UseTemp()) {
        } else {
            byte[] sendData = DataProtocol.MakeRequest(EnumProtocol.xl_logout,
                    EnumProtocol.xl_ctrl_start, sequenceNum++, null, 0);
            try {
                sendStream.write(sendData);
                logoutLocker.wait(1000);
                if (receiveThread != null) {
                    isStop = true;
                    receiveThread.join();
                    receiveThread = null;
                }

                if (netSocket != null) {
                    netSocket.close();
                    netSocket = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return 0;
    }

    public int GetCategory() {
        if (Configer.UseTemp()) {
            DataCategory.getInstance().cleanElement();
            DataCategory.getInstance().cleanBody();
            DataCategory.getInstance().setBody(ConfigTempData.getCategoryData());
            DataCategory.getInstance().parse();
        } else {
            byte[] sendData = DataProtocol.MakeRequest(EnumProtocol.xl_category,
                    EnumProtocol.xl_ctrl_start, sequenceNum++, null, 0);
            try {
                sendStream.write(sendData);
                categoryLocker.wait();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return 0;
    }

    public String StartReal(AppPlayer player) {
        String strKey = null;
        if (Configer.UseTemp()) {
            if (player.GetFlag() == AppPlayer.LEFT_PALER) {
                strKey = String.valueOf(DataSelectedVehicle.getInstance().getLeftChannel());
            } else {
                strKey = String.valueOf(DataSelectedVehicle.getInstance().getRightChannel());
            }

            synchronized(mapLocker) {
                playerMap.put(strKey, player);
            }

            if (testThread == null) {
                testIsRunning = true;
                testThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (testIsRunning) {
                            StructBlock testFrameData = ConfigTempData.getInstance().getBlockData();
                            if (testFrameData != null) {
                                int channel = testFrameData.getChannel(StructBlock.VOD);
                                synchronized(mapLocker) {
                                    AppPlayer val = playerMap.get(String.valueOf(channel));
                                    if (val != null) {
                                        val.InputData(testFrameData.blockData(),
                                                testFrameData.blockSize());
                                    }
                                }
                            }
                        }
                    }
                });
                testThread.start();
            }
        } else {
            int nSeq = sequenceNum++;
            strKey = String.valueOf(nSeq);
            synchronized(mapLocker) {
                playerMap.put(strKey, player);
            }

            DataRealPlay realPlay = new DataRealPlay();
            realPlay.setStrHostID(DataSelectedVehicle.getInstance().getSelectedVehicleId());
            if (player.GetFlag() == AppPlayer.LEFT_PALER) {
                realPlay.setChannel(DataSelectedVehicle.getInstance().getLeftChannel());
            } else {
                realPlay.setChannel(DataSelectedVehicle.getInstance().getRightChannel());
            }
            byte[] sendData = DataProtocol.MakeRequest(EnumProtocol.xl_real_play,
                    EnumProtocol.xl_ctrl_start, nSeq, realPlay.getBody(), realPlay.getBodyLen());
            try {
                sendStream.write(sendData);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return strKey;
    }

    public int StopReal(AppPlayer player) {
        if (Configer.UseTemp()) {
            ConfigTempData.getInstance().resetXLFile();
            synchronized (mapLocker) {
                playerMap.remove(player.GetKey());
            }

            if (playerMap.isEmpty() && testIsRunning == true) {
                try {
                    testIsRunning = false;
                    testThread.join();
                    testThread = null;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else {
            synchronized (mapLocker) {
                playerMap.remove(player.GetKey());
            }

            DataRealPlay realPlay = new DataRealPlay();
            realPlay.setStrHostID(DataSelectedVehicle.getInstance().getSelectedVehicleId());
            if (player.GetFlag() == AppPlayer.LEFT_PALER) {
                realPlay.setChannel(DataSelectedVehicle.getInstance().getLeftChannel());
            } else {
                realPlay.setChannel(DataSelectedVehicle.getInstance().getRightChannel());
            }
            byte[] sendData = DataProtocol.MakeRequest(EnumProtocol.xl_real_play,
                    EnumProtocol.xl_ctrl_start, Integer.parseInt(player.GetKey()), realPlay.getBody(), realPlay.getBodyLen());
            try {
                sendStream.write(sendData);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    private void processData() {
        DataProtocol parser = new DataProtocol(receiveBuffer, receiveLen);
        if (!parser.isCorrect()) {
            return;
        }

        switch (parser.getCommand()) {
            case EnumProtocol.xl_login:
                DataLogin.getInstance().setBody(parser.getBody());
                loginLocker.notify();
                break;
            case EnumProtocol.xl_logout:
                logoutLocker.notify();
                break;
            case EnumProtocol.xl_category:
                if (parser.getFlag() == EnumProtocol.xl_ctrl_data) {
                    DataCategory.getInstance().setBody(parser.getBody());
                } else if (parser.getFlag() == EnumProtocol.xl_ctrl_end) {
                    DataCategory.getInstance().parse();
                    categoryLocker.notify();
                }
                break;
            case EnumProtocol.xl_real_play:
                String strKey = String.valueOf(parser.getSeq());
                AppPlayer player = playerMap.get(strKey);
                if (player != null) {
                    player.InputData(parser.getBody(), parser.getBodyLen());
                }
                break;
        }
    }

    private class ReceiveThread extends Thread {
        static final int READ_HEAD_STATUS = 0;
        static final int READ_BODY_STATUS = 1;

        private InputStream receiveStream = null;
        private int readStatus = READ_HEAD_STATUS;

        public ReceiveThread(Socket sock) {
            try {
                receiveStream = sock.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            while (!isStop) {
                if (readStatus == READ_HEAD_STATUS) {
                    try {
                        receiveStream.read(receiveBuffer.array(), 0, EnumProtocol.HEADER_LEN);
                        receiveLen += EnumProtocol.HEADER_LEN;
                        readStatus = READ_BODY_STATUS;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    DataProtocol parser = new DataProtocol(receiveBuffer, EnumProtocol.HEADER_LEN);
                    try {
                        receiveStream.read(receiveBuffer.array(), EnumProtocol.HEADER_LEN,
                                parser.getBodyLen() + EnumProtocol.TAIL_LEN);
                        receiveLen += parser.getBodyLen() + EnumProtocol.TAIL_LEN;
                        readStatus = READ_HEAD_STATUS;

                        processData();
                        receiveLen = 0;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
