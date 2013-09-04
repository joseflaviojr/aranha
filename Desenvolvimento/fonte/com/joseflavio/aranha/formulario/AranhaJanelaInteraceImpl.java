
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

package com.joseflavio.aranha.formulario;


import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.joseflavio.aranha.Analise;
import com.joseflavio.aranha.Aranha;
import com.joseflavio.aranha.ControlarEspacoLivre;
import com.joseflavio.aranha.Grafico;
import com.joseflavio.aranha.Media;
import com.joseflavio.aranha.Navegador;
import com.joseflavio.aranha.Nodos;
import com.joseflavio.aranha.PosTCPDump;
import com.joseflavio.aranha.PreTCPDump;
import com.joseflavio.aranha.Util;
import com.joseflavio.aranha.analise.Dia;
import com.joseflavio.aranha.analise.HTTPDia;
import com.joseflavio.aranha.analise.NodosEthernet;
import com.joseflavio.aranha.analise.NodosIP;
import com.joseflavio.aranha.analise.NodosIPDia;
import com.joseflavio.aranha.analise.NodosIPFocosGravacao;
import com.joseflavio.aranha.analise.NodosIPGravacao;
import com.joseflavio.aranha.analise.Resumo;
import com.joseflavio.aranha.analise.ResumoDia;
import com.joseflavio.cultura.TransformacaoException;

/**
 * @author José Flávio de Souza Dias Júnior
 * @version 2011
 */
public class AranhaJanelaInteraceImpl implements AranhaJanelaInterface {
	
	private String tcpdumpComando = "tcpdump";
	
	private Process tcpdumpProcesso;
	
    public Map<String, String> sistema_abrirConfiguracao() {
    	
    	JFileChooser fileChooser = new JFileChooser( new File( System.getProperty( "user.home" ) ) );
		fileChooser.setDialogTitle( "Abrir Arquivo de Configuração" );
		fileChooser.setDialogType( JFileChooser.OPEN_DIALOG );
		fileChooser.setFileSelectionMode( JFileChooser.FILES_ONLY );
		fileChooser.setFileFilter( new FileNameExtensionFilter( "Aranha Configuração", "aranha" ) );
		fileChooser.showOpenDialog( null );
		File arquivo = fileChooser.getSelectedFile();
		
		if( arquivo != null && arquivo.exists() ){
			try{
				
				Properties prop = new Properties();
				prop.load( new FileInputStream( arquivo ) );
				
				String versao = prop.getProperty( "aranha.versao" );
				if( versao == null || Integer.parseInt( versao ) > 1 ) JOptionPane.showMessageDialog( null, "Arquivo incompatível.", "Aranha", JOptionPane.ERROR_MESSAGE );
				
				Map<String, String> mapa = new HashMap<String, String>();
				for( Object c : prop.keySet() ) mapa.put( (String) c, prop.getProperty( (String) c ) );
				return mapa;
				
			}catch( Exception e ){
				JOptionPane.showMessageDialog( null, "Arquivo incompatível.", "Aranha", JOptionPane.ERROR_MESSAGE );
			}
		}
		
        return null;
        
    }

    public void sistema_salvarConfiguracao( Map<String, String> propriedades ) {
    	
    	JFileChooser fileChooser = new JFileChooser( new File( System.getProperty( "user.home" ) ) );
		fileChooser.setDialogTitle( "Salvar Arquivo de Configuração" );
		fileChooser.setDialogType( JFileChooser.SAVE_DIALOG );
		fileChooser.setFileSelectionMode( JFileChooser.FILES_ONLY );
		fileChooser.setFileFilter( new FileNameExtensionFilter( "Aranha Configuração", "aranha" ) );
		fileChooser.showSaveDialog( null );
		File arquivo = fileChooser.getSelectedFile();
		
		if( arquivo != null ){
			try{
				
				if( ! arquivo.getName().endsWith( ".aranha" ) ) arquivo = new File( arquivo.getParentFile(), arquivo.getName() + ".aranha" );
				
				Properties prop = new Properties();
				
				prop.put( "aranha.versao", "1" );
				
				for( String c : propriedades.keySet() ) prop.put( c, propriedades.get( c ) );
				
				prop.store( new FileOutputStream( arquivo ), null );
				
			}catch( Exception e ){
				JOptionPane.showMessageDialog( null, "Erro ao salvar arquivo: " + e.getMessage(), "Aranha", JOptionPane.ERROR_MESSAGE );
			}
		}
        
    }

