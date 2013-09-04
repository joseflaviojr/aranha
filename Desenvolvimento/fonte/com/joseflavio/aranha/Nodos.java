
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

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.joseflavio.aranha.analise.NodosEthernet;
import com.joseflavio.aranha.analise.NodosIP;
import com.joseflavio.aranha.analise.NodosIPDia;
import com.joseflavio.aranha.analise.NodosIPGravacao;
import com.joseflavio.cultura.NumeroTransformacao;
import com.joseflavio.tqc.console.AplicacaoConsole;
import com.joseflavio.tqc.console.Argumentos;
import com.joseflavio.tqc.console.ChaveadosArgumentos;
import com.joseflavio.tqc.console.ChaveadosArgumentos.AdaptadoArgumentoProcessador;
import com.joseflavio.tqc.console.ChaveadosArgumentos.Chave;
import com.joseflavio.tqc.console.ChaveadosArgumentosBuilder;
import com.joseflavio.tqc.console.Cor;
import com.joseflavio.tqc.console.SwingConsole;
import com.joseflavio.util.CSVUtil;
import com.joseflavio.util.Calendario;
import com.joseflavio.util.SeparadorTextual;

/**
 * Mostra periodicamente o resultado ordenado de {@link NodosIP}, {@link NodosIPDia}, {@link NodosIPGravacao} ou {@link NodosEthernet}.
 * @author José Flávio de Souza Dias Júnior
 * @version 2011
 */
public class Nodos extends AplicacaoConsole {

	private static final Comparator<Ponto> ORDEM_PACOTES_ENVIADOS = new OrdemPacotesEnviados();
	private static final Comparator<Ponto> ORDEM_PACOTES_RECEBIDOS = new OrdemPacotesRecebidos();
	private static final Comparator<Ponto> ORDEM_PACOTES_TOTAL = new OrdemPacotesTotal();
	private static final Comparator<Ponto> ORDEM_BYTES_ENVIADOS = new OrdemBytesEnviados();
	private static final Comparator<Ponto> ORDEM_BYTES_RECEBIDOS = new OrdemBytesRecebidos();
	private static final Comparator<Ponto> ORDEM_BYTES_TOTAL = new OrdemBytesTotal();
	
	private String titulo;
	
	private List<Ponto> pontos = new ArrayList<Ponto>( 500 );
	
	private Ponto[] pontosOrdenados;
	
	private Map<Foco, Ranking> ranking = new HashMap<Foco, Ranking>( 500 );
	
	private int rankingContagem = 0;
	
	private Comparator<Ponto> ordem = ORDEM_BYTES_TOTAL;
	
	private long pacotesEnviados, bytesEnviados, pacotesRecebidos, bytesRecebidos, pacotesTotal, bytesTotal;
	
	/**
	 * Arquivo a ser ordenado e mostrado.
	 */
	private File arquivo;
	
	private boolean esperarPeloArquivo = false;
	
	/**
	 * Intervalo entre amostragens, em segundos.
	 */
	private long intervalo = 60;
	
	private int consoleFonte = 18, consoleLinhas = 20, consoleColunas = 60;
	
	private int janelaX, janelaY, janelaL, janelaA;
	
	/*
	 * Recursos para leitura do arquivo.
	 */
	private String[] colunas = new String[ 7 ];
	private StringBuilder buffer = new StringBuilder( 20 );
	
	private boolean contando = false;
	
	private boolean pausaAtivada = false;
	
	private boolean sairAoFechar = true;
	
	public Nodos( String[] args, boolean sairAoFechar ) {
		this.sairAoFechar = sairAoFechar;
		executar( this, args );
	}
	
	public static void main( String[] args ) {
		new Nodos( args, true );
	}
	
	private void executar( Nodos nodos, String[] args ) {
		
		try{
			
			nodos.inicio( args );
			
		}catch( Exception e ){
			mostrarErro( e.getMessage() );
			System.exit( 1 );
		}
		
	}
	
