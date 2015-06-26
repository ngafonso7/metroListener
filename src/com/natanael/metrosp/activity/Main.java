package com.natanael.metrosp.activity;

import java.io.InputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.natanael.adapter.ListAdapter;
import com.natanael.metrosp.R;
import com.natanael.metrosp.model.Linha;

import android.app.Activity;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

public class Main extends Activity {

	ArrayList<Linha> linhas;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		loadLines();
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
			
			//XmlPullParser parser = 
			//		XmlPullParserFactory.newInstance().newPullParser();
			//InputStream input = getApplicationContext().getAssets().open("@xml/linhas.xml");
			
			XmlResourceParser parser = this.getResources().getXml(R.xml.linhas);

			//parser.setInput(input, null);
			parseXML(parser);
			
			if (linhas.size() > 0) {
				
				ListView listaLinhas = (ListView) findViewById(R.id.listLinhas);
				
				ListAdapter adapter = new ListAdapter(this,R.layout.itemlistalinhas,linhas);
				
				listaLinhas.setAdapter(adapter);
				
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
						if(tag.equals("id")) {
							linha.setId(Integer.parseInt(parser.nextText()));
						}
						else if(tag.equals("nome")) {
							linha.setNome(parser.nextText());
						}
						else if (tag.equals("icone")) {
							linha.setIcone(parser.nextText());
						}
					}
					break;
				case XmlResourceParser.END_TAG:
					if (parser.getName().equalsIgnoreCase("linha") && linha != null) {
						linhas.add(linha);
						linha = null;
					}
					break;
			
			}
			
			eventType = parser.next();
			
		}
		
	}
}
