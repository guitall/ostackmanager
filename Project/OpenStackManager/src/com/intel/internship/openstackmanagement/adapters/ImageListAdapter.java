package com.intel.internship.openstackmanagement.adapters;

import java.util.List;

import com.intel.internship.openstackmanagement.R;
import com.intel.internship.openstackmanagement.adapters.InstanceListAdapter.HolderView;
import com.intel.internship.openstackmanagement.model.Image;
import com.intel.internship.openstackmanagement.model.Instance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ImageListAdapter extends BaseAdapter {

	private List<Image> images;
	private Context mContext;
	
	public ImageListAdapter (Context mContext, List<Image> image){
		this.images = image;
		this.mContext = mContext;
	}
	
	@Override
	public int getCount() {
		return images.size();
	}

	@Override
	public Object getItem(int pos) {
		return images.get(pos);
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
			holder.txV_imageName = (TextView) rowView.findViewById(R.id.txV_instanceName);
			holder.txV_imageStatus = (TextView) rowView.findViewById(R.id.txV_instanceStatus);
			rowView.setTag(holder);
		}else {
			holder = (HolderView) rowView.getTag();
		}
		
		Image img = (Image) getItem(position);
		holder.txV_imageName.setText(img.getName());
		holder.txV_imageStatus.setText(img.getStatus());
		
		return rowView;
	}
	
	static class HolderView {
		TextView txV_imageName;
		TextView txV_imageStatus;
	}
	
	
	
}
