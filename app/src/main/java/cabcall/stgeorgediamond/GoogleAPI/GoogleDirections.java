package cabcall.stgeorgediamond.GoogleAPI;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.maps.GeoPoint;

import android.content.res.Resources;
import android.os.AsyncTask;
import cabcall.stgeorgediamond.Main.MainApplication;
import cabcall.stgeorgediamond.R;

public class GoogleDirections extends AsyncTask<GeoPoint, Void, Void> {
	GoogleDirectionsListener Delegate;
	ArrayList<GeoPoint> poly;
	public interface GoogleDirectionsListener
	{
		void GoogleDirectionsReturned (ArrayList<GeoPoint> poly);
	}
	
	public GoogleDirections(GoogleDirectionsListener delegate)
	{
		super();
		Delegate = delegate;
		poly = new ArrayList<GeoPoint>();
	}
	
	private void decodePoly(String encoded) {

	    
	    int index = 0, len = encoded.length();
	    int lat = 0, lng = 0;

	    while (index < len) {
	        int b, shift = 0, result = 0;
	        do {
	            b = encoded.charAt(index++) - 63;
	            result |= (b & 0x1f) << shift;
	            shift += 5;
	        } while (b >= 0x20);
	        int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
	        lat += dlat;

	        shift = 0;
	        result = 0;
	        do {
	            b = encoded.charAt(index++) - 63;
	            result |= (b & 0x1f) << shift;
	            shift += 5;
	        } while (b >= 0x20);
	        int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
	        lng += dlng;

	        GeoPoint p = new GeoPoint((int) (((double) lat / 1E5) * 1E6),
	             (int) (((double) lng / 1E5) * 1E6));
	        poly.add(p);
	    }
	}

	@Override
	protected Void doInBackground(GeoPoint...params) {
		
		//Create the message in the form of a url request
		Resources r = MainApplication.getInstance().getResources();
		String origin = "" + (params[0].getLatitudeE6()/1E6) + "," + (params[0].getLongitudeE6()/1E6);
		String dest = "" + (params[1].getLatitudeE6()/1E6) + "," + (params[1].getLongitudeE6()/1E6);
		
		String TargetAddress = String.format(r.getString(R.string.GoogleDirectionsAddress), origin, dest);
		
		URL Target;
		try {
			Target = new URL(TargetAddress);
			HttpURLConnection targetConnection = (HttpURLConnection) Target.openConnection();
			targetConnection.setDoOutput(true);
			targetConnection.setDoInput(true);
			
			InputStream i = targetConnection.getInputStream();
			StringBuilder s = new StringBuilder();
			for(;;)
			{
				int thebyte = i.read();
				if(thebyte == -1)
					break;
				
				s.append((char)thebyte);
			}
			
			//Some Json passing 
			JSONObject root = new JSONObject(s.toString());
			JSONArray routes = root.getJSONArray("routes");
			
			JSONObject overviewpoly;
			String polydata;
			for(int j = 0; j < routes.length(); j++)
			{
				overviewpoly = routes.getJSONObject(j).getJSONObject("overview_polyline");
				polydata = overviewpoly.getString("points");
				decodePoly(polydata);
			}
			return null;
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	protected void onPostExecute (Void result)
	{
		Delegate.GoogleDirectionsReturned(poly);
	}
}
