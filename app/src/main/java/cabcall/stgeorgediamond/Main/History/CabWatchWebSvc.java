package cabcall.stgeorgediamond.Main.History;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.google.android.maps.GeoPoint;

import cabcall.stgeorgediamond.R;
import cabcall.stgeorgediamond.GoogleAPI.GoogleGeocode;
import cabcall.stgeorgediamond.GoogleAPI.GoogleGeocode.GeocodeMode;
import cabcall.stgeorgediamond.GoogleAPI.GoogleGeocode.GoogleGeocodeListener;
import cabcall.stgeorgediamond.Main.Defaults;
import cabcall.stgeorgediamond.Main.Defaults.iCabSequenceNumbers;
import cabcall.stgeorgediamond.Main.MainApplication;
import cabcall.stgeorgediamond.Main.Components.HistoryForm;
import cabcall.stgeorgediamond.MapContent.CentralLocationManager;
import cabcall.stgeorgediamond.MapContent.CentralLocationManager.GenericActivityListener;
import cabcall.stgeorgediamond.WebService.CabcallConnection;
import cabcall.stgeorgediamond.WebService.CabcallConnectionListener;

public class CabWatchWebSvc implements CabcallConnectionListener, GoogleGeocodeListener, LocationListener {
	public interface CabWatchWebSvcListener extends GoogleGeocodeListener, GenericActivityListener
	{
		void CabWatchUpdateTaxiCoordinates (HistoryForm taxicoordinates,  int zoomLevel, GeoPoint centrepoint);
		void runOnUiThread (Runnable action);
	}
	public int CompletedCycleCount;
	CabWatchWebSvcListener Delegate;
	HistoryForm historyForm;
	boolean Logging;
	boolean isCancelledc;
	GoogleGeocode reverseGeoReq;
	Resources res;
	CentralLocationManager locationmanager;
	CabcallConnection con;
	
	public CabWatchWebSvc(CabWatchWebSvcListener listener, HistoryForm HForm, boolean LogsStatus)
	{
		Delegate = listener;
		historyForm = HForm;
		Logging = LogsStatus;
		res = MainApplication.getInstance().getResources();
	}
	
	public void start ()
	{
		WebSvcGetBookingStatus();
	}
	
	void WebSvcGetBookingStatus ()
	{
		if(Logging)
			Log.d("CabWatchWebSvc", "WebSvcBookingStatus");
		
		if(this.isCancelledc)
		{
			Log.d("CabWatchWebSvc", "Noticed Cancelling");
			CompletedCycleCount++;
		}
		else
		{
			CabcallConnection GetBookingStatusMsg = new CabcallConnection("GetBookingStatus", this);
			con = GetBookingStatusMsg;
			
			DateTime currentTime = new DateTime();
			DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
			String sCurrentTime = currentTime.toString(fmt);
			GetBookingStatusMsg.setobject("dtLastCommsTime",sCurrentTime);
			GetBookingStatusMsg.setobject("nLatitude","0");
			GetBookingStatusMsg.setobject("nLongitude","0");
			GetBookingStatusMsg.setobject("nJobStatusSequence","0");
			GetBookingStatusMsg.setobject("nJobNumber", "" + historyForm.JobNumber);
			
			GetBookingStatusMsg.execute();
		}
	}

	@Override
	public void CabcallConnectionComplete(CabcallConnection connection) {
		// TODO Auto-generated method stub
		if(Logging)
			Log.d("CabWatchWebSvc", "CabcallConnectionComplete");
		WebSvcGetBookingStatusRecieved(connection);
	}
	
