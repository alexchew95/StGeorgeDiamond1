package cabcall.stgeorgediamond.Main.BookingProcess;

import java.util.ArrayList;

import org.joda.time.DateTime;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import cabcall.stgeorgediamond.R;
import cabcall.stgeorgediamond.Main.Defaults;
import cabcall.stgeorgediamond.Main.Defaults.ActionButton;
import cabcall.stgeorgediamond.Main.Defaults.eBools;
import cabcall.stgeorgediamond.Main.MainApplication;
import cabcall.stgeorgediamond.Main.Components.ClickableButton;
import cabcall.stgeorgediamond.Main.Components.HistoryForm;
import cabcall.stgeorgediamond.Main.Components.customlistitem1;
import cabcall.stgeorgediamond.Main.Components.customlistitem1adapter;
import cabcall.stgeorgediamond.WebService.CabcallConnection;
import cabcall.stgeorgediamond.WebService.CabcallConnectionListener;

public class BookingProcess3 extends Activity implements OnClickListener, CabcallConnectionListener {
	
	public enum FareEstimateStatus
	{
		Unobtained,
		Obtaining,
		Obtained,
		Unuseful,
	}
	
	ListView DetailsView;
	ArrayList<customlistitem1> DetailsList;
	
	ClickableButton ReturnButton;
	FareEstimateStatus FareEstimateStat;
	String kTaxiProviderPhoneNum;
	
