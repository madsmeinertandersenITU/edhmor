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
import modules.evaluation.CoppeliaSimEvaluator;
import modules.jeaf.operation.MutationOperation;
import modules.jeaf.operation.grow.AddNode;
import modules.individual.Connection;
import modules.individual.Node;
import modules.individual.TreeIndividual;
import org.apache.commons.math.util.ResizableDoubleArray;

/**
 *
 * @author fai
 */
public class TestOperation {

    public static void main(String[] args) {

        EAFRandom.init();
        int size = 37;


        TreeIndividual arbol = new TreeIndividual();

        ResizableDoubleArray[] dobles = new ResizableDoubleArray[1];

        dobles[0] = new ResizableDoubleArray(size);

        for (int i = 0; i < size; i++) {
            dobles[0].setElement(i, 0.0);
        }

        arbol.setChromosomes(dobles);
        
        //Construyo el arlbol o descomento la linea de abajo
        //arbol.generate();-
        Node rootNode = new Node(0, null);
        Node slider1Node = new Node(1, rootNode);
        Connection conexion = new Connection(rootNode, slider1Node, 6, 2);
        rootNode.addChildren(slider1Node, conexion);

        Node slider2Node = new Node(1, rootNode);
        conexion = new Connection(rootNode, slider2Node, 1, 4);
        rootNode.addChildren(slider2Node, conexion);
        
        Node slider3Node = new Node(1, rootNode);
        conexion = new Connection(rootNode, slider3Node, 5, 6);
        rootNode.addChildren(slider3Node, conexion);

        Node slider4Node = new Node(1, rootNode);
        conexion = new Connection(rootNode, slider4Node, 10, 6);
        rootNode.addChildren(slider4Node, conexion);

        Node slider5Node = new Node(1, slider3Node);
        conexion = new Connection(slider3Node, slider5Node, 2, 6);
        slider3Node.addChildren(slider5Node, conexion);

        Node slider6Node = new Node(1, slider5Node);
        conexion = new Connection(slider5Node, slider6Node, 6, 6);
        slider5Node.addChildren(slider6Node, conexion);

//        Node tele1Node = new Node(2, 0, 0, slider1Node);
//        conexion = new Connection(slider1Node, tele1Node, 3, 2);
//        slider1Node.addChildren(tele1Node, conexion);

//        Node tele2Node = new Node(2, 0, 0, slider2Node);
//        conexion = new Connection(slider2Node, tele2Node, 3, 4);
//        slider2Node.addChildren(tele2Node, conexion);

        arbol.setRootNode(rootNode);
        arbol.modifyChromosome();


        double[] valuesIni = arbol.getChromosomes()[0].getElements();



        CoppeliaSimEvaluator evaluadorIni = new CoppeliaSimEvaluator(valuesIni);
        evaluadorIni.setGuiOn(true);
        double calidadIni = 0;
        //calidadIni = evaluadorIni.evalua();
        System.out.println("Calidad = " + calidadIni);

        

        for (int j = 0; j < 1; j++) {

            System.out.println("");
            System.out.println("");
            System.out.println("Iteracion: " + j);
            System.out.println("");
            System.out.println("");

            MutationOperation op = new AddNode();
            op.run(arbol);

            double[] values = arbol.getChromosomes()[0].getElements();

            
            CoppeliaSimEvaluator evaluador = new CoppeliaSimEvaluator(values);
            evaluador.setGuiOn(true);
            double calidad = evaluador.evaluate();
            System.out.println("Calidad = " + calidad);


        }
    }
}
