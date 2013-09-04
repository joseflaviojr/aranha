
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

package com.joseflavio.aranha.analise;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

import com.joseflavio.aranha.AnaliseException;
import com.joseflavio.aranha.Aranha;
import com.joseflavio.aranha.ArquivavelAnalise;
import com.joseflavio.aranha.LibpcapLeitor;
import com.joseflavio.aranha.Nomes;
import com.joseflavio.aranha.Util;

/**
 * @author José Flávio de Souza Dias Júnior
 * @version 2011
 */
public class Resumo extends ArquivavelAnalise {
	
	private long pacotes = 0;
	
	private long bytes = 0;
	
	private ProtocoloEthernet[] ether = new ProtocoloEthernet[ 0x10000 ];
	
	private ProtocoloIP[] ip = new ProtocoloIP[ 0x100 ];
	
	private long tempoInicial = 0;
	
	private long tempoFinal = 0;
	
	public Resumo() {
		
		for( int i = 0; i < ether.length; i++ ) ether[ i ] = new ProtocoloEthernet( i );
		for( int i = 0; i < ip.length; i++ ) ip[ i ] = new ProtocoloIP( i );
		
	}
	
	@Override
	public void iniciar( Aranha aranha ) throws AnaliseException, IOException {
		
		criarArquivo( aranha.getSaida(), "Resumo.txt" );
		
		pacotes = 0;
		bytes = 0;
		
		for( int i = 0; i < ether.length; i++ ) ether[ i ].limpar();
		for( int i = 0; i < ip.length; i++ ) ip[ i ].limpar();
		
		tempoInicial = 0;
		tempoFinal = 0;
		
	}
	
	public void analisar( LibpcapLeitor pcap ) throws AnaliseException, IOException {
		
		int len = pcap.getTamanhoCapturado();
		
		pacotes++;
		bytes += len;
		
		int cod = pcap.getEthernet_Tipo() & 0xFFFF;
		if( cod >= 0x0000 && cod <= 0x05DC ){
			ether[ 0x0000 ].pacotes++;
			ether[ 0x0000 ].bytes += len;
		}else if( cod >= 0x0101 && cod <= 0x01FF ){
			ether[ 0x0101 ].pacotes++;
			ether[ 0x0101 ].bytes += len;
		}else if( cod >= 0x8137 && cod <= 0x8138 ){
			ether[ 0x8137 ].pacotes++;
			ether[ 0x8137 ].bytes += len;
		}else{
			ether[ cod ].pacotes++;
			ether[ cod ].bytes += len;
		}
		
		if( pcap.isIP() ){
			cod = pcap.getIP_Tipo() & 0xFF;
			ip[ cod ].pacotes++;
			ip[ cod ].bytes += len;
		}

		if( tempoInicial == 0 ) tempoInicial = pcap.getTimestampSegundos();
		tempoFinal = pcap.getTimestampSegundos();
		
	}
	
