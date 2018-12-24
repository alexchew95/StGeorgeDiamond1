package cabcall.stgeorgediamond.Main.Components;


import android.widget.Button;
import android.widget.ProgressBar;

public class customlistitem1 {
	public String field1;
	public String field2;
	public boolean UsesDisclaimer;
	public boolean ShowUpdating;
	
	public Button DisclaimerButton;
	public ProgressBar ActivityIndicator;
	
	public customlistitem1(String f1, String f2, boolean usesDisclaimer, boolean showUpdating)
	{
		field1 = f1;
		field2 = f2;
		UsesDisclaimer = usesDisclaimer;
		ShowUpdating = showUpdating;
	}
}
