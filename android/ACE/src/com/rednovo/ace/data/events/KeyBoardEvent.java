package com.rednovo.ace.data.events;

/**
 * Created by Administrator on 2016/2/27.
 */
public class KeyBoardEvent extends BaseEvent{
    private boolean show;

    public KeyBoardEvent(boolean show) {
        this.show = show;
    }


    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

}
