package cabcall.stgeorgediamond.MapContent;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.view.Display;

import cabcall.stgeorgediamond.R;
import cabcall.stgeorgediamond.Main.Defaults;
import cabcall.stgeorgediamond.Main.MainApplication;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class Annot extends Overlay {
public GeoPoint annotationSpot;
public String TitleText;
public String DetailText;
public int PixelsofAnnotIconAboveSpot;
boolean ShowDisplay;
NinePatchDrawable DisplayPanel;
Activity HostingActivity;


	public Annot(GeoPoint spot, String Titletext, String Detailtext, Activity hostingActivity)
	{
		annotationSpot = spot;
		TitleText = Titletext;
		DetailText = Detailtext;
		HostingActivity = hostingActivity;
		//ShowDisplay = true;
	}
	
	public int roundUp (double Value)
	{
		int val = Math.round((float) Value);
		if(Value > val)
			val++;
		
		return val;
	}
	
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow)
	{
		Resources r = MainApplication.getInstance().getResources();
//		DisplayPanel = (NinePatchDrawable)  r.getDrawable(R.drawable.annotation_display_panel);
		
		super.draw(canvas, mapView, shadow);
		
		if(ShowDisplay)
		{
			//Calculate the best size to make the Display Panel
			int TitleTotalSpace = Defaults.DPanelPadding1 + Defaults.DPanelTitleVerticalHeight + Defaults.DPanelPadding2;
			int SingleLineDetailSpace = Defaults.DPanelDetailVerticalHeight + Defaults.DPanelPadding2;
			
			double l = DetailText.length();
			double s = Defaults.DPanelMaxCharactersPerLine;
			int NoLines = roundUp(l/s);
			int DetailSpaceRequired = (SingleLineDetailSpace *NoLines);
			
			int HorizontalTextSpaceRequired = Defaults.DPanelMaxCharactersPerLine* Defaults.DPanelDetailHorizPerChar;
			
			int TotalHeightRequired = TitleTotalSpace + DetailSpaceRequired + Defaults.DPanelPadding3;
			int TotalWidthRequired = (HorizontalTextSpaceRequired + Defaults.DPanelRightMargin + Defaults.DPanelLeftMargin);
			
			if(TotalHeightRequired < DisplayPanel.getMinimumHeight())
			{
				TotalHeightRequired = DisplayPanel.getMinimumHeight();
			}
			
			if(TotalWidthRequired < DisplayPanel.getMinimumWidth())
			{
				TotalWidthRequired = DisplayPanel.getMinimumWidth();
			}
			
			//Prepare the dimensions of the panel
			Point pixelLoc = new Point();
			Projection p = mapView.getProjection();
			p.toPixels(annotationSpot, pixelLoc);
			
			Display display = HostingActivity.getWindowManager().getDefaultDisplay();
			
			int width = display.getWidth();
			
			int TopLeftx = 0;
			int TopLefty = pixelLoc.y - TotalHeightRequired - PixelsofAnnotIconAboveSpot;
			
			//Determine whether to use the reflected panel or not
			if(pixelLoc.x + TotalWidthRequired < width)
			{
//				DisplayPanel = (NinePatchDrawable)  r.getDrawable(R.drawable.annotation_display_panel);
				int PixelsBeforePointer = (int) (Defaults.DPanelProportionofIndicator*DisplayPanel.getMinimumWidth());
				TopLeftx = pixelLoc.x - PixelsBeforePointer;
			}
			else
			{
//				DisplayPanel = (NinePatchDrawable)  r.getDrawable(R.drawable.annotation_display_panel_reflected);
				int PixelsBeforePointer = (int) (Defaults.DPanelProportionofIndicator*DisplayPanel.getMinimumWidth());
				TopLeftx = pixelLoc.x - TotalWidthRequired + PixelsBeforePointer;
			}
			Rect drawingRect = new Rect(TopLeftx, TopLefty, TotalWidthRequired + TopLeftx, TotalHeightRequired + TopLefty);
			
			DisplayPanel.setBounds(drawingRect);
			DisplayPanel.draw(canvas);
			
			//Draw text
			Paint Titlepaint = new Paint();
			Titlepaint.setTextSize(Defaults.DPanelTitleVerticalHeight - 5);
//			Titlepaint.setColor(r.getColor(R.color.annotationprimarytextcolor));
			canvas.drawText(TitleText, TopLeftx + Defaults.DPanelLeftMargin, TopLefty + Defaults.DPanelPadding1 + (Defaults.DPanelTitleVerticalHeight), Titlepaint);
			
			Paint Detailpaint = new Paint();
			Detailpaint.setTextSize(Defaults.DPanelDetailVerticalHeight - 5);
//			Detailpaint.setColor(r.getColor(R.color.annotationsecondarytextcolor));
			
			for(int i = 0; i < NoLines; i++)
			{
				int endLength = (i+1)*Defaults.DPanelMaxCharactersPerLine;
				
				if(endLength > DetailText.length())
					endLength = DetailText.length();
				
				String sub = DetailText.substring((i*Defaults.DPanelMaxCharactersPerLine), endLength);
				if(i == 0)
				{
					canvas.drawText(sub, TopLeftx + Defaults.DPanelLeftMargin, TopLefty + TitleTotalSpace + (Defaults.DPanelDetailVerticalHeight/2), Detailpaint);
				}
				else
				{
					canvas.drawText(sub, TopLeftx + Defaults.DPanelLeftMargin, TopLefty + TitleTotalSpace + (SingleLineDetailSpace*i) + (Defaults.DPanelDetailVerticalHeight/2), Detailpaint);
				}
			}
		}
	}

	@Override
	public boolean onTap (GeoPoint point, MapView mapView)
	{
		try
		{
			int MaximumPixelDistance = 20;
			Projection mapProj = mapView.getProjection();
			
			Point tappedPoint = new Point();
			mapProj.toPixels(point, tappedPoint);
			
			Point DPanelPoint = new Point();
			
			mapProj.toPixels(annotationSpot, DPanelPoint);
			
			int xdist = tappedPoint.x - DPanelPoint.x;
			int ydist = tappedPoint.y - DPanelPoint.y;
			
			//Now a bit of pythag...
			double Distance = Math.sqrt((xdist*xdist) + (ydist*ydist)); 
			
			if(Distance < MaximumPixelDistance)
			{
				
				//ShowDisplay = true;
				return true;
			}
			
			//ShowDisplay = false;
			return false;	
		}
		catch (Exception e)
		{
			return false;
		}
	}
}
