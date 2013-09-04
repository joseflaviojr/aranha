
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
import java.io.IOException;
import java.util.Date;

import javax.swing.JFileChooser;

import com.joseflavio.cultura.Cultura;
import com.joseflavio.tqc.console.AplicacaoConsole;
import com.joseflavio.tqc.console.Argumentos;
import com.joseflavio.tqc.console.ChaveadosArgumentos;
import com.joseflavio.tqc.console.ChaveadosArgumentos.AdaptadoArgumentoProcessador;
import com.joseflavio.tqc.console.ChaveadosArgumentos.Chave;
import com.joseflavio.tqc.console.ChaveadosArgumentosBuilder;
import com.joseflavio.tqc.console.Cor;
import com.joseflavio.tqc.console.SwingConsole;
import com.joseflavio.util.Calendario;

/**
 * Navegador de Pacotes.
 * @author José Flávio de Souza Dias Júnior
 * @version 2011
 */
public class Navegador extends AplicacaoConsole {

	private File fonte;
	
	private String prefixo;
	
	private String sufixo;
	
	private int digitos;
	
	private int indice;
	
	private LibpcapLeitor pcap;
	
	private boolean sairAoFechar = true;
	
	public Navegador( String[] args, boolean sairAoFechar ) {
		this.sairAoFechar = sairAoFechar;
		executar( this, args );
	}
	
	public static void main( String[] args ) {
		new Navegador( args, true );
	}
	
	private void executar( Navegador navegador, String[] args ) {
		
		try{
			
			navegador.inicio( args );
			
		}catch( IllegalArgumentException e ){

			Aranha.enviarCabecalho( navegador );
			navegador.enviarln();
			
			if( e.getMessage() != null ){
				navegador.enviarln( Cor.VERMELHA_INTENSA, "ERRO: " + e.getMessage() + "\n" );
			}
			
			navegador.enviarln( Cor.BRANCA, "Argumentos:\n" );
			
			navegador.imprimirArgumento( "<LIVRE>",       "Endereco do primeiro arquivo de captura." );
			navegador.imprimirArgumento( "-dialogo",      "Dialogo visual para definir os principais argumentos." );
			navegador.imprimirArgumento( "-?",            "Esta ajuda." );
			
		}
		
	}
	
	@Override
	protected Argumentos processarArgumentos( String[] args ) {
		
		ChaveadosArgumentos argumentos = new ChaveadosArgumentosBuilder( args )
		.mais( "-dialogo", false, new Argumento_dialogo() )
		.mais( "-?", false, new Argumento_ajuda() )
		.getChaveadosArgumentos();
		
		if( argumentos.getTotalArgumentos() == 0 ) throw new IllegalArgumentException( "Informe os argumentos necessarios." );
		
		argumentos.processarArgumentos( new Argumento_null() );
		
		return argumentos;
			
	}
	
