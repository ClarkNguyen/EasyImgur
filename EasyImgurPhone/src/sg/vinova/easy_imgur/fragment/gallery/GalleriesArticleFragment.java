package sg.vinova.easy_imgur.fragment.gallery;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import sg.vinova.easy_imgur.activity.R;
import sg.vinova.easy_imgur.base.DataParsingController;
import sg.vinova.easy_imgur.fragment.base.BaseFragment;
import sg.vinova.easy_imgur.interfaces.TokenHandle;
import sg.vinova.easy_imgur.models.MGallery;
import sg.vinova.easy_imgur.networking.ImgurAPI;
import sg.vinova.easy_imgur.utilities.LogUtility;
import sg.vinova.easy_imgur.utilities.StringUtility;
import sg.vinova.easy_imgur.utilities.TextRefineUtil;
import sg.vinova.easy_imgur.widgets.HackyViewPageScrollView;
import sg.vinova.easy_imgur.widgets.ViewImagePopupWindow;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.koushikdutta.ion.Ion;

@SuppressLint({ "ValidFragment", "NewApi" })
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
	private HackyViewPageScrollView pagerContent;
	private ListImagesAdapter listImageAdapter;
	private TextView textDetailGallery;
	
	private RelativeLayout rlSocial;
	private TextView tvPoints;
	private TextView tvLink;
	private TextView textLinkDetail;
	private TextView textLinkDirect;
	private ImageView ivFavourite;
	
	private LinearLayout llUps;
	private LinearLayout llDowns;
	private TextView tvUps;
	private TextView tvDowns;
	private ImageView ivUps;
	private ImageView ivDowns;
	
	private TextView tvDescription;
	
	private RelativeLayout.LayoutParams mLayoutParams;
	
	private String shortLink;
	
	// Vlag for check if user is viewing detail of album
	private boolean isExploreDetail = false;
	
	// Value to hold current album's image
	private int currentImage = 0;
	
	public GalleriesArticleFragment(MGallery mGallery) {
		this.mGallery = mGallery;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Settings
		View view = inflater.inflate(R.layout.fragment_detail, container, false);
		mLayoutParams = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
		        ViewGroup.LayoutParams.WRAP_CONTENT);
		findViews(view);
		
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		// Get detail for gallery
		if ((mGallery.getImages() == null || mGallery.getImages().size() == 0) && mGallery.isAlbum()) {
			List<MGallery> listImages = new ArrayList<MGallery>();
			mGallery.setImages(listImages);
			getDetailForGallery();
		} else {
			fillViews();
		}
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
		rlSocial = (RelativeLayout) view.findViewById(R.id.rl_social);
		ivContent = (ImageView) view.findViewById(R.id.iv_content);
		pagerContent = (HackyViewPageScrollView) view.findViewById(R.id.pager_content);
		textDetailGallery = (TextView) view.findViewById(R.id.text_detail_gallery);
		
		mLayoutParams.addRule(RelativeLayout.BELOW, R.id.iv_content);
		rlSocial.setLayoutParams(mLayoutParams);
		
		// If gallery is an image
		if (!mGallery.isAlbum()) {
			ivContent.setOnClickListener(this);
			if (mGallery.isAnimated() && mGallery.getHeight() != 0) {
				ivContent.getLayoutParams().height = mGallery.getHeight();
			}
			
		// It's an album else
		} else {
			ivContent.getLayoutParams().height = 450;
			ivContent.setVisibility(View.VISIBLE);
			textDetailGallery.setVisibility(View.VISIBLE);
			pagerContent.setVisibility(View.GONE);
			textDetailGallery.setOnClickListener(this);
		}
		
		/**
		 * Info
		 */
		tvPoints = (TextView) view.findViewById(R.id.tv_points);
		tvLink = (TextView) view.findViewById(R.id.tv_link);
		textLinkDetail = (TextView) view.findViewById(R.id.text_link_detail);
		textLinkDirect = (TextView) view.findViewById(R.id.text_link_direct);
		ivFavourite = (ImageView) view.findViewById(R.id.iv_favourite);
		textLinkDetail.setOnClickListener(this);
		textLinkDirect.setOnClickListener(this);
		ivFavourite.setOnClickListener(this);
		
		/**
		 * Voting
		 */
		llUps = (LinearLayout) view.findViewById(R.id.ll_ups);
		llDowns = (LinearLayout) view.findViewById(R.id.ll_downs);
		tvUps = (TextView) view.findViewById(R.id.tv_ups);
		tvDowns = (TextView) view.findViewById(R.id.tv_downs);
		ivUps = (ImageView) view.findViewById(R.id.iv_ups);
		ivDowns = (ImageView) view.findViewById(R.id.iv_downs);
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
			if (mGallery.isAnimated()) {
				Ion.with(ivContent).load(mGallery.getLink());
			} else {
				imageLoader.displayImage(mGallery.getLink(), ivContent, options);	
			}
		} else {
			imageLoader.displayImage(mGallery.getImages().get(0).getLink(), ivContent, options);	
		}
		
		/**
		 * Info
		 */
		tvPoints.setText(mGallery.getScore()+"");
		handleLink(mGallery.getLink());
		tvLink.setText(shortLink);
		
		if (mGallery.isFavorite()) {
			ivFavourite.setImageResource(R.drawable.ic_favorite_positive);
		} else {
			ivFavourite.setImageResource(R.drawable.ic_favorite_negative);
		}
		
		if (mGallery.isAlbum() && !isExploreDetail) {
			toggleDirectLink();
		}
		
		/**
		 * Voting
		 */
		tvUps.setText(mGallery.getUps()+"");
		tvDowns.setText(mGallery.getDowns()+"");
		
		if (mGallery.getVote() != null) {
			if (mGallery.getVote().equals("up")) {
				ivUps.setImageResource(R.drawable.ic_ups_positive);
			} else if (mGallery.getVote().equals("down") || mGallery.getVote().equals("veto")) {
				ivDowns.setImageResource(R.drawable.ic_downs_positive);
			}
		}
		
		/**
		 * Description
		 */
		tvDescription.setText(TextRefineUtil.refineString(mGallery.getDescription()));
	}
	
	/**
	 * handle link
	 */
	private void handleLink(String link) {
		shortLink = TextRefineUtil.refineString(link);
		shortLink = StringUtility.removeString("http://", shortLink);
		shortLink = StringUtility.removeString(".jpg", shortLink);
		shortLink = StringUtility.removeString(".png", shortLink);
		shortLink = StringUtility.removeString(".gif", shortLink);
	}
	
	/**
	 * Fill the detail of an image
	 * @param imageId
	 */
	private void fillImageDetail(MGallery image) {
		/**
		 * Info
		 */
		handleLink(image.getLink());
		tvLink.setText(shortLink);
		
		if (image.isFavorite()) {
			ivFavourite.setImageResource(R.drawable.ic_favorite_positive);
		} else {
			ivFavourite.setImageResource(R.drawable.ic_favorite_negative);
		}
	}
	
	/**
	 * Get detail content for a gallery
	 */
	private void getDetailForGallery() {
		if (mGallery != null) {
			ImgurAPI.getClient().getDetailGallery(mContext, mGallery.getId(), getListener(), getErrorListener(new TokenHandle() {
				
				@Override
				public void onRefreshSuccess() {
					getDetailForGallery();
				}
				
				@Override
				public void onRefreshFailed() {
				}
			}));
		}
	}
	
	/**
	 * Get detail content for an image
	 * @param imageId
	 */
	private void getDetailForImage(final MGallery image) {
		if (!image.isExplored()) {
			ivFavourite.setClickable(false);
			ImgurAPI.getClient().getDetailImage(mContext, image.getId(), getListener(), getErrorListener(new TokenHandle() {
				
				@Override
				public void onRefreshSuccess() {
					getDetailForImage(image);
				}
				
				@Override
				public void onRefreshFailed() {	
				}
			}));
		} else {
			if (image.isFavorite()) {
				ivFavourite.setImageResource(R.drawable.ic_favorite_positive);
			} else {
				ivFavourite.setImageResource(R.drawable.ic_favorite_negative);
			}
		}
	}
	
	public void getDetailAfterVote(boolean isAlbum) {
		if (isAlbum) {
			ImgurAPI.getClient().getDetailGallery(mContext, mGallery.getId(), getAfterVoteListener(), getErrorListener(new TokenHandle() {
				
				@Override
				public void onRefreshSuccess() {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onRefreshFailed() {
					// TODO Auto-generated method stub
					
				}
			}));
		} else {
			ImgurAPI.getClient().getDetailImage(mContext, mGallery.getId(), getAfterVoteListener(), getErrorListener(new TokenHandle() {
				
				@Override
				public void onRefreshSuccess() {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onRefreshFailed() {
					// TODO Auto-generated method stub
					
				}
			}));
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
				MGallery gallery = DataParsingController.parseGallery(json);
				if (!isExploreDetail) {
					mGallery.setImages(gallery.getImages());
					fillViews();
				} else {
					mGallery.getImages().get(currentImage).setFavorite(gallery.isFavorite());
					ivFavourite.setClickable(true);
					if (gallery.isFavorite()) {
						ivFavourite.setImageResource(R.drawable.ic_favorite_positive);
					} else {
						ivFavourite.setImageResource(R.drawable.ic_favorite_negative);
					}
				}
			}
		};
	}
	
	private Response.Listener<JSONObject> getAfterVoteListener() {
		return new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject json) {
				MGallery gallery = DataParsingController.parseGallery(json);
				tvUps.setText(gallery.getUps()+"");
				tvDowns.setText(gallery.getDowns()+"");
			}
		};
	}

	@Override
	public void onClick(View v) {
		if (v == ivContent) {
			showFullImage(mGallery.getLink());
			
		} else if (v == textLinkDetail) {
			setClipboard("link", shortLink);
			
		} else if (v == textLinkDirect) {
			if (!isExploreDetail){
				setClipboard("direct", mGallery.getLink());
			} else {
				setClipboard("direct", mGallery.getImages().get(currentImage).getLink());
			}
			
		} else if (v == ivFavourite) {
			toggleFavorite();
			if (mGallery.isAlbum()) {
				if (!isExploreDetail) {
					postFavoriteAlbum();
				} else {
					postFavoriteImage(mGallery.getImages().get(currentImage).getId());
				}
			} else {
				postFavoriteImage(mGallery.getId());
			}
		} else if (v == textDetailGallery) {
			isExploreDetail = true;
			toggleDirectLink();
			
			textDetailGallery.setVisibility(View.GONE);
			ivContent.setVisibility(View.GONE);
			pagerContent.setVisibility(View.VISIBLE);
			
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT,
			        ViewGroup.LayoutParams.WRAP_CONTENT);
			lp.addRule(RelativeLayout.BELOW, R.id.pager_content);
			rlSocial.setLayoutParams(lp);
			
			getDetailForImage(mGallery.getImages().get(0));
			fillImageDetail(mGallery.getImages().get(0));
			mGallery.getImages().get(0).setExplored(true);
			
			pagerContent.setOnPageChangeListener(new OnPageChangeListener() {
				
				@Override
				public void onPageSelected(int arg0) {
					/*MGallery image = listImages.get(arg0);
					pagerContent.getLayoutParams().height = image.getHeight();*/
					currentImage = arg0;
					getDetailForImage(mGallery.getImages().get(arg0));
					fillImageDetail(mGallery.getImages().get(arg0));
					mGallery.getImages().get(arg0).setExplored(true);
					LogUtility.e(TAG, mGallery.getImages().get(arg0).isExplored());
				}
				
				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {
				}
				
				@Override
				public void onPageScrollStateChanged(int arg0) {
				}
			});
			
			listImageAdapter = new ListImagesAdapter(mGallery.getImages());
			pagerContent.setAdapter(listImageAdapter);
		} else if (v == llUps) {
			if (mGallery.getVote() != null) {
				if (mGallery.getVote().equals("up")) {
					Toast.makeText(mContext, "You had already voted up!", Toast.LENGTH_SHORT).show();
				} else {
					toggleVote(true);
				}
			} else {
				toggleVote(true);
			}

		} else if (v == llDowns) {
			if (mGallery.getVote() != null) {
				if (mGallery.getVote().equals("down")) {
					Toast.makeText(mContext, "You had already voted down!", Toast.LENGTH_SHORT).show();
				} else {
					toggleVote(false);
				}
			} else {
				toggleVote(false);
			}
		}
	}
	
	/**
	 * Send a voting request to specified gallery
	 * @param galleryId
	 * @param isUp
	 */
	private void postVoteGallery(final String galleryId, final boolean isUp) {
		ImgurAPI.getClient().voteGallery(mContext, galleryId, isUp, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject jsonObj) {
				handleOnVoteSuccess(isUp, "gallery", jsonObj);
			}
		}, getErrorListener(new TokenHandle() {
			
			@Override
			public void onRefreshSuccess() {
				postVoteGallery(galleryId, isUp);
			}
			
			@Override
			public void onRefreshFailed() {
				Toast.makeText(mContext, "Got something wrong, please try again.", Toast.LENGTH_SHORT).show();
			}
		}));
	}
	
	/**
	 * Send a favorite request to specified album
	 */
	private void postFavoriteAlbum() {
		ImgurAPI.getClient().favoriteAlbum(mContext, mGallery.getId(), new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject arg0) {
				handleOnFavoriteSuccess("album", arg0);
			}
		}, getErrorListener(new TokenHandle() {
			
			@Override
			public void onRefreshSuccess() {
				postFavoriteAlbum();
			}
			
			@Override
			public void onRefreshFailed() {
				toggleFavorite();
				Toast.makeText(mContext, "Got something wrong, please try again.", Toast.LENGTH_SHORT).show();
			}
		}));	
	}
	
	/**
	 * Send a favorite request to specified image
	 */
	private void postFavoriteImage(final String imageId) {
		ImgurAPI.getClient().favoriteImage(mContext, imageId, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject arg0) {
				handleOnFavoriteSuccess("image", arg0);
			}
		}, getErrorListener(new TokenHandle() {
			
			@Override
			public void onRefreshSuccess() {
				postFavoriteImage(imageId);
			}
			
			@Override
			public void onRefreshFailed() {
				toggleFavorite();
				Toast.makeText(mContext, "Got something wrong, please try again.", Toast.LENGTH_SHORT).show();
			}
		}));
	}
	
	/**
	 * Toggle the favorite button
	 */
	private void toggleFavorite() {
		if (!isExploreDetail) {
			if (mGallery.isFavorite()) {
				mGallery.setFavorite(false);
				ivFavourite.setImageResource(R.drawable.ic_favorite_negative);
			} else {
				mGallery.setFavorite(true);
				ivFavourite.setImageResource(R.drawable.ic_favorite_positive);
			}
		} else {
			if (mGallery.getImages().get(currentImage).isFavorite()) {
				mGallery.getImages().get(currentImage).setFavorite(false);
				ivFavourite.setImageResource(R.drawable.ic_favorite_negative);
			} else {
				mGallery.getImages().get(currentImage).setFavorite(true);
				ivFavourite.setImageResource(R.drawable.ic_favorite_positive);
			}
		}
	}
	
	/**
	 * Toggle vote buttons
	 */
	private void toggleVote(boolean isUp) {
		getDetailAfterVote(mGallery.isAlbum());
		
		String vote;
		if (isUp) {
			vote = "up";
			ivUps.setImageResource(R.drawable.ic_ups_positive);
			ivDowns.setImageResource(R.drawable.ic_downs_negative);
		} else {
			vote = "down";
			ivUps.setImageResource(R.drawable.ic_ups_negative);
			ivDowns.setImageResource(R.drawable.ic_downs_positive);
		}
		
		mGallery.setVote(vote);
		postVoteGallery(mGallery.getId(), isUp);
	}
	
	/**
	 * Toggle direct link
	 */
	private void toggleDirectLink() {
		if (!isExploreDetail) {
			textLinkDirect.setClickable(false);
			textLinkDirect.setTextColor(getResources().getColor(R.color.text_link_negative));
		} else {
			textLinkDirect.setClickable(true);
			textLinkDirect.setTextColor(getResources().getColor(R.color.text_link_positive));
		}
	}
	
	/**
	 * On favorite success
	 * @param type
	 * @param json
	 */
	private void handleOnFavoriteSuccess(String type, JSONObject json) {
		boolean isSuccess;
		String isFavorite;
		try {
			isSuccess = json.getBoolean("success");
			isFavorite = json.getString("data");
			if (isSuccess) {
				if (isFavorite.equals("favorited")) {
					Toast.makeText(mContext, "You added an " + type + " to favorite.", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(mContext, "You removed an " + type + " from favorite.", Toast.LENGTH_SHORT).show();
				}
			}
		} catch (JSONException e) {
			toggleFavorite();
			Toast.makeText(mContext, "Got something wrong, please try again.", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}
	
	/**
	 * On vote success
	 * @param isUp
	 * @param type
	 * @param json
	 */
	private void handleOnVoteSuccess(boolean isUp, String type, JSONObject json) {
		boolean isSuccess;
		try {
			isSuccess = json.getBoolean("success");
			if (isSuccess) {
				if (isUp) {
					Toast.makeText(mContext, "You voted up an " + type + ".", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(mContext, "You voted down an " + type + ".", Toast.LENGTH_SHORT).show();
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Set a text to clipboard
	 */
	@SuppressWarnings("deprecation")
	private void setClipboard(String tag, String text) {
		int sdk = android.os.Build.VERSION.SDK_INT;
		if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager)
            		getSherlockActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
		} else {
			ClipboardManager clipboard = (ClipboardManager) getSherlockActivity().getSystemService(Context.CLIPBOARD_SERVICE); 
			ClipData clip = ClipData.newPlainText(tag, text);
			clipboard.setPrimaryClip(clip);
		}
		Toast.makeText(mContext, text + " copied.", Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * Show popup display full image
	 * @param imageUrl
	 */
	private void showFullImage(String imageUrl) {
		LayoutInflater inflater = (LayoutInflater) getSherlockActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.popup_view_image, null, false);
		
		new ViewImagePopupWindow(this, imageUrl, WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.MATCH_PARENT,
				view, getSherlockActivity().getWindow().getDecorView().getRootView());
	}
	
	/**
	 * Class for display list image of a gallery
	 * @author May
	 *
	 */
	public class ListImagesAdapter extends PagerAdapter {

		private List<MGallery> listImages;
		
		public ListImagesAdapter(List<MGallery> mImage) {
			this.listImages = mImage;
		}
		
		@Override
		public int getCount() {
			return listImages.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}
		
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View view = LayoutInflater.from(mContext).inflate(R.layout.row_image, container, false);
			final MGallery image = listImages.get(position);
			
			ImageHolder holder = new ImageHolder();
			holder.ivContent = (ImageView) view.findViewById(R.id.iv_content);
			holder.ivContent.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					showFullImage(image.getLink());
				}
			});
			
			imageLoader.displayImage(image.getLink(), holder.ivContent, options);
			
			((ViewPager) container).addView(view);
			return view;
		}
		
		@Override
		public float getPageWidth(int position) {
			return 1f;
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
			super.restoreState(state, loader);
		}

		@Override
		public Parcelable saveState() {
			return super.saveState();
		}

		@Override
		public void startUpdate(View container) {
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}
	}
	
	public static class ImageHolder {
		ImageView ivContent;
	}
}
