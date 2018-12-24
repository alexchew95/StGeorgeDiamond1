package cabcall.stgeorgediamond.Main.BookingProcess;

import android.app.Activity;
import cabcall.stgeorgediamond.Main.Defaults;
import cabcall.stgeorgediamond.Main.Defaults.SearchDisplayType;
import cabcall.stgeorgediamond.Main.Defaults.eBools;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.*;
import android.widget.RadioGroup.OnCheckedChangeListener;
import cabcall.stgeorgediamond.R;
import cabcall.stgeorgediamond.Main.MainApplication;
import cabcall.stgeorgediamond.Main.Components.BookingForm;
import cabcall.stgeorgediamond.Main.Components.ClickableButton;
import java.util.*;

public class BookingProcess1 extends Activity implements OnClickListener, SearchViewControllerListener, OnTouchListener, OnCheckedChangeListener, AnimationListener {
	EditText SuburbField;
	EditText StreetField;
	EditText UnitStreetNumField;
	EditText PlaceField;
	EditText PickupRemarks;
	
	TextView SuburbLabel;
	TextView StreetLabel;
	TextView UnitStreetNumLabel;
	TextView PlaceLabel;
	
	ClickableButton continuebutton;
	RadioGroup SegmentedPlaceControl;
	
	String tempValue;
	SearchDisplayType searchViewReturned;
	
	boolean PlaceMode;
	
	boolean JustCreated;
	
