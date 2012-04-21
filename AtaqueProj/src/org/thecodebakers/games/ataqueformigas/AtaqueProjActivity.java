/*
 * Copyright (C) 2012 The Code Bakers
 * Authors: Cleuton Sampaio e Francisco Rodrigues
 * e-mail: thecodebakers@gmail.com
 * Project: http://code.google.com/p/fisica-videogame/
 * Site: http://www.thecodebakers.org
 *
 * Licensed under the Apache, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * @author Cleuton Sampaio e Francisco Rogrigues - thecodebakers@gmail.com
 */
package org.thecodebakers.games.ataqueformigas;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.BitmapFactory.Options;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

public class AtaqueProjActivity extends Activity {
	
	private AtaqueProjActivity actReference = this;
	private Bola bola = new Bola();
	private Bitmap fundo; 
	private Bitmap bitmap;
	private Bitmap mancha;
	private Bitmap formigaVerde1;
	private Bitmap formigaVerde2;
	private Bitmap formigaVermelha1;
	private Bitmap formigaVermelha2;
	private Bitmap formigaAmarela1;
	private Bitmap formigaAmarela2;
	private VerletView vview;
	private Executor executor;
	private boolean gameLoopRodando = false;
	private boolean gamePaused = false;
	private boolean gameEnd = false;
	private Formiga [] formigas = new Formiga[30];
	private Random sorteio;
	MediaPlayer media;
	MediaPlayer media2;
	MediaPlayer media3;
	private String tempo = "00:00:00";
	private String pontos = "0";
	private int pontosTotais = 0;
	private int amarelas = 0;
	private int verdes = 0;
	private int vermelhas = 0;
	Calendar cal;
	Calendar cal3;

	// Constantes
	int LARGURA = 320;
	int ALTURA = 480;
	int TAMBOLA = 50;
	float XLIMITE = 150f;
	float XINI = -30;
	float YINI = 400;
	float PISO = 370.0f;
	float MEIO = 130.0f;
	
	// Eventos importantes da activity:
	
	@Override
	protected void onPause() {
		super.onPause();
		gamePaused = true;
		gameLoopRodando = false;
		if (executor != null && executor.isAlive()) {
			executor.interrupt();
		}
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		if (gamePaused) {
			gamePaused = false;
			cal = Calendar.getInstance();
    		cal.add(Calendar.MINUTE, -1 * cal3.get(Calendar.MINUTE));
    		cal.add(Calendar.SECOND, -1 * cal3.get(Calendar.SECOND));
    		Calendar cal2 = Calendar.getInstance();
    		cal2.setTime(new Date());
    		long milis = cal2.getTimeInMillis() - cal.getTimeInMillis();
    		cal3.setTimeInMillis(milis);
        	executor = new Executor();
           	try {
           		gameLoopRodando = true;
           		bola.reset();
           		bola.parada = false;
            	executor.start();
           	}
           	catch(IllegalThreadStateException i) {   
           		gameLoopRodando = false;
           		Log.d("onResume", "Exception no Thread: " + i.getMessage());
           	}  
		}
	}




	/*
	 * Esta classe representa uma bola
	 * Podemos criar mais de uma...
	 */
	class Bola {
		double altura;
		double alturaAnterior;
		double alturaLimite;
		double limiteSuperior;
		double aceleracao;
		double velocidade;
		double dt;
		double forcaGrav;
		double massa;
		boolean parada = true;
		boolean descendo = true;
		double e = 0.50; // Coeficiente de restituição (pode variar)
		float x;
		float y;
		
		
		
		Bola() {
			reset();
		}
		
		void reset() {
			 limiteSuperior		= PISO;
			 altura 			= limiteSuperior;
			 alturaAnterior 	= altura;
			 alturaLimite		= 0;
			 aceleracao 		= 0.0d;
			 velocidade 		= 0.0d;
			 dt         		= 01.d;
			 forcaGrav 			= -9.8d;
			 massa            	= 1.0d;
			 descendo			= true;
		}
		
