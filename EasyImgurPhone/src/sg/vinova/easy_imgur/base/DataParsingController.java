package sg.vinova.easy_imgur.base;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import sg.vinova.easy_imgur.models.MGallery;
import sg.vinova.easy_imgur.utilities.LogUtility;

import com.google.gson.Gson;

public class DataParsingController {
	// TAG
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
			JSONArray jsonArr = json.getJSONArray(Constant.TAG_PARSE_DATA);
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
	
	/**
	 * Parse detail gallery
	 * @param json
	 * @return
	 */
	public static MGallery parseGallery(JSONObject json) {
		MGallery gallery = new MGallery();
		Gson gson = new Gson();
		
		try {
			// parse basic info
			JSONObject jsonObj = json.getJSONObject(Constant.TAG_PARSE_DATA);
			gallery = gson.fromJson(jsonObj.toString(), MGallery.class);
			
			// add images to gallery
			List<MGallery> images = new ArrayList<MGallery>();
			try {
				JSONArray jsonArrImgs = jsonObj.getJSONArray(Constant.TAG_PARSE_IMAGES);
				for (int i = 0; i < jsonArrImgs.length(); i++) {
					JSONObject jsonImgs = jsonArrImgs.getJSONObject(i);
					MGallery image = gson.fromJson(jsonImgs.toString(), MGallery.class);
					images.add(image);
				}
			} catch (JSONException e) {
				e.printStackTrace();
				LogUtility.e(TAG, Constant.ERROR_PARSE_LIST_IMAGES);
			}
			
			gallery.setImages(images);
			
		} catch (JSONException e) {
			e.printStackTrace();
			LogUtility.e(TAG, Constant.ERROR_PARSE_GALLERY_DETAIL);
		}
		
		return gallery;
	}
}
