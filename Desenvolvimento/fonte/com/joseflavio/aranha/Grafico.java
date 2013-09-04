
/*
 *  Copyright (C) 2010-2011 José Flávio de Souza Dias Júnior
 *
 *  This file is part of Aranha - <http://www.joseflavio.com/aranha/>.
 *
 *  Aranha is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Aranha is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Aranha. If not, see <http://www.gnu.org/licenses/>.
 */

/*
 *  Direitos Autorais Reservados (C) 2010-2011 José Flávio de Souza Dias Júnior
 * 
 *  Este arquivo é parte de Aranha - <http://www.joseflavio.com/aranha/>.
 * 
 *  Aranha é software livre: você pode redistribuí-lo e/ou modificá-lo
 *  sob os termos da Licença Pública Geral GNU conforme publicada pela
 *  Free Software Foundation, tanto a versão 3 da Licença, como
 *  (a seu critério) qualquer versão posterior.
 * 
 *  Aranha é distribuído na expectativa de que seja útil,
 *  porém, SEM NENHUMA GARANTIA; nem mesmo a garantia implícita de
 *  COMERCIABILIDADE ou ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA. Consulte a
 *  Licença Pública Geral do GNU para mais detalhes.
 * 
 *  Você deve ter recebido uma cópia da Licença Pública Geral do GNU
 *  junto com Aranha. Se não, veja <http://www.gnu.org/licenses/>.
 */

package com.joseflavio.aranha;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.DefaultXYDataset;

import com.joseflavio.aranha.analise.Dia;
import com.joseflavio.cultura.NumeroTransformacao;
import com.joseflavio.cultura.TransformacaoException;
import com.joseflavio.tqc.console.AplicacaoConsole;
import com.joseflavio.tqc.console.Argumentos;
import com.joseflavio.tqc.console.ChaveadosArgumentos;
import com.joseflavio.tqc.console.ChaveadosArgumentos.AdaptadoArgumentoProcessador;
import com.joseflavio.tqc.console.ChaveadosArgumentos.Chave;
import com.joseflavio.tqc.console.ChaveadosArgumentosBuilder;
import com.joseflavio.tqc.console.Cor;
import com.joseflavio.util.CSVUtil;
import com.joseflavio.util.Calendario;
import com.joseflavio.util.SeparadorTextual;

/**
 * Plota periodicamente o resultado de {@link Dia}.
 * @author José Flávio de Souza Dias Júnior
 * @version 2011
 */
public class Grafico extends AplicacaoConsole {

	/**
	 * Título do gráfico.
	 */
	private String titulo;
	
	/**
	 * Linha do tempo.
	 */
	private List<Long> tempo = new ArrayList<Long>();
	
	private Serie[] series = new Serie[ 100 ];
	
	private int seriesTotal = 0;

	/**
	 * Arquivo a ser plotado.
	 */
	private File arquivo;
	
	private boolean esperarPeloArquivo = false;
	
	/**
	 * Intervalo entre plotagens, em segundos.
	 */
	private long intervalo = 60;
	
	private boolean podar = false;
	
	private long podaInicial;
	
	private long podaFinal;
	
	private int janelaX, janelaY, janelaL, janelaA;
	
	private JFrame janela;
	
	private ChartPanel chartPanel = new ChartPanel( null, false );
	
	private JScrollPane scroll;
	
	private JViewport viewport;

	private static final Color[] CORES = { Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.BLACK, Color.MAGENTA, Color.ORANGE, Color.GRAY, Color.PINK };
	
	/*
	 * Recursos para leitura do arquivo.
	 */
	private String[] colunas = new String[ 500 ];
	private StringBuilder buffer = new StringBuilder( 20 );
	private NumeroTransformacao mbpsFormato = Util.novaMbpsTransformacao( getCultura() );
	private SimpleDateFormat horaFormato = new SimpleDateFormat( "HH:mm:ss" );
	
	private boolean pausaAtivada = false;
	
	private Carregamento carregamento;
	
	private boolean sairAoFechar = true;
	
