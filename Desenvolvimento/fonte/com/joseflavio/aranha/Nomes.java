
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

import java.util.Arrays;

/**
 * @author Jos� Fl�vio de Souza Dias J�nior
 * @version 2011
 */
public class Nomes {
	
	private static String[] ETHERNET_SIMPLES = new String[ 0x10000 ];
	
	private static String[] IP_SIMPLES = new String[ 0xFF ];
	
	static {
		
		Arrays.fill( ETHERNET_SIMPLES, "" );
		ETHERNET_SIMPLES[ 0x0800 ] = "IPv4";
		ETHERNET_SIMPLES[ 0x86DD ] = "IPv6";
		ETHERNET_SIMPLES[ 0x0806 ] = "ARP";
		ETHERNET_SIMPLES[ 0x8035 ] = "RARP";
		ETHERNET_SIMPLES[ 0x8100 ] = "VLAN";
		for( int i = 0x0000; i <= 0x05DC; i++ ) ETHERNET_SIMPLES[ i ] = "IEEE802.3";
		for( int i = 0x0101; i <= 0x01FF; i++ ) ETHERNET_SIMPLES[ i ] = "Experimental";
		for( int i = 0x8137; i <= 0x8138; i++ ) ETHERNET_SIMPLES[ i ] = "Novell";
		for( int i = 0x8847; i <= 0x8848; i++ ) ETHERNET_SIMPLES[ i ] = "MPLS";

		Arrays.fill( IP_SIMPLES, "" );
		IP_SIMPLES[  6 ] = "TCP";
		IP_SIMPLES[ 17 ] = "UDP";
		IP_SIMPLES[  4 ] = "IPv4";
		IP_SIMPLES[ 41 ] = "IPv6";
		IP_SIMPLES[  1 ] = "ICMP";
		IP_SIMPLES[  2 ] = "IGMP";
		IP_SIMPLES[ 58 ] = "IPv6-ICMP";
		IP_SIMPLES[ 89 ] = "OSPFIGP";
		
	}
	
	public static String getEthernetSimples( int protocolo ) {
		return ETHERNET_SIMPLES[ protocolo & 0x0000FFFF ];
	}
	
	public static String getIPSimples( int protocolo ) {
		return IP_SIMPLES[ protocolo & 0x000000FF ];
	}

}
