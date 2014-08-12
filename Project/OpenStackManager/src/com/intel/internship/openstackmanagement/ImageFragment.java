package com.intel.internship.openstackmanagement;

import java.util.ArrayList;

import com.intel.internship.openstack.services.ImageService;
import com.intel.internship.openstackmanagement.adapters.ImageListAdapter;
import com.intel.internship.openstackmanagement.adapters.InstanceListAdapter;
import com.intel.internship.openstackmanagement.model.Image;
import com.intel.internship.openstackmanagement.model.Instance;
import com.intel.internship.openstackmanagement.util.ApiRestManager;
import com.intel.internship.openstackmanagement.util.Tools;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.AdapterView.OnItemClickListener;

public class ImageFragment extends Fragment {
	
	private ListView lsV_table;
	private FrameLayout frL_detailContent;
	private ProgressBar prB_loadInstance;
	private ImageListAdapter imageListAdapter;
	
	private Handler handler = new Handler();
	private ImageService imageService;
	
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
		imageService = new ImageService(getActivity());
		inflateLayout();
		populateImageTable();
		
	}

	private void inflateLayout() {
		prB_loadInstance = (ProgressBar) getView().findViewById(R.id.pgB_laodInstance);
		lsV_table = (ListView) getView().findViewById(R.id.lsV_table);
		lsV_table.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
				onItemClickLsV_table(parent,view,pos,id);
			}
		});
		
		frL_detailContent = (FrameLayout) getView().findViewById(R.id.detail_content);
		frL_detailContent.setVisibility(View.GONE);
		
	}

	protected void onItemClickLsV_table(AdapterView<?> parent, View view,
			int pos, long id) {
		showDetailContent((Image)imageListAdapter.getItem(pos));
	}

	
	private void showDetailContent(Image image) {
		frL_detailContent.setVisibility(View.VISIBLE);
		ImageDetailFragment imageDetailFragment = new ImageDetailFragment();
		
		FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
		fragmentTransaction.add(R.id.detail_content, imageDetailFragment);
		fragmentTransaction.commit();
		getChildFragmentManager().executePendingTransactions();
		
		imageDetailFragment.setData(image);
	}

	/*
	 * It allows to populate the image table on background
	 */
	private void populateImageTable() {
		prB_loadInstance.setVisibility(View.VISIBLE);
		new Thread(new Runnable() {
			@Override
			public void run() {
				ArrayList<Image> result = imageService.getAllImages();
				updateImageTable(result);
			}
		}).start();
	}

	/**
	 * Allows to show the image table in us main thread
	 * @param result
	 */
	protected void updateImageTable(final ArrayList<Image> result) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				prB_loadInstance.setVisibility(View.GONE);
				if (result != null) {
					imageListAdapter = new ImageListAdapter(getActivity(), result);
					lsV_table.setAdapter(imageListAdapter);
				}else {
					Tools.showToast(getActivity(),R.string.dataLoadError);
				}
			}
		});
		
	}
	
	
}