	ClickableButton DisclaimerButton;
	ProgressBar ActivityIndicator;
	TextView DisclaimerLabel;
	TextView DisclaimerDetailsLabel;
	String sFareEst;
		
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookingprocess3);
        DetailsView = (ListView) findViewById(R.id.BookingProcess3_ListView);
        
        DetailsList = new ArrayList<customlistitem1>();
        
        customlistitem1adapter adapter = new customlistitem1adapter(this, R.layout.customlistitem1, DetailsList);
        DetailsView.setAdapter(adapter);
        
        ActivityIndicator = (ProgressBar) findViewById(R.id.BookingProcess3_ActivityIndicator);
        DisclaimerLabel = (TextView) findViewById(R.id.BookingProcess3_Label_DisclaimerLabel);
        DisclaimerDetailsLabel = (TextView) findViewById(R.id.BookingProcess3_Label_DisclaimerDetails);
        DisclaimerButton = (ClickableButton) findViewById(R.id.BookingProcess3_Button_Disclaimer);
        DisclaimerButton.setOnClickListener(this);
        
        ReturnButton = (ClickableButton) findViewById(R.id.BookingProcess3_Button_Confirm);
        ReturnButton.setOnClickListener(this);
        
        FareEstimateStat = FareEstimateStatus.Unobtained;
    }
	
	public void CreateRow (String Title, String Content)
	{
		DetailsList.add(new customlistitem1(Title,Content,false,false));
	}
	
	public void SetDisclaimerRow (String Content)
	{
		if(FareEstimateStat == FareEstimateStatus.Obtaining)
		{
			ActivityIndicator.setVisibility(View.VISIBLE);
			DisclaimerButton.setVisibility(View.INVISIBLE);
		}
		else if(FareEstimateStat == FareEstimateStatus.Obtained)
		{
			ActivityIndicator.setVisibility(View.INVISIBLE);
			DisclaimerButton.setVisibility(View.VISIBLE);
		}
		else if(FareEstimateStat == FareEstimateStatus.Unuseful)
		{
			ActivityIndicator.setVisibility(View.INVISIBLE);
			DisclaimerButton.setVisibility(View.INVISIBLE);
			DisclaimerButton.setEnabled(false);
		}
		DisclaimerDetailsLabel.setText(Content);
	}

	@Override
	public void onClick(View v) {
		if(v == ReturnButton)
		{
			MainApplication.PrimeConfirmationWebService = true;
			Intent i = new Intent(this,Confirmation.class);
			startActivity(i);			
		}
		else if (v == DisclaimerButton)
		{
			Disclaimer.EstimateString = sFareEst;
			
			Intent i = new Intent(this,Disclaimer.class);
			startActivity(i);			
				
		}
	}
	
	@Override
	public void onStart ()
	{
		super.onStart();

		this.DrawReturnButtonAndAdditionalControls();
		HistoryForm bookingForm = MainApplication.getInstance().TempBookingForm;

		if(Defaults.BookingModes[MainApplication.CurrentBookingMode.ordinal()][2] == eBools.eTrue)  // Favourite
		{
			this.CreateRow("Favourite Name:", bookingForm.FavouriteName1);	// add Favourite Name to top of list view
		}

		this.CreateRow("Passenger Name:", bookingForm.PaxName);
		
		
		String sPUAddress;
		
		if(bookingForm.PlaceFieldUsed)
		{
			sPUAddress = bookingForm.PUPlace1 + ", "+ bookingForm.PUSuburb1;
		}
		else
		{
			sPUAddress = bookingForm.PUStreetNumber1 + " " + bookingForm.PUStreetName1 + ", " + bookingForm.PUSuburb1;	
		}
		
		this.CreateRow("Pickup:", sPUAddress);
		
		String sPUTime;
		
		if((bookingForm.BookingPUDate == null) || (bookingForm.BookingPUTime == null))
		{
			// get current time and put this in row
			
    		DateTime currentTime = new DateTime();
    		sPUTime = currentTime.toString("EEE dd-MM-yy h:mm a");
		}
		else
		{
			sPUTime = bookingForm.PUTimeLabel;
		}
		
		this.CreateRow("Pick Up Date & Time:", sPUTime);
		
		this.CreateRow("Destination:", bookingForm.DestSuburb1);
		
		String sPURemarks;
		
		if((bookingForm.PURemarks1 == null) || (bookingForm.PURemarks1.length() == 0))
		{
			sPURemarks = "No Instructions";
		}
		else
		{
			sPURemarks = bookingForm.PURemarks1;
		}
		
		this.CreateRow("Driver Instructions:", sPURemarks);
		
		String sNumPax;
		
		if(bookingForm.NumPax == 1)
		{
			sNumPax = "1 Passenger";
		}
		else
		{
			sNumPax = bookingForm.NumPax + " Passengers";
		}
		
		this.CreateRow("Num of Passengers:", sNumPax);

		if(Defaults.BookingModes[MainApplication.CurrentBookingMode.ordinal()][2] == eBools.eTrue)  // Favourite
		{
			this.setTitle(R.string.BP3TitleFavourites);
			this.DisclaimerLabel.setVisibility(View.INVISIBLE);
			this.DisclaimerDetailsLabel.setVisibility(View.INVISIBLE);
			this.DisclaimerButton.setVisibility(View.INVISIBLE);
			this.ActivityIndicator.setVisibility(View.INVISIBLE);
			
		}
		else
		{
			this.setTitle(R.string.BP3TitleNormal);	
			kTaxiProviderPhoneNum = MainApplication.getInstance().getResources().getString(R.string.TaxiProviderPhoneNum);
			if(FareEstimateStat == FareEstimateStatus.Unobtained)
			{
				this.GetFareEstimate();	// fare estimate will be entered when GetFareEstimate returns
				this.SetDisclaimerRow("Downloading Fare Estimate...");
				// TODO need to add a button to display the disclaimer view - and annimate an activity indicator		
			}
			else
			{
				this.SetDisclaimerRow(sFareEst);
			}
		}
		
		// might need some code to get the colours sorted - then again might not		
		
	}
	
	public void DrawReturnButtonAndAdditionalControls ()
	{
		int imageDrawable;
		
		switch ((ActionButton) Defaults.BookingModes[MainApplication.CurrentBookingMode.ordinal()][4])
		{
		case eBook:
			imageDrawable = R.drawable.button_book;
			break;
		case eSaveFavourite:
			imageDrawable = R.drawable.button_save_favourite;
			break;
		default:
			imageDrawable = R.drawable.button_book;
			// log an error
			break;
		}
		
		// set the image for the return button to ImageName
		ReturnButton.setImageResource(imageDrawable);
	}
	
	public void GetFareEstimate ()
	{
		FareEstimateStat = FareEstimateStatus.Obtaining;
		String FLEET_NUMBER = MainApplication.getInstance().getResources().getString(R.string.FleetNumber);
		CabcallConnection getEst = new CabcallConnection("GetFareEstimate", this);
		getEst.setobject("FleetNumber",FLEET_NUMBER);
		HistoryForm bookingForm = MainApplication.getInstance().TempBookingForm;
		
		if(bookingForm.PlaceFieldUsed)
		{
			getEst.setobject("PUPlace1",bookingForm.PUPlace1);
		}
		else
		{
			getEst.setobject("PUStreetNumber1", bookingForm.PUStreetNumber1);
			String[] streetAddressComponents = bookingForm.PUStreetName1.split(" ");
			getEst.setobject("PUStreetName1", streetAddressComponents[0]);
			if(streetAddressComponents.length > 1)
				getEst.setobject("PUDesignation1", streetAddressComponents[1]);
		}
		
		getEst.setobject("PUSuburb1", bookingForm.PUSuburb1);
		getEst.setobject("DestSuburb1", bookingForm.DestSuburb1);
		getEst.setobject("cFareExTolls", "0");
		getEst.setobject("cFareIncTolls", "0");
		
		getEst.execute();
	}
	
	@Override
	public void CabcallConnectionComplete(CabcallConnection connection)
	{
		HistoryForm bookingForm = MainApplication.getInstance().TempBookingForm;
		if(connection.getresponseString("sResult") != null && connection.getresponseString("sResult").equals("Success"))
		{
			bookingForm.EstTollRoads = (int) MainApplication.SafeDouble(connection.getresponseString("cFareIncTolls"));
			bookingForm.EstNoTollRoads = (int) MainApplication.SafeDouble(connection.getresponseString("cFareExTolls"));
			
			if((bookingForm.EstTollRoads == 0) || (bookingForm.EstNoTollRoads == 0))
			{
				FareEstimateStat = FareEstimateStatus.Unuseful;
				sFareEst = "Please call " + kTaxiProviderPhoneNum + " for a fare estimate";  // handles case where fare estimator is not optioned within MTData
			}
			else
			{
				FareEstimateStat = FareEstimateStatus.Obtained;
				sFareEst = "Toll roads: $" + bookingForm.EstTollRoads + "; No toll roads: $" + bookingForm.EstNoTollRoads;
			}
		}
		else
		{
			FareEstimateStat = FareEstimateStatus.Unuseful;
			sFareEst = "Please call " + kTaxiProviderPhoneNum + " for a fare estimate";  // handles case where fare estimator is not optioned within MTData
		}
		SetDisclaimerRow(sFareEst);
	}
	
}
