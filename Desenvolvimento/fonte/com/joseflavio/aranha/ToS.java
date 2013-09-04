
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

/**
 * Campo "Type of Service" do protocolo IP.
 * @author José Flávio de Souza Dias Júnior
 * @version 2011
 */
public class ToS {

	private char[] bits = new char[ 8 ];
	
	private int hash = 0;
	
	/**
	 * @param bits 8 bits, podendo incluir o curinga "?". Ex.: 10001??0
	 */
	public ToS( String bits ) {
		if( bits == null || bits.length() != 8 ) throw new IllegalArgumentException( "ToS deve conter 8 bits." );
		for( int i = 0; i < 8; i++ ) this.bits[i] = bits.charAt( i );
	}
	
	/**
	 * @param bits 8 bits menos significativos. Ex.: 136 == 10001000
	 */
	public ToS( int bits ) {
		for( int b = 128, i = 0; b >= 1; b /= 2, i++ ){
			this.bits[i] = ( ( bits & 0x000000FF ) & b ) > 0 ? '1' : '0';
		}
	}
	
	public boolean equivale( ToS obj ) {
		ToS o = (ToS) obj;
		for( int i = 0; i < 8; i++ ){
			if( bits[i] != '?' && o.bits[i] != '?' && bits[i] != o.bits[i] ) return false;
		}
		return true;
	}
	
	public boolean equals( Object obj ) {
		ToS o = (ToS) obj;
		return Util.igual( bits, o.bits );
	}
	
	public int hashCode() {
		if( hash == 0 ){
			for( int i = 0; i < 8; i++ ){
				hash = 31 * hash + bits[i];	
			}
		}
		return hash;
	}
	
	public String toString() {
		return "tos" + new String( bits );
	}
	
}