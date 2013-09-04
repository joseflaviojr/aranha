
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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;

import com.joseflavio.util.DataSimples;

/**
 * Leitor de pacotes gravados em arquivo de captura no formato Libpcap.
 * @author José Flávio de Souza Dias Júnior
 * @version 2011
 */
public class LibpcapLeitor {
	
	private InputStream stream;
	
	private FileChannel channel;
	
	private PacoteEsperanca pacoteEsperanca;
	
	private long snaplen;
	
	private int etherTipo;
	private byte[] macOrigem = new byte[ 6 ];
	private byte[] macDestino = new byte[ 6 ];
	
	private int vlanPrioridade;
	private int vlanCFI;
	private int vlanId;
	
	private int mplsNiveis;
	private int[] mpls = new int[50];
	
	private int ipTipo;
	private byte[] ipOrigem = new byte[ 4 ];
	private byte[] ipDestino = new byte[ 4 ];
	private int ipToS;
	
	private boolean tcpCompleto;
	private int tcpPortaOrigem;
	private int tcpPortaDestino;
	
	private byte[] dados = new byte[ 1500 ];
	private int dadosTamanho;
	private int dadosTamanhoOriginal;
	
	private long timestamp;
	private DataSimples data = new DataSimples();
	private boolean dataSetada = false;
	
	private byte[] pacote = new byte[ 1600 ];
	private int pacoteBytesLidos;
	
	private int tamanhoCapturado;
	private int tamanhoGravado;
	
	private long pacotesLidos = 0;

	private long bytesLidos = 0;
	private long bytesTotalTemp = 0;
	
	private MAC cacheMACOrigem;
	private MAC cacheMACDestino;
	private IP  cacheIPOrigem;
	private IP  cacheIPDestino;
	private ToS cacheIPToS;
	private VLAN cacheVLAN;
	
	//Variáveis utilizadas apenas durante o processo de leitura do pacote
	private int lTamanhoTotal;
	
	public LibpcapLeitor( File arquivo, PacoteEsperanca pacoteEsperanca ) throws FileNotFoundException, IOException {
		
		FileInputStream fin = new FileInputStream( arquivo );
		this.stream = new BufferedInputStream( fin, 1048576 );
		this.channel = fin.getChannel();
		
		this.pacoteEsperanca = pacoteEsperanca;

		saltar( 16 );
		this.snaplen = ler32desc();
		saltar( 4 );
		
	}

	public LibpcapLeitor( String nome, PacoteEsperanca pacoteEsperanca ) throws FileNotFoundException, IOException {
		
		this( nome != null ? new File( nome ) : null, pacoteEsperanca );
		
	}
	
	/**
	 * Efetua a leitura do próximo pacote.
	 * @see PacoteEsperanca
	 */
	public final boolean lerPacote() throws IOException {

		//----------
		
		vlanPrioridade = -1;
		vlanCFI = -1;
		vlanId = -1;
		
		mplsNiveis = 0;
		
		cacheMACOrigem = null;
		cacheMACDestino = null;
		cacheIPOrigem = null;
		cacheIPDestino = null;
		cacheIPToS = null;
		cacheVLAN = null;
		
		//----------
		
		if( bytesLidos >= bytesTotalTemp ){
			bytesTotalTemp = channel.size();
			if( bytesLidos >= bytesTotalTemp && ! pacoteEsperanca.esperarNovoPacote() ) return false;
		}
		
		//Libpcap
		
		timestamp = ler32desc() * 1000L;
		dataSetada = false;
		saltar( 4 );
		tamanhoGravado = (int) ler32desc();
		tamanhoCapturado = (int) ler32desc();
		
		if( timestamp < 0 || tamanhoGravado < 0 || tamanhoCapturado < 0 || tamanhoGravado > 1526 || tamanhoCapturado > 1526 ){
			while( pacoteEsperanca.esperarNovoPacote() ){
				try{
					Thread.sleep( 1000 );
				}catch( InterruptedException e ){
					throw new IOException( e );
				}
			}
			return false;
		}
		
		//Pacote
		
		lTamanhoTotal = tamanhoGravado;
		pacoteBytesLidos = 0;
		
		lerEthernet();
		
		if( lTamanhoTotal > 0 ) saltar( lTamanhoTotal );
		
		pacotesLidos++;
		return true;
		
	}
	
	private void lerEthernet() throws IOException {
		
		ler( macDestino );
		ler( macOrigem );
		etherTipo = ler16();
		lTamanhoTotal -= 14;
		
		if( etherTipo == 0x8100 ) lerVLAN();
		
		while( etherTipo == 0x8847 ) lerMPLS();
		
		switch( etherTipo ){
			case 0x0800 : lerIP(); break;
		}
		
	}
	
	private void lerVLAN() throws IOException {
		
		int vlanTag = ler16();
		
		vlanPrioridade = ( vlanTag & 0x0000E000 ) >> 13;
		vlanCFI = ( vlanTag & 0x00001000 ) >> 12;
		vlanId =  ( vlanTag & 0x00000FFF );
		
		etherTipo = ler16();
		
		lTamanhoTotal -= 4;
		
	}
	
