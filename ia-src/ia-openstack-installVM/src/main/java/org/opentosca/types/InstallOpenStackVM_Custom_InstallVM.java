package org.opentosca.types;

import java.io.IOException;
import java.util.HashMap;

import javax.jws.Oneway;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.json.simple.parser.ParseException;
import org.openstack4j.api.exceptions.ClientResponseException;

@WebService
public class InstallOpenStackVM_Custom_InstallVM extends AbstractIAService {

	@WebMethod
	@SOAPBinding
	@Oneway
	public void TerminateVMbyServerId(
		@WebParam(name="credentials", targetNamespace="http://types.opentosca.org/") String credentials,
		@WebParam(name="endpointsAPI", targetNamespace="http://types.opentosca.org/") String endpointsAPI,
		@WebParam(name="serverId", targetNamespace="http://types.opentosca.org/") String serverId
	) {
		// This HashMap holds the return parameters of this operation.
		final HashMap<String,String> returnParameters = new HashMap<String, String>();
		try 
		{
			ICloudProviderAPI CP = CloudProviderAPIFactory.createCP(endpointsAPI);
			//String privKey = CP.GetKeypair(credentials, endpointsAPI, keypair);		
			
			CP.TerminateVM(credentials, endpointsAPI, serverId);
					
			returnParameters.put("success", "true");
			
			sendResponse(returnParameters);			
		}		
		catch (Exception npe) 
		{
			npe.printStackTrace();
			System.out.println("npe.getClass:"+ npe.getClass());
			sendFaultResponse("ws04", "General exception.");						
		}		
	}

	
	
	@WebMethod
	@SOAPBinding
	@Oneway
	public void InstallVMwithCustomKeypair(
		@WebParam(name="credentials", targetNamespace="http://types.opentosca.org/") String credentials,
		@WebParam(name="endpointsAPI", targetNamespace="http://types.opentosca.org/") String endpointsAPI,
		@WebParam(name="flavorId", targetNamespace="http://types.opentosca.org/") String flavorId,
		@WebParam(name="keypair", targetNamespace="http://types.opentosca.org/") String keypair,
		@WebParam(name="imageId", targetNamespace="http://types.opentosca.org/") String imageId
	) {
		// This HashMap holds the return parameters of this operation.
		final HashMap<String,String> returnParameters = new HashMap<String, String>();
		try 
		{
			ICloudProviderAPI CP = CloudProviderAPIFactory.createCP(endpointsAPI);
			//String privKey = CP.GetKeypair(credentials, endpointsAPI, keypair);		
			
			String retParams = CP.InstallVM(credentials, endpointsAPI, imageId, flavorId, keypair);
					
			String serverId = retParams.split(";")[0];
			returnParameters.put("serverId", serverId);			
	
			String floatingIp = retParams.split(";")[1];
			returnParameters.put("floatingIp", floatingIp);
			
			sendResponse(returnParameters);			
		} 
		catch (IOException e) 
		{	
			e.printStackTrace();
			System.out.println("e.getClass:"+ e.getClass());
			sendFaultResponse("ws01","Incorrect parameter given. Keypair might not exist.");								
		}
		catch (ParseException pe) 
		{
				System.out.println("ex.getClass:"+ pe.getClass());
				sendFaultResponse("ws02", "JSON Parsing error of input string");				
		}
		catch (ClientResponseException ex) 
		{
			System.out.println("ex.getClass:"+ ex.getClass());
			sendFaultResponse("ws03", "Server could not be created. Wrong values of flavor, image or keypair have been specified or floatingIp limit has exceeded");				
		}
		
		catch (Exception npe) 
		{
			npe.printStackTrace();
			System.out.println("npe.getClass:"+ npe.getClass());
			sendFaultResponse("ws04", "General exception. Either input parameters are wrong or floating Ip could not be allocated.");						
		}		
	}

	@WebMethod
	@SOAPBinding
	@Oneway
	public void InstallVMwithGeneratedKeypair(
		@WebParam(name="credentials", targetNamespace="http://types.opentosca.org/") String credentials,
		@WebParam(name="endpointsAPI", targetNamespace="http://types.opentosca.org/") String endpointsAPI,
		@WebParam(name="flavorId", targetNamespace="http://types.opentosca.org/") String flavorId,
		@WebParam(name="imageName", targetNamespace="http://types.opentosca.org/") String imageName
	) {
		// This HashMap holds the return parameters of this operation.
		final HashMap<String,String> returnParameters = new HashMap<String, String>();

		try 
		{
			ICloudProviderAPI CP = CloudProviderAPIFactory.createCP(endpointsAPI);
			String imgId = CP.GetImage(credentials, endpointsAPI, imageName);			

			java.util.Date today = new java.util.Date();
			String keypair = "os_key_"+String.valueOf(today.hashCode());
			String privKey = CP.GetKeypair(credentials, endpointsAPI, keypair);		
			
			String retParams = CP.InstallVM(credentials, endpointsAPI, imgId, flavorId, keypair);
					
			String serverId = retParams.split(";")[0];
			returnParameters.put("serverId", serverId);			
	
			String floatingIp = retParams.split(";")[1];
			returnParameters.put("floatingIp", floatingIp);
			
			returnParameters.put("privKey", privKey);	
			sendResponse(returnParameters);			
		} 
		catch (IOException e) 
		{	
			e.printStackTrace();
			System.out.println("e.getClass:"+ e.getClass());
			sendFaultResponse("ws01","Incorrect parameter given. Keypair with same name might already exist.");								
		}
		catch (ParseException pe) 
		{
				System.out.println("ex.getClass:"+ pe.getClass());
				sendFaultResponse("ws02", "JSON Parsing error of input string");				
		}
		catch (ClientResponseException ex) 
		{
			System.out.println("ex.getClass:"+ ex.getClass());
			ex.printStackTrace();
			sendFaultResponse("ws03", "Server could not be created. Wrong values of flavor, image or keypair have been specified or floatingIp limit has exceeded");				
		}
		
		catch (Exception npe) 
		{
			npe.printStackTrace();
			System.out.println("npe.getClass:"+ npe.getClass());
			sendFaultResponse("ws04", "General exception. Either input parameters are wrong or floating Ip could not be allocated.");						
		}
	}

