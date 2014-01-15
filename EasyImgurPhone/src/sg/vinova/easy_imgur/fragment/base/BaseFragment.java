package sg.vinova.easy_imgur.fragment.base;

import sg.vinova.easy_imgur.activity.ContentActivity;
import sg.vinova.easy_imgur.activity.R;
import android.content.Context;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragment;
import com.android.volley.Response;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class BaseFragment extends SherlockFragment {

	public Context mContext;

	// Image loader
	public ImageLoader imageLoader;
	public DisplayImageOptions options;

	// Paging
	public int page;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// init context;
		this.mContext = getActivity();

		// init image loader
		if (getActivity() != null && getActivity() instanceof ContentActivity) {
			imageLoader = ((ContentActivity) getActivity()).getImageLoader();
			// options = ((ContentActivity)
			// getActivity()).getImageLoaderOptions();
		}
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.bg_default)
				.showImageForEmptyUri(R.drawable.bg_default)
				.showImageOnFail(R.drawable.bg_default)
				.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
				.cacheInMemory(true).cacheOnDisc(true).build();
	}

	public Response.ErrorListener getErrorListener() {
		if (getActivity() != null && getActivity() instanceof ContentActivity) {
			return ((ContentActivity) getActivity()).getErrorListener();
		}
		return null;
	}

}
