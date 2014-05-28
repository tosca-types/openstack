package main.java.org.ia;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import javax.jws.Oneway;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

@WebService
public class org_ia_PutUpServer__com_example_www_interfaces_lifecycle extends AbstractIAService {

	@WebMethod
	@SOAPBinding
	@Oneway
	public void install(
		@WebParam(name="username", targetNamespace="http://ia.org/") String username,
		@WebParam(name="password", targetNamespace="http://ia.org/") String password,
		@WebParam(name="minDisk", targetNamespace="http://ia.org/") String minDisk,
		@WebParam(name="minRAM", targetNamespace="http://ia.org/") String minRAM,
		@WebParam(name="imageUrl", targetNamespace="http://ia.org/") String imageUrl,
		@WebParam(name="keypair", targetNamespace="http://ia.org/") String keypair,
		@WebParam(name="name", targetNamespace="http://ia.org/") String name
	) {
		// This HashMap holds the return parameters of this operation.
		final HashMap<String,String> returnParameters = new HashMap<String, String>();

		JSONObject returnedJObject= new JSONObject();
        JSONObject pwdCredJObject=new JSONObject();
        JSONObject authJObject=new JSONObject();
        String serverId = "";
        String flavorURL = "";
       // String imageUrl = "";
        String freeFloatingIp = "";
        String staticIp = "";
    
        pwdCredJObject.put("username",username);
		pwdCredJObject.put("password",password);
		authJObject.put("passwordCredentials", pwdCredJObject);
		returnedJObject.put("auth",authJObject);
					
		//Get temporary token
		String tokenId = Utilities.postMethodforFirstToken("http://129.69.209.127:5000/v2.0/tokens", returnedJObject);			
		
		//Get tenant-id using token
		String tenant_obj_id = Utilities.getTenant(tokenId);
		
		//Get final token using tenant-id
		String tokenIdS = Utilities.postMethodforFinalToken(username, password,tenant_obj_id);
	
		//Get URL of last image in the list 			
		//imageUrl = Utilities.getImage(tokenIdS,tenant_obj_id);
		//Utilities.getImageByName(tokenIdS,tenant_obj_id);
		
		//Get URL of chosen flavor from the list 
        String minDiskInGB = minDisk;
        String minRAMInMB = minRAM;         
        flavorURL = Utilities.getFlavorBySize(minDiskInGB,minRAMInMB,tokenIdS,tenant_obj_id);		
		
		//Create VM with given parameters
        serverId = Utilities.createServer(tokenIdS,imageUrl,flavorURL,keypair,name,tenant_obj_id);
	 	
        //Assign the floating ip to the VM
	 	staticIp = Utilities.getStaticIp(serverId, tokenIdS, tenant_obj_id);
	        
		// Do NOT delete the next line of code. Set "" as value if you want to return nothing or an empty result!
		returnParameters.put("serverId", serverId);
		returnParameters.put("staticIp", staticIp);
		sendResponse(returnParameters);
	}

	@WebMethod
	@SOAPBinding
	@Oneway
	public void configure(
		@WebParam(name="username", targetNamespace="http://ia.org/") String username,
		@WebParam(name="password", targetNamespace="http://ia.org/") String password,
		@WebParam(name="serverId", targetNamespace="http://ia.org/") String serverId
	) {
		// This HashMap holds the return parameters of this operation.
		final HashMap<String,String> returnParameters = new HashMap<String, String>();
		JSONObject returnedJObject= new JSONObject();
	    JSONObject pwdCredJObject=new JSONObject();
	    JSONObject authJObject=new JSONObject();
	        
		pwdCredJObject.put("username",username);
		pwdCredJObject.put("password",password);
		authJObject.put("passwordCredentials", pwdCredJObject);
		returnedJObject.put("auth",authJObject);
							
		//Get temporary token
		String tokenId = Utilities.postMethodforFirstToken("http://129.69.209.127:5000/v2.0/tokens", returnedJObject);			
				
		//Get tenant-id using token
		String tenant_obj_id = Utilities.getTenant(tokenId);
				
		//Get final token using tenant-id
		String tokenIdS = Utilities.postMethodforFinalToken(username, password,tenant_obj_id);
			
		//Acquire the first floating ip from the list, or create it
		String freeFloatingIp = Utilities.getFreeFloatingIp(tokenIdS,tenant_obj_id);
			
		//Assign the floating ip to the VM
		Utilities.allocateFloatingIpToServer(freeFloatingIp, serverId, tokenIdS,tenant_obj_id);
		
		returnParameters.put("floatingIp", freeFloatingIp);
		sendResponse(returnParameters);	
	}

