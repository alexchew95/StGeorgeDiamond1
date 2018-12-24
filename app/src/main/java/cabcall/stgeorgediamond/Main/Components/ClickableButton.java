package cabcall.stgeorgediamond.Main.Components;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.widget.ImageView;
import android.util.AttributeSet;
import android.view.*;
//import android.view.View.OnTouchListener;
import android.graphics.drawable.*;
import android.graphics.drawable.shapes.RoundRectShape;
import cabcall.stgeorgediamond.R;
//import android.graphics.*;

public class ClickableButton extends ImageView {
	private ShapeDrawable Backshade;
	private boolean FirstTime;
	
	
	public ClickableButton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public ClickableButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public ClickableButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		//this.setOnTouchListener(this);
		
		if(!FirstTime)
		{
			Resources r = this.getResources();
			float Radius = 10;
			float[] outerRadii = new float[] {Radius,Radius,Radius,Radius,Radius,Radius,Radius,Radius};
			Backshade = new ShapeDrawable(new RoundRectShape(outerRadii,null,null));
			Backshade.setBounds(0,0,getMeasuredWidth(),getMeasuredHeight());
			Backshade.getPaint().setColor(r.getColor(R.color.buttonclickcolor));
			Backshade.setAlpha(0);
			
		}
		
		
		FirstTime = true;
		Backshade.draw(canvas);
		
	}
	
	@Override
	public boolean onTouchEvent (MotionEvent e)
	{
		if(e.getAction() == MotionEvent.ACTION_DOWN)
		{
			Backshade.setAlpha(255);
		}
		else if(e.getAction() == MotionEvent.ACTION_UP)
		{
			Backshade.setAlpha(0);
		}
		this.invalidate();
		
		return super.onTouchEvent(e);
	}
}
