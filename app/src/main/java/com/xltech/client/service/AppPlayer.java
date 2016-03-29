package com.xltech.client.service;

import android.opengl.GLSurfaceView;

import com.xltech.client.data.DataSelectedVehicle;
import com.xltech.client.data.StructBlock;
import com.xltech.client.data.StructH264Frame;

/**
 * Created by JooLiu on 2016/1/13.
 */
public class AppPlayer {
    public static final String LEFT_PALER = "left_player";
    public static final String RIGHT_PLAYER = "right_player";

    private GLFrameRender m_GLFRenderer;
    private H264Decoder m_decoder = null;

    private String m_strKey = null;
    private String m_strFlag = null;

    private StructBlock block = null;
    private boolean isPlay = false;

    public AppPlayer(GLSurfaceView surface, String strFlag) {
        m_strFlag = strFlag;

        surface.setEGLContextClientVersion(2);
        m_GLFRenderer = new GLFrameRender(surface, strFlag);
        surface.setRenderer(m_GLFRenderer);

        block = new StructBlock();
    }

    public boolean Play() {
        long channel;
        if (m_strFlag == LEFT_PALER) {
            channel = DataSelectedVehicle.getInstance().getLeftChannel();
        } else {
            channel = DataSelectedVehicle.getInstance().getRightChannel();
        }

        if (channel == 0) {
            return false;
        }

        if (isPlay) {
            return true;
        }

        isPlay = true;
        if (m_decoder == null) {
            m_decoder = new H264Decoder();
            m_decoder.InitializeDecoder();
            m_strKey = NetProtocol.getInstance().StartReal(this);
        }

        return true;
    }

    public boolean Stop() {
        if (!isPlay) {
            return true;
        }

        isPlay = false;
        if (m_decoder != null) {
            NetProtocol.getInstance().StopReal(this);
            m_decoder.DeInitializeDecoder();
            m_decoder = null;
        }

        return true;
    }

    public boolean Restart() {
        NetProtocol.getInstance().StopReal(this);
        m_strKey = NetProtocol.getInstance().StartReal(this);
        return true;
    }

    public void Shot() {
        m_GLFRenderer.takeShot();
    }

    public void InputData(byte[] data, int len) {
        if (block != null) {
            block.ParseData(data, len);
        }

        StructH264Frame h264Frame = block.getH264Frame();
        if (h264Frame != null) {
            if (m_decoder != null) {
                if (m_decoder.DecodeFrame(h264Frame.getFrame(), h264Frame.getFrameLength())) {
                    m_GLFRenderer.update(m_decoder.getWidth(), m_decoder.getHeight());
                    m_GLFRenderer.update(m_decoder.GetYData(), m_decoder.GetUData(), m_decoder.GetVData());
                }
            }
            h264Frame.clear();
        }
    }

    public String GetFlag() {
        return m_strFlag;
    }

    public String GetKey() {
        return m_strKey;
    }
}
