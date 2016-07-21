package com.rednovo.ace.data.events;

/**
 * Created by Administrator on 2016/3/1.
 */
public class BaseEvent {
    public int id;

    public Object object;
    public BaseEvent() {

    }

    public BaseEvent(int eventId) {
        this.id = eventId;

    }

    public BaseEvent(int eventId, Object object){
        this(eventId);
        this.object = object;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }



}
