package org.opentosca.types;

import java.util.ArrayList;

import org.json.simple.JSONObject;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesRequest;

public class AwsUtilities 
{
	public static String Authorize(JSONObject credentials)
	{
		String accessKey = (String) credentials.get("accessKey");
		String secretKey = (String) credentials.get("secretKey");
		return accessKey + ";" + secretKey;
	}
	
	public static String CreateServer(String keys, JSONObject endpointsAPI, String imageId, String typeId, String keypair)
	{
		String instanceId = null;
		String accessKey = keys.split(";")[0];
		String secretKey = keys.split(";")[1];
		
		String regionEndpoint = (String) endpointsAPI.get("amazon-regionEndpoint");
		
		try 
		{
			// EC2 Client for given region and credentials
			AmazonEC2 ec2 = new AmazonEC2Client(new BasicAWSCredentials(accessKey, secretKey));
			ec2.setEndpoint(regionEndpoint);
			
			RunInstancesRequest req = new RunInstancesRequest();
			req.withInstanceType(typeId);
			req.withImageId(imageId);
			req.withMinCount(1).withMaxCount(1);
			req.withSecurityGroups("securityGroup");
			req.withKeyName(keypair);
				
			// Execute run instance request and return ID of first (and only) instance
			RunInstancesResult runInstances = ec2.runInstances(req);
			instanceId = runInstances.getReservation().getInstances().get(0).getInstanceId();
			
		} 
		catch (AmazonServiceException ase)
		{
			System.out.println("Caught an AmazonServiceException, which means your request made it " + "to AWS, but was rejected with an error response for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
			instanceId = "MSG_FAILED";
			
		} 
		catch (AmazonClientException ace) 
		{
			System.out.println("Caught an AmazonClientException, which means the client encountered " + "a serious internal problem while trying to communicate with AWS, " + "such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
			instanceId = "MSG_FAILED";
			
		}
		return instanceId;
	}

	public static void StopServer(String keys, JSONObject endpointsAPI, String serverId)
	{			
		String accessKey = keys.split(";")[0];
		String secretKey = keys.split(";")[1];
		
		String regionEndpoint = (String) endpointsAPI.get("amazon-regionEndpoint");
		try
		{
			// EC2 Client for given region and credentials
						AmazonEC2 ec2 = new AmazonEC2Client(new BasicAWSCredentials(accessKey, secretKey));
						ec2.setEndpoint(regionEndpoint);
						
						StopInstancesRequest req = new StopInstancesRequest();
						ArrayList<String> list = new ArrayList<String>();
						list.add(serverId);
						req.setInstanceIds(list);
						
						System.out.println("Stopping Instance " + serverId);
						ec2.stopInstances(req);
						
						return;
						
					}
					catch (AmazonServiceException ase)
					{
						System.out.println("Caught an AmazonServiceException, which means your request made it " + "to AWS, but was rejected with an error response for some reason.");
						System.out.println("Error Message:    " + ase.getMessage());
						System.out.println("HTTP Status Code: " + ase.getStatusCode());
						System.out.println("AWS Error Code:   " + ase.getErrorCode());
						System.out.println("Error Type:       " + ase.getErrorType());
						System.out.println("Request ID:       " + ase.getRequestId());
						return;
						
					} 
					catch (AmazonClientException ace)
					{
						System.out.println("Caught an AmazonClientException, which means the client encountered " + "a serious internal problem while trying to communicate with AWS, " + "such as not being able to access the network.");
						System.out.println("Error Message: " + ace.getMessage());
						return;
					}
	
	}
	
	public static void StartServer(String keys, JSONObject endpointsAPI, String serverId)
	{			
		String accessKey = keys.split(";")[0];
		String secretKey = keys.split(";")[1];
		
		String regionEndpoint = (String) endpointsAPI.get("amazon-regionEndpoint");
		try
		{
			// EC2 Client for given region and credentials
						AmazonEC2 ec2 = new AmazonEC2Client(new BasicAWSCredentials(accessKey, secretKey));
						ec2.setEndpoint(regionEndpoint);
						
						StartInstancesRequest req = new StartInstancesRequest();
						ArrayList<String> list = new ArrayList<String>();
						list.add(serverId);
						req.setInstanceIds(list);
						
						System.out.println("Starting Instance " + serverId);
						ec2.startInstances(req);
						
						return;
						
					}
					catch (AmazonServiceException ase)
					{
						System.out.println("Caught an AmazonServiceException, which means your request made it " + "to AWS, but was rejected with an error response for some reason.");
						System.out.println("Error Message:    " + ase.getMessage());
						System.out.println("HTTP Status Code: " + ase.getStatusCode());
						System.out.println("AWS Error Code:   " + ase.getErrorCode());
						System.out.println("Error Type:       " + ase.getErrorType());
						System.out.println("Request ID:       " + ase.getRequestId());
						return;
						
					} 
					catch (AmazonClientException ace)
					{
						System.out.println("Caught an AmazonClientException, which means the client encountered " + "a serious internal problem while trying to communicate with AWS, " + "such as not being able to access the network.");
						System.out.println("Error Message: " + ace.getMessage());
						return;
					}
	}
}
