package cabcall.stgeorgediamond.Main.Components;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;




public class TimeDialogFragment extends DialogFragment {
	DateTimeDelegate Delegate;
	int mHour;
	int mMinute;
	
	public TimeDialogFragment(DateTimeDelegate delegate, int hour, int minute)
	{
		Delegate = delegate;
		mHour = hour;
		mMinute = minute;
	}
	
	@Override
	 public Dialog onCreateDialog(Bundle savedInstanceState) {
		TimePickerDialog t = new TimePickerDialog(getActivity(), Delegate, mHour, mMinute, false);
		return t;
	 }
	
	@Override
	public void onDismiss(DialogInterface dialog)
	{
		super.onDismiss(dialog);
		//Delegate.DateTimeNegativeReturned();
	}
}
