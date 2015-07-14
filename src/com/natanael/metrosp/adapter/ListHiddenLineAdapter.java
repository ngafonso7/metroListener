package com.natanael.metrosp.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.natanael.metrosp.R;
import com.natanael.metrosp.model.Linha;

public class ListHiddenLineAdapter extends ArrayAdapter<Linha> {
	
	public ListHiddenLineAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
	}
	
	public ListHiddenLineAdapter(Context context, int resource, List<Linha> items) {
		super(context, resource, items);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View v = convertView;
		
		if (v == null) {
			LayoutInflater inflater = LayoutInflater.from(getContext());
			v = inflater.inflate(R.layout.itemlisthiddenline, null);
		}
		
		Linha linha = getItem(position);
		
		if (linha != null) {
			
			ImageView icone = (ImageView) v.findViewById(R.id.icone);
			TextView nome = (TextView) v.findViewById(R.id.nome);
			
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

			
		}
		
		return v;
		
	}

}