    public List<String> captura_interfaces(){
    	
    	try{
    		return captura_interfaces( "tcpdump" );
		}catch( Exception e1 ){
			try{
	    		return captura_interfaces( "windump" );
			}catch( Exception e2 ){
				return new ArrayList<String>();
			}
		}
    	
    }
    
    private List<String> captura_interfaces( String comando ) throws Exception {

    	this.tcpdumpComando = comando;
    	
		Process proc = Runtime.getRuntime().exec( comando + " -D" );
		proc.waitFor();
		
		List<String> lista = new ArrayList<String>();
		StringBuilder linha = new StringBuilder( 100 );
		
		InputStreamReader in = new InputStreamReader( proc.getInputStream() );
		char ch;
		while( true ){
			ch = (char) in.read();
			if( ch == (char) -1 ) break;
			if( ch == '\r' ) continue;
			if( ch == '\n' ){
				String str = linha.toString();
				str = str.substring( str.indexOf( '.' ) + 1 );
				int separador = str.indexOf( ' ' );
				String parte1 = separador > -1 ? str.substring( separador + 1 ) : null;
				String parte2 = separador > -1 ? str.substring( 0, separador ) : str;
				lista.add( ( parte1 != null ? parte1 : "(" + parte2 + ")" ) + " :: " + parte2 );
				linha.delete( 0, linha.length() );
			}else{
				linha.append( ch );
			}
		}
		
		in.close();
		proc.destroy();
		
		return lista;
		
    }

    public String captura_destino( String atual ){
        
    	try{
			
    		JFileChooser fileChooser = new JFileChooser( atual == null || atual.length() == 0 || atual.equals( " " ) ? new File( System.getProperty( "user.home" ) ) : new File( atual ).getParentFile() );
			fileChooser.setDialogTitle( "Destino da Captura" );
			fileChooser.setDialogType( JFileChooser.OPEN_DIALOG );
			fileChooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
			fileChooser.showOpenDialog( null );
			File arquivo = fileChooser.getSelectedFile();
			
			return arquivo != null && arquivo.exists() ? arquivo.getCanonicalPath() : atual;
			
		}catch( Exception e ){
			erro( "Problema: " + e.getMessage() );
			return atual;
		}
    	
    }

    public boolean captura_executar( final String interfac, final String destino, final String prefixo, final String tamanho, final String digitos, final String bytesPorPacote, final boolean preTCPDump, final boolean posTCPDump ) throws IllegalArgumentException {
        
    	if( ! validarArquivo( destino, false, "Destino inválido." ) ) return false;
    	if( ! validarTexto( prefixo, false, "Prefixo indefinido." ) ) return false;
    	if( ! validarInteiro( tamanho, false, "Incorreto: Tamanho = " + tamanho ) ) return false;
    	if( ! validarInteiro( digitos, false, "Incorreto: Dígitos = " + digitos ) ) return false;
    	if( ! validarInteiro( bytesPorPacote, false, "Incorreto: Bytes por Pacote = " + bytesPorPacote ) ) return false;
    	
		Thread thread = new Thread(){
			public void run() {
				
				try{
					
					if( preTCPDump ){
						PreTCPDump.main( new String[]{ "-fonte", destino, "-prefixo", prefixo, "-digitos", digitos } );
					}
					
					StringBuilder digTexto = new StringBuilder( "1" );
					int digTotal = Integer.parseInt( digitos );
					for( int i = 0; i < digTotal; i++ ) digTexto.append( '0' );

					tcpdumpProcesso = Runtime.getRuntime().exec( tcpdumpComando + " -C " + tamanho + " -W " + digTexto + " -s " + bytesPorPacote + " -i " + interfac.substring( interfac.indexOf( "::" ) + 3 ) + " -w \"" + destino + File.separator + prefixo + "\"" );
					tcpdumpProcesso.waitFor();
					
					if( posTCPDump ){
						PosTCPDump.main( new String[]{ "-fonte", destino, "-prefixo", prefixo, "-digitos", digitos } );
					}
					
				}catch( Exception e ){
				}
				
			}
		};
		
		try{
			
			thread.start();
			Thread.sleep( 2000 );
			
			return thread.isAlive();
			
		}catch( Exception e ){
			
			return false;
			
		}
		
    }
    
