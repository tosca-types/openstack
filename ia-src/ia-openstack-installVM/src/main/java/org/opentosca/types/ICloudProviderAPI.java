package org.opentosca.types;

import org.json.simple.JSONObject;

public interface ICloudProviderAPI 
{
	public String InstallVM(String credentials, String endpointsAPI, String imageId, String typeId, String keypair) throws Exception;
	
	public void StopVM(String credentials, String endpointsAPI, String serverId) throws Exception;
	
	public void StartVM(String credentials, String endpointsAPI, String serverId) throws Exception;
	
	public void TerminateVM(String credentials, String endpointsAPI, String serverId);
	
	public String GetImage(String credentials, String endpointsAPI, String imgName) throws Exception;
	
	public String GetKeypair(String credentials, String endpointsAPI, String keypair) throws Exception;

	public String GetFlavor(String credentials, String endpointsAPI,String minDisk, String minRAM) throws Exception;
}
