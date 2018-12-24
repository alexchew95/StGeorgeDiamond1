package cabcall.stgeorgediamond.MapContent;

import com.google.android.maps.GeoPoint;

import cabcall.stgeorgediamond.Main.*;
import cabcall.stgeorgediamond.Main.History.CabWatchWebSvc.CabWatchWebSvcListener;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

public class CentralLocationManager {
	LocationManager locmanager;
	//LocationListener _locationListener;
	static int TimeFilter = 10000;
	static int DistanceFilter = 10;
	Criteria gpsCriteria;
	LocationListener locationListener;
	
	public boolean UpdatesActive;
	
	public interface GenericActivityListener 
	{
		WindowManager getWindowManager();
	}

	public CentralLocationManager(LocationListener locListener, boolean requestUpdates) {
		UpdatesActive = true;
		locmanager = (LocationManager) MainApplication.getInstance().getSystemService(Context.LOCATION_SERVICE);
		locationListener = locListener;
		
		gpsCriteria = new Criteria();
		gpsCriteria.setAccuracy(Criteria.ACCURACY_FINE);
		gpsCriteria.setAltitudeRequired(false);
		gpsCriteria.setBearingRequired(false);
		gpsCriteria.setCostAllowed(true);
		gpsCriteria.setPowerRequirement(Criteria.POWER_LOW);
		
		String bestProv = locmanager.getBestProvider(gpsCriteria, true);
		if(bestProv != null)
		{
			Location location = locmanager.getLastKnownLocation(bestProv);
		
			if(requestUpdates)
				locmanager.requestLocationUpdates(bestProv, TimeFilter, DistanceFilter, locationListener);
			
			if(location != null)
				locationListener.onLocationChanged(location);
		}
		else
		{
			Toast toast = Toast.makeText(MainApplication.getInstance().getApplicationContext(), "Unable to locate you, please turn on GPS and restart app", Toast.LENGTH_SHORT);
            toast.show();
		}
	}
	
	public void resumeUpdates ()
	{
		UpdatesActive = true;
		String bestProv = locmanager.getBestProvider(gpsCriteria, true);
		if (bestProv != null)
		{
			locmanager.requestLocationUpdates(bestProv, TimeFilter, DistanceFilter, locationListener);
		}
		else
		{
			Toast toast = Toast.makeText(MainApplication.getInstance().getApplicationContext(), "Unable to locate you, please turn on GPS and restart app", Toast.LENGTH_SHORT);
            toast.show();
		}
	}
	
	public void suspendUpdates ()
	{
		UpdatesActive = false;
		locmanager.removeUpdates(locationListener);
	}
	
	public static int ZoomLevelForRequiredPoints (GeoPoint point1, GeoPoint point2, GenericActivityListener del)
	{
		float[] results = new float[3];
		Location.distanceBetween((double) (point1.getLatitudeE6()/1E6), (double) (point1.getLongitudeE6()/1E6), 
				(double) (point2.getLatitudeE6()/1E6), (double) (point2.getLongitudeE6()/1E6), results);
		
		float directDistance = results[0]/1000;
		float angle = results[1];
		float xDist = 0;
		float yDist = 0;
		
		Display display = del.getWindowManager().getDefaultDisplay();
		
		//Prepare the angle
		if(angle < 0)
			angle = 360 + angle; //-90 + 360 = 270
		
		if(angle > 360)//450 - 360 = 90
		{
			angle = angle - 360;
		}
		
		//Convert angle to radians
		
		
		//Determine the x length
		if(angle < 90)
		{
			angle = (float) ((angle/180)*Math.PI);
			xDist = (float) (Math.sin(angle) * directDistance);
		}
		else if (angle > 90 && angle < 180)
		{
			angle = 180-angle;
			angle = (float) ((angle/180)*Math.PI);
			xDist = (float) (Math.sin(angle) * directDistance);
		}
		else if (angle > 180 && angle < 270)
		{
			angle -= 180;
			angle = (float) ((angle/180)*Math.PI);
			xDist = (float) (Math.sin(angle) * directDistance);
		}
		else if (angle > 270 && angle < 360)
		{
			angle = 360 - angle;
			angle = (float) ((angle/180)*Math.PI);
			xDist = (float) (Math.sin(angle) * directDistance);
		}
		else if (angle == 180 || angle == 0 || angle == 360) 
		{
			//Vertical Straight line, usually a straight line 
			//would mean no x distance however because we need the distance in x we get the ratio of the direct distance
			//to the vertical screen height and multiply it by the screen width to get a pretend x value
			xDist = display.getHeight()/display.getWidth() * directDistance;
		}
		else //if(angle == 90 || angle == 270) //Horizontal straight line
		{
			xDist = directDistance;
		}
		
		//Calculate the yDist
		yDist = (float) Math.sqrt((directDistance*directDistance) - (xDist*xDist));
			
		
		//Now Calculate the zoom level
		float EquatorLengthKM = 40075.0f;
		float ScreenPx = 0;
		float SizeCoefficient = 0.4f;
		float CalculationDistance = 0;
		
		if(yDist > xDist)
		{
			CalculationDistance = yDist;
			ScreenPx = display.getHeight();
		}
		else
		{
			CalculationDistance = xDist;
			ScreenPx = display.getWidth();
		}
		
		float fval = (EquatorLengthKM*ScreenPx*SizeCoefficient)/(256*CalculationDistance);
		return (int) Math.floor( (Math.log(fval)/Math.log(2)) + 1 );
	}
}

