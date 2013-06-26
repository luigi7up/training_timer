package training.timer.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import training.timer.model.DatabaseHelper;
import training.timer.R;

public class AddWorkoutsView extends Activity implements OnClickListener{
    /** Called when the activity is first created. */
	
	private SQLiteDatabase db;
	long training_id;
	
	AutoCompleteTextView workoutName;
	EditText workoutDescription;
	Spinner workoutType;
	NumberPicker workoutDuration;
	Cursor curWorkoutsOrder;
	
	AlertDialog.Builder dialogAddNew;
   
	@Override
    public void onCreate(Bundle savedInstanceState) {
        
		super.onCreate(savedInstanceState);
        setContentView(R.layout.add_edit_workout);
        
        //Get the id of parent training passed as extra to this activity
        Bundle extras = getIntent().getExtras();
        training_id = Long.parseLong(extras.getString("training_id"));
        
        //Dialog Yes/No for add new workout
        dialogAddNew = new AlertDialog.Builder(this);
        
        DatabaseHelper helper = new DatabaseHelper(this);
		db = helper.openAndReturnDb();
		
		
		//To be able to add order number to a newly added workout we get all workouts for this training
		curWorkoutsOrder = getAllWorkouts();

			
				
		workoutDescription = (EditText) findViewById(R.id.workoutDescription);
		workoutType = (Spinner) findViewById(R.id.workoutType);
		workoutDuration = (NumberPicker) findViewById(R.id.workoutDuration);
		
		workoutDuration.setCurrent(1);

		//Update the auto complete vocabulary
		String [] vocabularyArray = getWorkoutVocabulary();

		//Auto complete workout name. Gets all the names added into DB so far and suggests it
	    workoutName = (AutoCompleteTextView) findViewById(R.id.workoutName);
	    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.auto_complete_item, vocabularyArray);
	    workoutName.setAdapter(adapter);
	
        Button save = (Button)findViewById(R.id.workoutSave);
        save.setOnClickListener(this);


    }
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		db.close();
	}
	
	@Override
	public void onClick(View v){
		switch(v.getId()) {
	    	case R.id.cancel: 
				
				this.finish();
				
	        break;
	        
			case R.id.workoutSave:   
								
				final String workoutNameString = workoutName.getText().toString(); 
				final String workoutDescriptionString = workoutDescription.getText().toString();
				final String workoutTypeString = workoutType.getSelectedItem().toString();
				final String workoutDurationString = String.valueOf(workoutDuration.getCurrent());

				if(!workoutNameString.trim().isEmpty() && !workoutDurationString.equals("0") && !workoutDurationString.trim().isEmpty()){

					long last_id = saveWorkout(training_id, workoutNameString, workoutDescriptionString, workoutTypeString, workoutDurationString);
					showDialog();

				}else{
					Toast toast2 = Toast.makeText(this, "Name or duration is wrong!", 2000);
					toast2.setGravity(Gravity.CENTER, 0, 0);
					toast2.show();
				}

	        break;
		}
		
	}
	
	
	/*
	 *	Saves the new workout into the DB  
	 * 
	 */
	public long saveWorkout(long training_id, String name, String description, String type, String duration){
	
		//Get the number of workouts for this training to have the order number!
		int numberOfWorkouts = curWorkoutsOrder.getCount();
		
		ContentValues values = new ContentValues();
		
		values.put("training_id", training_id);
		values.put("order_number", numberOfWorkouts+1);
		values.put("name", name);
		values.put("description", description);
		values.put("type", type);
		values.put("duration", duration);
				
		//returns the last inserted id or -1 if error occured
		long result = db.insert("workouts", null, values);
		
		//refresh cursor with new data from DB
		curWorkoutsOrder = getAllWorkouts();
		
		return result;

	}

	//Returns the cursor of all workouts for the current training_id
	public Cursor getAllWorkouts(){
		
		return db.query("workouts", new String[]{BaseColumns._ID,"order_number"}, "training_id="+training_id, null, null, null, null);
		
	}
	
	public void showDialog(){
		//dialogAddNew.setIcon(android.R.drawable.);
		dialogAddNew.setTitle("Workout added :)");
		dialogAddNew.setMessage("Add another workout?");
		

		dialogAddNew.setPositiveButton("Yes", new DialogInterface.OnClickListener() {  
		      public void onClick(DialogInterface dialog, int which) { 
		    	  
		    	  showAddNewActivity();
		    	  return;  
		}}); 
		
		
		dialogAddNew.setNegativeButton("No", new DialogInterface.OnClickListener() {  
		      public void onClick(DialogInterface dialog, int which) { 
		    	  
		    	  finish();
		    	  return;  
		}});
		
		dialogAddNew.show();

	}
	
	
	//Method queries workouts table, retrieves all the names and returns them in String array
	public String[] getWorkoutVocabulary(){
		
		Cursor cursor = db.query("workouts", new String[]{"name"}, null, null, null, null, null);
		
		String[]vocabularyArray = new String[cursor.getCount()];
		
		cursor.moveToFirst();
		int counter=0;
		while(cursor.isAfterLast()== false){
			vocabularyArray[counter] = cursor.getString(cursor.getColumnIndex("name"));
			cursor.moveToNext();
			counter++;
		}

		return vocabularyArray;
		
	}
	
	public void showAddNewActivity(){
		//Call the same activity again
        Intent addWorkoutsIntent = new Intent(this, AddWorkoutsView.class);
        
        //pass the value of created training to the next activity
        addWorkoutsIntent.putExtra("training_id", String.valueOf(training_id));
        
        startActivity(addWorkoutsIntent);
        this.finish();
	}

}//class	