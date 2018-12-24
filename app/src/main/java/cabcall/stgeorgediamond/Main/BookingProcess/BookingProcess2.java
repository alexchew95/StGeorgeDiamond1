package cabcall.stgeorgediamond.Main.BookingProcess;

import java.util.ArrayList;
import java.util.Calendar;

import org.joda.time.DateTime;

import cabcall.stgeorgediamond.R;
import cabcall.stgeorgediamond.Main.Defaults;
import cabcall.stgeorgediamond.Main.Defaults.SearchDisplayType;
import cabcall.stgeorgediamond.Main.Defaults.eBookingModes;
import cabcall.stgeorgediamond.Main.Defaults.eBools;
import cabcall.stgeorgediamond.Main.MainApplication;
import cabcall.stgeorgediamond.Main.Components.BookingForm;
import cabcall.stgeorgediamond.Main.Components.ClickableButton;
import cabcall.stgeorgediamond.Main.Components.DateDialogFragment;
import cabcall.stgeorgediamond.Main.Components.DateTimeDelegate;
import cabcall.stgeorgediamond.Main.Components.TimeDialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

public class BookingProcess2 extends FragmentActivity implements OnClickListener, 
RadioGroup.OnCheckedChangeListener, OnTouchListener, SearchViewControllerListener, OnItemSelectedListener, DateTimeDelegate {

	EditText DestinationField;
	EditText FavouriteNameField;

	TextView PUTimeLabel;
	TextView PUTimeDetailsLabel;
	TextView FavLabel;
	
	ClickableButton ContinueButton;
	
	Spinner NumPaxSpinner;
	
	//segmented button - Now - Later
	
	RadioGroup PUTimeRadio;
	RadioButton PUTimeButton;
	
	private boolean validPUTime = true;
	private int mHour;
    private int mMinute;

    private int year;
	private int month;
	private int day;
	
	SearchDisplayType searchViewReturned;
	String tempValue;

	boolean FirstTouch;
 
	@Override
    public void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookingprocess2);
        searchViewReturned = SearchDisplayType.eNull;
	}
	
	
	
	@Override
	public void onStart()
	{
		super.onStart();
		
		// event handlers
		
		ContinueButton = (ClickableButton) findViewById(R.id.BookingProcess2_Button_Continue);
		ContinueButton.setOnClickListener(this);
		
		DestinationField = (EditText) findViewById(R.id.BookingProcess2_TextField_DestSuburb);
		FavouriteNameField = (EditText) findViewById(R.id.BookingProcess2_TextField_FavouriteName);
		
		PUTimeLabel = (TextView) findViewById(R.id.BookingProcess2_TextField_PUTimeLabel);
		PUTimeDetailsLabel = (TextView) findViewById(R.id.BookingProcess2_TextField_PUTimeDetailsLabel);
		FavLabel = (TextView) findViewById(R.id.BookingProcess2_Label_FavouriteName);
		
	    NumPaxSpinner = (Spinner) findViewById(R.id.BookingProcess2_Spinner_NoPassengers);
	    PUTimeRadio = (RadioGroup) findViewById(R.id.BookingProcess2_RadioGroup_PUTime);
		
		if(Defaults.BookingModes[MainApplication.CurrentBookingMode.ordinal()][2] == eBools.eTrue)  // Favourite
		{
			// unhide FavouriteName text box and FavouriteName label
			
			FavouriteNameField.setVisibility(View.VISIBLE);
			FavLabel.setVisibility(View.VISIBLE);
			
			// hide PUTime control and label as well as PUTimeDetails label
			
			PUTimeLabel.setVisibility(View.INVISIBLE);
			PUTimeDetailsLabel.setVisibility(View.INVISIBLE);
			PUTimeRadio.setVisibility(View.INVISIBLE);
			
			this.setTitle(R.string.BP2TitleFavourites);
			
			// possible line of code to set view mode for destination text field see iPhone code
		}
		else
		{
			this.setTitle(R.string.BP2TitleNormal);
			MainApplication.CurrentBookingMode = eBookingModes.eBookNew;
			
			FavouriteNameField.setVisibility(View.INVISIBLE);
			FavLabel.setVisibility(View.INVISIBLE);
		}
		
		//If the view has returned from the searchviewcontroller this code will put the value in the destinationfield
		if(searchViewReturned != SearchDisplayType.eNull)
		{
			MainApplication.getInstance().TempBookingForm.DestSuburb1 = tempValue;
			
		}
		searchViewReturned = SearchDisplayType.eNull;
		  
		this.UpdateFieldsFromBookingForm();		// Dig out the current values for all settings
		
		// set listeners
		NumPaxSpinner.setOnItemSelectedListener(this);
		DestinationField.setOnTouchListener(this);	
		PUTimeRadio.setOnCheckedChangeListener(this);
	}
		
	//
	// Utilities
	//
	
	public void UpdateFieldsFromBookingForm()
	{
		BookingForm bookingForm = MainApplication.getInstance().TempBookingForm;
		String Dest = bookingForm.DestSuburb1;
		if(Dest == null)
			Dest = "";
		DestinationField.setText(Dest);
		
		String FavName = bookingForm.FavouriteName1;
		if(FavName == null)
			FavName = "";
		FavouriteNameField.setText(FavName);
		
		NumPaxSpinner.setSelection(bookingForm.NumPax-1);
		
		// display the PUTime label if we have one
		
		if ((bookingForm.BookingPUDate == null) || (bookingForm.BookingPUDate.length() == 0))	//check the logic here
		{
			PUTimeDetailsLabel.setText("");
			// hide the PUTimeLabel
			
		}
		else
		{
			PUTimeDetailsLabel.setText(bookingForm.PUTimeLabel);
			PUTimeRadio.check(R.id.BookingProcess2_Segment_Later);
			// unhide the PUTimeLabel
			
		}
		
	}
	
	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		if(FirstTouch)
		{
			this.DestinationFieldReceivedFocus();
			FirstTouch = false;
		}
		else
			FirstTouch = true;
		return true;
	}
	
	
	public void DestinationFieldReceivedFocus()
	{
		SearchViewController.StartTextboxString = DestinationField.getText().toString();
		SearchViewController.Delegate = this;
		SearchViewController.searchDisplayType = SearchDisplayType.SDT_SuburbDisplayPlaceContext;
		
		Intent i = new Intent(this, SearchViewController.class);
		startActivity(i);
	}
	
	@Override
	public void SearchViewCtrlResult(String Result,
			SearchDisplayType searchDisplayType, boolean ResultDidChange) {
		this.searchViewReturned = searchDisplayType;
		this.tempValue = Result;
		
	}
	
	//Num pax field
	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		BookingForm bookingForm = MainApplication.getInstance().TempBookingForm;
		bookingForm.NumPax = arg2 + 1;
		
	}



	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}

	//Later button and DateTime picker
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) 
	{
		  if(checkedId == R.id.BookingProcess2_Segment_Later)	// Later
		  {
			  // Get the current date and time
	        	
			  final Calendar c = Calendar.getInstance();
			  mHour = c.get(Calendar.HOUR_OF_DAY);
			  mMinute = c.get(Calendar.MINUTE);
			  year = c.get(Calendar.YEAR);
			  month = c.get(Calendar.MONTH);
			  day = c.get(Calendar.DAY_OF_MONTH);
			  
			  // launch the time picker (data picker is launched when time picker closes)
			  //FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			  TimeDialogFragment TimeDialog = new TimeDialogFragment(this, mHour, mMinute); 
			  TimeDialog.show(getSupportFragmentManager(), "TimeDialog");
		  }
		  else		// Button is set to Now
		  {
			  PUTimeDetailsLabel.setText("");
			  
			  // need to reset these as user may have selected a later time and then selected now
			  
			  MainApplication.getInstance().TempBookingForm.BookingPUDate = null;
			  MainApplication.getInstance().TempBookingForm.BookingPUTime = null;
			  
			  validPUTime = true;	// for maIncompleteFields method 
		  }
	 }

	// the callback received when the user "sets" the time in the dialog
	
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) 
    {
    	
    	// Save time in class variables
    	
    	mHour = hourOfDay;
    	mMinute = minute;
    	
     
    	// call the date picker
    	FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
	    DateDialogFragment DateDialog = new DateDialogFragment(this, year, month, day); 
		DateDialog.show(ft, "DateDialog");
    	
    }
    
    // when date dialog box is closed, method below will be called.
	public void onDateSet(DatePicker view, int selectedYear,int selectedMonth, int selectedDay) 
	{
		year = selectedYear;
		month = selectedMonth + 1;
		day = selectedDay;

		// get current time - the code from here on uses Joda time
		DateTime currentTime = new DateTime();
		DateTime sevenDaysTime = currentTime.plusDays(7);

		// create a DateTime object from the time and date selected by the pickers
		DateTime pickedDateTime = new DateTime(year, month, day, mHour, mMinute);
		DateTime pickedDate = new DateTime(year,month,day,0,0);
		DateTime pickedTime = new DateTime(1,1,1,mHour, mMinute);
		
		
		// compare currentTime to pickedTime - can't be before the current time and can't be more than 7 days into the future
		
		if(pickedDateTime.isBeforeNow()) //Is the current picked date time before now
		{
			//	display a message to user to reenter the time
    		PUTimeDetailsLabel.setText("Pick Up cannot be in the past. Please enter a new time");
    		validPUTime = false;	// for maIncompleteFields method
    		
    		// re-display of time picker is handled by user by pressing Now or Later buttons
    		
		}
		else if (pickedDateTime.isAfter(sevenDaysTime.toInstant()))
		{
			// display a msg to user to re-enter a date and time that is less than 7 days away from now
			
    		PUTimeDetailsLabel.setText("Pick Up cannot be more than 7 days time. Please enter a new date and time");
    		validPUTime = false;	// for maIncompleteFields method

		}
		else		// time is within range
		{
    		// set selected date into bookingForm in dd-MMM-yyyy format and time in hh:mm format
    		
    		MainApplication.getInstance().TempBookingForm.BookingPUDate = pickedDate.toString("dd-MMM-yyyy");
    		MainApplication.getInstance().TempBookingForm.BookingPUTime = pickedTime.toString("HH:mm");
    		
    		// format label for screen and store in bookingForm
    		
    		MainApplication.getInstance().TempBookingForm.PUTimeLabel = pickedDateTime.toString("EEE dd-MM-yy h:mm a");
    		PUTimeDetailsLabel.setText(MainApplication.getInstance().TempBookingForm.PUTimeLabel);
    		validPUTime = true;	// for maIncompleteFields method
		}

	}
	
	@Override
	public void DateTimeNegativeReturned() {
		if((MainApplication.getInstance().TempBookingForm.BookingPUDate == null) || (MainApplication.getInstance().TempBookingForm.BookingPUDate.length() == 0))
		{
			PUTimeRadio.check(R.id.BookingProcess2_Segment_Now);
		}
	}
		
	//Exiting Functionality
	@Override
	public void onClick(View v) 
	{
		// Continue button clicked
		
		ArrayList<View> aIncompleteFields = IncompleteFields();
		
		if(aIncompleteFields.size() == 0)	// all fields completed OK
		{
			this.UpdateBookingFormFromFields();
			Intent i = new Intent(this, BookingProcess3.class);
			startActivity(i);
		}
		else
		{
			for(View view : aIncompleteFields)
			{
				if(view.getClass() == EditText.class)
				{
					EditText textField = (EditText) view;
					textField.setHint("Please Complete");
				}
			}
		}		
	}
	
	public void UpdateBookingFormFromFields()
	{
		BookingForm bookingForm = MainApplication.getInstance().TempBookingForm;
		bookingForm.DestSuburb1 = this.DestinationField.getText().toString();
		bookingForm.FavouriteName1 = this.FavouriteNameField.getText().toString();
		
	}
	
	public ArrayList<View> IncompleteFields()
	{
		ArrayList<View> maIncompleteFields = new ArrayList<View>();
		
		if(Defaults.BookingModes[MainApplication.CurrentBookingMode.ordinal()][2] == eBools.eTrue)  // Favourite
		{
			if(this.FavouriteNameField.getText().length() == 0)
				maIncompleteFields.add(this.FavouriteNameField);
		}
		
		/*if(this.NumPaxField.getText().length() == 0)
		{
			maIncompleteFields.add(this.NumPaxField);
		}*/
		
		if(Defaults.BookingModes[MainApplication.CurrentBookingMode.ordinal()][2] == eBools.eFalse)	// Destination is optional for favourites
		{
			if(this.DestinationField.getText().length() == 0)
			{
				maIncompleteFields.add(this.DestinationField);
			}
		}
		
		// need to check that time is correct
		if(!validPUTime)
		{
			maIncompleteFields.add(this.PUTimeDetailsLabel);	// might need to change the UI to get this to work.
		}
		return maIncompleteFields;
	}
	
	
 
	   	/*private static String pad(int c) 
	    {
	        if (c >= 10)
	            return String.valueOf(c);
	        else
	            return "0" + String.valueOf(c);
	    }*/
}

		