	private void lerMPLS() throws IOException {

		int tag = (int) ler32();
		mpls[ mplsNiveis++ ] = tag;

		lTamanhoTotal -= 4;
		
		if( ( ( tag & 0x100 ) >> 8 ) == 1 ){
			etherTipo = 0x0800;
		}
		
	}
	
	private void lerIP() throws IOException {
		
		int tamIPCab = ( ler8() & 0xF ) * 4;
		ipToS = ler8();
		int tamIPDados = ler16() - tamIPCab;
		saltar( 5 );
		ipTipo = ler8();
		ler16();
		ler( ipOrigem );
		ler( ipDestino );
		
		if( tamIPCab > 20 ) saltar( tamIPCab - 20 );
		lTamanhoTotal -= tamIPCab;
		
		switch( ipTipo ){
			case 6  : lerTCP( tamIPDados ); break;
			case 17 : break; //UDP
		}
		
	}
	
	private void lerTCP( int tamanhoIPDados ) throws IOException {
		
		int tam, tamTCPCab;
		
		if( tcpCompleto = ( lTamanhoTotal >= 13 ) ){
			
			tcpPortaOrigem = ler16();
			tcpPortaDestino = ler16();
			
			if( tcpCompleto = ( tcpPortaOrigem > 0 && tcpPortaDestino > 0 ) ){
			
				saltar( 8 );
				tamTCPCab = ( ler8() >> 4 ) * 4;
				if( tamTCPCab == 0 ) tamTCPCab = 20;
	
				tam = tamTCPCab;
				if( tam > lTamanhoTotal ) tam = lTamanhoTotal;
				if( tam > 13 ) saltar( tam - 13 );
				lTamanhoTotal -= tam;
				
				// Dados TCP
				
				dadosTamanhoOriginal = tamanhoIPDados - tamTCPCab;
				dadosTamanho = dadosTamanhoOriginal <= lTamanhoTotal ? dadosTamanhoOriginal : lTamanhoTotal;
				ler( dados, dadosTamanho );
				lTamanhoTotal -= dadosTamanho;
			
			}else{
				
				lTamanhoTotal -= 4;
				
			}
		
		}
		
	}
	
	private int ler8() throws IOException {
		
		int b;
		
		while( true ){
		
			b = stream.read();
			if( b != -1 ) break;
			
			try{
				Thread.sleep( 100 );
			}catch( InterruptedException e ){
				throw new IOException( e );
			}
			
		}
		
		pacote[ pacoteBytesLidos++ ] = (byte) b;
		bytesLidos++;
        return b;
        
    }
	
	private int ler16() throws IOException {
		
		int b1 = ler8();
        int b2 = ler8();
        
        return ( b1 << 8 ) | ( b2 );
        
    }

	private long ler32() throws IOException {
		
		int b1 = ler8();
        int b2 = ler8();
        int b3 = ler8();
        int b4 = ler8();
        
        return ( b1 << 24 ) | ( b2 << 16 ) | ( b3 << 8 ) | ( b4 );
        
    }
	
	private long ler32desc() throws IOException {
		
		int b1 = ler8();
        int b2 = ler8();
        int b3 = ler8();
        int b4 = ler8();
        
        return ( b4 << 24 ) | ( b3 << 16 ) | ( b2 << 8 ) | ( b1 );
        
    }
	
	private void ler( byte b[] ) throws IOException {
		int total = b.length;
		for( int i = 0; i < total; i++ ) b[i] = (byte) ler8();
	}
	
	private void ler( byte b[], int total ) throws IOException {
		for( int i = 0; i < total; i++ ) b[i] = (byte) ler8();
	}
	
	private void saltar( long n ) throws IOException {
		for( int i = 0; i < n; i++ ) ler8();
	}
	
	public void fechar() throws IOException {

		channel.close();
		channel = null;
		
		stream.close();
		stream = null;
		
	}
	
	public boolean isIP() {
		return etherTipo == 0x0800;
	}
	
	public boolean isUDP() {
		return ipTipo == 17;
	}
	
	public boolean isTCP() {
		return ipTipo == 6;
	}
	
	public boolean isTCPCompleto() {
		return tcpCompleto;
	}
	
	public long getSnaplen() {
		return snaplen;
	}
	
	public int getVLAN_Prioridade() {
		return vlanPrioridade;
	}
	
	public int getVLAN_CFI() {
		return vlanCFI;
	}
	
	public int getVLAN_Id() {
		return vlanId;
	}
	
	public VLAN getVLAN() {
		if( vlanId == - 1 ) return null;
		return cacheVLAN != null ? cacheVLAN : ( cacheVLAN = new VLAN( vlanId, vlanPrioridade, vlanCFI ) );
	}
	
	public int getMPLS_Niveis() {
		return mplsNiveis;
	}
	
	public int getMPLS_Rotulo( int nivel ) {
		return ( mpls[ nivel ] & 0xFFFFF000 ) >> 12;
	}
	
	public int getMPLS_Bits( int nivel ) {
		return ( mpls[ nivel ] & 0xE00 ) >> 9;
	}
	
