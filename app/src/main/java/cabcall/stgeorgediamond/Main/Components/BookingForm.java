package cabcall.stgeorgediamond.Main.Components;

import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import android.content.SharedPreferences;
import android.content.res.Resources;
import cabcall.stgeorgediamond.Main.Defaults;
import cabcall.stgeorgediamond.Main.MainApplication;


/*//
//  BookingForm.java
//  BookingProcess
//
//  Created by Christopher Palmer on 26/01/11.
//  Copyright 2011 Wicom Technologies PTY LTD. All rights reserved.
//

*/

public class BookingForm {
	public static String MobileNumberKey;
	public static String NameKey;
	public static String UserIdKey;
	public static String SPIDKey;
	
	public boolean PlaceFieldUsed;        // flag to indicate whether the booking or favourite is a place name
	public int TaxiNumber;
	public int JobNumber;
//	public double JobNumber;
	public int TaxiDist;
	public int NumPax;                 // hard coded in the UI and set to 1 to 4 (for Premier)
	
	public String PaxName;
	public String MobileNum;
	public String PUStreetNumber1;
	public String PUStreetName1;
	public String PUDesignation;
	public String PUPlace1;
	public String PUSuburb1;
	public String PURemarks1;
	public String DestSuburb1;
	public int EstTollRoads;               // fare estimate for trip on toll roads - whole dollars No cents
	public int EstNoTollRoads;             // fare estimate for trip with No toll roads - whole dollars No cents
	
	public String FavouriteName1;       // two strings are required for the modify favourites function
	public String FavouriteName2;       // allows the old and new favourite name to be remembered in case the
                                    // create new favourite web service fails
	public String BookingPUDate;
	public String BookingPUTime;
	public String PUTimeLabel;
	public String ContactName;          // used to store that this is an iPhone booking in MTData 	
    
	public String TaxiStreetNumber;
	public String TaxiStreetName;
	public String TaxiSuburb;
    
	public int StatusIdentifier;           // used to control which icon to display as part of booking status in JobDetails.m and CabWatch.m
    
    public BookingForm()
    {
        //
        // Initialises a BookingForm object - all value are set to null except the Pax Name and Mobile Number.
        // This data is retrieved from the Settings dictionary by the methods: userName and userMobileNumber
        //
    	this.PaxName = BookingForm.userName();
        this.MobileNum = BookingForm.userMobileNumber();
    }
    
    public String StringSafeCopy(String value)
    {
    	if(value == null)
    	{
    		return null;
    	}
    	else
    	{
    		return new String(value);
    	}
    }
    
    public BookingForm(BookingForm objectToClone)
    {
		this.PlaceFieldUsed = objectToClone.PlaceFieldUsed;        // flag to indicate whether the booking or favourite is a place name
		this.TaxiNumber = objectToClone.TaxiNumber;
		this.JobNumber = objectToClone.JobNumber;
		this.TaxiDist = objectToClone.TaxiDist;
		this.NumPax = objectToClone.NumPax;                 // hard coded in the UI and set to 1 to 4 (for Premier)
		
		this.PaxName = StringSafeCopy(objectToClone.PaxName);
		this.MobileNum = StringSafeCopy(objectToClone.MobileNum);
		this.PUStreetNumber1 = StringSafeCopy(objectToClone.PUStreetNumber1);
		this.PUStreetName1 = StringSafeCopy(objectToClone.PUStreetName1);
		this.PUDesignation = StringSafeCopy(objectToClone.PUDesignation);
		this.PUPlace1 = StringSafeCopy(objectToClone.PUPlace1);
		this.PUSuburb1 = StringSafeCopy(objectToClone.PUSuburb1);
		this.PURemarks1 = StringSafeCopy(objectToClone.PURemarks1);
		this.DestSuburb1 = StringSafeCopy(objectToClone.DestSuburb1);
		this.EstTollRoads = objectToClone.EstTollRoads;              // fare estimate for trip on toll roads - whole dollars No cents
		this.EstNoTollRoads = objectToClone.EstNoTollRoads;             // fare estimate for trip with No toll roads - whole dollars No cents
		
		this.FavouriteName1 = StringSafeCopy(objectToClone.FavouriteName1);       // two strings are required for the modify favourites function
		this.FavouriteName2 = StringSafeCopy(objectToClone.FavouriteName2);       // allows the old and new favourite name to be remembered in case the
	                                    // create new favourite web service fails
		this.BookingPUDate = StringSafeCopy(objectToClone.BookingPUDate);
		this.BookingPUTime = StringSafeCopy(objectToClone.BookingPUTime);
		this.PUTimeLabel = StringSafeCopy(objectToClone.PUTimeLabel);
		this.ContactName = StringSafeCopy(objectToClone.ContactName);       // used to store that this is an iPhone booking in MTData 	
	    
		this.TaxiStreetNumber = StringSafeCopy(objectToClone.TaxiStreetNumber);
		this.TaxiStreetName = StringSafeCopy(objectToClone.TaxiStreetName);
		this.TaxiSuburb = StringSafeCopy(objectToClone.TaxiSuburb);
	    
		this.StatusIdentifier = objectToClone.StatusIdentifier;
    }
    