	public Grafico( String[] args, boolean sairAoFechar ) {
		this.sairAoFechar = sairAoFechar;
		executar( this, args );
	}
	
	public static void main( String[] args ) {
		new Grafico( args, true );
	}
	
	private void executar( Grafico grafico, String[] args ) {
		
		try{
			
			grafico.inicio( args );
			
		}catch( Exception e ){
			mostrarErro( e.getMessage() );
			System.exit( 1 );
		}
		
	}
	
	@Override
	protected Argumentos processarArgumentos( String[] args ) {
		
		ChaveadosArgumentos argumentos = new ChaveadosArgumentosBuilder( args )
		.mais( "-titulo", true, new Argumento_titulo() )
		.mais( "-rotulo", true, new Argumento_rotulo() )
		.mais( "-cor", true, new Argumento_cor() )
		.mais( "-naomostrar", true, new Argumento_naomostrar() )
		.mais( "-intervalo", true, new Argumento_intervalo() )
		.mais( "-janela", true, new Argumento_janela() )
		.mais( "-fechamento", true, new Argumento_fechamento() )
		.mais( "-fechamentoDia", false, new Argumento_fechamentoDia() )
		.mais( "-esperar", false, new Argumento_esperar() )
		.mais( "-podar", false, new Argumento_podar() )
		.mais( "-podaInicial", true, new Argumento_podaInicial() )
		.mais( "-podaFinal", true, new Argumento_podaFinal() )
		.mais( "-dialogo", false, new Argumento_dialogo() )
		.mais( "-?", false, new Argumento_ajuda() )
		.getChaveadosArgumentos();

		argumentos.processarArgumentos( new Argumento_null() );
		
		if( arquivo == null ) throw new IllegalArgumentException( "Informe o arquivo." );
		
		if( podaInicial == 0 ) podaInicial = 10800000;
		if( podaFinal == 0 ) podaFinal = 97199000;
		
		return argumentos;

	}
	
	@Override
	protected void principal() {
		
		try{

			janela = new JFrame( "Aranha" );
			
			carregarArquivo();
			
			scroll = new JScrollPane( chartPanel );
			viewport = scroll.getViewport();
			
			MouseControle mouseControle = new MouseControle();
			TamanhoControle tamanhoControle = new TamanhoControle();

			scroll.setHorizontalScrollBarPolicy( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS );
			scroll.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
			
			chartPanel.setPopupMenu( null );
			chartPanel.setZoomAroundAnchor( true );
			chartPanel.setMouseZoomable( false );
			chartPanel.setMouseWheelEnabled( false );
			chartPanel.addMouseListener( mouseControle );
			chartPanel.addMouseMotionListener( mouseControle );
			chartPanel.addMouseWheelListener( mouseControle );
			
			DisplayMode tela = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode();
			boolean maximizar = janelaL == 0 || janelaA == 0;
			if( maximizar ) janelaL = (int)( tela.getWidth() * 0.5f );
			if( maximizar ) janelaA = (int)( tela.getHeight() * 0.5f );
			
			janela.setJMenuBar( new BarraDeMenus() );
			janela.setBounds( janelaX, janelaY, janelaL, janelaA );
			janela.getContentPane().add( scroll );
			janela.addComponentListener( tamanhoControle );
			janela.setDefaultCloseOperation( sairAoFechar ? JFrame.EXIT_ON_CLOSE : JFrame.DISPOSE_ON_CLOSE );
			if( maximizar ) janela.setExtendedState( Frame.MAXIMIZED_BOTH );
			janela.setVisible( true );
			
			carregamento = new Carregamento();
			carregamento.start();
			new CarregamentoVerificador().start();
			
		}catch( Exception e ){
			throw new IllegalArgumentException( e.getMessage() );
		}
		
	}
	
	@Override
	protected void fim() {
	}
	
	private synchronized void carregarArquivo() throws InterruptedException, IOException {
		
		try{
			
			carregarArquivo0();
			
		}catch( Exception e ){
			
			Thread.sleep( 3000 );
				
			carregarArquivo0();
			
		}
		
	}
	
