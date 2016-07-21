package com.rednovo.ace.data.events;

/**
 * 禁播
 */
public class ForBidEvent extends BaseEvent {
    public String content;

    public ForBidEvent(int eventId, String content) {
        super(eventId);
        this.content = content;
    }
}