	@WebMethod
	@SOAPBinding
	@Oneway
	public void InstallVMwithCustomFlavor(
		@WebParam(name="credentials", targetNamespace="http://types.opentosca.org/") String credentials,
		@WebParam(name="endpointsAPI", targetNamespace="http://types.opentosca.org/") String endpointsAPI,
		@WebParam(name="minDisk", targetNamespace="http://types.opentosca.org/") String minDisk,
		@WebParam(name="minRAM", targetNamespace="http://types.opentosca.org/") String minRAM,
		@WebParam(name="imageName", targetNamespace="http://types.opentosca.org/") String imageName,
		@WebParam(name="keypair", targetNamespace="http://types.opentosca.org/") String keypair
	) {
		// This HashMap holds the return parameters of this operation.
		final HashMap<String,String> returnParameters = new HashMap<String, String>();

		try 
		{
			ICloudProviderAPI CP = CloudProviderAPIFactory.createCP(endpointsAPI);
			
			String imgId = CP.GetImage(credentials, endpointsAPI, imageName);
			
			String flavor = CP.GetFlavor(credentials, endpointsAPI, minDisk, minRAM);			
			
			String retParams = CP.InstallVM(credentials,endpointsAPI, imgId, flavor, keypair); 	
			
			String serverId = retParams.split(";")[0];
			returnParameters.put("serverId", serverId);			
	
			String floatingIp = retParams.split(";")[1];
			returnParameters.put("floatingIp", floatingIp);	
			sendResponse(returnParameters);			
		} 
		catch (IOException e) 
		{			
			e.printStackTrace();
			System.out.println("e.getClass:"+ e.getClass());
			sendFaultResponse("ws01","Incorrect parameter given.");								
		}	
		catch (ParseException pe) 
		{
				System.out.println("ex.getClass:"+ pe.getClass());
				sendFaultResponse("ws02", "JSON Parsing error of input string.");				
		}
		catch (ClientResponseException ex) 
		{
			ex.printStackTrace();
			System.out.println("ex.getClass:"+ ex.getClass());
			sendFaultResponse("ws03", "Server could not be created possibly because of inadequate flavor specifications or non-existent keypair name. Choose a different flavor or keypair for the image.");				
		}
		catch (Exception npe) 
		{
			npe.printStackTrace();
			System.out.println("npe.getClass:"+ npe.getClass());
			sendFaultResponse("ws04", "General exception. Either input parameters are wrong or floating Ip could not be allocated.");						
		}
	}

	@WebMethod
	@SOAPBinding
	@Oneway
	public void InstallVMwithCustomImage(
		@WebParam(name="credentials", targetNamespace="http://types.opentosca.org/") String credentials,
		@WebParam(name="endpointsAPI", targetNamespace="http://types.opentosca.org/") String endpointsAPI,
		@WebParam(name="flavorId", targetNamespace="http://types.opentosca.org/") String flavorId,
		@WebParam(name="imageName", targetNamespace="http://types.opentosca.org/") String imageName,
		@WebParam(name="keypair", targetNamespace="http://types.opentosca.org/") String keypair
	) {
		// This HashMap holds the return parameters of this operation.
		final HashMap<String,String> returnParameters = new HashMap<String, String>();

		try 
		{
			ICloudProviderAPI CP = CloudProviderAPIFactory.createCP(endpointsAPI);
			String imgId = CP.GetImage(credentials, endpointsAPI, imageName);
			
			String retParams = CP.InstallVM(credentials,endpointsAPI, imgId, flavorId, keypair); 	
			
			String serverId = retParams.split(";")[0];
			returnParameters.put("serverId", serverId);			
	
			String floatingIp = retParams.split(";")[1];
			returnParameters.put("floatingIp", floatingIp);	
			
			sendResponse(returnParameters);			
		} 		
		catch (IOException e) 
		{				
			System.out.println("e.getClass:"+ e.getClass());
			sendFaultResponse("ws01","Incorrect parameter given.");								
		}	
		catch (ParseException pe) 
		{
				System.out.println("ex.getClass:"+ pe.getClass());
				sendFaultResponse("ws02", "JSON Parsing error of input string.");				
		}
		catch (ClientResponseException ex) 
		{
				System.out.println("ex.getClass:"+ ex.getClass());
				sendFaultResponse("ws03", "Server could not be created. Image name may be wrong or floatingIp limit may have exceeded.");				
		}
		catch (Exception npe) 
		{
				System.out.println("npe.getClass:"+ npe.getClass());
				sendFaultResponse("ws04", "General exception. Either input parameters are wrong or floating Ip could not be allocated.");						
		}
	}

}
