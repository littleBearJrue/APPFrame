package com.jrue.appframe.lib.event;


import com.jrue.appframe.lib.base.BaseEvent;

/**
 * 应用有窗口正在显示
 */

public enum OnApplicationStartCtrlEvent implements BaseEvent {
    INSTANCE;

    @Override
    public String toString() {
        return "OnApplicationStartCtrlEvent";
    }
}
