package cabcall.stgeorgediamond.Main.Components;


import android.os.Bundle;
import android.app.Dialog;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;

public class ConfirmationDialogFragment extends DialogFragment
{
	DialogInterface.OnClickListener Delegate;
	String title;
	String message;
	
	public ConfirmationDialogFragment(DialogInterface.OnClickListener delegate, String Title, String Message)
	{
		Delegate = delegate;	
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
		
		//set buttons
		alertDialogBuilder.setPositiveButton("Cancel", Delegate); 
		alertDialogBuilder.setNeutralButton("Try Again", Delegate);
		alertDialogBuilder.setNegativeButton("Go Back", Delegate);
 
		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();
 
		// show it
		return alertDialog;
	 }
	
	
	
}