    public void captura_parar() {
    	
    	try{
			
			if( tcpdumpProcesso != null ){
				tcpdumpProcesso.destroy();
				tcpdumpProcesso = null;
			}
			
		}catch( Exception e ){
		}
    	
    }

    public String analise_capturaInicial( String atual ){

    	try{
    		
	    	JFileChooser fileChooser = new JFileChooser( atual == null || atual.length() == 0 || atual.equals( " " ) ? new File( System.getProperty( "user.home" ) ) : new File( atual ).getParentFile() );
	    	fileChooser.setDialogTitle( "Arquivo de Captura Inicial" );
			fileChooser.setDialogType( JFileChooser.OPEN_DIALOG );
			fileChooser.setFileSelectionMode( JFileChooser.FILES_ONLY );
			fileChooser.showOpenDialog( null );
			final File arquivo = fileChooser.getSelectedFile();
		
			return arquivo != null && arquivo.exists() ? arquivo.getCanonicalPath() : atual;
			
		}catch( Exception e ){
			erro( "Problema: " + e.getMessage() );
			return atual;
		}
        
    }

    public String analise_destino( String atual ){
        
    	try{
			
    		JFileChooser fileChooser = new JFileChooser( atual == null || atual.length() == 0 || atual.equals( " " ) ? new File( System.getProperty( "user.home" ) ) : new File( atual ).getParentFile() );
			fileChooser.setDialogTitle( "Destino da Análise" );
			fileChooser.setDialogType( JFileChooser.OPEN_DIALOG );
			fileChooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
			fileChooser.showOpenDialog( null );
			File arquivo = fileChooser.getSelectedFile();
			
			return arquivo != null && arquivo.exists() ? arquivo.getCanonicalPath() : atual;
			
		}catch( Exception e ){
			erro( "Problema: " + e.getMessage() );
			return atual;
		}
    	
    }

