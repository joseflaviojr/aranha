
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
 * VLAN, IEEE 802.1Q.
 * @author José Flávio de Souza Dias Júnior
 * @version 2011
 */
public class VLAN {

	public static final int INDEFINIDO = -1;
	
	private int id = INDEFINIDO;
	
	private int prioridade = INDEFINIDO;
	
	private int cfi = INDEFINIDO;
	
	/**
	 * @param id Pode ser {@link #INDEFINIDO}.
	 * @param prioridade Pode ser {@link #INDEFINIDO}.
	 * @param cfi Pode ser {@link #INDEFINIDO}.
	 */
	public VLAN( int id, int prioridade, int cfi ) {
		this.id = id;
		this.prioridade = prioridade;
		this.cfi = cfi;
	}
	
	public VLAN( int id ) {
		this.id = id;
	}
	
	/**
	 * @param vlan ID.PRIORIDADE.CFI ou ID Exemplos: 400.3.1, 400.?.?, ?.3.?, ?.?.1, 400, ?
	 */
	public VLAN( String vlan ) {
		
		try{
			
			String s;
			int i = vlan.indexOf( '.' );
			
			if( i == -1 ){
				
				s = vlan;
				this.id = s.equals( "?" ) ? INDEFINIDO : Integer.parseInt( s );
				
			}else{
				
				int j = vlan.indexOf( '.', i + 1 );
				
				s = vlan.substring( 0, i );
				this.id = s.equals( "?" ) ? INDEFINIDO : Integer.parseInt( s );
				
				s = vlan.substring( i + 1, j );
				this.prioridade = s.equals( "?" ) ? INDEFINIDO : Integer.parseInt( s );
				
				s = vlan.substring( j + 1, vlan.length() );
				this.cfi = s.equals( "?" ) ? INDEFINIDO : Integer.parseInt( s );
				
			}
			
		}catch( Exception e ){
			throw new IllegalArgumentException( "Formato de VLAN incorreto: " + vlan );
		}
		
	}

	public boolean equivale( VLAN obj ) {
		VLAN o = (VLAN) obj;
		if( id != INDEFINIDO && o.id != INDEFINIDO && id != o.id ) return false;
		if( prioridade != INDEFINIDO && o.prioridade != INDEFINIDO && prioridade != o.prioridade ) return false;
		if( cfi != INDEFINIDO && o.cfi != INDEFINIDO && cfi != o.cfi ) return false;
		return true;
	}
	
	public boolean equals( Object obj ) {
		VLAN o = (VLAN) obj;
		return id == o.id && prioridade == o.prioridade && cfi == o.cfi;
	}
	
	public int hashCode() {
		return id + prioridade + cfi;
	}
	
	public String toString() {
		boolean apenasID = prioridade == INDEFINIDO && cfi == INDEFINIDO;
		return "vlan" + ( id != INDEFINIDO ? id : "?" ) + ( ! apenasID ? "." + ( prioridade != INDEFINIDO ? prioridade : "?" ) + "." + ( cfi != INDEFINIDO ? cfi : "?" ) : "" );
	}
	
	public int getId() {
		return id;
	}
	
	public int getPrioridade() {
		return prioridade;
	}
	
	public int getCFI() {
		return cfi;
	}
	
}