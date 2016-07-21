package com.rednovo.ace.data.events;

/**
 * 封号
 */
public class SealNumberEvent extends BaseEvent {
    public String content;

    public SealNumberEvent(int eventId, String content) {
        super(eventId);
        this.content = content;
    }
}