		void atualizar() {

			altura = altura + velocidade * dt + 
    				  (aceleracao * Math.pow(dt, 2)) / 2.0d;
	    	double vel2 = velocidade + (aceleracao * dt) /2.0d;
	    	double aceleracao = forcaGrav / massa ;
	    	velocidade = vel2 + (aceleracao * dt) / 2.0d;

		}
	}
	
	/*
	 * Esta classe representa a view que será apresentada
	 * pela activity
	 */
	class VerletView extends View {

		public VerletView(Context context) {
			super(context);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			
			if (!gameLoopRodando) {
				redimensionar(this);
			}
			
			canvas.drawBitmap(fundo, 0, 0, null);
			Paint paint = new Paint();
			paint.setColor(Color.BLACK);
			paint.setTypeface(Typeface.DEFAULT_BOLD);
			paint.setTextSize(TAMBOLA / 2);
			canvas.drawText(tempo, 0, paint.getTextSize() + 3, paint);
			canvas.drawBitmap(formigaAmarela1, 0, bitmap.getHeight(), null);
			canvas.drawText(Integer.toString(amarelas), formigaAmarela1.getWidth() + (formigaAmarela1.getWidth() * 0.2f), bitmap.getHeight() , paint);
			canvas.drawBitmap(formigaVerde1, 0, bitmap.getHeight() * 2 , null);
			canvas.drawText(Integer.toString(verdes), formigaAmarela1.getWidth() + (formigaAmarela1.getWidth() * 0.2f), bitmap.getHeight() * 2 , paint);
			canvas.drawBitmap(formigaVermelha1, 0, bitmap.getHeight() * 3, null);
			canvas.drawText(Integer.toString(vermelhas), formigaAmarela1.getWidth() + (formigaAmarela1.getWidth() * 0.2f), bitmap.getHeight() * 3 , paint);
			pontos = Integer.toString(pontosTotais);
			canvas.drawText(pontos, 0, bitmap.getHeight() * 4.5f, paint);
			
			if (gameEnd) {
				String mensagem = getResources().getString(R.string.restart);
				canvas.drawText(mensagem, 0, bitmap.getHeight() * 4, paint);
				return;
			}
			else {
				String mensagem = getResources().getString(R.string.inicio);
				canvas.drawText(mensagem, 0, bitmap.getHeight() * 4, paint);			
			}
			
			
			bola.atualizar();

			
			if (bola.altura <= 0) {
				// A bola bateu no chão
				bola.altura = 0;
				bola.x = MEIO;
				bola.y = (float)(PISO - bola.altura);
				canvas.drawBitmap(bitmap, bola.x, bola.y, null);
				if (!bola.parada) {
					verificaColisao(canvas);
				}
				
				
				double novaAltura = Math.pow(bola.e, 2) * bola.alturaAnterior;
				bola.velocidade = Math.sqrt(2 * (-bola.forcaGrav) * novaAltura);
				bola.alturaAnterior = novaAltura;

				if (novaAltura < (formigaVerde1.getHeight() /2)) {
					bola.parada = true;
				}


			}
			else {
				// A bola ainda está caindo
				canvas.drawBitmap(bitmap, MEIO, (float)(PISO - bola.altura), null);
				bola.x = MEIO;
				bola.y = (float)(PISO - bola.altura);
			}

			for (int x = 0; x < formigas.length; x++) {
				if (formigas[x] != null && formigas[x].ativa) {
					Bitmap bmap = null;
					switch(formigas[x].cor) {
					case Color.GREEN:
						bmap = (formigas[x].togleBitmap) ? formigaVerde1 : formigaVerde2;
						break;
					case Color.RED:
						bmap = (formigas[x].togleBitmap) ? formigaVermelha1 : formigaVermelha2;
						break;
					case Color.YELLOW:
						bmap = (formigas[x].togleBitmap) ? formigaAmarela1 : formigaAmarela2;
						break;
					}
					canvas.drawBitmap(bmap, formigas[x].xIni, formigas[x].yIni, null);
				}
			}
		}

