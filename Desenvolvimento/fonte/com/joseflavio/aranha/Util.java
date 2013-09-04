
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

import com.joseflavio.cultura.Cultura;
import com.joseflavio.cultura.NumeroTransformacao;


/**
 * @author José Flávio de Souza Dias Júnior
 * @version 2011
 */
public class Util {
	
	private static final char[] HEX_DIGITOS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
	
	public static boolean isDigitoHexadecimal( char c ) {
		for( int i = 0; i < 16; i++ ){
			if( c == HEX_DIGITOS[i] ) return true;
		}
		return false;
	}
	
	/**
	 * Converte o MAC de uma interface de rede em um array de bytes.
	 * @param mac 00:00:00:00:00:00 ou 000000000000, sendo que ':' pode ser qualquer outro caractere fora das faixas 0-9, A-F e a-f.
	 * @return um array com 6 bytes. <code>null</code> == MAC inválido.
	 */
	public static byte[] converterMacEmBytes( String mac ) {
		
		char[] s = mac.toUpperCase().toCharArray();
		if( s.length != 12 && s.length != 17 ) return null;
		
		byte[] array = new byte[6];
		int b = 0;
		
		boolean pronto = false;
		char ult = '0';
		
		for( int i = 0; i < s.length; i++ ){
			if( ! isDigitoHexadecimal( s[i] ) ) continue;
			if( pronto ){
				array[b++] = (byte) ( Integer.parseInt( "" + ult, 16 ) * 16 + Integer.parseInt( "" + s[i], 16 ) );
				pronto = false;
			}else{
				ult = s[i];
				pronto = true;
			}
		}
		
		if( b != 6 ) return null;
		
		return array;
		
	}
	
	/**
	 * Obtém o MAC, no formato 00:00:00:00:00:00, codificado em um array de bytes.
	 */
	public static String converterBytesEmMac( byte[] mac ) {
		StringBuilder sb = new StringBuilder( 17 );
		sb.append( obterHexFF( mac[ 0 ] ) );
		for( int i = 1; i < 6; i++ ) sb.append( ":" + obterHexFF( mac[ i ] ) );
		return sb.toString();
	}
	
	/**
	 * Converte um IPv4 em um array de bytes.
	 * @param ip xxx.xxx.xxx.xxx, sendo xxx = 0 a 255
	 * @return um array com 4 bytes. <code>null</code> == IP inválido.
	 */
	public static byte[] converterIPv4EmBytes( String ip ) {
		
		int x = ip.indexOf( '.' );
		int y = ip.indexOf( '.', x + 1 );
		int z = ip.indexOf( '.', y + 1 );
		
		int a = Integer.parseInt( ip.substring( 0, x ) );
		int b = Integer.parseInt( ip.substring( x + 1, y ) );
		int c = Integer.parseInt( ip.substring( y + 1, z ) );
		int d = Integer.parseInt( ip.substring( z + 1 ) );
		
		return new byte[]{
				(byte)( a & 0x000000FF ),
				(byte)( b & 0x000000FF ),
				(byte)( c & 0x000000FF ),
				(byte)( d & 0x000000FF )
		};
		
	}
	
	public static boolean igual( byte[] a, byte[] b ) {
		if( a == b ) return true;
		if( a == null || b == null ) return false;
		for( int i = 0; i < a.length; i++ ){
			if( a[i] != b[i] ) return false;
		}
		return true;
	}
	
	public static boolean igual( char[] a, char[] b ) {
		if( a == b ) return true;
		if( a == null || b == null ) return false;
		for( int i = 0; i < a.length; i++ ){
			if( a[i] != b[i] ) return false;
		}
		return true;
	}
	
	public static String obterHexFF( byte valor ) {
		String hex = Integer.toHexString( valor ).toUpperCase();
		int len = hex.length();
		if( len > 2 ) return hex.substring( len - 2, len );
		return len == 2 ? hex : "0" + hex;
	}
	
	public static String obterHexFFFF( int valor ) {
		String hex = Integer.toHexString( valor ).toUpperCase();
		int len = hex.length();
		if( len == 4 ) return hex;
		if( len == 3 ) return "0" + hex;
		if( len == 2 ) return "00" + hex;
		return "000" + hex;
	}
	
	/**
	 * Constrói o nome de um arquivo de captura: [prefixo][indice][sufixo]<br>
	 * Exemplo: arquivo0003.cap == [arquivo][0003][.cap] (4 dígitos)
	 * @param sufixo Não deve conter números.
	 * @param digitos 0 == desconsiderar.
	 */
	public static String criarNomeUnidadeGravacao( String prefixo, int indice, String sufixo, int digitos ) {
		
		if( digitos == 0 ) return prefixo + indice + sufixo;
		
		StringBuilder sb = new StringBuilder();
		for( int i = Integer.toString( indice ).length(); i < digitos; i++ ) sb.append( '0' );
		return sb.insert( 0, prefixo ).append( indice ).append( sufixo ).toString();
		
	}
	
	public static String obterPrefixo( String nomeArquivo ) {
		
		int len = nomeArquivo.length();
		int suf = obterSufixo( nomeArquivo ).length();
		int dig = obterDigitos( nomeArquivo );
		
		return nomeArquivo.substring( 0, len - suf - dig );
		
	}
	
	public static String obterSufixo( String nomeArquivo ) {
		
		int i, ult = nomeArquivo.length() - 1;
		
		for( i = ult; i >= 0 && ! Character.isDigit( nomeArquivo.charAt( i ) ); i-- );
		
		return i < ult ? nomeArquivo.substring( i + 1 ) : "";
		
	}
	
	public static int obterIndice( String nomeArquivo ) {
		
		int len = nomeArquivo.length();
		int suf = obterSufixo( nomeArquivo ).length();
		int dig = obterDigitos( nomeArquivo );
		
		return Integer.parseInt( nomeArquivo.substring( len - suf - dig, len - suf ) );
		
	}
	
	public static int obterDigitos( String nomeArquivo ) {
		
		int i, ult = nomeArquivo.length() - 1, digitos = 0;
		
		for( i = ult; i >= 0 && ! Character.isDigit( nomeArquivo.charAt( i ) ); i-- );
		
		while( i >= 0 && Character.isDigit( nomeArquivo.charAt( i-- ) ) ) digitos++;
		
		return digitos;
		
	}
	
	public static NumeroTransformacao novaMbpsTransformacao( Cultura cultura ) {
		NumeroTransformacao mbpsFormato = cultura.novaRealTransformacao();
		mbpsFormato.setUsarMilha( false );
		return mbpsFormato;
	}

}
