package sg.vinova.easy_imgur.fragment.login;

import sg.vinova.easy_imgur.activity.R;
import sg.vinova.easy_imgur.base.Constant;
import sg.vinova.easy_imgur.fragment.base.BaseFragment;
import sg.vinova.easy_imgur.models.MUser;
import sg.vinova.easy_imgur.utilities.LogUtility;
import sg.vinova.easy_imgur.utilities.TokenUtility;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

public class LoginFragment extends BaseFragment {

	// Tag
	public static final String TAG = "LoginFragment";

	// UI component
	private WebView wvLogin;
	
	// Loading component
	private LinearLayout viewLoading;
	private TextView tvLoading;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.login_fragment, container, false);

		findViews(view);

		return view;
	}

	private void findViews(View view) {
		viewLoading = (LinearLayout) view.findViewById(R.id.viewLoading);
		tvLoading = (TextView) view.findViewById(R.id.tvLoading);
		wvLogin = (WebView) view.findViewById(R.id.wvLogin);
		String url = "https://api.imgur.com/oauth2/authorize?client_id="
				+ Constant.CLIENT_ID + "&response_type=token";
		
		WebViewClient wvClient = new WebViewClient(){
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
			
			@Override
			public void onLoadResource(WebView view, String url) {
				Uri uri = Uri.parse(url);
				if (url.startsWith("https://imgur.com/#")) {
					Gson gson = new Gson();
					String paramString = uri.toString().replaceAll("https://imgur.com/#", "");
					if (!TextUtils.isEmpty(paramString)) {
						MUser mUser = new MUser();
						String[] params = paramString.split("&");
						for (int i = 0; i < params.length; i++) {
							if (params[i].split("=").length == 2) {
								String data = params[i].split("=")[1];
								if (params[i].startsWith("access_token")) {
									mUser.setAccessToken(data);
								} else if (params[i].startsWith("refresh_token")) {
									mUser.setRefreshToken(data);
								} else if (params[i].startsWith("token_type")) {
									mUser.setTokenType(data);
								} else if (params[i].startsWith("account_username")) {
									mUser.setUserName(data);
								} else if (params[i].startsWith("expires_in")) {
									mUser.setExpires(Integer.valueOf(data));
								}
							}
						}
						LogUtility.e(TAG, "MUser: " + gson.toJson(mUser));
						TokenUtility.saveUser(mContext, gson.toJson(mUser));
					}
				} else if (!TextUtils.isEmpty(uri.getQueryParameter("error"))) {
					// TODO process if click deny permission
					LogUtility.e(TAG, "Deny permission");
				}
			}
			
		};
		wvLogin.setWebViewClient(wvClient);
		wvLogin.setWebChromeClient(new CustomWebChromeClient());
		wvLogin.loadUrl(url);
	}
	
	private class CustomWebChromeClient extends WebChromeClient {
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			super.onProgressChanged(view, newProgress);
			viewLoading.setVisibility(View.VISIBLE);
			tvLoading.setText("Loading..." + newProgress + "%");

			if (newProgress == 100) {
				viewLoading.setVisibility(View.GONE);
			}
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}
}
