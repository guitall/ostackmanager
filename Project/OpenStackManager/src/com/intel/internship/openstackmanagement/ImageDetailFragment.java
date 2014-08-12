package com.intel.internship.openstackmanagement;

import com.intel.internship.openstackmanagement.model.Image;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ImageDetailFragment extends Fragment {

	private TextView txV_imageName,txV_created, txV_updated, txV_status, txV_size;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_image_detail, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		((DashboardActivity)getParentFragment().getActivity()).setTitle(
				((DashboardActivity)getParentFragment().getActivity()).getSelectedActionBarTitle()
				);
		
		txV_imageName = (TextView) getView().findViewById(R.id.txV_imageName);
		txV_status = (TextView) getView().findViewById(R.id.txV_statusDetail);
		txV_created = (TextView) getView().findViewById(R.id.txV_created);
		txV_updated = (TextView) getView().findViewById(R.id.txV_updated);
		txV_size = (TextView) getView().findViewById(R.id.txV_imageSizeDetail);
	}
	
	public void setData(Image image){
		txV_imageName.setText(image.getName());
		txV_status.setText(image.getStatus());
		txV_size.setText("" + image.getSize());
		txV_created.setText(image.getCreatedDate());
		txV_updated.setText(image.getUpdatedDate());
	}

}
