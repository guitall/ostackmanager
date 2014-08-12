package com.intel.internship.openstackmanagement;


import java.util.List;

import com.intel.internship.openstack.services.LoginService;
import com.intel.internship.openstackmanagement.model.Profile;
import com.intel.internship.openstackmanagement.persistence.ProfileDataSource;
import com.intel.internship.openstackmanagement.util.ApiRestManager;
import com.intel.internship.openstackmanagement.util.Tools;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.support.v4.app.Fragment;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemLongClickListener;


public class MainActivity extends ActionBarActivity {
	
	public static final String PROFILE_FRAGMENT = "profileFragment"; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new ProfileFragment(), PROFILE_FRAGMENT).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_new_profile) {
			openNewProfileDialog(null);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void openNewProfileDialog(final Profile profile) {
		
		AlertDialog.Builder alBuilder = new AlertDialog.Builder(this);
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View view = inflater.inflate(R.layout.dialog_new_profile, null);
		if(profile != null) {
			((EditText) view.findViewById(R.id.etV_profileName)).setText(profile.getName());
			((EditText) view.findViewById(R.id.etV_endpoint)).setText(profile.getEndpoint());
			((EditText) view.findViewById(R.id.etV_username)).setText(profile.getUsername());
			((EditText) view.findViewById(R.id.etV_password)).setText(profile.getPassword());
			((EditText) view.findViewById(R.id.etV_tenantName)).setText(profile.getTenantName());
		}
		
		alBuilder.setView(view);
		alBuilder.setTitle(getString(R.string.action_new_profile));
		alBuilder.setPositiveButton(R.string.btn_save, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		} );
		alBuilder.setNegativeButton(R.string.btn_cancel, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				
			}
		});
		final AlertDialog dialog = alBuilder.create();
		dialog.show();
		dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {            
	          @Override
	          public void onClick(View v) {
	        	 if (profile == null) 
	        		 createNewProfile(view, dialog);
	        	 else
	        		 modifyProfile(view, dialog, profile.getId());
	          }
	    });
		
	}
	
	private Profile createPofileObject(View v) {
		
		final String profileName = ((EditText) v.findViewById(R.id.etV_profileName)).getText().toString();
		final String endpoint = ((EditText) v.findViewById(R.id.etV_endpoint)).getText().toString();
		final String username = ((EditText) v.findViewById(R.id.etV_username)).getText().toString();
		final String password = ((EditText) v.findViewById(R.id.etV_password)).getText().toString();
		final String tenantName = ((EditText) v.findViewById(R.id.etV_tenantName)).getText().toString();
		
		Profile newProfile = new Profile();
		newProfile.setName(profileName);
		newProfile.setEndpoint(endpoint);
		newProfile.setUsername(username);
		newProfile.setPassword(password);
		newProfile.setTenantName(tenantName);
		
		return newProfile;
	}

	protected void modifyProfile(View view, AlertDialog dialog, int idProfileToUpate) {
		Profile profileToEdit = createPofileObject(view);
		
		if(profileToEdit.getName().trim().length() == 0) {
			Tools.showToast(this, R.string.nameProfileEmpty);
			return;
		}
		
		ProfileDataSource profileDataSource = new ProfileDataSource (this);
		profileDataSource.updateProfile(profileToEdit, idProfileToUpate);
		((ProfileFragment)getSupportFragmentManager().findFragmentByTag(PROFILE_FRAGMENT)).loadProfile();
		dialog.dismiss();
	}

	protected void createNewProfile(View v, Dialog dialog) {
		
		Profile newProfile = createPofileObject(v);
		
		if(newProfile.getName().trim().length() == 0) {
			Tools.showToast(this, R.string.nameProfileEmpty);
			return;
		}
		
		ProfileDataSource profileDataSource = new ProfileDataSource(this);
		profileDataSource.insertProfile(newProfile);
		((ProfileFragment)getSupportFragmentManager().findFragmentByTag(PROFILE_FRAGMENT)).loadProfile();
		dialog.dismiss();
		
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class ProfileFragment extends Fragment {

		private ListView lsV_profiles;
		private Context mContext;
		protected Object mActionMode;
		private long selectedItemId = -1;
		private ProfilesAdapter adpater;
		private Profile selectedProfile;
		private Handler handler;
		
		public ProfileFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			
			View rootView = inflater.inflate(R.layout.fragment_main, container,	false);
			lsV_profiles = (ListView) rootView.findViewById(R.id.lsV_profiles);
			lsV_profiles.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			lsV_profiles.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					onItemClickLsV_profiles(parent,view,position,id);
				}
			});
	        return rootView;
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			
			super.onActivityCreated(savedInstanceState);
			handler = new Handler();
			mContext = getActivity();
			lsV_profiles.setOnItemLongClickListener(new OnItemLongClickListener() {

	            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

	            	if (mActionMode != null) {
	            		return false;
	            	}
	            	selectedItemId = id;
	            	selectedProfile = (Profile) adpater.getItem(position);
	            	mActionMode = ((ActionBarActivity) getActivity()).startSupportActionMode(mActionModeCallback);
	            	view.setSelected(true);
	            	lsV_profiles.setItemChecked(position, true);
	            	
	            	return true;
	            }
	        });
			
			loadProfile();
		}
		
		protected void onItemClickLsV_profiles(AdapterView<?> parent,
				View view, int position, long id) {
			Profile selectedProfile = (Profile)adpater.getItem(position);
//			if (Tools.isConnectivityAvailable(mContext)) {
//				loginProfile(selectedProfile);
//			} else {
//				Tools.notifyNoConnextion(mContext);
//			}
			loginProfile(selectedProfile);
				
			
		}
		
		public void loadProfile() {
			ProfileDataSource profileDataSource = new ProfileDataSource(getActivity());
			List<Profile> profiles = profileDataSource.getAllProfile();
			adpater = new ProfilesAdapter(profiles);
			lsV_profiles.setAdapter(adpater);
		}
		
		/**
		 * It allows to enter a openstack's session on background
		 * @param selectedProfile
		 */
		public void loginProfile(final Profile selectedProfile) {
			final ProgressDialog dialog= ProgressDialog.show(mContext, null,
					mContext.getString(R.string.logging),true,false);
			
			Thread thread = new Thread( new Runnable() {
				@Override
				public void run() {
					LoginService loginService = new LoginService(mContext);
					String result = loginService.login(selectedProfile);
					loginResult(result,dialog);
				}
			});
			thread.start();
		}
		
		/**
		 * Verify the result of the login's request on the US thread 
		 * @param result 
		 */
		public void loginResult(final String result,final Dialog dialog) {
			handler.post(new Runnable() {
				@Override
				public void run() {
					if(dialog != null)
						dialog.cancel();
					
					if(result != null){
						if(result.equals(ApiRestManager.OK)) {
							Intent it = new Intent(mContext, DashboardActivity.class);
							mContext.startActivity(it);
						} else { //Error whith token API
							Tools.showToast(mContext, result);
						}
					}else{ //Error with Endpoint Connection
						Tools.showToast(mContext, R.string.login_error);
					}
				}
			});
		}

		
		private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

	    	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
	    		MenuInflater inflater = mode.getMenuInflater();
	    		inflater.inflate(R.menu.login_contextual, menu);
	    		return true;
	    	}

	    	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
	    		return false; // Return false if nothing is done
	    	}

	    	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
	    		switch (item.getItemId()) {
	    		case R.id.action_edit:
	    			editProfile();
	    			mode.finish();
	    			return true;
	    		case R.id.action_delete:
	    			deleteProfile();
	    			mode.finish();
	    			return true;
	    		default:
	    			return false;
	    		}
	    	}

			public void onDestroyActionMode(ActionMode mode) {
	    		mActionMode = null;
	    		selectedItemId = -1;
	    	}
	    };
	    
		private void deleteProfile() {
			ProfileDataSource profileDataSource = new ProfileDataSource (getActivity());
			profileDataSource.deleteProfile(selectedProfile);
			loadProfile();
			
		}

		private void editProfile() {
			((MainActivity) getActivity()).openNewProfileDialog(selectedProfile);
		}
		
		public void updateProfileList(){
			adpater.notifyDataSetChanged();
		}

		//Custom Adapter for List View of the fragment.
		private static class ProfilesAdapter extends BaseAdapter {
			
			
			private List<Profile> items;
			
			public ProfilesAdapter(List<Profile> items){
				this.items = items;
			}
			
			@Override
			public int getCount() {
				return items.size();
			}

			@Override
			public Object getItem(int pos) {
				return items.get(pos);
			}
	
			@Override
			public long getItemId(int arg0) {
				return 0;
			}
	
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View rowView = convertView;
				ProfileHolder holder = null;
				
				if(rowView == null){
					rowView = LayoutInflater.from(parent.getContext())
							.inflate(R.layout.profile_item, null);
					holder = new ProfileHolder();
					holder.txV_profileName=(TextView) rowView.findViewById(R.id.txV_profileName);
					rowView.setTag(holder);
				}else{
					holder = (ProfileHolder) rowView.getTag();
				}
				
				Profile p = (Profile) getItem(position);
				holder.txV_profileName.setText(p.getName());
				return rowView;
			}
			
			static class ProfileHolder{
				TextView txV_profileName;
			}
		
		}
	}

	
}
