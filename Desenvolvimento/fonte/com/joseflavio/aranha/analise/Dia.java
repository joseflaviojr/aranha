
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
import java.util.ArrayList;
import java.util.List;

import com.joseflavio.aranha.AnaliseException;
import com.joseflavio.aranha.Aranha;
import com.joseflavio.aranha.ArquivavelAnalise;
import com.joseflavio.aranha.Foco;
import com.joseflavio.aranha.IP;
import com.joseflavio.aranha.LibpcapLeitor;
import com.joseflavio.aranha.MAC;
import com.joseflavio.aranha.Ponto;
import com.joseflavio.aranha.Util;
import com.joseflavio.cultura.NumeroTransformacao;
import com.joseflavio.util.DataSimples;

/**
 * @author José Flávio de Souza Dias Júnior
 * @version 2011
 */
public class Dia extends ArquivavelAnalise {
	
	private Segundo[][][] segundos = new Segundo[24][60][60];
	
	private NumeroTransformacao mbpsFormato;
	
	private Aranha aranha;
	
	private int ultimoDia = -1;
	
	private static final SimpleDateFormat dataFormato = new SimpleDateFormat( "yyyy-MM-dd" );

	@Override
	public void iniciar( Aranha aranha ) throws AnaliseException, IOException {
		
		this.aranha = aranha;
		
		mbpsFormato = Util.novaMbpsTransformacao( aranha.getCultura() );
		
		for( int h = 0; h < 24; h++ ){
			for( int m = 0; m < 60; m++ ){
				for( int s = 0; s < 60; s++ ){
					segundos[ h ][ m ][ s ] = new Segundo();
				}
			}
		}
		
		for( Foco foco : aranha.getFoco() ){
			for( int h = 0; h < 24; h++ ){
				for( int m = 0; m < 60; m++ ){
					for( int s = 0; s < 60; s++ ){
						segundos[ h ][ m ][ s ].pontos.add( new Ponto( foco ) );
					}
				}
			}
		}
		
	}
	
	public void analisar( LibpcapLeitor pcap ) throws AnaliseException, IOException {

		DataSimples data = pcap.getData();
		int dia = data.getDia();
		
		if( ultimoDia != dia ){
			if( arquivoAberto() ) finalizar();
			for( int h = 0; h < 24; h++ ) for( int m = 0; m < 60; m++ ) for( int s = 0; s < 60; s++ ) segundos[ h ][ m ][ s ].limpar();
			criarArquivo( aranha.getSaida(), dataFormato.format( data.getDate() ) + "." + aranha.getUnidadeTempo() + "s.csv" );
		}
		
		ultimoDia = dia;
		
		IP orig_ip = pcap.getIP_Origem();
		IP dest_ip = pcap.getIP_Destino();
		MAC orig_mac = pcap.getEthernet_Origem();
		MAC dest_mac = pcap.getEthernet_Destino();
		
		int len = pcap.getTamanhoCapturado();
		
		Segundo segundo = segundos[ data.getHora() ][ data.getMinuto() ][ data.getSegundo() ];
		
		segundo.pacotes++;
		segundo.bytes += len;
		
		for( Ponto ponto : segundo.pontos ){
			Foco foco = ponto.foco;
			if( foco.isFiltro() ){
				if( foco.equivale( pcap ) ){
					ponto.pacotesEnviados++;
					ponto.bytesEnviados += len;
				}
			}else{
				if( foco.equivale( orig_ip ) || foco.equivale( orig_mac ) ){
					ponto.pacotesEnviados++;
					ponto.bytesEnviados += len;
				}
				if( foco.equivale( dest_ip ) || foco.equivale( dest_mac ) ){
					ponto.pacotesRecebidos++;
					ponto.bytesRecebidos += len;
				}
			}
		}
		
	}
	
