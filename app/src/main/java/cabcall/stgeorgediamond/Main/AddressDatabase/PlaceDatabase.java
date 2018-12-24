package cabcall.stgeorgediamond.Main.AddressDatabase;

import java.util.ArrayList;

import cabcall.stgeorgediamond.R;

public class PlaceDatabase extends SmartDatabase {

	//Tells the system if the user is currently searching
    boolean UpdateUserSearching;

    public PlaceDatabase ()
    {
		super(R.string.PlaceDatabase);
    }

	
	public static boolean SearchDatabaseForSuburb (String SuburbString)
	{
		if(SuburbString == null || SuburbString.length() == 0)
		{
			delegate.SmartDatabaseReturn(new ArrayList<String>());
			return false;
		}
		PlaceDatabase p = new PlaceDatabase();
		p.SearchDatabaseForPrimary(SuburbString);
		
		return true;
	}
	
	public static boolean SearchDatabaseForPlace (String PlaceString, String SuburbString)
	{
		if(SuburbString == null || SuburbString.length() == 0 || PlaceString == null)
		{
			delegate.SmartDatabaseReturn(new ArrayList<String>());
			return false;
		}
		PlaceDatabase p = new PlaceDatabase();
		p.SearchDatabaseForSecondary(PlaceString,SuburbString);
		
		return true;
	}
}
