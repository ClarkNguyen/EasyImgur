package sg.vinova.easy_imgur.fragment.gallery;

import org.json.JSONObject;

import sg.vinova.easy_imgur.activity.R;
import sg.vinova.easy_imgur.base.DataParsingController;
import sg.vinova.easy_imgur.fragment.base.BaseFragment;
import sg.vinova.easy_imgur.models.MGallery;
import sg.vinova.easy_imgur.networking.ImgurAPI;
import sg.vinova.easy_imgur.utilities.LogUtility;
import sg.vinova.easy_imgur.utilities.TextRefineUtil;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.Response.Listener;

@SuppressLint("ValidFragment")
public class GalleriesArticleFragment extends BaseFragment implements OnClickListener {
	
	// TAG
	public static final String TAG = "GalleriesArticleFragment";

	private MGallery mGallery;
	
	/**
	 * All views
	 */
	private TextView tvViewsCount;
	private TextView tvTitle;
	private TextView tvAuthor;
	
	private ImageView ivContent;
	
	private TextView tvPoints;
	private TextView tvLink;
	private TextView textLinkCopy;
	private ImageView ivFavourite;
	
	private LinearLayout llUps;
	private LinearLayout llDowns;
	private TextView tvUps;
	private TextView tvDowns;
	
	private TextView tvDescription;
	
	public GalleriesArticleFragment(MGallery mGallery) {
		this.mGallery = mGallery;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Settings
		View view = inflater.inflate(R.layout.fragment_detail, container, false);
		findViews(view);
		
		getDetailForGallery();
		
		return view;
	}
	
	/**
	 * Find all views
	 */
	private void findViews(View view) {
		/**
		 * Title
		 */
		tvViewsCount = (TextView) view.findViewById(R.id.tv_views_count);
		tvTitle = (TextView) view.findViewById(R.id.tv_title);
		tvAuthor = (TextView) view.findViewById(R.id.tv_author);
		
		/**
		 * Image content
		 */
		ivContent = (ImageView) view.findViewById(R.id.iv_content);
		
		/**
		 * Info
		 */
		tvPoints = (TextView) view.findViewById(R.id.tv_points);
		tvLink = (TextView) view.findViewById(R.id.tv_link);
		textLinkCopy = (TextView) view.findViewById(R.id.text_link_copy);
		ivFavourite = (ImageView) view.findViewById(R.id.iv_favourite);
		textLinkCopy.setOnClickListener(this);
		ivFavourite.setOnClickListener(this);
		
		/**
		 * Voting
		 */
		llUps = (LinearLayout) view.findViewById(R.id.ll_ups);
		llDowns = (LinearLayout) view.findViewById(R.id.ll_downs);
		tvUps = (TextView) view.findViewById(R.id.tv_ups);
		tvDowns = (TextView) view.findViewById(R.id.tv_downs);
		llUps.setOnClickListener(this);
		llDowns.setOnClickListener(this);
		
		/**
		 * Description
		 */
		tvDescription = (TextView) view.findViewById(R.id.tv_description);
	}
	
	/**
	 * Fill views
	 */
	private void fillViews() {
		/**
		 * Fill title
		 */
		tvViewsCount.setText(mGallery.getViews()+"");
		tvTitle.setText(TextRefineUtil.refineString(mGallery.getTitle()));
		tvAuthor.setText(TextRefineUtil.refineString(mGallery.getAccountUrl()));
		
		/**
		 * Image content
		 */
		if (!mGallery.isAlbum()) {
			imageLoader.displayImage(mGallery.getLink(), ivContent, options);
		} else {
			// TODO is album
		}
		
		/**
		 * Info
		 */
		tvPoints.setText(mGallery.getScore()+"");
		tvLink.setText(TextRefineUtil.refineString(mGallery.getLink()));
		
		/**
		 * Voting
		 */
		tvUps.setText(mGallery.getUps()+"");
		tvDowns.setText(mGallery.getDowns()+"");
		
		/**
		 * Description
		 */
		tvDescription.setText(TextRefineUtil.refineString(mGallery.getDescription()));
	}
	
	/**
	 * Get detail content for a gallery
	 */
	private void getDetailForGallery() {
		if (mGallery != null) {
			ImgurAPI.getClient().getDetailGallery(mContext, mGallery.getId(), getListener(), getErrorListener());
		}
	}
	
	/**
	 * Listener for getting gallery detail
	 * @return
	 */
	private Response.Listener<JSONObject> getListener() { 
		return new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject json) {
				mGallery = DataParsingController.parseGallery(json);
				LogUtility.e(TAG, mGallery.getImages().size());
				fillViews();
			}
		};
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}
}