    public void analise_executar( String capturaInicial, String indiceFinal, boolean leituraEterna, String periodoInicial, String periodoFinal, String intervalo, String unidadeTempo, List<String> focos, String destino, boolean grafico, boolean resumoGeral, boolean resumoDiario, boolean trafegoGeralIP, boolean trafegoDiarioIP, boolean trafegoRecenteIP, boolean trafegoRecenteFocosIP, boolean trafegoGeralMAC, boolean registroURL ) throws IllegalArgumentException {
        
    	if( ! validarArquivo( capturaInicial, false, "Informe corretamente o arquivo de captura inicial." ) ) return;
    	if( ! leituraEterna && ! validarInteiro( indiceFinal, false, "Informe corretamente o índice final." ) ) return;
    	if( ! validarDataHora( periodoInicial, true, "Informe corretamente o período inicial." ) ) return;
    	if( ! validarDataHora( periodoFinal, true, "Informe corretamente o período final." ) ) return;
    	if( ! validarInteiro( intervalo, false, "Informe corretamente o intervalo." ) ) return;
    	if( ! validarInteiro( unidadeTempo, false, "Informe corretamente o intervalo." ) ) return;
    	if( ! validarArquivo( destino, false, "Informe corretamente o destino." ) ) return;
    	if( grafico == false && resumoGeral == false && resumoDiario == false && trafegoGeralIP == false && trafegoDiarioIP == false && trafegoRecenteIP == false && trafegoRecenteFocosIP == false && trafegoGeralMAC == false && registroURL == false ){
    		erro( "Escolha pelo menos um tipo de análise." );
    		return;
    	}
    	
    	final List<String> args = new ArrayList<String>();
    	StringBuilder str = new StringBuilder( 100 );
    	
    	args.add( "-arquivo" );
    	args.add( capturaInicial );
    	
    	if( ! leituraEterna ){
    		args.add( "-indicef" );
    		args.add( indiceFinal );
    	}else{
    		args.add( "-eterna" );	
    	}
    	
    	if( periodoInicial != null && periodoInicial.length() > 0 ){
    		args.add( "-inicio" );
    		args.add( periodoInicial );
    	}
    	
    	if( periodoFinal != null && periodoFinal.length() > 0 ){
    		args.add( "-fim" );
    		args.add( periodoFinal );
    	}
    	
    	args.add( "-intervalo" );
    	args.add( intervalo );
    	
    	args.add( "-unidadeTempo" );
    	args.add( unidadeTempo );
    	
    	if( focos.size() > 0 ){
    		
	    	args.add( "-foco" );
	    	str.delete( 0, str.length() );
	    	str.append( focos.get( 0 ) );
	    	for( int i = 1; i < focos.size(); i++ ) str.append( "," + focos.get( i ) );
	    	args.add( str.toString() );
	    	
	    	try{
    			Aranha.converter( str.toString() );
    		}catch( Exception e ){
    			erro( e.getMessage() );
    			return;
    		}
	    	
    	}
    	
    	args.add( "-saida" );
    	args.add( destino );
    	
    	args.add( "-analises" );
    	str.delete( 0, str.length() );
    	if( grafico ) maisAnalise( Dia.class, str );
    	if( resumoGeral ) maisAnalise( Resumo.class, str );
    	if( resumoDiario ) maisAnalise( ResumoDia.class, str );
    	if( trafegoGeralIP ) maisAnalise( NodosIP.class, str );
    	if( trafegoDiarioIP ) maisAnalise( NodosIPDia.class, str );
    	if( trafegoRecenteIP ) maisAnalise( NodosIPGravacao.class, str );
    	if( trafegoRecenteFocosIP ) maisAnalise( NodosIPFocosGravacao.class, str );
    	if( trafegoGeralMAC ) maisAnalise( NodosEthernet.class, str );
    	if( registroURL ) maisAnalise( HTTPDia.class, str );
    	args.add( str.toString() );

    	new Thread(){
			public void run(){
				try{
					new Aranha( args.toArray( new String[ args.size() ] ), false );
				}catch( Exception e ){
				}
			};
		}.start();
    	
    }
    
    private void maisAnalise( Class<? extends Analise> analise, StringBuilder destino ){
    	if( destino.length() > 0 ) destino.append( "," );
    	destino.append( analise.getName() );
    }

    public String relatorio_grafico_arquivo( String atual ){
    	
    	try{
    		
	    	JFileChooser fileChooser = new JFileChooser( atual == null || atual.length() == 0 || atual.equals( " " ) ? new File( System.getProperty( "user.home" ) ) : new File( atual ).getParentFile() );
	    	fileChooser.setDialogTitle( "Abrir Gráfico" );
			fileChooser.setDialogType( JFileChooser.OPEN_DIALOG );
			fileChooser.setFileSelectionMode( JFileChooser.FILES_ONLY );
			fileChooser.setFileFilter( new FileNameExtensionFilter( "Gráfico Aranha em CSV", "csv" ) );
			fileChooser.showOpenDialog( null );
			File arquivo = fileChooser.getSelectedFile();
		
			return arquivo != null && arquivo.exists() ? arquivo.getCanonicalPath() : atual;
			
		}catch( Exception e ){
			erro( "Problema: " + e.getMessage() );
			return atual;
		}
		
    }

    public List<String> relatorio_grafico_serie( String endereco ) {

    	List<String> lista = new ArrayList<String>( 20 );
    	
    	try{
    		
    		File arquivo = new File( endereco );
        	if( ! arquivo.exists() ) return lista;
    		
			BufferedReader br = new BufferedReader( new FileReader( arquivo ) );
			String linha = br.readLine();
			if( linha != null ){
				StringTokenizer st = new StringTokenizer( linha, ";" );
				while( st.hasMoreTokens() ){
					String coluna = st.nextToken();
					if( coluna != null && coluna.length() > 0 ) lista.add( coluna );
				}
			}
			br.close();
			
		}catch( Exception e ){
		}
    	
        return lista;
        
    }

