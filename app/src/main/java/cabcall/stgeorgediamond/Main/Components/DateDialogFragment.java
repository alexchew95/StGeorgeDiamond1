package cabcall.stgeorgediamond.Main.Components;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;



public class DateDialogFragment extends DialogFragment{
	DateTimeDelegate Delegate;
	int year;
	int month;
	int day;
	
	public DateDialogFragment(DateTimeDelegate delegate, int nyear, int nmonth, int nday)
	{
		Delegate = delegate;
		year = nyear;
		month = nmonth;
		day = nday;
	}
	
	@Override
	 public Dialog onCreateDialog(Bundle savedInstanceState) {
		DatePickerDialog d = new DatePickerDialog(getActivity(), Delegate, year, month, day);
		return d;
	 }

	@Override
	public void onDismiss(DialogInterface dialog)
	{
		super.onDismiss(dialog);
		//Delegate.DateTimeNegativeReturned();
	}
	 
}
