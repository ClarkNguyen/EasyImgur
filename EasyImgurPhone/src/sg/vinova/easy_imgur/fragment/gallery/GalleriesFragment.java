package sg.vinova.easy_imgur.fragment.gallery;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import sg.vinova.easy_imgur.activity.R;
import sg.vinova.easy_imgur.base.Constant;
import sg.vinova.easy_imgur.base.DataParsingController;
import sg.vinova.easy_imgur.fragment.base.BaseFragment;
import sg.vinova.easy_imgur.interfaces.TokenHandle;
import sg.vinova.easy_imgur.models.MGallery;
import sg.vinova.easy_imgur.networking.ImgurAPI;
import sg.vinova.easy_imgur.utilities.LogUtility;
import sg.vinova.easy_imgur.widgets.EllipsizingTextView;
import uk.co.senab.actionbarpulltorefresh.extras.actionbarsherlock.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.android.volley.Response;
import com.android.volley.Response.Listener;

public class GalleriesFragment extends BaseFragment implements
		OnRefreshListener, OnItemClickListener, OnNavigationListener {

	// TAG
	public static final String TAG = "GalleriesFragment";

	// ListView galleries
	private ListView lvGalleries;

	// List all galleries data
	private List<MGallery> allGalleries;
	
	// List galleries load on list view
	private List<MGallery> galleriesOnList;
	
	// page item
	private int pageItem;

	// List adapter
	private GalleryAdapter adapter;

	// custom paging for load 10 item per scroll
	private int startIndex;
	private int endIndex;

	// pull to refresh layout
	private PullToRefreshLayout mPullToRefreshLayout;

	// current section
	private int currSectionPos;
	private String currSection;
	private String[] sections;

	public GalleriesFragment() {
		allGalleries = new ArrayList<MGallery>();
		galleriesOnList = new ArrayList<MGallery>();
		currSection = Constant.PARAM_TYPE_SECTION_HOT;
		currSectionPos = 0;
		pageItem = 0;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.galleries_fragment, container,
				false);
		findViews(view);
		isArticle = false;
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (allGalleries.isEmpty()) {
			getAllGalleries();
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		actionBar.setSelectedNavigationItem(currSectionPos);
	}

	private void findViews(View view) {
		// Connect to UI component
		lvGalleries = (ListView) view.findViewById(R.id.lv_galleries);
		adapter = new GalleryAdapter(mContext, R.layout.row_gallery, galleriesOnList);
		lvGalleries.setAdapter(adapter);
		
		lvGalleries.setOnItemClickListener(this);

		lvGalleries.setOnScrollListener(new OnScrollListener() {

			int currentState;

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				this.currentState = scrollState;
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;
				if (loadMore && currentState != SCROLL_STATE_IDLE) {
					if (isMoreData) {
						isMoreData = false;
						loadGalleriesOnList();
					}
				}
			}
		});

		// pull to refresh
		mPullToRefreshLayout = (PullToRefreshLayout) view
				.findViewById(R.id.ptr_layout);
		ActionBarPullToRefresh.from(getActivity()).allChildrenArePullable()
				.listener(this).setup(mPullToRefreshLayout);

		// setup drop down menu section
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		sections = getResources().getStringArray(R.array.menu_sections);
		ArrayAdapter<String> aAdpt = new ArrayAdapter<String>(mContext,
				android.R.layout.simple_list_item_1, android.R.id.text1,
				sections);
		actionBar.setListNavigationCallbacks(aAdpt, this);
	}

	@Override
	public void onRefreshStarted(View view) {
		page = 0;
		pageItem = 0;
		getAllGalleries();
	}
	
	/**
	 * Custom load 10 item to list per scroll
	 */
	private void loadGalleriesOnList() {
		startIndex = pageItem * Constant.ITEM_PER_PAGE;
		++pageItem;
		endIndex = pageItem * Constant.ITEM_PER_PAGE;
		
		LogUtility.e(TAG, "start: " + startIndex + "--- end: "+endIndex);
		
		if (endIndex >= allGalleries.size()) {
			page++;
			getAllGalleries();
			
		} else {
			for (int i = startIndex; i < endIndex; i++) {
				galleriesOnList.add(allGalleries.get(i));
			}
			isMoreData = true;
		}
		
		adapter.notifyDataSetChanged();
	}

	private void getAllGalleries() {
		LogUtility.e(TAG, "Load image in page: " + page);
		ImgurAPI.getClient().getAllGallery(mContext, currSection, page, null,
				null, true, getListener(), getErrorListener(new TokenHandle() {
					
					@Override
					public void onRefreshSuccess() {
						LogUtility.e(TAG, "Get data after refresh");
						getAllGalleries();
					}
					
					@Override
					public void onRefreshFailed() {
						// TODO Auto-generated method stub
						
					}
				}));
	}

	private Response.Listener<JSONObject> getListener() {
		return new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject json) {
				List<MGallery> lstTmp = DataParsingController
						.parseGalleries(json);
				if (page == 0) {
					allGalleries.clear();
					galleriesOnList.clear();
					allGalleries.addAll(lstTmp);
					loadGalleriesOnList();
					isMoreData = true;
					mPullToRefreshLayout.setRefreshComplete();
					return;
				} 
				if (page > 0) {
					allGalleries.addAll(lstTmp);
					LogUtility.e(TAG, "Load image in next page on list");
					for (int i = startIndex; i < endIndex; i++) {
						galleriesOnList.add(allGalleries.get(i));
					}
					adapter.notifyDataSetChanged();
					isMoreData = true;
					return;
				}
				
			}
		};
	}
	
	private void loadAlbums(String albumId, ImageView ivThumb, int position) {
		ImgurAPI.getClient().getAlbumDetails(mContext, albumId, getAlbumDetailsListener(ivThumb, position), getErrorListener(new TokenHandle() {
			
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
	
	private Response.Listener<JSONObject> getAlbumDetailsListener(final ImageView ivThumb, final int position) {
		return new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject json) {
				LogUtility.e(TAG, "Load first image in album");
				MGallery gallery = DataParsingController.parseGallery(json);
				if (gallery.getImagesCount() > 0 && !gallery.getImages().isEmpty()) {
					imageLoader.displayImage(gallery.getImages().get(0).getLink(), ivThumb, options);
					galleriesOnList.set(position, gallery);
				}
			}
		};
	}

	private class GalleryAdapter extends ArrayAdapter<MGallery> {

		private List<MGallery> galleries;
		private GalleryHolder holder;

		public GalleryAdapter(Context context, int resource,
				List<MGallery> objects) {
			super(context, resource, objects);
			this.galleries = objects;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			final MGallery mGallery = galleries.get(position);

			if (row == null) {
				row = LayoutInflater.from(mContext).inflate(
						R.layout.row_gallery, parent, false);
				holder = new GalleryHolder();
				holder.tvTitle = (EllipsizingTextView) row
						.findViewById(R.id.tvTitle);
				holder.tvTitle.setMaxLines(2);
				holder.ivThumb = (ImageView) row.findViewById(R.id.ivThumb);
				holder.ibGifPlay = (ImageButton) row
						.findViewById(R.id.ibGifPlay);
				holder.ibGifPlay.setFocusable(false);
				holder.tvUpCount = (TextView) row.findViewById(R.id.tvUpCount);
				holder.tvDownCount = (TextView) row
						.findViewById(R.id.tvDownCount);
				holder.tvTime = (TextView) row.findViewById(R.id.tvTime);
				holder.tvScore = (TextView) row.findViewById(R.id.tvScore);

				row.setTag(holder);
			} else {
				holder = (GalleryHolder) row.getTag();
			}

			// fill data to row
			holder.tvTitle.setText(mGallery.getTitle());
			holder.tvUpCount.setText(mGallery.getUps() + "");
			holder.tvDownCount.setText(mGallery.getDowns() + "");
			holder.tvScore.setText(mGallery.getScore() + "");
			holder.tvTime.setText(DateFormat.format("MM-dd-yyyy",
					Long.valueOf(mGallery.getDatetime()) * 1000));

			if (!mGallery.isAlbum()) {
				if (mGallery.isAnimated()) {
					holder.ibGifPlay.setVisibility(View.VISIBLE);
					imageLoader.displayImage(mGallery.getLink(),
							holder.ivThumb, options);
				} else {
					holder.ibGifPlay.setVisibility(View.GONE);
					imageLoader.displayImage(mGallery.getLink(),
							holder.ivThumb, options);
				}
			} else {
				holder.ibGifPlay.setVisibility(View.GONE);
				if (mGallery.getImages() != null && !mGallery.getImages().isEmpty()) {
					imageLoader.displayImage(mGallery.getImages().get(0).getLink(), holder.ivThumb, options);
				} else {
					loadAlbums(mGallery.getId(), holder.ivThumb, position);
				}
			}

			return row;
		}

	}

	static class GalleryHolder {
		public EllipsizingTextView tvTitle;
		public ImageView ivThumb;
		public ImageButton ibGifPlay;
		public TextView tvUpCount;
		public TextView tvDownCount;
		public TextView tvScore;
		public TextView tvTime;
	}

				
	public void onItemClick(AdapterView<?> parent, View view, int position, long itemId) {
		switchContent(new GalleriesArticleFragment(allGalleries.get(position)),
				true, GalleriesArticleFragment.TAG);
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		
		if (itemPosition != currSectionPos) {
			switch (itemPosition) {
			case 0:
				currSectionPos = 0;
				currSection = Constant.PARAM_TYPE_SECTION_HOT;
				break;

			case 1:
				currSectionPos = 1;
				currSection = Constant.PARAM_TYPE_SECTION_TOP;
				break;

			case 2:
				currSectionPos = 2;
				currSection = Constant.PARAM_TYPE_SECTION_USER;
				break;

			default:
				break;
			}
			page = 0;
			getAllGalleries();
			return true;
		}
		return false;
	}
}
