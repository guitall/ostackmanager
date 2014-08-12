package com.intel.internship.openstackmanagement;

import com.intel.internship.openstackmanagement.adapters.DrawerListAdapter;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class DashboardActivity extends ActionBarActivity{
	
	
	public static final String INSTANCE_FRAGMENT = "Instances";
	public static final String IMAGE_FRAGMENT = "Images";
	public static final String VOLUME_FRAGMENT = "Volumes";
	public static final String OVERVIEW_FRAGMENT = "Overview";
	
	private String[] mFeaturesTitles;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	
	public String selectedActionBarTitle = "";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dashboard);
		inflateLayout();
		if (savedInstanceState == null)
			showFragment(OVERVIEW_FRAGMENT);
		
	}
	
	@Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
          return true;
        }
        // Handle your other action bar items...
        return super.onOptionsItemSelected(item);
    }
   
    private void inflateLayout() {
		
		mFeaturesTitles = getResources().getStringArray(R.array.featuresTitles);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		mDrawerToggle = new ActionBarDrawerToggle(
				this, 
				mDrawerLayout,
				R.drawable.ic_drawer, 
				R.string.drawer_open, 
				R.string.drawer_close) {

					@Override
					public void onDrawerClosed(View drawerView) {
						super.onDrawerClosed(drawerView);
					}

					@Override
					public void onDrawerOpened(View drawerView) {
						super.onDrawerOpened(drawerView);
					}
		};
		
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		
		//Set the adapter for the list view
//		mDrawerList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice,mFeaturesTitles));
		mDrawerList.setAdapter(new DrawerListAdapter(this, R.layout.drawer_item_list, mFeaturesTitles));
		mDrawerList.setItemsCanFocus(false);
		mDrawerList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		mDrawerList.setItemChecked(0, true);
		
		mDrawerList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				onItemClickMDrawerList(parent,view,position,id);
			}
		});
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
		
		
	}

	
	//item click event of the drawer list
	protected void onItemClickMDrawerList(AdapterView<?> parent, View view,
			int position, long id) {
		
		showFragment(mFeaturesTitles[position]);
		
		//Highlight the selected item, update the title, and close the drawer
		view.setSelected(true);
		mDrawerList.setItemChecked(position, true);
		setTitle(mFeaturesTitles[position]);
		selectedActionBarTitle = mFeaturesTitles[position];
		mDrawerLayout.closeDrawer(mDrawerList);
		
	}
	
	private void showFragment(String feature) {
		Fragment fragment = null;
		if(feature.compareTo(INSTANCE_FRAGMENT) == 0) {
			fragment = new InstancesFragment();
		}else if (feature.compareTo(IMAGE_FRAGMENT) == 0) {
			fragment = new ImageFragment();
		}else if (feature.compareTo(VOLUME_FRAGMENT) == 0) {
			fragment = new VolumeFragment();
		}else if (feature.compareTo(OVERVIEW_FRAGMENT) == 0) {
			fragment = new OverviewFragment();
		}
		
		if (fragment != null) {
			FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
			fragmentTransaction.replace(R.id.content_frame, fragment);
//			fragmentTransaction.addToBackStack(null);
			fragmentTransaction.commit();
		}
		
	}


	/**
	 * Change the action bar title
	 */
	public void setTitle(CharSequence title) {
		getSupportActionBar().setTitle(title);
	}
	
	public String getSelectedActionBarTitle() {
		return selectedActionBarTitle;
	}


	public void setSelectedActionBarTitle(String selectedActionBarTitle) {
		this.selectedActionBarTitle = selectedActionBarTitle;
	}
	
	
	
	
	
	
	
	
	
}
