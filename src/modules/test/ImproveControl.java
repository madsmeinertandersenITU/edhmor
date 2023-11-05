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
package modules.test;

import es.udc.gii.common.eaf.util.EAFRandom;
import modules.evaluation.CoppeliaSimEvaluator;
import modules.jeaf.operation.MutationOperation;
import modules.jeaf.operation.ShakingControl;
import modules.individual.String2Tree;
import modules.individual.TreeIndividual;

/**
 *
 * @author fai
 */
public class ImproveControl {

    static int nImprove = 1;
    static int nVariations = 30;

    public static void main(String[] args) {

        EAFRandom.init();


        String str = "0.0, 3.0, 4.0, 4.0, 4.0, 4.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 2.0, 2.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 7.0, 7.0, 8.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 219.26429211379258, 335.61590566923803, 294.94316563017884, 265.7191058446118, 183.0850262547146, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0";
        String2Tree str2Tree = new String2Tree(str);
        TreeIndividual arbol = str2Tree.toTree();
        arbol.setFitness(-10000000.0);

        System.out.println(arbol.detailedToString());
        System.out.println("\n\n");


        for (int i = 0; i < nImprove; i++) {

            for (int j = 0; j < nVariations; j++) {

                MutationOperation op = new ShakingControl(0.2, 0.1);
                op.run(arbol);
                arbol.modifyChromosome();

                double[] values = arbol.getChromosomes()[0].getElements();



                CoppeliaSimEvaluator evaluador = new CoppeliaSimEvaluator(values);
                evaluador.setGuiOn(false);
                double calidad = evaluador.evaluate();
                arbol.setFitness(calidad);

                op.repair(arbol);

                System.out.println("\nCalidad: " + arbol.getFitness());

            }

        }

    }
}
