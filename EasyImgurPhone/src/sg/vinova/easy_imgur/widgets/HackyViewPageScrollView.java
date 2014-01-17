package sg.vinova.easy_imgur.widgets;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class HackyViewPageScrollView extends ViewPager {

	public HackyViewPageScrollView(Context context) {
		super(context);
	}

	public HackyViewPageScrollView(Context context, AttributeSet attr) {
		super(context, attr);

	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// Tell our parent to stop intercepting our events!
        boolean ret = super.onInterceptTouchEvent(ev);
        if (ret) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        return ret;
	}

}
