package com.natanael.metrosp.activity;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONArray;
import org.json.JSONObject;

import com.natanael.metrosp.R;
import com.natanael.metrosp.adapter.ListAdapter;
import com.natanael.metrosp.adapter.ListHiddenLineAdapter;
import com.natanael.metrosp.model.Linha;
import com.natanael.metrosp.network.Connect;
import com.natanael.metrosp.network.Constantes;

import android.R.anim;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends Activity implements OnItemLongClickListener, OnClickListener {
	
	public class MainReceiver extends BroadcastReceiver {
		
		private Context context;
		private MainReceiver mainReceiver = null;
		
		public MainReceiver(Context context) {
			LocalBroadcastManager.getInstance(getApplicationContext())
				.registerReceiver(this, new IntentFilter(Constantes.INTENT_ACTION_LIST_ITEM_SWIPE));
			registerReceiver(this, new IntentFilter(Constantes.INTENT_ACTION_ALARM_UPDATE));
			this.context = Main.this;
		}
		
		public void setContext(Context context){
			this.context = context;
		}
		
		@Override
		public void onReceive(Context context, Intent intent) {
			
			if (Constantes.INTENT_ACTION_LIST_ITEM_SWIPE.equals(intent.getAction())) {
				int position = intent.getIntExtra("position", -1);
				if ("show".equals(intent.getStringExtra("action")) && position != -1) {
					showLineInformation(position);
				} else if ("remove".equals(intent.getStringExtra("action")) && position != -1) {
					removeLineFromList(position);
				}
			} else if (Constantes.INTENT_ACTION_ALARM_UPDATE.equals(intent.getAction())) {
				Toast.makeText(Main.this, "Alarm", Toast.LENGTH_SHORT).show();
				Vibrator v = (Vibrator) this.context.getSystemService(Context.VIBRATOR_SERVICE);
				// Vibrate for 500 milliseconds
				v.vibrate(2000);
				
			}
		}
		
		public void showLineInformation(int position){
			Linha linha = (Linha) listaLinhas.getItemAtPosition(position);
			if (!"".equals(linha.getDescricaoFalha())) {
				new AlertDialog.Builder(context)
				.setTitle(linha.getNome())
				.setMessage(linha.getDescricaoFalha())
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// do nothing, this is just to inform the user
						
					}
				}) 
				.show();
			}
		}
		
		public void removeLineFromList(final int position){
			new AlertDialog.Builder(context)
				.setTitle(linhas.get(position).getNome())
				.setMessage(R.string.confirm_remove_line)
				.setNegativeButton("OK", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						hiddenLines.add(linhas.get(position));
						
						lineHiddenList = lineHiddenList.concat("," 
								+ String.valueOf(linhas.get(position).getId()));
						sharedEditor = sharedPref.edit();
						sharedEditor.putString(Constantes.SHARED_PREF_HIDDEN_LIST,lineHiddenList);
						sharedEditor.commit();
						
						linhas.remove(linhas.get(position));
						updateLista();
						
					}
				}) 
				.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// do nothing, this is just to inform the user
					}
				}) 
				.show();
		}
		
	}

	private static ArrayList<Linha> linhas = new ArrayList<Linha>();
	private static ArrayList<Linha> hiddenLines = new ArrayList<Linha>();
	
	private TextView lastUpdate;
	private ListView listaLinhas;
	private Dialog dialog;
	
	private String lineHiddenList;
	
	private boolean loading;
	private boolean firstLoad = true;;
	
	MainReceiver mainReceiver = null;
	
	SharedPreferences sharedPref ;
	SharedPreferences.Editor sharedEditor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		lastUpdate = (TextView) findViewById(R.id.lastUpdate);
		listaLinhas = (ListView) findViewById(R.id.listLinhas);
		
		
		//sharedPref = getSharedPreferences(Constantes.SHARED_PREF_NAME, Context.MODE_PRIVATE);
		sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		
		this.lineHiddenList = sharedPref.getString(Constantes.SHARED_PREF_HIDDEN_LIST,"-").toString();
		if ("-".equals(this.lineHiddenList)){
			sharedEditor = sharedPref.edit();
			sharedEditor.putString(Constantes.SHARED_PREF_HIDDEN_LIST, "1,2,3,4,5,6,7,8,9,10,11,12");
			sharedEditor.commit();
			this.lineHiddenList = sharedPref.getString(Constantes.SHARED_PREF_HIDDEN_LIST,"-");
		}
		
		listaLinhas.setAdapter(new ListAdapter(Main.this,R.layout.itemlistalinhas,linhas));
		
		//Load the Lines's status from Network
		//loadStatus();
		
		loadLastUpdate();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_add_line:
				if(!loading && hiddenLines.size() > 0){
					dialog = new Dialog(this);
					dialog.setContentView(R.layout.listddlines);

					Collections.sort(hiddenLines);
					
					ListView listHiddenLines = (ListView) dialog.findViewById(R.id.listhiddenlines);
					listHiddenLines.setAdapter(new ListHiddenLineAdapter(this,R.layout.itemlisthiddenline ,hiddenLines));
					listHiddenLines.setOnItemLongClickListener(Main.this);
					Button cancelButton = (Button) dialog.findViewById(R.id.cancelButton);
					cancelButton.setOnClickListener(Main.this);
					
					dialog.setCancelable(true);
					dialog.setTitle("Selecione uma Linha: ");
					dialog.show();
						
				} else if (firstLoad){
					Toast.makeText(this, R.string.first_load_line, Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(this, R.string.no_hidden_line, Toast.LENGTH_LONG).show();
				}
				return true;
			case R.id.action_refresh:
				if(!loading) {
					//Load the Lines's status from Network
					loadStatus();
				}
				return true;
			case R.id.action_settings:
				Intent settingsMenu = new Intent(this, Settings.class);
				startActivityForResult(settingsMenu,1);
				
				return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	
	@Override
	protected void onPause() {
		try{
			LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mainReceiver);
			unregisterReceiver(mainReceiver);
		} catch (Exception e) {
			
		}
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		mainReceiver = new MainReceiver(Main.this);
		super.onResume();
	}

	private void loadStatus(){
		
		loading = true;
		firstLoad = false;
 
		if (linhas.size()>0){
			for(Linha linha : linhas){
				linha.setLoading(true);
				if (isLineHidden(linha.getId())){
					linha.setStatus(-3);
				}
			}
		} else {
			Linha linha = new Linha();
			linha.setId(-1);
			linha.setNome(getResources().getString(R.string.list_empty));
			linha.setLoading(true);
			linhas.add(linha);
			linha = null;
		}
		
		((ListAdapter) listaLinhas.getAdapter()).notifyDataSetChanged();
		
		if(isOnline()) {
		
			new AsyncTask<Void, Void, Void>() {
	
				@Override
				protected Void doInBackground(Void... params) {
					try{
						Log.d("[MetroSP]","Atualizando informações de linha");

						int idLinha = -1;
						int statusLinha = -2;
						String nomeLinha = "";
						String statusLinhaString = "";
						String descricaoFalhaLinha = "";
						
						String info = new Connect(Constantes.ID_URL_CPTM).getHtml();
						
						if (TextUtils.isEmpty(info)){
							Message msg = Message.obtain();
							msg.what = Constantes.HANDLER_ERROR_CONNECTION_NULL_RESPONSE;
							handler.sendMessage(msg);
							return null;
						}
						JSONArray jsonArray = new JSONArray(info);
						JSONObject jsonObj = null;
						
						for(int i = 0; i < jsonArray.length(); i++){
							jsonObj = jsonArray.getJSONObject(i);
							
							idLinha = jsonObj.getInt(Constantes.TAG_LINHA_ID);
							statusLinhaString = jsonObj.getString(Constantes.TAG_LINHA_STATUS);
							nomeLinha = jsonObj.getString(Constantes.TAG_LINHA_NOME);
							nomeLinha = nomeLinha.substring(0,1).toUpperCase() + nomeLinha.substring(1).toLowerCase();
							
							if ("Operação Normal".equals(statusLinhaString)) {
								statusLinha = 1;
								descricaoFalhaLinha = statusLinhaString;
							} else if ("Velocidade Reduzida".equals(statusLinhaString)){
								statusLinha = 0; 
								descricaoFalhaLinha = jsonObj.getString(Constantes.TAG_LINHA_DESCRICAO);
							} else if ("vermelho".equals(statusLinhaString)){
								statusLinha = -1; 
								descricaoFalhaLinha = jsonObj.getString(Constantes.TAG_LINHA_DESCRICAO);
							}
							updateStatusLinha(idLinha,nomeLinha, statusLinha,descricaoFalhaLinha);
							idLinha = -1;
							nomeLinha = "";
							statusLinhaString = "";
							descricaoFalhaLinha = "";
						}

					} catch (ConnectTimeoutException e){
						Log.d("[MetroSP]", "Erro ao estabelecer Conexão");
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
		} else {
			Message msg = Message.obtain();
			msg.what = Constantes.HANDLER_ERROR_NO_CONNECTION_ACTIVE;
			handler.sendMessage(msg);
		}
	}
	
	private synchronized void updateStatusLinha(int id, String nome, int status, String descricao){
		
		ArrayList<Linha> linhasLocal = null;
		ArrayList<Linha> linhasRemove = null;
		
		if (isLineHidden(id)){
			linhasLocal = hiddenLines;
			linhasRemove = linhas;
		} else {
			linhasLocal = linhas;
			linhasRemove = hiddenLines;
		}
		
		for(Linha linha : linhasLocal){
			if(linha.getId() == id) {
				linha.setStatus(status);
				linha.setDescricaoFalha(descricao);
				return;
			}
		}
		
		for(Linha linha : linhasRemove){
			if(linha.getId() == id) {
				linhasRemove.remove(linha);
				return;
			}
		}
		
		Linha linha = new Linha();
		linha.setId(id);
		//linha.setNome("Linha " + String.valueOf(id) + " - " + nome);
		linha.setNome(nome);
		linha.setStatus(status);
		linha.setDescricaoFalha(descricao);
		linha.setIcone(Normalizer.normalize(nome.toLowerCase(),Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", ""));
		linha.setLoading(false);
		
		linhasLocal.add(linha);
	}
	
	private Handler handler = new Handler(Looper.getMainLooper()) {
		public void handleMessage(Message msg) {
			if (msg.what == Constantes.HANDLER_UPDATE_LIST){

				Time now = new Time();
				now.setToNow();
				sharedEditor = sharedPref.edit();
				sharedEditor.putLong(Constantes.SHARED_TIME_LAST_UPDATE, now.toMillis(true));
				sharedEditor.commit();
				
				lastUpdate.setText(getResources().getString(R.string.last_update_time) + " menos de 1 minuto");
				
			} else if (msg.what == Constantes.HANDLER_ERROR_TIMEOUT_CONNECTION){
				Toast.makeText(getApplicationContext(), R.string.handler_error_timeout_connection, Toast.LENGTH_SHORT).show();
				showUpdateTime();
			} else if (msg.what == Constantes.HANDLER_ERROR_CONNECTION_NULL_RESPONSE){
				Toast.makeText(getApplicationContext(), R.string.handler_error_connection_null_response, Toast.LENGTH_SHORT).show();
				showUpdateTime();
			} else if (msg.what == Constantes.HANDLER_ERROR_NO_CONNECTION_ACTIVE){
				Toast.makeText(getApplicationContext(), R.string.handler_error_no_connection_active, Toast.LENGTH_SHORT).show();
				showUpdateTime();
			}
			loading = false;
			
			updateLista();
			
		};
	};
	
	private void showUpdateTime(){
		Time now = new Time();
		now.setToNow();
		
		long milis = sharedPref.getLong(Constantes.SHARED_TIME_LAST_UPDATE, 0);
		
		if (milis == 0){
			lastUpdate.setText(getResources().getString(R.string.last_update_time) + " menos de 1 minuto");
		} else {
			
			int deltaTime = (int) (now.toMillis(true) - milis)/60000;
			if (deltaTime == 0){
				lastUpdate.setText(getResources().getString(R.string.last_update_time) + " menos de 1 minuto");
			} else if (deltaTime < 60){
				lastUpdate.setText(getResources().getString(R.string.last_update_time) + " " + String.valueOf(deltaTime) + " minuto(s)");
			} else {
				int hour = deltaTime/60;
				deltaTime -= hour*60;
				lastUpdate.setText(getResources().getString(R.string.last_update_time) + " " + String.valueOf(hour) + " hora(s)");
			}
		}
	}
	
	private void updateLista(){
		Linha toBeRemoved = null;
		String lineIds = "";
		String lineNames = "";
		String lineStatus = "";
		String lineDescr = "";
		if (linhas.size() > 0) {
			Collections.sort(linhas);
			for(Linha linha : linhas){
				if (linha.getId() == -1 && linhas.size() > 1) {
					toBeRemoved = linha;
				} else {
					lineIds = lineIds.concat("," + linha.getId());
					lineNames = lineNames.concat("," + linha.getNome());
					lineStatus = lineStatus.concat("," + linha.getStatus());
					lineDescr = lineDescr.concat("," + linha.getDescricaoFalha());
				}
				linha.setLoading(false);
			}
			lineIds = lineIds.substring(1);
			lineNames = lineNames.substring(1);
			lineStatus = lineStatus.substring(1);
			lineDescr = lineDescr.substring(1);
		} else {
			Linha linha = new Linha();
			linha.setId(-1);
			linha.setNome(getResources().getString(R.string.list_empty));
			linhas.add(linha);
			linha = null;
		}
		if (toBeRemoved != null) {
			linhas.remove(toBeRemoved);
		}
		((ListAdapter) listaLinhas.getAdapter()).notifyDataSetChanged();
		
		sharedEditor = sharedPref.edit();
		sharedEditor.putString(Constantes.SHARED_ARRAY_LINE_ID, lineIds);
		sharedEditor.putString(Constantes.SHARED_ARRAY_LINE_NAME, lineNames);
		sharedEditor.putString(Constantes.SHARED_ARRAY_LINE_STATUS, lineStatus);
		sharedEditor.putString(Constantes.SHARED_ARRAY_LINE_DESCR, lineDescr);
		sharedEditor.commit();
	}
	
	private boolean isLineHidden(int id){
		
		if (this.lineHiddenList != null) {
			for (String l : this.lineHiddenList.split(",")) {
				if (l.equals(String.valueOf(id))){
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean isOnline(){
		ConnectivityManager cm =
		        (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    return netInfo != null && netInfo.isConnectedOrConnecting();
	}
	
	private void loadLastUpdate() {
		String lineIds = "";
		String lineNames = "";
		String lineStatus = "";
		String lineDescr = "";
		
		lineIds = sharedPref.getString(Constantes.SHARED_ARRAY_LINE_ID, "");
		
		if (!"".equals(lineIds)) {
			
			lineNames = sharedPref.getString(Constantes.SHARED_ARRAY_LINE_NAME, "");
			lineStatus = sharedPref.getString(Constantes.SHARED_ARRAY_LINE_STATUS, "");
			lineDescr = sharedPref.getString(Constantes.SHARED_ARRAY_LINE_DESCR, "");
			
			String[] ids = lineIds.split(",") ;
			String[] names = lineNames.split(",") ;
			String[] status = lineStatus.split(",") ;
			String[] descr = lineDescr.split(",") ;
			
			linhas.clear();
			
			for (int i=0; i<ids.length; i++) {
				Linha linha = new Linha();
				linha.setId(Integer.parseInt(ids[i]));
				linha.setNome(names[i]);
				linha.setStatus(Integer.parseInt(status[i]));
				linha.setDescricaoFalha(descr[i]);
				linha.setIcone(Normalizer.normalize(names[i].toLowerCase(),Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", ""));
				
				linhas.add(linha);
			}
		}
		showUpdateTime();
		updateLista();
	}
	
	private Calendar getFullCalendar(){
		Calendar defaultDay = Calendar.getInstance();
		int day = defaultDay.get(Calendar.DAY_OF_MONTH);
		int month = defaultDay.get(Calendar.MONTH);
		int year = defaultDay.get(Calendar.YEAR);
		int hour = defaultDay.get(Calendar.HOUR_OF_DAY);
		int minute = defaultDay.get(Calendar.MINUTE);
		int second = defaultDay.get(Calendar.SECOND);
		Calendar cal =  new GregorianCalendar(year, month, day);
		cal.add(Calendar.HOUR_OF_DAY, hour);
		cal.add(Calendar.MINUTE, minute);
		cal.add(Calendar.SECOND, second);
		return cal;
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		if (parent.getId() == R.id.listhiddenlines) {
			Linha linha = hiddenLines.get(position);
			linhas.add(linha);
			hiddenLines.remove(position);
			if(dialog != null){
				dialog.dismiss();
			}
			String newLineHiddenList = "";
			for (String l : lineHiddenList.split(",")){
				String ids = String.valueOf(linha.getId()); 
				if (!ids.equals(l)) {
					newLineHiddenList = newLineHiddenList.concat(","+l);
				}
			}
			if (!"".equals(newLineHiddenList)) { 
				lineHiddenList = newLineHiddenList.substring(1);
			}
			sharedEditor = sharedPref.edit();
			sharedEditor.putString(Constantes.SHARED_PREF_HIDDEN_LIST, lineHiddenList);
			sharedEditor.commit();
			updateLista();
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.cancelButton) {
			if(dialog != null){
				dialog.dismiss();
			}
		}
		
	}
	
}
