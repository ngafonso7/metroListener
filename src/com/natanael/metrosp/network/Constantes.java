package com.natanael.metrosp.network;

public final class Constantes {

	public static int ID_URL_METRO = 1;
	public static int ID_URL_CPTM = 2;
	
	public static String TAG_LINHA_ID = "LinhaId";
	public static String TAG_LINHA_STATUS = "Status";
	
	public static int HANDLER_UPDATE_LIST = 1;
	public static int HANDLER_ERROR_TIMEOUT_CONNECTION = 2;
	public static int HANDLER_ERROR_CONNECTION_NULL_RESPONSE = 3;
	
	private static String url_metro = "http://www.metro.sp.gov.br/Sistemas/direto-do-metro-via4/MetroStatusLinha/mobile/smartPhone/diretoDoMetro.aspx";
	private static String url_cptm = "http://apps.cptm.sp.gov.br:8080/AppMobileService/api/LinhasMetropolitanas";
	
	public String getUrl(int url){
		
		switch(url){
			case 1:
				return url_metro;
			case 2:
				return url_cptm;
			default:
				return null;
				
		}
	}
	
}
