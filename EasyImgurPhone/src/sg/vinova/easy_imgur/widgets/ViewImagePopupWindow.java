package sg.vinova.easy_imgur.widgets;
import sg.vinova.easy_imgur.activity.R;
import sg.vinova.easy_imgur.fragment.base.BaseFragment;
import uk.co.senab.photoview.PhotoViewAttacher;
import uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener;
import android.annotation.SuppressLint;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.PopupWindow;

@SuppressLint("ViewConstructor")
public class ViewImagePopupWindow extends PopupWindow implements OnClickListener {

	private View contentView;
	
	private ImageView ivContent;
	
	private PhotoViewAttacher mAttacher;
	
	private BaseFragment mFragemnt;
	
	private String url;

	@SuppressWarnings("deprecation")
	public ViewImagePopupWindow(BaseFragment fragment, String url, int width, int height, View view, View parantView) {
		super(view, width, height, true);
		
		this.contentView = view;
		this.mFragemnt = fragment;
		this.url = url;
		
		findViews();

		//this.setAnimationStyle(R.style.AnimationPopupSlideVerticalUp);
		this.setOutsideTouchable(true);
		this.setBackgroundDrawable(new BitmapDrawable());
		this.showAtLocation(parantView, Gravity.CENTER, 0, 0);
	}

	/**
	 * Find all views
	 */
	private void findViews() {
		ivContent = (ImageView) contentView.findViewById(R.id.iv_content);
		ivContent.setOnClickListener(this);
		
		mAttacher = new PhotoViewAttacher(ivContent);
		mAttacher.setOnPhotoTapListener(new OnPhotoTapListener() {
			
			@Override
			public void onPhotoTap(View view, float x, float y) {
				// TODO Auto-generated method stub
				ViewImagePopupWindow.this.dismiss();
			}
		});
		
		mFragemnt.imageLoader.displayImage(url, ivContent, mFragemnt.options);
	}

	@Override
	public void onClick(View v) {
		this.dismiss();
	}

}
