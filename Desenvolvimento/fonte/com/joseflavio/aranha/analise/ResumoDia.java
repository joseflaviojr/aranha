
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
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

import com.joseflavio.aranha.AnaliseException;
import com.joseflavio.aranha.Aranha;
import com.joseflavio.aranha.ArquivavelAnalise;
import com.joseflavio.aranha.LibpcapLeitor;
import com.joseflavio.aranha.analise.Resumo.Protocolo;
import com.joseflavio.aranha.analise.Resumo.ProtocoloEthernet;
import com.joseflavio.aranha.analise.Resumo.ProtocoloIP;
import com.joseflavio.util.DataSimples;

/**
 * @author José Flávio de Souza Dias Júnior
 * @version 2011
 */
public class ResumoDia extends ArquivavelAnalise {
	
	private long pacotes = 0;
	
	private long bytes = 0;
	
	private ProtocoloEthernet[] ether = new ProtocoloEthernet[ 0x10000 ];
	
	private ProtocoloIP[] ip = new ProtocoloIP[ 0x100 ];
	
	private long tempoInicial = 0;
	
	private long tempoFinal = 0;
	
	private Aranha aranha;
	
	private int ultimoDia = -1;
	
	private Date dataAtual;
	
	private static final SimpleDateFormat dataFormato = new SimpleDateFormat( "yyyy-MM-dd" );
	
	public ResumoDia() {
		
		for( int i = 0; i < ether.length; i++ ) ether[ i ] = new ProtocoloEthernet( i );
		for( int i = 0; i < ip.length; i++ ) ip[ i ] = new ProtocoloIP( i );
		
	}
	
	@Override
	public void iniciar( Aranha aranha ) throws AnaliseException, IOException {
		this.aranha = aranha;
	}
	
	public void analisar( LibpcapLeitor pcap ) throws AnaliseException, IOException {
		
		DataSimples data = pcap.getData();
		int dia = data.getDia();
		
		if( ultimoDia != dia ){
			if( arquivoAberto() ) finalizar();
			limpar();
			criarArquivo( aranha.getSaida(), "Resumo." + dataFormato.format( dataAtual = data.getDate() ) + ".txt" );
		}
		
		ultimoDia = dia;
		
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

		if( ! arquivoAberto() ) return;
		
		limparArquivo();
		
		escrever( "\nResumo do Dia " + dataFormato.format( dataAtual ) + "\n\n" );
		
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
	
	private void limpar() {
		
		pacotes = 0;
		bytes = 0;
		
		for( int i = 0; i < ether.length; i++ ) ether[ i ].limpar();
		for( int i = 0; i < ip.length; i++ ) ip[ i ].limpar();
		
		tempoInicial = 0;
		tempoFinal = 0;
		
	}
	
	private void imprimir( Protocolo prot ) throws IOException {
		escrever( "%1$-20s", Resumo.obterFormato0xFFFF( prot.codigo ) + " " + prot.getNomeSimples() );
		escrever( "%1$-50s", " = " + prot.pacotes + " pacotes (" + ( (float) prot.pacotes / pacotes * 100 ) + " %)" );
		escrever( " = " + prot.bytes + " bytes (" + ( (float) prot.bytes / bytes * 100 ) + " %)\n" );
	}
	
}
