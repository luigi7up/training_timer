package training.timer.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;

public class CounterClockView extends View {

	
    Paint paint = new Paint();
    Paint paint2 = new Paint();
    
    final RectF rect = new RectF();
    final RectF rect2 = new RectF();

    long counterArcAngle = 0;

    //Example values
  
    public CounterClockView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        
        paint.setColor(Color.GRAY);
        paint.setStrokeWidth(30);
        paint.setAntiAlias(true);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        
        paint2.setColor(Color.RED);
        paint2.setStrokeWidth(30);
        paint2.setAntiAlias(true);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint2.setStyle(Paint.Style.STROKE);
        
  this.setBackgroundColor(Color.WHITE);

    }

    @Override
    public void onDraw(Canvas canvas) {

    	//Size of the circle 
    	rect.set(20, 20, 150, 150);
    	rect2.set(20, 20, 150, 150);
    	
    	canvas.drawArc(rect, -90, 360, false, paint);
    	canvas.drawArc(rect2, -90, counterArcAngle, false, paint2);
    	
    	invalidate();
    	
    }
    

     

}