	package cabcall.stgeorgediamond.WebService;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import cabcall.stgeorgediamond.R;
import cabcall.stgeorgediamond.Main.MainApplication;
import cabcall.stgeorgediamond.Main.Components.BookingForm;

public class CabcallConnection extends AsyncTask<Void, Void, ConnectionStatus> {
	private Map<String, Parameters> _parameters;
	private Map<String, Object> _responseNodes;
	private ArrayList<String> _orderedParameters;
	
	private Document Message;
	public String SoapName;
	private String WebServiceAddress;
	
	private String SoapAction;
	private String Response;
	private String ignorenodesstring;
	private CabcallConnectionListener listener;
	
	public boolean isComplete;
	
	public CabcallConnection(String messageIdentifier, CabcallConnectionListener clistener)
	{
		this._orderedParameters = new ArrayList<String>();
		this._parameters = (Map<String, Parameters>) new HashMap<String, Parameters>();
		this.SoapName = messageIdentifier;
		RegisterParamsFromWebSvc(SoapName);
		
		//Get WebServiceAddress from a String Resource
		Resources r = MainApplication.getInstance().getResources();
		WebServiceAddress =  r.getString(R.string.WebServiceAddress);
		ignorenodesstring = r.getString(R.string.IgnoreNodes);
		
		String SPID = BookingForm.Spid();
		
		//Plug the authentication constants and the SPID in
		this.setobject("nUserNumber", r.getString(R.string.UserNumber));
		this.setobject("strPassword", r.getString(R.string.Password));
		if(SPID != null && this._parameters.containsKey("SPId"))
			this.setobject("SPId", SPID);
		else if(SPID != null && this._parameters.containsKey("SPid"))	// GetSPHistoricBookingList has a typo in it
			this.setobject("SPId", SPID);
		listener = clistener;
	}
	
	private void RegisterParamsFromWebSvc (String WebSvcName)
	{
		NodeList ElementNodes = MainApplication.getInstance().webServiceAsset.ElementNodesForLookup;
		
		//Find the node that corresponds to the WebSvcName that has been provided
		int desiredElementIndex = 0;
		for(int i = 0; i < ElementNodes.getLength();i++)
		{
			Node n = ElementNodes.item(i).getParentNode().getParentNode();
			NamedNodeMap attributes = n.getAttributes();
			
			Node namenode = attributes.getNamedItem("name");
			String name = namenode.getNodeValue();
			
			if(name.equalsIgnoreCase(WebSvcName))
			{
				desiredElementIndex = i;
				break;
			}
		}
		
		//Navigate to the s:schema node which is the parent of the actual parameters we want to read
		Node WebSvcRoot = ElementNodes.item(desiredElementIndex);
		
		//Take the children which are the parameters and read them
		NodeList ChildNodes = WebSvcRoot.getChildNodes();
		for(int i = 0; i < ChildNodes.getLength(); i++)
		{
			Node CurrentNode = ChildNodes.item(i);
			String CurrentNodeName = CurrentNode.getNodeName();
			
			if(CurrentNodeName.equalsIgnoreCase("s:element"))
			{
				NamedNodeMap nodeattributes = CurrentNode.getAttributes();
				String ParamName = nodeattributes.getNamedItem("name").getNodeValue();
				String ParamType = nodeattributes.getNamedItem("type").getNodeValue();
				
				ParamType = ParamType.replaceAll("s:", ""); //Remove the s: prefix
				
				//now add the node to our list
				this._parameters.put(ParamName, new Parameters(null, ParamType));
				this._orderedParameters.add(ParamName);
			}
			
		}
	}
	
	//Message Handling
	
	@Override
	protected ConnectionStatus doInBackground(Void... params) {
		try {
			//Encode the parameter objects into xml
			this.GenerateXMLForMessage();
			
			//Send the server request
			this.SendRequest();
			
			//Now decode the data
			this.HandleResponse();
			
			//Free some unneeded variables
			this.Message = null;
			this.Response = null;
			return ConnectionStatus.Success;
		} 
		catch (ConnectException e)
		{
			e.printStackTrace();
			return ConnectionStatus.PotenshNoInternet;
		}
		catch (Exception e) {
			e.printStackTrace();
			return ConnectionStatus.Fail;
		}
	}
	
