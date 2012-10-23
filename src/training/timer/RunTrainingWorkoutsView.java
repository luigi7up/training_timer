package training.timer;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class RunTrainingWorkoutsView extends Activity implements OnClickListener{
	
	private SQLiteDatabase db;
	private Cursor cursor;
	
	private long training_id;
	private long workout_id;
	
	
	private MediaPlayer mp;
	
	private long workout_total;
	private  long current_second = 0;
	
	boolean isPaused = false;
	
	private long next_workout_id;
	
	private boolean moreWorkoutsLeft = false;
	
	Button btnPauseResume;	
	TextView txtTimer, txtNextWorkout, txtWorkoutName, txtWorkoutDescription;

	CounterClockView counterClockView;
	CountDownTimer countDownTimer;
	
	private static final String fields[] = {BaseColumns._ID, "training_id", "name", "description", "type", "duration"};

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		setContentView(R.layout.run_workout);
		
		DatabaseHelper helper = new DatabaseHelper(this);
		db = helper.openAndReturnDb();

		txtWorkoutName = (TextView) findViewById(R.id.workout_name);
		txtWorkoutDescription = (TextView) findViewById(R.id.workout_description);

		txtTimer = (TextView) findViewById(R.id.timer_text);
		txtNextWorkout = (TextView) findViewById(R.id.next_workout);
		
		//Get the id of parent training passed as extra to this activity
		Bundle extras = getIntent().getExtras();
		training_id = Long.parseLong(extras.getString("training_id"));
		workout_id = Long.parseLong(extras.getString("workout_id"));

		//Add the graphic counter to the layout
		counterClockView = (CounterClockView) findViewById(R.id.counter_clock_surface);
		
		//Pause-Resume button
		btnPauseResume = (Button)findViewById(R.id.pause_resume);
		btnPauseResume.setOnClickListener(this);
	
		cursor = db.query("workouts", // Table name
				fields, // String[] containing your column names
		        "training_id = "+training_id, // your where statement, you do not include the WHERE or the FROM DATABASE_TABLE parts of the query,
		        null,
		        null,
		        null,
		        null
		);
		
		startManagingCursor(cursor);
		cursor.moveToFirst();
		
		//Find and Set next_workout_id, duration of the current etc.
		while (cursor.isAfterLast() == false) {
        	
			//Find next workout in cursor
        	long currentCursorID = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
        	
        	if(currentCursorID  == workout_id){
        		//We are in current workout row in cursor so get the details for it
        		workout_total = cursor.getLong(cursor.getColumnIndex("duration"));	
        		txtWorkoutName.setText(cursor.getString(cursor.getColumnIndex("name")));
        		txtWorkoutDescription.setText(cursor.getString(cursor.getColumnIndex("description")));
        		
        		//Get the name of the following workout in the cursor
        		if(!cursor.isLast()){
        			cursor.moveToNext();
        			next_workout_id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
            		String nextWorkoutName = cursor.getString(cursor.getColumnIndex("name"));
            		txtNextWorkout.append(nextWorkoutName);
            		moreWorkoutsLeft = true;
        		}else{
        			moreWorkoutsLeft = false;
        			txtNextWorkout.append("NONE!");
        		}
        		
        		break;
        	}        	
        	cursor.moveToNext(); 
        }

        startResumeCounter();
		
	}
	

	public void startResumeCounter(){

		int timerToGo;
		
		if(current_second != 0) timerToGo = (int) (workout_total - current_second);
		else timerToGo = (int) workout_total;
		
		//Create a countdown object that will update the views (namelly counterClockView)
		countDownTimer = new CountDownTimer((timerToGo+2) * 1000, 1000) {
		     
			public void onTick(long millisUntilFinished) {

				//Resume counter from current_second if current second isn't 0	
				if(current_second != 0) millisUntilFinished = current_second * 1000;
			
				if(current_second == workout_total) counterClockView.counterArcAngle = 360;
			
				else counterClockView.counterArcAngle = (current_second*360/workout_total);

				//View has to be invalidated on every tick
				//counterClockView.invalidate();
				
		    	 if(current_second == 0){
		    		 playSound(R.raw.start_bell);
		    	 }
		    	 else if(current_second == workout_total){
		    		 playSound(R.raw.positive);
		    		 
		    	 }else if(current_second >= workout_total-3){
    		 
		    		 playSound(R.raw.vector);
		    	 }
		   
		    	 
	txtTimer.setText(String.valueOf(workout_total-current_second));
	
	Log.d("XXX", "Workout_dur"+workout_total);
		    	 
		    	 current_second++;
 
		     }
			
			// When timer comes to 0 call the next workout in new activity
		     public void onFinish() {
		 		
		    	 //Call the same activity passing the next workout details
		         Intent runWorkoutActivity = new Intent(RunTrainingWorkoutsView.this, RunTrainingWorkoutsView.class);
		         
		         if(moreWorkoutsLeft){
		        	//pass the value of created training to the next activity
			         runWorkoutActivity.putExtra("training_id", String.valueOf(training_id));
			         runWorkoutActivity.putExtra("workout_id", String.valueOf(next_workout_id));		         
			         finish();
			         startActivity(runWorkoutActivity);		    
			         
		         }else finish();

		     }
		  };
		
		countDownTimer.start();
		
	}
	
	public void onClick(View v) {
		
		switch(v.getId()){
			case R.id.pause_resume: 
				if(isPaused){
					startResumeCounter();
					isPaused = false;
				}
				else {
					countDownTimer.cancel();
					isPaused = true;
				}
		        break;
		  	}	
	}
	
	public void playSound(int sound){

		this.mp = MediaPlayer.create(this, sound);
		this.mp.start();
	Log.d("XXX","SOUUUUUUND!!!!");
		
	}
	
	
	
	//Deal wit rotation of the screen yourself because by default the activity is restarted
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		  super.onConfigurationChanged(newConfig);
		  
	}
	
	
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		
		Log.d("XXX", "onDestroy called");
		
		countDownTimer.cancel();
		
		//mp.release();
		finish();
		//mp.stop();
		
	}


	
	

}