	@Override
	protected void principal() {
		
		try{

			/* ********************* */
			
			setConsole( new SwingConsole( Cultura.getPadrao(), "Navegador - Aranha 2011", 12, 30, 100, sairAoFechar ) );

			/* ********************* */
			
			abrir();
			
			while( true ){
				
				limpar();
				Aranha.enviarCabecalho( this );
				enviarln();
				
				//ARQUIVO
				enviarln( Cor.CINZA_ESCURA, "Arquivo: " + indice + " Pacote: " + pcap.getPacotesLidos() + " [" + pcap.getData() + "] " + pcap.getTamanhoGravado() + "/" + pcap.getTamanhoCapturado() + " bytes" );
				
				//ETHERNET
				enviarln( Cor.MAGENTA, "ETHERNET Origem = " + pcap.getEthernet_Origem() + "  Destino = " + pcap.getEthernet_Destino() + "  Tipo = " + getNomeEthernet( pcap.getEthernet_Tipo() ) );
				
				//VLAN
				if( pcap.getVLAN() != null ) enviarln( Cor.CINZA_ESCURA, "VLAN " + pcap.getVLAN_Id() + "  Prioridade=" + pcap.getVLAN_Prioridade() + "  CFI=" + pcap.getVLAN_CFI() );
				
				//MPLS
				for( int nivel = 0; nivel < pcap.getMPLS_Niveis(); nivel++ ){
					enviarln( Cor.CINZA_INTENSA, "MPLS " + pcap.getMPLS_Rotulo( nivel ) + "  Exp.Bits=" + pcap.getMPLS_Bits( nivel ) + "  TTL=" + pcap.getMPLS_TTL( nivel ) );
				}
				
				//IP
				if( pcap.isIP() ){
					enviarln( Cor.VERDE_INTENSA, "IP Origem = " + pcap.getIP_Origem() + "  Destino = " + pcap.getIP_Destino() );
					enviarln( Cor.VERDE_INTENSA, "IP Protocolo = " + getNomeIP( pcap.getIP_Tipo() ) + "  ToS = " + pcap.getIP_ToS().toString().substring( 3 ) );
				}
				
				//TCP
				if( pcap.isTCP() ){
					if( pcap.isTCPCompleto() ){
						enviarln( Cor.VERMELHA_INTENSA, "TCP Origem = " + pcap.getTCP_Origem() + "  Destino = " + pcap.getTCP_Destino() );
						enviarln( Cor.VERMELHA_INTENSA, "TCP Dados = " + pcap.getDadosTamanhoOriginal() + " bytes  Capturado = " + pcap.getDadosTamanho() + " bytes" );
					}else{
						enviarln( Cor.VERMELHA_INTENSA, "TCP Corrompido" );
					}
				}
				
				//PACOTE
				enviarln( Cor.BRANCA, new String( pcap.copiarPacotePara( new char[ pcap.getTamanhoGravado() ] ) ).replaceAll( "\n", " " ) );
				
				//COMANDOS
				enviarln( Cor.CINZA_ESCURA, "[N] -1  [M] +1  [Z] -1000  [X] +1000  [P] Pacote  [D] Data/Hora  [H] Hora  [S] Sair" );
				
				char cmd = Character.toUpperCase( esperar( false ) );
				
				switch( cmd ){
					
					case 'N' :
						recuar();
						break;
						
					case 'M' :
						avancar();
						break;
						
					case 'Z' :
						for( int i = 0; i < 1000; i++ ) recuar();
						break;
						
					case 'X' :
						for( int i = 0; i < 1000; i++ ) avancar();
						break;
						
					case 'P' :
						Long numPacote = receberInteiro( "Informe o numero do pacote: ", "Valor incorreto." );
						if( numPacote == null ) break;
						irParaPacote( numPacote );
						break;
					
					case 'D' :
						Date data = receberDataHora( "Informe a data/hora desejada: ", "Data/hora incorreta." );
						if( data == null ) break;
						irParaData( data.getTime() );
						break;
						
					case 'H' :
						Date hora = receberHora( "Informe o horario desejado: ", "Horario incorreto." );
						if( hora == null ) break;
						irParaData( new Calendario().setData( hora ).setDia( pcap.getData().getDia() ).setMes( pcap.getData().getMes() - 1 ).setAno( pcap.getData().getAno() ).getTimestamp() );
						break;
						
					case 'S' :
						System.exit( 0 );
						break;
					
				}
				
			}
			
			/* ********************* */

		}catch( Exception e ){
			enviarln( Cor.VERMELHA_INTENSA, "ERRO: " + e.getMessage() );
		}
		
	}

	@Override
	protected void fim() {
	}
	
	private void abrir() throws IOException {
		File arquivo = new File( fonte, Util.criarNomeUnidadeGravacao( prefixo, indice, sufixo, digitos ) );
		if( pcap != null ) pcap.fechar();
		pcap = new LibpcapLeitor( arquivo.getCanonicalPath(), new PacoteEsperancaImpl() );
		pcap.lerPacote();
	}
	
	private boolean avancar() throws IOException {
		if( ! pcap.lerPacote() ){
			File arquivo = new File( fonte, Util.criarNomeUnidadeGravacao( prefixo, indice + 1, sufixo, digitos ) );
			if( arquivo.exists() ){
				indice++;
				abrir();
				return true;
			}
			return false;
		}
		return true;
	}
	
