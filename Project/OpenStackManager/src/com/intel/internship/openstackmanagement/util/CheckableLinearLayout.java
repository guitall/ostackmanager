package com.intel.internship.openstackmanagement.util;

import com.intel.internship.openstackmanagement.R;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CheckableLinearLayout extends LinearLayout implements Checkable  {

	private CheckedTextView txV_text;
	
	public CheckableLinearLayout(Context context, AttributeSet attrs) {
		super(context,attrs);
	}
	
	@Override
    protected void onFinishInflate() {
    	super.onFinishInflate();
    	// find checked text view
		int childCount = getChildCount();
		for (int i = 0; i < childCount; ++i) {
			View v = getChildAt(i);
			if (v instanceof CheckedTextView) {
				txV_text = (CheckedTextView)v;
			}
		}    	
    }
	
	@Override
	public boolean isChecked() {
		return txV_text != null ? txV_text.isChecked() : false; 
		
	}

	@Override
	public void setChecked(boolean checked) {
		if (txV_text != null) {
    		txV_text.setChecked(checked);
    	}
		if(checked){
			this.setBackgroundColor(getResources().getColor(R.color.maroon));
			txV_text.setTextColor(Color.WHITE);
		}
		else {
			this.setBackgroundColor(getResources().getColor(R.color.blockBackgorund));
			txV_text.setTextColor(Color.BLACK);
		}
	}
	
	@Override
	public void toggle() {
		if (txV_text != null) {
    		txV_text.toggle();
    	}
	}

}
