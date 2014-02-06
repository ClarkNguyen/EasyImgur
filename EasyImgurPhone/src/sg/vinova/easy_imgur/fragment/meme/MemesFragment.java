package sg.vinova.easy_imgur.fragment.meme;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import sg.vinova.easy_imgur.activity.R;
import sg.vinova.easy_imgur.base.DataParsingController;
import sg.vinova.easy_imgur.fragment.base.BaseFragment;
import sg.vinova.easy_imgur.interfaces.TokenHandle;
import sg.vinova.easy_imgur.models.MGallery;
import sg.vinova.easy_imgur.networking.ImgurAPI;
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
import com.android.volley.Response;

public class MemesFragment extends BaseFragment implements OnItemClickListener, OnRefreshListener {

	// tag
	public static final String TAG = "MemesFragment";

	// ListView memes
	private ListView lvMemes;

	// List data
	private List<MGallery> memes;

	// adapter
	private MemeAdapter adapter;

	// pull to refresh layout
	private PullToRefreshLayout mPullToRefreshLayout;

	public MemesFragment() {
		memes = new ArrayList<MGallery>();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.galleries_fragment, container,
				false);
		findViews(view);
		isArticle = false;
		setHasOptionsMenu(true);
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
	}

	private void findViews(View view) {
		lvMemes = (ListView) view.findViewById(R.id.lv_galleries);
		adapter = new MemeAdapter(mContext, R.layout.row_gallery, memes);

		lvMemes.setAdapter(adapter);
		lvMemes.setOnItemClickListener(this);
		
		lvMemes.setOnScrollListener(new OnScrollListener() {
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
						page++;
						getMemeData();
					}
				}
			}
		});

		// pull to refresh
		mPullToRefreshLayout = (PullToRefreshLayout) view
				.findViewById(R.id.ptr_layout);
		ActionBarPullToRefresh.from(getActivity()).allChildrenArePullable()
				.listener(this).setup(mPullToRefreshLayout);
	}
	
	@Override
	public void onRefreshStarted(View view) {
		page = 0;
		getMemeData();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (memes.isEmpty()) {
			getMemeData();
		}
	}

	private void getMemeData() {
		ImgurAPI.getClient().getAllMeme(mContext, null, null, page,
				getMemeListener(), getErrorListener(new TokenHandle() {

					@Override
					public void onRefreshSuccess() {
						getMemeData();
					}

					@Override
					public void onRefreshFailed() {
						// TODO Auto-generated method stub

					}
				}));
	}

	private Response.Listener<JSONObject> getMemeListener() {
		return new Response.Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject json) {
				List<MGallery> memesTmp = DataParsingController
						.parseGalleries(json);
				if (page == 0) {
					memes.clear();
				}
				isMoreData = true;
				memes.addAll(memesTmp);
				adapter.notifyDataSetChanged();
				mPullToRefreshLayout.setRefreshComplete();
			}
		};
	}

	private class MemeAdapter extends ArrayAdapter<MGallery> {

		private List<MGallery> galleries;
		private MemeHolder holder;

		public MemeAdapter(Context context, int resource, List<MGallery> objects) {
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
				holder = new MemeHolder();
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
				holder = (MemeHolder) row.getTag();
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
				holder.ivThumb.setImageResource(R.drawable.bg_default);
				// if (mGallery.getImages() != null &&
				// !mGallery.getImages().isEmpty()) {
				// imageLoader.displayImage(mGallery.getImages().get(0).getLink(),
				// holder.ivThumb, options);
				// }
			}

			return row;
		}

	}

	static class MemeHolder {
		public EllipsizingTextView tvTitle;
		public ImageView ivThumb;
		public ImageButton ibGifPlay;
		public TextView tvUpCount;
		public TextView tvDownCount;
		public TextView tvScore;
		public TextView tvTime;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long itemId) {
		switchContent(new MemesArticleFragment(memes.get(position)), true, MemesArticleFragment.TAG);
	}

}
