
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

/* Generated By:JavaCC: Do not edit this line. AranhaFiltragemConstants.java */
package com.joseflavio.aranha.filtro.javacc;


/**
 * Token literal values and constants.
 * Generated by org.javacc.parser.OtherFilesGen#start()
 */
public interface AranhaFiltragemConstants {

  /** End of File. */
  int EOF = 0;
  /** RegularExpression Id. */
  int IGUAL = 5;
  /** RegularExpression Id. */
  int DIFERENTE = 6;
  /** RegularExpression Id. */
  int MAIOR = 7;
  /** RegularExpression Id. */
  int MAIOR_IGUAL = 8;
  /** RegularExpression Id. */
  int MENOR = 9;
  /** RegularExpression Id. */
  int MENOR_IGUAL = 10;
  /** RegularExpression Id. */
  int E = 11;
  /** RegularExpression Id. */
  int OU = 12;
  /** RegularExpression Id. */
  int OUEX = 13;
  /** RegularExpression Id. */
  int NULO = 14;
  /** RegularExpression Id. */
  int VERDADEIRO = 15;
  /** RegularExpression Id. */
  int FALSO = 16;
  /** RegularExpression Id. */
  int D = 17;
  /** RegularExpression Id. */
  int H = 18;
  /** RegularExpression Id. */
  int B = 19;
  /** RegularExpression Id. */
  int DECIMAL = 20;
  /** RegularExpression Id. */
  int HEXADECIMAL = 21;
  /** RegularExpression Id. */
  int BYTE = 22;
  /** RegularExpression Id. */
  int IPV4_ = 23;
  /** RegularExpression Id. */
  int IPV4 = 24;
  /** RegularExpression Id. */
  int MAC = 25;

  /** Lexical state. */
  int DEFAULT = 0;

  /** Literal token values. */
  String[] tokenImage = {
    "<EOF>",
    "\" \"",
    "\"\\r\"",
    "\"\\n\"",
    "\"\\t\"",
    "\"=\"",
    "\"!=\"",
    "\">\"",
    "\">=\"",
    "\"<\"",
    "\"<=\"",
    "\"e\"",
    "\"ou\"",
    "\"ouex\"",
    "\"nulo\"",
    "\"v\"",
    "\"f\"",
    "<D>",
    "<H>",
    "<B>",
    "<DECIMAL>",
    "<HEXADECIMAL>",
    "<BYTE>",
    "<IPV4_>",
    "<IPV4>",
    "<MAC>",
    "\"(\"",
    "\")\"",
    "\"ip.o\"",
    "\"ip.d\"",
    "\"ip.tipo\"",
    "\"ip.tos\"",
    "\"eth.o\"",
    "\"eth.d\"",
    "\"eth.tipo\"",
    "\"vlan.pri\"",
    "\"vlan.cfi\"",
    "\"vlan.id\"",
    "\"vlan\"",
    "\"mpls.ttl\"",
    "\"mpls.bits\"",
    "\"mpls.rotulo\"",
    "\"mpls\"",
    "\"tcp.o\"",
    "\"tcp.d\"",
    "\"[\"",
    "\"]\"",
  };

}
