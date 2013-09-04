
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

import java.io.File;
import java.util.Arrays;

import com.joseflavio.tqc.console.AplicacaoConsole;
import com.joseflavio.tqc.console.Argumentos;
import com.joseflavio.tqc.console.ChaveadosArgumentos;
import com.joseflavio.tqc.console.ChaveadosArgumentos.AdaptadoArgumentoProcessador;
import com.joseflavio.tqc.console.ChaveadosArgumentos.Chave;
import com.joseflavio.tqc.console.ChaveadosArgumentosBuilder;

/**
 * Organiza os arquivos de captura do TCPDump.
 * @author Jos� Fl�vio de Souza Dias J�nior
 * @version 2011
 */
class OrganizarTCPDump extends AplicacaoConsole {
	
	private File fonte = new File( System.getProperty( "user.dir" ) );
	
	private String prefixo = "cap";
	
	private String sufixo = "";
	
	private int digitos = 3;
	
	private boolean prePrefixar;
	
	protected OrganizarTCPDump( boolean prePrefixar ) {
		this.prePrefixar = prePrefixar;
	}

	@Override
	protected Argumentos processarArgumentos( String[] args ) {

		ChaveadosArgumentos argumentos = new ChaveadosArgumentosBuilder( args )
		.mais( "-fonte", true, new Argumento_fonte() )
		.mais( "-prefixo", true, new Argumento_prefixo() )
		.mais( "-sufixo", true, new Argumento_sufixo() )
		.mais( "-digitos", true, new Argumento_digitos() )
		.getChaveadosArgumentos();
		
		argumentos.processarArgumentos( null );
		
		if( digitos <= 0 ) throw new IllegalArgumentException( "Quantidade de digitos incorreta: " + digitos );
		
		return argumentos;
		
	}
	
	@Override
	protected void principal() {

		try{
			
			File[] arquivos = fonte.listFiles();
			int indice = 0;
			
			Arrays.sort( arquivos, new TCPDumpComparator( prePrefixar ) );
			
			for( File arquivo : arquivos ){
				
				String nome = arquivo.getName();
				if( nome.charAt( 0 ) == '.' ) nome = nome.substring( 1 );
				
				if( ! Util.obterPrefixo( nome ).equals( prefixo ) ) continue;
				if( ! Util.obterSufixo( nome ).equals( sufixo ) ) continue;
				if( Util.obterDigitos( nome ) != digitos ) continue;
				
				arquivo.renameTo( new File( fonte, ( prePrefixar ? "." : "" ) + Util.criarNomeUnidadeGravacao( prefixo, indice++, sufixo, digitos ) ) );
				
			}
			
		}catch( Exception e ){
			throw new IllegalArgumentException( "Erro ao processar um dos arquivos: " + e.getMessage() );
		}
		
	}
	
	@Override
	protected void fim() {
	}
	
	private class Argumento_fonte extends AdaptadoArgumentoProcessador {
		public void processar( Chave chave, String valor ) throws IllegalArgumentException {
			fonte = new File( valor );
			if( ! fonte.exists() ) fonte = new File( new File( System.getProperty( "user.dir" ) ), valor );
			if( ! fonte.exists() || ! fonte.isDirectory() ) throw new IllegalArgumentException( "Fonte inexistente: " + valor );
		}
	}
	
	private class Argumento_prefixo extends AdaptadoArgumentoProcessador {
		public void processar( Chave chave, String valor ) throws IllegalArgumentException {
			prefixo = valor;
		}
	}
	
	private class Argumento_sufixo extends AdaptadoArgumentoProcessador {
		public void processar( Chave chave, String valor ) throws IllegalArgumentException {
			sufixo = valor;
		}
	}

	private class Argumento_digitos extends AdaptadoArgumentoProcessador {
		public void processar( Chave chave, String valor ) throws IllegalArgumentException {
			try{
				digitos = (int) getCultura().novaInteiroTransformacao().transformarInteiro( valor );
			}catch( Exception e ){
				throw new IllegalArgumentException( "Quantidade de digitos incorreta: " + valor );
			}
		}
	}
	
}