	private void carregarArquivo0() throws IOException {
		
		try{
			
			if( esperarPeloArquivo ){
				while( ! arquivo.exists() || arquivo.length() == 0 ) Thread.sleep( 2000 );
			}else{
				while(   arquivo.exists() && arquivo.length() == 0 ) Thread.sleep( 2000 );				
			}
			
			FileReader entrada = new FileReader( arquivo );
			Serie serie;
			
			tempo.clear();
			Arrays.fill( colunas, null );
			buffer.delete( 0, buffer.length() );
			
			int i;
			for( i = 0; i < series.length; i++ ){
				if( series[i] != null ) series[i].limpar();
			}
			
			System.gc();
			
			seriesTotal = CSVUtil.proximaLinha( entrada, colunas, ';', buffer ) - 1;
			
			for( i = 0; i < seriesTotal; i++ ){
				if( series[i] == null ) series[i] = new Serie();
				series[i].tituloReal = colunas[i+1];
			}
			
			long time;
			while( CSVUtil.proximaLinha( entrada, colunas, ';', buffer ) > 0 ){
				time = horaFormato.parse( colunas[0] ).getTime();
				if( time < podaInicial || time > podaFinal ) continue;
				tempo.add( time );
				for( i = 0; i < seriesTotal; i++ ){
					series[i].valores.add( mbpsFormato.transformarReal( colunas[i+1] ) );
				}
			}
			
			entrada.close();
			
			JFreeChart grafico = new JFreeChart( titulo, new XYPlot( null, new DateAxis(), new NumberAxis(), new StandardXYItemRenderer() ) );
			DefaultXYDataset dataset = new DefaultXYDataset();
			XYItemRenderer render = grafico.getXYPlot().getRenderer();

			int tempoTotal = tempo.size();
			
			if( podar ){
				
				int menor = tempoTotal, menorMaior = Integer.MIN_VALUE, maior = -1, maiorMenor = Integer.MAX_VALUE;
				
				for( int s = 0; s < seriesTotal; s++ ){
					serie = series[s];
					for( i = 0; i < tempoTotal; i++ ){
						if( serie.valores.get( i ) != 0 ){
							if( i < menor ) menor = i;
							if( menor > menorMaior ) menorMaior = menor;
							break;
						}
					}
					for( i = tempoTotal - 1; i >= 0; i-- ){
						if( serie.valores.get( i ) != 0 ){
							if( i > maior ) maior = i;
							if( maior < maiorMenor ) maiorMenor = maior;
							break;
						}
					}
				}
				
				if( menor == tempoTotal ) menor = 0;
				if( maior == -1 ) maior = tempoTotal; else maior++;
				if( ( menor - menorMaior ) == -1 ) menor = menorMaior;
				if( ( maior - maiorMenor ) ==  1 ) maior = maiorMenor;
				
				sublista( tempo, menor, maior );
				for( int s = 0; s < seriesTotal; s++ ){
					serie = series[s];
					sublista( serie.valores, menor, maior );
				}
				
			}
			
			tempoTotal = tempo.size();
			
			double[] x = new double[ tempoTotal ];
			for( i = 0; i < tempoTotal; i++ ) x[i] = tempo.get( i );
			
			for( int s = 0; s < seriesTotal; s++ ){
				serie = series[s];
				if( ! serie.ativado ) continue;
				double[] y = new double[ tempoTotal ];
				for( i = 0; i < tempoTotal; i++ ) y[i] = serie.valores.get( i );
				dataset.addSeries( serie.getTitulo(), new double[][]{ x, y } );
				render.setSeriesPaint( dataset.getSeriesCount() - 1, getCorDaSerie( s ) );
			}
			
			grafico.getXYPlot().setDataset( dataset );
			chartPanel.setChart( grafico );
			
			System.gc();
			
		}catch( FileNotFoundException e ){
			throw new IOException( "Arquivo não encontrado: " + arquivo.getName() );
		}catch( ParseException e ){
			throw new IOException( "Formato de arquivo inválido." );
		}catch( TransformacaoException e ){
			throw new IOException( "Formato de arquivo inválido." );
		}catch( Exception e ){
			throw new IOException( "Erro desconhecido: " + e.getMessage() );
		}
		
	}
	
