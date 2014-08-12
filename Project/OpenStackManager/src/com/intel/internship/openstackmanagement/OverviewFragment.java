package com.intel.internship.openstackmanagement;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class OverviewFragment extends Fragment{
	
	FragmentTabHost tabHost;
	
	//Mandatory Constructor
	public OverviewFragment() {
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.fragment_main_overview, container, false);
		tabHost = (FragmentTabHost) rootView.findViewById(android.R.id.tabhost);
		tabHost.setup(getActivity(), getChildFragmentManager(),R.id.tabContent);
		
		//Add limits tab
		tabHost.addTab(tabHost.newTabSpec("limits").setIndicator(getString(R.string.limits)), 
				LimitsOverviewFragment.class,null);
		
		//Add usage summary tab
		tabHost.addTab(tabHost.newTabSpec("usage").setIndicator(getString(R.string.usage)), 
				UsageOverviewFragment.class,null);
		
		return rootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
	}

}
