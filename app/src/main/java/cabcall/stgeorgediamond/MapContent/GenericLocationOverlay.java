package cabcall.stgeorgediamond.MapContent;

import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import cabcall.stgeorgediamond.Main.Defaults;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

public class GenericLocationOverlay extends CurrentLocAnnot implements LocationListener {
	LocationListener delegate;
	MapView mapRef;
	CentralLocationManager locManager;
	boolean ActivityHandlesRegion;
	
	public GenericLocationOverlay(Activity HostingActivity, MapView mapView, LocationListener del) {
		super(null, "", "", HostingActivity);
		ShowingAnnotation = false;
		delegate = del;
		mapRef = mapView;
	}
	
	public void setActivityHandlesRegion(boolean flag)
	{
		ActivityHandlesRegion = flag;
	}
	
	
	public boolean enableMyLocation()
	{
		if(locManager == null)
			locManager = new CentralLocationManager(this, true);
		
		locManager.resumeUpdates();
		return false;
	}
	
	
	public void disableMyLocation()
	{
		locManager.suspendUpdates();
	}
	
	@Override
	public void onLocationChanged (Location location)
	{
		if(location != null)
		{
			ShowingAnnotation = true;
			annotationSpot = new GeoPoint((int)(location.getLatitude()*1E6),(int)(location.getLongitude()*1E6));
			delegate.onLocationChanged(location);
			if(!ActivityHandlesRegion)
			{
				mapRef.getController().setZoom(Defaults.DefaultZoomLevel);
				mapRef.getController().animateTo(annotationSpot);
			}
		}
	}


	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	
	
}
