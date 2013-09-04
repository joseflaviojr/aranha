
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Formatter;

/**
 * @author Jos� Fl�vio de Souza Dias J�nior
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
	 * @return <code>null</code> caso n�o {@link #criarArquivo(File, String)}
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
