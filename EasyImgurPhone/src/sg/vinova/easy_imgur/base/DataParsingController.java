package sg.vinova.easy_imgur.base;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import sg.vinova.easy_imgur.models.MGallery;
import sg.vinova.easy_imgur.utilities.LogUtility;

import com.google.gson.Gson;

public class DataParsingController {

	public static final String TAG = "DataParsingController";
	
	/**
	 * TODO parse all galleries
	 * @param json
	 * @return
	 */
	public static List<MGallery> parseGalleries(JSONObject json) {
		List<MGallery> galleries = new ArrayList<MGallery>();
		Gson gson = new Gson();
		
		try {
			JSONArray jsonArr = json.getJSONArray("data");
			for (int i = 0; i < jsonArr.length(); i++) {
				JSONObject jsonGallery = jsonArr.getJSONObject(i);
				MGallery mGallery = gson.fromJson(jsonGallery.toString(), MGallery.class);
				galleries.add(mGallery);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogUtility.e(TAG, "parse galleries error");
		}
		return galleries;
	}
}
