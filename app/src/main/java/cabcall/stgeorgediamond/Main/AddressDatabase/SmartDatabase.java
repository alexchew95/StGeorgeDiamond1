package cabcall.stgeorgediamond.Main.AddressDatabase;

import java.io.InputStream;
import java.util.ArrayList;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.AsyncTask;
import cabcall.stgeorgediamond.Main.MainApplication;

enum Mode
{
	primary,
	primarySecondary,
}

public class SmartDatabase extends AsyncTask<String, Void, Void> {
	int id;
	Mode mode;
	public static SmartDatabaseDelegate delegate;
	ArrayList<String> lastSearch;
	
	
	SmartDatabase(int id)
	{
		this.id = id;
	}
	
	public void SearchDatabaseForPrimary (String PrimaryString)
	{
		this.mode = Mode.primary;
		this.execute(PrimaryString);
	}
	
	public void SearchDatabaseForSecondary (String SecondaryString,String PrimaryString)
	{
		this.mode = Mode.primarySecondary;
		this.execute(SecondaryString, PrimaryString);
	}
	
	boolean CharactersEqual(byte byte1, byte byte2)
	{
		if(byte2 == 0)
		{
			return true;
		}
		
		if(byte1 > 64 && byte1 < 91)
			byte1 += 32;
		
		if(byte2 > 64 && byte2 < 91)
			byte2 += 32;
		
		if(byte1 == byte2)
		{
			return true;
		}
		
		return false;
	}
	
	public ArrayList<String> _SearchDatabaseForPrimary (String PrimaryString)
	{
		//Open Database
    	AssetManager manager = MainApplication.getInstance().getAssets();
		Resources r = MainApplication.getInstance().getResources();
		
		//Load the database
		InputStream inputstream;
		
		ArrayList<String> returningStrings = new ArrayList<String>();
		StringBuilder tempString = new StringBuilder();
		boolean FoundFirstEntry = false;
		boolean ComparisonTestRequired = true;
		boolean GoingToAddCharactersBeforeComma = false;
		int Depth = 0;
		int Length = PrimaryString.length();
		byte[] primaryStringArray;
		if(Length == 0)
		{
			primaryStringArray = new byte[1];
			primaryStringArray[0] = 0;
			Length = 1;
		}
		else
			primaryStringArray = PrimaryString.toLowerCase().getBytes();
		
		try 
		{
			inputstream = manager.open(r.getString(id));
			for(;;)
			{
				byte b = (byte)inputstream.read();
				if(b == -1)
					break;
				
				if (b != ',' && b != '\n')
				{
					if(ComparisonTestRequired)
					{
						if(CharactersEqual(b,primaryStringArray[Depth]))
						{
							tempString.append((char)b);
							
							//Check that we are not at the last character of the pSA array
							if(Depth + 1 == Length)
							{
								ComparisonTestRequired = false;
								GoingToAddCharactersBeforeComma = true;
								Depth = 0;
							}
							else
								Depth++;
						}
						else
						{
							if(FoundFirstEntry)
								break;
							
							ComparisonTestRequired = false;
							GoingToAddCharactersBeforeComma = false;
							Depth = 0;
							
							//Clear tempString buffer
							tempString.delete(0, tempString.length());
						}
					}
					else if(GoingToAddCharactersBeforeComma)
					{
						tempString.append((char)b);
					}
				}
				//For Commas
				else if(b == ',')
				{
					if(GoingToAddCharactersBeforeComma)
					{
						FoundFirstEntry = true;
						returningStrings.add(tempString.toString());
						tempString.delete(0, tempString.length());
					}
					
					ComparisonTestRequired = false;
					GoingToAddCharactersBeforeComma = false;
				}
				//New Line
				else if(b== '\n')
				{
					ComparisonTestRequired = true;
				}
			}
		}
		catch (Exception e)
		{
			
		}
		return returningStrings;
	}
	
