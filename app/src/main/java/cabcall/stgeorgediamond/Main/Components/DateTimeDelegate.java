package cabcall.stgeorgediamond.Main.Components;

import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog.OnTimeSetListener;

public interface DateTimeDelegate extends OnDateSetListener , OnTimeSetListener {
	void DateTimeNegativeReturned();
}
