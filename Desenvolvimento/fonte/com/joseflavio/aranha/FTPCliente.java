
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
 * @author Jos� Fl�vio de Souza Dias J�nior
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
