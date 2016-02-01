package com.xltech.client.service;

import android.os.Environment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by JooLiu on 2016/1/26.
 */
public class ManPictures {
    private static ManPictures instance = null;
    private String m_strCurrentTime = null;
    private List<String> m_listPath = null; //存放路径
    private int m_nIndex = 0;

    public static ManPictures getInstance() {
        if (instance == null) {
            instance = new ManPictures();
        }
        return instance;
    }

    public void getPictureList() {
        if (m_listPath == null) {
            m_listPath = new ArrayList<String>();
        }

        m_listPath.clear();
        String filePath = getFilePath();
        if (filePath != null) {
            File file = new File(filePath);
            File[] files = file.listFiles();
            // 将所有文件存入list中
            if(files != null)
            {
                int count = files.length;// 文件个数
                for (int i = 0; i < count; i++)
                {
                    File fileItem = files[i];
                    m_listPath.add(fileItem.getPath());
                }
                Collections.sort(m_listPath);
            }
        }
    }

    public boolean nextPicture() {
        if (m_nIndex < (m_listPath.size() - 1)) {
            m_nIndex++;
            return true;
        }
        return false;
    }

    public boolean prevPicture() {
        if (m_nIndex > 0) {
            m_nIndex--;
            return true;
        }
        return false;
    }

    private String getFilePath() {
        File dirFile = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED); //判断sd卡是否存在
        if (sdCardExist)
        {
            dirFile = Environment.getExternalStorageDirectory();//获取SD卡根目录
        } else {
            dirFile = Environment.getDataDirectory();//获取DATA根目录
        }

        String strFileDir = dirFile.toString() + "/xl_picture";
        File file = new File(strFileDir);
        if (!file.exists()) {
            if (file.mkdir()) {
                return strFileDir;
            }
        } else {
            return strFileDir;
        }
        return null;
    }

    public void refreshTime() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss");
        m_strCurrentTime = sDateFormat.format(new java.util.Date());
    }

    public String getPictureName(String flag) {
        if (m_listPath.size() > m_nIndex) {
            String strPicturePath = m_listPath.get(m_nIndex);
            if (strPicturePath != null) {
                return strPicturePath + "/" + flag + ".png";
            }
        }

        return null;
    }

    public String getFileName(String flag) {
        String strFileDir = getFilePath();
        String strFilePath = null;
        if (strFileDir != null && m_strCurrentTime != null) {
            strFilePath = strFileDir + "/" + m_strCurrentTime;
            File file = new File(strFilePath);
            if (!file.exists()) {
                if (!file.mkdir()) {
                    strFilePath = null;
                }
            }
        }

        String strFileName = null;
        if (strFilePath != null) {
            strFileName = strFilePath + "/" + flag + ".png";
        }
        return strFileName;
    }
}