    public void relatorio_grafico_visualizar( String arquivo, String titulo, int serieTotal, int[] serieIndice, Color[] serieCor, String[] serieRotulo, boolean podaAutomatica, String podaInicialData, String podaFinalData, String janela, String intervalo ) throws IllegalArgumentException {
        
    	if( ! validarArquivo( arquivo, false, "Informe corretamente o arquivo de gráfico." ) ) return;
    	if( ! validarHora( podaInicialData, true, "Informe corretamente o horário da poda inicial." ) ) return;
    	if( ! validarHora( podaFinalData, true, "Informe corretamente o horário da poda final." ) ) return;
    	if( ! validarListaInteiro( janela, true, "Informe corretamente os atributos da janela." ) ) return;
    	if( ! validarInteiro( intervalo, false, "Informe corretamente o intervalo." ) ) return;
    	
    	final List<String> args = new ArrayList<String>();
    	
    	args.add( arquivo );
    	
    	if( titulo != null && titulo.length() > 0 ){
    		args.add( "-titulo" );
    		args.add( titulo );
    	}

    	boolean mostrar[] = new boolean[ serieTotal ];
    	Arrays.fill( mostrar, false );
    	for( int i = 0; i < serieIndice.length; i++ ){
			if( serieIndice[i] >= 0 ){
				mostrar[ serieIndice[i] ] = true;
				args.add( "-cor" );
				args.add( serieIndice[i] + ":" + Util.obterHexFF( (byte) serieCor[i].getRed() ) + Util.obterHexFF( (byte) serieCor[i].getGreen() ) + Util.obterHexFF( (byte) serieCor[i].getBlue() ) );
				if( serieRotulo[i] != null && serieRotulo[i].length() > 0 ){
					args.add( "-rotulo" );
					args.add( serieIndice[i] + ":" + serieRotulo[i] );
				}
			}
		}
    	
    	StringBuilder naoMostrar = new StringBuilder( 100 );
    	for( int i = 0; i < mostrar.length; i++ ){
			if( ! mostrar[i] ){
				if( naoMostrar.length() > 0 ) naoMostrar.append( ',' );
				naoMostrar.append( i );				
			}
		}
    	if( naoMostrar.length() > 0 ){
    		args.add( "-naomostrar" );
    		args.add( naoMostrar.toString() );
    	}
    	
    	if( podaAutomatica ) args.add( "-podar" );
    	
    	if( podaInicialData != null && podaInicialData.length() > 0 ){
    		args.add( "-podaInicial" );
    		args.add( podaInicialData );
    	}
    	
    	if( podaFinalData != null && podaFinalData.length() > 0 ){
    		args.add( "-podaFinal" );
    		args.add( podaFinalData );
    	}
    	
    	if( janela != null && janela.length() > 0 ){
    		args.add( "-janela" );
    		args.add( janela );
    	}
    	
    	args.add( "-intervalo" );
		args.add( intervalo );
    	
    	new Thread(){
			public void run(){
				try{
					new Grafico( args.toArray( new String[ args.size() ] ), false );
				}catch( Exception e ){
				}
			};
		}.start();
    	
    }

    public String relatorio_medicao_arquivo( String atual ){
        
    	try{
    		
	    	JFileChooser fileChooser = new JFileChooser( atual == null || atual.length() == 0 || atual.equals( " " ) ? new File( System.getProperty( "user.home" ) ) : new File( atual ).getParentFile() );
	    	fileChooser.setDialogTitle( "Abrir Medição" );
			fileChooser.setDialogType( JFileChooser.OPEN_DIALOG );
			fileChooser.setFileSelectionMode( JFileChooser.FILES_ONLY );
			fileChooser.setFileFilter( new FileNameExtensionFilter( "Medição Aranha em CSV", "csv" ) );
			fileChooser.showOpenDialog( null );
			File arquivo = fileChooser.getSelectedFile();
		
			return arquivo != null && arquivo.exists() ? arquivo.getCanonicalPath() : atual;
			
		}catch( Exception e ){
			erro( "Problema: " + e.getMessage() );
			return atual;
		}
    	
    }