	boolean ResultDidChange;
	int[] StartPoints = {0,0};
	int[] EndPoints = {0,0};
	float EndDistToTravel;
	float StartDistToTravel;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookingprocess1);
        searchViewReturned = SearchDisplayType.eNull;
        continuebutton = (ClickableButton) findViewById(R.id.BookingProcess1_Button_Continue);
        continuebutton.setOnClickListener(this);
        
        StreetField = (EditText) findViewById(R.id.BookingProcess1_TextField_Street);
        SuburbField = (EditText) findViewById(R.id.BookingProcess1_TextField_Suburb);
        UnitStreetNumField = (EditText) findViewById(R.id.BookingProcess1_TextField_StreetNo);
        PlaceField = (EditText) findViewById(R.id.BookingProcess1_TextField_Place);
        PickupRemarks = (EditText) findViewById(R.id.BookingProcess1_TextField_PickupInfo);
        
        SuburbLabel = (TextView) findViewById(R.id.BookingProcess1_Label_Suburb);
    	StreetLabel = (TextView) findViewById(R.id.BookingProcess1_Label_Street);
    	UnitStreetNumLabel = (TextView) findViewById(R.id.BookingProcess1_Label_StreetNo);
    	PlaceLabel = (TextView) findViewById(R.id.BookingProcess1_Label_Place);
        SegmentedPlaceControl = (RadioGroup) findViewById(R.id.BookingProcess1_RadioGroup_PlaceMode);
    	
        StreetField.setOnTouchListener(this);
        SuburbField.setOnTouchListener(this);
        SegmentedPlaceControl.setOnCheckedChangeListener(this);
        
        PlaceField.setOnTouchListener(this);
        
        //This flag causes the updatefieldsfrombookingform function to be called at the right time if the ui has just been inflated
        JustCreated = true;
	}
	
	@Override
	public void onWindowFocusChanged (boolean visible)
	{
		super.onWindowFocusChanged(visible);
		if(JustCreated)
			this.UpdateFieldsFromBookingForm();
		
		JustCreated = false;
	}
	
	@Override
	public void onStart ()
	{
		super.onStart();
		
		if(Defaults.BookingModes[MainApplication.CurrentBookingMode.ordinal()][2] == eBools.eTrue)
	    {
	        this.setTitle(R.string.BP1TitleFavourites);
	    }
	    else
	    {
	    	this.setTitle(R.string.BP1TitleNormal);
	    }
		
		switch (searchViewReturned)
		{
		case SDT_SuburbDisplayStreetContext:
			MainApplication.getInstance().TempBookingForm.PUSuburb1 = tempValue;
			break;
		
		case SDT_SuburbDisplayPlaceContext:
			MainApplication.getInstance().TempBookingForm.PUSuburb1 = tempValue;
			if(ResultDidChange)
			{
				MainApplication.getInstance().TempBookingForm.PUPlace1 = "";
			}
			break;
			
		case SDT_StreetDisplay:
			MainApplication.getInstance().TempBookingForm.PUStreetName1 = tempValue;
			if(ResultDidChange)
			{
				MainApplication.getInstance().TempBookingForm.PUSuburb1 = "";
			}
			break;
			
		case SDT_PlaceDisplay:
			MainApplication.getInstance().TempBookingForm.PUPlace1 = tempValue;
			break;
			
		default:
			break;
		}
		searchViewReturned = SearchDisplayType.eNull;
		this.ResultDidChange = false;
		
		if(!JustCreated)
			this.UpdateFieldsFromBookingForm();
	}
	
	
	public void UpdateFieldsFromBookingForm ()
	{
		this.SuburbField.setText(MainApplication.getInstance().TempBookingForm.PUSuburb1);
		this.StreetField.setText(MainApplication.getInstance().TempBookingForm.PUStreetName1);
		this.UnitStreetNumField.setText(MainApplication.getInstance().TempBookingForm.PUStreetNumber1);
		this.PlaceField.setText(MainApplication.getInstance().TempBookingForm.PUPlace1);
		this.PickupRemarks.setText(MainApplication.getInstance().TempBookingForm.PURemarks1);
		
		if(MainApplication.getInstance().TempBookingForm.PlaceFieldUsed)
		{
			//set the address/place switch to the right value
			SegmentedPlaceControl.check(R.id.BookingProcess1_Segment_PlaceMode);
			AnimatePlaceModeChange();
		}	
		else
		{
			MainApplication.getInstance().TempBookingForm.PlaceFieldUsed = false;
			SegmentedPlaceControl.check(R.id.BookingProcess1_Segment_StreetMode);
		}
	}
	
	public ArrayList<EditText> IncompleteFields ()
	{
		ArrayList<EditText> maIncompleteFields = new ArrayList<EditText>();
		if(this.SuburbField.getText().length() == 0)
		{
			maIncompleteFields.add(this.SuburbField);
		}
		
		if(this.PlaceMode)
		{
			if(this.PlaceField.getText().length() == 0)
			{
				maIncompleteFields.add(PlaceField);
			}
		}
		else 
		{
			if (this.StreetField.getText().length() == 0) 
			{
				maIncompleteFields.add(StreetField);
			}
			
			if(this.UnitStreetNumField.getText().length() == 0)
			{
				maIncompleteFields.add(UnitStreetNumField);
			}
		}
	    
		return maIncompleteFields;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v == continuebutton)
			this.ContinuePressed();
		
		
	}
	
	void ContinuePressed ()
	{
		ArrayList<EditText> aIncompleteFields = this.IncompleteFields();
		if(aIncompleteFields.size() == 0)
		{
			this.UpdateBookingFormFromFields();
			Intent i = new Intent(this, BookingProcess2.class);
			startActivity(i);
		}
		else 
		{
			for(EditText textField : aIncompleteFields)
			{
				textField.setHint("Please Complete");
			}
		}
	}
	
	void StreetFieldRecievedFocus ()
	{
		SearchViewController.StartTextboxString = StreetField.getText().toString();
		SearchViewController.searchDisplayType = SearchDisplayType.SDT_StreetDisplay;
		SearchViewController.Delegate = this;
		
		Intent i = new Intent(this, SearchViewController.class);
		startActivity(i);
	}
	
	void SuburbFieldRecievedFocus ()
	{
		SearchViewController.StartTextboxString = SuburbField.getText().toString();
		SearchViewController.Delegate = this;
		SearchViewController.Context = StreetField.getText().toString();

	    if(PlaceMode)
	    {
	    	SearchViewController.searchDisplayType = SearchDisplayType.SDT_SuburbDisplayPlaceContext;
	    	Intent i = new Intent(this, SearchViewController.class);
			startActivity(i);
	    }
	    else
	    {
	        if(this.StreetField.getText().length() == 0)
	        {
	            Toast toast = Toast.makeText(getApplicationContext(), "Please enter a valid Street before entering a Suburb", Toast.LENGTH_SHORT);
	            toast.show();
	        }
	        else
	        {
	        	SearchViewController.searchDisplayType = SearchDisplayType.SDT_SuburbDisplayStreetContext;
	        	Intent i = new Intent(this, SearchViewController.class);
	    		startActivity(i);
	        }
	    }
	}
	
	void PlaceFieldRecievedFocus ()
	{
		if(this.SuburbField.getText().length() == 0)
		{
			Toast toast = Toast.makeText(getApplicationContext(), "Please enter a valid Suburb before entering a Place", Toast.LENGTH_SHORT);
			toast.show();
		}
		else
		{
			SearchViewController.Context = SuburbField.getText().toString();
			SearchViewController.StartTextboxString = PlaceField.getText().toString();
			SearchViewController.Delegate = this;
			SearchViewController.searchDisplayType = SearchDisplayType.SDT_PlaceDisplay;
			
			Intent i = new Intent(this, SearchViewController.class);
    		startActivity(i);
		}
	}
	
	@Override
	public void onCheckedChanged(RadioGroup arg0, int arg1) {
		
		if(arg1 == R.id.BookingProcess1_Segment_StreetMode)
		{
			PlaceMode = false;
		}
		else
		{
			PlaceMode = true;
		}
		AnimatePlaceModeChange();
		
	}
	
	void AnimatePlaceModeChange()
	{
		long AnimationDuration = 1000;
		//Create the fade out animation for StreetName and StreetNo.
		AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
		fadeOut.setDuration(AnimationDuration);
		fadeOut.setInterpolator(new AccelerateDecelerateInterpolator());
		fadeOut.setFillAfter(true);
		
		//Create the translate animation for the SuburbBox
		if(PlaceMode)
		{
			EndDistToTravel = StreetField.getTop() - SuburbField.getTop();
			StartDistToTravel = 0;
		}
		else
		{
			StartDistToTravel = (EndDistToTravel);
			EndDistToTravel = 0;
		}
		
		TranslateAnimation translateTo = new TranslateAnimation(0, 0, StartDistToTravel, EndDistToTravel);
		translateTo.setDuration(AnimationDuration);
		translateTo.setInterpolator(new AccelerateDecelerateInterpolator());
		translateTo.setFillAfter(true);
		translateTo.setAnimationListener(this);
		
		//Create fade in animation for the Hidden place field
		AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
		fadeIn.setDuration(AnimationDuration);
		fadeIn.setInterpolator(new AccelerateDecelerateInterpolator());
		fadeIn.setFillAfter(true);
		
		//Apply animations
		if(PlaceMode)
		{
			UnitStreetNumField.startAnimation(fadeOut);
			UnitStreetNumLabel.startAnimation(fadeOut);
			StreetField.startAnimation(fadeOut);
			StreetLabel.startAnimation(fadeOut);
			StreetField.setVisibility(View.VISIBLE);
			StreetLabel.setVisibility(View.VISIBLE);
			PlaceField.startAnimation(fadeIn);
			PlaceLabel.startAnimation(fadeIn);
		}
		else
		{
			SuburbField.setVisibility(View.VISIBLE);
			UnitStreetNumField.startAnimation(fadeIn);
			UnitStreetNumLabel.startAnimation(fadeIn);
			StreetField.startAnimation(fadeIn);
			StreetLabel.startAnimation(fadeIn);
			PlaceField.startAnimation(fadeOut);
			PlaceLabel.startAnimation(fadeOut);
		}
		SuburbField.startAnimation(translateTo);
		SuburbLabel.startAnimation(translateTo);
	}

