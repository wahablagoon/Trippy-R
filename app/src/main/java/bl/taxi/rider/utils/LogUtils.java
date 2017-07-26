package bl.taxi.rider.utils;

/*
 * Created by DELL on 26/07/2017.
 */

import android.support.annotation.Nullable;
import android.util.Log;

public class LogUtils {

    private static String TAG = "bltRider";

    public static void debug (String msg, @Nullable Throwable throwable) {
        if (throwable != null) {
            Log.d(TAG, msg, throwable);
        } else {
            Log.d(TAG, msg);
        }
    }
}
