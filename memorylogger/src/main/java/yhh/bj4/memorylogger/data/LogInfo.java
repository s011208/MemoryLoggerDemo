package yhh.bj4.memorylogger.data;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.logging.Level;

/**
 * Created by yenhsunhuang on 2016/8/12.
 */
public abstract class LogInfo implements Comparable<LogInfo> {
    public static final String CREATE_TIME = "create_time";
    public long mCreateTime;

    public LogInfo() {
        mCreateTime = System.currentTimeMillis();
    }

    public LogInfo(JSONObject fromJson) {
        try {
            mCreateTime = fromJson.getLong(CREATE_TIME);
        } catch (JSONException e) {
            mCreateTime = -1;
            e.printStackTrace();
        }
    }

    public abstract JSONObject toJson();

    public abstract Level getLevel();

    @Override
    public String toString() {
        return toJson().toString();
    }

    @Override
    public int compareTo(LogInfo logInfo) {
        if (mCreateTime == logInfo.mCreateTime) return 0;
        return mCreateTime < logInfo.mCreateTime ? -1 : 1;
    }
}
