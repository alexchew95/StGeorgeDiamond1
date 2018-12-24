package cabcall.stgeorgediamond.Main.AddressDatabase;

import java.util.ArrayList;

import cabcall.stgeorgediamond.R;

public class StreetDatabase extends SmartDatabase {
	
    //Tells the system if the user is currently searching
    boolean UpdateUserSearching;

    public StreetDatabase ()
    {
		super(R.string.StreetDatabase);
    }

	
	public static boolean SearchDatabaseForStreet (String StreetString)
	{
		if(StreetString == null || StreetString.length() == 0)
		{
			delegate.SmartDatabaseReturn(new ArrayList<String>());
			return false;
		}
		StreetDatabase s = new StreetDatabase();
		s.SearchDatabaseForPrimary(StreetString);
		return true;
	}
	
	public static boolean SearchDatabaseForSuburb (String SuburbString, String StreetString)
	{
		if(StreetString == null || StreetString.length() == 0 || SuburbString == null)
		{
			delegate.SmartDatabaseReturn(new ArrayList<String>());
			return false;
		}
		StreetDatabase s = new StreetDatabase();
		s.SearchDatabaseForSecondary(SuburbString,StreetString);
		return true;
	}
}
