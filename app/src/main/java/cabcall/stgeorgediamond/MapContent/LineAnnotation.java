package cabcall.stgeorgediamond.MapContent;

import java.util.ArrayList;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

import cabcall.stgeorgediamond.R;
import cabcall.stgeorgediamond.Main.MainApplication;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class LineAnnotation extends Overlay {

	public ArrayList<GeoPoint> PolyLine;
	public LineAnnotation(ArrayList<GeoPoint> poly)
	{
		PolyLine = poly;
	}
	
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow)
	{
		super.draw(canvas,mapView, shadow);
		
		if(!shadow)
		{
			Resources r = MainApplication.getInstance().getResources();
			Projection mapProj = mapView.getProjection();
			
			GeoPoint EndLine = PolyLine.get(0);
			
			Point pStartLine = new Point();
			Point pEndLine = new Point();
			mapProj.toPixels(EndLine, pEndLine);
			
			Paint lineStyle = new Paint();
			//lineStyle.setColor(r.getColor(R.color.maplinecolor));
			lineStyle.setColor(Color.BLUE);
			lineStyle.setStyle(Paint.Style.FILL_AND_STROKE);
			lineStyle.setStrokeJoin(Paint.Join.ROUND);
			lineStyle.setStrokeCap(Paint.Cap.ROUND);
			lineStyle.setStrokeWidth(5);
			lineStyle.setDither(true);
			
			for(int i = 0; i < PolyLine.size() - 1; i++)
			{
				pStartLine.x = pEndLine.x;
				pStartLine.y = pEndLine.y;
				EndLine = PolyLine.get(i+1);
				
				//Draw the points onto the map
				mapProj.toPixels(EndLine, pEndLine);
				canvas.drawLine((float)pStartLine.x, (float)pStartLine.y, (float)pEndLine.x, (float)pEndLine.y, lineStyle);
			}
		}
	}
}
