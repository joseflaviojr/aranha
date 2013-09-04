
/*
 *  Copyright (C) 2010-2011 Jos� Fl�vio de Souza Dias J�nior
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
 *  Direitos Autorais Reservados (C) 2010-2011 Jos� Fl�vio de Souza Dias J�nior
 * 
 *  Este arquivo � parte de Aranha - <http://www.joseflavio.com/aranha/>.
 * 
 *  Aranha � software livre: voc� pode redistribu�-lo e/ou modific�-lo
 *  sob os termos da Licen�a P�blica Geral GNU conforme publicada pela
 *  Free Software Foundation, tanto a vers�o 3 da Licen�a, como
 *  (a seu crit�rio) qualquer vers�o posterior.
 * 
 *  Aranha � distribu�do na expectativa de que seja �til,
 *  por�m, SEM NENHUMA GARANTIA; nem mesmo a garantia impl�cita de
 *  COMERCIABILIDADE ou ADEQUA��O A UMA FINALIDADE ESPEC�FICA. Consulte a
 *  Licen�a P�blica Geral do GNU para mais detalhes.
 * 
 *  Voc� deve ter recebido uma c�pia da Licen�a P�blica Geral do GNU
 *  junto com Aranha. Se n�o, veja <http://www.gnu.org/licenses/>.
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
 * @author Jos� Fl�vio de Souza Dias J�nior
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
