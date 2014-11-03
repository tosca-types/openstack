package org.opentosca.types;

import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openstack4j.api.OSClient;
import org.openstack4j.api.exceptions.ClientResponseException;


public class OpenStack4jAPI  implements ICloudProviderAPI
{
	@Override
	public String InstallVM(String credentials, String endpointsAPI,
			String imageId, String typeId, String keypair) throws IOException, ClientResponseException, ParseException, Exception
	{
		JSONObject creds = null;
		JSONObject endspoints = null;
		String retParams = "";
		
						
		creds = (JSONObject)new JSONParser().parse(credentials);	
		endspoints = (JSONObject)new JSONParser().parse(endpointsAPI);
		
		OSClient os = OSUtilities.Authorize(creds, endspoints);	
		java.util.Date today = new java.util.Date();
		String serverName = "os_vm_"+String.valueOf(today.hashCode());
		retParams = OSUtilities.CreateServer(os,serverName,imageId,typeId,keypair);
		System.out.println("retParams"+retParams);
			
	/*	catch (ParseException e) 
		{			
			throw new IOException("JSON parser error, " + e.getMessage(), e);
		}
		catch (ClientResponseException e) 
		{
			throw new ClientResponseException("Error in creating Server",413);
		}*/
		return retParams;	
	}

	@Override
	public void StopVM(String credentials, String endpointsAPI,
			String serverId) throws IOException, ParseException
	{
		JSONObject creds = null;
		JSONObject endspoints = null;
		String retParams = "";
		try
		{
			creds = (JSONObject)new JSONParser().parse(credentials);	
			endspoints = (JSONObject)new JSONParser().parse(endpointsAPI);
			OSClient os = OSUtilities.Authorize(creds, endspoints);	
			OSUtilities.StopServer(os,serverId);
		}		
		/*catch (ParseException e)
		{
			throw new IOException("JSON parser error, " + e.getMessage(), e);
		}*/	
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

	@Override
	public void StartVM(String credentials, String endpointsAPI,
			String serverId) throws IOException, ParseException 
	{
		JSONObject creds = null;
		JSONObject endspoints = null;
		String retParams = "";
		
		try 
		{					
			creds = (JSONObject)new JSONParser().parse(credentials);	
			endspoints = (JSONObject)new JSONParser().parse(endpointsAPI);
			OSClient os = OSUtilities.Authorize(creds, endspoints);	
		
			OSUtilities.StartServer(os,serverId);
		} 	
		catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
	}

	@Override
	public void TerminateVM(JSONObject credentials, JSONObject endpointsAPI,
			String serverId)
	{

		OSClient os = OSUtilities.Authorize(credentials, endpointsAPI);
		try 
		{
			//OSUtilities.StartServer(os,serverId);
			OSUtilities.TerminateServer(os,serverId);
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
		
	}

	@Override
	public String GetImage(String credentials, String endpointsAPI,
			String imgName) throws IOException, ParseException
	{	
		JSONObject creds = null;
		JSONObject endspoints = null;
		String imgId = "";		
							
			creds = (JSONObject)new JSONParser().parse(credentials);	
			endspoints = (JSONObject)new JSONParser().parse(endpointsAPI);
			String token = OSUtilities.GetToken(creds, endspoints);
			imgId = OSUtilities.GetImageId(creds, token, imgName);
		
		return imgId;
	}

	@Override
	public String GetKeypair(String credentials, String endpointsAPI,
			String keypair) throws IOException, ParseException 
	{
		//OSClient os = OSUtilities.Authorize(credentials, endpointsAPI);
		JSONObject creds = null;
		JSONObject endspoints = null;
		String kp = "";		
		try 
		{					
			creds = (JSONObject)new JSONParser().parse(credentials);	
			endspoints = (JSONObject)new JSONParser().parse(endpointsAPI);
			String token = OSUtilities.GetToken(creds, endspoints);
			kp = OSUtilities.CreateKeypair1(creds, token, keypair);
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
		return kp;
	}

	@Override
	public String GetFlavor(String credentials, String endpointsAPI,
			String minDisk, String minRAM) throws IOException, ParseException {
		
		JSONObject creds = null;
		JSONObject endspoints = null;
		String flavor = "";		
							
			creds = (JSONObject)new JSONParser().parse(credentials);	
			endspoints = (JSONObject)new JSONParser().parse(endpointsAPI);
			String token = OSUtilities.GetToken(creds, endspoints);
			flavor = OSUtilities.GetInstanceType(creds, token, minDisk, minRAM);
		
		
		return flavor;
	}

}
