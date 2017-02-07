package com.jrue.appframe.lib.event;

import android.text.TextUtils;

import com.jrue.appframe.lib.base.BaseEvent;

/**
 * 设置项变化通知消息
 * Created by jrue on 17/2/7.
 */
public class OnSettingChangedEvent implements BaseEvent {
    public final String key;

    private OnSettingChangedEvent(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "OnSettingChangedEvent{" + key + '}';
    }

    public static OnSettingChangedEvent newInstance(String key) {
        if (TextUtils.isEmpty(key)) {
            throw new IllegalArgumentException("Empty is not supported");
        }
        return new OnSettingChangedEvent(key);
    }
}
