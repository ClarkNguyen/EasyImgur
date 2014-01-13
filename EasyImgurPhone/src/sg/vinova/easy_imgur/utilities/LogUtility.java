package sg.vinova.easy_imgur.utilities;

import sg.vinova.easy_imgur.base.Constant;
import android.util.Log;

public class LogUtility {
	public static void e(String tag, Object message) {
		if (Constant.IS_APP_LOG_ENABLED) {
			Log.e(tag, message + "");
		}
	}

	public static void d(String tag, Object message) {
		if (Constant.IS_APP_LOG_ENABLED) {
			Log.d(tag, message + "");
		}
	}
}
