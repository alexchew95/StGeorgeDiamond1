package cabcall.stgeorgediamond.Main.Components;

import cabcall.stgeorgediamond.R;
import cabcall.stgeorgediamond.Main.MainApplication;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.AttributeSet;
import android.view.View;

public class GlowPanel extends View {

	public GlowPanel(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	public GlowPanel(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public GlowPanel(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		Resources r = MainApplication.getInstance().getResources();
		float Radius = 7;
		float[] outerRadii = new float[] {Radius,Radius,Radius,Radius,Radius,Radius,Radius,Radius};
		
		RoundRectShape s = new RoundRectShape(outerRadii,null,null);
		ShapeDrawable d = new ShapeDrawable(s);
		d.setBounds(25,25,getMeasuredWidth()-25,getMeasuredHeight()-25);
		d.getPaint().setColor(r.getColor(R.color.backshadecolor));
		
		d.draw(canvas);
	}
}