		private void verificaColisao(Canvas canvas) {
			//(int left, int top, int right, int bottom)
			Rect rBola = new Rect(Math.round(bola.x), Math.round(bola.y), Math.round(bola.x + bitmap.getWidth()), Math.round(bola.y + bitmap.getHeight()));
			boolean acertou = false;
			for (int x = 0; x < formigas.length; x++) {
				if (formigas[x] != null && formigas[x].ativa) {
					Rect rFormiga = new Rect(Math.round(formigas[x].xIni), Math.round(formigas[x].yIni), 
							Math.round(formigas[x].xIni + formigaVerde1.getWidth()), Math.round(formigas[x].yIni + formigaVerde1.getHeight()));
					if (rBola.intersect(rFormiga)) {
						acertou = true;
						pontos(x);
						mancha(canvas,x);
					}
				}
			}
			if (acertou) {
				media.start();
			}
		}

		private void mancha(Canvas canvas,int x) {
			canvas.drawBitmap(mancha, formigas[x].xIni, formigas[x].yIni, null);
			formigas[x].esmagada = true;
			formigas[x].ativa = false;
			
		}

		private void pontos(int x) {

			
			switch(formigas[x].cor) {
			case Color.YELLOW:
				pontosTotais += 1;
				amarelas++;
				break;
			case Color.GREEN:
				pontosTotais += 3;
				verdes++;
				break;
			case Color.RED:
				pontosTotais += 5;
				vermelhas++;
			}
		}
		

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		

        
        vview = new VerletView(this.getApplicationContext());
        this.setContentView(vview);
        
		media = MediaPlayer.create(this, R.raw.cork_pop);
		media2 = MediaPlayer.create(this, R.raw.boing_1);
		media3 = MediaPlayer.create(this, R.raw.laser_1);
		sorteio = new Random();
		
		
		Calendar ref = Calendar.getInstance();
		ref.set(Calendar.MINUTE, 5);
		ref.set(Calendar.SECOND, 00);
		
	}
	/*
	 * Este é o Thread que comanda a animação
	 */
	class Executor extends Thread {
		public void run() {
			bola.reset();
			bola.atualizar();
			int i = 0;
            do {
            	i++;
            	try {
					sleep(30); 
					
					if (sorteio.nextInt(60) < 3) {
						adicionar(novaFormiga());
					}
					atualizarFormigas();
					Calendar cal2 = Calendar.getInstance();
					long milis = cal2.getTimeInMillis() - cal.getTimeInMillis();

					cal3 = Calendar.getInstance();

					cal3.setTimeInMillis(milis);
					

					tempo = String.format("00:%1$tM:%1$tS", cal3);
					vview.postInvalidate();
					
					// Tempo
					
					// Calcula o tempo decorrido


					
					if (cal3.get(Calendar.MINUTE) >= 3) {
						gameLoopRodando = false;
					}
					
				} catch (InterruptedException e) {
					gameLoopRodando = false;
				}
            	
            } while (gameLoopRodando);
            if (!gamePaused) {
            	media3.start();
            	gameEnd = true;
            	vview.postInvalidate();
            }   
		}

		private void adicionar(Formiga novaFormiga) {
			
			// Só adiciona se tiver espaço (30 formigas)

			for (int x = 0; x < formigas.length; x++) {
				if (formigas[x] == null || !formigas[x].ativa) {
					formigas[x] = novaFormiga;
					break;
				}
			}
		}

		private void atualizarFormigas() {
			for (int x = 0; x < formigas.length; x++) {
				if (formigas[x] != null && formigas[x].ativa) {
					if (sorteio.nextInt(5) > 3 ) {
						formigas[x].aceleracao += sorteio.nextInt(4);
					}
					formigas[x].atualizar();
				}
			}
		}

	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (gameEnd) {
			return true;
		}
    	if (!gameLoopRodando) {
    		//media2.start();
    		cal = Calendar.getInstance();
    		pontosTotais = 0;
    		pontos = "0";
    		amarelas = 0;
    		verdes = 0;
    		vermelhas = 0;
        	executor = new Executor();
           	try {
            	gameLoopRodando = true;
           		bola.reset();
           		bola.parada = false;
            	executor.start();
           	}
           	catch(IllegalThreadStateException i) {
           		gameLoopRodando = false;
           		Log.d("onTouch", "Exception no Thread: " + i.getMessage());
           	}        		
    	}
    	else {
    		bola.reset();
    		bola.parada = false;
    	}
		return true;
	}
	
