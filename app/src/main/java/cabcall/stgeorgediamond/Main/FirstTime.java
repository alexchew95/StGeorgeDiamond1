package cabcall.stgeorgediamond.Main;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import cabcall.stgeorgediamond.R;
import cabcall.stgeorgediamond.Main.Components.BookingForm;
import cabcall.stgeorgediamond.Main.Components.ClickableButton;
import cabcall.stgeorgediamond.WebService.CabcallConnection;
import cabcall.stgeorgediamond.WebService.CabcallConnectionListener;

public class FirstTime extends Activity implements OnClickListener, CabcallConnectionListener, TextWatcher {
	EditText UserName;
	EditText UserMobileNumber;
	EditText UserID;
	ClickableButton FinishButton;
	ClickableButton GetIdButton;
	float defaultTextboxSize;
	boolean TextBoxResized;
	
    /** Called when the activity is first created. */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firsttime);  
    }
    
    @Override
    public void onStart()
    {
    	super.onStart();
    	//Set some event handlers...
    	FinishButton = (ClickableButton) findViewById(R.id.FirstTime_Button_Finish);
    	FinishButton.setOnClickListener(this);
    	GetIdButton = (ClickableButton) findViewById(R.id.FirstTime_Button_GetId);
    	GetIdButton.setOnClickListener(this);
    	
    	UserName = (EditText) findViewById(R.id.FirstTime_TextField_Name);
    	UserMobileNumber = (EditText) findViewById(R.id.FirstTime_TextField_MobileNumber);
    	UserID = (EditText) findViewById(R.id.FirstTime_TextField_IDNumber);
    	
    	//Dig out the current values for all settings
    	String Name = BookingForm.userName();
    	String Mob = BookingForm.userMobileNumber();
    	String Id = BookingForm.userID();
    	
    	if(Name == null)
    		Name = "";
    	if(Mob == null)
    		Mob = "";
    	if(Id == null)
    		Id = "";
    	
    	UserName.setText(Name);
    	UserMobileNumber.setText(Mob);
    	
    	
    	if(Defaults.UsesSpidPhoneSecurity)
    	{
    		UserID.setText(Id);
    		UserMobileNumber.addTextChangedListener(this);
    		RelativeLayout.LayoutParams p = (android.widget.RelativeLayout.LayoutParams) UserMobileNumber.getLayoutParams();
        	this.defaultTextboxSize = p.width;
    	}
    	else
    	{
    		UserID.setVisibility(View.INVISIBLE);
    		TextView textLabel = (TextView) findViewById(R.id.FirstTime_Label_IDNumber);
    		textLabel.setVisibility(View.INVISIBLE);
    	}
    }
    
    public boolean CommitChanges ()
    {
    	Resources r = getResources();
    	Context context = getApplicationContext();	
    	int duration = Toast.LENGTH_SHORT;

    	if(UserName.getText() == null || UserName.getText().length() == 0)
    	{
    		Toast toast = Toast.makeText(context, r.getString(R.string.FirstTimeNoName), duration);
    		toast.show();
    		return false;
    	}
    	
    	if(!BookingForm.validMobileNumber(UserMobileNumber.getText().toString()))
    	{
    		Toast toast = Toast.makeText(context, r.getString(R.string.FirstTimeNoMob), duration);
    		toast.show();
    		return false;
    	}
    	
    	if(Defaults.UsesSpidPhoneSecurity) // we don't need to check this if we are not using the extra security function
    	{
	    	if(!BookingForm.validID(UserID.getText().toString()))
	    	{
	    		Toast toast = Toast.makeText(context, r.getString(R.string.FirstTimeNoID), duration);
	    		toast.show();
	    		return false;
	    	}
    	}
    	
    	//If we get here, actually store the values
    	if(Defaults.UsesSpidPhoneSecurity)
    		BookingForm.setUserID(UserID.getText().toString());
    	
    	BookingForm.setUserName(UserName.getText().toString());
    	BookingForm.setUserMobileNumber(UserMobileNumber.getText().toString());
    	
    	return true;
    }

    @Override 
    public void onBackPressed()
    {
    	//Ignore any attempts to go backwards
    	return;
    }
    
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v == FinishButton)
		{
			if(CommitChanges())
			{
				if(!Defaults.UsesSpidPhoneSecurity)
				{
					if(BookingForm.validMobileNumber(MainApplication.SafeInt(UserMobileNumber.getText().toString())))
					{
						CabcallConnection getID = new CabcallConnection("RequestSPIDPhone",this);
						
						String mobnumber = Defaults.StringRemoveDelimeters(UserMobileNumber.getText().toString());
						getID.setobject("sPhoneNumber", mobnumber);
						
						getID.execute();
					}
				}
				this.finish();
			}
		}
		else if(v == GetIdButton)
		{
			if(BookingForm.validMobileNumber(MainApplication.SafeInt(UserMobileNumber.getText().toString())))
			{
				CabcallConnection getID = new CabcallConnection("RequestSPIDPhone",this);
				
				String mobnumber = Defaults.StringRemoveDelimeters(UserMobileNumber.getText().toString());
				getID.setobject("sPhoneNumber", mobnumber);
				
				getID.execute();
			}
		}
	}

	@Override
	public void CabcallConnectionComplete(CabcallConnection connection) {
		//Store SPID
		BookingForm.setSpid(connection.getresponseString("SPId"));
	}

	@Override
	public void afterTextChanged(Editable s) {
		//float ScaleFactorx = 0f;
		float buttonsizex = GetIdButton.getMeasuredWidth();
		float textboxsizex = UserMobileNumber.getMeasuredWidth();
		RelativeLayout.LayoutParams p = (android.widget.RelativeLayout.LayoutParams) UserMobileNumber.getLayoutParams();
		
		if(BookingForm.validMobileNumber(UserMobileNumber.getText().toString()))
		{
			//ScaleFactorx = (textboxsizex-buttonsizex)/textboxsizex;
			p.width = (int) (textboxsizex-buttonsizex);
			UserMobileNumber.setLayoutParams(p);
			//UserMobileNumber.invalidate();
			TextBoxResized = true;
			
			GetIdButton.setVisibility(View.VISIBLE);
		}
		else if(TextBoxResized)
		{
			//ScaleFactorx = defaultTextboxSize/textboxsizex;
			p.width = (int) defaultTextboxSize;
			UserMobileNumber.setLayoutParams(p);
			//UserMobileNumber.invalidate();
			TextBoxResized = false;
			
			GetIdButton.setVisibility(View.INVISIBLE);
			UserMobileNumber.setVisibility(View.VISIBLE);
		}
		
		
		//Animation code
		/*AnimationSet theAnimation = new AnimationSet(true);
		ScaleAnimation s = new ScaleAnimation(1,ScaleFactorx,1,1);
		s.setDuration(500);
		s.setFillAfter(true);
		theAnimation.addAnimation(s);
		theAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
		UserMobileNumber.startAnimation(theAnimation);*/
		
		
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		
	}
 
}