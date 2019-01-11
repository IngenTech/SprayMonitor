package com.wrms.spraymonitor.app;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;

public class CustomHttpClient {
	/** The time it takes for our client to timeout */
	public static final int HTTP_TIMEOUT = 3*60*1000;
	
	 /** Single instance of our HttpClient */
	private static HttpClient myClient;
	
	
	/**
     * Get our single instance of our HttpClient object.
     *
     * @return an HttpClient object with connection parameters set
     */
	private static HttpClient getHttpClient(){
		if(myClient == null){
			myClient = new DefaultHttpClient();
			final HttpParams param= myClient.getParams();
			HttpConnectionParams.setConnectionTimeout(param, HTTP_TIMEOUT);
			HttpConnectionParams.setSoTimeout(param, HTTP_TIMEOUT);
			ConnManagerParams.setTimeout(param, HTTP_TIMEOUT);
		}
		return myClient;
	}
	
	
	 /**
     * Performs an HTTP Post request to the specified url with the
     * specified parameters.
     * @param url The web address to post the request to
     * @param postParameters The parameters to sendRegistrationRequest via the request
     * @return The fuelDataArrayList of the request
     * @throws Exception
     */
	public static String executeHttpPost(String url,ArrayList<NameValuePair> postParameters)throws Exception {
		BufferedReader in = null;
		for(NameValuePair pair : postParameters){
			System.out.println(pair.getName()+ " : "+pair.getValue());
		}
		try{
			HttpClient client = getHttpClient();
			HttpPost request = new HttpPost(url);
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(postParameters,"UTF-8");
//			request.setHeader(HTTP.CONTENT_TYPE,"application/json; charset=utf-8");
			request.setHeader(HTTP.CONTENT_TYPE,"application/x-www-form-urlencoded");
			request.setEntity(formEntity);
			HttpResponse response = client.execute(request);
			in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			StringBuffer sb = new StringBuffer("");
			String line = "";
			String NL = System.getProperty("line.separator");
			 while ((line = in.readLine()) != null) {
	                sb.append(line + NL);
	            }
	            in.close();

	            String result = sb.toString();
	            return result;
			
		} finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
	}


	public static String executeHttpPostJSON(String url,String jsonString)throws Exception {
		BufferedReader in = null;
		try{
			HttpClient client = getHttpClient();
			HttpPost request = new HttpPost(url);
//			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(postParameters,"UTF-8");
			StringEntity se = new StringEntity(jsonString);
			request.setHeader(HTTP.CONTENT_TYPE,"application/json; charset=utf-8");
			request.setEntity(se);
			HttpResponse response = client.execute(request);
			in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			StringBuffer sb = new StringBuffer("");
			String line = "";
			String NL = System.getProperty("line.separator");
			while ((line = in.readLine()) != null) {
				sb.append(line + NL);
			}
			in.close();

			String result = sb.toString();
			return result;

		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Performs an HTTP Post request to the specified url with the
	 * specified parameters.
	 * @param url The web address to post the request to
	 * @return The fuelDataArrayList of the request
	 * @throws Exception
	 */
	public static String executeHttpPut(String url)throws Exception {
		BufferedReader in = null;
		try{
			HttpClient client = getHttpClient();
			HttpPut request = new HttpPut();
//			request.setEntity(new StringEntity(data.getEmailAddress()+"/"+data.getCreateUsername()+"/"+data.getPassword()+"/"+data.getVisibleNamename()));
			request.addHeader("content-type", "application/json");
			request.addHeader("charset", "utf-8");
			request.setURI(new URI(url));
			HttpResponse response = client.execute(request);
			in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			StringBuffer sb = new StringBuffer("");
			String line = "";
			String NL = System.getProperty("line.separator");
			while ((line = in.readLine()) != null) {
				sb.append(line + NL);
			}
			in.close();

			String result = sb.toString();
			return result;

		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	 /**
     * Performs an HTTP GET request to the specified url.
     * @param url The web address to post the request to
     * @return The fuelDataArrayList of the request
     * @throws Exception
     */
	public static String executeHttpGet(String url) throws Exception {
        BufferedReader in = null;
        try {
            HttpClient client = getHttpClient();
            HttpGet request = new HttpGet();
			request.addHeader("content-type", "application/json");
			request.addHeader("charset", "utf-8");
            request.setURI(new URI(url));
            HttpResponse response = client.execute(request);
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            StringBuffer sb = new StringBuffer("");
            String line = "";
            String NL = System.getProperty("line.separator");
            while ((line = in.readLine()) != null) {

				System.out.println("Carecter : "+line);

                sb.append(line + NL);
            }
            in.close();

            String result = sb.toString();
            return result;
        } finally {
            if (in != null) {
                try {
                    in.close();
                    
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
