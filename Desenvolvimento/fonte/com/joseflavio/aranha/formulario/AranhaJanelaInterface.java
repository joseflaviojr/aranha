
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

package com.joseflavio.aranha.formulario;


import java.awt.Color;
import java.util.List;
import java.util.Map;

/**
 * @author José Flávio de Souza Dias Júnior
 * @version 2011
 */
public interface AranhaJanelaInterface {

    public Map<String, String> sistema_abrirConfiguracao();
    public void sistema_salvarConfiguracao( Map<String, String> propriedades );

    public List<String> captura_interfaces();
    public String captura_destino( String atual );
    public boolean captura_executar( String interfac, String destino, String prefixo, String tamanho, String digitos, String bytesPorPacote, boolean preTCPDump, boolean posTCPDump ) throws IllegalArgumentException;
    public void captura_parar();

    public String analise_capturaInicial( String atual );
    public String analise_destino( String atual );
    public void analise_executar( String capturaInicial, String indiceFinal, boolean leituraEterna, String periodoInicial, String periodoFinal, String intervalo, String unidadeTempo, List<String> focos, String destino, boolean grafico, boolean resumoGeral, boolean resumoDiario, boolean trafegoGeralIP, boolean trafegoDiarioIP, boolean trafegoRecenteIP, boolean trafegoRecenteFocosIP, boolean trafegoGeralMAC, boolean registroURL ) throws IllegalArgumentException;

    public String relatorio_grafico_arquivo( String atual );
    public List<String> relatorio_grafico_serie( String arquivo );
    public void relatorio_grafico_visualizar( String arquivo, String titulo, int serieTotal, int[] serieIndice, Color[] serieCor, String[] serieRotulo, boolean podaAutomatica, String podaInicialData, String podaFinalData, String janela, String intervalo ) throws IllegalArgumentException;

    public String relatorio_medicao_arquivo( String atual );
    public void relatorio_medicao_visualizar( String arquivo, String titulo, String ordem, String fonte, String matriz, String intervalo ) throws IllegalArgumentException;

    public void ferramentaNavegador();
    public void ferramentaMedia();
    public void ferramentaControleEspacoLivre();

    public void finalizando();

}
