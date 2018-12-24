package cabcall.stgeorgediamond.Main.History;

import java.util.List;
import java.util.Timer;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import cabcall.stgeorgediamond.R;
import cabcall.stgeorgediamond.Main.Defaults;
import cabcall.stgeorgediamond.Main.Components.ClickableButton;
import cabcall.stgeorgediamond.Main.Components.GenericTimerTask;
import cabcall.stgeorgediamond.Main.Components.GenericTimerTask.TimerListener;
import cabcall.stgeorgediamond.Main.Components.HistoryForm;
import cabcall.stgeorgediamond.Main.Defaults.iCabStatusIconNumbers;
import cabcall.stgeorgediamond.Main.History.CabWatchWebSvc.CabWatchWebSvcListener;
import cabcall.stgeorgediamond.MapContent.CabWatchAnnot;
import cabcall.stgeorgediamond.MapContent.CurrentLocAnnot;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class CabWatch extends MapActivity implements CabWatchWebSvcListener, OnClickListener, TimerListener {

	public static HistoryForm HistoryData;
	boolean FirstTimeLoadingStatus;
	boolean RepositionMap;
	boolean TimerScheduled;
	
	Resources res;
	TextView TripLabel;
	TextView JobStatusLabel;
	ClickableButton RefreshButton;
	ProgressBar StatusUpdater;
	ImageView JobStatusImage;
	Timer RefreshButtonTimer;
	MapView map;
	
	CabWatchAnnot taxiAnnot;
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.cabwatch);
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		res = getResources();
		
		FirstTimeLoadingStatus = true;
		    
	    RefreshButtonTimer = new Timer();
		
	    StatusUpdater = (ProgressBar) findViewById(R.id.CabWatch_StatusUpdater);
	    RefreshButton = (ClickableButton) findViewById(R.id.CabWatch_Button_RefreshButton);
	    JobStatusLabel = (TextView) findViewById(R.id.CabWatch_Label_JobStatus);
	    JobStatusImage = (ImageView) findViewById(R.id.CabWatch_Image_JobStatus);
	    TripLabel = (TextView) findViewById(R.id.CabWatch_Label_TripLabel);
	    
	    RefreshButton.setEnabled(false);
	    RefreshButton.setImageResource(R.drawable.small_button_blank);
	    
	    map = (MapView) findViewById(R.id.CabWatch_MapView);
	    
	    RefreshButton.setOnClickListener(this);
	    
	    StatusUpdater.setVisibility(View.VISIBLE);
	    
	    SwitchController.CabWatchActivity = this;
	    
	    SetupDisplay();
	}
	
	void RunTimer ()
	{
		TimerScheduled = true;
		RefreshButtonTimer = new Timer();
		RefreshButtonTimer.schedule(new GenericTimerTask(this), Defaults.kRefreshWait, Defaults.kRefreshWait);
	}
	
	void StopTimer ()
	{
		if(TimerScheduled && RefreshButtonTimer != null)
			RefreshButtonTimer.cancel();
		TimerScheduled = false;
	}
	
	@Override
	public void GeocodeResponse(GeoPoint latlong) {
		if(HistoryData.NoPickupCoords == false)
		{
			String PUAddress;
			
			if(HistoryData.PlaceFieldUsed)
			{
				PUAddress = HistoryData.PUPlace1 + ", " + HistoryData.PUSuburb1;
			}
			else
			{
				PUAddress = HistoryData.PUStreetNumber1 + " " + HistoryData.PUStreetName1 + ", " + HistoryData.PUSuburb1;
			}
			CurrentLocAnnot PickupLocation = new CurrentLocAnnot(latlong, res.getString(R.string.PickupLocationAnnotTitle), PUAddress, this);
			
			
	        List<Overlay> overlays = map.getOverlays();
	        overlays.add(PickupLocation);
		}
	}
	
	public void SetupDisplay ()
	{
		String tripStr = String.format(res.getString(R.string.TripString), HistoryData.PUSuburb1, HistoryData.DestSuburb1);
		this.TripLabel.setText(tripStr);
		
		GeoPoint CityCentre = new GeoPoint((int)(Defaults.CityCentreLat * 1E6), (int) (Defaults.CityCentreLon*1E6));
		map.getController().setCenter(CityCentre);
		map.getController().setZoom(Defaults.CityCentreZoomLevel);
	}
	
	@Override
	public void CabWatchUpdateTaxiCoordinates(HistoryForm taxicoordinates, int zoomLevel, GeoPoint centrepoint) {
		PretendToRefresh();
		//Building the TaxiLocationAnnotation
		if(taxicoordinates.NoTaxiCoords)
		{
			//Do Nothing
		}
		else
		{
			String title = "Taxi" + taxicoordinates.TaxiNumber;
			String details = HistoryData.TaxiStreetNumber + " " + HistoryData.TaxiStreetName + ", " + HistoryData.TaxiSuburb;
			if(taxiAnnot == null)
				taxiAnnot = new CabWatchAnnot(taxicoordinates.nTaxiCoord, title, details, this);
			else
			{
				taxiAnnot.TitleText = title;
				taxiAnnot.DetailText = details;
				taxiAnnot.annotationSpot = taxicoordinates.nTaxiCoord;
			}
			map.getOverlays().add(taxiAnnot);
			//Cause annotation to come up
			taxiAnnot.onTap(taxicoordinates.nTaxiCoord, map);
		}
		//Zoom on region
		map.getController().animateTo(centrepoint);
		map.getController().setZoom(zoomLevel);
		RepositionMap = true;
		
		//Test the StatusIdentifier variable of the historyform and determine what icon to display
		//Test the StatusIdentifier and decide what icon to display
		if(taxicoordinates.PUStatusString == null || taxicoordinates.PUStatusString.length() == 0)
		{
			//Do nothing and ignore
		}
		else
		{
			this.JobStatusLabel.setText(taxicoordinates.PUStatusString);
		}
		
		int pointerToImage = 0;
		switch ((iCabStatusIconNumbers) Defaults.iCabStatusSequences[taxicoordinates.StatusIdentifier][Defaults.iCabStatusIconNumber])
		{
		case iIconLookingForCab:
			pointerToImage = R.drawable.status_icon_looking_for_a_cab;
			break;
		case iIconOnWay:
			pointerToImage = R.drawable.status_icon_taxi_on_it_s_way;
			break;
		case iIconPickedUp:
			pointerToImage = R.drawable.status_icon_successful_pickup;
			break;
		case iIconCompleted:
			pointerToImage = R.drawable.status_icon_successful_job;
			break;
		case iIconCancelled:
			pointerToImage = R.drawable.status_icon_cancelled_booking;
			break;
		default:
			pointerToImage = R.drawable.status_icon_booking_problem;
			break;
		}
		JobStatusImage.setImageResource(pointerToImage);
		
	}
	
	@Override
	public void onClick(View v) 
	{
		ClickableButton pressedView = (ClickableButton) v;

		if(pressedView == RefreshButton)
		{
			// Phase 2 - App pretends to be getting next status update;
			StatusUpdater.setVisibility(View.VISIBLE);
			pressedView.setEnabled(false);
			pressedView.setImageResource(R.drawable.small_button_blank);
			RunTimer();
		}
	}
	
	@Override
	public void TimerTick() {
		PretendToRefresh();
	}
	
	public void PretendToRefresh()
	{
		//Make the view look as though a refresh has just occurred
		StopTimer();
		StatusUpdater.setVisibility(View.INVISIBLE);
		RefreshButton.setEnabled(true);
		RefreshButton.setImageResource(R.drawable.small_button_refresh);
	}
	
	@Override
	public void onStop()
	{
		super.onStop();
		SwitchController.CabWatchActivity = null;
	}
	
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void ReverseGeocodeResponse(HistoryForm hform) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void UnsuccessfulResponse() {
		// TODO Auto-generated method stub
		
	}

}