	@Override
	public void gravar() throws AnaliseException, IOException {

		limparArquivo();
		
		escrever( "\nResumo Total\n\n" );
		
		escrever( "Pacotes = " );
		escrever( pacotes + "\n" );
		
		escrever( "Bytes = " );
		escrever( bytes + " (" + ( (float) bytes / 1048576 ) + " MB)\n" );
		
		long segundos = tempoFinal - tempoInicial;
		
		escrever( "Duracao = " + segundos + " segundos = " + ( (float) segundos / 60 ) + " minutos = " + ( (float) segundos / 3600 ) + " horas\n" );
		
		escrever( "Bytes/segundo = " + ( (float) bytes / segundos ) + "\n" );
		
		escrever( "Mbps = " + ( (float) bytes * 8 / 1000000 / segundos ) + "\n" );
		
		escrever( "\n------------------------------------------------------------------------\n" );
		escrever( "\nProtocolos Ethernet - Ordem: Bytes\n\n" );
		
		Arrays.sort(
				ether,
				new Comparator<Protocolo>() {
					public int compare( Protocolo o1, Protocolo o2 ) {
						if( o1.bytes == o2.bytes ) return 0;
						return o1.bytes > o2.bytes ? -1 : 1;
					}
				}
		);
		
		for( Protocolo prot : ether ){
			if( prot.pacotes > 0 || prot.bytes > 0 ) imprimir( prot );
		}
		
		escrever( "\n------------------------------------------------------------------------\n" );
		escrever( "\nProtocolos IP - Ordem: Bytes\n\n" );
		
		Arrays.sort(
				ip,
				new Comparator<Protocolo>() {
					public int compare( Protocolo o1, Protocolo o2 ) {
						if( o1.bytes == o2.bytes ) return 0;
						return o1.bytes > o2.bytes ? -1 : 1;
					}
				}
		);
		
		for( Protocolo prot : ip ){
			if( prot.pacotes > 0 || prot.bytes > 0 ) imprimir( prot );
		}
		
		escrever( "\n------------------------------------------------------------------------\n" );
		escrever( "\nProtocolos Ethernet - Ordem: Pacotes\n\n" );
		
		Arrays.sort(
				ether,
				new Comparator<Protocolo>() {
					public int compare( Protocolo o1, Protocolo o2 ) {
						if( o1.pacotes == o2.pacotes ) return 0;
						return o1.pacotes > o2.pacotes ? -1 : 1;
					}
				}
		);
		
		for( Protocolo prot : ether ){
			if( prot.pacotes > 0 || prot.bytes > 0 ) imprimir( prot );
		}
		
		escrever( "\n------------------------------------------------------------------------\n" );
		escrever( "\nProtocolos IP - Ordem: Pacotes\n\n" );
		
		Arrays.sort(
				ip,
				new Comparator<Protocolo>() {
					public int compare( Protocolo o1, Protocolo o2 ) {
						if( o1.pacotes == o2.pacotes ) return 0;
						return o1.pacotes > o2.pacotes ? -1 : 1;
					}
				}
		);
		
		for( Protocolo prot : ip ){
			if( prot.pacotes > 0 || prot.bytes > 0 ) imprimir( prot );
		}
		
		descarregarArquivo();
		
	}
	
	@Override
	public void finalizar() throws AnaliseException, IOException {
		gravar();
		fecharArquivo();
	}
	
	private void imprimir( Protocolo prot ) throws IOException {
		escrever( "%1$-20s", obterFormato0xFFFF( prot.codigo ) + " " + prot.getNomeSimples() );
		escrever( "%1$-50s", " = " + prot.pacotes + " pacotes (" + ( (float) prot.pacotes / pacotes * 100 ) + " %)" );
		escrever( " = " + prot.bytes + " bytes (" + ( (float) prot.bytes / bytes * 100 ) + " %)\n" );
	}
	
	public static String obterFormato0xFFFF( int valor ) {
		return "0x" + Util.obterHexFFFF( valor );
	}
	
	public static abstract class Protocolo {
		
		protected int codigo;
		
		protected long pacotes = 0;
		
		protected long bytes = 0;
		
		protected Protocolo( int codigo ) {
			this.codigo = codigo;
		}
		
		public void limpar() {
			pacotes = 0;
			bytes = 0;
		}
		
		public abstract String getNomeSimples();
		
	}
	
	public static class ProtocoloEthernet extends Protocolo {
		
		public ProtocoloEthernet( int codigo ) {
			super( codigo );
		}
		
		public String getNomeSimples() {
			return Nomes.getEthernetSimples( codigo );
		}
		
	}
	
	public static class ProtocoloIP extends Protocolo {
		
		public ProtocoloIP( int codigo ) {
			super( codigo );
		}
		
		public String getNomeSimples() {
			return Nomes.getIPSimples( codigo );
		}
		
	}
	
}
