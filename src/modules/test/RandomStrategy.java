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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import modules.evaluation.CoppeliaSimEvaluator;
import modules.individual.TreeIndividual;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.math.util.ResizableDoubleArray;

/**
 *
 * @author fai
 */
public class RandomStrategy {

    private double calidadMedia = 0;
    private double calidadMax = -50;
    private int maxEval = 0;
    private int poblacion = 0;
    private int sizeCromosoma;
    private double[] cromosomaDouble;
    private int[] cromosoma;
    private int num0,  num1,  num2,  num3,  num4;
    double upper0, upper1, upper2, upper3, upper4;
    double low0, low1, low2, low3, low4;
    TreeIndividual bestTree = null;

    public static void main(String[] args) {

        RandomStrategy yo = new RandomStrategy();
        yo.exec();

    }

    private void exec() {

        EAFRandom.init();

        FileOutputStream fos = null;
        FileOutputStream fosBest = null;
        try {
            try {
                XMLConfiguration config = new XMLConfiguration("./walk_config.xml");
                poblacion = config.getInt("Population.Size");
                maxEval = config.getInt("StopTests.StopTest.MaxFEs");
                sizeCromosoma = config.getInt("Population.Individual.Chromosome[@size]");
                cromosomaDouble = new double[sizeCromosoma];
                cromosoma = new int[sizeCromosoma];
            } catch (ConfigurationException ex) {
                Logger.getLogger(RandomStrategy.class.getName()).log(Level.SEVERE, null, ex);
            }
            double calidadActual, media = 0;
            int generacion = 0;
            fos = new FileOutputStream("./resultados_aleatorio.txt");
            fosBest = new FileOutputStream("./bestAleatorio.txt");
            for (int i = 0; i < maxEval; i++) {
                    

                //generarCromosoma();
                TreeIndividual treeAleatorio = new TreeIndividual();
                ResizableDoubleArray[] dobles = new ResizableDoubleArray[1];
                dobles[0] = new ResizableDoubleArray(sizeCromosoma);
                for (int ii = 0; ii < sizeCromosoma; ii++) {
                    dobles[0].setElement(ii, 0.0);
                }

                treeAleatorio.setChromosomes(dobles);

                treeAleatorio.generate();

                double[] cromoDouble = treeAleatorio.getChromosomeAt(0);


                CoppeliaSimEvaluator decodificador = new CoppeliaSimEvaluator(cromoDouble);
                decodificador.setGuiOn(false);
                calidadActual = decodificador.evaluate();
                treeAleatorio.setFitness(calidadActual);

                media = (media * i  + calidadActual)/ (i + 1);

                calidadMedia += (calidadActual / maxEval);

                if (calidadActual > calidadMax) {
                    calidadMax = calidadActual;
                    bestTree = treeAleatorio.clone();
                }

                if (i % poblacion == 0 && i != 0) {
                    String str = generacion + " - " + calidadMax + " - " + media + " - " + i + "\n";
                    System.out.println(str);
                    fos.write(str.getBytes());
                    String bestStr = generacion + " - " + bestTree.toString() + "\n";
                    fosBest.write(bestStr.getBytes());
                    generacion++;
                }
            }

            String str = generacion + " - " + calidadMax + " - " + media + " - " + maxEval +"\n";
            System.out.println(str);
            fos.write(str.getBytes());
            generacion++;

        } catch (IOException ex) {
            Logger.getLogger(RandomStrategy.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fos.close();
            } catch (IOException ex) {
                Logger.getLogger(RandomStrategy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }



    }

}
