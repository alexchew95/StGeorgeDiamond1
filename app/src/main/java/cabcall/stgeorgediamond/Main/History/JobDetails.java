package cabcall.stgeorgediamond.Main.History;

import java.text.SimpleDateFormat;
import java.util.Timer;

import org.joda.time.DateTime;
import com.google.android.maps.GeoPoint;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import cabcall.stgeorgediamond.R;
import cabcall.stgeorgediamond.Main.Defaults;
import cabcall.stgeorgediamond.Main.Defaults.eBookingModes;
import cabcall.stgeorgediamond.Main.Defaults.eCurrentModes;
import cabcall.stgeorgediamond.Main.Defaults.iCabStatusIconNumbers;
import cabcall.stgeorgediamond.Main.MainApplication;
import cabcall.stgeorgediamond.Main.BookingProcess.BookingProcess2;
import cabcall.stgeorgediamond.Main.Components.BookingForm;
import cabcall.stgeorgediamond.Main.Components.ClickableButton;
import cabcall.stgeorgediamond.Main.Components.GenericTimerTask;
import cabcall.stgeorgediamond.Main.Components.GenericTimerTask.TimerListener;
import cabcall.stgeorgediamond.Main.Components.HistoryForm;
import cabcall.stgeorgediamond.Main.History.CabWatchWebSvc.CabWatchWebSvcListener;
import cabcall.stgeorgediamond.WebService.CabcallConnection;
import cabcall.stgeorgediamond.WebService.CabcallConnectionListener;


public class JobDetails extends FragmentActivity implements OnClickListener, OnCheckedChangeListener, DialogInterface.OnClickListener, CabcallConnectionListener, CabWatchWebSvcListener, TimerListener
{
	
	
///	ArrayList< JobDetail;
	static HistoryForm HistoryData;
	
	eCurrentModes CurrentMode;
	
	boolean CancellationFailed;
	boolean HistoricBooking;
	boolean GotStatus;
	boolean TimerScheduled;
	
	HistoryForm historyForm;
	
	ClickableButton RefreshButton;
	ClickableButton RingPremierButton;
	ClickableButton CancelBookingButton;
	ClickableButton AddToFavouritesButton;
	ClickableButton NewBookingButton;
	ClickableButton HistoryAddToFavouritesButton;

	ProgressBar StatusUpdater;
	TextView PUDateLabel;
	TextView PUTimeLabel;
	TextView BookingNoLabel;
	TextView PUStreetLabel;
	TextView PUSuburbLabel;
	TextView DestSuburbLabel;
	TextView JobStatusLabel;

	ImageView JobStatusImage;
	JobDetailsDialogFragment dialogFragment;
	Timer RefreshButtonTimer;
	
