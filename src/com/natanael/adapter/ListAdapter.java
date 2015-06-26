package com.natanael.adapter;

import java.util.List;

import com.natanael.metrosp.R;
import com.natanael.metrosp.model.Linha;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
		}
		
		return v;
		
	}

}