    public void relatorio_medicao_visualizar( String arquivo, String titulo, String ordem, String fonte, String matriz, String intervalo ) throws IllegalArgumentException {
        
    	if( ! validarArquivo( arquivo, false, "Informe corretamente o arquivo de medição." ) ) return;
    	if( ! validarTexto( ordem, false, "Informe corretamente a ordem de classificação." ) ) return;
    	if( ! validarInteiro( fonte, false, "Informe corretamente a fonte." ) ) return;
    	if( ! validarListaInteiro( matriz, false, "Informe corretamente a matriz." ) ) return;
    	if( ! validarInteiro( intervalo, false, "Informe corretamente o intervalo." ) ) return;
    	
    	final List<String> args = new ArrayList<String>();
    	
    	args.add( arquivo );
    	
    	if( titulo != null && titulo.length() > 0 ){
    		args.add( "-titulo" );
    		args.add( titulo );
    	}
    	
    	args.add( "-ordem" );
		args.add( ordem.substring( 0, 2 ) );
		
		args.add( "-console" );
		args.add( fonte + "," + matriz );

		args.add( "-intervalo" );
		args.add( intervalo );
		
    	new Thread(){
			public void run(){
				try{
					new Nodos( args.toArray( new String[ args.size() ] ), false );
				}catch( Exception e ){
				}
			};
		}.start();
    	
    }

    public void ferramentaNavegador() {
        
    	JFileChooser fileChooser = new JFileChooser( new File( System.getProperty( "user.home" ) ) );
    	fileChooser.setDialogTitle( "Arquivo de Captura Inicial" );
		fileChooser.setDialogType( JFileChooser.OPEN_DIALOG );
		fileChooser.setFileSelectionMode( JFileChooser.FILES_ONLY );
		fileChooser.showOpenDialog( null );
		final File arquivo = fileChooser.getSelectedFile();
		
		if( arquivo != null && arquivo.exists() ){
			new Thread(){
				public void run(){
					try{
						new Navegador( new String[]{ arquivo.getCanonicalPath() }, false );
					}catch( Exception e ){
					}
				};
			}.start();
		}
    	
    }

    public void ferramentaMedia() {
        
    	JFileChooser fileChooser = new JFileChooser( new File( System.getProperty( "user.home" ) ) );
    	fileChooser.setDialogTitle( "Selecionar Arquivos de Gráfico" );
		fileChooser.setDialogType( JFileChooser.OPEN_DIALOG );
		fileChooser.setFileSelectionMode( JFileChooser.FILES_ONLY );
		fileChooser.setFileFilter( new FileNameExtensionFilter( "Gráfico Aranha em CSV", "csv" ) );
		fileChooser.setMultiSelectionEnabled( true );
		fileChooser.showOpenDialog( null );
		File[] selecionados = fileChooser.getSelectedFiles();
		
		if( selecionados != null && selecionados.length > 0 ){
			
			File destino = null;
			
			for( File f : selecionados ){
		    	if( f != null && f.exists() ){
		    		destino = f.getParentFile();
		    		break;
		    	}
			}
	    	
			fileChooser = new JFileChooser( destino );
	    	fileChooser.setDialogTitle( "Salvar Resultado da Média" );
			fileChooser.setDialogType( JFileChooser.SAVE_DIALOG );
			fileChooser.setFileSelectionMode( JFileChooser.FILES_ONLY );
			fileChooser.setFileFilter( new FileNameExtensionFilter( "Gráfico Aranha em CSV", "csv" ) );
			fileChooser.showSaveDialog( null );
			destino = fileChooser.getSelectedFile();
			
			if( destino == null ){
				erro( "Informe o arquivo para resultado da média." );
				return;
			}
			
			if( ! destino.getName().endsWith( ".csv" ) ) destino = new File( destino.getParentFile(), destino.getName() + ".csv" );
			
			List<File> arquivos = new ArrayList<File>( 50 );
			
			for( File f : selecionados ){
		    	if( f != null && f.exists() ) arquivos.add( f );
			}
			
			if( arquivos.size() < 2 ){
				erro( "Informe pelo menos 2 arquivos de gráfico." );
				return;
			}
			
			try{
			
				Media.executarMedia( arquivos, destino );
				
			}catch( IOException e ){
				erro( "Erro de E/S: " + e.getMessage() );
			}catch( TransformacaoException e ){
				erro( "Erro de integridade: " + e.getMessage() );
			}
			
		}
		
    }
    