	@Override
	protected Argumentos processarArgumentos( String[] args ) {
		
		ChaveadosArgumentos argumentos = new ChaveadosArgumentosBuilder( args )
		.mais( "-titulo", true, new Argumento_titulo() )
		.mais( "-ordem", true, new Argumento_ordem() )
		.mais( "-intervalo", true, new Argumento_intervalo() )
		.mais( "-console", true, new Argumento_console() )
		.mais( "-janela", true, new Argumento_janela() )
		.mais( "-posicao", true, new Argumento_posicao() )
		.mais( "-fechamento", true, new Argumento_fechamento() )
		.mais( "-fechamentoDia", false, new Argumento_fechamentoDia() )
		.mais( "-esperar", false, new Argumento_esperar() )
		.mais( "-?", false, new Argumento_ajuda() )
		.getChaveadosArgumentos();

		argumentos.processarArgumentos( new Argumento_null() );
		
		if( arquivo == null ) throw new IllegalArgumentException( "Informe o arquivo." );
		
		return argumentos;

	}
	
	@Override
	protected void principal() {
		
		try{
			
			setConsole( new SwingConsole( getCultura(), "Aranha", consoleFonte, consoleLinhas, consoleColunas, sairAoFechar ) );

			carregarArquivo();
			
			JFrame janela = ((SwingConsole)getConsole()).getJanela();
			janela.setLocation( janelaX, janelaY );
			if( janelaL != 0 && janelaA != 0 ) janela.setSize( janelaL, janelaA );
			janela.addKeyListener( new AlterarOrdem() );
			
			while( true ){
				contando = true;
				for( long i = intervalo; i > 0; i-- ){
					while( pausaAtivada ) Thread.sleep( 100 );
					janela.setTitle( ( titulo != null ? titulo : arquivo.getName() ) + " (" + i + " s)" );
					Thread.sleep( 1000 );
				}
				contando = false;
				carregarArquivo();
			}
			
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

			pontos.clear();
			
			Ponto ponto = null;
			Foco foco = null;
			IP ip = null;
			MAC mac = null;
			
			System.gc();
			
			CSVUtil.proximaLinha( entrada, colunas, ';', buffer );
			
			while( CSVUtil.proximaLinha( entrada, colunas, ';', buffer ) > 0 ){
				
				try{
					ip = new IP( colunas[0] );
				}catch( Exception e ){
					mac = new MAC( colunas[0] );
				}finally{
					foco = ip != null ? new Foco( ip ) : new Foco( mac );
					ponto = new Ponto( foco );
					pontos.add( ponto );
				}
				
				ponto.pacotesEnviados  = Long.parseLong( colunas[1] );
				ponto.pacotesRecebidos = Long.parseLong( colunas[2] );
				ponto.bytesEnviados    = Long.parseLong( colunas[3] );
				ponto.bytesRecebidos   = Long.parseLong( colunas[4] );
				
			}
			
			entrada.close();

			///////
			
			pontosOrdenados = new Ponto[ pontos.size() ];
			pontos.toArray( pontosOrdenados );
			ordenar();
			
			pacotesEnviados  = 0;
			bytesEnviados    = 0;
			pacotesRecebidos = 0;
			bytesRecebidos   = 0;
			for( Ponto p : pontosOrdenados ){
				pacotesEnviados  += p.pacotesEnviados;
				bytesEnviados    += p.bytesEnviados;
				pacotesRecebidos += p.pacotesRecebidos;
				bytesRecebidos   += p.bytesRecebidos;
			}
			pacotesTotal = pacotesEnviados + pacotesRecebidos;
			bytesTotal = bytesEnviados + bytesRecebidos;
			
			///////
			
			imprimirResultado();

		}catch( FileNotFoundException e ){
			throw new IOException( "Arquivo não encontrado: " + arquivo.getName() );
		}catch( NumberFormatException e ){
			throw new IOException( "Formato de arquivo inválido." );
		}catch( Exception e ){
			throw new IOException( "Erro desconhecido: " + e.getMessage() );
		}
		
	}
	
	private synchronized void ordenar() {
		
		Arrays.sort( pontosOrdenados, ordem );
		
		Foco f;
		Ranking r;
		for( int i = 0; i < pontosOrdenados.length; i++ ){
			f = pontosOrdenados[i].foco;
			r = ranking.get( f );
			if( r == null ) ranking.put( f, r = new Ranking() );
			r.total += i;
		}
		
		rankingContagem++;
		
	}
	