	private static void mostrarErro( String mensagem ) {
		JOptionPane.showMessageDialog( null, mensagem, "Aranha", JOptionPane.ERROR_MESSAGE );
	}
	
	/**
	 * Elimina elementos da lista deixando apenas os da sublista desejada.
	 * @param fim Exclusive.
	 */
	private static void sublista( List<?> lista, int inicio, int fim ) {
		int i;
		for( i = lista.size() - 1; i >= fim; i-- ) lista.remove( i );
		for( i = 0; i < inicio; i++ ) lista.remove( 0 );
	}
	
	private void setTamanho( int largura, int altura ) {
		chartPanel.setMinimumDrawWidth( largura );
		chartPanel.setMaximumDrawWidth( largura );
		chartPanel.setMinimumDrawHeight( altura );
		chartPanel.setMaximumDrawHeight( altura );
		chartPanel.setPreferredSize( new Dimension( largura, altura ) );
		chartPanel.setSize( largura, altura );
		chartPanel.repaint();
	}
	
	private Color getCorDaSerie( int indice ) {
		Serie serie = series[indice];
		return serie.cor != null ? serie.cor : CORES[indice%CORES.length];
	}
	
	private static class Serie {
		
		private String tituloVirtual;
		
		private String tituloReal;
		
		private boolean ativado = true;
		
		private Color cor;
		
		private List<Double> valores = new ArrayList<Double>( 10000 );
		
		public void limpar() {
			tituloReal = null;
			valores.clear();
		}
		
		public String getTitulo() {
			return tituloVirtual == null ? tituloReal : tituloVirtual;
		}
		
	}
	
	private class Argumento_null extends AdaptadoArgumentoProcessador {
		public void processar( Chave chave, String valor ) throws IllegalArgumentException {
			if( arquivo != null ) throw new IllegalArgumentException( "Arquivo já definido." );
			arquivo = new File( valor );
			if( ! arquivo.exists() ) arquivo = new File( new File( System.getProperty( "user.dir" ) ), valor );
			if( ! arquivo.exists() ) throw new IllegalArgumentException( "Arquivo inexistente: " + valor );
		}
	}
	
	private class Argumento_esperar extends AdaptadoArgumentoProcessador {
		public void processar( Chave chave, String valor ) throws IllegalArgumentException {
			esperarPeloArquivo = true;
		}
	}
	
	private class Argumento_podar extends AdaptadoArgumentoProcessador {
		public void processar( Chave chave, String valor ) throws IllegalArgumentException {
			podar = true;
		}
	}
	
	private class Argumento_titulo extends AdaptadoArgumentoProcessador {
		public void processar( Chave chave, String valor ) throws IllegalArgumentException {
			titulo = valor;
		}
	}
	
	private class Argumento_intervalo extends AdaptadoArgumentoProcessador {
		public void processar( Chave chave, String valor ) throws IllegalArgumentException {
			try{
				intervalo = getCultura().novaInteiroTransformacao().transformarInteiro( valor );
			}catch( Exception e ){
				throw new IllegalArgumentException( "Intervalo incorreto: " + valor );
			}
		}
	}
	
	private class Argumento_podaInicial extends AdaptadoArgumentoProcessador {
		public void processar( Chave chave, String valor ) throws IllegalArgumentException {
			try{
				podaInicial = horaFormato.parse( valor ).getTime();
			}catch( Exception e ){
				throw new IllegalArgumentException( "Poda inicial incorreta: " + valor );
			}
		}
	}
	
	private class Argumento_podaFinal extends AdaptadoArgumentoProcessador {
		public void processar( Chave chave, String valor ) throws IllegalArgumentException {
			try{
				podaFinal = horaFormato.parse( valor ).getTime();
			}catch( Exception e ){
				throw new IllegalArgumentException( "Poda final incorreta: " + valor );
			}
		}
	}
	
