package cabcall.stgeorgediamond.Main.History;

import java.util.ArrayList;
import java.util.Map;

import org.joda.time.DateTime;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import cabcall.stgeorgediamond.R;
import cabcall.stgeorgediamond.Main.Defaults.eBookingModes;
import cabcall.stgeorgediamond.Main.Defaults.eHistoryModes;
import cabcall.stgeorgediamond.Main.HomeScreenActivity;
import cabcall.stgeorgediamond.Main.MainApplication;
import cabcall.stgeorgediamond.Main.Components.BookingForm;
import cabcall.stgeorgediamond.Main.Components.HistoryForm;
import cabcall.stgeorgediamond.Main.Components.SegmentedRadioGroup;
import cabcall.stgeorgediamond.Main.Components.historylistadapter;
import cabcall.stgeorgediamond.WebService.CabcallConnection;
import cabcall.stgeorgediamond.WebService.CabcallConnectionListener;

public class HistoryActivity extends Activity implements OnItemClickListener, CabcallConnectionListener, OnCheckedChangeListener
{
	ArrayList<HistoryForm> CurrentBookings;
	ArrayList<HistoryForm> HistoryBookings;
	ArrayList<HistoryForm> BookingsToLoad;
	
	eHistoryModes HistoryMode;
	
	boolean UpdatedCurrentBookingList;
	boolean UpdatedPastBookingList;
	
	ListView HistoryView;
	ArrayList<Map<String,Object>> data;
	SegmentedRadioGroup srg;
	ProgressBar ActivityIndicator;
	CabcallConnection currentconnection;
	
