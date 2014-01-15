package sg.vinova.easy_imgur.fragment.gallery;

import android.annotation.SuppressLint;
import sg.vinova.easy_imgur.fragment.base.BaseFragment;
import sg.vinova.easy_imgur.models.MGallery;

@SuppressLint("ValidFragment")
public class GalleriesArticleFragment extends BaseFragment {
	
	// TAG
	public static final String TAG = "GalleriesArticleFragment";

	private MGallery mGallery;
	
	public GalleriesArticleFragment(MGallery mGallery) {
		this.mGallery = mGallery;
	}
}