public void UpdateBookingFormFromFields ()
{
	BookingForm bookingForm = MainApplication.getInstance().TempBookingForm;
	
	bookingForm.PlaceFieldUsed = PlaceMode;
	
	bookingForm.PUSuburb1 = this.SuburbField.getText().toString();
	bookingForm.PUStreetName1 = this.StreetField.getText().toString();
	bookingForm.PUStreetNumber1 = this.UnitStreetNumField.getText().toString();
	bookingForm.PUPlace1 = this.PlaceField.getText().toString();
	bookingForm.PURemarks1 = this.PickupRemarks.getText().toString();
	
}

	@Override
	public void onStop()
	{
		super.onStop();
		this.UpdateBookingFormFromFields();
	}

	@Override
	public void SearchViewCtrlResult(String Result,
			SearchDisplayType searchDisplayType, boolean ResultDidChange) {
			this.searchViewReturned = searchDisplayType;
			this.tempValue = Result;
			this.ResultDidChange = ResultDidChange;
			
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		if(arg1.getActionMasked() == MotionEvent.ACTION_DOWN)
		{
			if(arg0 == StreetField)
				if(PlaceMode)
				{
					this.SuburbFieldRecievedFocus();
				}
				else
				{
					this.StreetFieldRecievedFocus();
				}
			else if(arg0 == SuburbField)
				this.SuburbFieldRecievedFocus();
			else
			{
				if(PlaceMode) //Do this because sometimes the view renders incorrectly, leaving the place box above the suburb box
				{
					this.PlaceFieldRecievedFocus();
				}
				else
				{
					this.SuburbFieldRecievedFocus();
				}
			}
		}
		
		return true;
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		// TODO Auto-generated method stub
		//The Suburb Animation has ended so set the physical values for 
		if(PlaceMode)
		{
			SuburbField.setVisibility(View.INVISIBLE);
		}
		else
		{
			PlaceField.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub
		
	}



}