
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
import java.util.ArrayList;
import java.util.List;

import com.joseflavio.aranha.Analise;
import com.joseflavio.aranha.AnaliseException;
import com.joseflavio.aranha.Aranha;
import com.joseflavio.aranha.Foco;
import com.joseflavio.aranha.IP;
import com.joseflavio.aranha.LibpcapLeitor;

/**
 * {@link NodosIPGravacao} para cada {@link Foco} da {@link Aranha}.
 * @author Jos� Fl�vio de Souza Dias J�nior
 * @version 2011
 */
public class NodosIPFocosGravacao implements Analise {
	
	private List<Conjunto> conjuntos;
	
	@Override
	public void iniciar( Aranha aranha ) throws AnaliseException, IOException {
		
		List<Foco> lista = aranha.getFoco();
		conjuntos = new ArrayList<Conjunto>( lista.size() );
		
		for( Foco foco : lista ){
			if( foco.ip == null ) continue;
			NodosIPFocoGravacao analise = new NodosIPFocoGravacao( foco.ip );
			analise.iniciar( aranha );
			conjuntos.add( new Conjunto( foco.ip, analise ) );
		}
		
	}
	
	public void analisar( LibpcapLeitor pcap ) throws AnaliseException, IOException {
		
		if( ! pcap.isIP() ) return;
		
		IP orig_ip = pcap.getIP_Origem();
		IP dest_ip = pcap.getIP_Destino();

		for( Conjunto c : conjuntos ){
			if( c.foco.equivale( orig_ip ) || c.foco.equivale( dest_ip ) ){
				c.analise.analisar( pcap );
			}
		}
		
	}
	
	@Override
	public void gravar() throws AnaliseException, IOException {

		for( Conjunto c : conjuntos ) c.analise.gravar();
		
	}
	
	@Override
	public void finalizar() throws AnaliseException, IOException {
		
		for( Conjunto c : conjuntos ) c.analise.finalizar();
		
	}
	
	private static class Conjunto {
		
		private IP foco;
		
		private NodosIPFocoGravacao analise;

		private Conjunto( IP foco, NodosIPFocoGravacao analise ) {
			this.foco = foco;
			this.analise = analise;
		}
		
	}
	
}
