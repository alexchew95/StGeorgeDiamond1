package cabcall.stgeorgediamond.Main.AddressDatabase;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.res.AssetManager;
import android.content.res.Resources;
import cabcall.stgeorgediamond.Main.MainApplication;

public class Database {
	ArrayList<String> AlphabeticalKeys;
    HashMap<String, ArrayList<String>> TermDatabase;
    
    public Database (int id)
    {
    	AlphabeticalKeys = new ArrayList<String>();
    	TermDatabase = new HashMap<String, ArrayList<String>>();
    	
    	//Open Database
    	AssetManager manager = MainApplication.getInstance().getAssets();
		Resources r = MainApplication.getInstance().getResources();
		
		//Load the database
		InputStream inputstream;
		
		try 
		{
			inputstream = manager.open(r.getString(id));
			
			StringBuilder primaryString = new StringBuilder();
			StringBuilder subSecondaryString = new StringBuilder();
			ArrayList<String> secondaryStrings = new ArrayList<String>();
			
			int commasFound = 0;
			for(;;)
			{
				int bytetoAnalyze = inputstream.read();
				
				if(bytetoAnalyze == ',')
				{
					commasFound++;
					
					//Found a comma, if we have found 2 commas already then this string needs to be extracted
					//from the subSecondary and added to the secondaryStrings Array
					if(commasFound > 1)
					{
						secondaryStrings.add(subSecondaryString.toString());
						subSecondaryString = new StringBuilder();
					}
				}
				else if(bytetoAnalyze == '\n')
				{
					//We've reached the end of one line, reset our flags and objects
					
					//Save
					String key = primaryString.toString();
					
					AlphabeticalKeys.add(key);
					TermDatabase.put(key, secondaryStrings);
					
					//Reset
					commasFound = 0;
					primaryString = new StringBuilder();
					secondaryStrings = new ArrayList<String>();
				}
				else
				{
					//No commas no \nS found so it's just a normal char
					if(commasFound == 0) //Add this byte to the primaryString
					{
						primaryString.append((char) bytetoAnalyze);
					}
					else
					{
						subSecondaryString.append((char) bytetoAnalyze);
					}
				}
			}
			
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
    }
    
    public ArrayList<String> SearchDatabaseForPrimary (String PrimaryString)
	{
	    PrimaryString = PrimaryString.toUpperCase();
	    ArrayList<String> mArray = new ArrayList<String>();
	    boolean FoundFirstEntry = false;
	    boolean FoundEntry = false;
	    boolean SearchException = false;
	    
	    for(String key : AlphabeticalKeys)
	    {
	        if(PrimaryString.length() > key.length())
	        {
	            FoundEntry = false;
	            SearchException = true;
	        }
	        else
	        {
	            for(int i = 0; i < PrimaryString.length(); i++)
	            {
	                String keychar = key.substring(i,i+1);
	                String strchar = PrimaryString.substring(i,i+1);
	                
	                if(keychar.equals(strchar))
	                {
	                    FoundEntry = true;
	                }
	                else
	                {
	                    if(keychar.equals(" "))
	                        SearchException = true;
	                    FoundEntry = false;
	                    
	                    break;
	                }
	            }
	        }
	        
	        if(FoundEntry)
	        {
	            FoundFirstEntry = true;
	            mArray.add(key);
	        }
	        else if(SearchException && !FoundEntry)
	        {
	            //Keep Searching
	            SearchException = false;
	        }
	        else if(FoundFirstEntry && !FoundEntry)  //We've skimmed past all we can search through
	        {
	            break;
	        }
	    }
	    
	    return mArray;
	}
	
	public ArrayList<String> SearchDatabaseForSecondary (String SecondaryString,String PrimaryString)
	{
	    ArrayList<String> SecondarysForPrimary = this.TermDatabase.get(PrimaryString);
	    
	    if(SecondaryString.equals(""))
	    {
	        return SecondarysForPrimary;
	    }
	    else
	    {
	        ArrayList<String> mArray = new ArrayList<String>();
	        boolean FoundFirstEntry = false;
	        boolean FoundEntry = false;
	        boolean SearchException = false;
	        
	        for(String key : SecondarysForPrimary)
	        {
	            if(SecondaryString.length() > key.length())
	            {
	                FoundEntry = false;
	                SearchException = true;
	            }
	            else
	            {
	                for(int i = 0; i < SecondaryString.length(); i++)
	                {
	                    String keychar = key.substring(i,i+1);
	                    String strchar = SecondaryString.substring(i,i+1);
	                    
	                    if(keychar.equals(strchar))
	                    {
	                        FoundEntry = true;
	                    }
	                    else
	                    {
	                        if(keychar.equals(" "))
	                            SearchException = true;
	                        FoundEntry = false;
	                        
	                        break;
	                    }
	                }
	            }
	            
	            if(FoundEntry)
	            {
	                FoundFirstEntry = true;
	                mArray.add(key);
	            }
	            else if(SearchException && !FoundEntry)
	            {
	                //Keep Searching
	                SearchException = false;
	            }
	            else if(FoundFirstEntry && !FoundEntry)  //We've skimmed past all we can search through
	            {
	                break;
	            }
	        }
	                
	        return mArray;
	    }
	}
}
