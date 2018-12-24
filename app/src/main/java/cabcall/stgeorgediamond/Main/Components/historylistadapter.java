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

 public class historylistadapter extends ArrayAdapter<HistoryForm> {
        public historylistadapter(Context context, int textViewResourceId,ArrayList<HistoryForm> items) {
        	super(context, textViewResourceId, items);
            this.items = items;

	}

		private ArrayList<HistoryForm> items;
        private viewHolder viewHold;

        private class viewHolder {
            TextView date;
            TextView time; 
            TextView pu_street; 
            TextView pu_suburb; 
            TextView dest_suburb; 
            
        }
        
        @Override
        public View getView(int pos, View convertView, ViewGroup parent) {
            View v = convertView;
            HistoryForm item = items.get(pos);
            
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)MainApplication.getInstance().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.historylistitem, null);
                viewHold = new viewHolder();
                viewHold.date = (TextView)v.findViewById(R.id.historylistitem_date);
                viewHold.time = (TextView)v.findViewById(R.id.historylistitem_time);
                viewHold.pu_street = (TextView)v.findViewById(R.id.historylistitem_pu_street); 
                viewHold.pu_suburb = (TextView)v.findViewById(R.id.historylistitem_pu_suburb); 
                viewHold.dest_suburb = (TextView)v.findViewById(R.id.historylistitem_dest_suburb);
                v.setBackgroundResource(R.drawable.customlistitemstyle);
                v.setTag(viewHold);
            } 
            else 
            	viewHold = (viewHolder)v.getTag(); 

            

            if (item != null) {
                viewHold.date.setText(item.PUDate);
                viewHold.time.setText(item.PUTime);
                
                if(item.PlaceFieldUsed)
                	viewHold.pu_street.setText(item.PUPlace1);
                else
                	viewHold.pu_street.setText(item.PUStreetNumber1 + " " + item.PUStreetName1);
                
                viewHold.pu_suburb.setText(item.PUSuburb1); 
                viewHold.dest_suburb.setText(item.DestSuburb1);
            }

            return v;
        }
    }