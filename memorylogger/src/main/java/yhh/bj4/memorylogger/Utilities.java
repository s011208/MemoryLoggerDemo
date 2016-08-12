package yhh.bj4.memorylogger;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Process;

/**
 * Created by yenhsunhuang on 2016/8/12.
 */
public class Utilities {
    public static final long SECOND = 1000;
    public static final long MINUTE = 60 * SECOND;
    public static final long HOUR = 60 * MINUTE;
    public static final long DAY = 24 * HOUR;

    public static String getProcessName(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo rapi : am.getRunningAppProcesses()) {
            if (rapi.pid == Process.myPid()) {
                return rapi.processName;
            }
        }
        return null;
    }
}
