package com.xltech.client.service;


import com.xltech.client.data.StructRecordFrame;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by JooLiu on 2016/10/28.
 */
public class PrevRecorder {
    private ArrayBlockingQueue<StructRecordFrame> lstRecordFrames;
    public PrevRecorder() {
        lstRecordFrames = new ArrayBlockingQueue<StructRecordFrame>(250);
    }

    public void InitializeRecorder() {

    }

    public void DeInitializeRecorder() {

    }

    public void InputData(byte[] frameData, int frameLen, int frameType) {
        StructRecordFrame frame = new StructRecordFrame();
        frame.setLength(frameLen);
        frame.setFrameType(frameType);
        frame.copyData(frameData);

        if (!lstRecordFrames.offer(frame)) {
            StructRecordFrame first = lstRecordFrames.poll();
            if (first != null) {
                first.destroy();
            }
            lstRecordFrames.offer(frame);
        }
    }

    public void save() {
        
    }
}