	void WebSvcGetBookingStatusRecieved (CabcallConnection connection)
	{
		if(Logging)
			Log.d("CabWatchWebSvc", "WebSvcGetBookingStatusRecieved");
		
		if(isCancelledc)
		{
			Log.d("CabWatchWebSvc", "Noticed Cancelling");
			CompletedCycleCount++;
		}
		else
		{
			// Check the date and time of the booking
	        // If date and time are NULL this is a ASAP booking
	        // Else if the time and date are within 15 mins of now - treat booking as an ASAP booking
	        // Else booking is a future booking
			String strLat = connection.getresponseString("nLatitude");
			String strLon = connection.getresponseString("nLongitude");
			double lat = 0;
			double lon = 0;
			
			if(strLat == null || strLon == null || strLat.equals("") || strLon.equals(""))
			{
				//Do nothing
			}
			else
			{
				lat = MainApplication.SafeDouble(strLat);
				lon = MainApplication.SafeDouble(strLon);
			}
			
			String strCarNum = connection.getresponseString("sCarNumber");
			int taxinumber = 0;
			
			if(strCarNum != null)
				taxinumber = MainApplication.SafeInt(strCarNum);
			
			
			this.historyForm.nTaxiCoord = new GeoPoint((int)(lat * 1E6), (int)(lon * 1E6));
			this.historyForm.TaxiNumber = taxinumber;
			
			DetermineFutureBooking(connection);
			DetermineStatus(connection);
			
			if(lat == 0 || lon == 0)
			{
				historyForm.NoTaxiCoords = true;
				DesignViewingRegion();
			}
			else
			{
				historyForm.NoTaxiCoords = false;
				//Get the physical address of the taxi and continue the process of calculating the region
				GoogleGeocode googleReq = new GoogleGeocode(GeocodeMode.eReverse, this);
				reverseGeoReq = googleReq;
				googleReq.execute(this.historyForm.nTaxiCoord);
			}
		}
	}
	
	void DetermineFutureBooking (CabcallConnection connection)
	{
		if(Logging)
			Log.d("CabWatchWebSvc", "DetermineFutureBooking");
		String dateStr = historyForm.PUTime + " " + historyForm.PUDate;
		
		DateTimeFormatter formatter = DateTimeFormat.forPattern("HH-mm dd-MM-yy");
		DateTime bookingtime = formatter.parseDateTime(dateStr);
		
		DateTime nowplus15minutes = new DateTime().plusMinutes(15);
		
		if(bookingtime.isBefore(nowplus15minutes))
		{
			this.historyForm.FutureBooking = false;
		}
		else
		{
			this.historyForm.FutureBooking = true;
		}
	}
	
	void DetermineStatus (CabcallConnection connection)
	{
		if(Logging)
			Log.d("CabWatchWebSvc", "DetermineStatus");
		
		// State 0: response.sJobStatus equals "Job is being dispatched" and a future booking
	    //We determine whether it is a future booking or not (at this stage), so we ignore the future booking statusidentifier
	   
		if(historyForm.FutureBooking)
		{
			String Status = String.format(res.getString(R.string.kStatusFutureBooking), historyForm.PUDate, historyForm.PUTime);
			historyForm.PUStatusString = Status;
			historyForm.StatusIdentifier = 0;
		}
		else
		{
			int iStatusVal = MainApplication.SafeInt(connection.getresponseString("nJobStatusSequence"));
			historyForm.StatusIdentifier = iStatusVal;
			
			switch((iCabSequenceNumbers) Defaults.iCabStatusSequences[iStatusVal][Defaults.iCabSequenceNumber])
			{
			case iDispatching:
				historyForm.PUStatusString = res.getString(R.string.kStatusLookingForCab);
				break;
			case iAccepted:
				String sCarDistance;
				float[] Distances = new float[3];
				Location.distanceBetween((double) historyForm.nPUCoord.getLatitudeE6()/1E6, (double) historyForm.nPUCoord.getLongitudeE6()/1E6, 
						(double) historyForm.nTaxiCoord.getLatitudeE6()/1E6, (double) historyForm.nTaxiCoord.getLongitudeE6()/1E6, Distances);
				
				sCarDistance = String.format("%2.2f", Distances[0]/1000);
				
				String sStatus1 = String.format(res.getString(R.string.kStatusOnWay), 
						connection.getresponseString("sCarNumber"), sCarDistance);
				historyForm.PUStatusString = sStatus1;
				break;
			case iPickedUp:
				historyForm.PUStatusString = res.getString(R.string.kStatusPickedUp);
				break;
			case iCompleted:
				historyForm.PUStatusString = res.getString(R.string.kStatusComplete);
				break;
			case iCancelled:
				historyForm.PUStatusString = res.getString(R.string.kStatusCancelled);
				break;
			case iNoJob:
				historyForm.PUStatusString = res.getString(R.string.kStatusNoJob);
				break;
			case iRecall:
				String sStatus2 = String.format(res.getString(R.string.kStatusRecall), 
						res.getString(R.string.TaxiProviderName), res.getString(R.string.TaxiProviderPhoneNum));
				historyForm.PUStatusString = sStatus2;
				break;
			case iProblem:
				String sStatus3 = String.format(res.getString(R.string.kStatusProblem), 
						res.getString(R.string.TaxiProviderName), res.getString(R.string.TaxiProviderPhoneNum));
				historyForm.PUStatusString = sStatus3;
				break;
			default:
			//ignore
			break;
			}
		}
	}
	
