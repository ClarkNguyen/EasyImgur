package sg.vinova.easy_imgur.networking;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import sg.vinova.easy_imgur.base.Constant;
import sg.vinova.easy_imgur.utilities.LogUtility;
import sg.vinova.easy_imgur.utilities.TokenUtility;
import android.content.Context;
import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;

public class ImgurAPI {

	// TAG
	public static final String TAG = "ImgurAPI";

	private static RequestQueue mRequestQueue;

	// Instance object
	private static ImgurAPI imgurClient;

	// Imgur API url
	private static final String imgurClientUrl = "https://api.imgur.com/3";
	private static final String oauthUrl = "https://api.imgur.com/oauth2/token";

	private String url;

	public static void init(Context context) {
		mRequestQueue = Volley.newRequestQueue(context);
	}

	public static RequestQueue getRequestQueue() {
		if (mRequestQueue != null) {
			return mRequestQueue;
		} else {
			throw new IllegalStateException("RequestQueue not initialized");
		}
	}

	public static ImgurAPI getClient() {
		if (imgurClient == null) {
			imgurClient = new ImgurAPI();
			imgurClient.url = imgurClientUrl;
		}
		return imgurClient;
	}

	public String getUrl(String path) {
		return this.url + "/" + path;
	}

	public static String generateFullUrl(String url,
			HashMap<String, String> params) {
		if (params.keySet().size() > 0) {
			url = url + "?";
			for (String key : params.keySet()) {
				url = url + key + "=" + params.get(key) + "&";
			}
		}
		LogUtility.e(TAG, "request url: " + url);
		return url;
	}

