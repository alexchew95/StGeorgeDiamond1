package cabcall.stgeorgediamond.Main.Favourites;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import cabcall.stgeorgediamond.R;
import cabcall.stgeorgediamond.Main.Defaults;
import cabcall.stgeorgediamond.Main.Defaults.eBookingModes;
import cabcall.stgeorgediamond.Main.Defaults.eCurrentModes;
import cabcall.stgeorgediamond.Main.MainApplication;
import cabcall.stgeorgediamond.Main.BookingProcess.BookingProcess1;
import cabcall.stgeorgediamond.Main.BookingProcess.BookingProcess2;
import cabcall.stgeorgediamond.Main.Components.BookingForm;
import cabcall.stgeorgediamond.Main.Components.ClickableButton;
import cabcall.stgeorgediamond.Main.Components.HistoryForm;
import cabcall.stgeorgediamond.WebService.CabcallConnection;
import cabcall.stgeorgediamond.WebService.CabcallConnectionListener;


public class FavouriteDetails extends Activity implements OnClickListener, CabcallConnectionListener
{
	static HistoryForm FavouriteData;
	
	eCurrentModes CurrentMode;
	
	boolean GoingForward;
	
	HistoryForm favouriteForm;
	
	ClickableButton EditFavouriteButton;
	ClickableButton NewBookingButton;
	ClickableButton DeleteFavouriteButton;

	TextView FavNameLabel;
	TextView PUStreetLabel;
	TextView PUSuburbLabel;
	TextView DestSuburbLabel;
	
	@Override
    public void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favouritedetails);
        
 	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		
        FavNameLabel = (TextView) findViewById(R.id.FavouriteDetails_Label_FavName);
        PUStreetLabel = (TextView) findViewById(R.id.FavouriteDetails_Label_PUStreet);
        PUSuburbLabel = (TextView) findViewById(R.id.FavouriteDetails_Label_PUSuburb);
        DestSuburbLabel = (TextView) findViewById(R.id.FavouriteDetails_Label_DestSuburb);

        EditFavouriteButton = (ClickableButton) findViewById(R.id.FavouriteDetails_Button_EditFavourite);
        NewBookingButton = (ClickableButton) findViewById(R.id.FavouriteDetails_Button_NewBooking);
        DeleteFavouriteButton = (ClickableButton) findViewById(R.id.FavouriteDetails_Button_DeleteFavourite);
 
        EditFavouriteButton.setOnClickListener(this);
        NewBookingButton.setOnClickListener(this);
        DeleteFavouriteButton.setOnClickListener(this);
        
        favouriteForm = FavouriteDetails.FavouriteData;
		
	    CurrentMode = Defaults.eCurrentModes.eCurrentStatus;
	    
	    if(!GoingForward)
	    {
	    	MainApplication.CurrentBookingMode = eBookingModes.eNoBookingMode;
	    }
	    
	    GoingForward =false;

	    this.setupDisplay();  
	}	
	
	@Override
	public void onClick(View v) 
	{
		ClickableButton pressedView = (ClickableButton) v;

		if(pressedView == NewBookingButton)
		{	
			// Creates a new booking based on Favourite Data - case 5
			
			// Copy details of favourite into history form
			
			// move to Page 2
    
			MainApplication.CurrentBookingMode = eBookingModes.eBookNewFromFav;
			GoingForward = true;
    
			MainApplication.getInstance().TempBookingForm = new HistoryForm(favouriteForm);			
			MainApplication.getInstance().TempBookingForm.PaxName = BookingForm.userName();			
			MainApplication.getInstance().TempBookingForm.MobileNum = BookingForm.userMobileNumber();
			
			Intent i = new Intent(this, BookingProcess2.class);       //  go to page 2 to allow suburb to be entered
			startActivity(i);

		}

		else if(pressedView == EditFavouriteButton)
		{
			// Allows Favourite to be Edited via the Page 123 views - case 6
			
			// Copy details of favourite into booking form
			
			// move to Page 1
			MainApplication.CurrentBookingMode = eBookingModes.eModifyFav;
		    
			MainApplication.getInstance().TempBookingForm = new HistoryForm(favouriteForm);
			MainApplication.getInstance().TempBookingForm.PaxName = BookingForm.userName();			
			MainApplication.getInstance().TempBookingForm.MobileNum = BookingForm.userMobileNumber();
			
			GoingForward = true;
			Intent i = new Intent(this, BookingProcess1.class);       //  go to page 1
			startActivity(i);				
		} 

		else if(pressedView == DeleteFavouriteButton)
		{
			this.WebSvcDeleteFavourite();
		}
		
		else
		{
			Log.d("FavouriteDetails onClick()","Unknown button click");
		}
	}
	
	
	
	public void setupDisplay()
	{
		
		// Favourite Name label
		FavNameLabel.setText(favouriteForm.FavouriteName1);
		
		// street address label
		
		if(favouriteForm.PlaceFieldUsed)
		{
			PUStreetLabel.setText(favouriteForm.PUPlace1);
		}
		else
		{
			PUStreetLabel.setText(favouriteForm.PUStreetNumber1 + " " + favouriteForm.PUStreetName1);		// e.g. 34 Smith St - PUStreetName1 in favouriteForm contains the designation
		}
		
		// PU suburb label
		PUSuburbLabel.setText(favouriteForm.PUSuburb1);
		
		// Destination suburb label
		if(favouriteForm.DestSuburb1 == null || favouriteForm.DestSuburb1.length() == 0)
			DestSuburbLabel.setText("");
		else
			DestSuburbLabel.setText("To: " + favouriteForm.DestSuburb1);
	}
	
	public void WebSvcDeleteFavourite()
	{
		CurrentMode = eCurrentModes.eCurrentCancel;
		CabcallConnection deleteFavouriteMsg = new CabcallConnection("DeleteFavourite", this);
		
		deleteFavouriteMsg.setobject("CustomerNumber", BookingForm.userMobileNumber());
		deleteFavouriteMsg.setobject("FavouriteName", favouriteForm.FavouriteName1);
//		deleteFavouriteMsg.setobject("FleetNumber", MainApplication.getInstance().getResources().getString(R.string.FleetNumber));

		deleteFavouriteMsg.execute();
	}
	
	
	public void CabcallConnectionComplete(CabcallConnection connection)
	{	
		if(connection.SoapName.equals("DeleteFavourite"))
		{
			Intent i = new Intent(this, FavouriteHome.class);
			startActivity(i);

		}
		else
		{
			Log.d("FavouriteDetails CabCallConnectionComplete()","Unknown web service returned");
		}
		

	}

	
}
