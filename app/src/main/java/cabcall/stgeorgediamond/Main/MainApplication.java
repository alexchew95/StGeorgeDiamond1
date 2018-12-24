package cabcall.stgeorgediamond.Main;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;
import cabcall.stgeorgediamond.Main.Defaults.eBookingModes;
import cabcall.stgeorgediamond.Main.Components.BookingForm;
import cabcall.stgeorgediamond.Main.Components.HistoryForm;
import cabcall.stgeorgediamond.WebService.WebServiceAsset;

public class MainApplication extends Application/* implements CabcallConnectionListener*/ {
	private static MainApplication singleton;
	public WebServiceAsset webServiceAsset;
	public SharedPreferences Preferences;
	public HistoryForm TempBookingForm;
	public static eBookingModes CurrentBookingMode;
	public static BookingForm CurrentLocation;
	public static boolean PrimeConfirmationWebService;
	

	public static MainApplication getInstance()
	{
		return singleton;
	}
	
	public static double SafeDouble (String s)
	{
		try
		{
			double i = (Double.valueOf(s)).doubleValue();
			return i;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return 0.0;
		}
	}
	
	public static int SafeInt (String s)
	{
		try
		{
			int i = (Integer.valueOf(s)).intValue();
			return i;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return 0;
		}
	}
	
	@Override
	public final void onCreate()
	{	
		Log.d("app.Main.MainApplication","onCreate()");
		super.onCreate();
		
		PrimeConfirmationWebService = false;
		singleton = this;
		webServiceAsset = new WebServiceAsset();
		
		Resources r = MainApplication.getInstance().getResources();
        String tag = r.getString(cabcall.stgeorgediamond.R.string.PreferencesTag);
        tag = String.format(tag, r.getString(cabcall.stgeorgediamond.R.string.app_name));
        
    	SharedPreferences mySharedPrefs = MainApplication.getInstance().getSharedPreferences(tag, Activity.MODE_PRIVATE);
    	this.Preferences = mySharedPrefs;
    	
    	BookingForm.SetUpGlobals();
    	TempBookingForm = new HistoryForm();
    	
    	MainApplication.CurrentBookingMode = eBookingModes.eNoBookingMode;
    	
    	//Just for testing purposes
    	/*BookingForm.setUserID(r.getString(R.string.DummySpid));
    	BookingForm.setUserName("Graeme Bond");
    	BookingForm.setUserMobileNumber("0400020863");
    	BookingForm.setSpid(r.getString(R.string.DummySpid));*/
    	
	}

	/*public void CabcallConnectionComplete(CabcallConnection connection) {
		// TODO Auto-generated method stub
		
	}*/
}
