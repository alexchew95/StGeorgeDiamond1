package cabcall.stgeorgediamond.Main.Components;

import java.util.ArrayList;

import cabcall.stgeorgediamond.R;
import cabcall.stgeorgediamond.Main.MainApplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

 public class favouritelistadapter extends ArrayAdapter<HistoryForm> 
 	{
        public favouritelistadapter(Context context, int textViewResourceId, ArrayList<HistoryForm> items) 
        {
        	super(context, textViewResourceId, items);
            this.items = items;
        }

		private ArrayList<HistoryForm> items;
        private viewHolder viewHold;

        private class viewHolder 
        {
            TextView fav_name;
            TextView pu_street; 
            TextView pu_suburb; 
            TextView dest_suburb; 
        }
        
        @Override
        public View getView(int pos, View convertView, ViewGroup parent) 
        {
            View v = convertView;
            HistoryForm item = items.get(pos);
            
            if (v == null) 
            {
                LayoutInflater vi = (LayoutInflater) MainApplication.getInstance().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.favouritelistitem, null);
                viewHold = new viewHolder();
                viewHold.fav_name = (TextView) v.findViewById(R.id.favouritelistitem_fav_name);
                viewHold.pu_street = (TextView) v.findViewById(R.id.favouritelistitem_pu_street); 
                viewHold.pu_suburb = (TextView) v.findViewById(R.id.favouritelistitem_pu_suburb); 
                viewHold.dest_suburb = (TextView) v.findViewById(R.id.favouritelistitem_dest_suburb);
                v.setBackgroundResource(R.drawable.customlistitemstyle);
                v.setTag(viewHold);
            } 
            else 
            {
            	viewHold = (viewHolder)v.getTag(); 
            }
            
            if (item != null) 
            {
                viewHold.fav_name.setText(item.FavouriteName1);

                if(item.PlaceFieldUsed)
                {
                    viewHold.pu_street.setText("From: " + item.PUPlace1);
                	
                }
                else
                {
                	viewHold.pu_street.setText("From: " + item.PUStreetNumber1 + " " + item.PUStreetName1);
                }
                
                viewHold.pu_suburb.setText(item.PUSuburb1); 
                
                if(item.DestSuburb1 == null || item.DestSuburb1.length() == 0)
                {
                	viewHold.dest_suburb.setVisibility(View.GONE);
                }
                else
                {
                	viewHold.dest_suburb.setText("To: " + item.DestSuburb1);
                }
            }

            return v;
        }
    }