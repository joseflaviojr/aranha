
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

package com.joseflavio.aranha;

import java.util.Arrays;
import java.util.StringTokenizer;

/**
 * {@link IP}'s que são tratados como um. 
 * @author José Flávio de Souza Dias Júnior
 * @version 2011
 */
public class IPConjunto extends IP {
	
	private IP[] ip;
	
	private int hash = 0;

	/**
	 * @param conjunto Formato IP+IP+...+IP
	 */
	public IPConjunto( String conjunto ) throws IllegalArgumentException {
		
		super( "0.0.0.0" );
		
		StringTokenizer st = new StringTokenizer( conjunto, "+" );
		
		ip = new IP[ st.countTokens() ];
		
		for( int i = 0; st.hasMoreTokens(); i++ ) {
			ip[i] = new IP( st.nextToken() );
		}
		
		Arrays.sort( ip );
		
	}

	@Override
	public boolean equivale( IP obj ) {
		
		if( obj.getClass() == IP.class ){
			IP alvo = (IP) obj;
			for( IP i : ip ){
				if( i.equivale( alvo ) ) return true;
			}
			return false;
		}
		
		IPConjunto o = (IPConjunto) obj;
		boolean sucesso;
		
		for( int i = 0; i < ip.length; i++ ) {
			sucesso = false;
			for( int j = 0; j < o.ip.length; j++ ) {
				if( ip[i].equivale( o.ip[j] ) ){
					sucesso = true;
					break;
				}
			}
			if( ! sucesso ) return false;
		}
		
		return true;
		
	}
	
	public boolean equals( Object obj ) {
		
		if( obj.getClass() != IPConjunto.class ) return false;
		
		IPConjunto o = (IPConjunto) obj;

		if( ip.length != o.ip.length ) return false;
		
		for( int i = 0; i < ip.length; i++ ) {
			if( ! ip[i].equals( o.ip[i] ) ) return false;
		}
		
		return true;
		
	}
	
	@Override
	public int compareTo( IP obj ) {
		
		int ip2_len = obj instanceof IPConjunto ? ((IPConjunto)obj).ip.length : 1;
		int iguais = 0;
		int comp;
		
		for( int i = 0; i < ip.length; i++ ) {
			if( i >= ip2_len ) break;
			comp = ip[i].compareTo( obj instanceof IPConjunto ? ((IPConjunto)obj).ip[i] : obj );
			if( comp < 0 ) return -1;
			if( comp == 0 ) iguais++;
		}
			
		return ip.length == ip2_len && iguais == ip.length ? 0 : 1;
		
	}
	
	public int hashCode() {
		if( hash == 0 ){
			for( IP i : ip ) hash += i.hashCode();
		}
		return hash;
	}
	
	public String toString() {
		StringBuilder s = new StringBuilder( ip.length * 15 + ip.length );
		boolean primeiro = true;
		for( IP i : ip ){
			if( primeiro ){
				s.append( i.toString() );
				primeiro = false;
			}else{
				s.append( "+" + i.toString() );
			}
		}
		return s.toString();
	}
	
}