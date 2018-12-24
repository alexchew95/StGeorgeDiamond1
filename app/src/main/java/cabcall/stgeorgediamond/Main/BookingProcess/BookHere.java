package cabcall.stgeorgediamond.Main.BookingProcess;

import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.TextView;

import cabcall.stgeorgediamond.R;
import cabcall.stgeorgediamond.GoogleAPI.GoogleGeocode;
import cabcall.stgeorgediamond.GoogleAPI.GoogleGeocode.GeocodeMode;
import cabcall.stgeorgediamond.GoogleAPI.GoogleGeocode.GoogleGeocodeListener;
import cabcall.stgeorgediamond.Main.Defaults;
import cabcall.stgeorgediamond.Main.Defaults.eBookingModes;
import cabcall.stgeorgediamond.Main.MainApplication;
import cabcall.stgeorgediamond.Main.Components.ClickableButton;
import cabcall.stgeorgediamond.Main.Components.HistoryForm;
import cabcall.stgeorgediamond.MapContent.CurrentLocAnnot;
import cabcall.stgeorgediamond.MapContent.GenericLocationOverlay;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

public class BookHere extends MapActivity implements OnTouchListener, GoogleGeocodeListener, OnClickListener, LocationListener {
	boolean IsUsingGPSLocation;
	boolean ContinueButtonEnabled;
	CurrentLocAnnot CurrentAnnotation;
	TextView AddressDisplayText;
	MapView MyMap;
	GenericLocationOverlay CurrentLocOverlay;
	int MotionID = -1;
	int MotionOriginalx;
	int MotionOriginaly;
	long MotionStartTime;
	GeoPoint DroppedPinCoordinate;
	ClickableButton ContinueButton;
	
	@Override
	protected void onCreate (Bundle bundle)
	{
		super.onCreate(bundle);
		setContentView(R.layout.bookhere);
		//Get views from xml file
		MyMap = (MapView) findViewById(R.id.BookHere_MapView);
		ContinueButton = (ClickableButton) findViewById(R.id.BookHere_Button_Continue);
		AddressDisplayText = (TextView) findViewById(R.id.BookHere_Label_AddressText);
		AddressDisplayText.setText("Waiting For Location...");
		ContinueButton.setOnClickListener(this);
		
		CurrentLocOverlay = new GenericLocationOverlay(this, MyMap, this);
		IsUsingGPSLocation = true;
	}
	
	@Override 
	protected void onStart()
	{
		super.onStart();
		MainApplication.CurrentBookingMode = eBookingModes.eBookFromHere;
		if(MainApplication.CurrentLocation != null)
			this.AddressDisplayText.setText(MainApplication.CurrentLocation.BasicSummary(false));
		
		//Register for touch events
		MyMap.setOnTouchListener(this);
		MyMap.getOverlays().add(CurrentLocOverlay);
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		//Get the current location on the screen
		if(IsUsingGPSLocation)
			CurrentLocOverlay.enableMyLocation();
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int eventindex = event.getActionIndex();
		int id = event.getPointerId(eventindex);
		int actiontype = event.getActionMasked();
		
		if (actiontype == MotionEvent.ACTION_DOWN && MotionID == -1) //We do not already have a finger on the map
		{
			//Map has been clicked
			MotionID = id;
			MotionStartTime = event.getEventTime();
			MotionOriginalx = (int) event.getX();
			MotionOriginaly = (int) event.getY();
		}
		else if(actiontype == MotionEvent.ACTION_MOVE && MotionID == id)
		{
			//Map is being dragged down
			MotionID = -1;
		}
		else if(actiontype == MotionEvent.ACTION_UP && id == MotionID)
		{
			//Map has been released
			if(event.getEventTime() - MotionStartTime > Defaults.MaxTimeForMapClick) //if they've held it down for long enough
			{
				PlaceAnnotationOnMap((int)event.getX(),(int)event.getY());
			}
			
			MotionID = -1;
			MotionStartTime = 0;
		}

		return false;
	}
	
	public void PlaceAnnotationOnMap (int x, int y)
	{
		CurrentLocOverlay.disableMyLocation();
		MyMap.getOverlays().remove(CurrentLocOverlay);
		ContinueButtonEnabled = false;
		IsUsingGPSLocation = false;
		GeoPoint location = MyMap.getProjection().fromPixels(x,y);
		
		if(CurrentAnnotation != null)
		{
			CurrentAnnotation.annotationSpot = location;
		}
		else
		{
			CurrentAnnotation = new CurrentLocAnnot(location,"","",this);
			MyMap.getOverlays().add(CurrentAnnotation);
		}
		
		MyMap.getController().animateTo(location);
		DroppedPinCoordinate = location;
		GoogleGeocode geocodeRoutine = new GoogleGeocode(GeocodeMode.eReverse, this);
		geocodeRoutine.execute(location);
		MyMap.getController().setZoom(Defaults.DefaultZoomLevel);
	}
	
	@Override
	public void ReverseGeocodeResponse(HistoryForm hform) {
		// TODO Auto-generated method stub
		String detailString = hform.PUStreetNumber1 + " " + hform.PUStreetName1 + ", " + hform.PUSuburb1;
		AddressDisplayText.setText(detailString);
		
		MainApplication.CurrentLocation = hform;
		
		ContinueButtonEnabled = true;
		//ContinueButton.setEnabled(true);
		//this.AddressDisplayText.setVisibility(View.VISIBLE);
		
		if(!IsUsingGPSLocation)
		{
			CurrentAnnotation.TitleText = "Pickup :";
			CurrentAnnotation.DetailText = detailString;
		}
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		CurrentLocOverlay.disableMyLocation();
	}
	
	@Override 
	protected void onStop()
	{
		super.onStop();
	}
	
	@Override
	public void onClick(View v) {
		if(ContinueButtonEnabled)
		{
			MainApplication.getInstance().TempBookingForm.PUSuburb1 = MainApplication.CurrentLocation.PUSuburb1;
			MainApplication.getInstance().TempBookingForm.PUStreetName1 = MainApplication.CurrentLocation.PUStreetName1;
			MainApplication.getInstance().TempBookingForm.PUStreetNumber1 = MainApplication.CurrentLocation.PUStreetNumber1;
			
			Intent nextView = new Intent(this, BookingProcess1.class);
			startActivity(nextView);
		}
	}
	
	@Override
	public void onLocationChanged(Location location) {
		GeoPoint g = new GeoPoint((int)(location.getLatitude()*1E6), (int)(location.getLongitude()*1E6));
		GoogleGeocode geocodeRoutine = new GoogleGeocode(GeocodeMode.eReverse, this);
		geocodeRoutine.execute(g);
		MyMap.getController().animateTo(g);
		MyMap.getController().setZoom(Defaults.DefaultZoomLevel);
	}
	
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void GeocodeResponse(GeoPoint latlong) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void UnsuccessfulResponse() {
		// TODO Auto-generated method stub
		
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
