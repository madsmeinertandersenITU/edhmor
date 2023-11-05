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

import java.util.ArrayList;
import java.util.List;
import modules.ModuleSetFactory;
import modules.evaluation.CoppeliaSimEvaluator;

/**
 *
 * @author fai
 */
public class deceptiveness {

    static double[] cromosoma = {0.0, 2.0, 1.0, 1.0, 2.0, 3.0, 4.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,//tipo
        1.0, 1.0, 1.0, 1.0, 2.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,//conexiones
        4.0, 9.0, 8.0, 5.0, 6.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,//cara padre
        2.0, 5.0, 5.0, 4.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,//orientacion
        37.87515809242915, 148.11557010119813, 140.11575866281677, 294.622740119388, 66.36826559708047, 258.01280372902505, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -0.04324740375041103, 0.4877556761588998, -0.23097819120981988, 0.3360408414519077, 0.1645258391870701, 0.1343523646243242, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
    static int nModuleMax = 16;

    public static void main(String[] args) {
        /*
        for (int j = 0; j < 4; j++) {

        double valorOptimo = cromosoma[nModuleMax * 3 - 2 + j];

        List<Double> calidades = new ArrayList<Double>();
        int maxOrientation = ModulesSet.getModulesReducedFacesNumber((int) cromosoma[j + 1]);
        for (int i = 0; i < maxOrientation; i++) {
        cromosoma[nModuleMax * 3 - 2 + j] = i;
        Evaluador evaluador = new Evaluador(cromosoma);
        evaluador.setGuiOn(false);
        double calidad = evaluador.evalua();
        calidades.add(calidad);
        }

        System.out.println("");
        for (int i = 0; i < calidades.size(); i++) {
        System.out.print(calidades.get(i) + ", ");

        }

        cromosoma[nModuleMax * 3 - 2 + j] = valorOptimo;
        }
         */

        for (int j = 0; j < 16; j++) {

            cromosoma[nModuleMax * 2 - 1] = j;

            List<Double> calidades = new ArrayList<Double>();
            int maxOrientation = ModuleSetFactory.getModulesSet().getModuleOrientations((int) cromosoma[1]);
            for (int i = 0; i < maxOrientation; i++) {
                cromosoma[nModuleMax * 3 - 2 ] = i;
                CoppeliaSimEvaluator evaluador = new CoppeliaSimEvaluator(cromosoma);
                evaluador.setGuiOn(false);
                double calidad = evaluador.evaluate();
                calidades.add(calidad);
                System.out.println( j + " " + i + " " + calidad );
            }

            System.out.println("");
//            for (int i = 0; i < calidades.size(); i++) {
//                System.out.print(calidades.get(i) + ", ");
//
//            }

        }

    }
}
