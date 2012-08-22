package training.timer;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


/**
 * This activity shows the window for adding a new training into DB
 * */
public class AddTrainingView extends Activity implements OnClickListener{
	
	private SQLiteDatabase db;
	
	EditText trainingName;
	EditText trainingDescription;
   
	@Override
    public void onCreate(Bundle savedInstanceState) {
        
		super.onCreate(savedInstanceState);
        setContentView(R.layout.add_edit_training);
        
        DatabaseHelper helper = new DatabaseHelper(this);
		db = helper.openAndReturnDb();
        
        Button cancel = (Button)findViewById(R.id.cancel);
        Button save = (Button)findViewById(R.id.save);
    
        cancel.setOnClickListener(this);
        save.setOnClickListener(this);
        
        trainingName = (EditText) findViewById(R.id.trainingName);
        trainingDescription = (EditText) findViewById(R.id.trainingDescription);

    }
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		db.close();
	}
	
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
	    	case R.id.cancel:  
	    		//Log.d("XXXXXXX","Cancel pressed");
				//Toast toast = Toast.makeText(AddTrainingView.this, "Canceling...", 1000);
				//toast.setGravity(Gravity.CENTER, 0, 0);
				//toast.show();
				
				this.finish();
				
	        break;
	        
			case R.id.save:   
								
				final String trainingNameString = trainingName.getText().toString(); 
				final String trainingDescriptionString = trainingDescription.getText().toString();
				
				if(!trainingNameString.trim().isEmpty()){
					
					//Save the training and get last_id
					long last_id = saveTraining(trainingNameString, trainingDescriptionString);
					
					//Call add training screen
			        Intent addWorkoutsIntent = new Intent(this, AddWorkoutsView.class);
			        
			        //pass the value of created training to the next activity
			        addWorkoutsIntent.putExtra("training_id", String.valueOf(last_id));
			      	
			        Toast toast2 = Toast.makeText(AddTrainingView.this, "Saving... ", 1000);
			      	toast2.setGravity(Gravity.CENTER, 0, 0);
					toast2.show();
					
					startActivity(addWorkoutsIntent); 
					
					toast2.cancel();
					
					this.finish();
					
						
					
				}else{
					Toast toast2 = Toast.makeText(AddTrainingView.this, "No name provided!", 0);
					toast2.setGravity(Gravity.CENTER, 0, 0);
					toast2.show();
				}


	        break;
		}
		
	}
	
	/*
	 *	Saves the new training into the DB and returns id of newly created entry. returns -1 if save didn't happen  
	 * 
	 */
	public long saveTraining(String name, String description){
	
		ContentValues values = new ContentValues();
				
		values.put("name", name);
		values.put("description", description);
				
		//returns the last inserted id or -1 if error occured
		return db.insert("trainings", null, values);

	}



}//class	