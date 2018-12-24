package cabcall.stgeorgediamond.MapContent;

import cabcall.stgeorgediamond.Main.*;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Projection;
import cabcall.stgeorgediamond.R;

public class CurrentLocAnnot extends Annot {
boolean updateCalled;
boolean firstTime;
MapView map;
int PreviousXLocation;
int PreviousYLocation;
public boolean ShowingAnnotation;

	public CurrentLocAnnot (GeoPoint Location, String Titletext, String Detailtext, Activity HostingActivity)
	{
		super(Location, Titletext, Detailtext, HostingActivity);
		updateCalled = false;
		firstTime = true;
		ShowingAnnotation = true;
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow)
	{
		map = mapView;
		if(ShowingAnnotation)
		{
			Resources r = MainApplication.getInstance().getResources();
			Drawable myImage = r.getDrawable(R.drawable.icon_current_loc);
			
			int xsize = myImage.getMinimumWidth();
			int ysize = myImage.getMinimumHeight();
			Point pixelLoc = new Point();
			
			Projection p = mapView.getProjection();
			p.toPixels(annotationSpot, pixelLoc);
			
			int xstart = pixelLoc.x - xsize / 2; // Centre the image on the x axis
			int ystart = pixelLoc.y - ysize; //put the image off the screen, ready for animation
			this.PixelsofAnnotIconAboveSpot = ysize;
			super.draw(canvas, mapView, shadow);
			
			Rect drawingRect = new Rect(xstart,ystart,xsize + xstart ,ysize + ystart);
			
			myImage.setBounds(drawingRect);
			myImage.draw(canvas);
		}
	}
	
	@Override
	public boolean onTap (GeoPoint point, MapView mapView)
	{
		boolean result = super.onTap(point, mapView);
		if(result)
		{
			//Handle it
		}
		
		return false;
	}
	
	public void updateLocation (GeoPoint newLocation)
	{
		if(map != null) //This would be strange behavour if the map was equal to null, it would mean that the updatelocation method would be called before onDraw
		{
			//change the annotationSpot value so that is now set to the new location
			this.annotationSpot = newLocation;
			map.invalidate();
		}
	}
}
