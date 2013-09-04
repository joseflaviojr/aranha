
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
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.joseflavio.aranha.analise.Dia;
import com.joseflavio.aranha.analise.HTTPDia;
import com.joseflavio.aranha.analise.NodosEthernet;
import com.joseflavio.aranha.analise.NodosIP;
import com.joseflavio.aranha.analise.NodosIPDia;
import com.joseflavio.aranha.analise.NodosIPFocosGravacao;
import com.joseflavio.aranha.analise.NodosIPGravacao;
import com.joseflavio.aranha.analise.Resumo;
import com.joseflavio.aranha.analise.ResumoDia;
import com.joseflavio.aranha.filtro.javacc.AranhaFiltragem;
import com.joseflavio.tqc.console.AplicacaoConsole;
import com.joseflavio.tqc.console.Argumentos;
import com.joseflavio.tqc.console.ChaveadosArgumentos;
import com.joseflavio.tqc.console.ChaveadosArgumentos.AdaptadoArgumentoProcessador;
import com.joseflavio.tqc.console.ChaveadosArgumentos.Chave;
import com.joseflavio.tqc.console.ChaveadosArgumentosBuilder;
import com.joseflavio.tqc.console.Console;
import com.joseflavio.tqc.console.Cor;
import com.joseflavio.tqc.console.SwingConsole;

/**
 * Analisador de Pacotes Ethernet.
 * @author José Flávio de Souza Dias Júnior
 * @version 2011
 */
public class Aranha extends AplicacaoConsole {

	private List<Analise> analises = new ArrayList<Analise>();
	
	/**
	 * Diretório no qual os arquivos para análise estão localizados.
	 */
	private File fonte = new File( System.getProperty( "user.dir" ) );
	
	/**
	 * Diretório no qual os arquivos resultantes das análises serão armazenados.
	 */
	private File saida = new File( System.getProperty( "user.dir" ) );
	
	/**
	 * Prefixo do nome dos arquivos a serem analisados.
	 */
	private String prefixo = "cap";
	
	/**
	 * Sufixo do nome dos arquivos a serem analisados.
	 */
	private String sufixo = "";
	
	/**
	 * Índice do primeiro arquivo a ser analisado.
	 */
	private int indiceInicial = 0;
	
	/**
	 * Índice do último arquivo a ser analisado. Padrão: -1 == último existente.
	 */
	private int indiceFinal = -1;
	
	/**
	 * Quantidade de dígitos do índice, a qual é obtida, caso necessário, com inserção de zeros à esquerda.
	 */
	private int indiceDigitos = 3;
	
	/**
	 * Intervalo entre as gravações dos resultados parciais, em segundos.
	 */
	private long intervalo = 60;
	
	/**
	 * Data e hora a partir da qual será processado o primeiro pacote. Padrão: 0 == indefinida.
	 */
	private long tempoInicial = 0;
	
	/**
	 * Data e hora limite do último pacote a ser processado. Padrão: 0 == indefinida.
	 */
	private long tempoFinal = 0;
	
	/**
	 * {@link Foco}s das {@link Analise}s.
	 */
	private List<Foco> foco = new ArrayList<Foco>();
	
	/**
	 * Unidade de tempo padrão, em segundos.
	 */
	private long unidadeTempo = 10;
	
	private boolean leituraEterna = false;
	
	private Map<String, String> especifico = new HashMap<String, String>();

	private String ftpServidor;
	
	private String ftpUsuario;
	
	private String ftpSenha;
	
	private String ftpDestino = "/";
	
	private String ftpMascara = "(?!HTTP).*\\.[cC][sS][vV]";
	
	/**
	 * Índice do arquivo em atual processamento.
	 */
	private int indiceArquivo;
	
	private final SimpleDateFormat logDataFormato = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
	
	private int informacoesImpressas = 1000;
	
	private boolean sairAoFechar = true;
	
	public Aranha( String[] args, boolean sairAoFechar ) {
		this.sairAoFechar = sairAoFechar;
		executar( this, args );
	}
	
	public static void main( String[] args ) {
		new Aranha( args, true );
	}
	
