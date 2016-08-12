package yhh.bj4.memorylogger;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Debug;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import yhh.bj4.memorylogger.data.LogInfo;
import yhh.bj4.memorylogger.data.LogMemoryInfo;

/**
 * Created by yenhsunhuang on 2016/8/12.
 */
public class LogHelper {
    private static final String TAG = "LogHelper";
    private static final String SHARED_PREFERENCE_FILE = "logger_pref";

    private static final String SP_KEY_LOG_INTERVAL = "sp_key_log_interval";
    private static final String SP_KEY_MAXIMUM_LOGFILE_COUNT = "sp_key_maximum_logfile_count";
    private static final String SP_KEY_LOG_FILE_SIZE = "sp_key_log_file_size";
    private static final String SP_KEY_USE_BUFFER = "sp_key_use_buffer";
    private static final String SP_KEY_ENABLE_LOGGER = "sp_key_enable_logger";

    private static final boolean DEBUG = true;

    private static final int BUFFER_SIZE = 500;

    private static LogHelper sInstance;

    public synchronized static LogHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new LogHelper(context);
        }
        return sInstance;
    }

    private boolean mEnableLogger = true;
    private boolean mUsingBuffer = false;

    private long mLogInterval = Utilities.MINUTE * 5; // default 5 minutes

    private int mMaximumLogFileCount = 5;
    private int mLogFileSize = 1024 * 1024;

    private String mProcessName;

    private final Context mContext;

    private final SharedPreferences mPref;

    private final HandlerThread mHandlerThread = new HandlerThread("Logger handler");
    private final Handler mHandler;

    private final List<LogInfo> mBuffer = new ArrayList<>();

    private final Logger mLogger = Logger.getLogger("LogHelper");

    private FileHandler mFileHandler;

    private final Runnable mLogMemoryInfoRunnable = new Runnable() {
        @Override
        public void run() {
            Debug.MemoryInfo memoryInfo = new Debug.MemoryInfo();
            Debug.getMemoryInfo(memoryInfo);
            log(new LogMemoryInfo(memoryInfo));
            mHandler.postDelayed(mLogMemoryInfoRunnable, mLogInterval);
        }
    };

    {
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
    }

    private LogHelper(Context context) {
        mContext = context.getApplicationContext();
        mPref = mContext.getSharedPreferences(SHARED_PREFERENCE_FILE, Context.MODE_PRIVATE);

        mLogInterval = mPref.getLong(SP_KEY_LOG_INTERVAL, mLogInterval);
        mMaximumLogFileCount = mPref.getInt(SP_KEY_MAXIMUM_LOGFILE_COUNT, mMaximumLogFileCount);
        mLogFileSize = mPref.getInt(SP_KEY_LOG_FILE_SIZE, mLogFileSize);
        mUsingBuffer = mPref.getBoolean(SP_KEY_USE_BUFFER, mUsingBuffer);
        mEnableLogger = mPref.getBoolean(SP_KEY_ENABLE_LOGGER, mEnableLogger);

        mProcessName = Utilities.getProcessName(mContext);

        final File logDir = getLogDirectory(mContext);
        if (logDir != null) {
            try {
                mFileHandler = new FileHandler(logDir.getPath() + File.separator + "%g" + mProcessName + ".txt",
                        mLogFileSize, mMaximumLogFileCount, true);
                mFileHandler.setFormatter(new Formatter() {
                    @Override
                    public String format(LogRecord logRecord) {
                        return logRecord.getLevel() + ": " + logRecord.getMessage() + "\n";
                    }
                });
                mLogger.addHandler(mFileHandler);
                mLogger.setLevel(Level.ALL);
            } catch (IOException e) {
                if (DEBUG) Log.w(TAG, "failed to create logger", e);
            }
        }

        startToLog();
    }

    private void log(LogInfo info) {
        mBuffer.add(info);
        if (mBuffer.size() >= BUFFER_SIZE || !mUsingBuffer) {
            flushBuffer();
        }
    }

    private void flushBuffer() {
        if (mLogger == null) return;
        for (LogInfo info : mBuffer) {
            if (info instanceof LogMemoryInfo) {
                mLogger.log(info.getLevel(), info.toString());
            }
        }
    }

    private void startToLog() {
        mHandler.removeCallbacks(mLogMemoryInfoRunnable);
        mHandler.post(mLogMemoryInfoRunnable);
    }

    public boolean isEnableLogger() {
        return mEnableLogger;
    }

    public void setEnableLogger(boolean enable) {
        mEnableLogger = enable;
        if (mEnableLogger) {
            startToLog();
        }
    }

    public long getLogInterval() {
        return mLogInterval;
    }

    public void setLogInterval(long interval) {
        mLogInterval = interval;
        mPref.edit().putLong(SP_KEY_LOG_INTERVAL, interval).apply();
    }

    private static File getLogDirectory(Context context) {
        if (context == null) return null;
        final File contextFileDir = context.getFilesDir();
        if (contextFileDir == null) return null;
        final File logDir = new File(contextFileDir.getAbsolutePath() + File.separator + "logs");
        if (!logDir.exists()) logDir.mkdir();
        return logDir;
    }

}
