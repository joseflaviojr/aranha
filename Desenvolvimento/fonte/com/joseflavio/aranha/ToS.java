
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

package com.joseflavio.aranha;

/**
 * Campo "Type of Service" do protocolo IP.
 * @author Jos� Fl�vio de Souza Dias J�nior
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