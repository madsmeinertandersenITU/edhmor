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
package modules.individual;

import es.udc.gii.common.eaf.util.EAFRandom;
import modules.evaluation.CoppeliaSimEvaluator;
import org.apache.commons.math.util.ResizableDoubleArray;

/**
 *
 * @author fai
 */
public class TestGenerateTree {

    public static void main(String[] args) {

        EAFRandom.init();
        int size = 37;

        for (int j = 0; j < 20; j++) {


            TreeIndividual arbol = new TreeIndividual();

            ResizableDoubleArray[] dobles = new ResizableDoubleArray[1];

            dobles[0] = new ResizableDoubleArray(size);

            for (int i = 0; i < size; i++) {
                dobles[0].setElement(i, 0.0);
            }

            arbol.setChromosomes(dobles);
            arbol.generate();

            double[] values = arbol.getChromosomes()[0].getElements();



            System.out.print("\nvalues (Objetive function): ");
            for (int i = 0; i < values.length; i++) {
                System.out.print(values[i] + ", ");

            }



            CoppeliaSimEvaluator evaluador = new CoppeliaSimEvaluator(values);
            evaluador.setGuiOn(true);
            double calidad = evaluador.evaluate();
            System.out.println("Calidad = " + calidad);
        }
    }
}