	@Override
	public void gravar() throws AnaliseException, IOException {
		
		if( ! arquivoAberto() ) return;
		
		limparArquivo();

		escrever( ";mbps" );
		for( Foco foco : aranha.getFoco() ){
			String focoStr = foco.toString();
			if( foco.filtro != null ){
				escrever( ";" + focoStr + " mbps" );
				escrever( ";" + focoStr + " bytes" );
				escrever( ";" + focoStr + " pacotes" );
			}else{
				escrever( ";" + focoStr + " mbps total" );
				escrever( ";" + focoStr + " mbps env" );
				escrever( ";" + focoStr + " mbps rec" );
				escrever( ";" + focoStr + " bytes env" );
				escrever( ";" + focoStr + " bytes rec" );
			}
		}
		escrever( '\n' );
		
		Segundo segundo = null;
		long tempo = aranha.getUnidadeTempo();
		float mbpsEnv, mbpsRec;
		
		for( int h = 0; h < 24; h++ ){
			for( int m = 0; m < 60; m++ ){
				for( int s = 0; s < 60; s++, tempo-- ){
				
					if( segundo == null ) segundo = segundos[ h ][ m ][ s ].copiar();
					else segundos[ h ][ m ][ s ].adicionarA( segundo );
					
					if( tempo == 1 ){
						
						escrever( ( h < 10 ? "0" + h : h ) + ":" + ( m < 10 ? "0" + m : m ) + ":" + ( s < 10 ? "0" + s : s ) );
						
						mbpsEnv = (float) segundo.bytes * 8 / 1000000 / aranha.getUnidadeTempo();
						
						escrever( ";" + mbpsFormato.transcreverConfiante( mbpsEnv ) );
						
						for( Ponto ponto : segundo.pontos ){
							
							mbpsEnv = (float) ponto.bytesEnviados * 8 / 1000000 / aranha.getUnidadeTempo();
							mbpsRec = (float) ponto.bytesRecebidos * 8 / 1000000 / aranha.getUnidadeTempo();
							
							if( ponto.foco.filtro != null ){
								escrever( ";" + mbpsFormato.transcreverConfiante( mbpsEnv ) );
								escrever( ";" + ponto.bytesEnviados );
								escrever( ";" + ponto.pacotesEnviados );
							}else{
								escrever( ";" + mbpsFormato.transcreverConfiante( mbpsEnv + mbpsRec ) );
								escrever( ";" + mbpsFormato.transcreverConfiante( mbpsEnv ) );
								escrever( ";" + mbpsFormato.transcreverConfiante( mbpsRec ) );
								escrever( ";" + ponto.bytesEnviados );
								escrever( ";" + ponto.bytesRecebidos );
							}
							
						}
						
						escrever( '\n' );
						
						segundo = null;
						tempo = aranha.getUnidadeTempo() + 1;
						
					}
					
				}
			}
		}

		descarregarArquivo();
		
	}
	
	@Override
	public void finalizar() throws AnaliseException, IOException {
		gravar();
		fecharArquivo();
	}
	
	private class Segundo {
		
		public long pacotes = 0;
		
		public long bytes = 0;
		
		private List<Ponto> pontos = new ArrayList<Ponto>();
		
		public void limpar() {
			pacotes = 0;
			bytes = 0;
			for( Ponto ponto : pontos ) ponto.limpar();
		}
		
		public Segundo adicionarA( Segundo destino ) {
			
			destino.pacotes += pacotes;
			destino.bytes += bytes;
			
			int total = pontos.size();
			for( int i = 0; i < total; i++ ){
				Ponto a = destino.pontos.get( i );
				Ponto b = pontos.get( i );
				a.pacotesEnviados  += b.pacotesEnviados;
				a.pacotesRecebidos += b.pacotesRecebidos;
				a.bytesEnviados    += b.bytesEnviados;
				a.bytesRecebidos   += b.bytesRecebidos;
			}
			
			return destino;
			
		}
		
		public Segundo copiar() {
			Segundo s = new Segundo();
			s.pacotes = pacotes;
			s.bytes = bytes;
			s.pontos = new ArrayList<Ponto>( pontos.size() );
			for( Ponto p : pontos ) s.pontos.add( p.copiar() );
			return s;
		}
		
	}
	
}
