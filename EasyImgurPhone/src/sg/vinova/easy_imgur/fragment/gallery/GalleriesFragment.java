package sg.vinova.easy_imgur.fragment.gallery;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import sg.vinova.easy_imgur.activity.R;
import sg.vinova.easy_imgur.base.Constant;
import sg.vinova.easy_imgur.base.DataParsingController;
import sg.vinova.easy_imgur.fragment.base.BaseFragment;
import sg.vinova.easy_imgur.models.MGallery;
import sg.vinova.easy_imgur.networking.ImgurAPI;
import uk.co.senab.actionbarpulltorefresh.extras.actionbarsherlock.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import android.content.Context;
import android.os.Bundle;
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

import com.android.volley.Response;
import com.android.volley.Response.Listener;

public class GalleriesFragment extends BaseFragment implements
		OnRefreshListener, OnItemClickListener {

	// TAG
	public static final String TAG = "GalleriesFragment";

	// ListView galleries
	private ListView lvGalleries;

	// List data
	private List<MGallery> galleries;

	// List adapter
	private GalleryAdapter adapter;

	private boolean isMore;

	private PullToRefreshLayout mPullToRefreshLayout;

	public GalleriesFragment() {
		galleries = new ArrayList<MGallery>();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.galleries_fragment, container,
				false);
		findViews(view);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (galleries.isEmpty()) {
			getAllGalleries();
		}
	}

	private void findViews(View view) {
		// Connect to UI component
		lvGalleries = (ListView) view.findViewById(R.id.lv_galleries);
		adapter = new GalleryAdapter(mContext, R.layout.row_gallery, galleries);
		lvGalleries.setAdapter(adapter);

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
					if (isMore) {
						isMore = false;
						page++;
						getAllGalleries();
					}
				}
			}
		});
		lvGalleries.setOnItemClickListener(this);

		// pull to refresh
		mPullToRefreshLayout = (PullToRefreshLayout) view
				.findViewById(R.id.ptr_layout);
		ActionBarPullToRefresh.from(getActivity()).allChildrenArePullable()
				.listener(this).setup(mPullToRefreshLayout);
	}

	@Override
	public void onRefreshStarted(View view) {
		page = 0;
		getAllGalleries();
	}

	private void getAllGalleries() {
		ImgurAPI.getClient().getAllGallery(mContext,
				Constant.PARAM_TYPE_SECTION_HOT, page, null, null, true,
				getListener(), getErrorListener());
	}

	private Response.Listener<JSONObject> getListener() {
		return new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject json) {
				isMore = true;
				List<MGallery> lstTmp = DataParsingController
						.parseGalleries(json);
				if (page == 0) {
					galleries.clear();
				}
				galleries.addAll(lstTmp);
				adapter.notifyDataSetChanged();
				mPullToRefreshLayout.setRefreshComplete();
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
				holder.tvTitle = (TextView) row.findViewById(R.id.tvTitle);
				holder.ivThumb = (ImageView) row.findViewById(R.id.ivThumb);
				holder.ibGifPlay = (ImageButton) row.findViewById(R.id.ibGifPlay);

				row.setTag(holder);
			} else {
				holder = (GalleryHolder) row.getTag();
			}

			// fill data to row
			holder.tvTitle.setText(mGallery.getTitle());
			if (!mGallery.isAlbum()) {
				if (mGallery.isAnimated()) {
					holder.ibGifPlay.setVisibility(View.VISIBLE);
					holder.ivThumb.setImageDrawable(getResources().getDrawable(
							R.drawable.bg_default));
//					Ion.with(holder.ivThumb).load(mGallery.getLink());
//					holder.ivThumb.setImageDrawable(getResources().getDrawable(
//							R.drawable.bg_default));
//					holder.ibGifPlay.setOnClickListener(new OnClickListener() {
//						
//						@Override
//						public void onClick(View v) {
//							holder.ibGifPlay.setVisibility(View.GONE);
//							Ion.with(holder.ivThumb).load(mGallery.getLink());
//						}
//					});
				} else {
					holder.ibGifPlay.setVisibility(View.GONE);
					imageLoader.displayImage(mGallery.getLink(),
							holder.ivThumb, options);
				}
			} else {
				holder.ibGifPlay.setVisibility(View.GONE);
				holder.ivThumb.setImageDrawable(getResources().getDrawable(
						R.drawable.bg_default));
			}

			return row;
		}

	}

	static class GalleryHolder {
		public TextView tvTitle;
		public ImageView ivThumb;
		public ImageButton ibGifPlay;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long itemId) {
		switchContent(new GalleriesArticleFragment(galleries.get(position)), true, GalleriesArticleFragment.TAG);
	}
}
