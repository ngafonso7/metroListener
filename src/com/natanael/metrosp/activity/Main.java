package com.natanael.metrosp.activity;

import java.util.ArrayList;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONArray;
import org.json.JSONObject;

import com.natanael.metrosp.R;
import com.natanael.metrosp.adapter.ListAdapter;
import com.natanael.metrosp.model.Linha;
import com.natanael.metrosp.network.Connect;
import com.natanael.metrosp.network.Constantes;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.XmlResourceParser;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends Activity {

	private static ArrayList<Linha> linhas;
	
	private ProgressBar progressBar ;
	private TextView progressText;
	private ListView listaLinhas;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		progressText = (TextView) findViewById(R.id.progressText);
		
		//Load Lines info from XML resource
		loadLines();
		
		//Load the Lines's status from Network
		loadStatus();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void loadLines(){
		try{
			
			XmlResourceParser parser = this.getResources().getXml(R.xml.linhas);
			parseXML(parser);
			
			if (linhas.size() > 0) {
				listaLinhas = (ListView) findViewById(R.id.listLinhas);
				listaLinhas.setAdapter(new ListAdapter(this,R.layout.itemlistalinhas,linhas));
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void parseXML(XmlResourceParser parser) throws Exception{
		linhas = null;
		Linha linha = null;
		String tag = "";
		int eventType = parser.getEventType();
		while (eventType != XmlResourceParser.END_DOCUMENT) {
			
			switch (eventType){
				case XmlResourceParser.START_DOCUMENT:
					linhas = new ArrayList<Linha>();
					break;
				case XmlResourceParser.START_TAG:
					tag = parser.getName();
					if (tag.equals("linha")) {
						linha = new Linha();
					}
					else if (linha != null) {
						if("id".equals(tag)) {
							linha.setId(Integer.parseInt(parser.nextText()));
						}
						else if("nome".equals(tag)) {
							linha.setNome(parser.nextText());
						}
						else if ("icone".equals(tag)) {
							linha.setIcone(parser.nextText());
						}
					}
					break;
				case XmlResourceParser.END_TAG:
					if (parser.getName().equalsIgnoreCase("linha") && linha != null) {
						linha.setStatus(-2);
						linhas.add(linha);
						linha = null;
					}
					break;
			}
			eventType = parser.next();
			
		}
		
	}
	
	/*private void loadStatus(){
	
		progressBar.setVisibility(0);
		progressText.setVisibility(0);
		
		if(isOnline()) {
		
			new AsyncTask<Void, Void, Void>() {
	
				@Override
				protected Void doInBackground(Void... params) {
					try{
						Log.d("[MetroSP]","Atualizando informações de linha");
						
						boolean linhaDeStatus = false;
						int idLinha = -1;
						int statusLinha = -2;
						int faseParser = 1;
						Matcher matcher = null;
						Pattern p = Pattern.compile("Linha ([1,2,3,4,5])");
						
						Connect connect = new Connect(Constantes.ID_URL_METRO);
						String[] linhas = connect.getHtml().replace("\t", "").split("\n");
						
						for(String l : linhas){
							if (l.startsWith("<li class=") || l.startsWith("<li >")) {
								linhaDeStatus = true;
								p = Pattern.compile("Linha ([1,2,3,4,5])");
							} else if (l.startsWith("</li")) {
								linhaDeStatus = false;
							}
							if (linhaDeStatus) {
								matcher= p.matcher(l);
								if(matcher.find()){
									switch (faseParser){
										case 1:
											Log.d("[MetroSP]","Linha - " + matcher.group(1));
											idLinha = Integer.parseInt(matcher.group(1));
											p = Pattern.compile("images/status_(verde).png");
											faseParser = 2;
											break;
										case 2:
											Log.d("[MetroSP]","Linha - " + String.valueOf(idLinha) + " : Status - " + matcher.group(1));
											if ("verde".equals(matcher.group(1))) {
												statusLinha = 1;
											} else if ("amarelo".equals(matcher.group(1))){
												statusLinha = 0; 
											} else if ("vermelho".equals(matcher.group(1))){
												statusLinha = -1; 
											}
											p = Pattern.compile("Linha ([1,2,3,4,5])");
											faseParser = 1;
											
											updateStatusLinha(idLinha,statusLinha);
											
											linhaDeStatus = false;
											idLinha = -1;
											statusLinha = -2;
											break;
									}
								}
								
							}
						}
					} catch (TimeoutException e){
						Log.d("[MetroSP]","Erro ao estabelecer Conexão");
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					return null;
				}
				
				protected void onPostExecute(Void result) {
					Log.d("[MetroSP]","Atualização de linhas terminado");
					Message msg = Message.obtain();
					msg.what = Constantes.HANDLER_UPDATE_LIST;
					handler.sendMessageDelayed(msg,1000);
				};
				
			}.execute();
		}
		
	}*/
	private void loadStatus(){
	
		progressBar.setVisibility(0);
		progressText.setVisibility(0);
		
		if(isOnline()) {
		
			new AsyncTask<Void, Void, Void>() {
	
				@Override
				protected Void doInBackground(Void... params) {
					try{
						Log.d("[MetroSP]","Atualizando informações de linha");

						int idLinha = -1;
						int statusLinha = -2;
						String statusLinhaString = "";
						
						String linhas = new Connect(Constantes.ID_URL_CPTM).getHtml();
						
						if (TextUtils.isEmpty(linhas)){
							Message msg = Message.obtain();
							msg.what = Constantes.HANDLER_ERROR_CONNECTION_NULL_RESPONSE;
							handler.sendMessage(msg);
							return null;
						}
						JSONArray jsonArray = new JSONArray(linhas);
						JSONObject jsonObj = null;
						
						for(int i = 0; i < jsonArray.length(); i++){
							jsonObj = jsonArray.getJSONObject(i);
							
							idLinha = jsonObj.getInt(Constantes.TAG_LINHA_ID);
							statusLinhaString = jsonObj.getString(Constantes.TAG_LINHA_STATUS);
							
							Log.d("[MetroSP]","Linha - " + String.valueOf(idLinha) + " : Status - " + statusLinhaString);
							
							if ("Operação Normal".equals(statusLinhaString)) {
								statusLinha = 1;
							} else if ("Operação Parcial".equals(statusLinhaString)){
								statusLinha = 0; 
							} else if ("vermelho".equals(statusLinhaString)){
								statusLinha = -1; 
							}
							
							updateStatusLinha(idLinha,statusLinha);
							
						}

					} catch (ConnectTimeoutException e){
						Log.d("[MetroSP]","Erro ao estabelecer Conexão");
						Message msg = Message.obtain();
						msg.what = Constantes.HANDLER_ERROR_TIMEOUT_CONNECTION;
						handler.sendMessage(msg);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					return null;
				}
				
				protected void onPostExecute(Void result) {
					Log.d("[MetroSP]","Atualização de linhas terminado");
					Message msg = Message.obtain();
					msg.what = Constantes.HANDLER_UPDATE_LIST;
					handler.sendMessageDelayed(msg,1000);
				};
				
			}.execute();
		}
		
	}
	
	private synchronized void updateStatusLinha(int id, int status){
		
		for(Linha linha : linhas){
			if(linha.getId() == id) {
				linha.setStatus(status);
				break;
			}
		}
		
	}
	
	private Handler handler = new Handler(Looper.getMainLooper()) {
		public void handleMessage(Message msg) {
			if (msg.what == Constantes.HANDLER_UPDATE_LIST){
				if (linhas.size() > 0) {
					listaLinhas.setAdapter(new ListAdapter(Main.this,R.layout.itemlistalinhas,linhas));
				}
				progressBar.setVisibility(8);
				progressText.setVisibility(8);
				
			} else if (msg.what == Constantes.HANDLER_ERROR_TIMEOUT_CONNECTION){
				Toast.makeText(getApplicationContext(), R.string.handler_error_timeout_connection, Toast.LENGTH_SHORT).show();
				progressBar.setVisibility(8);
				progressText.setVisibility(8);
			} else if (msg.what == Constantes.HANDLER_ERROR_CONNECTION_NULL_RESPONSE){
				Toast.makeText(getApplicationContext(), R.string.handler_error_connection_null_response, Toast.LENGTH_SHORT).show();
				progressBar.setVisibility(8);
				progressText.setVisibility(8);
			}
		};
	};
	
	private boolean isOnline(){
		ConnectivityManager cm =
		        (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    return netInfo != null && netInfo.isConnectedOrConnecting();
	}
}