	public int getMPLS_TTL( int nivel ) {
		return ( mpls[ nivel ] & 0xFF );
	}
	
	public int getEthernet_Tipo() {
		return etherTipo;
	}

	public MAC getEthernet_Origem() {
		return cacheMACOrigem != null ? cacheMACOrigem : ( cacheMACOrigem = new MAC( macOrigem ) );
	}

	public MAC getEthernet_Destino() {
		return cacheMACDestino != null ? cacheMACDestino : ( cacheMACDestino = new MAC( macDestino ) );
	}
	
	public int getIP_Tipo() {
		return ipTipo;
	}
	
	public byte[] getIP_OrigemBruto() {
		return ipOrigem;
	}
	
	public IP getIP_Origem() {
		return cacheIPOrigem != null ? cacheIPOrigem : ( cacheIPOrigem = new IP( ipOrigem ) );
	}
	
	public byte[] getIP_DestinoBruto() {
		return ipDestino;
	}
	
	public IP getIP_Destino() {
		return cacheIPDestino != null ? cacheIPDestino : ( cacheIPDestino = new IP( ipDestino ) );
	}
	
	public ToS getIP_ToS() {
		return cacheIPToS != null ? cacheIPToS : ( cacheIPToS = new ToS( ipToS ) );
	}
	
	public int getTCP_Origem() {
		return tcpPortaOrigem;
	}
	
	public int getTCP_Destino() {
		return tcpPortaDestino;
	}

	public int getDadosTamanhoOriginal() {
		return dadosTamanhoOriginal;
	}
	
	/**
	 * @see #copiarDadosPara(byte[])
	 */
	public int getDadosTamanho() {
		return dadosTamanho;
	}

	/**
	 * Copia todo o pacote.
	 * @see #getTamanhoGravado()
	 * @return destino
	 */
	public byte[] copiarPacotePara( byte[] destino ) {
		if( destino.length < tamanhoGravado ) throw new IllegalArgumentException();
		System.arraycopy( pacote, 0, destino, 0, tamanhoGravado );
		return destino;
	}
	
	/**
	 * {@link #copiarPacotePara(char[], int)} com {@link #getTamanhoGravado()}.
	 */
	public char[] copiarPacotePara( char[] destino ) {
		return copiarPacotePara( destino, tamanhoGravado );
	}
	
	/**
	 * Copia todo o pacote, convertendo cada byte diretamente para char.
	 * @param maximo Máximo de caracteres a copiar.
	 * @see #copiarPacotePara(byte[])
	 * @return destino
	 */
	public char[] copiarPacotePara( char[] destino, int maximo ) {
		if( maximo > tamanhoGravado ) maximo = tamanhoGravado;
		if( destino.length < maximo ) throw new IllegalArgumentException();
		for( int i = 0; i < maximo; i++ ) destino[i] = (char) pacote[i];
		return destino;
	}
	
	/**
	 * Copia os dados do pacote de transporte.
	 * @see #getDadosTamanho()
	 * @return destino
	 */
	public byte[] copiarDadosPara( byte[] destino ) {
		if( destino.length < dadosTamanho ) throw new IllegalArgumentException();
		System.arraycopy( dados, 0, destino, 0, dadosTamanho );
		return destino;
	}
	
	/**
	 * {@link #copiarDadosPara(char[], int)} com {@link #getDadosTamanho()}.
	 */
	public char[] copiarDadosPara( char[] destino ) {
		return copiarDadosPara( destino, dadosTamanho );
	}
	
	/**
	 * Copia os dados do pacote de transporte, convertendo cada byte diretamente para char.
	 * @param maximo Máximo de caracteres a copiar.
	 * @see #copiarDadosPara(byte[])
	 * @return destino
	 */
	public char[] copiarDadosPara( char[] destino, int maximo ) {
		if( maximo > dadosTamanho ) maximo = dadosTamanho;
		if( destino.length < maximo ) throw new IllegalArgumentException();
		for( int i = 0; i < maximo; i++ ) destino[i] = (char) dados[i];
		return destino;
	}
	
	/**
	 * O mesmo que {@link #copiarDadosPara(char[])}, porém adicionando o resultado em {@link StringBuilder}.
	 * @see StringBuilder#append(char)
	 * @see #copiarDadosPara(char[])
	 * @return destino
	 */
	public StringBuilder copiarDadosPara( StringBuilder destino ) {
		for( int i = 0; i < dadosTamanho; i++ ) destino.append( (char) dados[i] );
		return destino;
	}
	
	public long getTimestamp() {
		return timestamp;
	}
	
	public long getTimestampSegundos() {
		return timestamp / 1000L;
	}
	
	public DataSimples getData() {
		if( ! dataSetada ){
			data.setTimestamp( timestamp );
			dataSetada = true;
		}
		return data;
	}
	
	public int getTamanhoCapturado() {
		return tamanhoCapturado;
	}

	public int getTamanhoGravado() {
		return tamanhoGravado;
	}
	
	public long getPacotesLidos() {
		return pacotesLidos;
	}

}
