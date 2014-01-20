package sg.vinova.easy_imgur.fragment.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import sg.vinova.easy_imgur.activity.R;
import sg.vinova.easy_imgur.fragment.base.BaseFragment;

public class HomeFragment extends BaseFragment {
	
	// tag 
	public static final String TAG = "HomeFragment";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.home_fragment, container, false);
		
		findViews(view);
		
		return view;
	}

	private void findViews(View view) {
		// TODO Auto-generated method stub
		
	}

}
