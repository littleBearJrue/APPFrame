package com.jrue.appframe.lib.event;


import com.jrue.appframe.lib.base.BaseEvent;

/**
 * 应用所有窗口都消失
 */
public enum OnApplicationStopCtrlEvent implements BaseEvent {
    INSTANCE;

    @Override
    public String toString() {
        return "OnApplicationStopCtrlEvent";
    }
}
