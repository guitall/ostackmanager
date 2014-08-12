package com.intel.internship.openstackmanagement.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.intel.internship.openstackmanagement.R;

import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class Tools {

	public static void showToast(Context context, int msj) {
		Toast.makeText(context, msj, Toast.LENGTH_SHORT).show();
	}

	public static void showToast(Context context, String msj) {
		Toast.makeText(context, msj, Toast.LENGTH_SHORT).show();
	}

	/**
	 * Create a dialog simply that allow show a message
	 * 
	 * @param Context
	 *            context
	 * @param String
	 *            title of dialog
	 * @param String
	 *            msj of dialog
	 */
	public static void showDialogSimply(Context context, String title,
			String msj) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		dialog.setTitle(title);
		dialog.setMessage(msj);
		dialog.show();
	}

	/**
	 * Create a dialog simply that allow show a message
	 * 
	 * @param Context
	 *            context
	 * @param int title of dialog
	 * @param int msj of dialog
	 */
	public static void showDialogSimply(Context context, int title, int msj) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		dialog.setTitle(title);
		dialog.setMessage(msj);
		dialog.show();
	}

	/**
	 * Create a dialog simply that allow show a message
	 * 
	 * @param Context
	 *            context
	 * @param int title of dialog
	 * @param String
	 *            msg of dialog
	 */
	public static void showDialogSimply(Context context, int title, String msj) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		dialog.setTitle(title);
		dialog.setMessage(msj);
		dialog.show();
	}

	/**
	 * Allows return a String thought a resource
	 * 
	 * @return
	 */
	public static String loadResoruceString(Context cntx, int res) {
		return cntx.getString(res);

	}

	/**
	 * Check if the device is connected to a network. In the case that the user
	 * has more than one network enable, such as wifi and data (3g, 4g), it
	 * checks the active network connectivity state.
	 * 
	 * @param Context
	 * 
	 * @return boolean true if the active network is connected, false otherwise
	 */
	public static boolean isConnectivityAvailable(Context context) {
		
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivity.getActiveNetworkInfo();

		return (info != null) ? info.isConnected() : false;
	}

	public static void notifyNoConnextion(Context ct) {
		showDialogSimply(ct, R.string.title_dialog_no_connectivity,
				R.string.dialog_no_connectivity);
	}
	
	public static Date getFirstCurrentMonthDate(){
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(new Date());
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		Calendar c = Calendar.getInstance();
		c.set(year, month, 1);
		return c.getTime();
	}
	
	public static String formatDate(Date dateToFormat, String format){
		SimpleDateFormat sdf = new  SimpleDateFormat(format);
		return sdf.format(dateToFormat);
	}
	
	
	 public static Date getDateObject(String fecha){
	       try {
	        	int year = Integer.parseInt(fecha.substring(0, 4));
	        	int month = Integer.parseInt(fecha.substring(5, 7));
	            int day = Integer.parseInt(fecha.substring(8, 10));
	            Calendar c = Calendar.getInstance();
	    		c.set(year, month-1, day);
	    		return c.getTime();
	            
	        } catch (Exception e) {
	            return null;
	        }
	        
	 }
	 
	 public static Date setTimeToDate(Date date, int hour,int minute, int second){
		 GregorianCalendar calendar = new GregorianCalendar();
		 calendar.setTime(date);
		 int year = calendar.get(Calendar.YEAR);
		 int month = calendar.get(Calendar.MONTH);
		 int day = calendar.get(Calendar.DAY_OF_MONTH);
		 Calendar c = Calendar.getInstance();
 		 c.set(year, month, day);
 		 c.set(Calendar.HOUR, hour);
 		 c.set(Calendar.MINUTE, minute);
 		 c.set(Calendar.SECOND, second);
		 return c.getTime();
	}

	
	
	
}
