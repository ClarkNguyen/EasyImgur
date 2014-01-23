package sg.vinova.easy_imgur.fragment.base;

import sg.vinova.easy_imgur.activity.BaseActivity;
import sg.vinova.easy_imgur.activity.ContentActivity;
import sg.vinova.easy_imgur.interfaces.TokenHandle;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.android.volley.Response;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class BaseFragment extends SherlockFragment {

	public Context mContext;

	// Image loader
	public ImageLoader imageLoader;
	public DisplayImageOptions options;

	// Paging
	public int page;
	
	// Action bar
	public ActionBar actionBar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// init context;
		this.mContext = getActivity();

		// init image loader
		if (getActivity() != null && getActivity() instanceof ContentActivity) {
			imageLoader = ((ContentActivity) getActivity()).getImageLoader();
			options = ((ContentActivity) getActivity()).getImageLoaderOptions();
			actionBar = ((ContentActivity) getActivity()).getSupportActionBar();
		}
	}

	public Response.ErrorListener getErrorListener(TokenHandle tokenHandle) {
		if (getActivity() != null && getActivity() instanceof ContentActivity) {
			return ((ContentActivity) getActivity()).getErrorListener(tokenHandle);
		}
		return null;
	}
	
	/**********************************
	 ********* Switch content ********* 
	 **********************************/
	
	public void switchContent(Fragment fragment, boolean addToBackstack,
			boolean clearBackstack) {
		if (getActivity() == null) {
			return;
		}
		
		if (getActivity() instanceof ContentActivity) {
			((ContentActivity) getActivity()).switchContent(fragment, addToBackstack, clearBackstack);
		}
	}
	
	public void switchContent(Fragment fragment, boolean addToBackstack,
			String tag) {
		if (getActivity() == null) {
			return;
		}
		
		if (getActivity() instanceof ContentActivity) {
			((ContentActivity) getActivity()).switchContent(fragment, addToBackstack, tag);
		}
	}
	
	public void switchContent(Fragment fragment, boolean addToBackstack,
			boolean clearBackstack, String tag) {
		if (getActivity() == null) {
			return;
		}
		
		if (getActivity() instanceof ContentActivity) {
			((ContentActivity) getActivity()).switchContent(fragment, addToBackstack, clearBackstack, tag);
		}
	}

	public void switchContent(Fragment fragment, boolean addToBackstack) {
		if (getActivity() == null) {
			return;
		}

		if (getActivity() instanceof ContentActivity) {
			((ContentActivity) getActivity()).switchContent(fragment,
					addToBackstack);
		}
	}

	/**
	 * Show the progress bar
	 * @param visible
	 */
	protected void showProgressBar(boolean visible) {
		if (mContext == null) {
			return;
		}
		
		if (getActivity() instanceof BaseActivity) {
			BaseActivity activity = (BaseActivity) mContext;
			activity.showProgressBar(visible);
		}
	}
}
