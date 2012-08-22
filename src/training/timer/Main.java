package training.timer;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Main extends Activity implements OnClickListener {
    /** Called when the activity is first created. */
	private Button addTraining;
	private Button listTrainings;
	
	

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        addTraining = (Button)findViewById(R.id.addTraining);
        listTrainings = (Button)findViewById(R.id.listTrainings);
        
        addTraining.setOnClickListener(this);
        listTrainings.setOnClickListener(this);

    }
    
	//@Override
	public void onClick(View v){
		switch(v.getId()) {
	    	case R.id.listTrainings:  
	    		
		        //Call add training screen
		        Intent listTrainingsIntent = new Intent(Main.this, ListTrainingsView.class);
		        startActivity(listTrainingsIntent); 	
		        
	        break;
	        
			case R.id.addTraining:   
		        //Call add training screen
		        Intent addTrainingIntent = new Intent(Main.this, AddTrainingView.class);
		        startActivity(addTrainingIntent); 	        
	        break;
		}
		
	}
}