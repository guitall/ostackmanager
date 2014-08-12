package com.intel.internship.openstackmanagement;

import com.intel.internship.openstackmanagement.model.Volume;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class VolumeDetailFragment extends Fragment {

	private TextView txV_volumeName, txV_volumeDescription, txV_statusDetail,
		txV_volumeSizeDetail, txV_volumeAvaZone, txV_volumeBootable, txV_volumeType;
		
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_volume_detail, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		((DashboardActivity)getParentFragment().getActivity()).setTitle(
				((DashboardActivity)getParentFragment().getActivity()).getSelectedActionBarTitle());
		
		txV_volumeName = (TextView) getView().findViewById(R.id.txV_volumeName);
		txV_volumeDescription = (TextView) getView().findViewById(R.id.txV_volumeDescription);
		txV_statusDetail = (TextView) getView().findViewById(R.id.txV_statusDetail);
		txV_volumeSizeDetail = (TextView) getView().findViewById(R.id.txV_volumeSizeDetail);
		txV_volumeAvaZone = (TextView) getView().findViewById(R.id.txV_volumeAvaZone);
		txV_volumeBootable = (TextView) getView().findViewById(R.id.txV_volumeBootable);
		txV_volumeType = (TextView) getView().findViewById(R.id.txV_volumeType);
	}
	
	public void setData(Volume vol) {
		txV_volumeName.setText(vol.getName());
		txV_volumeDescription.setText(vol.getDescription());
		txV_statusDetail.setText(vol.getStatus());
		txV_volumeSizeDetail.setText(vol.getSize() + " GB");
		txV_volumeAvaZone.setText(vol.getAvailability_zone());
		txV_volumeBootable.setText(vol.getBootable());
		txV_volumeType.setText(vol.getType().toString());
	}

	
	
}
