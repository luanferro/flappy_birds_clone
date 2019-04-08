package com.airon.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.lang.reflect.Field;
import java.util.Random;

public class FlappyBird extends ApplicationAdapter {

	private SpriteBatch batch;
	private Texture[] passaros;
	private Texture fundo;
	private Texture canoBaixo;
	private Texture canoTopo;
	private Texture gameOver;
	private Random numeroRandomico;
	private BitmapFont fonte;
	private BitmapFont mensagem;
	private Circle passaroCirculo;
	private Rectangle canoTopoRetangulo;
	private Rectangle canoBaixoRetangulo;
//	private ShapeRenderer shape;

	//atributos de configuração
	private float larguraDispositivo;
	private float alturaDispositivo;
	private int estadoJogo=0;//0--> jogo nao inciado || 1-> jogo iniciado || 2-> game over
	private int pontuacao=0;

	private float variacao = 0;
	private float velocidadeQueda = 0;
	private float posicaoInicialVertical;
	private float posicaoMovimentoCanoHorizontal;
	private float espacoEntreCanos;
	private float deltaTime;
	private float alturaEntreCanosRandomica;
	private boolean marcouPonto;

	//camera
//	private OrthographicCamera camera;
//	private Viewport viewport;
//	private final float VIRTUAL_WIDTH=780;
//	private final float VIRTUAL_HEIGHT=1024;

	@Override
	public void create () {

		batch = new SpriteBatch();
		passaroCirculo = new Circle();
//		canoTopoRetangulo = new Rectangle();
//		canoBaixoRetangulo = new Rectangle();
//		shape = new ShapeRenderer();
		numeroRandomico = new Random();
		fonte = new BitmapFont();
		fonte.setColor(Color.WHITE);
		fonte.getData().setScale(6);

		mensagem = new BitmapFont();
		mensagem.setColor(Color.WHITE);
		mensagem.getData().setScale(3);

		passaros = new Texture[3];
		passaros[0] = new Texture("passaro1.png");
		passaros[1] = new Texture("passaro2.png");
		passaros[2] = new Texture("passaro3.png");

		fundo = new Texture("fundo.png");
		canoBaixo = new Texture("cano_baixo_maior.png");
		canoTopo = new Texture("cano_topo_maior.png");
		gameOver = new Texture("game_over.png");

		/************************************
		 *  Configuração da camera
		 * */
//		camera = new OrthographicCamera();
//		camera.position.set(VIRTUAL_WIDTH,VIRTUAL_HEIGHT,0);
//		viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);

		//pega a largura do disposivo
		larguraDispositivo = Gdx.graphics.getWidth();
		//pega a altura do dispositivo
	 	alturaDispositivo = Gdx.graphics.getHeight();
	 	//variavel para pegar a posicao do cano na vertical que e passada atraves da divisao da altura do dispositivo por 2
	 	posicaoInicialVertical = alturaDispositivo/2;
	 	//variavel para pegar a posicao do cano na horizontal que e passsada atraves da largura do dispositivo
	 	posicaoMovimentoCanoHorizontal = larguraDispositivo;
	 	espacoEntreCanos = 250;

	}

