
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

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.joseflavio.aranha.analise.Dia;
import com.joseflavio.cultura.Cultura;
import com.joseflavio.cultura.NumeroTransformacao;
import com.joseflavio.cultura.TransformacaoException;
import com.joseflavio.tqc.console.AplicacaoConsole;
import com.joseflavio.tqc.console.Argumentos;
import com.joseflavio.tqc.console.Cor;
import com.joseflavio.tqc.console.SemArgumentos;
import com.joseflavio.util.CSVUtil;

/**
 * Calcula a média de arquivos gerados pela {@link Analise} {@link Dia}.
 * @author José Flávio de Souza Dias Júnior
 * @version 2011
 */
public class Media extends AplicacaoConsole {
	
	private List<File> arquivos = new ArrayList<File>( 50 );
	
	public static void main( String[] args ) {
		
		Media media = new Media();
		
		try{
			
			media.inicio( args );
			
		}catch( Exception e ){
			
			media.enviarln( Cor.VERMELHA_INTENSA, e.getMessage() );
			
		}
		
	}

	@Override
	protected Argumentos processarArgumentos( String[] args ) {

		if( args.length == 0 ) throw new IllegalArgumentException( "Informe os arquivos dos quais a media sera calculada." );
		
		for( int i = 0; i < args.length; i++ ){
			File arq = new File( args[i] );
			if( ! arq.exists() ) throw new IllegalArgumentException( "Arquivo ou pasta inexistente: " + args[i] );
			if( arq.isFile() ) arquivos.add( arq );
			else for( File f : arq.listFiles() ) arquivos.add( f );
		}
		
		if( arquivos.size() == 0 ) throw new IllegalArgumentException( "Informe os arquivos dos quais a media sera calculada." );
		
		return new SemArgumentos( args );
		
	}
	
	@Override
	protected void principal() {

		try{
			
			StringBuilder saidaNome = new StringBuilder();
			if( arquivos.size() < 10 ){
				for( File f : arquivos ){
					String nome = f.getName();
					saidaNome.append( nome.substring( 0, nome.lastIndexOf( '.' ) + 1 ) );
				}
			}else{
				saidaNome.append( "Aranha Média." );	
			}
			saidaNome.append( "csv" );
			
			executarMedia( arquivos, new File( arquivos.get( 0 ).getParentFile(), saidaNome.toString() ) );
		
		}catch( IOException e ){
			throw new IllegalArgumentException( "Erro de E/S: " + e.getMessage() );
		}catch( TransformacaoException e ){
			throw new IllegalArgumentException( "Erro de integridade: " + e.getMessage() );
		}
		
	}
	
	@Override
	protected void fim() {
	}
	
	public static void executarMedia( List<File> arquivos, File destino ) throws IOException, TransformacaoException {
		
		/* **************** */
		
		FileReader[] entradas = new FileReader[ arquivos.size() ];
		for( int i = 0; i < arquivos.size(); i++ ){
			entradas[i] = new FileReader( arquivos.get( i ) );
		}
		
		/* **************** */
		
		if( destino.exists() ) destino.delete();
		destino.createNewFile();
		FileWriter saida = new FileWriter( destino );
		
		/* **************** */
		
		String[] colunas = new String[ 500 ];
		double[] valores = new double[ 500 ];
		int i, total = 0;
		boolean temLinhas = true;
		StringBuilder buffer = new StringBuilder( 20 );
		NumeroTransformacao mbpsFormato = Util.novaMbpsTransformacao( Cultura.getPadrao() );
		
		for( FileReader entrada : entradas ) total = CSVUtil.proximaLinha( entrada, colunas, ';', buffer );

		for( i = 1; i < total; i++ ) saida.write( ";" + colunas[i] );
		saida.write( '\n' );
		
		while( true ){
			String rotulo = null;
			Arrays.fill( valores, 0d );
			for( FileReader entrada : entradas ){
				if( temLinhas = CSVUtil.proximaLinha( entrada, colunas, ';', buffer ) > 0 ){
					rotulo = colunas[0];
					for( i = 1; i < total; i++ ) valores[i] += mbpsFormato.transformarReal( colunas[i] );
				}
			}
			if( temLinhas ){
				saida.write( rotulo );
				int totalArquivos = arquivos.size();
				for( i = 1; i < total; i++ ) saida.write( ";" + mbpsFormato.transcrever( valores[i] / totalArquivos ) );
				saida.write( '\n' );
			}else{
				break;
			}
		}
		
		/* **************** */
		
		for( FileReader entrada : entradas ) entrada.close();
		saida.close();
		
		/* **************** */
		
	}
	
}
