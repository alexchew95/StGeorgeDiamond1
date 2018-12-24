package cabcall.stgeorgediamond.Main.BookingProcess;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import cabcall.stgeorgediamond.R;
import cabcall.stgeorgediamond.Main.Components.ClickableButton;

public class Disclaimer extends Activity implements OnClickListener {
	ClickableButton finishbutton;
	TextView estimatelabel;
	public static String EstimateString;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.disclaimer);
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		finishbutton = (ClickableButton) findViewById(R.id.Disclaimer_Button_Finish);
		finishbutton.setOnClickListener(this);
		estimatelabel = (TextView) findViewById(R.id.Disclaimer_Label_Estimate);
		
		estimatelabel.setText(EstimateString);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		this.finish();
	}
}