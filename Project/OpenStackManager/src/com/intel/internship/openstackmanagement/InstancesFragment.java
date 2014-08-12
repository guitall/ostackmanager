package com.intel.internship.openstackmanagement;

import java.util.ArrayList;
import java.util.List;

import com.intel.internship.openstack.services.ComputeService;
import com.intel.internship.openstack.services.ImageService;
import com.intel.internship.openstackmanagement.adapters.InstanceListAdapter;
import com.intel.internship.openstackmanagement.model.AvailabilityZone;
import com.intel.internship.openstackmanagement.model.Flavor;
import com.intel.internship.openstackmanagement.model.Image;
import com.intel.internship.openstackmanagement.model.Instance;
import com.intel.internship.openstackmanagement.util.ApiRestManager;
import com.intel.internship.openstackmanagement.util.Tools;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

public class InstancesFragment extends Fragment{
	
//	public final static String BUILD_STATUS = "BUILD";
	
	private ListView lsV_table;
	private FrameLayout frL_detailContent;
	private ProgressBar prB_loadInstance;
	
	private InstanceListAdapter instanceListAdapter;
	private ComputeService computeService;
	private Handler handler = new Handler();
	private Object mActionMode;
	private long selectedItemId = -1;
	private Instance selectedInstance;
	
	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

    	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
    		MenuInflater inflater = mode.getMenuInflater();
    		int idMenu = -1; 
    		
    		if(selectedInstance.getStatus().compareToIgnoreCase("shutoff") == 0)
    			idMenu = R.menu.instance_contextual_stop;
    		else if(selectedInstance.getStatus().compareToIgnoreCase("active") == 0)
    			idMenu = R.menu.instance_contextual_running;
    		
    		if(idMenu != -1)
    			inflater.inflate(idMenu, menu);
    		
