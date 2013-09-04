
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
 * @author José Flávio de Souza Dias Júnior
 * @version 2011
 */
public class IP implements Comparable<IP> {
	
	private byte[] bruto = new byte[ 4 ];
	
	private boolean[] curinga = new boolean[ 4 ];
	
	private int hash = 0;

	public IP( byte[] bruto, boolean[] curinga ) {
		for( int i = 0; i < 4; i++ ){
			this.bruto[i] = bruto[i];
			this.curinga[i] = curinga[i];
		}
	}
	
	public IP( byte[] bruto ) {
		for( int i = 0; i < 4; i++ ){
			this.bruto[i] = bruto[i];
			this.curinga[i] = false;
		}
	}
	
	public IP( String ip ) throws IllegalArgumentException {
		
		try{
			
			int p1 = ip.indexOf( '.' );
			int p2 = ip.indexOf( '.', p1 + 1 );
			int p3 = ip.indexOf( '.', p2 + 1 );
			
			String s0 = ip.substring( 0, p1 );
			String s1 = ip.substring( p1 + 1, p2 );
			String s2 = ip.substring( p2 + 1, p3 );
			String s3 = ip.substring( p3 + 1 );
			
			bruto[0] = ( curinga[0] = s0.equals( "*" ) ) ? 0 : (byte) ( Integer.parseInt( s0 ) & 0x000000FF );
			bruto[1] = ( curinga[1] = s1.equals( "*" ) ) ? 0 : (byte) ( Integer.parseInt( s1 ) & 0x000000FF );
			bruto[2] = ( curinga[2] = s2.equals( "*" ) ) ? 0 : (byte) ( Integer.parseInt( s2 ) & 0x000000FF );
			bruto[3] = ( curinga[3] = s3.equals( "*" ) ) ? 0 : (byte) ( Integer.parseInt( s3 ) & 0x000000FF );
		
		}catch( Exception e ) {
			throw new IllegalArgumentException( ip, e );
		}
		
	}
	
	/**
	 * Equivalente. * == [0,1,2,...,255]
	 */
	public boolean equivale( IP obj ) {
		
		if( obj instanceof IPConjunto ){
			return ((IPConjunto)obj).equivale( this );
		}
		
		IP ip = (IP) obj;
		
		for( int i = 0; i < 4; i++ ){
			if( ! curinga[i] && ! ip.curinga[i] && bruto[i] != ip.bruto[i] ) return false;
		}
		
		return true;
		
	}
	
	public boolean equals( Object obj ) {
		
		if( obj.getClass() != IP.class ) return false;
		
		IP ip = (IP) obj;
		
		for( int i = 0; i < 4; i++ ){
			if( curinga[i] != ip.curinga[i] ) return false;
			if( bruto[i] != ip.bruto[i] ) return false;
		}
		
		return true;
		
	}
	
	/**
	 * Ordena de tal forma que * < 0 && * != [0,1,2,...,255]
	 */
	@Override
	public int compareTo( IP ip ) {
		
		if( ip instanceof IPConjunto ){
			int comp = ((IPConjunto)ip).compareTo( this );
			return comp == 0 ? 0 : - comp;
		}
		
		for( int i = 0, a, b; i < 4; i++ ){
			a = curinga[i] ? -1 : 0x000000FF & bruto[i];
			b = ip.curinga[i] ? -1 : 0x000000FF & ip.bruto[i];
			if( a < b ) return -1; else if( a > b ) return 1;
		}
		
		return 0;
		
	}
	
	public int hashCode() {
		if( hash == 0 ){
			for( int i = 0; i < 4; i++ ){
				hash = 31 * hash + ( curinga[i] ? -1 : 0x000000FF & bruto[i] );	
			}
		}
		return hash;
	}
	
	public String toString() {
		
		StringBuilder s = new StringBuilder( 15 );
		
		s.append( ( curinga[0] ? "*" : 0x000000FF & bruto[0] ) + "." );
		s.append( ( curinga[1] ? "*" : 0x000000FF & bruto[1] ) + "." );
		s.append( ( curinga[2] ? "*" : 0x000000FF & bruto[2] ) + "." );
		s.append( ( curinga[3] ? "*" : 0x000000FF & bruto[3] ) );
		
		return s.toString();
		
	}
	
	/**
	 * Instancia devidamente uma das seguintes classes:<br>
	 * {@link IP} 10.0.*.*<br>
	 * {@link IPConjunto} 10.0.*.*+10.1.*.*<br>
	 * {@link IPNegado} !10.0.*.*+10.1.*.*
	 * @param ip Formato: [!]IP[+IP+IP+...]
	 */
	public static IP instanciar( String ip ) {
		
		if( ip.indexOf( ' ' ) > -1 ) ip = ip.replaceAll( " ", "" );
		
		boolean negado = false;
		if( ip.charAt( 0 ) == '!' ){
			negado = true;
			ip = ip.substring( 1 );
		}
		
		IP inst = ip.indexOf( '+' ) > -1 ? new IPConjunto( ip ) : new IP( ip );
		
		return negado ? new IPNegado( inst ) : inst;
		
	}
	
}