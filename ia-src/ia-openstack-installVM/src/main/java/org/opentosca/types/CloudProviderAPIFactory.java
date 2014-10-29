package org.opentosca.types;

import org.json.simple.JSONObject;

public abstract class CloudProviderAPIFactory
{	
	public static ICloudProviderAPI createCP(String bundleAPIEndpoints)
	{
		String endpointAPI =  (String) bundleAPIEndpoints.toString();		
		if(endpointAPI.contains("openstack") || endpointAPI.contains("os"))
		{
			return new OpenStack4jAPI();
		}
		else
		{
			return null;
		}
	}
}