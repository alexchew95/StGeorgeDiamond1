package cabcall.stgeorgediamond.WebService;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import cabcall.stgeorgediamond.Main.*;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.content.res.AssetManager;

public class WebServiceAsset {

	public NodeList ElementNodesForLookup;
	public NodeList SoapActionNodesForLookup;
	public String StringSoapEnvelope;
	
	public WebServiceAsset()
	{
		InitAssets();
	}
	
	void InitAssets ()
	{
		InputStream SoapEnvelope;
		AssetManager manager = MainApplication.getInstance().getAssets();
		
		//Load the webservice xml
		InputStream inputstream;
		try {
			inputstream = manager.open("bookingservice.wsdl");
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			
			//Load data into a document and parse it
			Document doc = db.parse(inputstream);
			
			NodeList ElementNodes = doc.getElementsByTagName("s:sequence");
			this.ElementNodesForLookup = ElementNodes;
			
			//Now load the soap action Nodes
			this.SoapActionNodesForLookup = doc.getElementsByTagName("soap:operation");
			
			//Load the soap envelope
			SoapEnvelope = manager.open("envelope.xml");
			
			StringBuilder s = new StringBuilder();
			for(;;)
			{
				int b = SoapEnvelope.read();
				if(b == -1)
					break;
				s.append((char)b);
			}
			StringSoapEnvelope = s.toString();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
