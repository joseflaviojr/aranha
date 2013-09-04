
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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import javax.swing.JOptionPane;

import com.joseflavio.tqc.console.AplicacaoConsole;
import com.joseflavio.tqc.console.Argumentos;
import com.joseflavio.tqc.console.ChaveadosArgumentos;
import com.joseflavio.tqc.console.ChaveadosArgumentos.AdaptadoArgumentoProcessador;
import com.joseflavio.tqc.console.ChaveadosArgumentos.Chave;
import com.joseflavio.tqc.console.ChaveadosArgumentosBuilder;
import com.joseflavio.tqc.console.Cor;
import com.joseflavio.tqc.console.SwingConsole;

/**
 * @author José Flávio de Souza Dias Júnior
 * @version 2011
 */
public class ControlarEspacoLivre extends AplicacaoConsole {
	
	private File fonte = new File( System.getProperty( "user.dir" ) );
	
	private String prefixo = "cap";
	
	private String sufixo = "";
	
	private int digitos = 3;
	
	private long espaco = 512 * 1024 * 1024; //512 MB
	
	private final SimpleDateFormat logDataFormato = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
	
	private boolean sairAoFechar = true;
	
	public ControlarEspacoLivre( String[] args, boolean sairAoFechar ) {
		this.sairAoFechar = sairAoFechar;
		executar( this, args );
	}
	
	public static void main( String[] args ) {
		new ControlarEspacoLivre( args, true );
	}
	
	private static void executar( ControlarEspacoLivre aplicacao, String[] args ) {
		
		try{
			
			aplicacao.inicio( args );
			
		}catch( Exception e ){
			
			aplicacao.enviarln( Cor.VERMELHA_INTENSA, e.getMessage() );
			mostrarErro( e.getMessage() );
			
		}
		
	}
	
	@Override
	protected Argumentos processarArgumentos( String[] args ) {

		ChaveadosArgumentos argumentos = new ChaveadosArgumentosBuilder( args )
		.mais( "-fonte", true, new Argumento_fonte() )
		.mais( "-prefixo", true, new Argumento_prefixo() )
		.mais( "-sufixo", true, new Argumento_sufixo() )
		.mais( "-digitos", true, new Argumento_digitos() )
		.mais( "-espaco", true, new Argumento_espaco() )
		.mais( "-?", false, new Argumento_ajuda() )
		.getChaveadosArgumentos();
		
		argumentos.processarArgumentos( null );
		
		if( digitos <= 0 ) throw new IllegalArgumentException( "Quantidade de digitos incorreta: " + digitos );
		
		return argumentos;
		
	}
	
	@Override
	protected void principal() {

		try{
			
			setConsole( new SwingConsole( "Controle de Espaço Livre", sairAoFechar ) );

			Date data;
			long atual;
			
			while( true ){
				
				Thread.sleep( 10000 );
				
				data = new Date();
				atual = fonte.getFreeSpace();
				
				enviarln( "[" + logDataFormato.format( data ) + "] Espaço Livre = " + (int)( atual / 1024f / 1024f ) + " MB ( Mínimo " + (int)( espaco / 1024f / 1024f ) + " )" );
				
				if( atual <= espaco ){
					
					enviarln( Cor.AMARELA, "[" + logDataFormato.format( data ) + "] Limite atingido = " + (int)( atual / 1024f / 1024f ) + " MB"  );
			
					File[] arquivos = fonte.listFiles();
					boolean apagou = false;
					
					Arrays.sort( arquivos, new TCPDumpComparator( true ) );
					
					for( File arquivo : arquivos ){
						
						String nome = arquivo.getName();
						if( nome.charAt( 0 ) == '.' ) nome = nome.substring( 1 );
						
						if( ! Util.obterPrefixo( nome ).equals( prefixo ) ) continue;
						if( ! Util.obterSufixo( nome ).equals( sufixo ) ) continue;
						if( Util.obterDigitos( nome ) != digitos ) continue;
						
						enviar( Cor.VERMELHA_INTENSA, "[" + logDataFormato.format( new Date() ) + "] Excluindo " + arquivo.getName() + "... " );
						
						apagou = arquivo.delete();
						
						enviarln( apagou ? "OK" : "ERRO" );
						
						break;
						
					}
					
					if( ! apagou ) mostrarErro( "Não foi possível apagar pelo menos um arquivo para aumentar o espaço livre." );
				
				}
			
			}
			
		}catch( Exception e ){
			throw new IllegalArgumentException( e.getMessage() );
		}
		
	}
	
	@Override
	protected void fim() {
	}
	
	private static void mostrarErro( String mensagem ) {
		JOptionPane.showMessageDialog( null, mensagem, "Controle de Espaço Livre", JOptionPane.ERROR_MESSAGE );
	}
	
	private class Argumento_fonte extends AdaptadoArgumentoProcessador {
		public void processar( Chave chave, String valor ) throws IllegalArgumentException {
			fonte = new File( valor );
			if( ! fonte.exists() ) fonte = new File( new File( System.getProperty( "user.dir" ) ), valor );
			if( ! fonte.exists() || ! fonte.isDirectory() ) throw new IllegalArgumentException( "Fonte inexistente: " + valor );
		}
	}
	
	private class Argumento_prefixo extends AdaptadoArgumentoProcessador {
		public void processar( Chave chave, String valor ) throws IllegalArgumentException {
			prefixo = valor;
		}
	}
	
	private class Argumento_sufixo extends AdaptadoArgumentoProcessador {
		public void processar( Chave chave, String valor ) throws IllegalArgumentException {
			sufixo = valor;
		}
	}

	private class Argumento_digitos extends AdaptadoArgumentoProcessador {
		public void processar( Chave chave, String valor ) throws IllegalArgumentException {
			try{
				digitos = (int) getCultura().novaInteiroTransformacao().transformarInteiro( valor );
			}catch( Exception e ){
				throw new IllegalArgumentException( "Quantidade de digitos incorreta: " + valor );
			}
		}
	}
	
	private class Argumento_espaco extends AdaptadoArgumentoProcessador {
		public void processar( Chave chave, String valor ) throws IllegalArgumentException {
			try{
				espaco = getCultura().novaInteiroTransformacao().transformarInteiro( valor );
				espaco *= 1024 * 1024;
			}catch( Exception e ){
				throw new IllegalArgumentException( "Espaco livre minimo incorreto: " + valor );
			}
		}
	}
	
	private class Argumento_ajuda extends AdaptadoArgumentoProcessador {
		
		public void processar( Chave chave, String valor ) throws IllegalArgumentException {
			Aranha.enviarCabecalho( ControlarEspacoLivre.this );
			enviarln();
			arg( "-fonte",      "Diretorio no qual os arquivos para analise estao localizados. Padrao: diretorio corrente." );
			arg( "-prefixo",    "Prefixo do nome dos arquivos a serem analisados. Padrao: \"cap\"." );
			arg( "-sufixo",     "Sufixo do nome dos arquivos a serem analisados. Padrao: em branco." );
			arg( "-digitos",    "Quantidade de digitos do indice, a qual e obtida, caso necessario, com insercao de zeros a esquerda. Padrao: 3." );
			arg( "-espaco",     "Espaco livre desejado, em MB. Padrao: 512." );
			arg( "-?",          "Esta ajuda." );
			System.exit( 0 );
		}
		
		private void arg( String comando, String explicacao ) {
			enviar( Cor.VERDE_INTENSA, "%1$-10s", comando );
			enviarln( Cor.CINZA_INTENSA, explicacao );
		}
		
	}
	
}
