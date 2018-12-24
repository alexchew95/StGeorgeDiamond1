package cabcall.stgeorgediamond.Main.RankFinder;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import cabcall.stgeorgediamond.R;
import cabcall.stgeorgediamond.GoogleAPI.GoogleDirections;
import cabcall.stgeorgediamond.GoogleAPI.GoogleDirections.GoogleDirectionsListener;
import cabcall.stgeorgediamond.Main.MainApplication;
import cabcall.stgeorgediamond.MapContent.CentralLocationManager;
import cabcall.stgeorgediamond.MapContent.CentralLocationManager.GenericActivityListener;
import cabcall.stgeorgediamond.MapContent.CurrentLocAnnot;
import cabcall.stgeorgediamond.MapContent.GenericLocationOverlay;
import cabcall.stgeorgediamond.MapContent.LineAnnotation;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;


// need to create a key value pair with a key for each rank. the following elements need to be stored
// [0] RankName - String
// [1] RankHour - String
// [2] RankLatitude - double
// [3] RankLongtitude - double
// [4] RankStreetAddress - String

public class RankFinderActivity extends MapActivity implements LocationListener, GenericActivityListener, GoogleDirectionsListener {

	boolean RanksLoaded;
	
	ClosestRank closestRank;
	CurrentLocAnnot CurrentAnnotation;
	TextView RankDetailsDisplayText;
	MapView MyMap;
	ProgressBar ActivityIndicator;
	GenericLocationOverlay CurrentLocOverlay;

	LineAnnotation DirectionsOverlay;
	
	ArrayList<RankItem> rankArray;
	GeoPoint CurrentLocation;
	
	@Override
	protected void onCreate (Bundle bundle)
	{
		super.onCreate(bundle);
		setContentView(R.layout.rank_finder);
		//Get views from xml file
		MyMap = (MapView) findViewById(R.id.RankFinder_MapView);
		RankDetailsDisplayText = (TextView) findViewById(R.id.RankFinder_Label_DetailsText);
		ActivityIndicator = (ProgressBar) findViewById(R.id.RankFinder_ActivityIndicator);
		
		RankDetailsDisplayText.setText("Searching for nearest rank...");
		rankArray = new ArrayList<RankItem>();
		closestRank = new ClosestRank();
		
		//Must be last because it triggers the onLocationChanged routine to be called
		CurrentLocOverlay = new GenericLocationOverlay(this, MyMap, this);
		CurrentLocOverlay.setActivityHandlesRegion(true);
	}
	
	@Override 
	protected void onStart()
	{
		super.onStart();
		
		//Add Current Location Overlay to map
		MyMap.getOverlays().add(CurrentLocOverlay);
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		//Get the current location on the screen
		CurrentLocOverlay.enableMyLocation();
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		CurrentLocOverlay.disableMyLocation();
	}
		
	@Override
	public void onLocationChanged(Location location) {
		ActivityIndicator.setVisibility(View.VISIBLE);
		GeoPoint currentLocation = new GeoPoint((int)(location.getLatitude()*1E6), (int)(location.getLongitude()*1E6));
		this.CurrentLocation = currentLocation;
		
		//Organize the Ranks
		if(!RanksLoaded)
		{
			LoadRanks();
			UnpackRankArray();
		}
		GetRankClosestToCoordinates(CurrentLocation);
		
		RanksLoaded = true;
		
		GeoPoint MidPoint = new GeoPoint((CurrentLocation.getLatitudeE6() + closestRank.coordinates.getLatitudeE6())/2,
				(CurrentLocation.getLongitudeE6() + closestRank.coordinates.getLongitudeE6())/2);
		
		int ZoomLevel = CentralLocationManager.ZoomLevelForRequiredPoints(currentLocation, closestRank.coordinates, this);
		
		MyMap.getController().animateTo(MidPoint);
		MyMap.getController().setZoom(ZoomLevel);
		
		//Get Directions Overlay
		GoogleDirections directionsMsg = new GoogleDirections(this);
		directionsMsg.execute(CurrentLocation, closestRank.coordinates);
	}

	//
	// Unpack Rank Dictionary - load each rank in the dictionary as an annotation on the map
	//
	
	public void UnpackRankArray() {
		RankAnnot rAnnot = null;
		List<Overlay> overlays = MyMap.getOverlays();
		for (RankItem item : rankArray)
		{
			rAnnot = new RankAnnot(item,this);
			overlays.add(rAnnot);
		}
	}
	
	//
	// Rank Closest to Coordinates - return the closest rank to current location 
	//
	
	public void GetRankClosestToCoordinates (GeoPoint location) {
		closestRank.distance = -1;
		String RankName = "";
		
		for (RankItem item : rankArray)
		{
			GeoPoint rankloc = item.RankCoordinates;
			float[] results = new float[3];
			Location.distanceBetween(location.getLatitudeE6()/1E6, location.getLongitudeE6()/1E6, 
					rankloc.getLatitudeE6()/1E6, rankloc.getLongitudeE6()/1E6, results);
			
			if(results[0] < closestRank.distance || closestRank.distance == -1)	// found a rank closer so far or first rank in the dictionary
			{
				closestRank.distance = results[0];
				closestRank.coordinates = rankloc;
				RankName = item.RankName;
			}
		}

		String displayText;
		displayText = String.format("You are %2.2f km away from the %s Rank", closestRank.distance/1000, RankName);
		RankDetailsDisplayText.setText(displayText);
	}
	
	
	
	//
	// Load Ranks - open the RankFinder file and store each rank details in the RankDictionary hash map
	//

	public void LoadRanks() {
		
		//Open Database
    	AssetManager manager = MainApplication.getInstance().getAssets();
		Resources r = MainApplication.getInstance().getResources();
		
		//Load the database
		InputStream inputstream;
		
		ArrayList<String> recordStrings = new ArrayList<String>();
		StringBuilder tempString = new StringBuilder();
		
		try 
		{
			inputstream = manager.open(r.getString(R.string.RankDatabase));
			for(;;)
			{
				byte b = (byte)inputstream.read();
				if(b == -1)	// end of file (hopefully)
					break;
				
				// for actual data
				if (b != ',' && b != '\n')
					tempString.append((char)b);		
			
				//For Commas
				else if(b == ',')
				{
					recordStrings.add(tempString.toString());  
					tempString.delete(0, tempString.length());
				}

				//New Line - means rank record has been read
				else if(b == '\n')
				{
					double lat = MainApplication.SafeDouble(recordStrings.get(2));
					double lon = MainApplication.SafeDouble(recordStrings.get(3));
					// store the rank info
					RankItem item = new RankItem();
					item.RankName = recordStrings.get(0);
					item.RankHours = recordStrings.get(1);
					item.RankCoordinates = new GeoPoint((int) (lat*1E6), (int) (lon*1E6));
					item.RankAddress = recordStrings.get(4);
					
					rankArray.add(item);
					recordStrings.clear();
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		
	}
	
	@Override
	public void GoogleDirectionsReturned(ArrayList<GeoPoint> poly) {
		if(DirectionsOverlay == null)
		{
			DirectionsOverlay = new LineAnnotation(poly);
			MyMap.getOverlays().add(0,DirectionsOverlay);
		}
		else
		{
			DirectionsOverlay.PolyLine = poly;
		}
		ActivityIndicator.setVisibility(View.INVISIBLE);
	}
	
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
}
		