    public static void SetUpGlobals()
    {
    	//Retrieve preferences data for the application
    	
        Resources r = MainApplication.getInstance().getResources();
        BookingForm.NameKey = r.getString(cabcall.stgeorgediamond.R.string.PrefsNameKey);
        BookingForm.MobileNumberKey = r.getString(cabcall.stgeorgediamond.R.string.PrefsMobileNumberKey);
        BookingForm.UserIdKey = r.getString(cabcall.stgeorgediamond.R.string.PrefsUserIdKey);
        BookingForm.SPIDKey = r.getString(cabcall.stgeorgediamond.R.string.PrefsSPIDKey);
    }

    public BookingForm (Map<String, Object> advancedResponse) //replaces initWithAdvancedEntryResponse
    {
        //
        // Alternate initialisation method that puts the contents of an advancedResponse object into a Booking Form object.
        // The advancedResponse object contains the contents of either a GetFavouriteList, a GetCurentBookingList
        // or a GetHistoricBookingList web service response.
        //
        // The booking list web services utilise a sub class of BookingForm called HistoryForm and use super init to call this method
        //
        // Note the PUDesignation field in BookingForm is this init method is not used as the PUStreetNo field returned by the web service contains 
        // the street name and the designation
        
    
		this.FavouriteName1 = (String) advancedResponse.get("FavouriteName");
 //       this.FavouriteName2 = (String) advancedResponse.get("FavouriteName2");
		this.FavouriteName2 = this.FavouriteName1;
		this.PUSuburb1 = (String) advancedResponse.get("PUSuburb1");
		this.PUStreetName1 = (String) advancedResponse.get("PUAddress1");
//		this.PUStreetNumber1 = (String) advancedResponse.get("PUStreetNumber1");;
		this.PUStreetNumber1 = (String) advancedResponse.get("PUStreetNo1");
		this.PUPlace1 = (String) advancedResponse.get("PUPlace1");
        
		this.DestSuburb1 = (String) advancedResponse.get("DestSuburb1");
		this.PURemarks1 = (String) advancedResponse.get("PURemarks1");
		this.MobileNum = BookingForm.userMobileNumber();
		this.PaxName = BookingForm.userName();
		if((String)advancedResponse.get("JobNo") != null)
			this.JobNumber = MainApplication.SafeInt((String)advancedResponse.get("JobNo")); // get an expection if JobNo == null (GetFavourites)
		else
			this.JobNumber = 0;
            
        // set the PLaceFieldUsed flag appropriately
        
        if(this.PUPlace1 == null || this.PUPlace1.length() == 0)
        {
            this.PlaceFieldUsed = false;
        }
        else
        {
            this.PlaceFieldUsed = true;
        }
    	
    }

