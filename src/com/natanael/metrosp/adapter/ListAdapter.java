package com.natanael.metrosp.adapter;

import java.util.List;

import com.natanael.metrosp.R;
import com.natanael.metrosp.model.Linha;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ListAdapter extends ArrayAdapter<Linha> {
	
	public ListAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
	}
	
	public ListAdapter(Context context, int resource, List<Linha> items) {
		super(context, resource, items);
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
			
			nome.setText(linha.getNome());
			
			if ("azul".equals(linha.getIcone())) {
				icone.setImageResource(R.drawable.azul);
			}
			else if ("vermelha".equals(linha.getIcone())) {
				icone.setImageResource(R.drawable.vermelha);
			}
			else if ("verde".equals(linha.getIcone())) {
				icone.setImageResource(R.drawable.verde);
			}
			else if ("amarela".equals(linha.getIcone())) {
				icone.setImageResource(R.drawable.amarela);
			}
			else if ("lilas".equals(linha.getIcone())) {
				icone.setImageResource(R.drawable.lilas);
			}
			else {
				icone.setImageResource(R.drawable.metro);
			}
			
			if (linha.getStatus() == -1){
				status.setImageResource(R.drawable.statusvermelho);
			} else if (linha.getStatus() == 0){
				status.setImageResource(R.drawable.statusamarelo);
			} else if (linha.getStatus() == 1){
				status.setImageResource(R.drawable.statusverde);
			} else {
				status.setImageResource(R.drawable.statuserro);
			}
			
		}
		
		return v;
		
	}

}
