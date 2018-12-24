package cabcall.stgeorgediamond.Main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import cabcall.stgeorgediamond.Main.BookingProcess.BookingProcess1;
import cabcall.stgeorgediamond.Main.Components.BookingForm;
import cabcall.stgeorgediamond.Main.Components.ClickableButton;
import cabcall.stgeorgediamond.Main.Favourites.FavouriteHome;
import cabcall.stgeorgediamond.Main.Defaults.eBookingModes;
import cabcall.stgeorgediamond.Main.History.HistoryActivity;
import cabcall.stgeorgediamond.Main.RankFinder.RankFinderActivity;
import cabcall.stgeorgediamond.R;

public class HomeScreenActivity extends Activity implements OnClickListener {
    /** Called when the activity is first created. */
	ClickableButton BookATaxiButton;
	ClickableButton FavouritesButton;
	ClickableButton HistoryButton;
	ClickableButton RankFinderButton;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen_full);
      //Checks all necessary fields to see if we need to launch the FirstTime Screen
    	if(BookingForm.Spid() == null || BookingForm.userMobileNumber() == null || BookingForm.userID() == null || BookingForm.userName() == null)
    	{
    		Intent i = new Intent(this,HomeScreenActivity.class);

    		i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    		startActivity(i);
    	}
        
        
        
       
    }
    
    @Override
    public void onStart()
    {
    	super.onStart();
    	BookATaxiButton = (ClickableButton) findViewById(R.id.HomeScreen_Button_BookTaxi);
        FavouritesButton = (ClickableButton) findViewById(R.id.HomeScreen_Button_Favourites);
        HistoryButton = (ClickableButton) findViewById(R.id.HomeScreen_Button_History);
        RankFinderButton = (ClickableButton) findViewById(R.id.HomeScreen_Button_Find_Rank);
        
        
    	
    	 //Register for click events from buttons
        BookATaxiButton.setOnClickListener(this);
        FavouritesButton.setOnClickListener(this);
        HistoryButton.setOnClickListener(this);
        RankFinderButton.setOnClickListener(this);
        
        //If we have returned from the confirmation screen then generally either the history screen or the favourite screen follows
        //This code does not need to be here as now the application will proceed straight to either the history or favourites screen and then
        //Backtrack into this view.
        /*switch ((ViewTypes) Defaults.BookingModes[MainApplication.CurrentBookingMode.ordinal()][3]) 
        {
	        case eHistoryview:
	            MainApplication.CurrentBookingMode = eBookingModes.eNoBookingMode;
	            this.onClick(HistoryButton);
	            break;
	            
	        case eFavouriteview_browse:
	            MainApplication.CurrentBookingMode = eBookingModes.eNoBookingMode;
	            this.onClick(FavouritesButton);
	            break;
	            
	        default:
	            break;
        }*/
    }


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		ClickableButton selectedButton = (ClickableButton) v;
		
		if(BookATaxiButton == selectedButton)
		{
			//release 1
			if(MainApplication.CurrentBookingMode != eBookingModes.eBookNew)
			{
				MainApplication.getInstance().TempBookingForm.ResetForm();
			}
			MainApplication.CurrentBookingMode = eBookingModes.eBookNew;
		 
			Intent i = new Intent(this, BookATaxiActivity.class);
			startActivity(i);
			
			/* release 2
			 * Intent i = new Intent(this, BookATaxiActivity.class);
			startActivity(i);*/
		}
		else if (FavouritesButton == selectedButton)
		{
			Intent i = new Intent(this, FavouriteHome.class);
			startActivity(i);
		}
		else if (HistoryButton == selectedButton)
		{
			Intent i = new Intent(this, HistoryActivity.class);
			startActivity(i);
		}
		else //RankFinderButton
		{
			Intent i = new Intent(this, RankFinderActivity.class);
			startActivity(i);
		}
	}
    
    
}