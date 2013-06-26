package training.timer.view;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import training.timer.model.DatabaseHelper;
import training.timer.R;


/**
 * This activity shows the window for adding a new training into DB
 * */
public class EditTrainingView extends Activity implements OnClickListener{
	
	private SQLiteDatabase db;
	private long training_id;
	
	private Cursor cursor;
	
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
        
        //Get the id of the training passed from previous activity
		Bundle extras = getIntent().getExtras();
		training_id = Long.parseLong(extras.getString("training_id"));
        
		trainingName = (EditText) findViewById(R.id.trainingName);
        trainingDescription = (EditText) findViewById(R.id.trainingDescription);
        
        //Get the data from DB for this training
		cursor = db.rawQuery("SELECT * FROM trainings WHERE _id="+training_id, null);
		cursor.moveToFirst();
		
		//Enter the training name and description from DB into the text fields
		trainingName.setText(cursor.getString(cursor.getColumnIndex("name")));
		trainingDescription.setText(cursor.getString(cursor.getColumnIndex("description")));
        
        

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
	    		
				this.finish();
				
	        break;
	        
			case R.id.save:   
								
				final String trainingNameString = trainingName.getText().toString(); 
				final String trainingDescriptionString = trainingDescription.getText().toString();
				
				if(!trainingNameString.trim().isEmpty()){

					updateTraining(training_id);
					
			        Toast toast2 = Toast.makeText(EditTrainingView.this, "Saving... ", 0);
			      	toast2.setGravity(Gravity.CENTER, 0, 0);
					toast2.show();
					
					
					toast2.cancel();
					
					this.finish();
					
						
					
				}else{
					Toast toast2 = Toast.makeText(EditTrainingView.this, "No name provided!", 0);
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
	public void updateTraining(long id){
	
		String name = trainingName.getText().toString();
		String description = trainingDescription.getText().toString();
		
		ContentValues contentValues = new ContentValues();
		contentValues.put("name", name);
		contentValues.put("description", description);
		db.update("trainings", contentValues, BaseColumns._ID + "=" + training_id, null);

	}



}//class	
