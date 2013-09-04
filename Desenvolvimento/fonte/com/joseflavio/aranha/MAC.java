
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
 * @author Jos� Fl�vio de Souza Dias J�nior
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