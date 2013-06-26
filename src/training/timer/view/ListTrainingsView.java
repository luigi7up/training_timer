/*
 * Activity reads a table of all trainings in the DB and displays it to a user as a ListView
 * */

package training.timer.view;

import android.app.ListActivity;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import training.timer.model.DatabaseHelper;
import training.timer.R;
import training.timer.model.TrainingsListCursorAdapter;

public class ListTrainingsView extends ListActivity{

	 
	private SQLiteDatabase db;
	private TrainingsListCursorAdapter listAdapter;
	private Cursor cursor;
	private Display display;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_trainings);

		DatabaseHelper helper = new DatabaseHelper(this);
		db = helper.openAndReturnDb();
		
		//Get the width of the screen to adjust list items width and pass it to the list cursor adapter to create the items
		display = getWindowManager().getDefaultDisplay(); 
		int currentWidth= display.getWidth(); 

		//Return all details for every training
		cursor = db.rawQuery("SELECT _id, t.name ,t.description, duration, num_of_workouts FROM trainings t LEFT OUTER JOIN  (SELECT training_id, SUM(duration) as duration, COUNT(_id) as num_of_workouts FROM workouts GROUP BY (training_id)) temp ON t._id = temp.training_id", null);
	
		startManagingCursor(cursor);
	
		listAdapter = new TrainingsListCursorAdapter(this,
				R.layout.list_trainings_item, 
				cursor, 
				new String []{"name", "description"},
				new int[] {R.id.name_entry, R.id.description_entry },
				currentWidth
		);
		
		//Add the style of pressed item in the list
		
		
		//listAdapter.setSelector( R.drawable.listselector);
		
		
		
		setListAdapter(listAdapter);

		//enable context popup
		registerForContextMenu(getListView());
		
		
       
    }
	
	@Override
	public void onDestroy(){
		super.onDestroy();

		db.close();
	}
	
	
	public void onConfigurationChanged(Configuration newConfig) {
		  super.onConfigurationChanged(newConfig);
		  setContentView(R.layout.list_trainings);
		  	//Get the width of the screen to adjust list items width and pass it to the list cursor adapter to create the items
		  
			int currentWidth = display.getWidth(); 			
			listAdapter.setOrientationWidth(currentWidth);
				
			//enable context popup
			registerForContextMenu(getListView());
			
		  }
	
	
	/*	List items selected*/
	/*
	 * Method responds to cliscks on items in the list	
	 * */	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		//Call add training screen
        Intent listWorkoutsForTraining = new Intent(this, ListWorkoutsForTrainingView.class);
        
        //pass the value of created training to the next activity
        listWorkoutsForTraining.putExtra("training_id", String.valueOf(id));
        
        startActivity(listWorkoutsForTraining);

	}
	
	/***	MENU	***/
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.list_trainings_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		int item_id = item.getItemId();
		
		switch(item_id){
			case R.id.list_training_menu_add:
				
				//Call add training screen
		        Intent addTrainingIntent = new Intent(this, AddTrainingView.class);
		        startActivity(addTrainingIntent); 				
			break;
		}
		
		return true;
	}
	
	
	/*** CONTEXT MENU ***/
	@Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.training_list_context, menu);
    }
	
	@Override
    public boolean onContextItemSelected(MenuItem item) {
       
		AdapterView.AdapterContextMenuInfo info;
		
		try {
            info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        } catch (ClassCastException e) {
            Log.e("", "bad menuInfo", e);
            return false;
        }
		
		
		//Get the ID of the item in the CURSOR!!! It is not a position in a list
        long id = getListAdapter().getItemId(info.position);        
		

        //Switch between Delete/Edit context actions
        switch (item.getItemId()) {
		    case R.id.delete:
	
		          deleteTraining(id); //ID of item should be passed to method deleteitem
		          
		          //refresh cursor and list
		          cursor.requery();
		          
		          Toast.makeText(this, "Deleting...", 2000).show();
		         
		          return true;
		          
		    case R.id.edit:
		    	//Call the edit activity passing the training id

		        Intent editTraining = new Intent(this, EditTrainingView.class);
		        
		        //pass the value of created training to the next activity
		        editTraining.putExtra("training_id", String.valueOf(id));

		        startActivity(editTraining); 
		    	
		    	return true;
		          
		    default:
		    	return super.onContextItemSelected(item);
	  }
        
    }
	
	public void deleteTraining(long id){
		
		//db.delete("trainings", BaseColumns._ID+"="+id, null);
	
		db.delete("trainings", BaseColumns._ID+"="+id, null);
		db.delete("workouts", "training_id="+id, null);
	}
	
	
	
	

	



}//class	


