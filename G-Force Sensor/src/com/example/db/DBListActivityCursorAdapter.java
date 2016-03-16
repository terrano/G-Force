package com.example.db;

import com.example.g_forcesensor.R;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DBListActivityCursorAdapter extends CursorAdapter{

	private LayoutInflater mInflater;
	
	private class ViewHolder {
		TextView g_max_value;
		TextView mainText;
		TextView subText;
	}
	
	@SuppressWarnings("deprecation")
	public DBListActivityCursorAdapter(Context context, Cursor cursor) {
		super(context, cursor);
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);		
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		int sdk = android.os.Build.VERSION.SDK_INT;
		if (cursor.getPosition() % 2 == 1) {			
			if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
				view.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.bg_list_light_purple_before_16));
			} else {
				view.setBackground(context.getResources().getDrawable(R.drawable.bg_list_light_purple));
			}
		} else {
			if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
				view.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.bg_list_dark_purple_before_16));
			} else {
				view.setBackground(context.getResources().getDrawable(R.drawable.bg_list_dark_purple));
			}
		}
		
		ViewHolder vh = (ViewHolder) view.getTag();

		vh.mainText.setText(" " + cursor.getString(cursor.getColumnIndex(ShakeDBHandler.DURATION_COLUMN)) + "ms");
		vh.subText.setText(cursor.getString(cursor.getColumnIndex(ShakeDBHandler.DATE_OF_SHAKE_COLUMN)));
		vh.g_max_value.setText(cursor.getString(cursor.getColumnIndex(ShakeDBHandler.SHAKE_MAX_COLUMN)));
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
		View view = mInflater.inflate(R.layout.list_row, viewGroup, false);

		ViewHolder vh = new ViewHolder();
		vh.mainText = (TextView) view.findViewById(R.id.label);
		vh.subText = (TextView) view.findViewById(R.id.subscript);
		vh.g_max_value = (TextView) view.findViewById(R.id.g_value);
		
		view.setTag(vh);
		
		return view;
	}
}
