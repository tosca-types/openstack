package org.opentosca.types;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient;
import org.openstack4j.api.exceptions.ClientResponseException;
import org.openstack4j.model.compute.Action;
import org.openstack4j.model.compute.ActionResponse;
import org.openstack4j.model.compute.FloatingIP;
import org.openstack4j.model.compute.Keypair;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.compute.Server.Status;
import org.openstack4j.model.compute.ServerCreate;
import org.openstack4j.openstack.OSFactory;

public class OSUtilities {
	public static OSClient Authorize(JSONObject credentials,
			JSONObject endpointsAPI) {
		JSONObject auth = (JSONObject) credentials.get("auth");
		String tenantId = (String) auth.get("tenantId");
		JSONObject pwdcred = (JSONObject) auth.get("passwordCredentials");
		String username = (String) pwdcred.get("username");
		String password = (String) pwdcred.get("password");

		String identityService = (String) endpointsAPI.get("os-identity-api");

		OSClient os = OSFactory.builder().endpoint(identityService)
				.credentials(username, password).tenantId(tenantId)
				.authenticate();

		return os;
	}

	public static String CreateServer(OSClient os, String server_name,
			String image_id, String flavor_id, String keypair)
			throws ClientResponseException, Exception {
		Server server = null;
		String server_id = "";
		String floatingIpAddress = "";
		String retParams = "";
		// Create a Server Model Object

		ServerCreate sc = Builders.server().name(server_name).flavor(flavor_id)
				.image(image_id).keypairName(keypair).build();
		// Boot the Server
		server = os.compute().servers().boot(sc);
		String servers[] = server.toString().split(",");
		server_id = servers[0].split("=")[1];

		java.util.List<? extends FloatingIP> ips = os.compute().floatingIps()
				.list();

		String ips_arr[] = ips.toString().split("NovaFloatingIP");
		int i = 1;
		while (i < ips_arr.length) {
			// System.out.println("Nova:"+ips_arr[i]);
			if (!ips_arr[i].toString().contains("instanceId")) {
				System.out.println(i);
				String floating_ip = ips_arr[i].toString();
				floatingIpAddress = floating_ip.split(",")[1].split("=")[1];
				break;
			}

			i++;
		}
		if (floatingIpAddress.isEmpty()) {
			FloatingIP floating_ip = os.compute().floatingIps()
					.allocateIP("nova-informatik-intern");
			String ip[] = floating_ip.toString().split(",");
			floatingIpAddress = ip[1].split("=")[1];
		}

		try {
			TimeUnit.SECONDS.sleep(10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ActionResponse r = os.compute().floatingIps()
				.addFloatingIP(server, floatingIpAddress);
		retParams = server_id + ";" + floatingIpAddress;

		return retParams;
	}

	public static void StopServer(OSClient os, String server_id)
			throws InterruptedException {
		Status status = os.compute().servers().get(server_id).getStatus();
		System.out.print(status);
		if (status.toString().contentEquals("ACTIVE")) {
			os.compute().servers().action(server_id, Action.STOP);
		}
		TimeUnit.SECONDS.sleep(10);

		System.out.println("stop");
	}

	public static void StartServer(OSClient os, String server_id)
			throws InterruptedException {
		Status status = os.compute().servers().get(server_id).getStatus();
		System.out.print(status);
		if (!status.toString().contentEquals("ACTIVE")) {
			os.compute().servers().action(server_id, Action.START);
		}
		TimeUnit.SECONDS.sleep(10);

		System.out.println("start");
	}

	public static String CreateKeypair(OSClient os, String keypair)
			throws InterruptedException {
		Keypair kp = os.compute().keypairs().create(keypair, null);
		String name = kp.getName();
		System.out.println("name: " + name);
		return name;
	}

	public static String CreateKeypair1(JSONObject credentials, String token,
			String keypair) throws InterruptedException, IOException {
		String keypair1 = "";
		try {
			JSONObject nameJObject = new JSONObject();
			nameJObject.put("name", keypair);
			JSONObject keypairJObject = new JSONObject();
			keypairJObject.put("keypair", nameJObject);

			JSONObject auth = (JSONObject) credentials.get("auth");
			String tenantId = (String) auth.get("tenantId");

			String response = PostMethod("http://129.69.209.127:8774/v2/"
					+ tenantId + "/os-keypairs", keypairJObject, token);
			JSONObject jsonObj = (JSONObject) JSONValue
					.parseWithException(response.toString());
			JSONObject keypairObj = (JSONObject) jsonObj.get("keypair");
			Object keypair_name = keypairObj.get("name");

			Object private_key = keypairObj.get("private_key");
			keypair1 = private_key.toString();

		} catch (org.json.simple.parser.ParseException e) {
			throw new IOException("JSON Parsing failed");
		}
		return keypair1;
	}

	public static void TerminateServer(OSClient os, String server_id)
			throws InterruptedException {
		System.out.println(os.compute().servers().get(server_id).getStatus());
		os.compute().servers().delete(server_id);
		System.out.println(os.compute().servers().get(server_id).getStatus());

	}

	private static String GetMethod(String strUrl, String tokenId)
			throws IOException {
		StringBuffer strResponse = new StringBuffer();
		try {
			HttpURLConnection strConnection = null;
			URL sUrl = new URL(strUrl);
			strConnection = (HttpURLConnection) sUrl.openConnection();
			strConnection.setRequestMethod("GET");
			strConnection.setRequestProperty("X-Auth-Token", tokenId);
			strConnection.setRequestProperty("Accept", "application/json");

			int responseCode = strConnection.getResponseCode();

			BufferedReader inBR = new BufferedReader(new InputStreamReader(
					strConnection.getInputStream()));
			String inputLine;

			while ((inputLine = inBR.readLine()) != null) {
				strResponse.append(inputLine);
			}
			inBR.close();
		} catch (IOException e) {
			throw new IOException("HTTP GET failed");
		}
		return strResponse.toString();
	}

	private static String PostMethod(String strUrl, JSONObject returnedJObject,
			String tokenId) throws IOException {
		StringBuffer strResponse = new StringBuffer();
		try {
			HttpURLConnection strConnection = null;
			URL url = new URL(strUrl);
			strConnection = (HttpURLConnection) url.openConnection();
			strConnection.setRequestMethod("POST");
			strConnection
					.setRequestProperty("Content-Type", "application/json");
			strConnection.setRequestProperty("X-Auth-Token", tokenId);
			strConnection.setRequestProperty("Content-Length", "2000");
			strConnection.setRequestProperty("Content-Language", "en-US");
			strConnection.setUseCaches(false);
			strConnection.setDoInput(true);
			strConnection.setDoOutput(true);

			if (returnedJObject != null) {
				DataOutputStream strDOS = new DataOutputStream(
						strConnection.getOutputStream());
				strDOS.writeBytes(returnedJObject.toJSONString());
				strDOS.flush();
				strDOS.close();
			}
			InputStream strIS = strConnection.getInputStream();
			BufferedReader strBR = new BufferedReader(new InputStreamReader(
					strIS));
			String strline;

			while ((strline = strBR.readLine()) != null) {
				strResponse.append(strline);
				strResponse.append('\r');
			}
			strBR.close();

		} catch (IOException e) {
			throw new IOException("HTTP POST failed");
		}
		return strResponse.toString();
	}

	public static String GetToken(JSONObject credentials,
			JSONObject endpointsAPI) throws IOException,
			org.json.simple.parser.ParseException {
		String tokenIdS = "";
		try {
			String identityService = (String) endpointsAPI
					.get("os-identity-api") + "/tokens";

			HttpURLConnection connect = null;
			URL surl = new URL(identityService);
			connect = (HttpURLConnection) surl.openConnection();
			connect.setRequestMethod("POST");
			connect.setRequestProperty("Content-Type", "application/json");
			connect.setRequestProperty("Content-Length", "2000");
			connect.setRequestProperty("Content-Language", "en-US");
			connect.setUseCaches(false);
			connect.setDoInput(true);
			connect.setDoOutput(true);

			if (credentials != null) {
				DataOutputStream wrs = new DataOutputStream(
						connect.getOutputStream());
				wrs.writeBytes(credentials.toJSONString());
				wrs.flush();
				wrs.close();
			}
			InputStream isS = connect.getInputStream();
			BufferedReader rdS = new BufferedReader(new InputStreamReader(isS));
			String lineS;
			StringBuffer responseS = new StringBuffer();

			while ((lineS = rdS.readLine()) != null) {
				responseS.append(lineS);
				responseS.append('\r');
			}

			JSONObject jsonObjectS = (JSONObject) JSONValue
					.parseWithException(responseS.toString());
			JSONObject accessS = (JSONObject) jsonObjectS.get("access");
			JSONObject tokenS = (JSONObject) accessS.get("token");
			Object idS = tokenS.get("id");
			tokenIdS = idS.toString();
		} catch (IOException e) {
			throw new IOException("Error in generating tokens");
		}

		return tokenIdS;
	}

	public static String GetImageId(JSONObject credentials, String token,
			String imgName) throws IOException {
		JSONObject auth = (JSONObject) credentials.get("auth");
		String tenantId = (String) auth.get("tenantId");

		String url = "http://129.69.209.127:8774/v2/" + tenantId + "/images";
		String imageURL = ""; // {"ubuntu", "12.04"};

		try {
			String response = GetMethod(url, token);
			JSONObject jsonObj = (JSONObject) JSONValue
					.parseWithException(response.toString());
			JSONArray images = (JSONArray) jsonObj.get("images");
			System.out.println("images" + images.toJSONString());
			String sUrl = "";

			for (Iterator i = images.iterator(); i.hasNext();) {
				JSONObject it = (JSONObject) i.next();
				String name = (String) it.get("name");
				if (name.equalsIgnoreCase(imgName)) {
					sUrl = (String) it.get("id");
					break;
				}
			}
			imageURL = sUrl.toString();
			System.out.println("imageURL:" + imageURL);
		}

		catch (org.json.simple.parser.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return imageURL;
	}

	public static String GetInstanceType(JSONObject credentials, String token,
			String minDisk, String minRAM) throws IOException {
		JSONObject auth = (JSONObject) credentials.get("auth");
		String tenantId = (String) auth.get("tenantId");
		String url = "http://129.69.209.127:8774/v2/" + tenantId
				+ "/flavors?minDisk=" + minDisk + "&minRam=" + minRAM;
		String instanceType = "";

		try {
			String response = GetMethod(url, token);
			JSONObject jsonObj = (JSONObject) JSONValue
					.parseWithException(response.toString());
			JSONArray flavors = (JSONArray) jsonObj.get("flavors");

			JSONObject ft = (JSONObject) flavors.get(0);
			instanceType = (String) ft.get("id");
			/*
			 * JSONArray links = (JSONArray) ft.get("links"); JSONObject href =
			 * (JSONObject) links.get(0); Object urlhref = href.get("href");
			 * instanceType = urlhref.toString();
			 */
		} catch (org.json.simple.parser.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("flavorURL:" + instanceType.toString());
		return instanceType;

	}
}