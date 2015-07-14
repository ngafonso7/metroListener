package com.natanael.metrosp.network;

public final class Constantes {

	public static int ID_URL_METRO = 1;
	public static int ID_URL_CPTM = 2;
	
	public static String INTENT_ACTION_LIST_ITEM_SWIPE = "listView.action.itemSwipe";
	public static String INTENT_ACTION_ALARM_UPDATE = "update.alarm";
	
	public static String TAG_LINHA_ID = "LinhaId";
	public static String TAG_LINHA_NOME = "Nome";
	public static String TAG_LINHA_STATUS = "Status";
	public static String TAG_LINHA_DESCRICAO = "Descricao";
	
	public static int HANDLER_UPDATE_LIST = 1;
	public static int HANDLER_ERROR_TIMEOUT_CONNECTION = 2;
	public static int HANDLER_ERROR_CONNECTION_NULL_RESPONSE = 3;
	public static int HANDLER_ERROR_NO_CONNECTION_ACTIVE = 4;
	
	private static String url_metro = "http://www.metro.sp.gov.br/Sistemas/direto-do-metro-via4/MetroStatusLinha/mobile/smartPhone/diretoDoMetro.aspx";
	private static String url_cptm = "http://apps.cptm.sp.gov.br:8080/AppMobileService/api/LinhasMetropolitanas";
	
	public static String SHARED_PREF_NAME = "METRO_PREF";
	public static String SHARED_TIME_LAST_UPDATE = "LAST_UPDATE";
	public static String SHARED_PREF_HIDDEN_LIST = "HIDEN_LINE_LIST";
	
	public static String SHARED_ARRAY_LINE_ID = "LINE_IDS";
	public static String SHARED_ARRAY_LINE_NAME = "LINE_NAMES";
	public static String SHARED_ARRAY_LINE_STATUS = "LINE_STATUS";
	public static String SHARED_ARRAY_LINE_DESCR = "LINE_DESCR";
	
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
