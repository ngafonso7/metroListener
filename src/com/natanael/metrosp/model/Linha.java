package com.natanael.metrosp.model;

public class Linha implements Comparable{

	private int id;
	private String nome;
	private String icone;
	private String descricaoFalha;
	private int status;
	
	private boolean isLoading;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getIcone() {
		return icone;
	}
	public void setIcone(String icone) {
		this.icone = icone;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getDescricaoFalha() {
		return descricaoFalha;
	}
	public void setDescricaoFalha(String descricaoFalha) {
		this.descricaoFalha = descricaoFalha;
	}
	public boolean isLoading() {
		return isLoading;
	}
	public void setLoading(boolean isLoading) {
		this.isLoading = isLoading;
	}
	@Override
	public int compareTo(Object arg0) {
		int id = this.id;
		int nextId = ((Linha) arg0).getId();
		return id - nextId;
	}
	
}
