package main.java.org.ia;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

public class Utilities 
{	
	public static String postMethod(String strUrl, JSONObject returnedJObject, String tokenId)
	{
	StringBuffer strResponse = new StringBuffer(); 	 
	 try
	 {
		HttpURLConnection strConnection = null; 
		URL url = new URL(strUrl);
		strConnection = (HttpURLConnection)url.openConnection();
		strConnection.setRequestMethod("POST");
		strConnection.setRequestProperty("Content-Type","application/json");
		strConnection.setRequestProperty("X-Auth-Token", tokenId);
		strConnection.setRequestProperty("Content-Length", "2000");
	    strConnection.setRequestProperty("Content-Language", "en-US");  				
	    strConnection.setUseCaches (false);
	    strConnection.setDoInput(true);
	    strConnection.setDoOutput(true);
	   
	    if (returnedJObject!=null) 
	    {
	      DataOutputStream strDOS = new DataOutputStream (strConnection.getOutputStream ());
	      strDOS.writeBytes (returnedJObject.toJSONString());
	      strDOS.flush ();
	      strDOS.close ();					
	    }	    
	    InputStream strIS = strConnection.getInputStream();
	    BufferedReader strBR = new BufferedReader(new InputStreamReader(strIS));
	    String strline;    
	    
        while((strline = strBR.readLine()) != null)
	    {
        	strResponse.append(strline);
        	strResponse.append('\r');	        
	    }  		
        strBR.close();
        
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
		return strResponse.toString();
	}
	
	public static String getMethod(String strUrl, String tokenId)
	{	
		StringBuffer strResponse = new StringBuffer(); 	
		try
		{
		HttpURLConnection strConnection = null; 
		URL sUrl = new URL(strUrl);
		strConnection = (HttpURLConnection)sUrl.openConnection();
		strConnection.setRequestMethod("GET");
		strConnection.setRequestProperty("X-Auth-Token",tokenId);
		strConnection.setRequestProperty("Accept","application/json");	
		
		int responseCode = strConnection.getResponseCode();
		
		BufferedReader inBR = new BufferedReader(
		        new InputStreamReader(strConnection.getInputStream()));
		String inputLine;
	
		while ((inputLine = inBR.readLine()) != null)
		{
			strResponse.append(inputLine);
		}
		inBR.close();
		} 
	    catch (IOException e)
	    {
			e.printStackTrace();
		}
		return strResponse.toString();
	}

	public static String postMethodforFirstToken(String strUrl, JSONObject returnedJObject)
	{
		StringBuffer strResponse = new StringBuffer(); 	 
		String tokenId = "";
		 try
		 {
			HttpURLConnection strConnection = null; 
			URL url = new URL(strUrl);
			strConnection = (HttpURLConnection)url.openConnection();
			strConnection.setRequestMethod("POST");
			strConnection.setRequestProperty("Content-Type","application/json");
			strConnection.setRequestProperty("Content-Length", "2000");
		    strConnection.setRequestProperty("Content-Language", "en-US");  				
		    strConnection.setUseCaches (false);
		    strConnection.setDoInput(true);
		    strConnection.setDoOutput(true);
		   
		    if (returnedJObject!=null) 
		    {
		      DataOutputStream strDOS = new DataOutputStream (strConnection.getOutputStream ());
		      strDOS.writeBytes (returnedJObject.toJSONString());
		      strDOS.flush ();
		      strDOS.close ();					
		    }	    
		    InputStream strIS = strConnection.getInputStream();
		    BufferedReader strBR = new BufferedReader(new InputStreamReader(strIS));
		    String strline;    
		    
	        while((strline = strBR.readLine()) != null)
		    {
	        	strResponse.append(strline);
	        	strResponse.append('\r');	        
		    }  		
	        strBR.close();
	        JSONObject jsonObject;
			jsonObject = (JSONObject) JSONValue.parseWithException(strResponse.toString());			
		 	JSONObject access =  (JSONObject) jsonObject.get("access");
			JSONObject token = (JSONObject) access.get("token");
			Object issuedAt = token.get("issued_at");
			Object expires =   token.get("expires");			
			Object id = token.get("id");
			tokenId = id.toString();		        
			} 
		 	catch (IOException e) 
			{
				e.printStackTrace();
			}
		 	catch (ParseException e) 
		 	{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		 	
			return tokenId;
		}

	public static String getTenant(String tokenId)
	{
		String tenant_obj_id = "";
		try
		{
			String strURL = "http://129.69.209.127:5000/v2.0/tenants";
			String response = getMethod(strURL,tokenId);
			
			JSONObject jsonObject2 = (JSONObject) JSONValue.parseWithException(response.toString());
	        JSONArray tenants =  (JSONArray) jsonObject2.get("tenants");           
			JSONObject tenant_obj = (JSONObject) tenants.get(0);
			tenant_obj_id = (String)tenant_obj.get("id");		
		}
		catch (ParseException e) 
		{
			e.printStackTrace();
		}
		return tenant_obj_id;
	}
	
	public static String postMethodforFinalToken(String username, String password, String tenant_obj_id)
	{
		String tokenIdS = "";
		try
		{	
			JSONObject returnedJObject1= new JSONObject();
	        JSONObject pwdCredJObject1=new JSONObject ();
	        JSONObject authJObject1=new JSONObject ();
	        
			pwdCredJObject1.put("username",username);
			pwdCredJObject1.put("password",password);
			authJObject1.put("passwordCredentials", pwdCredJObject1);
			authJObject1.put("tenantId",tenant_obj_id);
			returnedJObject1.put("auth",authJObject1);			
			
			HttpURLConnection connect = null; 
			URL surl = new URL("http://129.69.209.127:5000/v2.0/tokens");
			connect = (HttpURLConnection)surl.openConnection();
			connect.setRequestMethod("POST");
		    connect.setRequestProperty("Content-Type","application/json");
		    connect.setRequestProperty("Content-Length", "2000");
		    connect.setRequestProperty("Content-Language", "en-US");  				
		    connect.setUseCaches (false);
		    connect.setDoInput(true);
		    connect.setDoOutput(true);
		   
		    if (returnedJObject1!=null) 
		    {
		      DataOutputStream wrs = new DataOutputStream (connect.getOutputStream ());
		      wrs.writeBytes (returnedJObject1.toJSONString());
		      wrs.flush ();
		      wrs.close ();					
		    }	    
		    InputStream isS = connect.getInputStream();
		    BufferedReader rdS = new BufferedReader(new InputStreamReader(isS));
		    String lineS;
		    StringBuffer responseS = new StringBuffer(); 	 
		    
	        while((lineS = rdS.readLine()) != null)
		    {
		        responseS.append(lineS);
		        responseS.append('\r');	        
		    }  
			
			
			JSONObject jsonObjectS = (JSONObject) JSONValue.parseWithException(responseS.toString());
	        JSONObject accessS =  (JSONObject) jsonObjectS.get("access");
			JSONObject tokenS = (JSONObject) accessS.get("token");
			Object idS = tokenS.get("id");
			tokenIdS = idS.toString();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ParseException e) 
		{
			e.printStackTrace();
		}
		return tokenIdS;
	}
	
	public static String getImage(String tokenId, String tenant_obj_id)
	{
		String url = "http://129.69.209.127:8774/v2/"+tenant_obj_id+"/images";
		String image = "";
		String imgUrl = "";
		    
			try
			{
				String response = getMethod(url,tokenId);		
				System.out.println(response);
				JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(response);
				JSONArray images = (JSONArray) jsonObj.get("images");
	            for ( Iterator i = images.iterator(); i.hasNext(); )
				 {				
						 JSONObject it = (JSONObject) i.next();
						 //System.out.println("flavor:"+ it);
						 String name = (String) it.get("name");
							
						 JSONArray links = (JSONArray) it.get("links");
						 //System.out.println("links:"+ links);
							 
						 JSONObject href = (JSONObject) links.get(0);
						 // System.out.println("href:"+ href);
								 
						 imgUrl = (String) href.get("href");
						 //System.out.println("url:"+ sUrl);			
				 }	
			   	System.out.println(imgUrl.toString());
			}
			catch (ParseException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return imgUrl;
	}
	
	public static String getImageByName(String tokenId, String tenant_obj_id)
	{
		String url = "http://129.69.209.127:8774/v2/"+tenant_obj_id+"/images";
		String imageURL ="";
		String[] osName = {"ubuntu", "12.04"};  
		try
		{
			String response = getMethod(url,tokenId);
			JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(response.toString());
			JSONArray images = (JSONArray) jsonObj.get("images");
            String sUrl = "";
            
		     for ( Iterator i = images.iterator(); i.hasNext(); )
			 {				
					 JSONObject it = (JSONObject) i.next();
					 String name = (String) it.get("name");
					 int k = 0;
					 if(name.toLowerCase().matches("(?i).*"+osName[0]+".*"))
					 {
						 k = 1;
						 while (k < osName.length)					 
						  {
							 if(name.toLowerCase().matches("(?i).*"+osName[k]+".*"))
							 {
								 k++;
							 }
							 else
							 {
								 break;
							 }
						  }
						 
					 }					  
						 if(k > 0)
							
						  {
							 JSONArray links = (JSONArray) it.get("links");
							 JSONObject href = (JSONObject) links.get(0);
							 sUrl = (String) href.get("href");
							 if (k == osName.length)
							 {
								  break;
							 }
							 
						 }
						 else 
						 {
							 sUrl = "Required OS not found";
						 }
					 
			}	
		    imageURL = sUrl.toString();	
		    
		    System.out.println("imageURL:"+ imageURL);
		} 
		catch (ParseException e) 
		{
			e.printStackTrace();
		}
	  return imageURL;			
	}

	public static String getFlavorBySize(String minDiskInGB, String minRAMInMB, String tokenId, String tenant_obj_id)
	{
		String flavorURL = "";
		String url = "http://129.69.209.127:8774/v2/"+tenant_obj_id+"/flavors?minDisk="+minDiskInGB+"&minRam="+minRAMInMB;
		try
		{			
			String response = getMethod(url, tokenId);
	 		JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(response.toString());
			JSONArray flavors = (JSONArray) jsonObj.get("flavors");
				
			JSONObject ft = (JSONObject) flavors.get(0);
			JSONArray links = (JSONArray) ft.get("links");
			JSONObject href = (JSONObject) links.get(0);						 
			Object urlhref = href.get("href");			
			flavorURL = urlhref.toString();					
		} 
		catch (ParseException e)
		{
			e.printStackTrace();
		}	  
		System.out.println("flavorURL:"+ flavorURL.toString());
		return flavorURL;
	}
	
	public static String createSSHkeypair(String tokenId)
	{
		String keypair = "";
		try
		{			
			JSONObject nameJObject= new JSONObject();
			nameJObject.put("name","pkey");
			JSONObject keypairJObject=new JSONObject();
			keypairJObject.put("keypair",nameJObject);
		
			String response = postMethod("http://129.69.209.127:8774/v2/dbada39fa35b40dc8d452c7d50ff2843/os-keypairs", keypairJObject, tokenId);
			JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(response.toString());
		 	JSONObject keypairObj = (JSONObject) jsonObj.get("keypair");
		 	
		 	Object keypair_name = keypairObj.get("name");
		 	keypair = keypair_name.toString();
		 	
		} 
		catch (ParseException e)
		{
			e.printStackTrace();
		}
		System.out.println("keypair:" + keypair);
		return keypair;
	}	
 
	private static String checkSSHkeypair(String tokenId, String tenant_obj_id)
	{
		String url = "http://129.69.209.127:8774/v2/"+tenant_obj_id+"/os-keypairs";
		String keypair_name = "";
		try
		{
			String response = getMethod(url,tokenId);
			//print result
			System.out.println(response.toString());
			JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(response.toString());
		 	JSONArray keypairs = (JSONArray) jsonObj.get("keypairs");

			JSONObject keypair_list = (JSONObject)keypairs.get(0);
		 	JSONObject keypair_item = (JSONObject)keypair_list.get("keypair");
			keypair_name = (String)keypair_item.get("name");
			System.out.println(keypair_name.toString());
		}
		catch (ParseException e) 
		{
			e.printStackTrace();
		} 
		return keypair_name;
	}
	
	public static String createServer(String tokenId,String imageURL,String flavorURL,String keypair, String name, String tenant_obj_id)
	{
		String serverId = "";
		try
		{
			JSONObject serverDataJObject=new JSONObject ();
			JSONObject finalJObject= new JSONObject();
			serverDataJObject.put("flavorRef",flavorURL);
	        serverDataJObject.put("imageRef",imageURL);
	        serverDataJObject.put("key_name",keypair);
	        serverDataJObject.put("name",name);
	        finalJObject.put("server",serverDataJObject);
			
	        System.out.println(finalJObject.toString());
	       
		    String responsefinal = postMethod("http://129.69.209.127:8774/v2/"+tenant_obj_id+"/servers", finalJObject, tokenId);
			JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(responsefinal.toString());
		 	JSONObject serverObj = (JSONObject) jsonObj.get("server");
		 	
		 	Object server_id = serverObj.get("id");
		 	serverId  = server_id.toString();
		}
		catch (ParseException e) 
		{
			
		   e.printStackTrace();
		}
	System.out.println("serverId:"+serverId);
	return serverId;
	}

	public static String getFreeFloatingIp(String tokenId, String tenant_obj_id)
	{
		String url = "http://129.69.209.127:8774/v2/"+tenant_obj_id+"/os-floating-ips";
		String freeFloatingIp ="";
		String instance_id = "";
		String newFloatingIp = "";
		try
		{			
			String response = getMethod(url, tokenId);
			JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(response.toString());
			JSONArray fIps = (JSONArray) jsonObj.get("floating_ips");
			if (!fIps.isEmpty())
			{
			 for ( Iterator i = fIps.iterator(); i.hasNext(); )
			 {				
					 JSONObject it = (JSONObject) i.next();
					 instance_id = (String) it.get("instance_id");
					 
					 if(instance_id == null)
					 {
						 String ip = (String) it.get("ip");
						 freeFloatingIp = ip;
						 break;
					 }
			 }		
			}
			 if (instance_id != null|!fIps.isEmpty())
			 {
				 newFloatingIp = createFloatingIpForTenant(tokenId, tenant_obj_id);
				 freeFloatingIp = newFloatingIp;
			 }			
		}
		catch (ParseException e) 
		{
			e.printStackTrace();
		}
		return freeFloatingIp;
	}
	
	private static String createFloatingIpForTenant(String tokenId, String tenant_obj_id)
	{		
		String newFloatingIp ="";
		try 
		{
				String url = "http://129.69.209.127:8774/v2/"+tenant_obj_id+"/os-floating-ips";
						
				JSONObject floatingIpJObject=new JSONObject();
				floatingIpJObject.put("pool","nova-informatik-intern");
				System.out.println(floatingIpJObject.toString());
				
				String response = postMethod(url, floatingIpJObject, tokenId);
				System.out.println(response.toString());
				String instance_id = "";
				
			 	JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(response.toString());
				JSONObject floating_ip = (JSONObject) jsonObj.get("floating_ip");	
						
				instance_id = (String) floating_ip.get("instance_id");
							 
				if(instance_id == null)
				{
				  String ip = (String) floating_ip.get("ip");
				  newFloatingIp = ip;
				}		
		}	
		catch (ParseException e)
		{
			e.printStackTrace();
		}
		return newFloatingIp;
	}	

	public static void allocateFloatingIpToServer(String floatingIp, String serverId, String tokenId, String tenant_obj_id)
	{		
		try 
		{
			String status = "";
			URL surl = new URL("http://129.69.209.127:8774/v2/"+tenant_obj_id+"/servers/"+serverId);
			
			while(!status.contentEquals("ACTIVE"))
			{
				HttpURLConnection connection4 = (HttpURLConnection) surl.openConnection();	 
				connection4.setRequestMethod("GET");
				//add request header
				connection4.setRequestProperty("Accept","application/json");
				connection4.setRequestProperty("X-Auth-Token",tokenId);
				int responseCode = connection4.getResponseCode();
				
				BufferedReader br = new BufferedReader(
				        new InputStreamReader(connection4.getInputStream()));
				String strinputLine;
				StringBuffer sbresponse = new StringBuffer();
		 
				while ((strinputLine = br.readLine()) != null) {
					sbresponse.append(strinputLine);
				}
				br.close();
				JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(sbresponse.toString());
				JSONObject serverObj = (JSONObject) jsonObj.get("server");
				status = (String) serverObj.get("status");   
				connection4.disconnect();
			}
		
			if (status.contentEquals("ACTIVE"))
			
			{
				HttpURLConnection connection5 = null; 
			
				URL furl = new URL("http://129.69.209.127:8774/v2/"+tenant_obj_id+"/servers/"+serverId+"/action");
				connection5 = (HttpURLConnection)furl.openConnection();
				connection5.setRequestMethod("POST");
				connection5.setRequestProperty("Content-Type","application/json");
				connection5.setRequestProperty("Content-Length", "2000");
				connection5.setRequestProperty("Content-Language", "en-US");  
				connection5.setRequestProperty("X-Auth-Token",tokenId);
				connection5.setUseCaches (false);
				connection5.setDoInput(true);
				connection5.setDoOutput(true);			
				
				JSONObject addressJObject= new JSONObject();
				addressJObject.put("address",floatingIp);
				JSONObject floatingIpJObject=new JSONObject();
				floatingIpJObject.put("addFloatingIp",addressJObject);	
				
				DataOutputStream dosf = new DataOutputStream (connection5.getOutputStream());
				dosf.writeBytes (floatingIpJObject.toJSONString());
				dosf.flush ();
				dosf.close ();	
				
				System.out.println("\nSending 'POST' request to URL : " + furl);
				System.out.println("Response Code : " + connection5.getResponseCode());
				//InputStream isIp = connection4.getInputStream();
			}
		}			
	    catch (ParseException e) 
	    {
	    	e.printStackTrace();
	    }
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String getStaticIp(String serverId, String token, String tenant_obj_id)
	{
		String url = "http://129.69.209.127:8774/v2/"+tenant_obj_id+"/servers/"+serverId;
		String staticIp ="";
		try
		{	
			String status = "";
			URL surl = new URL(url);
			while(!status.contentEquals("ACTIVE"))
			{
				HttpURLConnection connection4 = (HttpURLConnection) surl.openConnection();
				connection4.setRequestMethod("GET");
				//add request header
				connection4.setRequestProperty("Accept","application/json");
				connection4.setRequestProperty("X-Auth-Token",token);
				int responseCode = connection4.getResponseCode();
				
				BufferedReader br = new BufferedReader(
				        new InputStreamReader(connection4.getInputStream()));
				String strinputLine;
				StringBuffer sbresponse = new StringBuffer();
		 
				while ((strinputLine = br.readLine()) != null) {
					sbresponse.append(strinputLine);
				}
				br.close();
				JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(sbresponse.toString());
				JSONObject serverObj = (JSONObject) jsonObj.get("server");
				status = (String) serverObj.get("status");   
				connection4.disconnect();
			}
		
			if (status.contentEquals("ACTIVE"))
			
			{		
				String response = Utilities.getMethod(url, token);
				JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(response.toString());
				JSONObject serverObj = (JSONObject) jsonObj.get("server");
				JSONObject addressesObj = (JSONObject) serverObj.get("addresses"); 
				System.out.println("address:"+ addressesObj.toString());
				JSONArray novanetworkObj = (JSONArray) addressesObj.get("novanetwork"); 
				JSONObject OS_EXT_IPS = (JSONObject) novanetworkObj.get(0); //change index to get another flavor
				System.out.println("OS_EXT_IPS:"+ OS_EXT_IPS);
				String addr = (String) OS_EXT_IPS.get("addr"); 
				System.out.println("addr: "+addr);
				staticIp = addr;
	
			}
		}	
		catch (ParseException e) 
		{			
		   e.printStackTrace();
		} 
		catch (MalformedURLException e) 
		{
		   e.printStackTrace();
		} 
		catch (IOException e) 
		{
		   e.printStackTrace();
		}
		return staticIp;
	}

}