	private class Argumento_cor extends AdaptadoArgumentoProcessador {
		public void processar( Chave chave, String valor ) throws IllegalArgumentException {
			
			try{
				
				SeparadorTextual st = new SeparadorTextual( ':' );
				st.executar( valor );
				
				int i = Integer.parseInt( st.getParte( 0 ) );
				int cor = Integer.parseInt( st.getParte( 1 ), 16 );
				
				if( series[i] == null ) series[i] = new Serie();
				series[i].cor = new Color( cor );
				
			}catch( Exception e ){
				throw new IllegalArgumentException( "Cor de série incorreta: " + valor );
			}
			
		}
	}
	
	private class Argumento_janela extends AdaptadoArgumentoProcessador {
		public void processar( Chave chave, String valor ) throws IllegalArgumentException {
		
			try{
				
				NumeroTransformacao it = getCultura().novaInteiroTransformacao();
				
				SeparadorTextual st = new SeparadorTextual( new char[]{ ',', ',', ',' } );
				st.executar( valor );
				
				janelaX = (int) it.transformarInteiro( st.getParte( 0 ) );
				janelaY = (int) it.transformarInteiro( st.getParte( 1 ) );
				janelaL = (int) it.transformarInteiro( st.getParte( 2 ) );
				janelaA = (int) it.transformarInteiro( st.getParte( 3 ) );
				
			}catch( Exception e ){
				throw new IllegalArgumentException( "Configuração de janela incorreta: " + valor );
			}
			
		}
	}
	
	private class Argumento_rotulo extends AdaptadoArgumentoProcessador {
		public void processar( Chave chave, String valor ) throws IllegalArgumentException {

			try{
				
				SeparadorTextual st = new SeparadorTextual( ':' );
				st.executar( valor );
				
				int i = Integer.parseInt( st.getParte( 0 ) );
				String rotulo = st.getParte( 1 );
				
				if( series[i] == null ) series[i] = new Serie();
				series[i].tituloVirtual = rotulo;
				
			}catch( Exception e ){
				throw new IllegalArgumentException( "Rótulo de série incorreto: " + valor );
			}
			
		}
	}
	
	private class Argumento_naomostrar extends AdaptadoArgumentoProcessador {
		public void processar( Chave chave, String valor ) throws IllegalArgumentException {

			try{
				
				StringTokenizer st = new StringTokenizer( valor, "," );
				List<Integer> indices = new ArrayList<Integer>();
				
				while( st.hasMoreTokens() ){
					String t = st.nextToken();
					int faixa = t.indexOf( '-' );
					if( faixa == -1 ){
						indices.add( Integer.parseInt( t ) );
					}else{
						int inicio = Integer.parseInt( t.substring( 0, faixa ) );
						int fim = Integer.parseInt( t.substring( faixa + 1, t.length() ) );
						for( int n = inicio; n <= fim; n++ ){
							indices.add( n );
						}
					}
				}
				
				for( Integer i : indices ){
					if( i < 0 || i >= series.length ) continue;
					if( series[i] == null ) series[i] = new Serie();
					series[i].ativado = false;
				}
				
			}catch( Exception e ){
				e.printStackTrace();
				throw new IllegalArgumentException( "Sequência de índices incorreta em naomostrar: " + valor );
			}
			
		}
	}
	
	private class Argumento_fechamento extends AdaptadoArgumentoProcessador {
		public void processar( Chave chave, String valor ) throws IllegalArgumentException {
			try{
				long fechamento = getCultura().novaInteiroTransformacao().transformarInteiro( valor );
				new Timer().schedule( new Fechamento(), fechamento * 1000 );
			}catch( Exception e ){
				throw new IllegalArgumentException( "Fechamento incorreto: " + valor );
			}
		}
	}
	
	private class Argumento_fechamentoDia extends AdaptadoArgumentoProcessador {
		public void processar( Chave chave, String valor ) throws IllegalArgumentException {
			long tempo = System.currentTimeMillis();
			tempo = new Calendario().setTimestamp( tempo ).setHora( 23 ).setMinuto( 59 ).setSegundo( 59 ).setMilissegundo( 0 ).getTimestamp() - tempo;
			new Timer().schedule( new Fechamento(), tempo );
		}
	}
	
