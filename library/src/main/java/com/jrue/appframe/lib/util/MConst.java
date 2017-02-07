/*
 * Copyright (C) 2015 Meizu Technology Co., Ltd.
 *
 * All rights reserved.
 */

package com.jrue.appframe.lib.util;

/**
 * 存放多个模块使用的常量数据
 * <p/>
 * Created by jrue on 2017/02/05.
 */
public class MConst {
    /**
     * Prevents this class from being instantiated.
     */
    private MConst() {
        throw new AssertionError("Don't create MConst");
    }

    public static final String VERSION_REGULAR = "(\\d+\\.)+\\d+";

    public static final String DB_NAME = "provider.db";

}
