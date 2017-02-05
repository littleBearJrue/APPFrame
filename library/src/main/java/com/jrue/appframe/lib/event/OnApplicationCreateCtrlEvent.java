package com.jrue.appframe.lib.event;


import com.jrue.appframe.lib.base.BaseEvent;

/**
 * 有Activity创建
 */
public enum OnApplicationCreateCtrlEvent implements BaseEvent {
    INSTANCE;

    @Override
    public String toString() {
        return "OnApplicationCreateCtrlEvent";
    }
}