	private synchronized void imprimirResultado() {
		
		limpar();
		
		String tit = titulo != null ? titulo : arquivo.getName();
		enviarCentralizado( Cor.VERDE_INTENSA, tit );
		
		enviarCentralizado( Cor.CINZA_INTENSA, "PE = " + pacotesEnviados + " / PR = " + pacotesRecebidos );
		enviarCentralizado( Cor.CINZA_INTENSA, "BE = " + bytesEnviados + " / BR = " + bytesRecebidos );
		
		int numero = getTotalColunas() - ( 18 + 6 * 6 );
		numero = numero / 6 + 5;
		int restante = getTotalColunas() - ( 18 + 6 * ( numero + 1 ) );
		if( restante == 0 ){
			numero--;
			restante = 6;
		}
		setCorTextoFundo( Cor.CINZA_ESCURA );
		String barra = "%1$-18s %2$"+numero+"s %3$"+numero+"s %4$"+numero+"s %5$"+numero+"s %6$"+numero+"s %7$"+numero+"s";
		enviar( Cor.PRETA, barra + "%8$"+restante+"s", "Nodo", getTituloColuna( "PE" ), getTituloColuna( "PR" ), getTituloColuna( "BE" ), getTituloColuna( "BR" ), getTituloColuna( "PT" ), getTituloColuna( "BT" ), " " );
		setCorTextoFundo( Cor.PRETA );
		
		Ponto ponto;
		int total = getTotalLinhas() - 7;
		
		for( int i = 0; i < total && i < pontosOrdenados.length; i++ ){
			ponto = pontosOrdenados[i];
			enviar( ranking.get( ponto.foco ).getCor(), "%1$-18s %2$ "+numero+".1f %3$ "+numero+".1f %4$ "+numero+".1f %5$ "+numero+".1f %6$ "+numero+".1f %7$ "+numero+".1f\n",
					ponto.foco.toString(),
					pacotesEnviados == 0 ? 0 : (float) ponto.pacotesEnviados / pacotesEnviados * 100,
					pacotesRecebidos == 0 ? 0 : (float) ponto.pacotesRecebidos / pacotesRecebidos * 100,
					bytesEnviados == 0 ? 0 : (float) ponto.bytesEnviados / bytesEnviados * 100,
					bytesRecebidos == 0 ? 0 : (float) ponto.bytesRecebidos / bytesRecebidos * 100,
					pacotesTotal == 0 ? 0 : (float) ( ponto.pacotesEnviados + ponto.pacotesRecebidos ) / pacotesTotal * 100,
					bytesTotal == 0 ? 0 : (float) ( ponto.bytesEnviados + ponto.bytesRecebidos ) / bytesTotal * 100
					);
		}
		
		total -= pontosOrdenados.length;
		for( int i = 0; i < total; i++ ) enviarln();

		enviar( Cor.CINZA_ESCURA, barra + "\n", " ", "1-PE", "2-PR", "3-BE", "4-BR", "5-PT", "6-BT" );
		enviarCentralizado( Cor.CINZA_ESCURA, "P=Pacotes B=Bytes E=Enviados R=Recebidos T=Total" );
		
		numero = rankingContagem;
		int algarismos = 0;
		while( numero > 0 ){
			numero /= 10;
			algarismos++;
		}
		numero = 10 + ( CORES.length * 2 ) + 8 + algarismos + 9;
		numero = ( getTotalColunas() - numero ) / 2 + 10;
		enviar( Cor.CINZA_ESCURA, "%1$" + numero + "s", "Ranking: +" );
		for( int i = 0; i < CORES.length - 1; i++ ){
			guardarCores();
			setCorTextoFundo( CORES[i] );
			enviar( "  " );
			restaurarCores();
		}
		guardarCores();
		setCorTextoFundo( CORES[CORES.length-1] );
		enviar( "  " );
		restaurarCores();
		enviar( Cor.CINZA_ESCURA, "- Passo=" + rankingContagem + " P-" + ( pausaAtivada ? "Contin" : "Pausar" ) );
		
	}
	
	private static void mostrarErro( String mensagem ) {
		JOptionPane.showMessageDialog( null, mensagem, "Aranha", JOptionPane.ERROR_MESSAGE );
	}
	
