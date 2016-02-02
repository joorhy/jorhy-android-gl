package com.xltech.client.service;

import android.app.Activity;
import android.provider.ContactsContract;

import com.xltech.client.config.Configer;
import com.xltech.client.config.ConfigTempData;
import com.xltech.client.data.*;
import com.xltech.client.ui.ActivityLogin;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * Created by JooLiu on 2016/1/11.
 */
public class NetProtocol {
    private static NetProtocol instance = null;
    private Queue<Object> taskQueue = null;
    private HashMap<String, Object> playerMap = null;

    private boolean isStop = false;
    private int sequenceNum = 0;
    private Thread workThread = null;

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
        playerMap = new HashMap<String, Object>();
        taskQueue = new LinkedList<Object>();
    }

    public int Login(String strAddress, int nPort, String strUsername, String strPassword, int nForced) {
        if (Configer.UseTemp()) {
            DataLogin.getInstance().setBody(ConfigTempData.getLoginData());
            Activity activity = ManActivitys.getInstance().currentActivity();
            if (activity.getClass() == ActivityLogin.class) {
                ((ActivityLogin)activity).OnLoginReturn();
            }
        } else {
            DataServerInfo.getInstance().setAddress(strAddress);
            DataServerInfo.getInstance().setPort(nPort);

            DataLogin.getInstance().setUsername(strUsername);
            DataLogin.getInstance().setPassword(strPassword);
            DataLogin.getInstance().setForced(nForced);

            synchronized(taskQueue) {
                taskQueue.offer(DataLogin.getInstance());
            }

            if (workThread == null) {
                isStop = false;
                workThread = new WorkThread();
                workThread.start();
            }
        }
        return 0;
    }

    public int Logout() {
        if (Configer.UseTemp()) {
        } else {
            DataNull logout = new DataNull();
            logout.setCommand(EnumProtocol.xl_logout);
            synchronized (taskQueue) {
                taskQueue.offer(logout);
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
            DataNull category = new DataNull();
            category.setCommand(EnumProtocol.xl_category);
            synchronized (taskQueue) {
                taskQueue.offer(category);
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

            synchronized(playerMap) {
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
                                synchronized(playerMap) {
                                    AppPlayer val = (AppPlayer)playerMap.get(String.valueOf(channel));
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
            int sequence = sequenceNum++;
            DataRealPlay realPlay = new DataRealPlay();
            realPlay.setPlayer(player);
            realPlay.setStrHostID();
            realPlay.setChannel();
            realPlay.setSequence(sequence);
            realPlay.setFlag(EnumProtocol.xl_ctrl_start);

            synchronized(playerMap) {
                playerMap.put(String.valueOf(sequence), realPlay);
            }

            synchronized (taskQueue) {
                taskQueue.offer(realPlay);
            }
        }
        return strKey;
    }

    public int StopReal(AppPlayer player) {
        if (Configer.UseTemp()) {
            ConfigTempData.getInstance().resetXLFile();
            synchronized (playerMap) {
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
            DataRealPlay realPlay;
            synchronized (playerMap) {
                realPlay = (DataRealPlay)playerMap.get(player.GetKey());
            }

            if (realPlay != null) {
                realPlay.setFlag(EnumProtocol.xl_ctrl_stop);
                synchronized (taskQueue) {
                    taskQueue.offer(realPlay);
                }
            }
        }
        return 0;
    }

    private void processData(DataProtocol dataProtocol, ByteBuffer bodyBuffer) {
        if (!dataProtocol.isCorrect()) {
            return;
        }

        switch (dataProtocol.getCommand()) {
            case EnumProtocol.xl_login:
                DataLogin.getInstance().setBody(bodyBuffer.array());
                Activity activity = ManActivitys.getInstance().currentActivity();
                if (activity.getClass() == ActivityLogin.class) {
                    ((ActivityLogin)activity).OnLoginReturn();
                }
                break;
            case EnumProtocol.xl_logout:
                try {
                    isStop = true;
                    if (workThread != null) {
                        workThread.join();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    workThread = null;
                }
                break;
            case EnumProtocol.xl_category:
                if (dataProtocol.getFlag() == EnumProtocol.xl_ctrl_data) {
                    DataCategory.getInstance().setBody(bodyBuffer.array());
                } else if (dataProtocol.getFlag() == EnumProtocol.xl_ctrl_end) {
                    DataCategory.getInstance().parse();
                }
                break;
            case EnumProtocol.xl_real_play:
                String strKey = String.valueOf(dataProtocol.getSequence());
                AppPlayer player = ((DataRealPlay)playerMap.get(strKey)).getPlayer();
                if (player != null) {
                    player.InputData(bodyBuffer.array(), dataProtocol.getBodyLen());
                }
                break;
        }
    }

    private class WorkThread extends Thread {
        static final int READ_HEAD_STATUS = 0;
        static final int READ_BODY_STATUS = 1;

        private int readStatus = READ_HEAD_STATUS;

        @Override
        public void run() {
            try {
                DataProtocol dataProtocol = new DataProtocol();
                Selector selector = Selector.open();
                SocketChannel socketChannel = SocketChannel.open();
                socketChannel.connect(
                        new InetSocketAddress(DataServerInfo.getInstance().getAddress(),
                                DataServerInfo.getInstance().getPort()));
                socketChannel.configureBlocking(false);
                SelectionKey key = socketChannel.register(selector,
                        SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                while (!isStop) {
                    int nReadyChannels = selector.select();
                    if (nReadyChannels == 0) {
                        continue;
                    }

                    if (key.isReadable()) {
                        if (readStatus == READ_HEAD_STATUS) {
                            while (dataProtocol.getHeaderBuffer().hasRemaining()) {
                                socketChannel.read(dataProtocol.getHeaderBuffer());
                            }
                            readStatus = READ_BODY_STATUS;
                        } else {
                            ByteBuffer bodyBuffer = ByteBuffer.allocate(dataProtocol.getBodyLen() +
                                    EnumProtocol.TAIL_LEN);
                            while (bodyBuffer.hasRemaining()) {
                                socketChannel.read(bodyBuffer);
                            }
                            readStatus = READ_HEAD_STATUS;

                            processData(dataProtocol, bodyBuffer);
                            dataProtocol.getHeaderBuffer().clear();
                        }
                    } else if (key.isWritable()) {
                        Object task;
                        synchronized (taskQueue) {
                            if (taskQueue.size() == 0) {
                                continue;
                            }
                            task = taskQueue.poll();
                        }

                        if (task != null) {
                            byte[] sendData = null;
                            if (task.getClass() == DataLogin.class) {
                                sendData = DataProtocol.MakeRequest(EnumProtocol.xl_login,
                                        EnumProtocol.xl_ctrl_start, sequenceNum++,
                                        DataLogin.getInstance().getBody(),
                                        DataLogin.getInstance().getBodyLen());
                            } else if (task.getClass() == DataNull.class) {
                                DataNull dataNull = (DataNull)task;
                                sendData = DataProtocol.MakeRequest(dataNull.getCommand(),
                                        EnumProtocol.xl_ctrl_start, sequenceNum++, null, 0);
                            } else if (task.getClass() == DataRealPlay.class) {
                                DataRealPlay realPlay = (DataRealPlay)task;
                                sendData = DataProtocol.MakeRequest(EnumProtocol.xl_real_play,
                                        realPlay.getFlag(), realPlay.getSequence(),
                                        realPlay.getBody(), realPlay.getBodyLen());
                            }

                            if (sendData != null) {
                                socketChannel.write(ByteBuffer.wrap(sendData));
                            }
                        }
                    }
                }
            } catch(IOException e){
                e.printStackTrace();
            } finally {
                isStop = true;
                workThread = null;
            }
        }
    }
}
