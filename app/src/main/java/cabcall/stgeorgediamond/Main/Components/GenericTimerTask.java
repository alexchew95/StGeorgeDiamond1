package cabcall.stgeorgediamond.Main.Components;

import java.util.TimerTask;

public class GenericTimerTask extends TimerTask
{
	public interface TimerListener
	{
		void TimerTick ();
		void runOnUiThread(Runnable action);
	}
	
	TimerListener Listener;
	public GenericTimerTask (TimerListener listener)
	{
		Listener = listener;
	}
	public void run() {
		Listener.runOnUiThread(new Runnable()
		{
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Listener.TimerTick();
			}
		});
	}
	
	
}