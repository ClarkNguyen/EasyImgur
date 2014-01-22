package sg.vinova.easy_imgur.activity;

import org.json.JSONObject;

import sg.vinova.easy_imgur.base.Constant;
import sg.vinova.easy_imgur.interfaces.TokenHandle;
import sg.vinova.easy_imgur.models.MUser;
import sg.vinova.easy_imgur.networking.ImgurAPI;
import sg.vinova.easy_imgur.utilities.LogUtility;
import sg.vinova.easy_imgur.utilities.TokenUtility;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Window;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

/**
 * Base activity for another activities in app
 * @author May
 *
 */
public class BaseActivity extends SherlockFragmentActivity {
	// TAG
	public static final String TAG = "BaseActivity";
	
	// Fragment
	protected Fragment mainContent;
	
	/**
	 * Settings for image loading
	 */
	protected ImageLoader imageLoader;
	protected DisplayImageOptions options;
	
	/**
	 * Action bar
	 */
	protected ActionBar actionBar;
	
	/**
	 * Value to save name of current fragment
	 */
	private String currentFragment;
	
	@Override
	protected void onCreate(Bundle arg0) {
		// Request use progress loading bar feature
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(arg0);
		
		setImageLoader();
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.bg_default)
				.showImageForEmptyUri(R.drawable.bg_default)
				.showImageOnFail(R.drawable.bg_default)
				.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
				.cacheInMemory(true).cacheOnDisc(true).build();
		
