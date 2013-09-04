
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Formatter;

/**
 * @author José Flávio de Souza Dias Júnior
 * @version 2011
 */
public abstract class ArquivavelAnalise implements Analise {

	private File arquivo;
	
	private Writer saida;
	
	private Formatter formatador;
	
	protected void criarArquivo( File local, String nome ) throws IOException {

		arquivo = new File( local, nome );
		
		limparArquivo();
		
	}

	protected void limparArquivo() throws IOException {
		
		if( arquivo != null ){
			
			if( saida != null ) saida.close();
			
			if( arquivo.exists() ) arquivo.delete();
			arquivo.createNewFile();
			
			saida = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( arquivo ) ) );
			formatador = new Formatter( saida );
			
		}
		
	}
	
	protected void escrever( String texto ) throws IOException {
		saida.write( texto );
	}
	
	protected void escrever( char letra ) throws IOException {
		saida.write( letra );
	}
	
	/**
	 * @see Formatter
	 */
	protected void escrever( String formato, Object... args ) throws IOException {
		formatador.format( formato, args );
	}
	
	protected void descarregarArquivo() throws IOException {

		if( saida != null ) saida.flush();
		
	}

	protected void fecharArquivo() throws IOException {

		if( arquivo != null ){
		
			formatador = null;
			
			saida.close();
			saida = null;
			
			arquivo = null;
		
		}
		
	}
	
	/**
	 * @return <code>null</code> caso não {@link #criarArquivo(File, String)}
	 */
	protected File getArquivo() {
		return arquivo;
	}
	
	/**
	 * @return {@link #getArquivo()} != <code>null</code>
	 */
	protected boolean arquivoAberto() {
		return arquivo != null;
	}

}
