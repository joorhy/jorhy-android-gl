package com.xltech.client.service;

import android.util.DisplayMetrics;

/**
 * Created by JooLiu on 2016/1/13.
 */
public class H264Decoder {
    private DisplayMetrics m_DisplayMetrics = new DisplayMetrics();
    private int m_nDecodeHandler = 0;
    private byte[] m_yData = null;
    private byte[] m_uData = null;
    private byte[] m_vData = null;

    // Load the .so
    static {
        System.loadLibrary("openh264");
        System.loadLibrary("openh264wraper");
    }

    // C functions we call
    public static native int CreateDecoder();
    public static native void DestroyDecoder(int handler);
    public static native int GetWidth(int handler);
    public static native int GetHeight(int handler);
    public static native boolean DecodeOneFrame(int handler, byte[] in, int len);
    public static native boolean GetYUVData(int handler, byte[] y, byte[] u, byte[] v);

    public H264Decoder() {
    }

    public void InitializeDecoder() {
        if (m_nDecodeHandler == 0) {
            m_nDecodeHandler = CreateDecoder();
        }
    }

    public void DeInitializeDecoder() {
        if (m_nDecodeHandler != 0) {
            DestroyDecoder(m_nDecodeHandler);
            m_nDecodeHandler = 0;
        }
    }

    public boolean DecodeFrame(byte[] frameData, int frameLen) {
        if (DecodeOneFrame(m_nDecodeHandler, frameData, frameLen)) {
            Initialize();
            return GetYUVData(m_nDecodeHandler, m_yData, m_uData, m_vData);
        }

        return false;
    }

    private void Initialize() {
        if (m_DisplayMetrics.widthPixels == 0) {
            m_DisplayMetrics.widthPixels = GetWidth(m_nDecodeHandler);
        }
        if (m_DisplayMetrics.heightPixels == 0) {
            m_DisplayMetrics.heightPixels = GetHeight(m_nDecodeHandler);
        }
        if (m_uData == null && m_uData == null && m_vData == null) {
            int nYLen = m_DisplayMetrics.widthPixels * m_DisplayMetrics.heightPixels;
            m_yData = new byte[nYLen];

            int nUVLen = nYLen >> 2;
            m_uData = new byte[nUVLen];
            m_vData = new byte[nUVLen];
        }
    }

    public byte[] GetYData() {
        return m_yData;
    }

    public byte[] GetUData() {
        return m_uData;
    }

    public byte[] GetVData() {
        return m_vData;
    }

    public int getWidth() {
        return m_DisplayMetrics.widthPixels;
    }

    public int getHeight() {
        return  m_DisplayMetrics.heightPixels;
    }
}
