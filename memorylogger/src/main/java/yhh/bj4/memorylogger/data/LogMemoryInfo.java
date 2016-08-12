package yhh.bj4.memorylogger.data;

import android.os.Build;
import android.os.Debug;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.logging.Level;

/**
 * Created by yenhsunhuang on 2016/8/12.
 */
public class LogMemoryInfo extends LogInfo {
    public static final String TOTAL_PRIVATE_CLEAN = "total_private_clean";
    public static final String TOTAL_PRIVATE_DIRTY = "total_private_dirty";
    public static final String TOTAL_PSS = "total_pss";
    public static final String TOTAL_SHARED_CLEAN = "total_shared_clean";
    public static final String TOTAL_SHARED_DIRTY = "total_shared_dirty";

    private int mTotalPrivateClean;
    private int mTotalPrivateDirty;
    private int mTotalPss;
    private int mTotalSharedClean;
    private int mTotalSharedDirty;

    private Debug.MemoryInfo mMemoryInfo;

    private final Level mLogLevel = new CustomLogLevel("memory", 0);

    public LogMemoryInfo(Debug.MemoryInfo info) {
        super();
        mMemoryInfo = info;
        mTotalPrivateDirty = mMemoryInfo.getTotalPrivateDirty();
        mTotalPss = mMemoryInfo.getTotalPss();
        mTotalSharedDirty = mMemoryInfo.getTotalSharedDirty();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mTotalPrivateClean = mMemoryInfo.getTotalPrivateClean();
            mTotalSharedClean = mMemoryInfo.getTotalSharedClean();
        }
    }

    public LogMemoryInfo(JSONObject fromJson) {
        super(fromJson);

    }

    @Override
    public JSONObject toJson() {
        JSONObject rtn = new JSONObject();
        try {
            rtn.put(CREATE_TIME, mCreateTime);
            rtn.put(TOTAL_PRIVATE_CLEAN, mTotalPrivateClean);
            rtn.put(TOTAL_PRIVATE_DIRTY, mTotalPrivateDirty);
            rtn.put(TOTAL_PSS, mTotalPss);
            rtn.put(TOTAL_SHARED_CLEAN, mTotalSharedClean);
            rtn.put(TOTAL_SHARED_DIRTY, mTotalSharedDirty);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rtn;
    }

    @Override
    public Level getLevel() {
        return mLogLevel;
    }
}
