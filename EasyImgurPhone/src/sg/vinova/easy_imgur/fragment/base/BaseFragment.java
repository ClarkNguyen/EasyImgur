package sg.vinova.easy_imgur.fragment.base;

import android.content.Context;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragment;

public class BaseFragment extends SherlockFragment {
	
	public Context mContext;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.mContext = getActivity();
	}

}
