
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

package com.joseflavio.aranha.filtro;

import com.joseflavio.aranha.LibpcapLeitor;

/**
 * �lgebra booleana entre {@link Filtro}s.
 * @author Jos� Fl�vio de Souza Dias J�nior
 * @version 2011
 */
public final class LogicaFiltro implements Filtro {

	/**
	 * Conjun��o.
	 */
	public static final int E = 0;
	
	/**
	 * Disjun��o.
	 */
	public static final int OU = 1;
	
	/**
	 * Disjun��o exclusiva.
	 */
	public static final int OUEX = 2;
	
	private Filtro esquerda;
	
	private int operador = E;
	
	private Filtro direita;
	
	private static final String[] OPERADOR = { "e", "ou", "ouex" };

	/**
	 * @param esquerda {@link Filtro} � esquerda do operador l�gico.
	 * @param operador Operador l�gico.
	 * @param direita {@link Filtro} � direita do operador l�gico.
	 */
	public LogicaFiltro( Filtro esquerda, int operador, Filtro direita ) {
		this.esquerda = esquerda;
		this.operador = operador;
		this.direita = direita;
	}
	
	/**
	 * <pre>
	 *    "e"  {@link #E}
	 *   "ou"  {@link #OU}
	 * "ouex"  {@link #OUEX}
	 * </pre>
	 * @param esquerda {@link Filtro} � esquerda do operador l�gico.
	 * @param operador Operador l�gico.
	 * @param direita {@link Filtro} � direita do operador l�gico.
	 */
	public LogicaFiltro( Filtro esquerda, String operador, Filtro direita ) {
		
		this.esquerda = esquerda;
		this.direita = direita;
		
		operador = operador.toLowerCase();
		for( int i = 0; i < OPERADOR.length; i++ ){
			if( operador.equals( OPERADOR[i] ) ){
				this.operador = i;
				break;
			}
		}
		
	}
	
	@Override
	public boolean filtrar( LibpcapLeitor pcap ) {
		
		switch( operador ){
			case E    :  return esquerda.filtrar( pcap ) && direita.filtrar( pcap );
			case OU   :  return esquerda.filtrar( pcap ) || direita.filtrar( pcap );	
			case OUEX :  return esquerda.filtrar( pcap ) ^  direita.filtrar( pcap );
		}
		
		return false;
		
	}
	
	@Override
	public boolean equals( Object obj ) {
		if( obj == null || !( obj instanceof LogicaFiltro ) ) return false;
		LogicaFiltro filtro = (LogicaFiltro) obj;
		return operador == filtro.operador && esquerda.equals( filtro.esquerda ) && direita.equals( filtro.direita );
	}
	
	@Override
	public int hashCode() {
		return esquerda.hashCode() + operador + direita.hashCode();
	}
	
}
