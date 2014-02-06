package sg.vinova.easy_imgur.fragment.meme;

import sg.vinova.easy_imgur.activity.R;
import sg.vinova.easy_imgur.fragment.base.BaseFragment;
import sg.vinova.easy_imgur.models.MGallery;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("ValidFragment")
public class MemesArticleFragment extends BaseFragment {
	
	// tag
	public static final String TAG = "MemesArticleFragment";
	
	// Model
	private MGallery mGallery;
	
	// UI component
	private ImageView ivThumb;
	private TextView tvUps;
	private TextView tvDowns;
	private TextView tvScore;
	private TextView tvTitle;
	private TextView tvAuthor;
	
	public MemesArticleFragment(MGallery mGallery) {
		this.mGallery = mGallery;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.meme_article_fragment, container, false);
		
		findViews(view);
		isArticle = true;
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
	}
	


	private void findViews(View view) {
		// connect to UI component
		tvTitle = (TextView) view.findViewById(R.id.tvTitle);
		tvAuthor = (TextView) view.findViewById(R.id.tvAuthor);
		ivThumb = (ImageView) view.findViewById(R.id.ivThumb);
		tvUps = (TextView) view.findViewById(R.id.tvUpCount);
		tvDowns = (TextView) view.findViewById(R.id.tvDownCount);
		tvScore = (TextView) view.findViewById(R.id.tvScore);
		
		tvTitle.setText(mGallery.getTitle());
		tvAuthor.setText("By " + mGallery.getAccountUrl());
		tvUps.setText(mGallery.getUps() + "");
		tvDowns.setText(mGallery.getDowns() + "");
		tvScore.setText(mGallery.getScore() + "");
		imageLoader.displayImage(mGallery.getLink(), ivThumb, options);
	}
}