	@Override
	public void render () {

//		camera.update();

		//limpar frames anteriores
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		//funcao da biblioteca gdx que pega a diferenca de tempo entre um render e outro
		deltaTime = Gdx.graphics.getDeltaTime();
		//incrementa a variacao para mudar as imagens em um determinado tempo e criar a animacao
		variacao += deltaTime * 5;
		//verifica se quando a variacao chega a 2 e com isso define ela pra zero, fazendo repetir as imagens criando assim a animacao do passaro
		if (variacao > 2) {
			variacao = 0;
		}

		//verifica o estado do jogo
		if(estadoJogo == 0){	//nao iniciado
			if(Gdx.input.justTouched()){
				estadoJogo=1;
			}
		}else {	//jogo iniciado

			//incrementa a velocidade da queda do passaro
			velocidadeQueda++;

			//verifica se o passaro chegou no chao(base da tela)
			if (posicaoInicialVertical > 0 || velocidadeQueda < 0) {
				posicaoInicialVertical -= velocidadeQueda;
			}

			if(estadoJogo==1){

				//decrementa a posicao do cano fazendo ele se movimentar em uma determinada velocidade
				posicaoMovimentoCanoHorizontal -= deltaTime * 250;

				//verifca o clique da tela e com isso faz subir
				if (Gdx.input.justTouched()) {
					velocidadeQueda = -20;
				}

				//verifica se o cano saiu da tela
				if (posicaoMovimentoCanoHorizontal < -canoTopo.getWidth()) {
					posicaoMovimentoCanoHorizontal = larguraDispositivo;
					alturaEntreCanosRandomica = numeroRandomico.nextInt(600) - 300;
					marcouPonto = false;
				}

				//Verifica se o cano passou do passaro e incrementa a pontuacao
				if (posicaoMovimentoCanoHorizontal < 120) {
					if (!marcouPonto) {
						pontuacao++;
						marcouPonto = true;
					}
				}
			} else {	//tela de game over

				if(Gdx.input.justTouched()){
					estadoJogo=0;
					pontuacao=0;
					velocidadeQueda=0;
					posicaoInicialVertical = alturaDispositivo/2;
					posicaoMovimentoCanoHorizontal = larguraDispositivo;
				}

			}
		}

		//configurar dados de projecao da camera
//		batch.setProjectionMatrix(camera.combined);

		batch.begin();
		//inicializa o fundo
		batch.draw(fundo,0,0, larguraDispositivo, alturaDispositivo);
		//inicializa o cano do topo
		batch.draw(canoTopo,posicaoMovimentoCanoHorizontal,alturaDispositivo/2+35 + espacoEntreCanos/2 + alturaEntreCanosRandomica);
		//incializa o cano de baixo;
		batch.draw(canoBaixo	,posicaoMovimentoCanoHorizontal,(alturaDispositivo/2-canoBaixo.getHeight())- 35-espacoEntreCanos/2 + alturaEntreCanosRandomica);
		//inicializa o array dos passaros(a posicao da imagens dentro do array que sera exibida e definida pela variacao )
		batch.draw(passaros[(int)variacao], 120,posicaoInicialVertical);
		fonte.draw(batch, String.valueOf(pontuacao), larguraDispositivo/2, alturaDispositivo-75);
		if(estadoJogo==2){
			batch.draw(gameOver, larguraDispositivo/2-gameOver.getWidth()/2, alturaDispositivo/2);
			mensagem.draw(batch, "Toque para reiniciar ",larguraDispositivo/2 - 200, alturaDispositivo/2 - gameOver.getHeight()/2);
		}
		batch.end();

		//cria as formas para verificar as colisoes
		passaroCirculo.set(120 + passaros[0].getWidth()/2, posicaoInicialVertical + passaros[0].getHeight()/2, passaros[0].getWidth()/2);
		canoBaixoRetangulo = new Rectangle(
				posicaoMovimentoCanoHorizontal,(alturaDispositivo/2-canoBaixo.getHeight())- 35-espacoEntreCanos/2 + alturaEntreCanosRandomica,
				canoBaixo.getWidth(),canoBaixo.getHeight()
		);
		canoTopoRetangulo = new Rectangle(
				posicaoMovimentoCanoHorizontal,alturaDispositivo/2+35 + espacoEntreCanos/2 + alturaEntreCanosRandomica,
				canoTopo.getWidth(),canoTopo.getHeight()
		);

		//desenhar formas
//		shape.begin(ShapeRenderer.ShapeType.Filled);
//		shape.circle(passaroCirculo.x, passaroCirculo.y, passaroCirculo.radius);
//		shape.rect(canoBaixoRetangulo.x,canoBaixoRetangulo.y,canoBaixoRetangulo.width,canoBaixoRetangulo.height);
//		shape.rect(canoTopoRetangulo.x,canoTopoRetangulo.y,canoTopoRetangulo.width,canoTopoRetangulo.height);
//		shape.setColor(Color.RED);
//		shape.end();

		//teste de colisao
		if(Intersector.overlaps(passaroCirculo,canoBaixoRetangulo) || Intersector.overlaps(passaroCirculo, canoTopoRetangulo)
			|| posicaoInicialVertical <= 0 || posicaoInicialVertical >= alturaDispositivo){
			estadoJogo = 2;
		}

	}

//	@Override
//	public void resize(int width, int height) {
//		viewport.update(width, height);
//	}
}
