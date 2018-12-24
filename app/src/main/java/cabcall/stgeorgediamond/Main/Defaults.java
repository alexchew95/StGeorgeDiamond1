package cabcall.stgeorgediamond.Main;

 

public class 	Defaults {

	public enum  SearchDisplayType{
	    SDT_SuburbDisplayPlaceContext,
		SDT_SuburbDisplayStreetContext,
		SDT_StreetDisplay,
		SDT_PlaceDisplay,
		eNull,
	};

	 public enum MessageTypes {
		eCBAOP,		// 0 - Create Booking from Address Or Place 
		eUB,		// 1 - Update Booking
	    eCnlB,      // 2 - Cancel Booking
		eCF,		// 3 - Create Favourite
	    eDF,        // 4 - Delete Favourite
	    eUF,        // 5 - Update Favourite
	    eNull,
	};

	 public enum ViewTypes {
		eHistoryview,
		eFavouriteview_edit,
		eFavouriteview_browse,
		eCurrentlocview,
		eBookingProcess_Step1,
		eNull,
	};

	 public enum ActionButton{
		eBook,
		eUpdateBooking,
		eSaveFavourite,
		eNull,
	};


	public enum eBookingModes{
		eNoBookingMode,
		eBookNew,					// 1
		eBookFromHere,
		eHistoryUpdateCurrent,
		eCreateBookingFromHistory,
		eCreateFavFromHistory,
		eBookNewFromFav,			// 6
		eModifyFav,
		eNewFav,
		eLastMode,					// 9
	    eAppStart,
	};

	 public enum eHistoryModes{
		eHistoryCurrent,
		eHistoryPast,
		eNull,
	} ;
		
	 public enum eCurrentModes {
		eCurrentStatus,
		eCurrentCancel,
		eNull,
	} ;
	
	public enum eBools
	{
		eTrue,
		eFalse,
	};
	
	public enum eSwitchContTabIndex
	{
		eCabWatch,
		eJobDetails,
	}
	
	//Book Here
	public static long MaxTimeForMapClick = 100; //The time to wait before a pin is dropped where the user has touched
	public static int MaxMovementAllowedForMapClick = 50;
	public static int MaxUpdatesBeforeMapResponds = 15;
	
	public static boolean UsesSpidPhoneSecurity = false;
	public static Object[][] BookingModes  = 

			/* Modes								Landing Page	Web Svc						Fav Name Field	Return Page								Page 3 Action Button*/

			/* 0 - 0BookingMode */					{{-1,			MessageTypes.eNull,         eBools.eFalse,				ViewTypes.eNull,                        ActionButton.eNull},

			/* 1 - Book from new */					{1,             MessageTypes.eCBAOP,		eBools.eFalse,				ViewTypes.eHistoryview,               	ActionButton.eBook},

			/* 2 - Book from here */				{1,			    MessageTypes.eCBAOP,		eBools.eFalse,				ViewTypes.eHistoryview,					ActionButton.eBook},

			/* 3 - History - Update Current */		{1,				MessageTypes.eUB,			eBools.eFalse,				ViewTypes.eHistoryview,					ActionButton.eUpdateBooking},

			/* 4 - History - Create Booking */		{3,				MessageTypes.eCBAOP,		eBools.eFalse,				ViewTypes.eHistoryview,					ActionButton.eBook}, // - Must go via page 1 & 2 first

			/* 5 - History - Create Favourite */	{3,				MessageTypes.eCF,			eBools.eTrue,				ViewTypes.eFavouriteview_browse,		ActionButton.eSaveFavourite},

			/* 6 - Favourite - Book New */			{2,				MessageTypes.eCBAOP,		eBools.eFalse,				ViewTypes.eHistoryview,					ActionButton.eBook},

			/* 7 - Favourite - Modify Fav */		{1,				MessageTypes.eUF,			eBools.eTrue,				ViewTypes.eFavouriteview_browse,		ActionButton.eSaveFavourite},

			/* 8 - Favourite - New Fav */			{1,				MessageTypes.eCF,			eBools.eTrue,				ViewTypes.eFavouriteview_browse,		ActionButton.eSaveFavourite}};
	
	public static int iCabSequenceNumber = 0;
	public static int iCabStatusIconNumber = 1;
	
	public enum iCabSequenceNumbers{
		iFutureBooking,
		iDispatching,					// 1
		iAccepted,
		iPickedUp,
		iCompleted,
		iCancelled,
		iNoJob,			// 6
		iRecall,        //7
	    iProblem,       //8
	    eNull;
	};

	public enum iCabStatusIconNumbers{
	    iIconLookingForCab,
	    iIconOnWay,
	    iIconPickedUp,
	    iIconCompleted,
	    iIconCancelled,
	    iIconProblem,
	    eNull;
	};

	//CabWatch
	public static long kCabWatchWebSvcTimeInterval = 31000;
	public static long kRefreshWait = 3000;
	
	public static double CityCentreLat = -33.873651;
	public static double CityCentreLon = 151.206890;
	public static int CityCentreZoomLevel = 12;
	public static int DefaultZoomLevel = 19;
	
