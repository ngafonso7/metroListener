package com.natanael.metrosp.adapter;

import java.util.List;

import com.natanael.metrosp.R;
import com.natanael.metrosp.activity.Main;
import com.natanael.metrosp.model.Linha;
import com.natanael.metrosp.network.Constantes;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ListAdapter extends ArrayAdapter<Linha> {
	
	private static View selectedView;
	
	
	public ListAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
	}
	
	public ListAdapter(Context context, int resource, List<Linha> items) {
		super(context, resource, items);
	}
	
	public static View getSelectedView(){
		return selectedView;
	}
	
	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View v = convertView;
		
		if (v == null) {
			LayoutInflater inflater = LayoutInflater.from(getContext());
			v = inflater.inflate(R.layout.itemlistalinhas, null);
		}
		
		Linha linha = getItem(position);
		
		if (linha != null) {
			
			ImageView icone = (ImageView) v.findViewById(R.id.icone);
			TextView nome = (TextView) v.findViewById(R.id.nome);
			ImageView status = (ImageView) v.findViewById(R.id.status);
			ProgressBar loadingBar = (ProgressBar) v.findViewById(R.id.loadingBar);
			
			v.setTag(position);
			if (linha.getId() != -1) {
				nome.setText("Linha " + String.valueOf(linha.getId()) + " - " + linha.getNome());
			} else {
				nome.setText(linha.getNome());
			}
			
			if ("azul".equals(linha.getIcone())) {
				icone.setImageResource(R.drawable.azul);
			} else if ("vermelha".equals(linha.getIcone())) {
				icone.setImageResource(R.drawable.vermelha);
			} else if ("verde".equals(linha.getIcone())) {
				icone.setImageResource(R.drawable.verde);
			} else if ("amarela".equals(linha.getIcone())) {
				icone.setImageResource(R.drawable.amarela);
			} else if ("lilas".equals(linha.getIcone())) {
				icone.setImageResource(R.drawable.lilas);
			} else if ("rubi".equals(linha.getIcone())) {
				icone.setImageResource(R.drawable.cptm);
			} else if ("diamante".equals(linha.getIcone())) {
				icone.setImageResource(R.drawable.cptm);
			} else if ("esmeralda".equals(linha.getIcone())) {
				icone.setImageResource(R.drawable.cptm);
			} else if ("turquesa".equals(linha.getIcone())) {
				icone.setImageResource(R.drawable.cptm);
			} else if ("coral".equals(linha.getIcone())) {
				icone.setImageResource(R.drawable.cptm);
			} else if ("safira".equals(linha.getIcone())) {
				icone.setImageResource(R.drawable.cptm);
			} else {
				icone.setImageResource(R.drawable.statuserro);
			}
			
			if(linha.isLoading()){
				status.setVisibility(View.INVISIBLE);
				loadingBar.setVisibility(View.VISIBLE);
			} else {
				if (linha.getStatus() == -1){
					status.setImageResource(R.drawable.statusvermelho);
				} else if (linha.getStatus() == 0){
					status.setImageResource(R.drawable.statusamarelo);
				} else if (linha.getStatus() == 1){
					status.setImageResource(R.drawable.statusverde);
				} else {
					status.setImageResource(R.drawable.statuserro);
				}
				status.setVisibility(View.VISIBLE);
				loadingBar.setVisibility(View.INVISIBLE);
			}
			
		}
		
		v.setOnTouchListener(new View.OnTouchListener() {
			
			private int paddingL = 0;
			private int paddingR = 0;
			private int initialx = 0;
	        private int currentx = 0;
	        private boolean showInformation = false;
	        private boolean removeLine = false;
	        
	        private void clearSelectedView(){
	        	if (selectedView != null) {
	        		selectedView.setPadding(0, selectedView.getPaddingTop(),
	        				0, selectedView.getPaddingBottom());
	        		selectedView.findViewById(R.id.linha).setBackgroundColor(0x00000000);
	        		selectedView = null;
	        		showInformation = false;
	    	        removeLine = false;
	        	}
	    	}
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (((ProgressBar) v.findViewById(R.id.loadingBar)).getVisibility() == View.VISIBLE){
					return false;
				}
				if ( event.getAction() == MotionEvent.ACTION_DOWN)
	            {
	                paddingL = 0;
	                paddingR = 0;
	                initialx = (int) event.getX();
	                currentx = (int) event.getX();
	                selectedView = v;
	            }
	            if ( event.getAction() == MotionEvent.ACTION_MOVE)
	            {
	                currentx = (int) event.getX();
	                int delta = currentx - initialx;
	                if (delta > 0){
	                	paddingL = delta;
	                	paddingR = 0;
	                } else {
	                	paddingL = 0;
	                	paddingR = -delta;
	                }
	            }
	            if (selectedView != null) {
	            	
		            if ( event.getAction() == MotionEvent.ACTION_UP || 
		                 event.getAction() == MotionEvent.ACTION_CANCEL){
		            	if (showInformation){
		            		Intent intent = new Intent(Constantes.INTENT_ACTION_LIST_ITEM_SWIPE);
		            		intent.putExtra("position", (Integer) selectedView.getTag());
		            		intent.putExtra("action", "show");
		            		LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
		            	} else if (removeLine){
		            		Intent intent = new Intent(Constantes.INTENT_ACTION_LIST_ITEM_SWIPE);
		            		intent.putExtra("position", (Integer) selectedView.getTag());
		            		intent.putExtra("action", "remove");
		            		LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
		            	}
		                //selectedView = null;
		                clearSelectedView();
		                return true;
		            }
		            if (paddingL > 150) {
		            	selectedView.findViewById(R.id.linha).setBackgroundColor(0xFF00FF00);
		            	showInformation = true;
		            	//if (padding > 250) {
			            //	showLineInformation(selectedViewPosition);
			            //	clearSelectedView();
			            //    return true;
		            	//}
		            } else if (paddingR > 150) {
		            	selectedView.findViewById(R.id.linha).setBackgroundColor(0xFFFF0000);  
		            	removeLine = true;
		            	//if (padding < -250) {
		            	//	removeLineFromList(selectedViewPosition);
		            	//	clearSelectedView();
			            //    return true;
		            	//}
		            } else {
		            	selectedView.findViewById(R.id.linha).setBackgroundColor(0x00000000);
		            	showInformation = false;
		            	removeLine = false;
		            }
		            if (paddingR <= 250 && paddingL <= 250) {
		            	selectedView.setPadding(paddingL, 
		            			selectedView.getPaddingTop(), paddingR, selectedView.getPaddingBottom());
		            }
		            
		            return true;
				}
				return false;
			}
		});
		
		return v;
		
	}

}
