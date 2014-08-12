package com.intel.internship.openstackmanagement.adapters;

import java.util.List;

import com.intel.internship.openstackmanagement.R;
import com.intel.internship.openstackmanagement.model.Volume;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class VolumeListAdapter extends BaseAdapter {

	private List<Volume> volumes;
	private Context mContext;
	
	public VolumeListAdapter(Context mContext, List<Volume> volumes) {
		this.volumes = volumes;
		this.mContext = mContext;
	}
	
	@Override
	public int getCount() {
		return volumes.size();
	}

	@Override
	public Object getItem(int pos) {
		return volumes.get(pos);
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
			holder.txV_instanceStatus = (TextView) rowView.findViewById(R.id.txV_instanceStatus);
			rowView.setTag(holder);
		}else {
			holder = (HolderView) rowView.getTag();
		}
		
		Volume vl = (Volume) getItem(position);
		holder.txV_instanceName.setText(vl.getName());
		holder.txV_instanceStatus.setText(vl.getStatus());
		
		return rowView; 
	}
	
	static class HolderView {
		TextView txV_instanceName;
		TextView txV_instanceStatus;
	}

	public void addVolume(Volume volume) {
		volumes.add(volume);
		notifyDataSetChanged();
	}

	public void updateRow(Volume vol) {
		for (int i = 0; i < volumes.size(); i++) {
			if(volumes.get(i).getId().compareTo(vol.getId())== 0){
				volumes.set(i, vol);
				break;
			}
		}
		notifyDataSetChanged();
	}

	public void deleteInstance(Volume volume) {
		volumes.remove(volume);
		notifyDataSetChanged();
	}

}
