package cabcall.stgeorgediamond.Main.History;


import java.util.Timer;
import java.util.TimerTask;

import com.google.android.maps.GeoPoint;

import cabcall.stgeorgediamond.GoogleAPI.GoogleGeocode;
import cabcall.stgeorgediamond.GoogleAPI.GoogleGeocode.GeocodeMode;
import cabcall.stgeorgediamond.Main.Defaults;
import cabcall.stgeorgediamond.Main.Defaults.eSwitchContTabIndex;

import cabcall.stgeorgediamond.R;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;
import android.widget.Toast;
import cabcall.stgeorgediamond.Main.Components.HistoryForm;
import cabcall.stgeorgediamond.Main.History.CabWatch;
import cabcall.stgeorgediamond.Main.History.CabWatchWebSvc.CabWatchWebSvcListener;
import cabcall.stgeorgediamond.Main.History.JobDetails;


	public class SwitchController extends TabActivity implements CabWatchWebSvcListener, TabHost.OnTabChangeListener {
		
		public class SwitchControllerTimerTask extends TimerTask 
		{
			//SwitchControllerTimerTask properties
			public int CabWatchTimerCycleCount;
			public CabWatchWebSvc cabWatchWebSvc;
			CabWatchWebSvcListener delegate;
			
			
			public SwitchControllerTimerTask (CabWatchWebSvcListener Delegate)
			{
				delegate = Delegate;
				cabWatchWebSvc = new CabWatchWebSvc(delegate, SwitchController.HistoryData, true);
			}
			
			@Override
			public void run() {
				//Timer Tick
				if(cabWatchWebSvc.CompletedCycleCount < CabWatchTimerCycleCount)
				{
					Log.d("app.Main.History.SwitchControllerTimerTask", "The CabWatchWebSvc is lagging");
				}
				CabWatchWebSvc newRequest = new CabWatchWebSvc(delegate, SwitchController.HistoryData, true);
				newRequest.CompletedCycleCount = cabWatchWebSvc.CompletedCycleCount;
				cabWatchWebSvc = newRequest;
				
				cabWatchWebSvc.start();
			}

		}
		
		//SwitchController properties
		Timer CabWatchTimer;
		Resources r;
		SwitchControllerTimerTask timerTask;
		static CabWatchWebSvcListener CabWatchActivity;
		static CabWatchWebSvcListener JobDetailsActivity;
		boolean CabWatchCalled;
		static HistoryForm HistoryData;
		boolean TimerWasCancelled;
		HistoryForm TaxiCoordinates;
		int ZoomLevel;
		GeoPoint CentrePoint;
		
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.switchcontroller);
	    
	    //Setup Utilities
	    JobDetails.HistoryData = HistoryData;
	    CabWatch.HistoryData = HistoryData;
	    
	    Resources res = getResources(); // Resource object to get Drawables
	    TabHost tabHost = getTabHost();  // The activity TabHost
	    TabHost.TabSpec spec;  // Resusable TabSpec for each tab

	    // Initialize a TabSpec for each tab and add it to the TabHost
	    spec = tabHost.newTabSpec(res.getString(R.string.TabCabWatch)).setIndicator(res.getString(R.string.LabelCabWatch),
	                      res.getDrawable(R.drawable.ic_tab_cabwatch))
	                  .setContent(new Intent().setClass(this, CabWatch.class));
	    tabHost.addTab(spec);

	 // Initialize a TabSpec for each tab and add it to the TabHost
	    spec = tabHost.newTabSpec(res.getString(R.string.TabJobDetails)).setIndicator(res.getString(R.string.LabelJobDetails),
	                      res.getDrawable(R.drawable.ic_tab_jobdetails))
	                  .setContent(new Intent().setClass(this, JobDetails.class));
	    tabHost.addTab(spec);

	    tabHost.setCurrentTab(0);
	    
	    tabHost.setOnTabChangedListener(this);
	    
	    r = getResources();
	    
	    this.setTitle(R.string.LabelCabWatch);
	    
	    //Set to true to cause the timer to be initialized the first time it is called
	    TimerWasCancelled = true;
	}
	
	@Override
	protected void onStart ()
	{
		super.onStart();
		//Get the lat long of the pickup location
		GoogleGeocode geocodeRequest = new GoogleGeocode(GeocodeMode.eNormal, this);
		geocodeRequest.execute(HistoryData);
		
		//CabWatchTimer = new Timer(r.getString(R.string.CabWatchTimerName));
	    timerTask = new SwitchControllerTimerTask(this);
	}
	
	void RunTimer ()
	{
		if(TimerWasCancelled)
			CabWatchTimer = new Timer(r.getString(R.string.CabWatchTimerName));
		CabWatchTimer.schedule(timerTask,0,Defaults.kCabWatchWebSvcTimeInterval);
		
		TimerWasCancelled = false;
	}
	
	void StopTimer ()
	{
		CabWatchTimer.cancel();
		timerTask.cabWatchWebSvc.cancelc();
		TimerWasCancelled = true;
	}
	
	public void CabWatchUpdateTaxiCoordinates (HistoryForm taxicoordinates,  int zoomLevel, GeoPoint centrepoint)
	{
		CabWatchCalled = true;
		if(getTabHost().getCurrentTab() == eSwitchContTabIndex.eCabWatch.ordinal())
		{
			CabWatchActivity.CabWatchUpdateTaxiCoordinates(taxicoordinates, zoomLevel, centrepoint);
		}
		else
		{
			JobDetailsActivity.CabWatchUpdateTaxiCoordinates(taxicoordinates, zoomLevel, centrepoint);
		}
		
		//Set internal variables
		TaxiCoordinates = taxicoordinates;
		ZoomLevel = zoomLevel;
		CentrePoint = centrepoint;
	}

	@Override
	public void GeocodeResponse(GeoPoint latlong) {
		// TODO Auto-generated method stub
		if(getTabHost().getCurrentTab() == eSwitchContTabIndex.eCabWatch.ordinal())
		{
			CabWatchActivity.GeocodeResponse(latlong);
		}
		
		HistoryData.nPUCoord = latlong;
		RunTimer();
	}

	@Override
	public void onTabChanged(String tabId) {
		// TODO Auto-generated method stub
		if(CabWatchCalled)
		{
			if(getTabHost().getCurrentTab() == eSwitchContTabIndex.eCabWatch.ordinal())
			{
				this.setTitle(R.string.LabelCabWatch);
				CabWatchActivity.CabWatchUpdateTaxiCoordinates(this.TaxiCoordinates, ZoomLevel, CentrePoint);
			}
			else
			{
				this.setTitle(R.string.LabelJobDetails);
				JobDetailsActivity.CabWatchUpdateTaxiCoordinates(this.TaxiCoordinates, ZoomLevel, CentrePoint);
			}
		}
		
		CabWatchCalled = false;
	}

	@Override
	public void UnsuccessfulResponse() {
		// TODO Auto-generated method stub
		String errorstring = r.getString(R.string.app_name) + r.getString(R.string.GeocodeErrorString);
		Toast t = Toast.makeText(this, errorstring, Toast.LENGTH_LONG);
		t.show();
		
		RunTimer();
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		StopTimer();
	}
	
	@Override
	public void ReverseGeocodeResponse(HistoryForm hform) {
		//NOT USED
	}
}
