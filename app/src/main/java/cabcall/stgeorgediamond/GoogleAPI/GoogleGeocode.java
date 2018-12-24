package cabcall.stgeorgediamond.GoogleAPI;

import java.util.List;

import com.google.android.maps.GeoPoint;

import cabcall.stgeorgediamond.R;
import cabcall.stgeorgediamond.Main.MainApplication;
import cabcall.stgeorgediamond.Main.Components.HistoryForm;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

public class GoogleGeocode extends AsyncTask<Object, Void, Void> {
	public interface GoogleGeocodeListener
	{
		public void ReverseGeocodeResponse (HistoryForm hform);
		public void GeocodeResponse (GeoPoint latlong);
		public void UnsuccessfulResponse ();
	}
	
	public enum GeocodeMode
	{
		eReverse,
		eNormal,
	};
	GeocodeMode mode;
	Address addressdata;
	GoogleGeocodeListener delegate;
	
	public GoogleGeocode (GeocodeMode e, GoogleGeocodeListener Delegate)
	{
		mode = e;
		delegate = Delegate;
	}
	
	@Override
	protected Void doInBackground(Object... params) {
		Geocoder geocodeRequest = new Geocoder(MainApplication.getInstance());
		
		try {
			List<Address> result;
			
			switch (mode)
			{
			case eReverse:
				GeoPoint pointdata = (GeoPoint) params[0];
				result = geocodeRequest.getFromLocation((double) pointdata.getLatitudeE6()/1E6, (double) pointdata.getLongitudeE6()/1E6,1);
				addressdata = result.get(0);
				break;
			default:
				HistoryForm histdata = (HistoryForm) params[0];
				
				String addresstoquery = "";
				if(!histdata.PlaceFieldUsed)
				{
					addresstoquery = histdata.PUStreetNumber1 + ", " + histdata.PUStreetName1;
				}
				else
				{
					addresstoquery = histdata.PUPlace1;
				}
				addresstoquery += ", " + histdata.PUSuburb1;
				
				result = geocodeRequest.getFromLocationName(addresstoquery, 1);
				addressdata = result.get(0);
				break;
			}
		
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			delegate.UnsuccessfulResponse();
		}
		return null;
	}
	
	@Override
	protected void onPostExecute (Void result)
	{
		try
		{
			switch (mode)
			{
			case eReverse:
				String StateAbb = MainApplication.getInstance().getResources().getString(R.string.StateAbbreviation);
				
				HistoryForm hform = new HistoryForm();
				//Deal with steet name and number
				String line1 = addressdata.getAddressLine(0);
				int index = line1.indexOf(" ");
				hform.PUStreetNumber1 = line1.substring(0,index);
				hform.PUStreetName1 = line1.substring(index+1, line1.length());
				
				//Deal with suburb
				String line2 = addressdata.getAddressLine(1);
				int index2 = line2.indexOf(StateAbb);
				hform.PUSuburb1 = line2.substring(0, index2 - 1);
				
				delegate.ReverseGeocodeResponse(hform);
				break;
			default:
				delegate.GeocodeResponse(new GeoPoint((int)(addressdata.getLatitude() * 1E6), (int) (addressdata.getLongitude() * 1E6)));
				break;
			}
		}
		catch (Exception e)
		{
			delegate.UnsuccessfulResponse();
		}
	}
}