	@Override
	public void ReverseGeocodeResponse(HistoryForm hform) 
	{
		if(Logging)
			Log.d("CabWatchWebSvc", "ReverseGeocodeResponse");
		
		if(this.isCancelledc)
		{
			Log.d("CabWatchWebSvc", "Noticed Cancelling");
			CompletedCycleCount++;
		}
		else
		{
			historyForm.TaxiStreetName = hform.PUStreetName1;
			historyForm.TaxiStreetNumber = hform.PUStreetNumber1;
			historyForm.TaxiSuburb = hform.PUSuburb1;
			
			DesignViewingRegion();
		}
	}
	
	void DesignViewingRegion ()
	{
		if(Logging)
			Log.d("CabWatchWebSvc", "DesignViewingRegion");
		
		if(historyForm.NoPickupCoords) //Because there are no pickup coordinates we need to get the user's location as a reference to where we can zoom in on the map
		{
			CentralLocationManager locmanager = new CentralLocationManager(this, false);
			this.locationmanager = locmanager;
		}
		else
		{
			CalculateViewingRegion();
		}
	}
	
	@Override
	public void onLocationChanged(Location location) {
		if(Logging)
			Log.d("CabWatchWebSvc", "onLocationChanged");
		historyForm.nPUCoord = new GeoPoint((int)location.getLatitude(),(int)location.getLongitude());
		CalculateViewingRegion();
	}

	@Override
	public void onProviderDisabled(String provider) {
		if(Logging)
			Log.d("CabWatchWebSvc", "onProviderDisabled");
		int Lat = MainApplication.SafeInt(res.getString(R.string.CityCentreLat));
		int Lon = MainApplication.SafeInt(res.getString(R.string.CityCentreLat));
		historyForm.nPUCoord = new GeoPoint(Lat,Lon);
		
	}
	
	void CalculateViewingRegion ()
	{
		int ZoomLevel;
		GeoPoint CentrePoint;
		
		if(historyForm.NoTaxiCoords)
		{
			ZoomLevel = Defaults.DefaultZoomLevel;
			CentrePoint = historyForm.nPUCoord;
		}
		else
		{
			//Create a midpoint which is between the taxi and the user's pickup location so that the android can centre the map and view the details
			int Latitude = (historyForm.nPUCoord.getLatitudeE6() + historyForm.nTaxiCoord.getLatitudeE6())/2;
			int Longitude = (historyForm.nPUCoord.getLongitudeE6() + historyForm.nTaxiCoord.getLongitudeE6())/2;
			CentrePoint = new GeoPoint(Latitude, Longitude);
			
			ZoomLevel = CentralLocationManager.ZoomLevelForRequiredPoints(historyForm.nPUCoord, historyForm.nTaxiCoord, Delegate);
			
			if(Logging)
				Log.d("CabWatchWebSvc", "NotifyDelegate");
			CompletedCycleCount++;
		}
			
		if(isCancelledc)
		{
			Log.d("CabWatchWebSvc", "Noticed Cancelling");
		}
		else
		{
			Delegate.CabWatchUpdateTaxiCoordinates (historyForm,  ZoomLevel, CentrePoint);
		}
	}
	
	public void cancelc ()
	{
		if(Logging)
			Log.d("CabWatchWebSvc", "cancel");
		this.isCancelledc = true;
		
		if(con != null && !con.isComplete)
			con.cancel(true);
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
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
}