	public ArrayList<String> _SearchDatabaseForSecondary (String SecondaryString,String PrimaryString)
	{
		//Open Database
    	AssetManager manager = MainApplication.getInstance().getAssets();
		Resources r = MainApplication.getInstance().getResources();
		
		//Load the database
		InputStream inputstream;
		
		ArrayList<String> returningStrings = new ArrayList<String>();
		StringBuilder tempString = new StringBuilder();
		
		boolean FoundFirstEntry = false;
		boolean ComparisonTestRequired = true;
		boolean GoingToAddCharactersBeforeComma = false;
		boolean FoundPrimary = false;
		boolean ExpectComma = false;
		
		int Depth = 0;
		int pLength = PrimaryString.length();
		int sLength = SecondaryString.length();
		byte[] primaryStringArray = PrimaryString.toLowerCase().getBytes();
		byte[] secondaryStringArray;
		
		if(sLength == 0)
		{
			secondaryStringArray = new byte[1];
			secondaryStringArray[0] = 0;
			sLength = 1;
		}
		else
			secondaryStringArray = SecondaryString.toLowerCase().getBytes();
		
		try 
		{
			inputstream = manager.open(r.getString(id));
			for(;;)
			{
				byte b = (byte)inputstream.read();
				if(b == -1)
					break;
				
				if (b != ',' && b != '\n')
				{
					if(FoundPrimary)
					{
							if(ComparisonTestRequired)
							{
								if(CharactersEqual(b,secondaryStringArray[Depth]))
								{
									tempString.append((char)b);
									
									//Check that we are not at the last character of the pSA array
									if(Depth + 1 == sLength)
									{
										ComparisonTestRequired = false;
										GoingToAddCharactersBeforeComma = true;
										Depth = 0;
									}
									else
										Depth++;
								}
								else
								{
									if(FoundFirstEntry)
										break;
									ComparisonTestRequired = false;
									GoingToAddCharactersBeforeComma = false;
									Depth = 0;
									
									//Clear tempString buffer
									tempString.delete(0, tempString.length());
								}
							}
							else if(GoingToAddCharactersBeforeComma)
							{
								tempString.append((char)b);
							}
					}
					else
					{
							if(ComparisonTestRequired)
							{
								if(ExpectComma)
								{
									ComparisonTestRequired = false;
									Depth = 0;
								}
								else if(CharactersEqual(b,primaryStringArray[Depth]))
								{
									//Check if we are at the last character of the pSA array
									if(Depth + 1 == pLength)
									{
										ExpectComma = true;
									}
									
									Depth++;
								}
								else
								{
									ComparisonTestRequired = false;
									Depth = 0;
								}
							}
					}
				}
				//For Commas
				else if(b == ',')
				{
					if(FoundPrimary)
					{
						if(GoingToAddCharactersBeforeComma)
						{
							FoundFirstEntry = true;
							returningStrings.add(tempString.toString());
							tempString.delete(0, tempString.length());
						}
						
						ComparisonTestRequired = true;
						GoingToAddCharactersBeforeComma = false;
					}
					else if (ExpectComma) //If we were expecting a comma, then we were obviously looking
					{						//for a the primaryString to end. In which case, we can move on to
						FoundPrimary = true;
						ComparisonTestRequired = true;
					}
					else
					{
						ComparisonTestRequired = false;
					}
					ExpectComma = false;
					Depth = 0;
				}
				//New Line
				else if(b == '\n')
				{
					if(FoundPrimary)
						break;
					else
						ComparisonTestRequired = true;
				}
			}
		}
		catch (Exception e)
		{
			
		}
		return returningStrings;
	}

	@Override
	protected Void doInBackground(String... params) {
		ArrayList<String> returnvalue;
		switch(mode)
		{
			case primary:
				returnvalue = this._SearchDatabaseForPrimary(params[0]);
			break;
			
			default:
				returnvalue = this._SearchDatabaseForSecondary(params[0], params[1]);
			break;
		}
		
		lastSearch = returnvalue;
		return null;
	}
	
	@Override
	protected void onPostExecute (Void result)
	{
		delegate.SmartDatabaseReturn(lastSearch);
	}
}
