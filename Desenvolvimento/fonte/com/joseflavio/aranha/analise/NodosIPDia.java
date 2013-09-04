
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
import java.util.HashMap;
import java.util.Map;

import com.joseflavio.aranha.AnaliseException;
import com.joseflavio.aranha.Aranha;
import com.joseflavio.aranha.ArquivavelAnalise;
import com.joseflavio.aranha.Foco;
import com.joseflavio.aranha.IP;
import com.joseflavio.aranha.LibpcapLeitor;
import com.joseflavio.aranha.Ponto;
import com.joseflavio.util.DataSimples;

/**
 * @author Jos� Fl�vio de Souza Dias J�nior
 * @version 2011
 */
public class NodosIPDia extends ArquivavelAnalise {
	
	private Map<IP, Ponto> pontos = new HashMap<IP, Ponto>( 500 );
	
	private Aranha aranha;
	
	private int ultimoDia = -1;
	
	private static final SimpleDateFormat dataFormato = new SimpleDateFormat( "yyyy-MM-dd" );
	
	@Override
	public void iniciar( Aranha aranha ) throws AnaliseException, IOException {
		this.aranha = aranha;
	}
	
	public void analisar( LibpcapLeitor pcap ) throws AnaliseException, IOException {
		
		if( ! pcap.isIP() ) return;
		
		DataSimples data = pcap.getData();
		int dia = data.getDia();
		
		if( ultimoDia != dia ){
			if( arquivoAberto() ) finalizar();
			pontos.clear();
			criarArquivo( aranha.getSaida(), "NodosIP." + dataFormato.format( data.getDate() ) + ".csv" );
		}
		
		ultimoDia = dia;
		
		IP orig_ip = pcap.getIP_Origem();
		IP dest_ip = pcap.getIP_Destino();
		
		int len = pcap.getTamanhoCapturado();

		if( orig_ip != null ){
			Ponto ponto = pontos.get( orig_ip );
			if( ponto == null ){
				ponto = new Ponto( new Foco( orig_ip ) );
				pontos.put( orig_ip, ponto );
			}
			ponto.pacotesEnviados++;
			ponto.bytesEnviados += len;
		}
		
		if( dest_ip != null ){
			Ponto ponto = pontos.get( dest_ip );
			if( ponto == null ){
				ponto = new Ponto( new Foco( dest_ip ) );
				pontos.put( dest_ip, ponto );
			}
			ponto.pacotesRecebidos++;
			ponto.bytesRecebidos += len;
		}
		
	}
	
	@Override
	public void gravar() throws AnaliseException, IOException {

		if( ! arquivoAberto() ) return;
		
		limparArquivo();
		
		escrever( "IP;Pacotes Env;Pacotes Rec;Bytes Env;Bytes Rec;Pacotes Total;Bytes Total\n" );
		
		for( Ponto ponto : pontos.values() ) imprimir( ponto );
		
		descarregarArquivo();
		
	}
	
	@Override
	public void finalizar() throws AnaliseException, IOException {
		gravar();
		fecharArquivo();
	}
	
	private void imprimir( Ponto ponto ) throws IOException {
		
		long pacotesTotal = ponto.pacotesEnviados + ponto.pacotesRecebidos;
		long bytesTotal = ponto.bytesEnviados + ponto.bytesRecebidos;
		
		escrever( ponto.foco.toString() );
		escrever( ";" + ponto.pacotesEnviados );
		escrever( ";" + ponto.pacotesRecebidos );
		escrever( ";" + ponto.bytesEnviados );
		escrever( ";" + ponto.bytesRecebidos );
		escrever( ";" + pacotesTotal );
		escrever( ";" + bytesTotal );
		escrever( '\n' );
		
	}
	
}
