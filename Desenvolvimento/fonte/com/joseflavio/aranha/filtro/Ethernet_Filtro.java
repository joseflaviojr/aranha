
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

package com.joseflavio.aranha.filtro;

import com.joseflavio.aranha.LibpcapLeitor;
import com.joseflavio.aranha.MAC;

/**
 * @author José Flávio de Souza Dias Júnior
 * @version 2011
 */
public class Ethernet_Filtro implements Filtro {

	private MAC alvo;
	
	private boolean origem;
	
	public Ethernet_Filtro( MAC alvo, boolean origem ) {
		this.alvo = alvo;
		this.origem = origem;
	}

	@Override
	public boolean filtrar( LibpcapLeitor pcap ) {
		
		MAC mac = origem ? pcap.getEthernet_Origem() : pcap.getEthernet_Destino();
		
		return mac != null ? mac.equals( alvo ) : false;
		
	}
	
	@Override
	public boolean equals( Object obj ) {
		if( obj == null || !( obj instanceof Ethernet_Filtro ) ) return false;
		Ethernet_Filtro filtro = (Ethernet_Filtro) obj;
		return alvo.equals( filtro.alvo ) && origem == filtro.origem;
	}
	
	@Override
	public int hashCode() {
		return alvo.hashCode() + ( origem ? 17 : 31 );
	}

}