	private class Argumento_dialogo extends AdaptadoArgumentoProcessador {
		public void processar( Chave chave, String valor ) throws IllegalArgumentException {
			
			JFileChooser fileChooser = new JFileChooser( new File( System.getProperty( "user.dir" ) ) );
			fileChooser.setDialogTitle( "Gráfico Aranha em CSV" );
			fileChooser.setDialogType( JFileChooser.OPEN_DIALOG );
			fileChooser.setFileSelectionMode( JFileChooser.FILES_ONLY );
			fileChooser.setFileFilter( new FileNameExtensionFilter( "Gráfico Aranha em CSV", "csv" ) );
			fileChooser.showOpenDialog( null );
			arquivo = fileChooser.getSelectedFile();
			
			valor = (String) JOptionPane.showInputDialog( null, "Informe o título do gráfico:", "Aranha", JOptionPane.INFORMATION_MESSAGE, null, null, arquivo.getName() );
			if( valor != null && valor.length() > 0 ){
				new Argumento_titulo().processar( chave, valor );
			}
			
			valor = (String) JOptionPane.showInputDialog( null, "Informe o intervalo entre plotagens, em segundos:", "Aranha", JOptionPane.INFORMATION_MESSAGE, null, null, intervalo );
			if( valor != null && valor.length() > 0 ){
				new Argumento_intervalo().processar( chave, valor );
			}
			
			valor = (String) JOptionPane.showInputDialog( null, "Informe as séries que não deverão ser mostradas:", "Aranha", JOptionPane.INFORMATION_MESSAGE, null, null, "0,1,4-100" );
			if( valor != null && valor.length() > 0 ){
				new Argumento_naomostrar().processar( chave, valor );
			}
			
			podar = JOptionPane.showConfirmDialog( null, "Podar o gráfico?", "Aranha", JOptionPane.YES_NO_OPTION ) == JOptionPane.YES_OPTION;
			
		}
	}
	
	private class Argumento_ajuda extends AdaptadoArgumentoProcessador {
		
		public void processar( Chave chave, String valor ) throws IllegalArgumentException {
			Aranha.enviarCabecalho( Grafico.this );
			enviarln();
			arg( "-titulo",        "Titulo do grafico." );
			arg( "-rotulo",        "Rotulo da serie N. Ex.: \"N:Download\"" );
			arg( "-cor",           "Cor da serie N em RGB hexadecimal. Ex.: \"N:00FF00\"" );
			arg( "-naomostrar",    "Nao mostrar as series A, B, ..., Z. Ex.: \"0,1,4-100\"" );
			arg( "-intervalo",     "Intervalo entre plotagens, em segundos. Padrao: 60" );
			arg( "-janela",        "Posicao e tamanho da janela na tela: x,y,largura,altura" );
			arg( "-fechamento",    "Tempo, em segundos, para fechamento automatico do programa." );
			arg( "-fechamentoDia", "Fecha automaticamente o programa ao final do dia." );
			arg( "-esperar",       "Espera pela criacao do arquivo." );
			arg( "-podar",         "Retira do grafico o inicio e o fim em branco." );
			arg( "-podaInicial",   "Oculta a parte do grafico ate este horario." );
			arg( "-podaFinal",     "Oculta a parte do grafico apos este horario." );
			arg( "-dialogo",       "Dialogo visual para definir os principais argumentos." );
			arg( "-?",             "Esta ajuda." );
			System.exit( 0 );
		}
		
		private void arg( String comando, String explicacao ) {
			enviar( Cor.VERDE_INTENSA, "%1$-15s", comando );
			enviarln( Cor.CINZA_INTENSA, explicacao );
		}
		
	}
	
	private class Fechamento extends TimerTask {
		
		@Override
		public void run() {
			try{
				janela.dispose();
				System.exit( 0 );
			}catch( Exception e ){
			}
		}
		
	}
	
