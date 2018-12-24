package cabcall.stgeorgediamond.Main.BookingProcess;

import cabcall.stgeorgediamond.Main.Defaults.SearchDisplayType;

public interface SearchViewControllerListener
{
	public void SearchViewCtrlResult (String Result, SearchDisplayType searchDisplayType, boolean ResultDidChange);
}
