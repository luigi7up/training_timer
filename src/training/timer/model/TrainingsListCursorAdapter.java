package training.timer.model;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import training.timer.R;


/**
 * This class is used to Adapt data from db to a simple list. It differs from SimpleCursorAdapter 
 * by adding a counter of current item displayed in the list. This way our list items are numerated
 * 
 * */
public class TrainingsListCursorAdapter extends SimpleCursorAdapter{
	
	private Context context;
	private int layout;
	private int orientationWidth;

	public TrainingsListCursorAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to, int orientationWidth) {
		
		
		super(context, layout, c, from, to);
		
		this.context = context;
		this.layout = layout;
		this.orientationWidth = orientationWidth; 
		
		
		
		
	}//contructor


    @Override
    public void bindView(View v, Context context, Cursor c) {

    	String name         = c.getString(c.getColumnIndex("name"));
    	String description  = c.getString(c.getColumnIndex("description"));
    	String order        = String.valueOf(c.getPosition()+1);
    	
    	String duration = minutesForSeconds(c.getString(c.getColumnIndex("duration")));
    	String workoutsInTraining = c.getString(c.getColumnIndex("num_of_workouts"));
  
    	//Set the values for layout elements
        TextView name_text = (TextView) v.findViewById(R.id.name_entry);
        name_text.setWidth(orientationWidth-100);
        
        
        TextView description_text = (TextView) v.findViewById(R.id.description_entry);
        description_text.setWidth(orientationWidth-95);
        
        TextView duration_text = (TextView) v.findViewById(R.id.duration);
        TextView order_text = (TextView) v.findViewById(R.id.list_item_num);
       
        TextView workoutsInTraining_text = (TextView) v.findViewById(R.id.workoutsInTraining);

        if (name_text != null) {
        	name_text.setText(name);
        }
        if (description_text != null) {
        	description_text.setText(description);
        }
        if (order_text != null) {
        	order_text.setText(order);
        }
        if (duration_text != null) {
        	duration_text.setText(duration);
        } 
        if (workoutsInTraining_text != null) {
        	workoutsInTraining_text.setText(workoutsInTraining);
        } 
        
    }
    
    public int getOrientationWidth(){
    	return this.orientationWidth;
    }
    public void setOrientationWidth(int width){
    	this.orientationWidth = width;
    }
	
	public String minutesForSeconds(String input){

		
		if(input != null){
			double seconds = Double.valueOf(input);
			double minutes = Math.floor(seconds/60);
			  
			
			return String.valueOf((int)(minutes));
		}else return "0";
		
		
	}
	
	

	
	
}//class
