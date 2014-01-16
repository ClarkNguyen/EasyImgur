package sg.vinova.easy_imgur.fragment.gallery;

import org.json.JSONObject;

import sg.vinova.easy_imgur.activity.R;
import sg.vinova.easy_imgur.base.DataParsingController;
import sg.vinova.easy_imgur.fragment.base.BaseFragment;
import sg.vinova.easy_imgur.models.MGallery;
import sg.vinova.easy_imgur.networking.ImgurAPI;
import sg.vinova.easy_imgur.utilities.LogUtility;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;
import com.android.volley.Response.Listener;

@SuppressLint("ValidFragment")
public class GalleriesArticleFragment extends BaseFragment {
	
	// TAG
	public static final String TAG = "GalleriesArticleFragment";

	private MGallery mGallery;
	
	public GalleriesArticleFragment(MGallery mGallery) {
		this.mGallery = mGallery;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_detail_n, container, false);
		getDetailForGallery();
		
		return view;
	}
	
	private void findViews() {
		
	}
	
	private void fillViews() {
		
	}
	
	private void getDetailForGallery() {
		if (mGallery != null) {
			ImgurAPI.getClient().getDetailGallery(mGallery.getId(), getListener(), getErrorListener());
		}
	}
	
	private Response.Listener<JSONObject> getListener() { 
		return new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject json) {
				mGallery = DataParsingController.parseGallery(json);
				LogUtility.e(TAG, mGallery.getImages().size());
			}
		};
	}
}
