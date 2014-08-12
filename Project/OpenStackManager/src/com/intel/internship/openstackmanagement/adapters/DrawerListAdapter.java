package com.intel.internship.openstackmanagement.adapters;

import com.intel.internship.openstackmanagement.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

public class DrawerListAdapter extends ArrayAdapter<String>{

	private Context mContext;
	
	public DrawerListAdapter(Context context, int resource, String[] objects) {
		super(context, resource, objects);
		this.mContext = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		HolderView holder = null;
		
		if (rowView == null) {
			rowView = LayoutInflater.from(parent.getContext())
					.inflate(R.layout.drawer_item_list, null);
			holder = new HolderView();
			holder.txV_featureTitle = (CheckedTextView) rowView.findViewById(R.id.txV_featureTitle);
			rowView.setTag(holder);
		}else{
			holder = (HolderView) rowView.getTag();
		}
		
		String title = getItem(position);
		holder.txV_featureTitle.setText(title);
		return rowView;
	}
	
	static class HolderView {
//		TextView txV_featureTitle;
		CheckedTextView txV_featureTitle;
	}
	

}
