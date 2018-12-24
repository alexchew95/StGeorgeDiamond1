package cabcall.stgeorgediamond.Main.BookingProcess;

import java.util.ArrayList;

import cabcall.stgeorgediamond.R;
import cabcall.stgeorgediamond.Main.Defaults.SearchDisplayType;
import cabcall.stgeorgediamond.Main.AddressDatabase.PlaceDatabase;
import cabcall.stgeorgediamond.Main.AddressDatabase.SmartDatabase;
import cabcall.stgeorgediamond.Main.AddressDatabase.SmartDatabaseDelegate;
import cabcall.stgeorgediamond.Main.AddressDatabase.StreetDatabase;
import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class SearchViewController extends Activity implements TextWatcher, OnItemClickListener, SmartDatabaseDelegate {
	public static SearchDisplayType searchDisplayType;
	public static String StartTextboxString;
	
	public static SearchViewControllerListener Delegate;
	
	//Interface References
	ListView TableViewRef;
	EditText searchBarRef;

	//Data Source
	ArrayList<String> DataByRow;
	
	//Suburb
	public static String Context;

	
	//UIActivityIndicatorView* LoadingView;
	
	//NSNumber* CurrentOperation;
    
	boolean DeathDay;
	boolean isOperating;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchviewcontroller);
	}
	
	public void onStart()
	{
		super.onStart();
		this.searchBarRef = (EditText) findViewById(R.id.SVC_TextField_SearchBarRef);
		this.TableViewRef = (ListView) findViewById(R.id.SVC_ListView_TableViewRef);
		
		switch (searchDisplayType) {
		case SDT_SuburbDisplayStreetContext:
			this.setTitle(R.string.SVC_SuburbList);
			this.searchBarRef.setHint(R.string.SVC_SuburbSearch);
			break;
        case SDT_SuburbDisplayPlaceContext:
        	this.setTitle(R.string.SVC_SuburbList);
        	this.searchBarRef.setHint(R.string.SVC_SuburbSearch);
            break;
		case SDT_StreetDisplay:
			this.setTitle(R.string.SVC_StreetList);
			this.searchBarRef.setHint(R.string.SVC_StreetSearch);
			break;
		default:
			this.setTitle(R.string.SVC_PlaceList);
			this.searchBarRef.setHint(R.string.SVC_PlaceSearch);
			break;
		}
		
		this.searchBarRef.setText(StartTextboxString);
		this.searchBarRef.addTextChangedListener(this);
		//If this is in place mode this line will simulate the text being changed so that there are some values already in the tableview
		if(SearchViewController.searchDisplayType == SearchDisplayType.SDT_PlaceDisplay || SearchViewController.searchDisplayType == SearchDisplayType.SDT_SuburbDisplayStreetContext)
		{
			if(StartTextboxString != null || StartTextboxString.length() != 0)
			{
				this.SetDataSourceUsingSearchString(StartTextboxString);
			}
			else
			{
				this.SetDataSourceUsingSearchString("");
			}
		}
		
		this.TableViewRef.setOnItemClickListener(this);
		
		SmartDatabase.delegate = this;
	}
	
	public void SetDataSourceUsingSearchString (String searchString)
	{
		
		switch (SearchViewController.searchDisplayType) {
        case SDT_SuburbDisplayStreetContext:
            StreetDatabase.SearchDatabaseForSuburb(searchString, SearchViewController.Context);
            break;
            
		case SDT_SuburbDisplayPlaceContext:
			PlaceDatabase.SearchDatabaseForSuburb(searchString);
			break;
			
		case SDT_StreetDisplay:
			StreetDatabase.SearchDatabaseForStreet(searchString);
			break;
			
		default:
			PlaceDatabase.SearchDatabaseForPlace(searchString, SearchViewController.Context);
			break;
		}
		
		
	}
	
	

	@Override
	public void afterTextChanged(Editable arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		this.SetDataSourceUsingSearchString(arg0.toString());
	}

	public void onBackPressed()
	{
		ExitViewWithResult(SearchViewController.StartTextboxString);
		return;
	}
	
	public void onStop()
	{
		super.onStop();
	}
	
	void ExitViewWithResult (String Result)
	{
		DeathDay = true;
		boolean ResultDidChange = false;
		
		if(SearchViewController.Delegate != null)
		{
			if(!SearchViewController.StartTextboxString.equals(Result))
				ResultDidChange = true;
			
			SearchViewController.Delegate.SearchViewCtrlResult(Result,SearchViewController.searchDisplayType,ResultDidChange);
		}
		this.finish();
	}

	

	@Override
	public void SmartDatabaseReturn(ArrayList<String> data) {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1, data);
		this.TableViewRef.setAdapter(adapter);
		
		this.DataByRow = data;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		ExitViewWithResult(DataByRow.get(arg2));
		
	}
}


/*



- (IBAction) CancelPressed : (id) sender
{
	[this ExitViewWithResult: this.StartTextboxString];
}


*/