	private void GenerateXMLForMessage () throws Exception
	{
		
		//try {
			InputStream input = new ByteArrayInputStream(MainApplication.getInstance().webServiceAsset.StringSoapEnvelope.getBytes());
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(input);
			
			Node base = doc.getFirstChild().getChildNodes().item(1);
			
			Node BookingServicesNode = doc.createElement("BookingServices:" + SoapName);
			NamedNodeMap nodeMap = BookingServicesNode.getAttributes();
			
			Attr xmlnsAttr = doc.createAttribute("xmlns");
			xmlnsAttr.setValue("http://ecabcall.org/");
			nodeMap.setNamedItem(xmlnsAttr);
			
			base.appendChild(BookingServicesNode);
			
			//Now add all the parameters
			for(int i = 0; i < this._orderedParameters.size(); i++)
			{
				String Tag = this._orderedParameters.get(i);
				Node newNode = doc.createElement("BookingServices:" + Tag);
				base.getChildNodes().item(1).appendChild(newNode);
				
				String Value = (String) this._parameters.get(Tag).Value;
				
				if(!(Value == null || Value.length() == 0)) //If the object is not null
				{
					newNode.appendChild(doc.createTextNode(Value));
				}
			}
			
			SoapAction = "http://ecabcall.org/" + SoapName;
			this.Message = doc;
			
		
	}
	
	private void SendRequest () throws Exception
	{
		//try {
			//Prepare the URLs
			URL Target = new URL(WebServiceAddress);
			HttpURLConnection targetConnection = (HttpURLConnection) Target.openConnection();
			targetConnection.setDoOutput(true);
			targetConnection.setDoInput(true);
			
			//Transform the data to a string
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(this.Message);
			
			StringWriter strWriter = new StringWriter();
			StreamResult stringStreamRes = new StreamResult(strWriter);
			transformer.transform(source, stringStreamRes);
			String MessageString = new String();
			//strWriter.write(MessageString);
			MessageString = strWriter.toString();
			Log.d("CabcallConnection","Outgoing Message:\n" + MessageString);
			
			//Set the Headers
			targetConnection.setRequestMethod("POST");
			//targetConnection.setRequestProperty("POST", "/ecwebservice/bookingService.asmx HTTP/1.1");
			//targetConnection.setRequestProperty("Host", Target.getHost());
			targetConnection.setRequestProperty("User-Agent", "WiCOM XML");
			targetConnection.setRequestProperty("SOAPAction", SoapAction);
			targetConnection.setRequestProperty("Content-Type", "application/soap+xml; charset=utf-8");
			targetConnection.setRequestProperty("Content-Length", "" + MessageString.length());
			
			Log.d("CabcallConnection","Header Information");
			Log.d("CabcallConnection","User-Agent:" + "WiCOM XML");
			Log.d("CabcallConnection","SOAPAction:" + SoapAction);
			Log.d("CabcallConnection","Content-Type:" + "application/soap+xml; charset=utf-8");
			Log.d("CabcallConnection","Content-Length:" + MessageString.length());
			
			//Prepare the Stream Objects
			OutputStreamWriter resultlev2 = new OutputStreamWriter(targetConnection.getOutputStream()); 
			StreamResult resultlev1 = new StreamResult(resultlev2);
			
			//Transform the data
			transformer.transform(source, resultlev1);

			//Flush all the data to the right places
			resultlev2.flush();
			
			Log.d("CabcallConnection", "Response Message:" + targetConnection.getResponseMessage());
			
			if(targetConnection.getResponseCode() != 200)
			{
				InputStream data = targetConnection.getErrorStream();
				StringBuilder newBuilder = new StringBuilder();
				for(boolean quit = false;quit == false;)
				{
					int databyte = data.read();
					if(databyte == -1)
						quit = true;
					else
						newBuilder.append((char)databyte);
				}
				Log.d("CabcallConnection","Error Message:" + newBuilder.toString());	
				Response = "";	
				throw new Exception("Cabcall Server returned error code" + targetConnection.getResponseCode());
			}
			else
			{
				//Get the data from the server
				InputStream data = targetConnection.getInputStream();
				StringBuilder newBuilder = new StringBuilder();
				for(;;)
				{
					for(boolean quit = false;quit == false;)
					{
						int databyte = data.read();
						if(databyte == -1)
							quit = true;
						else
							newBuilder.append((char)databyte);
					}
					
					if(newBuilder.toString().contains(new StringBuffer("</soap:Envelope>")))
					{
						break;
					}
				}
				Response = newBuilder.toString();
				//Log the data
				Log.d("CabcallConnection", "Recieved Message from " + WebServiceAddress + ":\n" +  newBuilder.toString());
			}
	}
	
	public void HandleResponse () throws Exception
	{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new ByteArrayInputStream(this.Response.getBytes()));
			
			//Find the element node that should be the response
			NodeList nds = doc.getChildNodes();
			int desiredlev1 = 0;
			for(int i = 0; i < nds.getLength(); i++)
			{
				if(nds.item(i).getNodeType() == Node.ELEMENT_NODE)
				{
					desiredlev1 = i;
					break;
				}
			}
			