    public String BasicSummary (boolean considerPlaceField)
    {
        // Used to produce an address summary with either a steert address, number and suburb or
        // a place and suburb
        //
        // Method is used by BookHere to display the reverse geocoded address on the screen
        // Also used by Booking Process Step3, Favourite Details and Job Details
        //
        // considerPlaceField is a boolean that tells the method whether to bother considering the place field or Not - as a safety
        // mechanism when using the BookHere call
        //
        StringBuilder SummaryString = new StringBuilder();
    	
        if(considerPlaceField && PlaceFieldUsed)
        {
        	SummaryString.append((CharSequence)(this.PUPlace1 + ", " + this.PUPlace1));
        }
        else
        {
        	SummaryString.append((CharSequence)(this.PUStreetNumber1 + " " + this.PUStreetName1 + ", " + this.PUSuburb1));
        }
    	return SummaryString.toString();
    }

    public void ResetForm ()
    {
        
        //
        // resets the booking form to default values
        // used when a new booking is created (either via Book Here or NewBooking) or a new favourite is added
        // avoids old booking form information being displayed to the user when performing the above functions
        //
        
    	this.PaxName = BookingForm.userName();
        this.MobileNum = BookingForm.userMobileNumber();
    	this.PUStreetNumber1 = null;
    	this.PUStreetName1 = null;
    	this.PUSuburb1 = null;
    	this.DestSuburb1 = null;
    	this.PURemarks1 = null;
    	this.NumPax = 1;
        this.EstTollRoads = 0;
        this.EstNoTollRoads = 0;
    	this.JobNumber = 0;
    	this.FavouriteName1 = null;
        this.FavouriteName2 = null;
    	this.PUPlace1 = null;
    	this.PlaceFieldUsed = false;
        
        this.BookingPUDate = null;
        this.BookingPUTime = null;
        this.ContactName = null; 
        this.PUTimeLabel = null;
    }

    public void PurifyForm ()
    {
        //
        // Method used to reset either the street name / num Or the place fields
        // Called by Confirmation prior to using the CreateBookingAddressOrPlace msg
        // If the booking is a Place then clear the StreetName / Num fields otherwise clear the Place field
        // 
        
    	if(PlaceFieldUsed)
    	{
    		this.PUStreetName1 = null;
    		this.PUStreetNumber1 = null;
    	}
    	else {
    		this.PUPlace1 = null;
    	}
        
    }

    //
    //Mobile Number
    //
    public static String userMobileNumber ()
    {
        // 
        // Method retrieves the Mobile Number from the settings dictionary using the kMobileNumberKey
        // If No number retrieved then returns nil, otherwise the mobile number as a string is returned
        //
    	String mobileNumber = MainApplication.getInstance().Preferences.getString(BookingForm.MobileNumberKey, "");
    	if(mobileNumber.length() == 0)
    		return null;
    	else
    		return Defaults.StringRemoveDelimeters(mobileNumber);
    }

    public static int userMobileNumberNum ()
    {
        //
        // As for string version of this method described above - but returns the Mobile umber in a NSNumber format
        //
    	String mobileNumber = MainApplication.getInstance().Preferences.getString(BookingForm.MobileNumberKey, "");
    	if(mobileNumber.length() == 0)
    		return 0;
    	else
    		return MainApplication.SafeInt(Defaults.StringRemoveDelimeters(mobileNumber));
    }
    
    public static void setUserMobileNumber (String number)
    {
        //
        // Sets the UserNumber in the Settings dictionary to the passed string
        //
        
    	SharedPreferences.Editor e = MainApplication.getInstance().Preferences.edit();
    	e.putString(BookingForm.MobileNumberKey, number);
    	e.commit();
    }
    public static boolean validMobileNumber (String number)
    {
    	try
    	{
    		int n = MainApplication.SafeInt(number);
	    	if(!BookingForm.validMobileNumber(n))
	    		return false;
	    	else
	        	return true;
    	}
    	catch (Exception e)
    	{
    		return false;
    	}
    	
    }
    public static boolean validMobileNumber (int number)
    {
        //
        // Tests to see if the mobile number passed has a valid number range
        // This needs to be set for each country
        //
        
        if((number >= 400000000) && (number < 500000000)) // valid number range for Australian mobiles
        {
            return true;
        }
        return false;
    }
    
