package com.intel.internship.openstackmanagement;

import com.intel.internship.openstackmanagement.model.Instance;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class InstanceDetailFragment extends Fragment {

	private TextView txV_instanceName;
	private TextView txV_imageName;
	private TextView txV_keyName;
	private TextView txV_securityGroup, txV_availabity_zone,
	txV_status, txV_privateIpAddresses, txV_publicIpAddresses,
	txV_created, txV_updated, txV_flavor;
	
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String a="ab";
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		super.onResume();
		((DashboardActivity)getParentFragment().getActivity()).setTitle(
				((DashboardActivity)getParentFragment().getActivity()).getSelectedActionBarTitle()
				);
		txV_instanceName = (TextView) getView().findViewById(R.id.txV_instanceNameDetail);
		txV_imageName = (TextView) getView().findViewById(R.id.txV_imageName);
		txV_keyName = (TextView) getView().findViewById(R.id.txV_keyName);
		txV_securityGroup = (TextView) getView().findViewById(R.id.txV_securityGroupName);
		txV_availabity_zone = (TextView) getView().findViewById(R.id.txV_availabilityZone);
		txV_status = (TextView) getView().findViewById(R.id.txV_statusDetail);
		txV_privateIpAddresses = (TextView) getView().findViewById(R.id.txV_privateIpAddresses);
		txV_publicIpAddresses = (TextView) getView().findViewById(R.id.txV_publicIpAddresses);
		txV_created = (TextView) getView().findViewById(R.id.txV_created);
		txV_updated = (TextView) getView().findViewById(R.id.txV_updated);
		txV_flavor = (TextView) getView().findViewById(R.id.txV_flavor);
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_instance_detail,container, false);
	}
	
	public void setData(Instance instance) {
		txV_instanceName.setText(instance.getName());
		txV_imageName.setText(instance.getImage().getName());
		txV_keyName.setText(instance.getKeyName());
		txV_securityGroup.setText(instance.getSecurityGroupName());
		txV_availabity_zone.setText(instance.getAvailabilityZone());
		txV_status.setText(instance.getStatus());
		txV_privateIpAddresses.setText(instance.getPrivateIp());
		txV_publicIpAddresses.setText(instance.getPublicIp());
		txV_created.setText(instance.getCreated());
		txV_updated.setText(instance.getUpdated());
		txV_flavor.setText(instance.getFlavor().getName() + ", " + instance.getFlavor().getRam() + " RAM");
	}

}
