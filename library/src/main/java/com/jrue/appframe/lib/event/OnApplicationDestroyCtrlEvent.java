package com.jrue.appframe.lib.event;


import com.jrue.appframe.lib.base.BaseEvent;

/**
 * 所有Activity都已经销毁
 */
public enum OnApplicationDestroyCtrlEvent implements BaseEvent {
    INSTANCE;

    @Override
    public String toString() {
        return "OnApplicationDestroyCtrlEvent";
    }
}
