package com.intel.internship.openstackmanagement;

import java.util.ArrayList;
import java.util.List;

import com.intel.internship.openstack.services.ComputeService;
import com.intel.internship.openstack.services.VolumeService;
import com.intel.internship.openstackmanagement.adapters.VolumeListAdapter;
import com.intel.internship.openstackmanagement.model.AvailabilityZone;
import com.intel.internship.openstackmanagement.model.Instance;
import com.intel.internship.openstackmanagement.model.Volume;
import com.intel.internship.openstackmanagement.model.VolumeType;
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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class VolumeFragment extends Fragment{
	
	private ListView lsV_table;
	private FrameLayout frL_detailContent;
	private ProgressBar prB_loadVolumes;
	private VolumeListAdapter volumeListAdapter;
	
	private Handler handler = new Handler();
	private VolumeService volumeService;
	private Object mActionMode;
	private long selectedItemId = -1;
	private Volume volumeSelected;
	
	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

    	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
    		MenuInflater inflater = mode.getMenuInflater();
    		inflater.inflate(R.menu.volume_contextual, menu);
    		return true;
    	}

    	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
    		return false; // Return false if nothing is done
    	}

    	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
    		switch (item.getItemId()) {
    		
    		case R.id.action_delete:
    			deleteInstance();
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_template_table, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		handler = new Handler();
		volumeService = new VolumeService(getActivity());
		inflateLayout();
		populateImageTable();
		
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
				openNewVolumeDialog();
				return true;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void openNewVolumeDialog() {
		AlertDialog.Builder alBuilder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View view = inflater.inflate(R.layout.dialog_new_volume, null);
		innitDialog(view);
		
		alBuilder.setView(view);
		alBuilder.setTitle(R.string.newVolume);
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
				createNewVolume(view,dialog);
			}
		});
		
	}

	private void innitDialog(View v) {
		final Spinner spn_volumeType = (Spinner) v.findViewById(R.id.spn_volumeType);
		final Spinner spn_availabilityZone= (Spinner) v.findViewById(R.id.spn_availabilityZone);
		
		final ComputeService computeService= new ComputeService(getActivity());
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				//get all volume types
				final List<VolumeType> volumesTypes = volumeService.getAllVolumeTypes();
				
				//get all availability zone
				final List<AvailabilityZone> availabilityZones = new ComputeService(getActivity())
																			.getAllAvailabilityZone();
				
				if(volumesTypes != null && availabilityZones != null) {
					handler.post(new Runnable() {
						@Override
						public void run() {
							//Set Image Spinner  
							ArrayAdapter<VolumeType> volumeTypeAdapter = new ArrayAdapter<VolumeType>(
									getActivity(),
									android.R.layout.simple_spinner_item, volumesTypes);
							volumeTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
							spn_volumeType.setAdapter(volumeTypeAdapter);
							
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
	
	protected void createNewVolume(View view, final AlertDialog dialog) {
		//Volume name
		final String name = ((EditText)view.findViewById(R.id.edT_volumeNameDialog)).getText().toString();
		if(name.trim().length() == 0) {
			Tools.showToast(getActivity(), R.string.volumeNameValidation);
			return;
		}
		
		//Volume description 
		final String description = ((EditText)view.findViewById(R.id.edT_volumeDescriptionDialog)).getText().toString();
		
		//Volume type
		VolumeType volumeType = null;
		Spinner vlmSpinner = (Spinner) view.findViewById(R.id.spn_volumeType);
		try {
			volumeType = ((VolumeType)vlmSpinner.getAdapter().getItem(vlmSpinner.getSelectedItemPosition()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//Availability zone
		Spinner avZoSpinner = (Spinner)view.findViewById(R.id.spn_availabilityZone);
		String availabilityZone = ((AvailabilityZone)avZoSpinner.getAdapter().getItem(avZoSpinner.getSelectedItemPosition())).getName();
				
		//Size
		String size = ((EditText)view.findViewById(R.id.edT_volumeSizeDialog)).getText().toString();
		if(size.trim().length() == 0) {
			Tools.showToast(getActivity(), R.string.volumeSizeValidation);
			return;
		}
		final int volumeSize = Integer.parseInt(size);
		
		final Volume newVolume = new Volume();
		newVolume.setName(name);
		newVolume.setAvailability_zone(availabilityZone);
		newVolume.setDescription(description);
		newVolume.setSize(volumeSize);
		newVolume.setType(volumeType);
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				final String res = volumeService.createVolume(newVolume);
				if(res != null){
					if(res.compareTo(ApiRestManager.OK) == 0){
						dialog.dismiss();
						addVolumeOnTable(newVolume);
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
							Tools.showToast(getActivity(), R.string.errorToCreateVolume);
						}
					});
				}
			}
		}).start();
	}

	protected void addVolumeOnTable(final Volume volume) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				volumeListAdapter.addVolume(volume); 
			}
		});
		
		updateVolumeStatus(volume, Volume.CREATE_ACTION);
	}

	private void updateVolumeStatus(Volume volume, int action) {
		String verificationStatus = "";
		if(action == Volume.CREATE_ACTION)
			verificationStatus = Volume.CREATING_STATUS;
		 
		do {
			volume = volumeService.getVolumeDetail(volume.getId());
			if (volume != null)
				updateTableRow(volume);
			else
				break;
			try {Thread.sleep(3000); } catch (InterruptedException e) {	e.printStackTrace();}
		} while (volume.getStatus().compareToIgnoreCase(verificationStatus) == 0);
		
	}
	
	private void updateTableRow(final Volume vol) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				volumeListAdapter.updateRow(vol);
			}
		});
	}

	private void inflateLayout() {
		prB_loadVolumes = (ProgressBar) getView().findViewById(R.id.pgB_laodInstance);
		lsV_table = (ListView) getView().findViewById(R.id.lsV_table);
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
		
		frL_detailContent = (FrameLayout) getView().findViewById(R.id.detail_content);
		frL_detailContent.setVisibility(View.GONE);
		
	}

	protected void loadActionBarContextMenu(int position, long id) {
		selectedItemId = id;
		volumeSelected = (Volume) volumeListAdapter.getItem(position);
    	mActionMode = ((ActionBarActivity) getActivity()).startSupportActionMode(mActionModeCallback);
	}

	protected void onItemClickLsV_table(AdapterView<?> parent, View view,
			int pos, long id) {
		showDetailContent((Volume)volumeListAdapter.getItem(pos));
	}
	
	private void showDetailContent(Volume volume) {
		frL_detailContent.setVisibility(View.VISIBLE);
		VolumeDetailFragment volumeDetailFragment = new VolumeDetailFragment();
		
		FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
		fragmentTransaction.add(R.id.detail_content, volumeDetailFragment);
		fragmentTransaction.commit();
		getChildFragmentManager().executePendingTransactions();
		
		volumeDetailFragment.setData(volume);
	}

	/**
	 * Populates the volumes table
	 */
	private void populateImageTable() {
		prB_loadVolumes.setVisibility(View.VISIBLE);
		new Thread(new Runnable() {
			@Override
			public void run() {
				ArrayList<Volume> result = volumeService.getAllVolumes();
				updateVolumeTable(result);
			}
		}).start();
	}
	
	/**
	 * It allows to update the volumes table in the us main thread.
	 * @param list
	 */
	protected void updateVolumeTable(final ArrayList<Volume> list) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				prB_loadVolumes.setVisibility(View.GONE);
				if (list != null) {
					volumeListAdapter = new VolumeListAdapter(getActivity(), list);
					lsV_table.setAdapter(volumeListAdapter);
				}else {
					Tools.showToast(getActivity(),R.string.dataLoadError);
				}
			}
		});
	}
	
	protected void deleteInstance() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				final String res = volumeService.deleteVolume(volumeSelected);
				if(res != null) {
					if(res.compareTo(ApiRestManager.OK) == 0)
						deleteVolumeOnTable(volumeSelected);
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
							Tools.showToast(getActivity(), R.string.errorToDeleteVolume);
						}
					});
				}
			}
		}).start();
		
	}

	protected void deleteVolumeOnTable(final Volume volume) {
		handler.post( new Runnable() {
			@Override
			public void run() {
				volumeListAdapter.deleteInstance(volume); 
			}
		});
	}

	
	
	
}
