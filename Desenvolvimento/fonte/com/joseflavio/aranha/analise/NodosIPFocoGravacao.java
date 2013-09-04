
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
import java.util.HashMap;
import java.util.Map;

import com.joseflavio.aranha.AnaliseException;
import com.joseflavio.aranha.Aranha;
import com.joseflavio.aranha.ArquivavelAnalise;
import com.joseflavio.aranha.Foco;
import com.joseflavio.aranha.IP;
import com.joseflavio.aranha.LibpcapLeitor;
import com.joseflavio.aranha.Ponto;

/**
 * @author José Flávio de Souza Dias Júnior
 * @version 2011
 */
class NodosIPFocoGravacao extends ArquivavelAnalise {
	
	IP foco;
	
	private Map<IP, Ponto> pontos = new HashMap<IP, Ponto>( 500 );
	
	public NodosIPFocoGravacao( IP foco ) {
		this.foco = foco;
	}

	@Override
	public void iniciar( Aranha aranha ) throws AnaliseException, IOException {
		String nome = foco != null ? "[" + foco.toString().replace( '*', '#' ) + "]" : "";
		criarArquivo( aranha.getSaida(), "NodosIP" + nome + ".Ultimos" + aranha.getIntervalo() + "s.csv" );
	}
	
	public void analisar( LibpcapLeitor pcap ) throws AnaliseException, IOException {
		
		if( ! pcap.isIP() ) return;
		
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

		limparArquivo();
		
		escrever( "IP;Pacotes Env;Pacotes Rec;Bytes Env;Bytes Rec;Pacotes Total;Bytes Total\n" );
		
		for( Ponto ponto : pontos.values() ){
			imprimir( ponto );
			ponto.limpar();
		}
		
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
