package cabcall.stgeorgediamond.Main;

import cabcall.stgeorgediamond.R;
import cabcall.stgeorgediamond.Main.Defaults.eBookingModes;
import cabcall.stgeorgediamond.Main.BookingProcess.BookHere;
import cabcall.stgeorgediamond.Main.BookingProcess.BookingProcess1;
import cabcall.stgeorgediamond.Main.Components.ClickableButton;
import cabcall.stgeorgediamond.Main.Favourites.FavouriteHome;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class BookATaxiActivity extends Activity implements OnClickListener{
	ClickableButton BookHereButton;
	ClickableButton BookFavouritesButton;
	ClickableButton BookNewButton;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookataxi);
        
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		BookHereButton = (ClickableButton) findViewById(R.id.BookTaxiScreen_Button_BookHere);
        BookFavouritesButton = (ClickableButton) findViewById(R.id.BookTaxiScreen_Button_Favourites);
        BookNewButton = (ClickableButton) findViewById(R.id.BookTaxiScreen_Button_NewBooking);
        
        BookHereButton.setOnClickListener(this);
        BookFavouritesButton.setOnClickListener(this);
        BookNewButton.setOnClickListener(this);
        
        
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		ClickableButton pressedView = (ClickableButton) v;
		if(pressedView == BookHereButton)
		{
			if(MainApplication.CurrentBookingMode != eBookingModes.eBookFromHere)
			{
				MainApplication.getInstance().TempBookingForm.ResetForm();
			}
			MainApplication.CurrentBookingMode = eBookingModes.eBookFromHere;
			
			Intent i = new Intent(this, BookHere.class);
			startActivity(i);
		}
		else if(pressedView == BookFavouritesButton)
		{
			MainApplication.CurrentBookingMode = eBookingModes.eBookNewFromFav;
			
			Intent i = new Intent(this, FavouriteHome.class);
			startActivity(i);
		}
		else
		{
			if(MainApplication.CurrentBookingMode != eBookingModes.eBookNew)
			{
				MainApplication.getInstance().TempBookingForm.ResetForm();
			}
			MainApplication.CurrentBookingMode = eBookingModes.eBookNew;
			
			// following code is just to speed up testing - comment out for release version
/*			MainApplication.getInstance().TempBookingForm.PUStreetName1 = "Eastern Rd";
	        MainApplication.getInstance().TempBookingForm.PUSuburb1 = "Turramurra";
	        MainApplication.getInstance().TempBookingForm.PUStreetNumber1 = "85";
	        MainApplication.getInstance().TempBookingForm.DestSuburb1 = "Wahroonga"; */
			//Load activity
			Intent i = new Intent(this, BookingProcess1.class);
			startActivity(i);
		}
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
        MainApplication.CurrentBookingMode = Defaults.eBookingModes.eNoBookingMode;
	}
}
