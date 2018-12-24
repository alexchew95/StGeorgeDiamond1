package cabcall.stgeorgediamond.Main.Favourites;

import java.util.ArrayList;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cabcall.stgeorgediamond.R;
import cabcall.stgeorgediamond.Main.Defaults.eBookingModes;
import cabcall.stgeorgediamond.Main.HomeScreenActivity;
import cabcall.stgeorgediamond.Main.MainApplication;
import cabcall.stgeorgediamond.Main.BookingProcess.BookingProcess1;
import cabcall.stgeorgediamond.Main.BookingProcess.BookingProcess2;
import cabcall.stgeorgediamond.Main.Components.BookingForm;
import cabcall.stgeorgediamond.Main.Components.ClickableButton;
import cabcall.stgeorgediamond.Main.Components.GlowPanel;
import cabcall.stgeorgediamond.Main.Components.HistoryForm;
import cabcall.stgeorgediamond.Main.Components.favouritelistadapter;
import cabcall.stgeorgediamond.WebService.CabcallConnection;
import cabcall.stgeorgediamond.WebService.CabcallConnectionListener;

public class FavouriteHome extends Activity implements OnClickListener, OnItemClickListener, CabcallConnectionListener
{
	ArrayList<HistoryForm> FavouriteList;
		
	boolean GoingForward;		// true when user clicks on entry in Favourite List or Adds a favourite; else it is false - need to know when false so that step 1 and 2 screens function OK when Fav Home is exited
	
	ListView FavouriteView;
	ArrayList<Map<String,Object>> data;
	ProgressBar ActivityIndicator;
	CabcallConnection currentconnection;
	ClickableButton AddFavouriteButton;
	TextView AddText;
	GlowPanel AddTextGlowPanel;
	
	@Override
    public void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favourite);
 	}
	
	@Override
	public void onStart()
	{
		super.onStart();

		AddFavouriteButton = (ClickableButton) findViewById(R.id.Favourite_Button_Add);
		AddFavouriteButton.setOnClickListener(this);
		AddText = (TextView) findViewById(R.id.Favourite_Label_Add);
		AddTextGlowPanel = (GlowPanel) findViewById(R.id.Favourite_GlowPanel);

		// event handlers
		
        FavouriteView = (ListView) findViewById(R.id.Favourite_ListView);
 
        FavouriteView.setOnItemClickListener(this);   // set up a listener for when a row is clicked.   
        
		if(MainApplication.CurrentBookingMode != eBookingModes.eBookNewFromFav)
		{
			MainApplication.CurrentBookingMode = eBookingModes.eNoBookingMode;
			
			// add the "+" button to the nav bar
			AddFavouriteButton.setEnabled(true);
			AddFavouriteButton.setVisibility(View.VISIBLE);
			AddText.setVisibility(View.VISIBLE);
			AddTextGlowPanel.setVisibility(View.VISIBLE);
		}
		else
		{
			AddFavouriteButton.setEnabled(false);
			AddFavouriteButton.setVisibility(View.GONE);	// Add Favourite button not required for book from favourite
			AddText.setVisibility(View.GONE);
			AddTextGlowPanel.setVisibility(View.GONE);
		}
		
        ActivityIndicator = (ProgressBar) findViewById(R.id.Favourite_ActivityIndicator);
 
        WebSvcGetFavourite();
        
        GoingForward = false;		// reset the going forward flag
	}
	
	public void WebSvcGetFavourite()
	{
		ActivityIndicator.setVisibility(View.VISIBLE);	// start animating progress bar
		
		CabcallConnection getFavouriteListMsg = new CabcallConnection("GetSPFavouriteList", this);
		getFavouriteListMsg.setobject("CustomerNumber", BookingForm.userMobileNumber());

		getFavouriteListMsg.execute();
	}
	
	@Override
	public void CabcallConnectionComplete(CabcallConnection connection)
	{
		ActivityIndicator.setVisibility(View.INVISIBLE);
		FavouriteList = new ArrayList<HistoryForm>();
		
		favouritelistadapter adapter = new favouritelistadapter(this, R.layout.favouritelistitem, FavouriteList);
        FavouriteView.setAdapter(adapter);
        
		data = connection.getResponseList();
		if(data != null)
		{
			for(Map<String,Object> dataSector : data)
			{
				HistoryForm hf = new HistoryForm(dataSector, false);	// false avoids initialization of the History parameters
				FavouriteList.add(hf);
			}
		}	
	}
			

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) 
	{
		GoingForward = true;
		
		if(MainApplication.CurrentBookingMode != eBookingModes.eBookNewFromFav)
		{
			//go to the Favourite Details view passing the Favourite (in a HistoryForm).
			HistoryForm hf = FavouriteList.get(arg2);
					
			//Launch FavouriteDetail
			Intent i = new Intent(this, FavouriteDetails.class);
			FavouriteDetails.FavouriteData = hf;	
			startActivity(i);
		}
		else
		{
			// go to Booking process step 2 with the favourite stored in the TempBookingForm
			MainApplication.getInstance().TempBookingForm = FavouriteList.get(arg2);  
			
			Intent i = new Intent(this, BookingProcess2.class);		// go to Booking Process step 2
			startActivity(i);
		}
	}

	@Override
	public void onClick(View v) 
	{
		ClickableButton pressedView = (ClickableButton) v;

		if(pressedView == AddFavouriteButton)
		{
			GoingForward = true;
			MainApplication.CurrentBookingMode = eBookingModes.eNewFav;
			MainApplication.getInstance().TempBookingForm.ResetForm();		// not sure about this line

			Intent i = new Intent(this, BookingProcess1.class);		// go to Booking Process step 1
			startActivity(i);
		}
		else
		{
			Log.d("FavouriteHome onClick()","Unknown button click");
		}
	}

	
	@Override
	public void onBackPressed()
	{
		Intent i = new Intent(this, HomeScreenActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
	}
	
	@Override
	public void onStop()
	{
		super.onStop();
		if(!GoingForward)		// not going to Favourite Details nor Add a new favourite, must be going back to Home screen 
		{
			MainApplication.CurrentBookingMode = eBookingModes.eNoBookingMode;	// reset the Current Booking mode flag otherwise when user enters Book Page 1 app will be confused.
		}
	}

		


}
