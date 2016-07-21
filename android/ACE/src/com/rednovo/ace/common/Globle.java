package com.rednovo.ace.common;

/**
 * Created by lizhen on 16/3/10.
 */
public interface Globle {

    /***
     * msgType
     */
    String TXT_MSG = "0";// 文本
    String MEDIA_MSG_AUDIO = "1";// 语音
    String MEDIA_MSG_PIC = "2";// 私聊语音

    String PRIVATE = "0";
    String GROUP = "1";

    String EXESTATUS = "exeStatus";

    String ERRCODE = "errCode";

    String ERRMSG = "errMsg";

    /**
     * InteractMode
     **/
    String REQUEST = "0";
    String RESPONSE = "1";

    String PREFIX_FILE = "file://";
    String PREFIX_CONTENT = "content://";
    String PREFIX_ASSETS = "asset:/";
    String PREFIX_RES = "res://";

    int LIVE_LEVE = 3;


    /**
     * 互动消息请求的状态码
     **/
    String KEY_ENTER_ROOM = "002-001";//进入房间
    String KEY_INIT_ROOM_DATA = "002-004";//初始化房间数据
    String KEY_EXIT_ROOM = "002-006";//退出房间
    String KEY_KICK_OUT = "002-007";//踢人
    String KEY_SEND_MSG_STATUS = "002-009";// 发送消息
    String KEY_SEND_GIF = "002-010";//送礼物
    String KEY_LIVE_END_DATA = "002-012";//直播实时数据(结束时展示)
    String KEY_SEND_PARISE = "002-013"; //发送点赞消息
    //String KEY_WARN = "002-017"; //直播间警告
    String KEY_ROOM_MSG = "002-018"; //广播用于在聊天区域显示的消息
    String KEY_SHUTUP = "002-019"; //直播间禁言
    String KEY_FORBID = "002-020"; //禁播
    String KEY_SEAL_NUM = "002-021"; //封号

    String KEY_ROOM_BROADCAST = "004-003"; // 房间消息广播
    String KEY_LIVE_CLOSE = "004-004"; // 房间消息广播
    String KEY_LIVE_PAUSE = "004-005"; // 直播暂停与恢复

    String TYOE_MSG_WARNING = "1";
    String TYP_MSG_SHARE = "2";
    String TYP_MSG_SUPPORT = "3";
    String TYP_MSG_SYSTEM = "4";
    String TYP_MSG_FOLLOW = "5";

    String KEY_FREEZE_ACCOUNT_CODE = "223";


    String KEY_FACING = "cameraId";
    String KEY_UP_STREAM_URL = "stream_url";
    String KEY_LIVE_INFO = "live_info";
    String KEY_SHOW_LIVEING_BTN = "show_liveing_btn";

    int KEY_EVENT_PHONE_STATE = 1; //来电监听
    int KEY_EVENT_CHAT = 2;
    int KEY_EVENT_SESSION_SUCCESS = 3;
    int KEY_EVENT_SESSION_CLOSE = 4;


    int KEY_EVENT_RECEIVE_GIFT = 5; // 收到普通礼物消息
    int KEY_EVENT_GIFT_FINISH = 6;  // 普通礼物动画执行结束
    int KEY_EVENT_ROOM_BROADCAST = 7; // 房间消息广播(EventBus消息)
    int KEY_ENTER_ROOM_MSG = 8; // 进场消息
    int KEY_KICKOUT_MSG = 9; // 踢人消息
    int KEY_LIVE_FINISH = 10; // 直播结束
    int KEY_ONCLICK_LIVE_FINISH = 11; //用户点击直播结束
    int KEY_EVENT_WECHAT_LOGIN_SUCCESS = 12;
    int KEY_EVENT_WECHAT_LOGIN_FAILED = 13;
    int KEY_EVENT_LOGIN_SUCCESS = 14;//登陆成功
    int SEND_GIFT_RESPONSE = 15;//发送礼物回执
    int KEY_RECEIVE_WARN = 16;//发送礼物回执
    int KEY_EVENT_LIVE_BAN = 17;//直播禁止
    int KEY_EVENT_SEAL_NUMBER = 18;//账号封停
    int KEY_EVENT_RECEIVE_SPECIAL_GIFT = 19; // 收到特殊礼物消息
    int KEY_EVENT_RECEIVE__GIFT_FORCHAT = 20; // 用做聊天展示的礼物消息
    int KEY_EVENT_LIVE_PAUSE = 21; // 直播间直播暂停与恢复

    int KEY_ALARM_REFRESH_HALL = 1000;  // 大厅定时刷新
    int KEY_ALARM_REQUEST_AUDIENCE = 1001;  // 大厅定时刷新
    int KEY_DELAY_SHOW_PARISE = 1002;       // 延迟展示下一个点赞图片


}