	public static void get(final Context mContext, String url,
			final HashMap<String, String> params,
			Response.Listener<JSONObject> listener,
			Response.ErrorListener errorListener) {
		RequestQueue mRequestQueue = getRequestQueue();
		JsonObjectRequest jsonRequest = new JsonObjectRequest(Method.GET,
				generateFullUrl(url, params), null, listener, errorListener) {
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				HashMap<String, String> params = new HashMap<String, String>();
				if (TokenUtility.getUser(mContext) != null
						&& !TextUtils.isEmpty(TokenUtility.getUser(mContext)
								.getAccessToken())) {
					params.put("Authorization", "Bearer "
							+ TokenUtility.getUser(mContext).getAccessToken());
					LogUtility.e(TAG, "Bearer "
							+ TokenUtility.getUser(mContext).getAccessToken());
				} else {
					params.put("Authorization", "Client-ID "
							+ Constant.CLIENT_ID);
					LogUtility.e(TAG, "Client-ID " + Constant.CLIENT_ID);
				}
				return params;
			}
		};
		mRequestQueue.add(jsonRequest);
	}

	public static void getArray(String url,
			final HashMap<String, String> params,
			Response.Listener<JSONArray> listener,
			Response.ErrorListener errorListener) {
		RequestQueue mRequestQueue = getRequestQueue();
		JsonArrayRequest jsonRequest = new JsonArrayRequest(generateFullUrl(
				url, params), listener, errorListener);
		mRequestQueue.add(jsonRequest);
	}

	public static void post(String url, JSONObject params,
			Response.Listener<JSONObject> listener,
			Response.ErrorListener errorListener) {
		RequestQueue mRequestQueue = getRequestQueue();
		JsonObjectRequest jsonRequest = new JsonObjectRequest(Method.POST, url,
				params, listener, errorListener);
		mRequestQueue.add(jsonRequest);
		LogUtility.e(TAG, "post url: " + url);
	}

	public static void post(final Context mContext, String url,
			final HashMap<String, String> params,
			Response.Listener<JSONObject> listener,
			Response.ErrorListener errorListener) {
		RequestQueue mRequestQueue = getRequestQueue();
		JsonObjectRequest jsonRequest = new JsonObjectRequest(Method.POST,
				generateFullUrl(url, params), null, listener, errorListener) {
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				HashMap<String, String> params = new HashMap<String, String>();
				if (TokenUtility.getUser(mContext) != null
						&& !TextUtils.isEmpty(TokenUtility.getUser(mContext)
								.getAccessToken())) {
					params.put("Authorization", "Bearer "
							+ TokenUtility.getUser(mContext).getAccessToken());
					LogUtility.e(TAG, "Bearer "
							+ TokenUtility.getUser(mContext).getAccessToken());
				} else {
					params.put("Authorization", "Client-ID "
							+ Constant.CLIENT_ID);
					LogUtility.e(TAG, "Client-ID " + Constant.CLIENT_ID);
				}
				return params;
			}
		};
		mRequestQueue.add(jsonRequest);
	}

	/**********************
	 ******* USER *********
	 **********************/

	public void getNewToken(Context mContext,
			Response.Listener<JSONObject> listener,
			Response.ErrorListener errorListener) {

		JSONObject params = new JSONObject();
		try {
			params.put(Constant.PARAM_CLIENT_ID, Constant.CLIENT_ID);
			params.put(Constant.PARAM_CLIENT_SECRET, Constant.CLIENT_SECRET);
			params.put(Constant.PARAM_GRANT_TYPE,
					Constant.PARAM_TYPE_REFRESH_TOKEN);
			params.put(Constant.PARAM_REFRESH_TOKEN,
					TokenUtility.getUser(mContext).getRefreshToken());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		ImgurAPI.post(oauthUrl, params, listener, errorListener);
	}

	/**********************
	 ******* GALLERY ******
	 **********************/
	/**
	 * Get all gallery images
	 * 
	 * @param mContext
	 * @param section
	 * @param page
	 * @param sort
	 * @param window
	 * @param showViral
	 * @param listener
	 * @param errorListener
	 */
	public void getAllGallery(Context mContext, String section, int page,
			String sort, String window, boolean showViral,
			Response.Listener<JSONObject> listener,
			Response.ErrorListener errorListener) {
		String url = "gallery/";
		url += section + "/";

		if (!TextUtils.isEmpty(sort)) {
			url += sort + "/";
		}
		if (section.equalsIgnoreCase(Constant.PARAM_TYPE_SECTION_TOP)
				&& !TextUtils.isEmpty(window)) {
			url += window + "/";
		}
		url += page;

		HashMap<String, String> params = new HashMap<String, String>();
		params.put(Constant.PARAM_SHOW_VIRAL, String.valueOf(showViral));

		ImgurAPI.get(mContext, getUrl(url), params, listener, errorListener);
	}

	/**
	 * Get album detail for show first image of album in list galleries
	 * 
	 * @param mContext
	 * @param albumId
	 * @param listener
	 * @param errorListener
	 */
	public void getAlbumDetails(Context mContext, String albumId,
			Response.Listener<JSONObject> listener,
			Response.ErrorListener errorListener) {
		String url = getUrl("gallery/album/") + albumId;

		HashMap<String, String> params = new HashMap<String, String>();

		ImgurAPI.get(mContext, url, params, listener, errorListener);
	}

	/**
	 * Get detail of a gallery
	 * 
	 * @param galleryId
	 * @param listener
	 * @param errorListener
	 */
	public void getDetailGallery(Context mContext, String galleryId,
			Response.Listener<JSONObject> listener,
			Response.ErrorListener errorListener) {
		String url = getUrl("gallery/") + galleryId;

		HashMap<String, String> params = new HashMap<String, String>();

		ImgurAPI.get(mContext, url, params, listener, errorListener);
	}

	/**
	 * Get detail of an image
	 * 
	 * @param imageId
	 * @param listener
	 * @param errorListener
	 */
	public void getDetailImage(Context mContext, String imageId,
			Response.Listener<JSONObject> listener,
			Response.ErrorListener errorListener) {
		String url = getUrl("image/") + imageId;

		HashMap<String, String> params = new HashMap<String, String>();

		ImgurAPI.get(mContext, url, params, listener, errorListener);
	}

	/**
	 * Favorite an album
	 * 
	 * @param mContext
	 * @param galleryId
	 * @param listener
	 * @param errorListener
	 */
	public void favoriteAlbum(Context mContext, String galleryId,
			Response.Listener<JSONObject> listener,
			Response.ErrorListener errorListener) {
		String url = getUrl("album/") + galleryId + "/favorite";

		HashMap<String, String> params = new HashMap<String, String>();

		ImgurAPI.post(mContext, url, params, listener, errorListener);
	}

	/**
	 * Favorite an image
	 * 
	 * @param mContext
	 * @param galleryId
	 * @param listener
	 * @param errorListener
	 */
	public void favoriteImage(Context mContext, String galleryId,
			Response.Listener<JSONObject> listener,
			Response.ErrorListener errorListener) {
		String url = getUrl("image/") + galleryId + "/favorite";

		HashMap<String, String> params = new HashMap<String, String>();

		ImgurAPI.post(mContext, url, params, listener, errorListener);
	}

	/**
	 * Vote an gallery object
	 * 
	 * @param mContext
	 * @param galleryId
	 * @param isUp
	 * @param listener
	 * @param errorListener
	 */
	public void voteGallery(Context mContext, String galleryId, boolean isUp,
			Response.Listener<JSONObject> listener,
			Response.ErrorListener errorListener) {
		String vote;
		if (isUp)
			vote = "up";
		else
			vote = "down";
		String url = getUrl("gallery/") + galleryId + "/vote/" + vote;

		HashMap<String, String> params = new HashMap<String, String>();

		ImgurAPI.post(mContext, url, params, listener, errorListener);
	}

	/**********************
	 ******* MEME *********
	 **********************/

	/**
	 * Get all meme list
	 * 
	 * @param mContext
	 * @param sort
	 * @param window
	 * @param page
	 * @param listener
	 * @param errorListener
	 */
	public void getAllMeme(Context mContext, String sort, String window,
			int page, Response.Listener<JSONObject> listener,
			Response.ErrorListener errorListener) {
		String url = "gallery/g/memes/";
		if (TextUtils.isEmpty(sort)) {
			url += "viral/";
		} else {
			url += sort;
		}
		if (!TextUtils.isEmpty(window)) {
			url += window + "/";
		}
		url += page;

		HashMap<String, String> params = new HashMap<String, String>();

		ImgurAPI.get(mContext, getUrl(url), params, listener, errorListener);
	}

	/******************************
	 ******* UPLOAD IMAGE *********
	 ******************************/

	/**
	 * Upload image
	 * @param mcContext
	 * @param imageFile
	 * @param album
	 * @param name
	 * @param title
	 * @param description
	 * @param callback
	 */
	public void uploadImage(Context mcContext, String imageFile, String album,
			String name, String title, String description,
			AjaxCallback<JSONObject> callback) {
		AQuery aq = new AQuery(mcContext);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("type", "base64");
		params.put("image", imageFile);
		if (!TextUtils.isEmpty(album)) {
			params.put("album", album);
		}
		if (!TextUtils.isEmpty(name)) {
			params.put("name", name);
		}
		params.put("title", title);
		params.put("description", description);
		aq.ajax(getUrl("upload"), params, JSONObject.class, callback);
	}
}