	private boolean recuar() throws IOException {
		long atual = pcap.getPacotesLidos();
		if( atual > 1 ){
			abrir();
			for( long i = atual - 2; i > 0; i-- ) pcap.lerPacote();
			return true;
		}else{
			File arquivo = new File( fonte, Util.criarNomeUnidadeGravacao( prefixo, indice - 1, sufixo, digitos ) );
			if( arquivo.exists() ){
				indice--;
				abrir();
				while( pcap.lerPacote() );
				return true;
			}
			return false;
		}
	}
	
	private void irParaPacote( long numero ) throws IOException {
		if( numero < pcap.getPacotesLidos() ) abrir();
		while( numero > pcap.getPacotesLidos() && pcap.lerPacote() );
	}
	
	private void irParaData( long data ) throws IOException {
		long atual = pcap.getTimestamp();
		if( data > atual ){
			while( data > atual && avancar() ) atual = pcap.getTimestamp();
		}else{
			while( data < atual ){
				abrir();
				atual = pcap.getTimestamp();
				if( data < atual ){
					File arquivo = new File( fonte, Util.criarNomeUnidadeGravacao( prefixo, indice - 1, sufixo, digitos ) );
					if( ! arquivo.exists() ) break;
					indice--;
				}
			}
			while( data > atual && avancar() ) atual = pcap.getTimestamp();
		}
	}
	
	private void imprimirArgumento( String comando, String explicacao ) {
		
		enviar( Cor.VERDE_INTENSA, "%1$-14s", comando );
		enviarln( Cor.CINZA_INTENSA, explicacao );
		
	}
	
	private String getNomeEthernet( int protocolo ) {
		String nome = Nomes.getEthernetSimples( protocolo );
		return Util.obterHexFFFF( protocolo ) + ( nome != null && nome.length() > 0 ? "-" + nome : "" );
	}
	
	private String getNomeIP( int protocolo ) {
		String nome = Nomes.getIPSimples( protocolo );
		return protocolo + ( nome != null && nome.length() > 0 ? "-" + nome : "" );
	}
	
	private class PacoteEsperancaImpl implements PacoteEsperanca {
		public boolean esperarNovoPacote() {
			return false;
		}
	}
	
	private class Argumento_dialogo extends AdaptadoArgumentoProcessador {
		public void processar( Chave chave, String valor ) throws IllegalArgumentException {
			
			JFileChooser fileChooser = new JFileChooser( new File( System.getProperty( "user.dir" ) ) );
			fileChooser.setDialogTitle( "Arquivo de Captura Inicial" );
			fileChooser.setDialogType( JFileChooser.OPEN_DIALOG );
			fileChooser.setFileSelectionMode( JFileChooser.FILES_ONLY );
			fileChooser.showOpenDialog( null );
			File arquivo = fileChooser.getSelectedFile();
			if( arquivo == null || ! arquivo.exists() ) throw new IllegalArgumentException( "Arquivo de captura indefinido." );
			
			valor = arquivo.getName();
			
			fonte = arquivo.getParentFile();
			prefixo = Util.obterPrefixo( valor );
			indice = Util.obterIndice( valor );
			sufixo = Util.obterSufixo( valor );
			digitos = Util.obterDigitos( valor );
			
		}
	}
	
	private class Argumento_null extends AdaptadoArgumentoProcessador {
		public void processar( Chave chave, String valor ) throws IllegalArgumentException {
			
			File arquivo = new File( valor );
			if( ! arquivo.exists() ) arquivo = new File( new File( System.getProperty( "user.dir" ) ), valor );
			if( ! arquivo.exists() ) throw new IllegalArgumentException( "Arquivo inexistente: " + valor );
			
			valor = arquivo.getName();
			
			fonte = arquivo.getParentFile();
			prefixo = Util.obterPrefixo( valor );
			indice = Util.obterIndice( valor );
			sufixo = Util.obterSufixo( valor );
			digitos = Util.obterDigitos( valor );
			
		}
	}
	
	private class Argumento_ajuda extends AdaptadoArgumentoProcessador {
		public void processar( Chave chave, String valor ) throws IllegalArgumentException {
			throw new IllegalArgumentException();
		}
	}
	
}
