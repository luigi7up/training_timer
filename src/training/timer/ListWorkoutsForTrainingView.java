package training.timer;

import android.app.Activity;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ListWorkoutsForTrainingView extends ListActivity{

	 
	private SQLiteDatabase db;
	private ListAdapter  listAdapter;
	private Cursor cursor;
	private long training_id;
	
	private static final String fields[] = {BaseColumns._ID, "training_id", "order_number", "name", "description", "type", "duration"};

	@Override
    public void onCreate(Bundle savedInstanceState) {
        
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_workouts);
	
		DatabaseHelper helper = new DatabaseHelper(this);
		db = helper.openAndReturnDb();
		
		//Get the id of parent training passed as extra to this activity
		Bundle extras = getIntent().getExtras();
		training_id = Long.parseLong(extras.getString("training_id"));
		
		cursor = db.query("workouts", // Table name
				fields, // String[] containing your column names
		        "training_id = "+training_id, // your where statement, you do not include the WHERE or the FROM DATABASE_TABLE parts of the query,
		        null,
		        null,
		        null,
		        "order_number",
		        null
		       );

		startManagingCursor(cursor);
		
		listAdapter = new SimpleCursorAdapter(this,
				R.layout.list_workouts_item, 
				cursor, 
				fields,
				new int[] { R.id.workout_name, R.id.workout_description,R.id.workout_type, R.id.workout_duration }
		);
		
		setListAdapter(listAdapter);
		
		//enable context popup
		registerForContextMenu(getListView());  
    }
	
	/*** CONTEXT MENU ***/
	@Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.workout_list_context, menu);
    }
	
	
	@Override
    public boolean onContextItemSelected(MenuItem item) {
       
		//GET CONTEXT ITEM DETAILS
		AdapterView.AdapterContextMenuInfo info;
		
		try {
            info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        } catch (ClassCastException e) {
            Log.e("", "bad menuInfo", e);
            return false;
        }
		
		
		//Get the ID of the item in the list adapter!!! It is not a position in a list
        long id = getListAdapter().getItemId(info.position);        

        //Switch between Delete/Edit context actions
        switch (item.getItemId()) {
		    
        	case R.id.moveUp:        		
        		moveUp(id);        		
        		return true;
        	case R.id.moveDown:        		
        		moveDown(id);
        		return true;
        	
        	case R.id.delete:
	
		          deleteWorkout(id); //ID of item should be passed to method deleteitem
		          
		          //refresh cursor and list
		          cursor.requery();
		          Toast.makeText(this, "Deleting...", Toast.LENGTH_LONG).show();
		          return true;
		          
		    default:
		    	return super.onContextItemSelected(item);
	  }
        
    }

	
	/***	MENU	***/
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.list_workouts_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		int item_id = item.getItemId();
		
		switch(item_id){
			case R.id.list_workouts_menu_add:
				
				//Call add training screen
		        Intent addWorkoutIntent = new Intent(this, AddWorkoutsView.class);
		        
		        addWorkoutIntent.putExtra("training_id", String.valueOf(training_id));
		        startActivity(addWorkoutIntent); 				
			break;
		}
		
		return true;
	}

	/*	Method receives the id of the workout, finds out which workout is previous in the cursor and 
	 * makes an DB update switching the order_number values*/
	public void moveUp(long id){
	
		int idOfPrevious = -1;
		int orderNumberOfPrevious = -1;
		int orderCurrent = -1;
		
		//Find the position in Cursor of the element with this id
		cursor.moveToFirst();
		while(cursor.isAfterLast() == false){
			
			//When you get to the cursor element with _ID == id
			if(cursor.getString(cursor.getColumnIndex(BaseColumns._ID)).equals(String.valueOf(id))){
				if(cursor.isFirst() == false){
					//Get the order field value
					orderCurrent = cursor.getInt(cursor.getColumnIndex("order_number"));
					
					//Return one position back
					cursor.moveToPrevious();
					
					//Get the id of the previous
					idOfPrevious = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID));
					orderNumberOfPrevious = cursor.getInt(cursor.getColumnIndex("order_number"));
					
					switchOrder(id, orderCurrent, idOfPrevious, orderNumberOfPrevious);
					
					cursor.requery();
					
				}

				break;
			}
			
			cursor.moveToNext();
		}
		
		Log.d("XXnX","id of previous: "+idOfPrevious);	
		Log.d("XXX","order of previous: "+orderNumberOfPrevious);
		
		//Log.d("XXX","position is: "+id);

	}
	
	/*	Method receives the id of the workout that needs to be moved down in the list, 
	 * finds out which workout is the next one in the list/cursor makes an DB update 
	 * by calling switchOrder method
	 * @param id is the id of the element in the list that needs to be moved
	 * */
	public void moveDown(long id){
	
		int idOfNext = -1;
		int orderNumberOfNext = -1;
		int orderCurrent = -1;
		
		//Find the position in Cursor of the element with this id
		cursor.moveToFirst();
		while(cursor.isAfterLast() == false){
			
			//When you get to the cursor element with _ID == id
			if(cursor.getString(cursor.getColumnIndex(BaseColumns._ID)).equals(String.valueOf(id))){
				if(cursor.isLast() == false){
					//Get the order field value
					orderCurrent = cursor.getInt(cursor.getColumnIndex("order_number"));
					
					//Return one position back
					cursor.moveToNext();
					
					//Get the id of the previous
					idOfNext = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID));
					orderNumberOfNext = cursor.getInt(cursor.getColumnIndex("order_number"));
					
					switchOrder(id, orderCurrent, idOfNext, orderNumberOfNext);
					
					cursor.requery();
					
				}

				break;
			}
			
			cursor.moveToNext();
		}
		
		Log.d("XXnX","id of previous: "+idOfNext);	
		Log.d("XXX","order of previous: "+orderNumberOfNext);
		
		//Log.d("XXX","position is: "+id);

	}
	
	public void switchOrder(long idCurrent, long orderCurrent, long idPrevious, long orderPrevious){
		
		
		Log.d("XXX", idCurrent+"  "+orderCurrent+"  "+idPrevious+"  "+orderPrevious);
	
		String query = "UPDATE workouts SET order_number="+orderPrevious+" WHERE "+BaseColumns._ID+"="+idCurrent;
	Log.d("QUERRRRRY", query);			
		db.execSQL(query);
		
		String query2 = "UPDATE workouts SET order_number="+orderCurrent+" WHERE "+BaseColumns._ID+"="+idPrevious;
		Log.d("QUERRRRRY", query2);			
			db.execSQL(query2);
		
		cursor.requery();
	
	
	}
	
	public void deleteWorkout(long id){
		
		//db.delete("trainings", BaseColumns._ID+"="+id, null);
		db.delete("workouts", BaseColumns._ID+"="+id, null);

	}
	
	/*	Click on item in the list*/
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		//Call add training screen
        Intent runWorkoutActivity = new Intent(this, RunTrainingWorkoutsView.class);
        
        //pass the value of created training to the next activity
        runWorkoutActivity.putExtra("training_id", String.valueOf(training_id));
        
        runWorkoutActivity.putExtra("workout_id", String.valueOf(id));
        
        startActivity(runWorkoutActivity);

	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		db.close();
	}
	
	

	



}//class	