	@Override
    public void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);
        HistoryMode = eHistoryModes.eHistoryCurrent;
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		
		// event handlers
		
        HistoryView = (ListView) findViewById(R.id.History_ListView);
 
        // set up a listerner for when a row is clicked.
        HistoryView.setOnItemClickListener(this);   
        
		MainApplication.CurrentBookingMode = eBookingModes.eNoBookingMode;
		srg = (SegmentedRadioGroup) findViewById(R.id.History_SegmentedControl);
        srg.setOnCheckedChangeListener(this);
        
        ActivityIndicator = (ProgressBar) findViewById(R.id.History_ActivityIndicator);
        // TODO get the view to load any current bookings - not sure how to do this - possibly the line of code below
        if(HistoryMode == eHistoryModes.eHistoryCurrent)
        	WebSvcGetCurrentBookings();
        else
        	WebSvcGetPastBookings();
      
       //CurrentBookings [0] = null;	// load a blank into the CurrentBookings array - will trigger a web service to refresh the current bookings list
	}
		  
	public void WebSvcGetCurrentBookings()
	{
		HistoryMode = eHistoryModes.eHistoryCurrent;
		  
		if(CurrentBookings == null)
		{
			ActivityIndicator.setVisibility(View.VISIBLE);
			CabcallConnection getCurrentBookingMsg = new CabcallConnection("GetSPCurrentBookingList", this);
			getCurrentBookingMsg.setobject("ShowFutureBooking", "true");
			getCurrentBookingMsg.setobject("CustomerNumber", BookingForm.userMobileNumber());
			getCurrentBookingMsg.setobject("NumberPreviousBookings", "20");
			getCurrentBookingMsg.execute();
			
			currentconnection = getCurrentBookingMsg;
		}
		else
		{
			BookingsToLoad = CurrentBookings;
		}
		
		UpdatedCurrentBookingList = true;
	}
	
	public void WebSvcGetPastBookings()
	{
		HistoryMode = eHistoryModes.eHistoryPast;
		
		if(HistoryBookings == null)
		{
			ActivityIndicator.setVisibility(View.VISIBLE);
			// start animating progress bar
			CabcallConnection getHistoricBookingMsg = new CabcallConnection("GetSPHistoricBookingList", this);
			getHistoricBookingMsg.setobject("CustomerNumber", BookingForm.userMobileNumber());
			getHistoricBookingMsg.setobject("NumberPreviousBookings", "20");
			
			// startDate is one month ago; endDate is now
			
			DateTime currentDate = new DateTime();
			DateTime oneMonthAgo = currentDate.minusMonths(1);
			
			getHistoricBookingMsg.setobject("EndDate", currentDate.toString("yyyy-MM-dd"));
			getHistoricBookingMsg.setobject("StartDate", oneMonthAgo.toString("yyyy-MM-dd"));

			getHistoricBookingMsg.execute();
			
			currentconnection = getHistoricBookingMsg;
		 }
		 else
		 {
			BookingsToLoad = HistoryBookings;
		 }
	}
	
	@Override
	public void CabcallConnectionComplete(CabcallConnection connection)
	{
		ActivityIndicator.setVisibility(View.INVISIBLE);
		// HistoryForm bookingForm = MainApplication.getInstance().TempBookingForm;
		if(connection.SoapName.equals("GetSPHistoricBookingList"))
		{
			// TODO get the booking list response and put it in bookingListResponse
			this.WebSvcGetHistoricBookingsReceived(connection);
		}
		else	// must be a GetCurrentBookingList response
		{
			// TODO get the booking list response and put it in bookingListResponse
			this.WebSvcGetCurrentBookingsReceived(connection);		
		}
	}
	
	public void WebSvcGetCurrentBookingsReceived(CabcallConnection connection)
	{
		CurrentBookings = new ArrayList<HistoryForm>();
		BookingsToLoad = CurrentBookings;
		
		HistoryMode = eHistoryModes.eHistoryCurrent;
		
		historylistadapter adapter = new historylistadapter(this, R.layout.historylistitem, BookingsToLoad);
        HistoryView.setAdapter(adapter);
        
		data = connection.getResponseList();
		if(data != null)
		{
			for(Map<String,Object> dataSector : data)
			{
				
				HistoryForm hf = new HistoryForm(dataSector);
				if(hf.BookingNotCancelled)	// only add a booking to the list if it has not been cancelled
				{
					CurrentBookings.add(hf);
				}
			}
		}		
	}
		

	public void WebSvcGetHistoricBookingsReceived(CabcallConnection connection)
	{
		HistoryBookings = new ArrayList<HistoryForm>();
		BookingsToLoad = HistoryBookings;
		
		HistoryMode = eHistoryModes.eHistoryPast;
		
		historylistadapter adapter = new historylistadapter(this, R.layout.historylistitem, BookingsToLoad);
        HistoryView.setAdapter(adapter);
        
		data = connection.getResponseList();
		if(data != null)
		{
			for(Map<String,Object> dataSector : data)
			{
				HistoryForm hf = new HistoryForm(dataSector);
				if(hf.BookingNotCancelled)	// only add a booking to the list if it has not been cancelled
				{
					HistoryBookings.add(hf);
				}
			}
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) 
	{
		//Toast.makeText(getApplicationContext(), "Click ListItem Number " + position, Toast.LENGTH_LONG).show();
		
		//go to the Job Details view passing the History Form.
		HistoryForm hc = BookingsToLoad.get(arg2);
		
		if(HistoryMode == eHistoryModes.eHistoryPast)
			hc.HistoricBooking = true;
		
		//Launch JobDetails
		Intent i = new Intent(this, SwitchController.class);
		SwitchController.HistoryData = hc;
		startActivity(i);
	}

	@Override
	public void onCheckedChanged(RadioGroup arg0, int arg1) {
		
		if(currentconnection != null)
			currentconnection.cancel(false);
		
		if(arg1 == R.id.History_Segment_Current)
		{
			CurrentBookings = null;	// code added by stu to handle multiple segment changes
			WebSvcGetCurrentBookings();
		}
		else
		{
			HistoryBookings = null; // code added by stu to handle multiple segment changes
			WebSvcGetPastBookings();
		}
		
	}
	
	// added by Stu
	
	@Override
	public void onBackPressed()
	{
		Intent i = new Intent(this, HomeScreenActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);

		
	}

		


}