	private void executar( Aranha aranha, String[] args ) {
		
		try{
			
			aranha.inicio( args );
			
		}catch( IllegalArgumentException e ){

			enviarCabecalho( aranha );
			aranha.enviarln();
			
			if( e.getMessage() != null ){
				aranha.enviarln( Cor.VERMELHA_INTENSA, "ERRO: " + e.getMessage() + "\n" );
			}
			
			aranha.enviarln( Cor.BRANCA, "Argumentos:\n" );
			
			aranha.imprimirArgumento( "-fonte",        "Diretorio no qual os arquivos para analise estao localizados. Padrao: diretorio corrente." );
			aranha.imprimirArgumento( "-saida",        "Diretorio no qual os arquivos resultantes das analises serao armazenados. Padrao: diretorio corrente." );
			aranha.imprimirArgumento( "-prefixo",      "Prefixo do nome dos arquivos a serem analisados. Padrao: \"cap\"." );
			aranha.imprimirArgumento( "-sufixo",       "Sufixo do nome dos arquivos a serem analisados. Padrao: em branco." );
			aranha.imprimirArgumento( "-indicei",      "Indice do primeiro arquivo a ser analisado. Padrao: 0." );
			aranha.imprimirArgumento( "-indicef",      "Indice do ultimo arquivo a ser analisado. Padrao: -1 == ultimo existente." );
			aranha.imprimirArgumento( "-digitos",      "Quantidade de digitos do indice, a qual e obtida, caso necessario, com insercao de zeros a esquerda. Padrao: 3." );
			aranha.imprimirArgumento( "-arquivo",      "Endereco do primeiro arquivo de captura. Define automaticamente fonte, prefixo, indicei, sufixo e digitos." );
			aranha.imprimirArgumento( "-intervalo",    "Intervalo entre as gravacoes dos resultados parciais, em segundos. Padrao: 60." );
			aranha.imprimirArgumento( "-inicio",       "Data e hora a partir da qual sera processado o primeiro pacote." );
			aranha.imprimirArgumento( "-fim",          "Data e hora limite do ultimo pacote a ser processado." );
			aranha.imprimirArgumento( "-analises",     "Analisadores a serem utilizados. Ex.: Dia,com.joseflavio.aranha.analise.Resumo" );
			aranha.imprimirArgumento( "-foco",         "Focos dos analisadores. Ex.: 10.0.0.21,!127.*.*.*+192.168.*.*,0001026354FC,eth.tipo=0x8100,vlan=100,mpls=21" );
			aranha.imprimirArgumento( "-unidadeTempo", "Unidade de tempo padrao, em segundos. Padrao: 10." );
			aranha.imprimirArgumento( "-eterna",       "Determina leitura eterna." );
			aranha.imprimirArgumento( "-ftpServidor",  "Servidor FTP para onde arquivos da saida serao copiados." );
			aranha.imprimirArgumento( "-ftpUsuario",   "Usuario para acesso ao Servidor FTP." );
			aranha.imprimirArgumento( "-ftpSenha",     "Senha para acesso ao Servidor FTP." );
			aranha.imprimirArgumento( "-ftpDestino",   "Diretorio de destino no FTP. Padrao: /" );
			aranha.imprimirArgumento( "-ftpMascara",   "Expressao regular que especifica os arquivos a serem copiados para o FTP. Padrao: (?!HTTP).*\\.[cC][sS][vV]" );
			aranha.imprimirArgumento( "-dialogo",      "Dialogo visual para definir os principais argumentos." );
			aranha.imprimirArgumento( "-x",            "Argumento especifico: chave=valor. Ex.: cor=azul" );
			aranha.imprimirArgumento( "-?",            "Esta ajuda." );
			
		}
		
	}
	