	private String getTituloColuna( String nome ) {
		return ordem.toString().equals( nome ) ? "(" + nome + ")" : nome;
	}
	
	private class Argumento_null extends AdaptadoArgumentoProcessador {
		public void processar( Chave chave, String valor ) throws IllegalArgumentException {
			if( arquivo != null ) throw new IllegalArgumentException( "Arquivo ja definido." );
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
	
	private class Argumento_titulo extends AdaptadoArgumentoProcessador {
		public void processar( Chave chave, String valor ) throws IllegalArgumentException {
			titulo = valor;
		}
	}
	
	private class Argumento_ordem extends AdaptadoArgumentoProcessador {
		public void processar( Chave chave, String valor ) throws IllegalArgumentException {
			String o = valor.toUpperCase();
			     if( o.equals( "PE" ) ) ordem = ORDEM_PACOTES_ENVIADOS;
			else if( o.equals( "PR" ) ) ordem = ORDEM_PACOTES_RECEBIDOS;
			else if( o.equals( "PT" ) ) ordem = ORDEM_PACOTES_TOTAL;
			else if( o.equals( "BE" ) ) ordem = ORDEM_BYTES_ENVIADOS;
			else if( o.equals( "BR" ) ) ordem = ORDEM_BYTES_RECEBIDOS;
			else if( o.equals( "BT" ) ) ordem = ORDEM_BYTES_TOTAL;
			else throw new IllegalArgumentException( "Ordem invalida: " + valor );
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
	
	private class Argumento_console extends AdaptadoArgumentoProcessador {
		public void processar( Chave chave, String valor ) throws IllegalArgumentException {
		
			try{
				
				NumeroTransformacao it = getCultura().novaInteiroTransformacao();
				
				SeparadorTextual st = new SeparadorTextual( new char[]{ ',', ',' } );
				st.executar( valor );
				
				consoleFonte   = (int) it.transformarInteiro( st.getParte( 0 ) );
				consoleLinhas  = (int) it.transformarInteiro( st.getParte( 1 ) );
				consoleColunas = (int) it.transformarInteiro( st.getParte( 2 ) );
				
			}catch( Exception e ){
				throw new IllegalArgumentException( "Configuração de console incorreta: " + valor );
			}
			
			if( consoleColunas < 55 ) throw new IllegalArgumentException( "Mínimo de colunas: 55" );
			
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
	
	private class Argumento_posicao extends AdaptadoArgumentoProcessador {
		public void processar( Chave chave, String valor ) throws IllegalArgumentException {
		
			try{
				
				NumeroTransformacao it = getCultura().novaInteiroTransformacao();
				
				SeparadorTextual st = new SeparadorTextual( ',' );
				st.executar( valor );
				
				janelaX = (int) it.transformarInteiro( st.getParte( 0 ) );
				janelaY = (int) it.transformarInteiro( st.getParte( 1 ) );
				
			}catch( Exception e ){
				throw new IllegalArgumentException( "Posição incorreta: " + valor );
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
	
	private class Argumento_ajuda extends AdaptadoArgumentoProcessador {
		
		public void processar( Chave chave, String valor ) throws IllegalArgumentException {
			Aranha.enviarCabecalho( Nodos.this );
			enviarln();
			arg( "-titulo",        "Titulo do relatorio." );
			arg( "-ordem",         "Classificar por: PE, PR, BE, BR, PT ou BT. Padrao: BT" );
			arg( "-intervalo",     "Intervalo para atualizacao, em segundos. Padrao: 60" );
			arg( "-console",       "Configuracao do console: fonte,linhas,colunas. Padrao: 18,20,60" );
			arg( "-janela",        "Posicao e tamanho da janela na tela: x,y,largura,altura" );
			arg( "-posicao",       "Posicao da janela na tela: x,y" );
			arg( "-fechamento",    "Tempo, em segundos, para fechamento automatico do programa." );
			arg( "-fechamentoDia", "Fecha automaticamente o programa ao final do dia." );
			arg( "-esperar",       "Espera pela criacao do arquivo." );
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
				System.exit( 0 );
			}catch( Exception e ){
			}
		}
		
	}
	
	private static class OrdemPacotesEnviados implements Comparator<Ponto> {
		public int compare( Ponto o1, Ponto o2 ) {
			long n1 = o1.pacotesEnviados;
			long n2 = o2.pacotesEnviados;
			return n1 < n2 ? 1 : n1 > n2 ? -1 : 0;
		}
		public String toString() {
			return "PE";
		}
	}
	
	private static class OrdemPacotesRecebidos implements Comparator<Ponto> {
		public int compare( Ponto o1, Ponto o2 ) {
			long n1 = o1.pacotesRecebidos;
			long n2 = o2.pacotesRecebidos;
			return n1 < n2 ? 1 : n1 > n2 ? -1 : 0;
		}
		public String toString() {
			return "PR";
		}
	}
	
	private static class OrdemBytesEnviados implements Comparator<Ponto> {
		public int compare( Ponto o1, Ponto o2 ) {
			long n1 = o1.bytesEnviados;
			long n2 = o2.bytesEnviados;
			return n1 < n2 ? 1 : n1 > n2 ? -1 : 0;
		}
		public String toString() {
			return "BE";
		}
	}
	
	private static class OrdemBytesRecebidos implements Comparator<Ponto> {
		public int compare( Ponto o1, Ponto o2 ) {
			long n1 = o1.bytesRecebidos;
			long n2 = o2.bytesRecebidos;
			return n1 < n2 ? 1 : n1 > n2 ? -1 : 0;
		}
		public String toString() {
			return "BR";
		}
	}

	private static class OrdemPacotesTotal implements Comparator<Ponto> {
		public int compare( Ponto o1, Ponto o2 ) {
			long n1 = o1.pacotesEnviados + o1.pacotesRecebidos;
			long n2 = o2.pacotesEnviados + o2.pacotesRecebidos;
			return n1 < n2 ? 1 : n1 > n2 ? -1 : 0;
		}
		public String toString() {
			return "PT";
		}
	}
	
	private static class OrdemBytesTotal implements Comparator<Ponto> {
		public int compare( Ponto o1, Ponto o2 ) {
			long n1 = o1.bytesEnviados + o1.bytesRecebidos;
			long n2 = o2.bytesEnviados + o2.bytesRecebidos;
			return n1 < n2 ? 1 : n1 > n2 ? -1 : 0;
		}
		public String toString() {
			return "BT";
		}
	}
	
	private class AlterarOrdem extends KeyAdapter {
		
		@Override
		public void keyTyped( KeyEvent e ) {
			
			try{
				
				Comparator<Ponto> op = null;
				
				switch( e.getKeyChar() ){
					
					case 'P' :
					case 'p' :
						pausaAtivada = ! pausaAtivada;
						imprimirResultado();
						return;
					
					case '1' :
						op = ORDEM_PACOTES_ENVIADOS;
						break;
						
					case '2' :
						op = ORDEM_PACOTES_RECEBIDOS;
						break;
						
					case '3' :
						op = ORDEM_BYTES_ENVIADOS;
						break;
						
					case '4' :
						op = ORDEM_BYTES_RECEBIDOS;
						break;
						
					case '5' :
						op = ORDEM_PACOTES_TOTAL;
						break;
						
					case '6' :
						op = ORDEM_BYTES_TOTAL;
						break;
					
				}
				
				if( op == null ) return;
				
				while( ! contando ) Thread.sleep( 100 );
					
				ordem = op;
				for( Ranking r : ranking.values() ) r.total = 0;
				rankingContagem = 0;
				ordenar();
				imprimirResultado();
				
			}catch( Exception exception ){
			}
			
		}
		
	}

	public static final Cor[] CORES = { Cor.VERMELHA_INTENSA, Cor.MAGENTA_INTENSA, Cor.AMARELA, Cor.VERDE_INTENSA, Cor.AZUL_INTENSA, Cor.CIANO_INTENSA, Cor.BRANCA, Cor.CINZA_ESCURA };
	
	private class Ranking {
		
		private int total = 0;
		
		public int getMedia() {
			return total / rankingContagem;
		}
		
		public Cor getCor() {
			int media = getMedia();
			if( media >= CORES.length ) media = CORES.length - 1;
			return CORES[ media ];
		}
		
	}
	
}