		actionBar = getSupportActionBar();
	}
	
	/**
	 * Initialize image loader
	 */
	private void setImageLoader() {
		imageLoader = ImageLoader.getInstance();
	}
	
	/**
	 * Get Image loader
	 * @return
	 */
	public ImageLoader getImageLoader() {
		return imageLoader;
	}
	
	/**
	 * Get Image option for image loader
	 * @return
	 */
	public DisplayImageOptions getImageLoaderOptions() {
		return options;
	}

	/**
	 * Listener for error response
	 * @return
	 */
	public Response.ErrorListener getErrorListener(final TokenHandle tokenHandle) {
		return new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError err) {
				showProgressBar(false);
				if (err.networkResponse != null) {
					if (err.networkResponse.statusCode == 403) {
						// handle refresh token
						ImgurAPI.getClient().getNewToken(BaseActivity.this, new Response.Listener<JSONObject>() {

							@Override
							public void onResponse(JSONObject json) {
								LogUtility.e(TAG, "RefreshToken: " + json.toString());
								if (json != null) {
									if (tokenHandle != null) {
										try {
											MUser mUser = TokenUtility.getUser(BaseActivity.this);
											mUser.setAccessToken(json.getString("access_token"));
											mUser.setExpires(json.getInt("expires_in"));
											mUser.setRefreshToken(json.getString("refresh_token"));
											mUser.setUserName(json.getString("account_username"));
											mUser.setTokenType(json.getString("token_type"));
											
											TokenUtility.saveUser(BaseActivity.this, new Gson().toJson(mUser));
											tokenHandle.onRefreshSuccess();
										} catch (Exception e) {
											LogUtility.e(TAG, "Parse token error");
										}
									}
								}
							}
						}, new Response.ErrorListener() {

							@Override
							public void onErrorResponse(VolleyError err) {
								LogUtility.e(TAG, "RefreshTokenError: " + err.networkResponse.statusCode);
								if (tokenHandle != null) {
									tokenHandle.onRefreshFailed();
								}
							}
						});
					}
				} else {
					handleOnError(err);
				}
			}
		};
	}
	
	/**
	 * Handle attitude on request data got error
	 */
	public void handleOnError(VolleyError err) {
		String message = err.toString();
		LogUtility.e(TAG, Constant.ERROR + message);
		if (message.contains(Constant.MESSAGE_NETWORK_UNREACHABLE)) {
			Toast.makeText(getBaseContext(), Constant.MESSAGE_NETWORK_UNREACHABLE, Toast.LENGTH_SHORT).show();
		} else if (message.contains(Constant.REQUEST_TIMEOUT)) {
			Toast.makeText(getBaseContext(), Constant.MESSAGE_REQUEST_TIMEOUT, Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(getBaseContext(), Constant.MESSSAGE_SERVER_ERROR, Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * Method to hide soft keyboard
	 */
	public void hideSoftKeyboard() {
		InputMethodManager inputMethodManager = (InputMethodManager) this
				.getSystemService(Activity.INPUT_METHOD_SERVICE);
		if (this.getCurrentFocus() != null)
			inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus()
					.getWindowToken(), 0);
	}
	
	/**
	 * Set current fragment
	 * @param fragmentTag
	 */
	public void setCurrentFragment(String fragmentTag) {
		this.currentFragment = fragmentTag;
	}
	
	/**
	 * Get current fragment
	 * @return
	 */
	public String getCurrentFragment() {
		return currentFragment;
	}
	
	/**
	 * Show the progress bar
	 * @param visible
	 */
	public void showProgressBar(boolean visible) {
		setProgressBarIndeterminateVisibility(visible);
	}
	
	/**
	 * Switch content of main view when user have just choosed a different kind
	 * of category from Left Menu
	 * 
	 * @param fragment
	 */
	public void switchContent(Fragment fragment, boolean addToBackstack) {
		if (fragment != null) {
			actionBar.setIcon(R.drawable.ic_launcher);
			mainContent = fragment;
			if (addToBackstack) {
				getSupportFragmentManager()
						.beginTransaction()
						// .setCustomAnimations(R.anim.slide_in_right,
						// R.anim.slide_out_left, R.anim.slide_in_left,
						// R.anim.slide_out_right)
						/*.setCustomAnimations(android.R.anim.fade_in,
								android.R.anim.fade_out,
								android.R.anim.fade_in, android.R.anim.fade_out)*/
						.replace(R.id.content_frame, fragment)
						.addToBackStack(null).commitAllowingStateLoss();

			} else {
				getSupportFragmentManager()
				.beginTransaction()
				// .setCustomAnimations(R.anim.slide_in_right,
				// R.anim.slide_out_left, R.anim.slide_in_left,
				// R.anim.slide_out_right)
				/*.setCustomAnimations(android.R.anim.fade_in,
						android.R.anim.fade_out,
						android.R.anim.fade_in, android.R.anim.fade_out)*/
				.replace(R.id.content_frame, fragment)
				.commitAllowingStateLoss();
			}

			actionBar.setDisplayHomeAsUpEnabled(addToBackstack);
		}

	}

	public void switchContent(Fragment fragment, boolean addToBackstack,
			boolean clearBackstack) {
		if (fragment != null) {
			actionBar.setIcon(R.drawable.ic_launcher);
			if (clearBackstack) {
				getSupportFragmentManager().popBackStack(null,
						FragmentManager.POP_BACK_STACK_INCLUSIVE);
			}
			mainContent = fragment;
			if (addToBackstack) {
				getSupportFragmentManager()
						.beginTransaction()
						// .setCustomAnimations(R.anim.slide_in_right,
						// R.anim.slide_out_left, R.anim.slide_in_left,
						// R.anim.slide_out_right)
						/*.setCustomAnimations(android.R.anim.fade_in,
								android.R.anim.fade_out,
								android.R.anim.fade_in, android.R.anim.fade_out)*/
						.replace(R.id.content_frame, fragment)
						.addToBackStack(null).commitAllowingStateLoss();

			} else {
				getSupportFragmentManager()
				.beginTransaction()
				// .setCustomAnimations(R.anim.slide_in_right,
				// R.anim.slide_out_left, R.anim.slide_in_left,
				// R.anim.slide_out_right)
				/*.setCustomAnimations(android.R.anim.fade_in,
						android.R.anim.fade_out,
						android.R.anim.fade_in, android.R.anim.fade_out)*/
				.replace(R.id.content_frame, fragment)
				.commitAllowingStateLoss();
			}

			actionBar.setDisplayHomeAsUpEnabled(addToBackstack);
		}
	}

	/**
	 * Switch content with fragment tag
	 * 
	 * @param fragment
	 * @param addToBackstack
	 * @param tag
	 */
	public void switchContent(Fragment fragment, boolean addToBackstack,
			String tag) {
		if (fragment != null) {
			actionBar.setIcon(R.drawable.ic_launcher);
			mainContent = fragment;
			if (addToBackstack) {
				getSupportFragmentManager()
						.beginTransaction()
						// .setCustomAnimations(R.anim.slide_in_right,
						// R.anim.slide_out_left, R.anim.slide_in_left,
						// R.anim.slide_out_right)
						/*.setCustomAnimations(android.R.anim.fade_in,
								android.R.anim.fade_out,
								android.R.anim.fade_in, android.R.anim.fade_out)*/
						.replace(R.id.content_frame, fragment, tag)
						.addToBackStack(null).commitAllowingStateLoss();

			} else {
				getSupportFragmentManager()
				.beginTransaction()
				// .setCustomAnimations(R.anim.slide_in_right,
				// R.anim.slide_out_left, R.anim.slide_in_left,
				// R.anim.slide_out_right)
				/*.setCustomAnimations(android.R.anim.fade_in,
						android.R.anim.fade_out,
						android.R.anim.fade_in, android.R.anim.fade_out)*/
				.replace(R.id.content_frame, fragment, tag)
				.commitAllowingStateLoss();
			}

			actionBar.setDisplayHomeAsUpEnabled(addToBackstack);
		}
	}

	public void switchContent(Fragment fragment, boolean addToBackstack,
			boolean clearBackstack, String tag) {
		if (fragment != null) {
			actionBar.setIcon(R.drawable.ic_launcher);
			if (clearBackstack) {
				getSupportFragmentManager().popBackStack(null,
						FragmentManager.POP_BACK_STACK_INCLUSIVE);
			}
			mainContent = fragment;
			if (addToBackstack) {
				getSupportFragmentManager()
						.beginTransaction()
						// .setCustomAnimations(R.anim.slide_in_right,
						// R.anim.slide_out_left, R.anim.slide_in_left,
						// R.anim.slide_out_right)
						/*.setCustomAnimations(android.R.anim.fade_in,
								android.R.anim.fade_out,
								android.R.anim.fade_in, android.R.anim.fade_out)*/
						.replace(R.id.content_frame, fragment, tag)
						.addToBackStack(null).commitAllowingStateLoss();

			} else {
				getSupportFragmentManager()
				.beginTransaction()
				// .setCustomAnimations(R.anim.slide_in_right,
				// R.anim.slide_out_left, R.anim.slide_in_left,
				// R.anim.slide_out_right)
				/*.setCustomAnimations(android.R.anim.fade_in,
						android.R.anim.fade_out,
						android.R.anim.fade_in, android.R.anim.fade_out)*/
				.replace(R.id.content_frame, fragment, tag)
				.commitAllowingStateLoss();
			}

			actionBar.setDisplayHomeAsUpEnabled(addToBackstack);
		}
	}
}