			Node responseNode = nds.item(desiredlev1);
			this._responseNodes = new HashMap<String,Object>();
			this.AddBaseNodes(responseNode, this._responseNodes);
	}
	
	private boolean AddBaseNodes (Node nodetosearch, Map<String,Object> maptoappend)
	{
		int listenumeration = 0;
		
		if(ShouldIgnore(nodetosearch))
		{
			
		}
		else
		{
			if(nodetosearch.hasChildNodes()) 
			{
				int index = hasLegitimateTextNode(nodetosearch);
				if(index != -1) //We've come to a node that has text in it
				{
					//get node value and text, store in the root of the response data map
					String nodename = nodetosearch.getNodeName();
					String nodevalue = nodetosearch.getChildNodes().item(index).getNodeValue();
					maptoappend.put(nodename, nodevalue);
				}
				else if(nodetosearch.getNodeName() != null && nodetosearch.getNodeName().equalsIgnoreCase("newdataset"))
				{
					//Let's deal with a data set
					//search through the dataset and for every table1 we find add to another map to an arraylist
					//for each table1 search in and find the nodes that host text nodes and add them to the map
					ArrayList<Map<String,Object>> dataset = new ArrayList<Map<String,Object>>();
					NodeList children = nodetosearch.getChildNodes();
					for(int i = 0; i < children.getLength(); i++)
					{
						if(children.item(i).getNodeType() == Node.ELEMENT_NODE) //ie not a text node
						{
							Map<String, Object> newMap = new HashMap<String,Object>();
							AddBaseNodes(children.item(i), newMap);
							dataset.add(newMap);
						}
					}
					listenumeration++;
					maptoappend.put("List"+listenumeration, dataset); //First list will be called List1
				}
				else//we've found a node that doesn't host text and isn't called newdataset, therefore we need to dig deeper
				{
					NodeList children = nodetosearch.getChildNodes();
					for(int i = 0; i < children.getLength(); i++)
					{
						if(children.item(i).getNodeType() == Node.ELEMENT_NODE) //ie not a text node
						{
							AddBaseNodes(children.item(i), maptoappend);
						}
					}
				}	
			}
		}
		
		return true;
	}
	
	private int hasLegitimateTextNode (Node node)
	{
		if(node.hasChildNodes())
		{
			NodeList Children = node.getChildNodes();
			//if this node has a legitimate text node then the node will not contain any text nodes with \n or \r
			for(int i = 0; i < Children.getLength(); i++)
			{
				if(Children.item(i).getNodeType() == Node.TEXT_NODE)
				{
					String nodeText = Children.item(i).getNodeValue();
					if(nodeText == null || nodeText.length() == 0 || nodeText.contains("\n") || nodeText.contains("\r"))
					{
						//Haven't found the node yet
					}
					else
					{
						return i;// We've found it
					}
				}
			}
		}
		return -1;
	}
	
	private boolean ShouldIgnore (Node node)
	{
		//Break up the ignore nodes string into an array, seperated by a comma
		String[] IgnoreNodes;
		if(ignorenodesstring.contains(new StringBuffer(",")))//test for ,
		{
			IgnoreNodes = ignorenodesstring.split(",");
		}
		else //if there is no comma then just put the ignorenodestring straight into the array
		{
			IgnoreNodes = new String[]{ignorenodesstring};
		}
		
		//Now test if we should ignore the node
		for(int i = 0; i < IgnoreNodes.length; i++)
		{
			String val = node.getNodeName();
			if(val != null)
			{
				if(val.equals(IgnoreNodes[i]))
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	@Override
	protected void onPostExecute(ConnectionStatus result)
	{
		isComplete = true;
		Resources r = MainApplication.getInstance().getResources();
		
		if(result == ConnectionStatus.PotenshNoInternet)
		{
			Toast toast = Toast.makeText(MainApplication.getInstance().getApplicationContext(), 
					r.getString(R.string.PotenshNoInternetError), 
					Toast.LENGTH_LONG);
			toast.show();
		}
		this.listener.CabcallConnectionComplete(this);
		this.listener = null; //release reference to the class
	}
	
	//Public Methods
	
	public void setobject (String key, Object value)
	{
		_parameters.get(key).Value = value;
	}
	
	public Object getobject (String key)
	{
		return _parameters.get(key).Value;
	}
	
	public String getresponseString (String key)
	{
		try {
		return (String) _responseNodes.get(key);
		}
		catch (Exception e)
		{
			return "";
		}
	}
	
	public ArrayList<Map<String,Object>> getResponseList ()
	{
		try {
			@SuppressWarnings("unchecked")
			ArrayList<Map<String,Object>> arrayList = (ArrayList<Map<String,Object>>) _responseNodes.get("List1");
			return arrayList;
		}
		catch (Exception e)
		{
			return new ArrayList<Map<String,Object>>();
		}
		
	}

	/**
	 * @return the listener
	 */
	public CabcallConnectionListener getListener() {
		return listener;
	}

	/**
	 * @param set the listener
	 */
	public void setListener(CabcallConnectionListener listener) {
		this.listener = listener;
	}
}
