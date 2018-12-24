package cabcall.stgeorgediamond.Main.History;


import android.os.Bundle;


import android.app.Dialog;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.util.Log;

/*
 * 4 possible alert boxes can be displayed depending on the options chosen
 * 
 * Box 1: Title = Confirm; Message = Are you sure you want to cancel this booking?; Buttons: "Yes"; "No"
 * 
 * If user selects "yes" in Box 1 -> Box 2: Title = Cancelling Booking...; Message = Cancelling Booking; No buttons (displayed while the CancelBooking web service is in progress)
 * 
 * If Cancel Booking web service returns "success" -> Box 3: Title = Cancelling Booking...; Message = Booking Cancelled; Button = OK
 * 
 * If Cancel Booking web service returns "failed" -> Box 4: Title = Cancelling Booking...; Message = Cancel Failed; Buttons: "Close"; "Retry" (Retry takes us back to Box 2)
 * 
 */
public class JobDetailsDialogFragment extends DialogFragment
{
	public enum eDialogType {eCancelRequest, eCancelInProgress, eCancelComplete, eCancelFailed};
	DialogInterface.OnClickListener delegate;
	public eDialogType dialogType; 
	String title;
	String message;
	
	public JobDetailsDialogFragment(DialogInterface.OnClickListener Delegate, String Title, String Message, eDialogType DType)
	{
		dialogType = DType;
		delegate = Delegate;	
		title = Title;
		message = Message;
	}
	
	
	public Dialog onCreateDialog(Bundle savedInstanceState) 
	{
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				getActivity());
 
		// set title
		alertDialogBuilder.setTitle(title);
 
		// set dialog message
		alertDialogBuilder.setMessage(message);
		alertDialogBuilder.setCancelable(false);
		
		if(dialogType == eDialogType.eCancelRequest)		// initial dialog box when "Cancel" button is pressed
		{
			//set buttons
			alertDialogBuilder.setPositiveButton("Yes", delegate); 
			alertDialogBuilder.setNegativeButton("No", delegate);
	 
		}
		else if (dialogType == eDialogType.eCancelInProgress)
		{
			// no buttons - just a message "Cancelling Booking"
		}
		
		else if (dialogType == eDialogType.eCancelComplete)	// cancelweb service has completed succesfully
		{
			//set button - just one
			alertDialogBuilder.setPositiveButton("OK", delegate); 
		}
		else if (dialogType == eDialogType.eCancelFailed)	// cancelweb service has failed
		{
			//set buttons
			alertDialogBuilder.setPositiveButton("Close", delegate); 
			alertDialogBuilder.setNegativeButton("Retry", delegate);
		}
		else
		{
			// we have a problem
			Log.d("JobDetailsDialogFragment","Unknown dialogType");
		}
		
	 
		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();
 
		// show it
		return alertDialog;
	 }
	
	
	
}
