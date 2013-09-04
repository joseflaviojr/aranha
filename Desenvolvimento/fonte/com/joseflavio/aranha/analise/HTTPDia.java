
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

import com.joseflavio.aranha.AnaliseException;
import com.joseflavio.aranha.Aranha;
import com.joseflavio.aranha.ArquivavelAnalise;
import com.joseflavio.aranha.LibpcapLeitor;
import com.joseflavio.util.DataSimples;

/**
 * @author José Flávio de Souza Dias Júnior
 * @version 2011
 */
public class HTTPDia extends ArquivavelAnalise {
	
	private Aranha aranha;
	
	private int ultimoDia = -1;
	
	private char[] tcp = new char[ 1420 ];
	
	private static final SimpleDateFormat dataFormato = new SimpleDateFormat( "yyyy-MM-dd" );
	
	@Override
	public void iniciar( Aranha aranha ) throws AnaliseException, IOException {
		this.aranha = aranha;
	}
	
	@Override
	public void analisar( LibpcapLeitor pcap ) throws AnaliseException, IOException {
		
		if( ! pcap.isTCP() || ! pcap.isTCPCompleto() ) return;
		
		DataSimples data = pcap.getData();
		int dia = data.getDia();
		
		if( ultimoDia != dia ){
			if( arquivoAberto() ) finalizar();
			criarArquivo( aranha.getSaida(), "HTTP." + dataFormato.format( data.getDate() ) + ".csv" );
			escrever( "Horario;Fato;Origem;Destino;Valor\n" );
		}
		
		ultimoDia = dia;
		
		int tam = pcap.getDadosTamanho();
		int h = data.getHora();
		int m = data.getMinuto();
		int s = data.getSegundo();
		char ch;
		
		if( tam >= 4 ){
		
			pcap.copiarDadosPara( tcp );
			
			boolean get = tcp[0] == 'G' && tcp[1] == 'E' && tcp[2] == 'T';
			boolean post = get ? false : tcp[0] == 'P' && tcp[1] == 'O' && tcp[2] == 'S' && tcp[3] == 'T';
			
			if( get || post ){
				
				escrever( ( h < 10 ? "0" + h : h ) + ":" + ( m < 10 ? "0" + m : m ) + ":" + ( s < 10 ? "0" + s : s ) + ";" );
				escrever( get ? "GET;" : "POST;" );
				escrever( pcap.getIP_Origem().toString() + ":" + pcap.getTCP_Origem() + ";" );
				escrever( pcap.getIP_Destino().toString() + ":" + pcap.getTCP_Destino() + ";" );
				
				for( int i = get ? 4 : 5; i < tam; i++ ){
					ch = tcp[i];
					if( ch == ' ' ) break;
					escrever( ch );
				}
				
				escrever( '\n' );
				
			}

		}
		
	}
	
	@Override
	public void gravar() throws AnaliseException, IOException {
		descarregarArquivo();
	}
	
	@Override
	public void finalizar() throws AnaliseException, IOException {
		gravar();
		fecharArquivo();
	}
	
}