	@WebMethod
	@SOAPBinding
	@Oneway
	public void start(
		@WebParam(name="username", targetNamespace="http://ia.org/") String username,
		@WebParam(name="password", targetNamespace="http://ia.org/") String password,
		@WebParam(name="serverId", targetNamespace="http://ia.org/") String serverId
	) {
		// This HashMap holds the return parameters of this operation.
		final HashMap<String,String> returnParameters = new HashMap<String, String>();

		String status = "";
		try
		{
			JSONObject returnedJObject= new JSONObject();
		    JSONObject pwdCredJObject=new JSONObject();
		    JSONObject authJObject=new JSONObject();
		        
			pwdCredJObject.put("username",username);
			pwdCredJObject.put("password",password);
			authJObject.put("passwordCredentials", pwdCredJObject);
			returnedJObject.put("auth",authJObject);
								
			//Get temporary token
			String tokenId = Utilities.postMethodforFirstToken("http://129.69.209.127:5000/v2.0/tokens", returnedJObject);			
					
			//Get tenant-id using token
			String tenant_obj_id = Utilities.getTenant(tokenId);
					
			//Get final token using tenant-id
			String tokenIdS = Utilities.postMethodforFinalToken(username, password,tenant_obj_id);
			
			String response = Utilities.getMethod("http://129.69.209.127:8774/v2/" + tenant_obj_id + "/servers/"+serverId, tokenIdS);
			JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(response.toString());
			JSONObject serverObj = (JSONObject) jsonObj.get("server");
			status = (String) serverObj.get("status"); 
			if (status.contentEquals("SUSPENDED"))
			{
				JSONObject finalJObject= new JSONObject();   
		        finalJObject.put("resume",null);		        
		        System.out.println(finalJObject.toString());		        
		        HttpURLConnection connection2 = null; 
		        URL furl = new URL("http://129.69.209.127:8774/v2/dbada39fa35b40dc8d452c7d50ff2843/servers/"+serverId+"/action");
				connection2 = (HttpURLConnection)furl.openConnection();
				connection2.setRequestMethod("POST");
				connection2.setRequestProperty("Content-Type","application/json");
				connection2.setRequestProperty("Content-Length", "2000");
				connection2.setRequestProperty("Content-Language", "en-US");  
				connection2.setRequestProperty("X-Auth-Token",tokenIdS);
				connection2.setUseCaches (false);
				connection2.setDoInput(true);
				connection2.setDoOutput(true);		
		        
				DataOutputStream dosf = new DataOutputStream (connection2.getOutputStream());
				dosf.writeBytes (finalJObject.toJSONString());
				dosf.flush ();
				dosf.close ();	
				
				System.out.println("\nSending 'POST' request to URL : " + furl);
				System.out.println("Response Code : " + connection2.getResponseCode());
				
				if (connection2.getResponseCode() == 202)
				{
					status = "Running";
				}
			}	
			else
			{
				status = "Already active";
			}
		}
		catch (IOException e) 
		{
			
		   e.printStackTrace();
		}
		catch (ParseException e)
		{
			
			   e.printStackTrace();
		}
		returnParameters.put("status", status);
		sendResponse(returnParameters);
}

	@WebMethod
	@SOAPBinding
	@Oneway
	public void stop(
		@WebParam(name="username", targetNamespace="http://ia.org/") String username,
		@WebParam(name="password", targetNamespace="http://ia.org/") String password,
		@WebParam(name="serverId", targetNamespace="http://ia.org/") String serverId
	) {
		// This HashMap holds the return parameters of this operation.
		final HashMap<String,String> returnParameters = new HashMap<String, String>();

		String status = "";
		try
		{
			JSONObject returnedJObject= new JSONObject();
	        JSONObject pwdCredJObject=new JSONObject();
	        JSONObject authJObject=new JSONObject();
	    
	        pwdCredJObject.put("username",username);
			pwdCredJObject.put("password",password);
			authJObject.put("passwordCredentials", pwdCredJObject);
			returnedJObject.put("auth",authJObject);
						
			//Get temporary token
			String tokenId = Utilities.postMethodforFirstToken("http://129.69.209.127:5000/v2.0/tokens", returnedJObject);			
			
			//Get tenant-id using token
			String tenant_obj_id = Utilities.getTenant(tokenId);
			
			//Get final token using tenant-id
			String tokenIdS = Utilities.postMethodforFinalToken(username, password,tenant_obj_id);
			
			String response = Utilities.getMethod("http://129.69.209.127:8774/v2/"+tenant_obj_id+"/servers/"+serverId, tokenIdS);
			JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(response.toString());
			JSONObject serverObj = (JSONObject) jsonObj.get("server");
			status = (String) serverObj.get("status"); 
			
			if (status.contentEquals("ACTIVE"))
			{
				JSONObject finalJObject= new JSONObject();   
		        finalJObject.put("suspend",null);		        
		        System.out.println(finalJObject.toString());
		        
		        HttpURLConnection connection2 = null; 
		        URL furl = new URL("http://129.69.209.127:8774/v2/"+tenant_obj_id+"/servers/"+serverId+"/action");
				connection2 = (HttpURLConnection)furl.openConnection();
				connection2.setRequestMethod("POST");
				connection2.setRequestProperty("Content-Type","application/json");
				connection2.setRequestProperty("Content-Length", "2000");
				connection2.setRequestProperty("Content-Language", "en-US");  
				connection2.setRequestProperty("X-Auth-Token",tokenIdS);
				connection2.setUseCaches (false);
				connection2.setDoInput(true);
				connection2.setDoOutput(true);		
		        
				DataOutputStream dosf = new DataOutputStream (connection2.getOutputStream());
				dosf.writeBytes (finalJObject.toJSONString());
				dosf.flush ();
				dosf.close ();	
				
				System.out.println("\nSending 'POST' request to URL : " + furl);
				System.out.println("Response Code : " + connection2.getResponseCode());
						
				if (connection2.getResponseCode() == 202)
				{
					status = "Shutdown";
				}
			}	
		}
		catch (IOException e) 
		{			
		   e.printStackTrace();
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
		// Output Parameter 'status' (optional)
		// TODO: Set status parameter here.
		// Do NOT delete the next line of code. Set "" as value if you want to return nothing or an empty result!
		returnParameters.put("status", status);
		sendResponse(returnParameters);
	}

	@WebMethod
	@SOAPBinding
	@Oneway
	public void uninstall(

	) {
		// TODO: Implement your operation here.
	}



}