    		return true;
    	}

    	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
    		return false; // Return false if nothing is done
    	}

    	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
    		switch (item.getItemId()) {
    		case R.id.action_run:
    			doAction(Instance.START_ACTION);
    			mode.finish();
    			return true;
    		case R.id.action_delete:
    			deleteInstance();
    			mode.finish();
    			return true;
    		case R.id.action_stop:
    			doAction(Instance.STOP_ACTION);
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
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		setRetainInstance(true);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		computeService = new ComputeService(getActivity());
		inflateLayout(getView());
		populateInstancesTable();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_template_table, container, false);
	}
	
	

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.instance_menu, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_new:
				openNewInstanceDialog();
				return true;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void openNewInstanceDialog() {
		AlertDialog.Builder alBuilder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View view = inflater.inflate(R.layout.dialog_new_instance, null);
		innitDialog(view);
		
		alBuilder.setView(view);
		alBuilder.setTitle(R.string.newInstance);
		alBuilder.setPositiveButton(R.string.btn_create, new Dialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				
			}
		});
		alBuilder.setNegativeButton(R.string.btn_cancel, new Dialog.OnClickListener() {
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
				createNewInstance(view,dialog);
			}
		});
	}

	private void innitDialog(View v) {
		final Spinner spn_imageType = (Spinner) v.findViewById(R.id.spn_imageType);
		final Spinner spn_flavor = (Spinner) v.findViewById(R.id.spn_flavor);
		final Spinner spn_availabilityZone= (Spinner) v.findViewById(R.id.spn_availabilityZone);
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				//get all images
				final List<Image> images = new ImageService(getActivity()).getAllImages();
				//get all flavor
				final List<Flavor> flavors = computeService.getAllFlavors();
				//get all availability zone
				final List<AvailabilityZone> availabilityZones = computeService.getAllAvailabilityZone();
				
				if(images != null && flavors != null && availabilityZones != null) {
					handler.post(new Runnable() {
						@Override
						public void run() {
							//Set Image Spinner  
							ArrayAdapter<Image> imageAdapter = new ArrayAdapter<Image>(
									getActivity(),
									android.R.layout.simple_spinner_item, images);
							imageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
							spn_imageType.setAdapter(imageAdapter);
							
							//Set Flavor Spinner
							ArrayAdapter<Flavor> flavorAdapter = new ArrayAdapter<>(
									getActivity(),
									android.R.layout.simple_spinner_dropdown_item, flavors);
							spn_flavor.setAdapter(flavorAdapter);
							
							//Set Flavor Spinner
							ArrayAdapter<AvailabilityZone> availabilityZoneAdapter = new ArrayAdapter<>(
									getActivity(),
									android.R.layout.simple_spinner_dropdown_item, availabilityZones);
							spn_availabilityZone.setAdapter(availabilityZoneAdapter);
						}
					});
				}
			}
		}).start();
	}
	
	protected void createNewInstance(View view, final Dialog dialog) {
		//Instance name
		final String name = ((EditText)view.findViewById(R.id.edT_nameInstanceDialog)).getText().toString();
		if(name.trim().length() == 0) {
			Tools.showToast(getActivity(), R.string.instanceNameValidation);
			return;
		}
		
		//Image id		
		Spinner imgSpinner = (Spinner)view.findViewById(R.id.spn_imageType);
		String imageId = ((Image)imgSpinner.getAdapter().getItem(imgSpinner.getSelectedItemPosition())).getId();
		Image image = new Image();
		image.setId(imageId);
		
		//Flavor
		Spinner flvSpinner = (Spinner)view.findViewById(R.id.spn_flavor);
		String flavorId = ((Flavor)flvSpinner.getAdapter().getItem(flvSpinner.getSelectedItemPosition())).getId();
		Flavor flavor = new Flavor();
		flavor.setId(flavorId);
		
		//Availability zone
		Spinner avZoSpinner = (Spinner)view.findViewById(R.id.spn_availabilityZone);
		String availabilityZone = ((AvailabilityZone)avZoSpinner.getAdapter().getItem(avZoSpinner.getSelectedItemPosition())).getName();
		
		int countInstance = 1;
		String count = ((EditText)view.findViewById(R.id.txV_instanceCount)).getText().toString();
		if(count.trim().length() > 0) {
			countInstance = Integer.parseInt(count);
		}
		final int countIns = countInstance;
		
		final Instance instance = new Instance();
		instance.setName(name);
		instance.setImage(image);
		instance.setFlavor(flavor);
		instance.setAvailabilityZone(availabilityZone);
		
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				final String res = computeService.createInstance(instance, countIns);
				if(res != null){
					if(res.compareTo(ApiRestManager.OK) == 0){
						dialog.dismiss();
						addInstanceOnTable(instance);
					}else{
						handler.post(new Runnable() {
							@Override
							public void run() {
								Tools.showToast(getActivity(), res);
							}
						});
					}
				} else{
					handler.post(new Runnable() {
						@Override
						public void run() {
							Tools.showToast(getActivity(), R.string.errorToCreateInstance);
						}
					});
				}
			}
		}).start();
	}
	
	protected void deleteInstance() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				final String res = computeService.deleteInstance(selectedInstance);
				if(res != null) {
					if(res.compareTo(ApiRestManager.OK) == 0)
						deleteInstanceOnTable(selectedInstance);
					else{
						handler.post(new Runnable() {
							@Override
							public void run() {
								Tools.showToast(getActivity(), res);
							}
						});
					}
				}else {
					handler.post(new Runnable() {
						@Override
						public void run() {
							Tools.showToast(getActivity(), R.string.errorToDeleteInstance);
						}
					});
				}
			}
		}).start();
	}
	
	/**
	 * Allows to delete an instance to the instance table in us main thread
	 * @param instance
	 */
	protected void deleteInstanceOnTable(final Instance instance) {
		handler.post( new Runnable() {
			@Override
			public void run() {
				instanceListAdapter.deleteInstance(instance); 
			}
		});
	}

	/**
	 * Allows to add an instance to the instance table in us main thread 
	 * and update the its status.
	 * @param instance
	 */
	protected void addInstanceOnTable(final Instance instance) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				instance.setStatus(Instance.BUILD_STATUS);
				instanceListAdapter.addInstance (instance); 
			}
		});
		updateInstanceStatus(instance, Instance.CREATE_ACTION);
	}

	private void updateTableRow(final Instance ins) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				instanceListAdapter.updateRow(ins);
			}
		});
	}

	private void inflateLayout(View view) {
		prB_loadInstance = (ProgressBar) view.findViewById(R.id.pgB_laodInstance);
		lsV_table = (ListView) view.findViewById(R.id.lsV_table);
		lsV_table.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
				onItemClickLsV_table(parent,view,pos,id);
			}
		});
		
		lsV_table.setOnItemLongClickListener(new OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            	if (mActionMode != null) {
            		return false;
            	}
            	loadActionBarContextMenu(position, id);
            	return true;
            }
        });
		
		frL_detailContent = (FrameLayout) view.findViewById(R.id.detail_content);
		frL_detailContent.setVisibility(View.GONE);
	}
	
	private void loadActionBarContextMenu(int position,long id) {
		selectedItemId = id;
    	selectedInstance = (Instance) instanceListAdapter.getItem(position);
    	mActionMode = ((ActionBarActivity) getActivity()).startSupportActionMode(mActionModeCallback);
    }
	
	
	/**
	 * On item click event in the instance list
	 */
	protected void onItemClickLsV_table(AdapterView<?> parent, View view,
			int pos, long id) {
		showDetailContent((Instance)instanceListAdapter.getItem(pos));
	}

	/**
	 * Populate the Instances table
	 */
	private void populateInstancesTable() {
		prB_loadInstance.setVisibility(View.VISIBLE);
		new Thread(new Runnable() {
			@Override
			public void run() {
				ArrayList<Instance> result = computeService.getAllInstances();
				updateInstanceTable(result);
			}
		}).start();
		
	}

	/**
	 * It allows to update the instances table
	 * @param list
	 */
	protected void updateInstanceTable(final ArrayList<Instance> list) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				prB_loadInstance.setVisibility(View.GONE);
				if (list != null) {
					instanceListAdapter = new InstanceListAdapter(getActivity(), list);
					lsV_table.setAdapter(instanceListAdapter);
				}else {
					Tools.showToast(getActivity(),R.string.dataLoadError);
				}
			}
		});
	}
	
	private void showDetailContent(Instance instance) {
		
		frL_detailContent.setVisibility(View.VISIBLE);
		InstanceDetailFragment fragmentDetailFragment = new InstanceDetailFragment();
		
		FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
		fragmentTransaction.add(R.id.detail_content, fragmentDetailFragment);
		fragmentTransaction.commit();
		getChildFragmentManager().executePendingTransactions();
		
		fragmentDetailFragment.setData(instance);
		
	}
	
	
	/**
	 * Allows to do some action for a created instance as start it or stop it. 
	 * @param action to do.
	 */
	protected void doAction(final int action) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				boolean res = computeService.doActionOnInstance(selectedInstance, action);
				if(res)
					updateInstanceStatus(selectedInstance, action);
				else{
					String msg="";
					if(action == Instance.START_ACTION)
						msg = getString(R.string.errorToStartInsntance);
					else if(action == Instance.STOP_ACTION)
						msg = getString(R.string.errorToStopInstance);
					
					final String errorMsg = msg;
					handler.post(new Runnable() {
						
						@Override
						public void run() {
							Tools.showToast(getActivity(), errorMsg);
						}
					});
				}
			}
		}).start();
	}

	/**
	 * It Allows to update the status of a row instance on instance table. 
	 * This method must run in background.   
	 * @param instance to update
	 * @param action that it was done 
	 */
	protected void updateInstanceStatus(Instance instance, int action) {
		String verificationStatus = "";
		if(action == Instance.START_ACTION)
			verificationStatus = Instance.ACTIVE_STATUS;
		else if (action == Instance.STOP_ACTION)
			verificationStatus = Instance.SHUTOFF_STATUS;
		else if (action == Instance.CREATE_ACTION)
			verificationStatus = Instance.ACTIVE_STATUS; 
		
		do {
			instance = computeService.getInstanceDetail(instance.getId());
			if (instance != null)
				updateTableRow(instance);
			else
				break;
			try {Thread.sleep(3000); } catch (InterruptedException e) {	e.printStackTrace();}
		} while (instance.getStatus().compareToIgnoreCase(verificationStatus) != 0);
	}
	
	
}