	@Override
	protected Argumentos processarArgumentos( String[] args ) {
		
		ChaveadosArgumentos argumentos = new ChaveadosArgumentosBuilder( args )
		.mais( "-fonte", true, new Argumento_fonte() )
		.mais( "-saida", true, new Argumento_saida() )
		.mais( "-prefixo", true, new Argumento_prefixo() )
		.mais( "-sufixo", true, new Argumento_sufixo() )
		.mais( "-indicei", true, new Argumento_indicei() )
		.mais( "-indicef", true, new Argumento_indicef() )
		.mais( "-digitos", true, new Argumento_digitos() )
		.mais( "-arquivo", true, new Argumento_arquivo() )
		.mais( "-intervalo", true, new Argumento_intervalo() )
		.mais( "-inicio", true, new Argumento_inicio() )
		.mais( "-fim", true, new Argumento_fim() )
		.mais( "-analises", true, new Argumento_analises() )
		.mais( "-foco", true, new Argumento_foco() )
		.mais( "-unidadeTempo", true, new Argumento_unidadeTempo() )
		.mais( "-eterna", false, new Argumento_eterna() )
		.mais( "-ftpServidor", true, new Argumento_ftpServidor() )
		.mais( "-ftpUsuario", true, new Argumento_ftpUsuario() )
		.mais( "-ftpSenha", true, new Argumento_ftpSenha() )
		.mais( "-ftpDestino", true, new Argumento_ftpDestino() )
		.mais( "-ftpMascara", true, new Argumento_ftpMascara() )
		.mais( "-dialogo", false, new Argumento_dialogo() )
		.mais( "-x", true, new Argumento_extra() )
		.mais( "-?", false, new Argumento_ajuda() )
		.getChaveadosArgumentos();
		
		if( argumentos.getTotalArgumentos() == 0 ) throw new IllegalArgumentException( "Informe os argumentos necessarios." );
		
		argumentos.processarArgumentos( new Argumento_null() );
		
		if( indiceDigitos <= 0 ) throw new IllegalArgumentException( "Quantidade de digitos incorreta: " + indiceDigitos );
		
		if( analises.size() == 0 ){
			analises.add( new Resumo() );
			analises.add( new ResumoDia() );
			analises.add( new NodosEthernet() );
			analises.add( new NodosIP() );
			analises.add( new NodosIPDia() );
			analises.add( new NodosIPGravacao() );
			analises.add( new NodosIPFocosGravacao() );
			analises.add( new Dia() );
			analises.add( new HTTPDia() );
		}
		
		return argumentos;
			
	}
	
	@Override
	protected void principal() {
		
		try{

			/* ********************* */
			
			setConsole( new SwingConsole( "Aranha 2011", sairAoFechar ) );

			/* ********************* */
			
			for( Analise analise : analises ){
				analise.iniciar( this );
			}
			
			/* ********************* */
			
			if( ftpServidor != null && ftpUsuario != null && ftpSenha != null && ftpMascara != null ){
				new FTPCliente( ftpServidor, ftpUsuario, ftpSenha, saida, ftpDestino, ftpMascara, intervalo ).start();
			}
			
			/* ********************* */
			
			long tempoAgora, tempoUltimo = System.currentTimeMillis();
			final long tempoIntervalo = intervalo * 1000L;
			
			for( indiceArquivo = indiceInicial; indiceFinal == -1 || indiceArquivo <= indiceFinal; indiceArquivo++ ){
				
				File arquivo = new File( fonte, Util.criarNomeUnidadeGravacao( prefixo, indiceArquivo, sufixo, indiceDigitos ) );
				if( ! arquivo.exists() ) break;

				long tempoArquivo = System.currentTimeMillis();
				
				infoln( Cor.CINZA_INTENSA, logDataFormato.format( new Date( tempoArquivo ) ) + " Processando arquivo " + arquivo.getName() + "..." );
				
				LibpcapLeitor pcap = new LibpcapLeitor( arquivo.getCanonicalPath(), new PacoteEsperancaImpl() );
				long timestamp;

				while( pcap.lerPacote() ){
					
					timestamp = pcap.getTimestamp();
					if( tempoInicial != 0 && ( timestamp < tempoInicial ) ) continue;
					if( tempoFinal != 0 && ( timestamp > tempoFinal ) ) break;

					for( Analise analise : analises ){
						analise.analisar( pcap );
					}
					
					tempoAgora = System.currentTimeMillis();
					if( ( tempoAgora - tempoUltimo ) >= tempoIntervalo ){
						info( Cor.AMARELA, logDataFormato.format( new Date( tempoAgora ) ) + " > Gravando resultados correntes..." );
						for( Analise analise : analises ){
							analise.gravar();
						}
						tempoUltimo = tempoAgora;
						enviarln( Cor.AMARELA, " OK" );
					}
					
				}
				
				pcap.fechar();
				
				tempoArquivo = System.currentTimeMillis() - tempoArquivo;
				infoln( Cor.MAGENTA, logDataFormato.format( new Date( System.currentTimeMillis() ) ) + " " + arquivo.getName() + ": " + tempoArquivo + " ms" );
				
			}
			
			/* ********************* */
			
			for( Analise analise : analises ){
				analise.finalizar();
			}
			
			infoln( Cor.AMARELA, logDataFormato.format( new Date( System.currentTimeMillis() ) ) + " Aranha Finalizada." );
			
			/* ********************* */

		}catch( Exception e ){
			enviarln( Cor.VERMELHA_INTENSA, "ERRO: " + e.getMessage() );
		}
		
	}

	@Override
	protected void fim() {
	}
	
