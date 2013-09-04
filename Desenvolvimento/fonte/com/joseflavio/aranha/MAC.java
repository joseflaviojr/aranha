
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
public class MAC {

	private byte[] mac;
	
	private int hash = 0;
	
	public MAC( byte[] mac ) {
		this.mac = new byte[ mac.length ];
		System.arraycopy( mac, 0, this.mac, 0, mac.length );
	}
	
	public MAC( String mac ) {
		this.mac = Util.converterMacEmBytes( mac );
	}
	
	public boolean equals( Object obj ) {
		MAC o = (MAC) obj;
		return Util.igual( mac, o.mac );
	}
	
	public int hashCode() {
		if( hash == 0 ){
			for( int i = 0; i < mac.length; i++ ){
				hash = 31 * hash + mac[i];	
			}
		}
		return hash;
	}
	
	public String toString() {
		return Util.converterBytesEmMac( mac );
	}
	
}