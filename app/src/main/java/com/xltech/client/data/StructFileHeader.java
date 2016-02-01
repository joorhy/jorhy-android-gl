package com.xltech.client.data;

import java.io.Serializable;
import java.nio.Buffer;
import java.nio.ByteBuffer;

/**
 * Created by JooLiu on 2016/1/29.
 */
public class StructFileHeader implements Serializable {
    /**
     * unsigned char szID[4];           //统一为GZ_V
     * unsigned char sRailwayID[2];     //路局代码
     * unsigned char bEnterprisesID;    //企业代码
     * unsigned char szEditionID[3];    //版本号
     * unsigned char bReserver[7];      //企业自定(保留)
     * unsigned char bSeparation1;      //分隔符号1('_')
     * unsigned char vehicleID[8];      //作业车号
     * unsigned char bSeparation2;
     * unsigned char startTime[12];     //年月日时分秒
     * unsigned char bSeparation3;
     * unsigned char dwDuration[4];     //时长(秒)
     * unsigned char bSeparation4;
     * unsigned short shChannel;        //通道码
     * unsigned char bSeparation5;
     * unsigned char bReserver[5];      //保留位
     * unsigned char szNO[32];			//车号
     * char  chDeviceType;				//区分卡的标记 1是海康 2 汉邦 3 中维世纪 4 大华 5 其它
     * unsigned int uChannelSum;        //通道总数
     * long long llChannelsNum;		    //对应通道号
     * bool bDownload;                  //是否为下载文件
     * long long tmStartTime;		    //开始时间 注：改成可精准到ms longlong
     * long long tmStopTime;			//结束时间 注：改成可精准到ms longlong
     * unsigned int uDuration;			//文件所用时间(ms)
     * unsigned char szHash[16];		//哈希校验值
     * unsigned long dwIndexTable[300];	//文件搜索表
     * unsigned char bReserver[64];		//保留字节  */
    public static int SIZE = 1399;
    private ByteBuffer fileHeaderBuffer = null;

    public StructFileHeader() {
        fileHeaderBuffer = ByteBuffer.allocate(SIZE);
    }

    public ByteBuffer getBuffer() {
        return fileHeaderBuffer;
    }

    public byte[] getArray() {
        return fileHeaderBuffer.array();
    }
}
