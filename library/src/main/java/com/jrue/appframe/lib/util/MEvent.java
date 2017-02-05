package com.jrue.appframe.lib.util;


import com.jrue.appframe.lib.base.BaseEvent;

import de.greenrobot.event.EventBus;

/**
 * https://github.com/greenrobot/EventBus/blob/master/HOWTO.md
 * Stick事件，Event发出后会被保存，有新订阅时自动发送最后的消息
 * <p/>
 * Created by jrue on 2/5/17.
 */
public class MEvent {
    private static final String TAG = "MEvent";

    public static final boolean DEFAULT_AUTO_REGISTER = false;

    private static final EventBus sEventBus;

    static {
        sEventBus = EventBus.builder()
                .throwSubscriberException(MLog.DEBUG)
                .logNoSubscriberMessages(MLog.DEBUG)
                .logSubscriberExceptions(MLog.DEBUG)
                .sendSubscriberExceptionEvent(false)
                .sendNoSubscriberEvent(false)
                .eventInheritance(false)
                .build();
    }

    public static void register(Object subscriber) {
        if (MLog.DEBUG) MLog.out.d(TAG, "register: " + subscriber);
        sEventBus.registerSticky(subscriber);
    }

    public static void unregister(Object subscriber) {
        if (MLog.DEBUG) MLog.out.d(TAG, "unregister: " + subscriber);
        if (sEventBus.isRegistered(subscriber)) {
            sEventBus.unregister(subscriber);
        } else {
            MLog.out.w(TAG, "Subscriber to unregister was not registered before: " + subscriber.getClass());
        }
    }

    public static void postSticky(BaseEvent event) {
        postSticky(event, MLog.DEBUG);
    }

    public static void postSticky(BaseEvent event, boolean debug) {
        if (debug) MLog.out.d(TAG, "post: " + event.toString());
        sEventBus.postSticky(event);
    }

    public static void post(BaseEvent event) {
        post(event, MLog.DEBUG);
    }

    public static void post(BaseEvent event, boolean debug) {
        if (debug) MLog.out.d(TAG, "post: " + event.toString());
        sEventBus.post(event);
    }

    public static <T extends BaseEvent> T getEvent(Class<T> clazz) {
        return sEventBus.getStickyEvent(clazz);
    }
}