	private class Carregamento extends Thread {
		
		private long ultimoCarregamento = System.currentTimeMillis();
		
		private boolean interrompendo = false;
		
		@Override
		public void run() {
			
			try{
				
				while( true ){
					for( long i = intervalo; i > 0; i-- ){
						while( pausaAtivada ) Thread.sleep( 100 );
						janela.setTitle( ( titulo != null ? titulo : arquivo.getName() ) + " (" + i + " s)" );
						Thread.sleep( 1000 );
					}
					carregarArquivo();
					ultimoCarregamento = System.currentTimeMillis();
				}
				
			}catch( Throwable e ){
				
				if( ! interrompendo ){
					mostrarErro( e.getMessage() );
					System.exit( 1 );
				}
				
			}finally{
				
				interrompendo = false;
				
			}
			
		}
		
	}
	
	private class CarregamentoVerificador extends Thread {
		
		@Override
		public void run() {
			
			try{
				
				long tempo = intervalo * 4000;
				
				while( true ){
					
					Thread.sleep( tempo );
					
					if( ! pausaAtivada && ( System.currentTimeMillis() - carregamento.ultimoCarregamento ) > tempo ){
						
						carregamento.interrompendo = true;
						carregamento.interrupt();
						
						carregamento = new Carregamento();
						carregamento.start();
						
					}
					
				}
				
			}catch( Throwable e ){
				mostrarErro( e.getMessage() );
				System.exit( 1 );
			}
			
		}
		
	}
	
	private class TamanhoControle extends ComponentAdapter {
		
		public void componentResized( ComponentEvent e ) {
			setTamanho( viewport.getWidth(), viewport.getHeight() );
		}

	}
	
	private class MouseControle extends MouseAdapter {
		
		private JScrollBar bh, bv;
		
		private int x, y, nx, ny, h, v, hmin, hmax, vmin, vmax;
		
		public MouseControle() {
			this.bh = scroll.getHorizontalScrollBar();
			this.bv = scroll.getVerticalScrollBar();
		}
		
		public void mousePressed( MouseEvent e ) {
			x = e.getXOnScreen();
			y = e.getYOnScreen();
			h = bh.getValue();
			v = bv.getValue();
			hmin = bh.getMinimum();
			hmax = bh.getMaximum() - bh.getVisibleAmount();
			vmin = bv.getMinimum();
			vmax = bv.getMaximum() - bv.getVisibleAmount();
		}
		
		public void mouseDragged( MouseEvent e ) {
			
			boolean CTRL = ( e.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK ) == MouseEvent.CTRL_DOWN_MASK;
			
			if( ! CTRL ) return;
			
			nx = e.getXOnScreen();
			ny = e.getYOnScreen();
			
			nx = h - nx + x;
			if( nx < hmin ) nx = hmin;
			if( nx > hmax ) nx = hmax;
			bh.setValue( nx );
		
			ny = v - ny + y;
			if( ny < vmin ) ny = vmin;
			if( ny > vmax ) ny = vmax;
			bv.setValue( ny );
			
		}
		
		public void mouseWheelMoved( MouseWheelEvent e ) {
			
			int wheel = - e.getWheelRotation();
			boolean CTRL = ( e.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK ) == MouseEvent.CTRL_DOWN_MASK;
			
			if( CTRL ){
				int largura = chartPanel.getWidth() + wheel * 100;
				int altura = chartPanel.getHeight() + wheel * 100;
				if( largura < viewport.getWidth() || altura < viewport.getHeight() ) return;
				setTamanho( largura, altura );
			}else{
				if( wheel > 0 ) chartPanel.zoomInBoth( e.getX(), e.getY() );
				else chartPanel.zoomOutBoth( e.getX(), e.getY() );
			}
			
		}
		
		public void mouseReleased( MouseEvent e ) {

			if( e.getButton() == MouseEvent.BUTTON3 ){
				chartPanel.restoreAutoBounds();
			}
			
		}
		
	}
	
	private class BarraDeMenus extends JMenuBar {
		
