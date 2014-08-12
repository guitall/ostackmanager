package com.intel.internship.openstackmanagement;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.intel.internship.openstack.services.ComputeService;
import com.intel.internship.openstackmanagement.adapters.InstanceUsageAdapter;
import com.intel.internship.openstackmanagement.model.Instance;
import com.intel.internship.openstackmanagement.util.Tools;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class UsageOverviewFragment extends Fragment {
	
	private EditText edT_startDate, edT_endDate;
	private Button btn_submit;
	private TextView txV_activeInstances, txV_activeRam,
		txV_VCPUHours, txV_GBHours;
	private DatePickerDialog.OnDateSetListener date;
	
	private FrameLayout frL_usageTable;
	private ListView lsV_usagetable;
	private ProgressBar prgBar;
	
	private Handler handler;
	private ComputeService computeService;
	private Calendar myCalendar = Calendar.getInstance();
	private EditText selectedFieldDate;
	
	
	public UsageOverviewFragment() {
	
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_usage_overview, container, false);
		inflateLayout(rootView);
		return rootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		handler = new Handler();
		computeService = new ComputeService(getActivity());
		loadUsageOverviewData();
	}

	private void inflateLayout(View rootView) {
		txV_activeInstances = (TextView) rootView.findViewById(R.id.txV_activeIntances);
		txV_activeRam = (TextView) rootView.findViewById(R.id.txV_activeRAM);
		txV_VCPUHours = (TextView) rootView.findViewById(R.id.txV_VCPUHours);
		txV_GBHours = (TextView) rootView.findViewById(R.id.txV_GBHours);
		edT_startDate = (EditText) rootView.findViewById(R.id.edT_startDate);
		edT_startDate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onClickEdT_startDate(v);
			}
		});
		
		edT_endDate = (EditText) rootView.findViewById(R.id.edT_endDate);
		edT_endDate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onClickEdT_endDate(v);
			}
		});
		
		btn_submit = (Button) rootView.findViewById(R.id.btn_submit);
		btn_submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onClickBtn_submit(v);
			}
		});
		lsV_usagetable = (ListView) rootView.findViewById(R.id.lsV_table);
		prgBar = (ProgressBar) rootView.findViewById(R.id.pgB_laodInstance);
		
		date = new DatePickerDialog.OnDateSetListener() {
				@Override
			    public void onDateSet(DatePicker view, int year, int monthOfYear,
			            int dayOfMonth) {
			        // TODO Auto-generated method stub
			        myCalendar.set(Calendar.YEAR, year);
			        myCalendar.set(Calendar.MONTH, monthOfYear);
			        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			        String patron = "yyyy-MM-dd";
					selectedFieldDate.setText(new SimpleDateFormat(patron).format(myCalendar.getTime()));
					
			    }
		};
	}

	protected void onClickEdT_endDate(View v) {
		selectedFieldDate = edT_endDate;
		edT_endDate.requestFocus();
		new DatePickerDialog(getActivity(), date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
	}

	protected void onClickEdT_startDate(View v) {
		selectedFieldDate = edT_startDate;
		edT_startDate.requestFocus();
		new DatePickerDialog(getActivity(), date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
	}

	protected void onClickBtn_submit(View v) {
		String startDate = edT_startDate.getText().toString();
		String endDate = edT_endDate.getText().toString();
		
		if(startDate.trim().length() == 0 || 
				endDate.trim().length() == 0) {
			Tools.showToast(getActivity(), R.string.requiredDate);
		}
		
		String sDate = Tools.formatDate(Tools.setTimeToDate(Tools.getDateObject(startDate), 0, 0, 0)
				, "yyyy-MM-dd'T'HH:mm:ss");
		
		String eDate = Tools.formatDate(Tools.getDateObject(endDate), "yyyy-MM-dd'T'HH:mm:ss");
		
		getUsageOverview(sDate, eDate);
	}
	
	private void loadUsageOverviewData() {
		Date currentDate = new Date();
		Date firstCurrentMonthDate = Tools.getFirstCurrentMonthDate();
		
		String startDate = Tools.formatDate(Tools.setTimeToDate(firstCurrentMonthDate, 0, 0, 0), 
				"yyyy-MM-dd'T'HH:mm:ss");
		String endDate = Tools.formatDate(currentDate, "yyyy-MM-dd'T'HH:mm:ss");
		
		edT_startDate.setText(Tools.formatDate(firstCurrentMonthDate, "yyyy-MM-dd"));
		edT_endDate.setText(Tools.formatDate(currentDate, "yyyy-MM-dd"));
		
		getUsageOverview(startDate, endDate);
	}
	
	/**
	 * Allows to get the Usage Overview data in background
	 * @param startDate 
	 * @param endDate
	 */
	private void getUsageOverview(final String startDate, final String endDate) {
		prgBar.setVisibility(View.VISIBLE);
		new Thread(new Runnable() {
			@Override
			public void run() {
				JSONObject result = computeService.getTenantUsage(startDate, endDate);
				if(result != null) {
					populateUsage(result);
				}else{
					handler.post(new Runnable() {
						@Override
						public void run() {
							Tools.showToast(getActivity(),R.string.dataLoadError);
							prgBar.setVisibility(View.GONE);
						}
					});
				}
				
			}
		}).start();
	}
	
	

	protected void populateUsage(final JSONObject result) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				
				int activeInstances = 0;
				int activeRAM = 0 ;
				float VCPUHour = 0;
				float GBHours = 0;
				
				ArrayList<Instance> instances = new ArrayList<>();
				prgBar.setVisibility(View.GONE);
				try {
					JSONObject tenantUsage = result.getJSONObject("tenant_usage");
					JSONArray activeInstancesArray = tenantUsage.getJSONArray("server_usages");
					activeInstances = activeInstancesArray.length();
					for (int i = 0; i < activeInstancesArray.length(); i++) {
						JSONObject item = activeInstancesArray.getJSONObject(i); 
						activeRAM += item.getInt("memory_mb");
						Instance ins = new Instance();
						ins.setName(item.getString("name"));
						int uptime = item.getInt("uptime");
						ins.setUptime(formatUptime(uptime));
						instances.add(ins);
					}
					VCPUHour =(float) tenantUsage.getDouble("total_vcpus_usage");
					GBHours = (float) tenantUsage.getDouble("total_local_gb_usage");
					
				} catch (JSONException ex) {
					ex.printStackTrace();
				}
				InstanceUsageAdapter adapter = new InstanceUsageAdapter(getActivity(), instances);
				lsV_usagetable.setAdapter(adapter);
				txV_activeInstances.setText("" + activeInstances);
				txV_activeRam.setText(activeRAM/1024 + "GB" );
				txV_VCPUHours.setText("" + VCPUHour);
				txV_GBHours.setText("" + GBHours);
				
			}
		});
	}
	
	private String formatUptime(int uptimeSeg) {
		int hor,seg,min;
		hor = uptimeSeg/3600;
		min = (uptimeSeg-(3600*hor))/60;
		seg = uptimeSeg - ((3600*hor)+(min*60));
		return hor + "h " + min + "m " + seg + "s";
	}
	
	
}
