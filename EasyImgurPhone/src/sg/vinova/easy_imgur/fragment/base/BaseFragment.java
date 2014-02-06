package sg.vinova.easy_imgur.fragment.base;

import sg.vinova.easy_imgur.activity.ContentActivity;
import sg.vinova.easy_imgur.interfaces.TokenHandle;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.MenuItem;
import com.android.volley.Response;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class BaseFragment extends SherlockFragment {
	
	// tag
	public static final String TAG = "BaseFragment";

	public Context mContext;

	// Image loader
	public ImageLoader imageLoader;
	public DisplayImageOptions options;

	// Paging
	public int page;

	// Action bar
	public ActionBar actionBar;

	// flag check load next page
	public boolean isMoreData;
	
	// flag check if in content screen
	public boolean isArticle;

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
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setDisplayShowHomeEnabled(true);
			actionBar.setDisplayShowTitleEnabled(true);
		}
		setHasOptionsMenu(true);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		ContentActivity contentActivity = (ContentActivity) getActivity();
		
		if (contentActivity == null) {
			return false;
		}
		
		if (!isArticle) {
			if (contentActivity.isLeftMenuOpen()) {
				contentActivity.handleLeftMenu(false);
			} else {
				contentActivity.handleLeftMenu(true);
			}
		} else {
			contentActivity.getSupportFragmentManager().popBackStack();
		}
		
		return super.onOptionsItemSelected(item);
	}

	public Response.ErrorListener getErrorListener(TokenHandle tokenHandle) {
		if (getActivity() != null && getActivity() instanceof ContentActivity) {
			return ((ContentActivity) getActivity())
					.getErrorListener(tokenHandle);
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
			((ContentActivity) getActivity()).switchContent(fragment,
					addToBackstack, clearBackstack);
		}
	}

	public void switchContent(Fragment fragment, boolean addToBackstack,
			String tag) {
		if (getActivity() == null) {
			return;
		}

		if (getActivity() instanceof ContentActivity) {
			((ContentActivity) getActivity()).switchContent(fragment,
					addToBackstack, tag);
		}
	}

	public void switchContent(Fragment fragment, boolean addToBackstack,
			boolean clearBackstack, String tag) {
		if (getActivity() == null) {
			return;
		}

		if (getActivity() instanceof ContentActivity) {
			((ContentActivity) getActivity()).switchContent(fragment,
					addToBackstack, clearBackstack, tag);
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

}
