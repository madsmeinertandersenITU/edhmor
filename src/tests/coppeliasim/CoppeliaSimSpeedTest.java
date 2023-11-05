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
package tests.coppeliasim;

import modules.evaluation.CoppeliaSimEvaluator;

/**
 *
 * @author fai
 */
public class CoppeliaSimSpeedTest {
    
        private int n_modulos = 6;
    private double calidad = 0;

    public static void main(String[] args) {
        double cromoDouble[] = {3.249298024954898, 4.135653584102353, 4.1513540012644805, 1.0, 1.0, 2.6658390187857073, 4.9662404789876655, 4.000493774578773, 0.32995753106063674, 2.4211479688942124, 2.5717063219831653, 2.1168381917788253, 0.1573403992158664, 2.999, 12.633716127020676, 0.0, 6.4162639110070625, 8.398481428361832, 12.065733841846003, 11.170619752845555, 13.999, 3.949603847665304, 4.289963392817382, 5.999, 5.999, 3.6357528325485005, 3.673283235801228, 2.259717806887455, 359.99, 281.8558936694033, 307.67341626032777, 359.99, 263.75254087500014, 39.178389659867705, 262.40953170085635};
        //double cromoDouble[] = new double[args.length];
        int cromosoma[];
        
        int nPruebas=5;
        
        if (args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                if(args[i].endsWith(","))
                    args[i]=args[i].substring(0, args[i].length()-1);
                System.out.print(args[i]+" ");
                cromoDouble[i] =  (Double.valueOf(args[i]));
            }
            cromosoma = new int[args.length];
        }else{
            cromosoma = new int[cromoDouble.length];
        }

        System.out.println();
        for (int i = 0; i < cromoDouble.length; i++) {
            cromosoma[i] = (int) Math.floor(cromoDouble[i]);
        }
        double cromosoma2[] = {2, 1, 2, 2, 2, 1, 2, 2, 1, 2, 2, 3, 1, 3, 3, 1, 3, 3, 3, 2, 8, 10, 6, 3, 4, 0, 0, 9, 0, 3, 3, 5, 5, 0, 5, 3, 3, 168, 65, 200, 81, 320, 315, 359, 334, 86, 359};
        //int cromosoma2[] = {2, 2, 2, 4, 2, 4, 1, 1, 1, 1, 0, 0, 0, 10, 8, 4, 2, 4, 2, 0, 1, 166, 338, 237, 74, 17, 246};
        //int cromosoma2[]={1, 1, 1, 4, 1, 4, 1, 1, 1, 1, 0, 0, 0, 10, 8, 4, 2, 4, 2, 0, 1, 166, 338, 237, 74, 17, 246};
        System.out.println("Clase Test");
        double goodTest1[]={0, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4,
                           4, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                           1, 4, 7, 10, 0, 0, 0, 0, 0, 0, 0, 0,
                           0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0,
                           0,   180, 180, 180,   180, 180, 180,   180, 180, 180,   0, 0, 0

        };
        
        double demo2[] = {0.0, 1.0, 1.0, 1.0, 1.0, 2.0, 2.0, 2.0, 2.0, 0.0,4.0, 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 4.0, 7.0, 10.0, 12.0, 4.0, 12.0, 2.0, 0.0,0.0, 5.0, 0.0, 5.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 180.0, 180.0, 180.0, 0.0, 180.0, 0.0, 180.0, 0.0};
        
        
        double [] calidades = new double[nPruebas];
        long [] tiempos = new long[nPruebas];

        double calMin=Double.MAX_VALUE, calMax=Double.NEGATIVE_INFINITY, calMedia=0, desviacionTipicaCal=0;
        long tempMin=Long.MAX_VALUE, tempMax=Long.MIN_VALUE, tempMedia=0, desviacionTipicaTemp=0;
        for(int i =0; i<nPruebas; i++){
            System.out.println("EVALUACION CALIDAD "+i);

            long tempIni, tempFinal, tiempo;
            tempIni=System.currentTimeMillis();
            CoppeliaSimEvaluator evaluador = new CoppeliaSimEvaluator( demo2);
            evaluador.setGuiOn(false);
            double calidad = evaluador.evaluate();
            tempFinal=System.currentTimeMillis();
            tiempo=tempFinal-tempIni;

            if (calidad>calMax)
                calMax=calidad;
            if (calidad<calMin)
                calMin=calidad;
            calMedia+=calidad;
            calidades[i]=calidad;

            if (tiempo>tempMax)
                tempMax=tiempo;
            if (tiempo<tempMin)
                tempMin=tiempo;
            tempMedia+=tiempo;
            tiempos[i]=tiempo;
        }
        calMedia=calMedia/nPruebas;
        tempMedia=tempMedia/nPruebas;
        
        for(int i =0; i<nPruebas; i++){
            System.out.println("Calidad " + i + ": " + calidades[i]);
            desviacionTipicaCal+=((calidades[i]-calMedia)*(calidades[i]-calMedia));
        }

        System.out.println();

        for(int i =0; i<nPruebas; i++){
            System.out.println("Tiempo " + i + ": " + tiempos[i]);
            desviacionTipicaTemp+=((tiempos[i]-tempMedia)*(tiempos[i]-tempMedia));
        }
        
        desviacionTipicaCal=Math.sqrt(desviacionTipicaCal/nPruebas);
        desviacionTipicaTemp= (long) Math.sqrt(desviacionTipicaTemp/nPruebas);
        System.out.println();
        System.out.println();

        System.out.println("Calidad Media= " + calMedia + ", Desviacion Tipica: " + desviacionTipicaCal);
        System.out.println("Calidad Maxima: " + calMax +", Calidad Minima: " + calMin);

        System.out.println();

        System.out.println("Tiempo Medio= " + tempMedia + ", Desviacion Tipica: " + desviacionTipicaTemp);
        System.out.println("Tiempo Maximo: " + tempMax +", Tiempo Minimo: " + tempMin);
    }

    public double getCalidad() {
        return calidad;
    }

}
