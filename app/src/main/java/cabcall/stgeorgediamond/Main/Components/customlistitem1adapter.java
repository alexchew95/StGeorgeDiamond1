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

 public class customlistitem1adapter extends ArrayAdapter<customlistitem1> {
        public customlistitem1adapter(Context context, int textViewResourceId,ArrayList<customlistitem1> items) {
        	super(context, textViewResourceId, items);
            this.items = items;

	}

		private ArrayList<customlistitem1> items;
        private viewHolder viewHold;

        private class viewHolder {
            TextView field1;
            TextView field2; 
            //Button DisclaimerButton;
            //ProgressBar progressBar;
        }
        
        @Override
        public View getView(int pos, View convertView, ViewGroup parent) {
            View v = convertView;
            customlistitem1 item = items.get(pos);
            
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)MainApplication.getInstance().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.customlistitem1, null);
                viewHold = new viewHolder();
                viewHold.field1 = (TextView)v.findViewById(R.id.customlistitem1_field1);
                viewHold.field2 = (TextView)v.findViewById(R.id.customlistitem1_field2);
                //viewHold.DisclaimerButton = (Button)v.findViewById(R.id.customlistitem1_DisclaimerButton);
                //viewHold.progressBar = (ProgressBar) v.findViewById(R.id.customlistitem1_ActivityIndicator);
                //v.setEnabled(false);
                v.setBackgroundResource(R.drawable.customlistitemstyle);
                v.setTag(viewHold);
            } 
            else 
            	viewHold = (viewHolder)v.getTag(); 

            

            if (item != null) {
                viewHold.field1.setText(item.field1);
                viewHold.field2.setText(item.field2);
                /*if(item.UsesDisclaimer)
                {
                	
                	if(item.ShowUpdating)
                	{
                		viewHold.progressBar.setVisibility(View.VISIBLE);
                	}
                	else
                	{
                		viewHold.DisclaimerButton.setVisibility(View.VISIBLE);
                	}
                	
                	item.ActivityIndicator = viewHold.progressBar;
                	item.DisclaimerButton = viewHold.DisclaimerButton;
                }*/
                
            }

            return v;
        }
    }
