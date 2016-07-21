package com.rednovo.ace.data.events;

/**
 * 直播暂停与恢复
 */
public class LivePauseEvent extends BaseEvent {
    public boolean isPause = false;

    public LivePauseEvent(int eventId, boolean isPause) {
        super(eventId);
        this.isPause = isPause;
    }
}
