package org.opentosca.types;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class AwsAPI implements ICloudProviderAPI
{

	@Override
	public String InstallVM(String credentials, String endpointsAPI,
			String imageId, String typeId, String keypair) throws ParseException
	{
		JSONObject creds = null;
		JSONObject endspoints = null;
		String retParams = "";
			
		creds = (JSONObject)new JSONParser().parse(credentials);		
		endspoints = (JSONObject)new JSONParser().parse(endpointsAPI);
		String os = AwsUtilities.Authorize(creds);	
		
		retParams = AwsUtilities.CreateServer(os, endspoints,imageId,typeId,keypair);
				
		return retParams;
	}

	@Override
	public void StopVM(String credentials, String endpointsAPI,
			String serverId) 
	{
		try
		{
			JSONObject creds = (JSONObject)new JSONParser().parse(credentials);
			JSONObject endspoints = (JSONObject)new JSONParser().parse(endpointsAPI);
			String os = AwsUtilities.Authorize(creds);	
			
			AwsUtilities.StopServer(os,endspoints,serverId);	
		}
		catch (ParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}						
	}

	@Override
	public void StartVM(String credentials, String endpointsAPI,
			String serverId)
	{
		try
		{
			JSONObject creds = (JSONObject)new JSONParser().parse(credentials);		
			JSONObject endspoints = (JSONObject)new JSONParser().parse(endpointsAPI);
			String os = AwsUtilities.Authorize(creds);
			
			AwsUtilities.StartServer(os,endspoints,serverId);		
		}
		catch (ParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void TerminateVM(JSONObject credentials, JSONObject endpointsAPI,
			String serverId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String GetImage(String credentials, String endpointsAPI,
			String imgName) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String GetKeypair(String credentials, String endpointsAPI,
			String keypair) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String GetFlavor(String credentials, String endpointsAPI,
			String minDisk, String minRAM) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
