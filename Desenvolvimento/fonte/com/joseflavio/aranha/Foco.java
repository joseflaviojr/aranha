
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

import com.joseflavio.aranha.filtro.Filtro;

/**
 * @author José Flávio de Souza Dias Júnior
 * @version 2011
 */
public class Foco {
	
	public IP ip;
	
	public MAC mac;
	
	public Filtro filtro;
	
	public String titulo;
	
	public Foco( IP ip, String titulo ) {
		if( ip == null ) throw new IllegalArgumentException();
		this.ip = ip;
		this.titulo = titulo;
	}
	
	public Foco( IP ip ) {
		this( ip, null );
	}
	
	public Foco( MAC mac, String titulo ) {
		if( mac == null ) throw new IllegalArgumentException();
		this.mac = mac;
		this.titulo = titulo;
	}
	
	public Foco( MAC mac ) {
		this( mac, null );
	}
	
	public Foco( Filtro filtro, String titulo ) {
		if( filtro == null ) throw new IllegalArgumentException();
		this.filtro = filtro;
		this.titulo = titulo;
	}

	public boolean equivale( IP ip ){
		return this.ip != null && ip != null ? this.ip.equivale( ip ) : false;
	}
	
	public boolean equivale( MAC mac ){
		return this.mac != null && mac != null ? this.mac.equals( mac ) : false;
	}
	
	/**
	 * @see Filtro#filtrar(LibpcapLeitor)
	 */
	public boolean equivale( LibpcapLeitor pcap ){
		return filtro != null ? filtro.filtrar( pcap ) : false;
	}
	
	@Override
	public boolean equals( Object obj ) {
		Foco o = (Foco) obj;
		if( ip != null ) return o.ip != null ? ip.equals( o.ip ) : false;
		if( mac != null ) return o.mac != null ? mac.equals( o.mac ) : false;
		return o.filtro != null ? filtro.equals( o.filtro ) : false;
	}
	
	@Override
	public int hashCode() {
		return ip != null ? ip.hashCode() : mac != null ? mac.hashCode() : filtro.hashCode();
	}
	
	public String toString() {
		if( titulo != null ) return titulo;
		return ip != null ? ip.toString() : mac != null ? mac.toString() : filtro.toString();
	}
	
	public boolean isFiltro() {
		return filtro != null;
	}
	
	public boolean isIP() {
		return ip != null;
	}
	
	public boolean isMAC() {
		return mac != null;
	}
	
}