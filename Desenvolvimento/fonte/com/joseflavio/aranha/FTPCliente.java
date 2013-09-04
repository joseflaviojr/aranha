
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
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

/**
 * @author José Flávio de Souza Dias Júnior
 * @version 2011
 */
public class FTPCliente extends Thread {
	
	private FTPClient ftp;
	
	private String servidor;
	
	private int porta;
	
	private String usuario;
	
	private String senha;
	
	private File origem;
	
	private String destino;
	
	private Pattern mascara;
	
	private long intervalo;
	
	private Map<String, Long> ultimaModificacao = new HashMap<String, Long>();
	
	public FTPCliente( String servidor, String usuario, String senha, File origem, String destino, String mascara, long intervalo ) {

		this.ftp = new FTPClient();
		
		if( servidor.contains( ":" ) ){
			String partes[] = servidor.split( ":" );
			this.servidor = partes[0];
			this.porta = Integer.parseInt( partes[1] );
		}else{
			this.servidor = servidor;
			this.porta = ftp.getDefaultPort();
		}
		
		this.usuario = usuario;
		this.senha = senha;
		this.origem = origem;
		this.destino = destino;
		this.mascara = Pattern.compile( mascara );
		this.intervalo = intervalo;

	}

	@Override
	public void run() {

		try{
			
			while( true ){
				
				try{
				
					if( ! ftp.isConnected() ){
						ftp.setUseEPSVwithIPv4( true );
						ftp.connect( servidor, porta );
						if( ! FTPReply.isPositiveCompletion( ftp.getReplyCode() ) || ! ftp.login( usuario, senha ) ) ftp.disconnect();
					}
					
					if( ftp.isConnected() ){

						if( ! ftp.changeWorkingDirectory( destino ) ){
							ftp.makeDirectory( destino );
							ftp.changeWorkingDirectory( destino );
						}
						
						ftp.setFileType( FTP.BINARY_FILE_TYPE );
						
						for( File arquivo : origem.listFiles( new FilenameFilterImpl() ) ){
							
							String nome = arquivo.getName();
							
							long atual = arquivo.lastModified();
							Long ultima = ultimaModificacao.get( nome );
							ultimaModificacao.put( nome, atual );
							
							if( ultima != null && ultima == atual ) continue;
							
							InputStream conteudo = new FileInputStream( arquivo );
							ftp.storeFile( nome, conteudo );
							conteudo.close();
							
						}
						
					}
					
				}catch( Exception e ){
					if( ftp.isConnected() ){
						try{
							ftp.disconnect();
						}catch( Exception f ){
						}
					}
				}

				Thread.sleep( intervalo );
				
			}
			
		}catch( Exception e ){
		}finally{
			if( ftp.isConnected() ){
				try{
					ftp.disconnect();
				}catch( Exception f ){
				}
			}
		}
		
	}

	private class FilenameFilterImpl implements FilenameFilter {
		public boolean accept( File dir, String name ) {
			return mascara.matcher( name ).matches();
		}
	}
	
}
