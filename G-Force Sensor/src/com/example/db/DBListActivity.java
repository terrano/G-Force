package com.example.db;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.g_forcesensor.R;
import android.widget.ListView;
import android.widget.AbsListView.MultiChoiceModeListener;


public class DBListActivity extends ListActivity implements LoaderCallbacks<Cursor>{
	
	private DBListActivityCursorAdapter adapter;
	private ShakeDBHandler db = null;	
	private Cursor cursor = null;
	
	ListView lv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.getListView().setDivider(new ColorDrawable(this.getResources().getColor(R.color.white)));
		this.getListView().setDividerHeight(1);		
		
		this.db = new ShakeDBHandler(this);
		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		
		this.adapter = new DBListActivityCursorAdapter(this, null);
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		getLoaderManager().getLoader(0).forceLoad();
		setListAdapter(adapter);
		
		lv = getListView();
		
		getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		getListView().setMultiChoiceModeListener(new MultiChoiceModeListener() {
			
			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				switch (item.getItemId()) {
				case R.id.delete:
					db.dropMultipleJumps(getListView().getCheckedItemIds());
					getLoaderManager().getLoader(0).forceLoad();
					mode.finish();
					return true;
				case R.id.deleteAll:
					
					AlertDialog.Builder ad = new AlertDialog.Builder(DBListActivity.this);
					ad.setTitle(R.string.rusure);
					ad.setMessage(R.string.delete_all_question);
					ad.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							getLoaderManager().destroyLoader(0);
							db.closeDB(); 
							db = null;	
							cursor.close();
							cursor = null;
							
							getApplicationContext().deleteDatabase(ShakeDBHandler.DATABASE_NAME);
							
							db = new ShakeDBHandler(getApplicationContext());
							getLoaderManager().initLoader(0, null, DBListActivity.this);
							getLoaderManager().getLoader(0).forceLoad();
						}
					});
					ad.setNeutralButton(R.string.no, new DialogInterface.OnClickListener() {						
						@Override
						public void onClick(DialogInterface dialog, int which) {}
					});
					
					ad.show();
					mode.finish();
					
					return true;
				default:
					mode.finish();
					return false;
				}
			}

			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				mode.getMenuInflater().inflate(R.menu.action_menu, menu);
				mode.setTitle("");
				return true;
			}

			@Override
			public void onDestroyActionMode(ActionMode mode) {}

			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				return false;
			}

			@Override
			public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
				final int checkedCount = getListView().getCheckedItemCount();
				mode.setTitle(checkedCount + " Selected");
			}
		});
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		getLoaderManager().restartLoader(0, null, this);
		getLoaderManager().destroyLoader(0);
		this.db.closeDB(); 
		this.db = null;	
		this.cursor.close();
		this.cursor = null;
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		this.db = new ShakeDBHandler(this);
		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		return new MyCursorLoader(this, this.db);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		this.cursor = cursor;		
		this.adapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		this.adapter.changeCursor(null);
	}
	
	static class MyCursorLoader extends CursorLoader {
		ShakeDBHandler db;
		
		public MyCursorLoader(Context context, ShakeDBHandler db) {
			super(context);
			this.db = db;
		}

		@Override
		public Cursor loadInBackground() {	
			Cursor cursor = db.getList();
			return cursor;
		}
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent intent = new Intent(this, ShakeViewActivity.class);
		intent.putExtra(ShakeDBHandler.COLUMN_ID, id);
		startActivity(intent);
	}
}