    //
    //Name
    //
    public static String userName ()
    {
        //
        // Method retrieves the User Name from the settings dictionary using the kNameKey
        // If No Name is found then returns nil, otherwise the UserName as a string is returned
        //
    	String userName = MainApplication.getInstance().Preferences.getString(BookingForm.NameKey, "");
    	if(userName.length() == 0)
    		return null;
    	else
    		return userName;
    }

    public static void setUserName (String name)
    {
        //
        // Sets the UserName in the Settings dictionary to the passed string
        //
    	SharedPreferences.Editor e = MainApplication.getInstance().Preferences.edit();
    	e.putString(BookingForm.NameKey, name); 
    	e.commit();
    }
    
    //
    //Spid
    //
    public static String Spid ()
    {
    	String TempSpid = MainApplication.getInstance().Preferences.getString(BookingForm.SPIDKey, "");
    	if(TempSpid.length() ==0)
    		return null;
    	else
    		return TempSpid;
    }
    
    public static void setSpid (String SpidToSet)
    {
    	SharedPreferences.Editor e = MainApplication.getInstance().Preferences.edit();
    	e.putString(BookingForm.SPIDKey, SpidToSet);
    	e.commit();
    }
    //
    //User Id
    //
    
    public static String userID ()
    {
    	if(!Defaults.UsesSpidPhoneSecurity)
        	return BookingForm.Spid();
        
    	String userId = MainApplication.getInstance().Preferences.getString(BookingForm.UserIdKey, "");
  
        if(userId.length() == 0)
            return null;    
        else
            return Defaults.StringRemoveDelimeters(userId);
    }

    public static void setUserID (String EnteredId)
    {
    	SharedPreferences.Editor e = MainApplication.getInstance().Preferences.edit();
    	e.putString(BookingForm.UserIdKey, EnteredId);
    	e.commit();
    }
    
    public static boolean validID (String IDField) 
    {
    	
        // check to see if Dummy SPID entered- back door for testing purposes
        if(IDField == null || IDField.length() == 0)
        	return false;
        
        IDField = Defaults.StringRemoveDelimeters(IDField); //This line removes any non-numeric characters and converts the string to an int
        String DummySpid = MainApplication.getInstance().getResources().getString(cabcall.stgeorgediamond.R.string.DummySpid);
        if(IDField.contains(DummySpid))  // ID entered matches dummy SPID value 
        {
            return true;
        }
        
        // read the SPID
        String TempSpid = BookingForm.Spid();
        
        // safety first
        if(TempSpid == null || TempSpid.length() == 0)
        	return false;
     
        // TempSpid and IDField are safe
        // Compare with the ID entered by the user

        if (IDField.contains(TempSpid)) 
        {
            return true;    // user has entered a valid ID
        }
        else 
        {
            return false;
        }
    }
    
    public String isoBookingTime()
    {
    	DateTimeFormatter Timeformatter = DateTimeFormat.forPattern("HH:mm");
    	DateTime time = Timeformatter.parseDateTime(this.BookingPUTime);
    	DateTimeFormatter isoFormatter = ISODateTimeFormat.dateTime();
    	return isoFormatter.print(time);
    }
    
    public String isoBookingDate()
    {
    	DateTimeFormatter Dateformatter = DateTimeFormat.forPattern("dd-MMM-yyyy");
    	DateTime date = Dateformatter.parseDateTime(this.BookingPUDate);
    	DateTimeFormatter isoFormatter = ISODateTimeFormat.dateTime();
    	return isoFormatter.print(date);
    }
    
}
