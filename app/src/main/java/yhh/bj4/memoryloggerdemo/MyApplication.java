package yhh.bj4.memoryloggerdemo;

import android.app.Application;

import yhh.bj4.memorylogger.LogHelper;

/**
 * Created by yenhsunhuang on 2016/8/12.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LogHelper.getInstance(this);
    }
}