		private static final long serialVersionUID = 1L;

		public BarraDeMenus() {
			
			add( new JMenuItem( new AcaoSalvar() ) );
			add( new JMenuItem( new AcaoMenosLargura() ) );
			add( new JMenuItem( new AcaoMaisLargura() ) );
			add( new JMenuItem( new AcaoMenosAltura() ) );
			add( new JMenuItem( new AcaoMaisAltura() ) );
			add( new JMenuItem( new AcaoAutoTamanho() ) );
			add( new JMenuItem( new AcaoAutoZoom() ) );
			add( new JMenuItem( new AcaoPausar() ) );
			
			JMenu mais = new JMenu( "Mais" );
			mais.add( new JMenuItem( new AcaoMenosTamanho() ) );
			mais.add( new JMenuItem( new AcaoMaisTamanho() ) );
			add( mais );
			
		}
		
	}
	
	private class AcaoAutoZoom extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public AcaoAutoZoom() {
			super( "Auto Zoom" );
		}
		
		public void actionPerformed( ActionEvent e ) {
			chartPanel.restoreAutoBounds();
		}
		
	}
	
	private class AcaoAutoTamanho extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public AcaoAutoTamanho() {
			super( "Auto Tam" );
		}
		
		public void actionPerformed( ActionEvent e ) {
			setTamanho( viewport.getWidth(), viewport.getHeight() );
		}
		
	}
	
	private class AcaoMaisTamanho extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public AcaoMaisTamanho() {
			super( "+ Tam" );
		}
		
		public void actionPerformed( ActionEvent e ) {
			setTamanho( chartPanel.getWidth() + 100, chartPanel.getHeight() + 100 );
		}
		
	}
	
	private class AcaoMenosTamanho extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public AcaoMenosTamanho() {
			super( "- Tam" );
		}
		
		public void actionPerformed( ActionEvent e ) {
			int largura = chartPanel.getWidth() - 100;
			int altura = chartPanel.getHeight() - 100;
			if( largura < viewport.getWidth() || altura < viewport.getHeight() ) return;
			setTamanho( largura, altura );
		}
		
	}
	
	private class AcaoMaisLargura extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public AcaoMaisLargura() {
			super( "+ Largo" );
		}
		
		public void actionPerformed( ActionEvent e ) {
			setTamanho( chartPanel.getWidth() + 100, chartPanel.getHeight() );
		}
		
	}
	
	private class AcaoMenosLargura extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public AcaoMenosLargura() {
			super( "- Largo" );
		}
		
		public void actionPerformed( ActionEvent e ) {
			int largura = chartPanel.getWidth() - 100;
			int altura = chartPanel.getHeight();
			if( largura < viewport.getWidth() ) return;
			setTamanho( largura, altura );
		}
		
	}
	
	private class AcaoMaisAltura extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public AcaoMaisAltura() {
			super( "+ Alto" );
		}
		
		public void actionPerformed( ActionEvent e ) {
			setTamanho( chartPanel.getWidth(), chartPanel.getHeight() + 100 );
		}
		
	}
	
	private class AcaoMenosAltura extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public AcaoMenosAltura() {
			super( "- Alto" );
		}
		
		public void actionPerformed( ActionEvent e ) {
			int largura = chartPanel.getWidth();
			int altura = chartPanel.getHeight() - 100;
			if( altura < viewport.getHeight() ) return;
			setTamanho( largura, altura );
		}
		
	}
	
	private class AcaoSalvar extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public AcaoSalvar() {
			super( "Salvar" );
		}
		
		public void actionPerformed( ActionEvent e ) {
			try{
				chartPanel.doSaveAs();
			}catch( IOException e1 ){
			}
		}
		
	}
	
	private class AcaoPausar extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public AcaoPausar() {
			super( "Pausar" );
		}
		
		public void actionPerformed( ActionEvent e ) {
			pausaAtivada = ! pausaAtivada;
			putValue( Action.NAME, pausaAtivada ? "Continuar" : "Pausar" );
		}
		
	}
	
}
