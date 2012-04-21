package org.thecodebakers.games.ataqueformigas;

import java.util.UUID;

import android.graphics.Color;

public class Formiga {
	public String id;
	public int cor;
	public boolean esmagada;
	public float xIni;
	public float yIni;
	public float largura;
	public float altura;
	public int aceleracao;
	public float xLimite;
	public boolean togleBitmap = false;
	public boolean ativa = false;
	
	public Formiga() {
		id = UUID.randomUUID().toString();
	}
	
	public void atualizar() {
		this.xIni += aceleracao;
		if (xIni >= xLimite) {
			ativa = false;
			return;
		}
		togleBitmap = !togleBitmap;

	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		return this.id.equals(((Formiga) o).id);
	}
	
	
	
}
