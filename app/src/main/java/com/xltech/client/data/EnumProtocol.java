package com.xltech.client.data;

/**
 * Created by JooLiu on 2016/1/11.
 */
public class EnumProtocol {
    public static final int HEADER_LEN = 12;
    public static final int TAIL_LEN = 2;
    /// 协议类型
    public static final byte xl_frame_message = 0;
    public static final byte xl_frame_request = 1;
    public static final byte xl_framer_response = 2;

    /// 媒体数据类型
    public static final byte xl_ctrl_data = 0;
    public static final byte xl_ctrl_start = 1;
    public static final byte xl_ctrl_stream = 2;
    public static final byte xl_ctrl_end = 3;
    public static final byte xl_ctrl_stop = 4;

    /// 操作指令定义
    //异步消息及异常推送
    public static final byte xl_message = 0x50;				    ///< 异步消息及异常推送
    //客户端在线心跳
    public static final byte xl_heart_beat = 0x51;				///< 客户端在线心跳
    //用户验证
    public static final byte  xl_login = 0x52;					///< 用户登录
    public static final byte xl_logout = 0x53;					///< 注销登录
    //获取资源目录
    public static final byte xl_category = 0x54;                ///< 资源目录
    //实时报警
    public static final byte xl_vehicle_status = 0x60;			///< 实时报警信息使能
    public static final byte xl_alarm_info = 0x61;				///< 实时报警信息使能
    //实时视频
    public static final byte xl_real_play = 0x70;				///< 实时视频播放
}