	@Override
    public void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jobdetails);
 	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		RefreshButtonTimer = new Timer();
		SwitchController.JobDetailsActivity = this;
		
        StatusUpdater = (ProgressBar) findViewById(R.id.JobDetails_StatusUpdater);
        PUDateLabel = (TextView) findViewById(R.id.JobDetails_Label_PUDate);
        PUTimeLabel = (TextView) findViewById(R.id.JobDetails_Label_PUTime);
        BookingNoLabel = (TextView) findViewById(R.id.JobDetails_Label_BookingNo);
        PUStreetLabel = (TextView) findViewById(R.id.JobDetails_Label_PUStreet);
        PUSuburbLabel = (TextView) findViewById(R.id.JobDetails_Label_PUSuburb);
        DestSuburbLabel = (TextView) findViewById(R.id.JobDetails_Label_DestSuburb);
        JobStatusLabel = (TextView) findViewById(R.id.JobDetails_Label_JobStatus);
        JobStatusImage = (ImageView) findViewById(R.id.JobDetails_Image_JobStatus);
        
        RefreshButton = (ClickableButton) findViewById(R.id.JobDetails_Button_RefreshButton);
        RingPremierButton = (ClickableButton) findViewById(R.id.JobDetails_Button_RingTaxiCo);
        CancelBookingButton = (ClickableButton) findViewById(R.id.JobDetails_Button_CancelBooking);
        AddToFavouritesButton = (ClickableButton) findViewById(R.id.JobDetails_Button_AddToFavourites);
        NewBookingButton = (ClickableButton) findViewById(R.id.JobDetails_Button_NewBooking);
        HistoryAddToFavouritesButton = (ClickableButton) findViewById(R.id.JobDetails_Button_History_AddToFavourites);
 
        RefreshButton.setOnClickListener(this);
        RingPremierButton.setOnClickListener(this);
        CancelBookingButton.setOnClickListener(this);
        
        AddToFavouritesButton.setOnClickListener(this);
        NewBookingButton.setOnClickListener(this);
        HistoryAddToFavouritesButton.setOnClickListener(this);
        
        GotStatus = false;		// we haven't got the status yet
        
        historyForm = JobDetails.HistoryData;
		
	    CurrentMode = Defaults.eCurrentModes.eCurrentStatus;

	    if(!historyForm.HistoricBooking)	// Current Booking
	    {
	    	if(!GotStatus)		// haven't got the status yet - start progress indicator animation
	    	{
	    		// TODO change the image of the Status button to small_button_blank.png
	    		RefreshButton.setImageResource(R.drawable.small_button_blank);
				StatusUpdater.setVisibility(View.VISIBLE);
	    	}
	    	
	    	// disable and hide buttons used in History - Job Details Page
	    	
	    	NewBookingButton.setEnabled(false);	// make sure you can't press the button
	    	NewBookingButton.setVisibility(View.GONE);
	    	
	    	HistoryAddToFavouritesButton.setEnabled(false);
	    	HistoryAddToFavouritesButton.setVisibility(View.GONE);
	    	
	    	// enable and unhide buttons for Current - Job Details page
	    	
	    	RingPremierButton.setEnabled(true);
	    	RingPremierButton.setVisibility(View.VISIBLE);
	    	
	    	CancelBookingButton.setEnabled(true);
	    	CancelBookingButton.setVisibility(View.VISIBLE);
	    	
	    	AddToFavouritesButton.setEnabled(true);
	    	AddToFavouritesButton.setVisibility(View.VISIBLE);	// change to visible in phase 2
	    }
	    
	    else
	    {
	    	// TODO Phase 2 - see iPhone code for extra steps here
	    	HistoryForm temph = new HistoryForm();
	    	temph.StatusIdentifier = 8;
	    	temph.PUStatusString = getResources().getString(R.string.kStatusComplete);
	    	this.CabWatchUpdateTaxiCoordinates(temph, 0, new GeoPoint(0,0));
	    	
	    	// disable and hide buttons used in Current - Job Details page
	    	
	    	RingPremierButton.setEnabled(false);
	    	RingPremierButton.setVisibility(View.GONE);
	    	
	    	CancelBookingButton.setEnabled(false);
	    	CancelBookingButton.setVisibility(View.GONE);
	    	
	    	AddToFavouritesButton.setEnabled(false);
	    	AddToFavouritesButton.setVisibility(View.GONE);
	    	
	    	RefreshButton.setEnabled(false);
	    	RefreshButton.setVisibility(View.INVISIBLE);	// status button not required for history

	    	// enable and unhide buttons used in History - Job Details Page
	    	
	    	NewBookingButton.setEnabled(true);
	    	NewBookingButton.setVisibility(View.VISIBLE);
	    	
	    	HistoryAddToFavouritesButton.setEnabled(true);
	    	HistoryAddToFavouritesButton.setVisibility(View.VISIBLE);	// change to visible in phase 2

	    }

	   
	    this.setupDisplay();
	    StatusUpdater.setVisibility(View.VISIBLE);
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
	public void TimerTick() {
		PretendToRefresh();
	}
	
	public void onClick(DialogInterface dialog,int id) 
	{
		if (dialogFragment.dialogType == JobDetailsDialogFragment.eDialogType.eCancelRequest)		// user has selected a button on the "Are you sure you want to cancel" alert dialog
		{

			switch(id)
			{
			case AlertDialog.BUTTON_POSITIVE:		// Yes I want to cancel the booking
				
				// if this button is clicked, cancel the booking via webservice & tell the user that this is happening
				dialogFragment = new JobDetailsDialogFragment(this, "Cancelling Booking...", "Cancelling Booking", JobDetailsDialogFragment.eDialogType.eCancelInProgress);
				dialogFragment.show(getSupportFragmentManager(), "AlertDialog");
	
				this.WebSvcCancelBooking();
				break;
			
			case AlertDialog.BUTTON_NEGATIVE:	// No I don't want to cancel the booking
				
				// if this button is clicked, just close the dialog box and do nothing
				break;
				
			default:
				Log.d("Cancel Alert Dialog","Unknown button clicked");
				
			}
		}
		
		else if (dialogFragment.dialogType == JobDetailsDialogFragment.eDialogType.eCancelComplete)		// CancelBooking web service has completed successfully
		{
			// user has clicked OK on the Alert Dialog - following a successful cancel booking web service result.
			
			
			
			// TODO go back to History view
			
			Intent i = new Intent(this, HistoryActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);

		}
		
		else if (dialogFragment.dialogType == JobDetailsDialogFragment.eDialogType.eCancelFailed)
		{
			switch(id)
			{
			case AlertDialog.BUTTON_POSITIVE:	// close button on the Cancel Failed alert dialog i.e. user wants to return to History
				
				
				
				// TODO go back to History view
				
				this.finish();
				break;
			
			case AlertDialog.BUTTON_NEGATIVE:	// retry button on the Cancel Failed alert dialog i.e. user wants to retry the cancellation
				
				// TODO dismiss the existing dialog (Box 3)

				dialogFragment = new JobDetailsDialogFragment(this, "Cancelling Booking...", "Cancelling Booking", JobDetailsDialogFragment.eDialogType.eCancelInProgress);
				dialogFragment.show(getSupportFragmentManager(), "AlertDialog");
	
				this.WebSvcCancelBooking();
				break;
				
			default:
				Log.d("Cancel Alert Dialog","Unknown button clicked");
			}
			
		}
	}

	
	@Override
	public void onClick(View v) 
	{
		ClickableButton pressedView = (ClickableButton) v;
    	Resources r = getResources();

		if(pressedView == RefreshButton)
		{
			// Phase 1 - use GetStatus web service
			// Phase 2 - App pretends to be getting next status update;
			//this.WebSvcGetBookingStatus();
			StatusUpdater.setVisibility(View.VISIBLE);
			pressedView.setEnabled(false);
			pressedView.setImageResource(R.drawable.small_button_blank);
			RunTimer();
		}
		else if(pressedView == RingPremierButton)
		{
			Intent intent = new Intent(Intent.ACTION_CALL);

			intent.setData(Uri.parse("tel:" + r.getString(R.string.TaxiProviderPhoneNum)));
			startActivity(intent);
			
		}
		else if(pressedView == CancelBookingButton)
		{
			
			// Bring up an alert dialog to ask if you are sure you want to cancel - Yes / No buttons
			
			dialogFragment = new JobDetailsDialogFragment(this, "Confirm", "Are you sure you want to cancel this booking?", JobDetailsDialogFragment.eDialogType.eCancelRequest);
			dialogFragment.show(getSupportFragmentManager(), "AlertDialog");

		}
		else if(pressedView == AddToFavouritesButton || pressedView == HistoryAddToFavouritesButton)
		{
			MainApplication.CurrentBookingMode = eBookingModes.eCreateFavFromHistory;
			
			MainApplication.getInstance().TempBookingForm = new HistoryForm(historyForm);
			// self.Navref.TempBookingForm = self.historyForm;
			
			MainApplication.getInstance().TempBookingForm.PaxName = BookingForm.userName();			
			MainApplication.getInstance().TempBookingForm.MobileNum = BookingForm.userMobileNumber();
			
			Intent i = new Intent(this, BookingProcess2.class);
			startActivity(i);
		}
		else if(pressedView == NewBookingButton)
		{	
			
			// Creates a new booking based on Historical Booking - case 3
	
			// Copy details of history entry into booking form & move to Page 2
    
			MainApplication.CurrentBookingMode = eBookingModes.eCreateBookingFromHistory;
			//   self.Navref.CurrentBookingMode = eCreateBookingFromHistory;
    
			MainApplication.getInstance().TempBookingForm = new HistoryForm(historyForm);
			// self.Navref.TempBookingForm = self.historyForm;
			
			MainApplication.getInstance().TempBookingForm.PaxName = BookingForm.userName();			
			MainApplication.getInstance().TempBookingForm.MobileNum = BookingForm.userMobileNumber();
    
			Intent i = new Intent(this, BookingProcess2.class);
			startActivity(i);

		}
		else
		{
			Log.d("JobDetails onClick()","Unknown button click");
		}
	}
	
	
	
	public void setupDisplay()
	{
		// Date label - convert format received from Web service and turn into a DateTime object and then the desired screen format 
		try
		{
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy");		// e.g. 03-04-12
		DateTime convertedDate;
		convertedDate = new DateTime(dateFormat.parse(historyForm.PUDate));
		PUDateLabel.setText(convertedDate.toString("EEE dd-MMM-yyyy"));		// e.g. Tue 03-Apr-2012
		
		// Time label - as for date - with different formatting
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH-mm");		// e.g. 18:53
		DateTime convertedTime;
		convertedTime = new DateTime(timeFormat.parse(historyForm.PUTime));
		PUTimeLabel.setText(convertedTime.toString("h:mm a"));				// e.g. 06:53 PM
		}
	    catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// street address label
		
		if(historyForm.PlaceFieldUsed)
		{
			PUStreetLabel.setText(historyForm.PUPlace1);
		}
		else
		{
			PUStreetLabel.setText(historyForm.PUStreetNumber1 + " " + historyForm.PUStreetName1);		// e.g. 34 Smith St - PUStreetName1 in historyForm contains the designation
		}
		
		// PU suburb label
		PUSuburbLabel.setText(historyForm.PUSuburb1);
		
		// Destination suburb label
		DestSuburbLabel.setText("To: " + historyForm.DestSuburb1);
		
		// Booking No label
		BookingNoLabel.setText("Booking #: " + historyForm.JobNumber);
		
		
	}
	
	void PretendToRefresh ()
	{
		//Make the view look as though a refresh has just occurred
		RefreshButtonTimer.cancel();
		StatusUpdater.setVisibility(View.INVISIBLE);
		RefreshButton.setEnabled(true);
		RefreshButton.setImageResource(R.drawable.small_button_refresh);
	}
	
	public void WebSvcCancelBooking()
	{
		CurrentMode = eCurrentModes.eCurrentCancel;
		CabcallConnection cancelBookingMsg = new CabcallConnection("CancelBooking", this);
		
		cancelBookingMsg.setobject("nJobNumber", String.valueOf(historyForm.JobNumber)); 
		cancelBookingMsg.execute();
	}
	
	public void CabcallConnectionComplete(CabcallConnection connection)
	{	
		if(connection.SoapName.equals("CancelBooking"))
		{
			//Dismiss the progress dialog
			dialogFragment.dismiss();
			if(connection.getresponseString("CancelBookingResult").equals("Cancellation Successful"))
			{
				dialogFragment = new JobDetailsDialogFragment(this, "Cancelling Booking...", "Booking Cancelled", JobDetailsDialogFragment.eDialogType.eCancelComplete);
				dialogFragment.show(getSupportFragmentManager(), "AlertDialog");

			}
			else	// CancelBooking web service failed - prompt the user to retry or cancel via an alert dialog
			{
				dialogFragment = new JobDetailsDialogFragment(this, "Cancelling Booking...", "Cancel Failed", JobDetailsDialogFragment.eDialogType.eCancelFailed);
				dialogFragment.show(getSupportFragmentManager(), "AlertDialog");
			}
		}

	}
	
	@Override
	public void CabWatchUpdateTaxiCoordinates(HistoryForm taxicoordinates,
			int zoomLevel, GeoPoint centrepoint) {
		GotStatus = true;
		PretendToRefresh();
		
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
	public void onStop()
	{
		super.onStop();
		SwitchController.JobDetailsActivity = null;
	}
	
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ReverseGeocodeResponse(HistoryForm hform) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void GeocodeResponse(GeoPoint latlong) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void UnsuccessfulResponse() {
		// TODO Auto-generated method stub
		
	}

}
