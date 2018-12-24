package cabcall.stgeorgediamond.Main.BookingProcess;




import cabcall.stgeorgediamond.R;
import cabcall.stgeorgediamond.Main.Defaults;
import cabcall.stgeorgediamond.Main.Defaults.ActionButton;
import cabcall.stgeorgediamond.Main.Defaults.MessageTypes;
import cabcall.stgeorgediamond.Main.HomeScreenActivity;

import cabcall.stgeorgediamond.Main.Defaults.eBools;
import cabcall.stgeorgediamond.Main.MainApplication;
import cabcall.stgeorgediamond.Main.Components.BookingForm;
import cabcall.stgeorgediamond.Main.Components.ClickableButton;
import cabcall.stgeorgediamond.Main.Components.ConfirmationDialogFragment;
import cabcall.stgeorgediamond.Main.Components.HistoryForm;
import cabcall.stgeorgediamond.Main.Favourites.FavouriteHome;
import cabcall.stgeorgediamond.Main.History.HistoryActivity;
import cabcall.stgeorgediamond.WebService.CabcallConnection;
import cabcall.stgeorgediamond.WebService.CabcallConnectionListener;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Confirmation extends FragmentActivity implements OnClickListener, DialogInterface.OnClickListener, CabcallConnectionListener {

	ImageView TickImage;
	TextView ConfirmationText;
	ClickableButton FinishButton;
	ProgressBar ActivityIndicator;
	int RequestAttemptCount;
	
	String message;
	String task;
	String title;


	@Override
    public void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
        if(!MainApplication.PrimeConfirmationWebService)
        {
        	Intent i = new Intent(this, HomeScreenActivity.class);
        	//If we are not meant to be here because in either 
			//history or favourites the app has crashed, bringing this activity up, the app goes back to home
        	i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        	startActivity(i);
        }
        else
        	setContentView(R.layout.confirmation);
        
       
       
        // need an activity indicator
	}

	public void onClick(DialogInterface dialog,int id) 
	{
		switch(id)
		{
		case AlertDialog.BUTTON_POSITIVE:
		// if this button is clicked, close current activity
			
			Intent newi = new Intent(this, HomeScreenActivity.class);
			newi.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(newi);
			
		break;
		
		case AlertDialog.BUTTON_NEGATIVE:
		// if this button is clicked, just close
		// the dialog box and do nothing
			
			this.finish();
		break;
		
		default:
		// if this button is clicked, resend the web service request
			
			this.WebSvcAction();
		break;
		}
	}
	
	@Override
	public void onClick(View v) 
	{
		if(Defaults.BookingModes[MainApplication.CurrentBookingMode.ordinal()][2] == eBools.eTrue)  // Favourite
		{
			Intent i = new Intent(this, FavouriteHome.class);
			startActivity(i);
		}
		else
		{
			Intent i = new Intent(this, HistoryActivity.class);
			startActivity(i);
		}
	}
	
	@Override
	public void onStart ()
	{
		super.onStart();

		if(Defaults.BookingModes[MainApplication.CurrentBookingMode.ordinal()][2] == eBools.eTrue)  // Favourite
		{
			this.setTitle(R.string.ConfirmationTitleFavourites);
		}
		else
		{
			this.setTitle(R.string.ConfirmationTitleNormal);
		}
		
		FinishButton = (ClickableButton) findViewById(R.id.Confirmation_Button_FinishButton);
        FinishButton.setOnClickListener(this);
		ConfirmationText = (TextView) findViewById(R.id.Confirmation_TextField_StatusText);
		TickImage = (ImageView) findViewById(R.id.Confirmation_Image_Tick);
		ActivityIndicator = (ProgressBar) findViewById(R.id.Confirmation_ActivityIndicator);
		RequestAttemptCount = 0;
		
		if(MainApplication.PrimeConfirmationWebService)
			this.WebSvcAction();
		else
		{
			//If we are not meant to be here because in either 
			//history or favourites the app has crashed, bringing this activity up, the app goes back to home
			Intent i = new Intent(this, HomeScreenActivity.class);
        	i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        	startActivity(i);
		}
	}
	
	
	public void WebSvcAction ()
	{
		switch((MessageTypes)Defaults.BookingModes[MainApplication.CurrentBookingMode.ordinal()][1])
		{
		case eCBAOP:
			this.WebSvcBookTaxi();
			ConfirmationText.setText("Booking your Taxi now...");
			break;
		case eCF:
			this.WebSvcAddFavourite();
			ConfirmationText.setText("Updating your Profile now...");
			break;
		case eUF:
			this.WebSvcModifyFavourite();
			ConfirmationText.setText("Updating your Profile now...");
			break;
		default:
			Log.d("WebSvcAction()","Unknown web service type");
			break;
		}
		MainApplication.PrimeConfirmationWebService = false; //Tells the system that this activity cannot resend the webservice if it is not launched via BP3
	}
	
	public void RequestSuccessful()
	{
		// stop animating activity indicator
		ActivityIndicator.setVisibility(View.INVISIBLE);
		FinishButton.setVisibility(View.VISIBLE);
		TickImage.setVisibility(View.VISIBLE);
		switch((ActionButton)Defaults.BookingModes[MainApplication.CurrentBookingMode.ordinal()][4])
		{
		case eBook:
			ConfirmationText.setText(R.string.ConfirmationSuccessMsgNormal);
			break;
		case eSaveFavourite:
			ConfirmationText.setText(R.string.ConfirmationSuccessMsgFavourites);
			break;
		default:
			Log.d("RequestSuccessful()","Unknown booking mode");
			break;
		}
	}
	
	public void RequestFailed()
	{
		String kAppName = MainApplication.getInstance().getResources().getString(R.string.app_name);
		switch((ActionButton) Defaults.BookingModes[MainApplication.CurrentBookingMode.ordinal()][4])
		{
		case eBook:
			Log.d("RequestFailed()","The booking request failed");
			task = "Booking Request";
			title = "Booking";
			break;
		case eSaveFavourite:
			Log.d("RequestFailed()","The favourite update failed");
			task = "Favourite Request";
			title = "Favourite";
			break;
		default:
			Log.d("RequestFailed()","Unknown booking mode");
			// log an error
			break;
		}
		
		Log.d("RequestFailed()","Web service request failed");
		message = "The " + task + " cannot be made at the current time, " + kAppName + " apologizes for any inconvenience";
		
		ConfirmationDialogFragment dialogFragment = new ConfirmationDialogFragment(this, title, message);
		dialogFragment.show(getSupportFragmentManager(), "AlertDialog");
	}
	
	public void WebSvcBookTaxi()
	{
		Resources Res = getResources();
		RequestAttemptCount++;
		HistoryForm bookingForm = MainApplication.getInstance().TempBookingForm;
		bookingForm.PurifyForm();
		
		CabcallConnection createBookingMsg = new CabcallConnection("CreateSPBookingAddressOrPlace", this);
		
		createBookingMsg.setobject("BookingDate", bookingForm.BookingPUDate); // need to ensure this is in the ISO format
		createBookingMsg.setobject("BookingTime", bookingForm.BookingPUTime);
		createBookingMsg.setobject("PUSuburb1", bookingForm.PUSuburb1);
		
		if(!bookingForm.PlaceFieldUsed)		// address not a place
		{
			String[] streetAddressComponents = bookingForm.PUStreetName1.split(" ");
			int nComponents = streetAddressComponents.length;
			StringBuilder sStreetName = new StringBuilder();
			if (nComponents > 1)	// streets with at least two words e.g. "Smith St"
			{
				for (int i = 0; i < (nComponents - 2); i++)
				{
					sStreetName.append(" " + streetAddressComponents[i]);
				}
				
				sStreetName.append(" " + streetAddressComponents[nComponents-2]);
				createBookingMsg.setobject("PUStreetName1", sStreetName.toString());
				createBookingMsg.setobject("PUDesignation1",streetAddressComponents[nComponents-1]);
				createBookingMsg.setobject("PUStreetNumber1", bookingForm.PUStreetNumber1);
			}
			else	// streets with one word e.g. "Esplanade" in Cairns
			{
				createBookingMsg.setobject("PUStreetName1", bookingForm.PUStreetName1);
				createBookingMsg.setobject("PUDesignation1", " ");  // to help Cab Call
				createBookingMsg.setobject("PUStreetNumber1", bookingForm.PUStreetNumber1);
			}
		}
		else	// place
		{
			createBookingMsg.setobject("PUPlace1", bookingForm.PUPlace1);
		}
		
		createBookingMsg.setobject("PURemarks1", bookingForm.PURemarks1);
		createBookingMsg.setobject("PaxName", bookingForm.PaxName);
		createBookingMsg.setobject("PhoneNumber", BookingForm.userMobileNumber());
		
		createBookingMsg.setobject("ContactName", Res.getString(R.string.contactnametag) + "SPID " + BookingForm.Spid());
		
		createBookingMsg.setobject("NumberPax", "" + bookingForm.NumPax);
		
		createBookingMsg.setobject("DestSuburb1", bookingForm.DestSuburb1);
		
		createBookingMsg.setobject("COA", "false");
		createBookingMsg.setobject("MOA", "true");
		createBookingMsg.setobject("FleetNumber", MainApplication.getInstance().getResources().getString(R.string.FleetNumber));
		createBookingMsg.execute();

	}
	
	public void WebSvcUpdateBooking ()
	{
		RequestAttemptCount++;
	}
	
	public void WebSvcAddFavourite()
	{
		RequestAttemptCount++;
		HistoryForm bookingForm = MainApplication.getInstance().TempBookingForm;
		
		CabcallConnection createFavouriteMsg = new CabcallConnection("CreateFavourite", this);
		createFavouriteMsg.setobject("PUSuburb1", bookingForm.PUSuburb1);
		
		if(!bookingForm.PlaceFieldUsed)		// address not a place
		{
			String[] streetAddressComponents = bookingForm.PUStreetName1.split(" ");
			int nComponents = streetAddressComponents.length;
			StringBuilder sStreetName = new StringBuilder();
			if (nComponents > 1)	// streets with at least two words e.g. "Smith St"
			{
				for (int i = 0; i < (nComponents - 2); i++)
				{
					sStreetName.append(" " + streetAddressComponents[i]);
				}
				
				sStreetName.append(" " + streetAddressComponents[nComponents-2]);
				createFavouriteMsg.setobject("PUStreetName1", sStreetName.toString());
				createFavouriteMsg.setobject("PUDesignation1",  streetAddressComponents[nComponents-1]);
				createFavouriteMsg.setobject("PUStreetNumber1", bookingForm.PUStreetNumber1);
			}
			else	// streets with one word e.g. "Esplanade" in Cairns
			{
				createFavouriteMsg.setobject("PUStreetName1", bookingForm.PUStreetName1);
				createFavouriteMsg.setobject("PUDesignation1", " ");  // to help Cab Call
				createFavouriteMsg.setobject("PUStreetNumber1", bookingForm.PUStreetNumber1);
			}
		}
		else	// place
		{
			createFavouriteMsg.setobject("PUPlace1", bookingForm.PUPlace1);
		}
		
		createFavouriteMsg.setobject("PURemarks1", bookingForm.PURemarks1);
		createFavouriteMsg.setobject("PaxName", bookingForm.PaxName);
		createFavouriteMsg.setobject("PhoneNumber", BookingForm.userMobileNumber());
		createFavouriteMsg.setobject("CustomerNumber", BookingForm.userMobileNumber());
		createFavouriteMsg.setobject("NumberPax", "" + bookingForm.NumPax);
		createFavouriteMsg.setobject("DestSuburb1", bookingForm.DestSuburb1);
		createFavouriteMsg.setobject("FavouriteName", bookingForm.FavouriteName1);
		createFavouriteMsg.setobject("FleetNumber", MainApplication.getInstance().getResources().getString(R.string.FleetNumber));
		
		createFavouriteMsg.execute();
	}
	
	public void WebSvcModifyFavourite ()
	{
		HistoryForm bookingForm = MainApplication.getInstance().TempBookingForm;
		CabcallConnection deleteFavouriteMsg = new CabcallConnection("DeleteFavourite", this);
		deleteFavouriteMsg.setobject("CustomerNumber", BookingForm.userMobileNumber());
		deleteFavouriteMsg.setobject("FavouriteName", bookingForm.FavouriteName2);
		
		deleteFavouriteMsg.execute();
	}
	
	public void CabcallConnectionComplete(CabcallConnection connection)
	{	
		if(connection.SoapName.equals("CreateSPBookingAddressOrPlace"))
		{
			try{
				if(MainApplication.SafeInt(connection.getresponseString("CreateSPBookingAddressOrPlaceResult")) != 0)
					this.RequestSuccessful();
				else
					this.RequestFailed();
			}
			catch (Exception e)
			{
				this.RequestFailed();
			}
			

		}
		else if(connection.SoapName.equals("CreateFavourite"))
		{
			if(connection.getresponseString("ReturnString").equals("Success"))
				this.RequestSuccessful();
			else
				this.RequestFailed();
		}
		else if(connection.SoapName.equals("DeleteFavourite"))
		{
			if(connection.getresponseString("DeleteFavouriteResult").equals("true"))
				this.WebSvcAddFavourite();
			else
				this.RequestFailed();
		}
		else
			Log.d("CabcallConnectionComplete()","Web service unknown msg type");
	}
	
	@Override 
    public void onBackPressed()
    {
    	//Ignore any attempts to go backwards
    	return;
    }
	
}