	public boolean onKeyUp(int keyCode, KeyEvent event)
    {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			gameEnd = false;
			this.onTouchEvent(null);
		}
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	if (gameLoopRodando) {
        		executor.interrupt();
        		executor = null;
        	}
        	finish();
        }
        return false;
    }

	public Formiga novaFormiga() {
		Formiga f = new Formiga();
		int acel = sorteio.nextInt(5);
		f.aceleracao = (acel == 0) ? 1 : acel;

		switch(sorteio.nextInt(3)) {
		case 0:
			f.cor = Color.GREEN;
			break;
		case 1:
			f.cor = Color.RED;
			break;
		case 2:
			f.cor = Color.YELLOW;
			break;
		}
		f.esmagada = false;
		f.ativa = true;
		f.altura = bitmap.getHeight() / 2.0f;
		f.largura = bitmap.getWidth();
		f.xLimite = this.XLIMITE;
		f.xIni = this.XINI;
		f.yIni = this.YINI;
		return f;
	}
	
	private void redimensionar(View v) {

		LARGURA = v.getWidth();
		ALTURA = v.getHeight();
		float escala = LARGURA / 320.f; 
		TAMBOLA = (int) (50 * escala);
		XLIMITE = (float) (LARGURA * 0.48f);
		XINI = -30;
		YINI = 400.0f * escala;
		PISO = 370.0f * escala;
		MEIO = 130.0f * escala;
		Options opc = new Options();
        opc.inDither = true;
		fundo  = BitmapFactory.decodeResource(getResources(), R.drawable.fundo, opc);
		fundo = Bitmap.createScaledBitmap(fundo, LARGURA, ALTURA, true);
		bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.yingyang);
		bitmap = Bitmap.createScaledBitmap(bitmap, TAMBOLA, TAMBOLA, true);
		mancha = BitmapFactory.decodeResource(getResources(), R.drawable.mancha);
		mancha = Bitmap.createScaledBitmap(mancha, TAMBOLA, TAMBOLA, true);
		formigaVerde1 = BitmapFactory.decodeResource(getResources(), R.drawable.verde1);
		formigaVerde1 = Bitmap.createScaledBitmap(formigaVerde1, bitmap.getWidth(),bitmap.getHeight() / 2, true);
		formigaVerde2 = BitmapFactory.decodeResource(getResources(), R.drawable.verde2);
		formigaVerde2 = Bitmap.createScaledBitmap(formigaVerde2, bitmap.getWidth(),bitmap.getHeight() / 2, true);
		formigaAmarela1 = BitmapFactory.decodeResource(getResources(), R.drawable.amarelo1);
		formigaAmarela1 = Bitmap.createScaledBitmap(formigaAmarela1, bitmap.getWidth(),bitmap.getHeight() / 2, true);
		formigaAmarela2 = BitmapFactory.decodeResource(getResources(), R.drawable.amarelo2);
		formigaAmarela2 = Bitmap.createScaledBitmap(formigaAmarela2, bitmap.getWidth(),bitmap.getHeight() / 2, true);
		formigaVermelha1 = BitmapFactory.decodeResource(getResources(), R.drawable.vermelho1);
		formigaVermelha1 = Bitmap.createScaledBitmap(formigaVermelha1, bitmap.getWidth(),bitmap.getHeight() / 2, true);
		formigaVermelha2 = BitmapFactory.decodeResource(getResources(), R.drawable.vermelho2);
		formigaVermelha2 = Bitmap.createScaledBitmap(formigaVermelha2, bitmap.getWidth(),bitmap.getHeight() / 2, true);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// Ignorar alterações de orientação
		super.onConfigurationChanged(newConfig);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
	}
}