    public void ferramentaControleEspacoLivre() {
    	
    	JFileChooser fileChooser = new JFileChooser( new File( System.getProperty( "user.home" ) ) );
    	fileChooser.setDialogTitle( "Arquivo de Captura Inicial" );
		fileChooser.setDialogType( JFileChooser.OPEN_DIALOG );
		fileChooser.setFileSelectionMode( JFileChooser.FILES_ONLY );
		fileChooser.showOpenDialog( null );
		final File arquivo = fileChooser.getSelectedFile();
		
		final String espaco = JOptionPane.showInputDialog( null, "Informe o espaço livre desejado, em MB:", "Aranha", JOptionPane.INFORMATION_MESSAGE );
		if( ! validarInteiro( espaco, false, "Espaço livre incorreto: " + ( espaco != null ? espaco : "VAZIO" ) + " MB." ) ) return;
		
		if( arquivo != null && arquivo.exists() ){
			new Thread(){
				public void run(){
					try{
						String nomeArquivo = arquivo.getName();
						new ControlarEspacoLivre( new String[]{ "-fonte", arquivo.getParentFile().getCanonicalPath(), "-prefixo", Util.obterPrefixo( nomeArquivo ), "-digitos", ""+Util.obterDigitos( nomeArquivo ), "-espaco", espaco }, false );
					}catch( Exception e ){
					}
				};
			}.start();
		}
    	
    }
    
    public void finalizando() {
    	
    	captura_parar();
    	
    }
    
    private void erro( String mensagem ){
    	JOptionPane.showMessageDialog( null, mensagem, "Aranha", JOptionPane.ERROR_MESSAGE );
    }
    
    private boolean validarArquivo( String endereco, boolean podeNulo, String mensagemErro ){
    	if( ( ! podeNulo && ( endereco == null || endereco.length() == 0 ) ) || ! new File( endereco ).exists() ){
    		erro( mensagemErro );
    		return false;
    	}
    	return true;
    }
    
    private boolean validarTexto( String texto, boolean podeVazio, String mensagemErro ){
    	if( ! podeVazio && ( texto == null || texto.length() == 0 ) ){
    		erro( mensagemErro );
    		return false;
    	}
    	return true;
    }
    
    private boolean validarInteiro( String inteiro, boolean podeNulo, String mensagemErro ){
    	try{
    		if( podeNulo && ( inteiro == null || inteiro.length() == 0 ) ) return true;
			Long.parseLong( inteiro );
			return true;
		}catch( Exception e ){
			erro( mensagemErro );
			return false;
		}
    }
    
    private boolean validarListaInteiro( String lista, boolean podeNulo, String mensagemErro ){
    	try{
    		if( podeNulo && ( lista == null || lista.length() == 0 ) ) return true;
    		StringTokenizer st = new StringTokenizer( lista, "," );
    		while( st.hasMoreTokens() ) Long.parseLong( st.nextToken() );
			return true;
		}catch( Exception e ){
			erro( mensagemErro );
			return false;
		}
    }
    
    private boolean validarDataHora( String dataHora, boolean podeNulo, String mensagemErro ){
    	try{
    		if( podeNulo && ( dataHora == null || dataHora.length() == 0 ) ) return true;
    		new SimpleDateFormat( "dd/MM/yyyy HH:mm:ss" ).parse( dataHora );
			return true;
		}catch( Exception e ){
			erro( mensagemErro );
			return false;
		}
    }
    
    private boolean validarHora( String hora, boolean podeNulo, String mensagemErro ){
    	try{
    		if( podeNulo && ( hora == null || hora.length() == 0 ) ) return true;
    		new SimpleDateFormat( "HH:mm:ss" ).parse( hora );
			return true;
		}catch( Exception e ){
			erro( mensagemErro );
			return false;
		}
    }

}
