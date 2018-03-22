package com.refollow.RefollowServerStatus;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Date;
import java.util.Properties;

public class RefollowServerStatus {
	static int responseCode;
	public static String CONFIG_PRO_FILE_PATH = "";

	public static void main(String[] args) throws Exception {
		String slackChannelUrl = null;
		String refollowAppUrl = null;
		String messageLine1 = null;
		String messageLine2 = null;
		String filePath = null;


		try {
			// Command line Argument Pass value Of variables[config.properties file path]
			// when Run jar file on CMD
			CONFIG_PRO_FILE_PATH = args[0];
			Properties prop = loadPropertiesFile();
			slackChannelUrl = prop.getProperty("slackIncomingWebHooksUrl");
			refollowAppUrl = prop.getProperty("refollowAppUrl");
			messageLine1 = prop.getProperty("messageLine1");
			messageLine2 = prop.getProperty("messageLine2");

			URL url = new URL(refollowAppUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();
			responseCode = connection.getResponseCode();
			if (responseCode == 500 || responseCode == 403 || responseCode == 404) {
				sendMessage(slackChannelUrl, refollowAppUrl, messageLine1, messageLine2);
			}
			
		} catch (IOException e) {
			sendMessage(slackChannelUrl, refollowAppUrl, messageLine1, messageLine2);
		}
	}

	public static void sendMessage(String slackChannelUrl, String refollowAppUrl, String messageLine1,
			String messageLine2) {
		URL url = null;
		try {
			url = new URL(slackChannelUrl);
		} catch (MalformedURLException e2) {
			e2.printStackTrace();
		}
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) url.openConnection();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if (conn != null) {
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(5000);
			HttpURLConnection.setFollowRedirects(false);
			try {
				conn.setRequestMethod("POST");
			} catch (ProtocolException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);
			try {
				DataOutputStream out = new DataOutputStream(conn.getOutputStream());
				out.writeBytes(
						"{\"text\": \"" +messageLine1+" ["+ new Date() +"]\n"+ messageLine2+"..\"}");
			} catch (IOException ex) {
				ex.printStackTrace();
			} finally {

			}

			int status = 0;
			String inputLine;
			StringBuffer content = new StringBuffer();

			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				status = conn.getResponseCode();
				while ((inputLine = in.readLine()) != null) {
					content.append(inputLine);
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	// Load PropertiesFile
	public static Properties loadPropertiesFile() throws Exception {
		Properties prop = new Properties();
		// For command line argument when Run Jar File using Eclipse
		prop.load(new FileInputStream(CONFIG_PRO_FILE_PATH));
		/*
		 * prop.load(new FileInputStream(
		 * "/home/punam/Desktop/Refollow_v4_workSpace/RefollowServerStatus/config.properties"
		 * ));
		 */
		return prop;
	}
}

