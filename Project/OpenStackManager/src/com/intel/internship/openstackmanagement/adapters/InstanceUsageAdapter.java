package com.intel.internship.openstackmanagement.adapters;

import java.util.List;

import com.intel.internship.openstackmanagement.R;
import com.intel.internship.openstackmanagement.adapters.InstanceListAdapter.HolderView;
import com.intel.internship.openstackmanagement.model.Instance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class InstanceUsageAdapter extends BaseAdapter{

	private List<Instance> instances;
	private Context mContext;
	
	public InstanceUsageAdapter(Context ctx, List<Instance> list){
		mContext = ctx;
		instances = list;
	}
	
	@Override
	public int getCount() {
		return instances.size();
	}

	@Override
	public Object getItem(int pos) {
		return instances.get(pos);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		HolderView holder = null;
		
		if(rowView == null) {
			rowView = LayoutInflater.from(mContext)
					.inflate(R.layout.list_item_instance, null);
			holder = new HolderView();
			holder.txV_instanceName = (TextView) rowView.findViewById(R.id.txV_instanceName);
			holder.txV_instanceUptime = (TextView) rowView.findViewById(R.id.txV_instanceStatus);
			rowView.setTag(holder);
		}else {
			holder = (HolderView) rowView.getTag();
		}
		
		Instance ins = (Instance) getItem(position);
		holder.txV_instanceName.setText(ins.getName());
		holder.txV_instanceUptime.setText(ins.getUptime());
		
		return rowView;
	}
	
	static class HolderView {
		TextView txV_instanceName;
		TextView txV_instanceUptime;
	}
	
	public void addInstance(Instance instance) {
		instances.add(instance);
		notifyDataSetChanged();
	}
	
	public void deleteInstance(Instance instance) {
		instances.remove(instance);
		notifyDataSetChanged();
	}

}
