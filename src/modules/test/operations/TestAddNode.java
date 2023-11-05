/* 
 * EDHMOR - Evolutionary designer of heterogeneous modular robots
 * <https://bitbucket.org/afaina/edhmor>
 * Copyright (C) 2015 GII (UDC) and REAL (ITU)
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package modules.test.operations;

import es.udc.gii.common.eaf.util.EAFRandom;
import modules.individual.Node;
import modules.individual.String2Tree;
import modules.individual.TreeIndividual;
import modules.util.SimulationConfiguration;

/**
 *
 * @author fai
 */
public class TestAddNode {

    public static void main(String[] args) {

        EAFRandom.init();

        
        //String str = "0.0, 3.0, 3.0, 1.0, 4.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 3.0, 0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 7.0, 11.0, 7.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 2.0, 6.0, 1.0, 5.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 338.6768364576931, 32.19388164330659, 184.6111251784785, 61.47740774274864, 55.909001633456455, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0";
        String str = "0.0, 3.0, 3.0, 3.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 2.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 11.0, 7.0, 5.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 3.0, 2.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 122.46384992476248, 352.2638878731472, 184.154423501979, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0";
        String2Tree str2Tree = new String2Tree(str);
        TreeIndividual arbol = str2Tree.toTree();

        System.out.println(arbol.detailedToString());
        System.out.println("\n\n");



        //Creamos el nodo un nodo
        //Variables de inicializacion del arbol
        int nTypeMax = SimulationConfiguration.getMaxTypeModules();
        int nTypeMin = SimulationConfiguration.getMinTypeModules();
        int nType = nTypeMax - nTypeMin + 1;


        //Seleccionamos el tipo del  nodo
        int type = 4;//EAFRandom.nextInt(nType) + nTypeMin;;



        Node nodoPadre =null;
//        for (Node nodo : arbol.getListNode()) {
//            if (nodo.getType() == 4) {
//                nodoPadre = nodo;
//            }
//        }
        nodoPadre=arbol.getRootNode();

        Node newNode = new Node(type, nodoPadre);
        nodoPadre.addChildren(newNode);

        arbol.modifyChromosome();

        System.out.println(arbol.detailedToString());

        //String2Tree str2TreeMod = new String2Tree(arbol.toString());

    }
}
