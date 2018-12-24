package cabcall.stgeorgediamond.Main.Components;

import java.util.Map;

import org.joda.time.*;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.google.android.maps.GeoPoint;


public class HistoryForm extends BookingForm {
	public String PUDate;
	public String PUTime;//Replace seperate string values for date and time
	public String PUStatusString;
    public boolean HistoricBooking;
    public boolean BookingNotCancelled;
    public boolean NoTaxiCoords;
    public boolean NoPickupCoords;
    public GeoPoint nTaxiCoord;
    public GeoPoint nPUCoord;
    
    public boolean FutureBooking;
    
    public HistoryForm ()
    {
    	super();
    }
    
	public HistoryForm (Map<String, Object> advancedResponse) //replaces initWithAdvancedEntryResponse
    {
		super(advancedResponse);
		parseHistoryData(advancedResponse);
    }
	
	public HistoryForm (Map<String, Object> advancedResponse, boolean UsesHistoryParsing) //replaces initWithAdvancedEntryResponse
    {
		super(advancedResponse);
		if(UsesHistoryParsing)
		{
			parseHistoryData(advancedResponse);
		}
    }
	
	public GeoPoint GeoPointSafeCopy(GeoPoint geoPointToCopy)
	{
		if(geoPointToCopy == null)
			return null;
		else
		return new GeoPoint(geoPointToCopy.getLatitudeE6(), geoPointToCopy.getLongitudeE6());
	}
	
	public HistoryForm (HistoryForm objectToClone)
	{
		super(objectToClone);
		this.PUDate = StringSafeCopy(objectToClone.PUDate);
		this.PUTime = StringSafeCopy(objectToClone.PUTime);//Replace seperate string values for date and time
		this.PUStatusString = StringSafeCopy(objectToClone.PUStatusString);
		this.HistoricBooking = objectToClone.HistoricBooking;
		this.BookingNotCancelled = objectToClone.BookingNotCancelled;
		this.NoTaxiCoords = objectToClone.NoTaxiCoords;
		this.NoPickupCoords = objectToClone.NoPickupCoords;
		this.nTaxiCoord = GeoPointSafeCopy(objectToClone.nTaxiCoord);
		this.nPUCoord = GeoPointSafeCopy(objectToClone.nPUCoord); 
	    this.FutureBooking = objectToClone.FutureBooking;
	}
	
	void parseHistoryData (Map<String,Object> advancedResponse)
	{
		DateTimeFormatter formatter = ISODateTimeFormat.dateHourMinuteSecond();
		String sBookDate = (String) advancedResponse.get("BookDate");
		String sBookTime = (String) advancedResponse.get("BookTime");
		sBookDate = sBookDate.substring(0,sBookDate.length()-6);
		sBookTime = sBookTime.substring(0,sBookTime.length()-6);
		DateTime date = formatter.parseDateTime(sBookDate);
		DateTime time = formatter.parseDateTime(sBookTime);
		this.PUDate = date.toString("dd-MM-yy");
		this.PUTime = time.toString("HH-mm");
		
        // set the BookingNotCancelled flag to allow the current bookings that are cancelled to be excluded from the current bookings table view
        String cancelparam = (String) advancedResponse.get("Cancel");
        if (cancelparam.contains("0")) // Booking has not been cancelled if the Cancel field contains a string of @"0"
        {
            this.BookingNotCancelled = true;
        }
        else
        {
            this.BookingNotCancelled = false;
        }
	}
}