	public static void enviarCabecalho( Console c ) {
		
		c.enviarln( Cor.VERDE_INTENSA, "\n  Aranha - Monitoramento de Rede Ethernet - 2011.10" );
		
		c.enviarln( Cor.VERDE, "  Copyright (C) 2011 Jose Flavio de Souza Dias Junior" );
		c.enviarln( Cor.VERDE, "  This program is under the terms of the GNU GPLv3." );
		c.enviarln( Cor.VERDE, "  http://www.joseflavio.com/aranha/" );
		
	}
	
	private void prepararParaNovaInformacao() {

		if( informacoesImpressas < ( getTotalLinhas() - 15 ) ) return;

		informacoesImpressas = 0;
		limpar();
		
		enviarCabecalho( this );
		enviarln();
		
		enviar( Cor.CINZA_INTENSA, "  Foco: " );
		boolean primeiro = true;
		for( Foco f : foco ){
			enviar( Cor.CINZA_INTENSA, ( primeiro ? "" : ", " ) + f.toString() );	
			primeiro = false;
		}
		enviar( "\n\n" );
		
	}
	
	private void info( Cor corTexto, String texto ) {
		prepararParaNovaInformacao();
		enviar( corTexto, texto );
		informacoesImpressas++;
	}
	
	private void infoln( Cor corTexto, String texto ) {
		prepararParaNovaInformacao();
		enviarln( corTexto, texto );
		informacoesImpressas++;
	}
	
	private void imprimirArgumento( String comando, String explicacao ) {
		
		enviar( Cor.VERDE_INTENSA, "%1$-14s", comando );
		enviarln( Cor.CINZA_INTENSA, explicacao );
		
	}
	
	public File getFonte() {
		return fonte;
	}
	
	public File getSaida() {
		return saida;
	}
	
	public String getPrefixo() {
		return prefixo;
	}
	
	public String getSufixo() {
		return sufixo;
	}
	
	public int getIndiceInicial() {
		return indiceInicial;
	}

	public int getIndiceFinal() {
		return indiceFinal;
	}

	public int getIndiceDigitos() {
		return indiceDigitos;
	}
	
	public long getIntervalo() {
		return intervalo;
	}
	
	public String getEspecifico( String nome ) {
		return especifico.get( nome );
	}
	
	public List<Foco> getFoco() {
		return foco;
	}
	
	public long getUnidadeTempo() {
		return unidadeTempo;
	}
	
	public boolean isLeituraEterna() {
		return leituraEterna;
	}
	
	/**
	 * Converte uma lista de {@link Foco}s, expressa numa {@link String}, para uma {@link List}.
	 */
	public static List<Foco> converter( String listaDeFocos ) throws IllegalArgumentException {
		
		List<Foco> focos = new ArrayList<Foco>();
		StringTokenizer st = new StringTokenizer( listaDeFocos, "," );
		
		while( st.hasMoreTokens() ){
			
			String alvo = st.nextToken();
			
			try{
				
				if( alvo.contains( "=" ) || alvo.contains( "<" ) || alvo.contains( ">" ) ){
					focos.add( new Foco( new AranhaFiltragem( new StringReader( alvo ) ).parse(), alvo ) );
					
				}else if( alvo.contains( "." ) ){
					focos.add( new Foco( IP.instanciar( alvo ), alvo ) );
					
				}else{
					focos.add( new Foco( new MAC( alvo ), alvo ) );
				}
				
			}catch( Throwable e ){
				throw new IllegalArgumentException( "Foco incorreto: " + alvo, e );
			}
			
		}
		
		return focos;
		
	}
	
	private class PacoteEsperancaImpl implements PacoteEsperanca {
		
		public boolean esperarNovoPacote() {
			
			if( ! leituraEterna ) return false;
			
			File proximo = new File( fonte, Util.criarNomeUnidadeGravacao( prefixo, indiceArquivo + 1, sufixo, indiceDigitos ) );
			
			if( ! proximo.exists() ){
				try{
					Thread.sleep( 10000 );
				}catch( InterruptedException e ){
					return false;
				}
			}
			
			return ! proximo.exists();
			
		}
		
	}
	
	private class Argumento_fonte extends AdaptadoArgumentoProcessador {
		public void processar( Chave chave, String valor ) throws IllegalArgumentException {
			fonte = new File( valor );
			if( ! fonte.exists() ) fonte = new File( new File( System.getProperty( "user.dir" ) ), valor );
			if( ! fonte.exists() || ! fonte.isDirectory() ) throw new IllegalArgumentException( "Fonte inexistente: " + valor );
		}
	}
	
