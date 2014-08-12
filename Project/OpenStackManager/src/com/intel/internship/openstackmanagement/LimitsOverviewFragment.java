package com.intel.internship.openstackmanagement;


import org.achartengine.GraphicalView;
import com.intel.internship.openstack.services.CommonService;
import com.intel.internship.openstackmanagement.model.Limits;
import com.intel.internship.openstackmanagement.util.ChartTools;
import com.intel.internship.openstackmanagement.util.Tools;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class LimitsOverviewFragment extends Fragment {

	
	private FrameLayout instancesChart, vcpusChart, ramChart, ipChart, 
		securityGroupChart, volumesChart, volumeStorageChart;
	
	private LinearLayout ln_chartGroup1, ln_chartGroup2, ln_chartGroup3, ln_chartGroup4;
	
	private TextView txV_instanceLimits, txV_volumeStorageLimits, txV_volumesLimits, 
		txV_securityGroupLimits, txV_floatingIpsLimits, txV_ramLimits, txV_vcpuLimits;
	
	private GraphicalView intanceChartView, vcpChartView, ramChartView, securityGroupChartView,
		volumeChartView, volumeStorageChartView , ipChartView;
	
	private ProgressBar progressBar;
	
	private Handler handler;
	private CommonService commonService;
	
	public LimitsOverviewFragment() {
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_limits_overview, container,false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		handler = new Handler();
		commonService = new CommonService(getActivity());
		inflateLayout();
		populateLimitOverviewData();
	}

	private void populateLimitOverviewData() {
		progressBar.setVisibility(View.VISIBLE);
		new Thread(new Runnable() {
			@Override
			public void run() {
				Limits limits = commonService.getAllLimits();
				if(limits != null) {
					paintChartLimits(limits);
				}else{
					handler.post(new Runnable() {
						@Override
						public void run() {
							Tools.showToast(getActivity(),R.string.dataLoadError);
							progressBar.setVisibility(View.GONE);
						}
					});
				}
				
			}
		}).start();
	}

	protected void paintChartLimits(final Limits limits) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				
				float ramUsed = limits.getTotalRAMused() / 1024;
				float totalRam = limits.getMaxTotalRam()/1024;
				
				final String instanceLimit = getString(R.string.used) + " " + limits.getTotalInstancesUsed()
						+ " " + getString(R.string.of) + " " + limits.getMaxTotalInstances();
				final String VCPUsLimit = getString(R.string.used) + " " + limits.getTotalCoresUsed()
						+ " " + getString(R.string.of) + " " + limits.getMaxTotalCores();
				final String ramLimit = getString(R.string.used) + " " + ramUsed + "GB" 
						+ " " + getString(R.string.of) + " " + totalRam + "GB";
				final String floatingIPsLimit = getString(R.string.used) + " " + limits.getTotalFlotingIpsUsed()
						+ " " + getString(R.string.of) + " " + limits.getMaxTotalFlotingIps();
				final String seLcurityGroupLimit = getString(R.string.used) + " " + limits.getTotalSecurityGroupUsed()
						+ " " + getString(R.string.of) + " " + limits.getMaxTotalSecurityGroup();
				final String volumeLimit = getString(R.string.used) + " " + limits.getTotalVolumesUsed()
						+ " " + getString(R.string.of) + " " + limits.getMaxTotalVolumes();
				final String volumeStorageLimit = getString(R.string.used) + " " + limits.getTotalVolumeStoragesUsed()
						+ "GB"	+ " " + getString(R.string.of) + " " + limits.getMaxTotalVolumeStorages() + "GB";
				
				txV_instanceLimits.setText(instanceLimit);
				txV_vcpuLimits.setText(VCPUsLimit);
				txV_floatingIpsLimits.setText(floatingIPsLimit);
				txV_ramLimits.setText(ramLimit);
				txV_volumesLimits.setText(volumeLimit);
				txV_volumeStorageLimits.setText(volumeStorageLimit);
				txV_securityGroupLimits.setText(seLcurityGroupLimit);
				
				paintCharts(limits);
				progressBar.setVisibility(View.GONE);
				changeChartGroupVisibility(true);
				
			}
		});
	}
	
	

	protected void paintCharts(Limits limits) {
		intanceChartView = ChartTools.createNewPairChart(getActivity()
				, new double[] { limits.getTotalInstancesUsed(), limits.getMaxTotalInstances() - limits.getTotalInstancesUsed()});
		instancesChart.addView(intanceChartView,
				new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		
		vcpChartView = ChartTools.createNewPairChart(getActivity()
				, new double[] { limits.getTotalCoresUsed(), limits.getMaxTotalCores() - limits.getTotalCoresUsed() } );
		vcpusChart.addView(vcpChartView,
				new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		
		ipChartView = ChartTools.createNewPairChart(getActivity()
				, new double[] { limits.getTotalFlotingIpsUsed(), limits.getMaxTotalFlotingIps() - limits.getTotalFlotingIpsUsed()} );
		ipChart.addView(ipChartView,
				new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		
		ramChartView = ChartTools.createNewPairChart(getActivity()
				, new double[] { limits.getTotalRAMused()/1024, (limits.getMaxTotalRam()/1024) - (limits.getTotalRAMused()/1024) } );
		ramChart.addView(ramChartView,
				new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		
		securityGroupChartView = ChartTools.createNewPairChart(getActivity()
				, new double[] { limits.getTotalSecurityGroupUsed(), limits.getMaxTotalSecurityGroup() - limits.getTotalSecurityGroupUsed()} );
		securityGroupChart.addView(securityGroupChartView,
				new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		
		volumeChartView = ChartTools.createNewPairChart(getActivity()
				, new double[] { limits.getTotalVolumesUsed(), limits.getMaxTotalVolumes() - limits.getTotalVolumesUsed() } );
		volumesChart.addView(volumeChartView,
				new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		
		volumeStorageChartView = ChartTools.createNewPairChart(getActivity()
				, new double[] { limits.getTotalVolumeStoragesUsed(), limits.getMaxTotalVolumeStorages() - limits.getTotalVolumeStoragesUsed() } );
		volumeStorageChart.addView(volumeStorageChartView,
				new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	}
	
	
	private void inflateLayout() {
		instancesChart = (FrameLayout) getView().findViewById(R.id.instancesChart);
		vcpusChart = (FrameLayout) getView().findViewById(R.id.VCPUsChart);
		ramChart = (FrameLayout) getView().findViewById(R.id.ramChart);
		ipChart = (FrameLayout) getView().findViewById(R.id.floatingIPsChart);
		securityGroupChart = (FrameLayout) getView().findViewById(R.id.securityGroupsChart);
		volumesChart = (FrameLayout) getView().findViewById(R.id.volumesChart);
		volumeStorageChart = (FrameLayout) getView().findViewById(R.id.volumeStoragesChart);
		
		txV_instanceLimits = (TextView) getView().findViewById(R.id.txV_instanceLimits);
		txV_floatingIpsLimits = (TextView) getView().findViewById(R.id.txV_floatingIPsLimits);
		txV_ramLimits = (TextView) getView().findViewById(R.id.txV_ramLimits);
		txV_securityGroupLimits = (TextView) getView().findViewById(R.id.txV_securityGroupsLimits);
		txV_vcpuLimits = (TextView) getView().findViewById(R.id.txV_vcpuLimits);
		txV_volumesLimits = (TextView) getView().findViewById(R.id.txV_volumesLimits);
		txV_volumeStorageLimits = (TextView) getView().findViewById(R.id.txV_volumeStorageLimits);
		
		ln_chartGroup1 = (LinearLayout) getView().findViewById(R.id.ln_chartGroup1);
		ln_chartGroup2 = (LinearLayout) getView().findViewById(R.id.ln_chartGroup2);
		ln_chartGroup3 = (LinearLayout) getView().findViewById(R.id.ln_chartGroup3);
		ln_chartGroup4 = (LinearLayout) getView().findViewById(R.id.ln_chartGroup4);
		
		changeChartGroupVisibility(false);
		
		progressBar = (ProgressBar) getView().findViewById(R.id.pgB_loadOverview);
		progressBar.setVisibility(View.GONE);
		
	}
	
	private void changeChartGroupVisibility(Boolean viewSatus){
		int visibility;
		if(viewSatus) {
			visibility = View.VISIBLE;
		}else
			visibility = View.GONE;
		
		ln_chartGroup1.setVisibility(visibility);
		ln_chartGroup2.setVisibility(visibility);
		ln_chartGroup3.setVisibility(visibility);
		ln_chartGroup4.setVisibility(visibility);
	}
	
	

}
