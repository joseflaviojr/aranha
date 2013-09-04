
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

package com.joseflavio.aranha.filtro;

import com.joseflavio.aranha.LibpcapLeitor;

/**
 * Álgebra booleana entre {@link Filtro}s.
 * @author José Flávio de Souza Dias Júnior
 * @version 2011
 */
public final class LogicaFiltro implements Filtro {

	/**
	 * Conjunção.
	 */
	public static final int E = 0;
	
	/**
	 * Disjunção.
	 */
	public static final int OU = 1;
	
	/**
	 * Disjunção exclusiva.
	 */
	public static final int OUEX = 2;
	
	private Filtro esquerda;
	
	private int operador = E;
	
	private Filtro direita;
	
	private static final String[] OPERADOR = { "e", "ou", "ouex" };

	/**
	 * @param esquerda {@link Filtro} à esquerda do operador lógico.
	 * @param operador Operador lógico.
	 * @param direita {@link Filtro} à direita do operador lógico.
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
	 * @param esquerda {@link Filtro} à esquerda do operador lógico.
	 * @param operador Operador lógico.
	 * @param direita {@link Filtro} à direita do operador lógico.
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
