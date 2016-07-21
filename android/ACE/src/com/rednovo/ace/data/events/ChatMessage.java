package com.rednovo.ace.data.events;

import com.rednovo.ace.data.cell.MsgLog;

/**
 * 聊天消息
 */
public class ChatMessage extends BaseEvent {
    public MsgLog mMsgLog;

    public ChatMessage(int id, MsgLog msgLog) {
        this.id = id;
        this.mMsgLog = msgLog;
    }


}
