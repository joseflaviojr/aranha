
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

import com.joseflavio.tqc.console.AplicacaoConsole;
import com.joseflavio.tqc.console.Argumentos;
import com.joseflavio.tqc.console.ChaveadosArgumentos;
import com.joseflavio.tqc.console.ChaveadosArgumentos.AdaptadoArgumentoProcessador;
import com.joseflavio.tqc.console.ChaveadosArgumentos.Chave;
import com.joseflavio.tqc.console.ChaveadosArgumentosBuilder;
import com.joseflavio.tqc.console.Cor;
import com.joseflavio.util.DataSimples;

/**
 * Executa um programa no mesmo hor�rio todo dia.
 * @author Jos� Fl�vio de Souza Dias J�nior
 * @version 2011
 */
public class Agendar extends AplicacaoConsole {
	
	private int hora = 0;
	
	private int minuto = 0;
	
	private String comando;
	
	public static void main( String[] args ) {
		
		Agendar aplicacao = new Agendar();
		
		try{
			
			aplicacao.inicio( args );
			
		}catch( Exception e ){
			
			aplicacao.enviarln( Cor.VERMELHA_INTENSA, e.getMessage() );
			
		}
		
	}
	
	@Override
	protected Argumentos processarArgumentos( String[] args ) {

		ChaveadosArgumentos argumentos = new ChaveadosArgumentosBuilder( args )
		.mais( "-hora", true, new Argumento_hora() )
		.mais( "-minuto", true, new Argumento_minuto() )
		.getChaveadosArgumentos();
		
		argumentos.processarArgumentos( new Argumento_comando() );
		
		if( comando == null || comando.length() == 0 ) throw new IllegalArgumentException( "Informe o comando a ser executado." );
		
		return argumentos;
		
	}
	
	@Override
	protected void principal() {

		try{
			
			DataSimples data = new DataSimples();
			
			while( true ){
				
				data.setTimestamp( System.currentTimeMillis() );
				
				if( data.getHora() == hora && data.getMinuto() == minuto ){
					Runtime.getRuntime().exec( comando );
					Thread.sleep( 50000 );	
				}
				
				Thread.sleep( 10000 );
				
			}
			
		}catch( Exception e ){
			throw new IllegalArgumentException( e.getMessage() );
		}
		
	}
	
	@Override
	protected void fim() {
	}
	
	private class Argumento_hora extends AdaptadoArgumentoProcessador {
		public void processar( Chave chave, String valor ) throws IllegalArgumentException {
			try{
				hora = (int) getCultura().novaInteiroTransformacao().transformarInteiro( valor );
				if( hora < 0 || hora > 23 ) throw new IllegalArgumentException();
			}catch( Exception e ){
				throw new IllegalArgumentException( "Hora incorreta: " + valor );
			}
		}
	}
	
	private class Argumento_minuto extends AdaptadoArgumentoProcessador {
		public void processar( Chave chave, String valor ) throws IllegalArgumentException {
			try{
				minuto = (int) getCultura().novaInteiroTransformacao().transformarInteiro( valor );
				if( minuto < 0 || minuto > 59 ) throw new IllegalArgumentException();
			}catch( Exception e ){
				throw new IllegalArgumentException( "Minuto incorreto: " + valor );
			}
		}
	}
	
	private class Argumento_comando extends AdaptadoArgumentoProcessador {
		public void processar( Chave chave, String valor ) throws IllegalArgumentException {
			comando = valor;
		}
	}
	
}
