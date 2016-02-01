package com.xltech.client.data;

import com.xltech.client.config.Configer;

/**
 * Created by JooLiu on 2016/1/26.
 */
public class DataSelectedVehicle {
    private static DataSelectedVehicle instance = null;

    private String m_strVehicleId = null;
    private int m_nTotalChannels = 0;
    private int m_nChannel = 0;

    public static DataSelectedVehicle getInstance() {
        if (instance == null) {
            instance = new DataSelectedVehicle();
        }
        return instance;
    }

    public void setSelectedVehicle(String strVehicleId, int nTotalChannels) {
        m_strVehicleId = strVehicleId;
        m_nTotalChannels = nTotalChannels;
        m_nChannel = 0;
    }

    public String getSelectedVehicleId() {
        return m_strVehicleId;
    }

    public boolean nextChannel() {

        if ((m_nChannel + 1) > m_nTotalChannels)
        {
            return false;
        }
        m_nChannel += 2;
        return true;
    }

    public boolean prevChannel() {
        m_nChannel -= 2;
        if (m_nChannel < 0)
        {
            m_nChannel = 0;
            return false;
        }
        return true;
    }

    public long getLeftChannel() {
        if (Configer.UseTemp()) {
            return (m_nChannel + 1);
        }
        return (1 << m_nChannel);
    }

    public long getRightChannel() {
        if ((m_nChannel + 1) >= m_nTotalChannels)
        {
            return 0;
        }

        if (Configer.UseTemp()) {
            return (m_nChannel + 2);
        }

        return (1 << (m_nChannel + 1));
    }
}
