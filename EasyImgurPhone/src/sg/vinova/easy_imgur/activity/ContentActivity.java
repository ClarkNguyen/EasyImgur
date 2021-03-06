package sg.vinova.easy_imgur.activity;

import sg.vinova.easy_imgur.fragment.gallery.GalleriesFragment;
import sg.vinova.easy_imgur.fragment.home.HomeFragment;
import sg.vinova.easy_imgur.fragment.login.LoginFragment;
import sg.vinova.easy_imgur.fragment.meme.MemesFragment;
import sg.vinova.easy_imgur.fragment.upload.UploadImageFragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.actionbarsherlock.view.MenuItem;

public class ContentActivity extends BaseActivity implements
		OnItemClickListener {

	// TAG
	public static final String TAG = "ContentActivity";

	// Drawer for left menu
	private ActionBarDrawerToggle drawerToggle;
	private DrawerLayout drawerLayout;

	// List menu categories
	private String[] categories;
	private ListView lvCategories;

	// current action bar title
	private String currentTitle;

	// current menu position
	private int currMenuPosition = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Settings
		currentTitle = getString(R.string.app_name);
		setContentView(R.layout.activity_container);
		findViews();

		if (savedInstanceState != null) {
			mainContent = getSupportFragmentManager().getFragment(
					savedInstanceState, TAG);
			setContentForAboveView(mainContent, TAG);
		}

		setContentForAboveView(new HomeFragment(), LoginFragment.TAG);

		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
	}

	/**
	 * Fill all views
	 */
	private void findViews() {
		// Connect to UI component
		lvCategories = (ListView) findViewById(R.id.lv_left_menu);
		categories = getResources().getStringArray(R.array.menu_categories);
		lvCategories.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, categories));
		lvCategories.setOnItemClickListener(this);

		/**
		 * Find views for drawer
		 */
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		// set a custom shadow that overlays the main content when the drawer
		// opens
		drawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);

		drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
				R.drawable.ic_drawer, R.string.app_name, R.string.app_name) {
			public void onDrawerClosed(View view) {
				// actionBar.setTitle(currentTitle);
				// calling onPrepareOptionsMenu() to show action bar icons
				// invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				// actionBar.setTitle("Drawer open");
				// calling onPrepareOptionsMenu() to hide action bar icons
				// invalidateOptionsMenu();
			}
		};
		drawerLayout.setDrawerListener(drawerToggle);
	}

	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		// TODO Auto-generated method stub
		return super.onCreateOptionsMenu(menu);
	}

//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		if (item.getItemId() == android.R.id.home) {
//			if (!drawerLayout.isDrawerOpen(lvCategories)) {
//				drawerLayout.openDrawer(lvCategories);
//			} else {
//				drawerLayout.closeDrawer(lvCategories);
//			}
//
//		}
//
//		return super.onOptionsItemSelected(item);
//	}
	
	/**
	 * check is menu open or close
	 * @return
	 */
	public boolean isLeftMenuOpen() {
		return drawerLayout.isDrawerOpen(lvCategories);
	}
	
	/**
	 * Handle open, close left menu
	 * @param open
	 */
	public void handleLeftMenu(boolean open) {
		if (open) {
			drawerLayout.openDrawer(lvCategories);
		} else {
			drawerLayout.closeDrawer(lvCategories);
		}
	}

	private void setContentForAboveView(Fragment fragment, String tag) {
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, fragment, tag)
				.commitAllowingStateLoss();

	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		drawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggles
		drawerToggle.onConfigurationChanged(newConfig);
	}

	/**
	 * Set the state of action bar drawer (is enable/disable, is show/hide icon)
	 * 
	 * @param isEnable
	 * @param isShowIcon
	 */
	public void changeStateActionBarDrawer(boolean isEnable, boolean isShowIcon) {
		if (isEnable) {
			drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
			drawerToggle.setDrawerIndicatorEnabled(isShowIcon);
		} else {
			drawerLayout
					.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
			drawerToggle.setDrawerIndicatorEnabled(false);
		}
	}

	/**
	 * Set the title of action bar
	 * 
	 * @param title
	 */
	public void setActionBarTitle(String title) {
		currentTitle = title;
		actionBar.setTitle(currentTitle);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		if (currMenuPosition != position) {
			// set actionbar title
			actionBar.setTitle(categories[position]);

			switch (position) {
			case 0:
				// galleries
				currMenuPosition = 0;
				switchContent(new GalleriesFragment(), true, true,
						GalleriesFragment.TAG);
				/*MGallery gallery = new MGallery();
				gallery.setId("6uS3m");
				switchContent(new GalleriesArticleFragment(gallery), true, true,
						GalleriesFragment.TAG);*/
				break;
				
			case 1:
				// memes
				switchContent(new MemesFragment(), true, true, MemesFragment.TAG);
				currMenuPosition = 1;
				break;
				
			case 2:
				// albums
				currMenuPosition = 2;
				break;
				
			case 3:
				// my photos
				currMenuPosition = 3;
				break;
				
			case 4:
				// upload image
				currMenuPosition = 4;
				switchContent(new UploadImageFragment(), true, true, UploadImageFragment.TAG);
				break;
				
			case 5:
				// login
				currMenuPosition = 5;
				switchContent(new LoginFragment(), true, true, LoginFragment.TAG);
				break;

			default:
				break;
			}

			lvCategories.setItemChecked(position, true);
		}
		
		if (drawerLayout.isDrawerOpen(lvCategories)) {
			drawerLayout.closeDrawer(lvCategories);
		}
	}

}
