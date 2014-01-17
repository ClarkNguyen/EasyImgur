package sg.vinova.easy_imgur.utilities;

import sg.vinova.easy_imgur.base.Constant;
import sg.vinova.easy_imgur.models.MUser;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.gson.Gson;

public class TokenUtility {

	// SharedPreferences file
	private static final String SHARED_PREFS_FILE = "easy-imgur-share-preferences";

	public static SharedPreferences getAppSharedPrerences(Context mContext) {
		return mContext.getSharedPreferences(SHARED_PREFS_FILE,
				Context.MODE_PRIVATE);
	}

	public static void saveUser(Context mContext, String jsonUser) {
		SharedPreferences prefs = getAppSharedPrerences(mContext);
		Editor editor = prefs.edit();
		editor.putString(Constant.KEY_USER, jsonUser);
		editor.commit();
	}

	public static MUser getUser(Context mContext) {
		SharedPreferences prefs = getAppSharedPrerences(mContext);
		String json = prefs.getString(Constant.KEY_USER, "");
		Gson gson = new Gson();
		MUser user = gson.fromJson(json, MUser.class);
		return user;
	}
}