	private class Argumento_saida extends AdaptadoArgumentoProcessador {
		public void processar( Chave chave, String valor ) throws IllegalArgumentException {
			saida = new File( valor );
			if( ! saida.exists() ) saida = new File( new File( System.getProperty( "user.dir" ) ), valor );
			if( ! saida.exists() || ! saida.isDirectory() ) throw new IllegalArgumentException( "Saida inexistente: " + valor );
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
	
	private class Argumento_indicei extends AdaptadoArgumentoProcessador {
		public void processar( Chave chave, String valor ) throws IllegalArgumentException {
			try{
				indiceInicial = (int) getCultura().novaInteiroTransformacao().transformarInteiro( valor );
			}catch( Exception e ){
				throw new IllegalArgumentException( "Indice inicial incorreto: " + valor );
			}
		}
	}
	
	private class Argumento_indicef extends AdaptadoArgumentoProcessador {
		public void processar( Chave chave, String valor ) throws IllegalArgumentException {
			try{
				indiceFinal = (int) getCultura().novaInteiroTransformacao().transformarInteiro( valor );
			}catch( Exception e ){
				throw new IllegalArgumentException( "Indice final incorreto: " + valor );
			}
		}
	}
	
	private class Argumento_digitos extends AdaptadoArgumentoProcessador {
		public void processar( Chave chave, String valor ) throws IllegalArgumentException {
			try{
				indiceDigitos = (int) getCultura().novaInteiroTransformacao().transformarInteiro( valor );
			}catch( Exception e ){
				throw new IllegalArgumentException( "Quantidade de digitos incorreta: " + valor );
			}
		}
	}
	
	private class Argumento_arquivo extends AdaptadoArgumentoProcessador {
		public void processar( Chave chave, String valor ) throws IllegalArgumentException {
			
			File arquivo = new File( valor );
			if( ! arquivo.exists() ) arquivo = new File( new File( System.getProperty( "user.dir" ) ), valor );
			if( ! arquivo.exists() ) throw new IllegalArgumentException( "Arquivo inexistente: " + valor );
			
			valor = arquivo.getName();
			
			fonte = arquivo.getParentFile();
			prefixo = Util.obterPrefixo( valor );
			indiceInicial = Util.obterIndice( valor );
			sufixo = Util.obterSufixo( valor );
			indiceDigitos = Util.obterDigitos( valor );
			
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
			
			fileChooser = new JFileChooser( arquivo.getParentFile() );
			fileChooser.setDialogTitle( "Diretório de Saída - Local dos Relatórios" );
			fileChooser.setDialogType( JFileChooser.OPEN_DIALOG );
			fileChooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
			fileChooser.showOpenDialog( null );
			saida = fileChooser.getSelectedFile();
			if( saida == null || ! saida.exists() ) throw new IllegalArgumentException( "Diretório de saída indefinido." );
			
			valor = arquivo.getName();
			
			fonte = arquivo.getParentFile();
			prefixo = Util.obterPrefixo( valor );
			indiceInicial = Util.obterIndice( valor );
			sufixo = Util.obterSufixo( valor );
			indiceDigitos = Util.obterDigitos( valor );
			
			leituraEterna = JOptionPane.showConfirmDialog( null, "Leitura eterna?", "Aranha", JOptionPane.YES_NO_OPTION ) == JOptionPane.YES_OPTION;
			
			if( ! leituraEterna ){
				valor = (String) JOptionPane.showInputDialog( null, "Informe o índice final:", "Aranha", JOptionPane.INFORMATION_MESSAGE, null, null, "" + indiceInicial );
				if( valor != null && valor.length() > 0 ){
					new Argumento_indicef().processar( chave, valor );
				}
			}
			
			valor = (String) JOptionPane.showInputDialog( null, "Informe os focos:", "Aranha", JOptionPane.INFORMATION_MESSAGE );
			if( valor != null && valor.length() > 0 ){
				new Argumento_foco().processar( chave, valor );
			}
			
			valor = (String) JOptionPane.showInputDialog( null, "Informe a unidade de tempo, em segundos:", "Aranha", JOptionPane.INFORMATION_MESSAGE, null, null, unidadeTempo );
			if( valor != null && valor.length() > 0 ){
				new Argumento_unidadeTempo().processar( chave, valor );
			}
			
			valor = (String) JOptionPane.showInputDialog( null, "Informe o intervalo de gravação, em segundos:", "Aranha", JOptionPane.INFORMATION_MESSAGE, null, null, intervalo );
			if( valor != null && valor.length() > 0 ){
				new Argumento_intervalo().processar( chave, valor );
			}
			
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
	
	private class Argumento_inicio extends AdaptadoArgumentoProcessador {
		public void processar( Chave chave, String valor ) throws IllegalArgumentException {
			try{
				tempoInicial = new SimpleDateFormat( "dd/MM/yyyy HH:mm:ss" ).parse( valor ).getTime();
			}catch( Exception e ){
				throw new IllegalArgumentException( "Inicio incorreto: " + valor );
			}
		}
	}
	
	private class Argumento_fim extends AdaptadoArgumentoProcessador {
		public void processar( Chave chave, String valor ) throws IllegalArgumentException {
			try{
				tempoFinal = new SimpleDateFormat( "dd/MM/yyyy HH:mm:ss" ).parse( valor ).getTime();
			}catch( Exception e ){
				throw new IllegalArgumentException( "Fim incorreto: " + valor );
			}
		}
	}
	
	private class Argumento_analises extends AdaptadoArgumentoProcessador {
		public void processar( Chave chave, String valor ) throws IllegalArgumentException {
			StringTokenizer st = new StringTokenizer( valor, "," );
			while( st.hasMoreTokens() ){
				String nome = st.nextToken();
				try{
					if( ! nome.contains( "." ) ) nome = Resumo.class.getPackage().getName() + "." + nome;
					Class<?> classe = Class.forName( nome );
					analises.add( (Analise) classe.newInstance() ); 
				}catch( Exception e ){
					throw new IllegalArgumentException( "Analise nao encontrada ou com problema: " + nome );
				}
			}
		}
	}
	
	private class Argumento_foco extends AdaptadoArgumentoProcessador {
		public void processar( Chave chave, String valor ) throws IllegalArgumentException {
			foco = converter( valor );
		}
	}
	
	private class Argumento_unidadeTempo extends AdaptadoArgumentoProcessador {
		public void processar( Chave chave, String valor ) throws IllegalArgumentException {

			try{
				unidadeTempo = getCultura().novaInteiroTransformacao().transformarInteiro( valor );
			}catch( Exception e ){
				throw new IllegalArgumentException( "Unidade de tempo incorreta: " + valor );
			}
		}
	}
	
	private class Argumento_eterna extends AdaptadoArgumentoProcessador {
		public void processar( Chave chave, String valor ) throws IllegalArgumentException {
			leituraEterna = true;
		}
	}
	
	private class Argumento_extra extends AdaptadoArgumentoProcessador {
		public void processar( Chave chave, String valor ) throws IllegalArgumentException {
			
			int igual = valor.indexOf( '=' );
			if( igual < 0 ) throw new IllegalArgumentException( "Declaracao invalida (falta '='): " + valor );
			if( igual == 0 ) throw new IllegalArgumentException( "Chave inexistente: " + valor );
			
			especifico.put( valor.substring( 0, igual ), ++igual < valor.length() ? valor.substring( igual ) : "" );
			
		}
	}
	
	private class Argumento_null extends AdaptadoArgumentoProcessador {
		public void processar( Chave chave, String valor ) throws IllegalArgumentException {
			throw new IllegalArgumentException( "Argumento desconhecido: " + valor );
		}
	}
	
	private class Argumento_ajuda extends AdaptadoArgumentoProcessador {
		public void processar( Chave chave, String valor ) throws IllegalArgumentException {
			throw new IllegalArgumentException();
		}
	}

	private class Argumento_ftpServidor extends AdaptadoArgumentoProcessador {
		public void processar( Chave chave, String valor ) throws IllegalArgumentException {
			ftpServidor = valor;
		}
	}
	
	private class Argumento_ftpUsuario extends AdaptadoArgumentoProcessador {
		public void processar( Chave chave, String valor ) throws IllegalArgumentException {
			ftpUsuario = valor;
		}
	}
	
	private class Argumento_ftpSenha extends AdaptadoArgumentoProcessador {
		public void processar( Chave chave, String valor ) throws IllegalArgumentException {
			ftpSenha = valor;
		}
	}
	
	private class Argumento_ftpDestino extends AdaptadoArgumentoProcessador {
		public void processar( Chave chave, String valor ) throws IllegalArgumentException {
			ftpDestino = valor;
		}
	}
	
	private class Argumento_ftpMascara extends AdaptadoArgumentoProcessador {
		public void processar( Chave chave, String valor ) throws IllegalArgumentException {
			ftpMascara = valor;
		}
	}
	
}
