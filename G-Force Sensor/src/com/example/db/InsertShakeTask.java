package com.example.db;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

public class InsertShakeTask extends AsyncTask<Uri, Void, Void>{
	
	private ShakeObject jo = null;
	private ShakeDBHandler jdb = null;
	private Context context = null;
	
	public InsertShakeTask(Context context, ShakeObject jo) {
		this.jo = jo;
		this.context = context;
	}

	@Override
	protected Void doInBackground(Uri... params) {
		if ((this.jo != null) && (this.context != null)){
			if ((this.jo.getShakeName() != null) && (this.jo.getmDataset() != null) && (this.jo.getDate() != null)) {
				this.jdb = new ShakeDBHandler(this.context);
				this.jdb.insert(jo);
				this.jdb.closeDB();
				this.context = null;
				this.jdb = null;
			}			
			this.jo.onDestroy();
			this.jo = null;
		}		
		return null;
	}
}