	public static Object[][] iCabStatusSequences  =
	/*iCabAppNumber Icon Number SeqNumber Description iCabApp Description*/
	{{iCabSequenceNumbers.eNull, iCabStatusIconNumbers.iIconLookingForCab},   /*0 NULL NULL\Ignore*/
	{iCabSequenceNumbers.iProblem, iCabStatusIconNumbers.iIconProblem},   /*1 Stop anything from happening to job There is a problem with your booking, please call Premier on 13 10 17*/
	{iCabSequenceNumbers.iProblem, iCabStatusIconNumbers.iIconProblem},   /*2 Job Cancelled Booking has been cancelled*/
	{iCabSequenceNumbers.iDispatching, iCabStatusIconNumbers.iIconLookingForCab},   /*3 Job is being dispatched Looking for a cab*/
	{iCabSequenceNumbers.iAccepted, iCabStatusIconNumbers.iIconOnWay},   /*4 Job is assigned to car Looking for a cab*/
	{iCabSequenceNumbers.iDispatching, iCabStatusIconNumbers.iIconLookingForCab},   /*5 Vehicle Acknowledge Reception of Data Looking for a cab*/
	{iCabSequenceNumbers.iAccepted, iCabStatusIconNumbers.iIconOnWay},   /*6 Job Accepted Taxi nnnn is x.xx km away*/
	{iCabSequenceNumbers.iPickedUp, iCabStatusIconNumbers.iIconPickedUp},   /*7 Meter has been turned on You have been picked up*/
	{iCabSequenceNumbers.iCompleted, iCabStatusIconNumbers.iIconCompleted},   /*8 Meter has been turned off Booking completed*/
	{iCabSequenceNumbers.iFutureBooking, iCabStatusIconNumbers.iIconLookingForCab},   /*9 Job is active Taxi is scheduled for dd-mm-yy at hh mm*/
	{iCabSequenceNumbers.iNoJob, iCabStatusIconNumbers.iIconProblem},   /*10 No Job The driver was unable to find you. Please call Premier on 13 10 17*/
	{iCabSequenceNumbers.iDispatching, iCabStatusIconNumbers.iIconLookingForCab},   /*11 Driver or vehicle rejected job Looking for a cab*/
	{iCabSequenceNumbers.iRecall, iCabStatusIconNumbers.iIconProblem},   /*12 Driver declined the job after after accepting it The driver was unable to pick you up. We are looking for a new cab for you.*/
	{iCabSequenceNumbers.iDispatching, iCabStatusIconNumbers.iIconLookingForCab},   /*13 Job is being redispatched Looking for a cab*/
	{iCabSequenceNumbers.iProblem, iCabStatusIconNumbers.iIconProblem},   /*14 Job is discarded by system There is a problem with your booking, please call Premier on 13 10 17*/
	{iCabSequenceNumbers.iProblem, iCabStatusIconNumbers.iIconProblem},   /*15 Job is exported to another system There is a problem with your booking, please call Premier on 13 10 17*/
	{-1, 0},   /*16 Pending Price Ignore*/
	{iCabSequenceNumbers.iDispatching, 0},   /*17 Job charges require authorisation Ignore*/
	{iCabSequenceNumbers.iProblem, iCabStatusIconNumbers.iIconProblem},   /*18 No car available after a long time There is a problem with your booking, please call Premier on 13 10 17*/
	{iCabSequenceNumbers.iProblem, iCabStatusIconNumbers.iIconProblem},   /*19 Covered By an Alternate company There is a problem with your booking, please call Premier on 13 10 17*/
	{iCabSequenceNumbers.iProblem, iCabStatusIconNumbers.iIconProblem},   /*20 Replaced with a new booking by a Bo There is a problem with your booking, please call Premier on 13 10 17*/
	{iCabSequenceNumbers.iProblem, iCabStatusIconNumbers.iIconProblem},   /*21 External Assigned There is a problem with your booking, please call Premier on 13 10 17*/
	{iCabSequenceNumbers.iProblem, iCabStatusIconNumbers.iIconProblem},   /*22 Inload Cancel There is a problem with your booking, please call Premier on 13 10 17*/
	{iCabSequenceNumbers.iProblem, iCabStatusIconNumbers.iIconProblem}};   /*23 External NoJob There is a problem with your booking, please call Premier on 13 10 17*/

	public static int DPanelMaxCharactersPerLine = 18;
	
	//Annotation Display
	
	//Vertical Parameters
	public static int DPanelPadding1 = 0;
	public static int DPanelTitleVerticalHeight = 25;
	public static int DPanelPadding2 = 0;
	public static int DPanelDetailVerticalHeight = 25;
	public static int DPanelPadding3 = 15;
	public static int DPanelPixelsInBetweenDetailCharacter = 3;
	
	//Horizontal Parameters
	public static int DPanelDetailHorizPerChar = 15;
	public static int DPanelLeftMargin = 20;
	public static int DPanelRightMargin = 20;
	public static double DPanelProportionofIndicator = 0.262;
	
	public static String StringRemoveDelimeters (String stringToChange)
	{
		StringBuilder FinalString = new StringBuilder();
	    for (int i = 0; i < stringToChange.length(); i++)
	    {
	        char Char = stringToChange.charAt(i);
	        if((Char >= 48 && Char <= 57))
	        {
	            //It's a letter
	            FinalString.append(Char);
	        }
	    }
	    
	    return  FinalString.toString();
	}
}
