package com.natanael.metrosp.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class Connect {

	private static HttpClient httpclient;
	private static HttpGet httpget;
	private static HttpResponse response;
	private static HttpEntity entity;
	
	public Connect(int url) throws Exception{
		
		String urlRequest = new Constantes().getUrl(url);
		
		HttpParams httpParams= new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
		HttpConnectionParams.setSoTimeout(httpParams, 15000);
		
		httpclient = new DefaultHttpClient(httpParams);
		httpget = new HttpGet(urlRequest);
		
		response = httpclient.execute(httpget);
		Log.i("[MetroSP]",response.getStatusLine().toString());
		entity = response.getEntity();

	}
	
	public String getHtml(){
		String result = "";
		
		try{
			if(entity != null)
			{
				InputStream input = entity.getContent();
				result = convertStreamToString(input);
			}
		} catch (IOException e){
			e.printStackTrace();
		}
		
		return result;
		
	}
	
	private static String convertStreamToString(InputStream is) {
	    /*
	     * To convert the InputStream to String we use the BufferedReader.readLine()
	     * method. We iterate until the BufferedReader return null which means
	     * there's no more data to read. Each line will appended to a StringBuilder
	     * and returned as String.
	     */
	    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	    StringBuilder sb = new StringBuilder();

	    String line = null;
	    try {
	        while ((line = reader.readLine()) != null) {
	            sb.append(line + "\n");
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            is.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	    
	    return sb.toString();
	}
	
	
	
}
