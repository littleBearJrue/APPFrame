package com.jrue.appframe.provider;

import android.content.Context;

import com.jrue.appframe.lib.provider.BaseSQLiteProvider;
import com.jrue.appframe.lib.provider.SettingTable;
import com.jrue.appframe.lib.util.MConst;
import com.jrue.appframe.main.MainApp;

/**
 * 数据库的访问接口，统一使用Provider方式
 * Created by jrue on 17/2/7.
 */
public class AppProvider extends BaseSQLiteProvider{
    public static String TAG = "AppProvider";

    static final int VERSION = 1;

    public AppProvider() {
        super(MConst.DB_NAME, VERSION);

        Context context = MainApp.getInstance();
        addSQLiteTable(new SettingTable(context));

    }
}
