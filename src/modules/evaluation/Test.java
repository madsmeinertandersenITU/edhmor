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
package modules.evaluation;

import modules.util.SimulationConfiguration;


/**
 *
 * @author fai
 */
public class Test {

    private int n_modulos = 6;
    private double calidad = 0;
    private static boolean visibilidad = false;
    private static int updateRate = 0;
    private static double maxSimulationTime = SimulationConfiguration.getMaxSimulationTime();

//    public static double main(String cromo, boolean gui, int maxSimulationTime, int uRate) {
//        Test.updateRate = uRate;
//        Test.visibilidad = gui;
//        Test.maxSimulationTime = maxSimulationTime;
//        String[] argsAux = new String[cromo.length()];
//        String[] args2 = new String[cromo.length()];
//        int inicio=0, numeros=0;
//        for(int i=0; i<cromo.length(); i++){
//            argsAux[i]=cromo.substring(i, i+1);
//            if(argsAux[i].contains(",")){
//                args2[numeros]=cromo.substring(inicio, i);
//                inicio=i+1;
//                numeros++;
//            }
//        }
//        args2[numeros]=cromo.substring(inicio, cromo.length());
//        numeros++;
//
//        String[] args = new String[numeros];
//        for(int j = 0; j < numeros; j++)
//            args[j]=args2[j];
//
//        return Test.main(args);
//    }
//
//    public static double main(String[] args, boolean gui) {
//        Test.visibilidad = gui;
//        return Test.main(args);
//    }

    public static void main(String[] args) {
       //double cromoDouble[] = {3.249298024954898, 4.135653584102353, 4.1513540012644805, 1.0, 1.0, 2.6658390187857073, 4.9662404789876655, 4.000493774578773, 0.32995753106063674, 2.4211479688942124, 2.5717063219831653, 2.1168381917788253, 0.1573403992158664, 2.999, 12.633716127020676, 0.0, 6.4162639110070625, 8.398481428361832, 12.065733841846003, 11.170619752845555, 13.999, 3.949603847665304, 4.289963392817382, 5.999, 5.999, 3.6357528325485005, 3.673283235801228, 2.259717806887455, 359.99, 281.8558936694033, 307.67341626032777, 359.99, 263.75254087500014, 39.178389659867705, 262.40953170085635};
       //double cromoDouble[] = {1.076065611958983, 3.1880225333924397, 2.0501032552965084, 1.0, 4.999, 4.8666159660064885, 1.780507137383247, 1.3400970051941574, 2.9129259398958274, 1.6513941197114241, 2.999, 7.037137988682949, 7.984912245424118, 0.0, 1.082737368082857, 1.1055104329580847, 0.0, 5.999, 5.2247653871683015, 5.999, 4.1114501241934995, 98.89067482952119, 210.99849736382004, 268.21594366480036, 301.82046250016924, 194.0873484551728, 22.43309155166582};
        
       //double cromoDouble[] = {4.999, 2.458894380765499, 4.13046785864533, 2.5489941221585686, 3.397342035504258, 2.7777251596017853, 4.999, 2.0, 4.000960156140461, 0.22577429132205662, 0.3980154976259977, 0.320186049918627, 0.0, 2.999, 0.9376974501415014, 1.6278706526524835, 2.0479216485527623, 5.181138348299048, 6.0, 10.6331668370157, 6.189930479015143, 9.630364028995135, 4.979296439235174, 4.953984154962901, 0.9489746601857899, 3.5168737636483214, 5.999, 5.999, 4.729021649521118, 4.971268087003696, 2.4486004961134746, 2.295045306199112, 0.7714902272984876, 25.9972231571567, 359.99, 182.75874835147252, 110.17882442287929, 199.12541085523162, 359.99, 359.99};
       //double cromoDouble[] = {0.0, 4.0, 3.0, 2.0, 4.0, 3.0, 2.0, 1.0, 4.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 8.0, 5.0, 1.0, 1.0, 5.0, 8.0, 0.0, 1.0, 2.0, 1.0, 3.0, 4.0, 1.0, 0.0, 241.44512555339338, 164.97322879915646, 68.54667850574329, 36.61070059312123, 277.04641812931595, 253.5366290571408, 293.7937247632845};

       double cromoDouble1[] = {0.0, 1.0, 2.0, 2.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 4.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 2.0, 3.0, 1.0, 7.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 3.0, 6.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 41.59208276981949, 26.056112323825115, 19.713746536864733, 157.04379798696527, 0.0, 0.0, 0.0, 0.0, 0.0};
       double cromoDouble2[] = {0.0, 1.0, 2.0, 2.0, 2.0, 0.0, 0.0, 0.0, 0.0, 0.0, 4.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 11.0, 3.0, 4.0, 2.0, 0.0, 0.0, 0.0, 0.0, 0.0, 6.0, 4.0, 0.0, 3.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 150.48283845549759, 133.91009663335504, 338.3910313775826, 136.37340467244627, 0.0, 0.0, 0.0, 0.0, 0.0};
       double cromoDouble3[] = {0.0, 4.0, 2.0, 4.0, 2.0, 0.0, 0.0, 0.0, 0.0, 0.0, 4.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 6.0, 10.0, 11.0, 5.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 10.872706478355937, 179.23952558768977, 211.00613559931668, 31.655998403916165, 0.0, 0.0, 0.0, 0.0, 0.0};

       //double cromoDouble[] = {0.0, 1.0, 2.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 2.0, 0.0, 2.0, 1.0, 0.0, 0.0, 0.0, 11.0, 7.0, 12.0, 4.0, 2.0, 2.0, 5.0, 1.0, 3.0, 3.0, 6.0, 3.0, 4.0, 0.0, 157.66709185847992, 15.88778201788573, 302.108841488441, 166.7109388293624, 279.31557624609184, 21.220792024399554, 241.31986247755916};
       //double cromoDouble[] = {0, 1, 2, 3, 4, 4, 1, 0, 0, 0, 3, 6, 9, 0, 0, 0, 0, 0, 270, 270, 0, 0};
       //double cromoDouble[] = {0.0, 4.0, 4.0, 4.0, 4.0, 4.0, 4.0, 4.0, 4.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 4.0, 0.0, 1.0, 1.0, 0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 7.0, 10.0, 4.0, 6.0, 0.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 138.44257561071728, 312.8342895577614, 144.27039938016867, 184.46924272036483, 55.40363137289369, 90.99007276443437, 146.34697392211825, 46.700888294049584, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
       double cromoDouble[] = new double[args.length];
        int cromosoma[];
        
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
        int cromosoma2[] = {2, 1, 2, 2, 2, 1, 2, 2, 1, 2, 2, 3, 1, 3, 3, 1, 3, 3, 3, 2, 8, 10, 6, 3, 4, 0, 0, 9, 0, 3, 3, 5, 5, 0, 5, 3, 3, 168, 65, 200, 81, 320, 315, 359, 334, 86, 359};
        //int cromosoma2[] = {0, 4, 3, 2, 2, 4, 2, 2, 4, 0, 0, 2, 1, 0, 0, 0, 2, 10, 7, 9, 2, 2, 0, 2, 4, 4, 0, 3, 4, 0, 37, 324, 175, 156, 353, 180, 101};
        
        int serpiente[]={4, 4, 4, 4, 4, 4, 4,
                          1, 1, 1, 1, 1, 1,
                          2, 2, 2, 2, 2, 2,
                          2, 2, 2 /*1*/, 2, 2, 2,
                          30, 30, 30, 30, 30, 30, 30};
                          //0, 90, 180, 270, 0, 90, 180};
        
        int goodTest1[]={0, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4,
                           4, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                           1, 4, 7, 10, 0, 0, 0, 0, 0, 0, 0, 0,
                           0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0,
                           0,   180, 180, 180, 0,   180, 180, 180, 180,   180, 180, 180, 180
        
        };

        double goodTest2[]={0, 1, 1, 1, 1, 2, 2, 2, 2,// 4, 4, 4, 4,
                           4, 1, 1, 1, 1, 1, 1, 1,// 1, 1, 1, 1,
                           1, 4, 7, 10,   12, 4, 12, 2,   //0, 0, 0, 0,
                           0, 5, 0, 5,    0, 0, 0, 0,   // 0, 0, 0, 0,
                           0,   0, 180, 180, 180,    0, 180, 0, 180,  // 180, 180, 180, 180

        };

        int demo[]={0, 2, 2,   2,0,   11,5,  0,0,     0, 180, 180};
        int demo2[]={0, 4, 1,   1,1,   0,0,  1,0,     0, 180, 180};

        int odeErrorbNormalized[]={0, 1, 4, 3, 2, 3, 1, 2, 4, 2, 1, 0, 0, 0, 0, 0, 4, 5, 6, 9, 7, 0, 3, 0, 4, 3, 4, 5, 1, 0, 357, 45, 191, 348, 193, 332, 154};
        
        int test[]={1, 1, 1, 1, 0, 0, 0};
        System.out.println("Clase Test");
        //Evaluador evaluador = new Evaluador(cromosoma);
        CoppeliaSimEvaluator evaluador = new CoppeliaSimEvaluator(goodTest2);
        evaluador.setGuiOn(Test.visibilidad);
        evaluador.setMaxSimulationTime(Test.maxSimulationTime);
        evaluador.setUpdateRate(updateRate);
        double calidad = evaluador.evaluate();
        System.out.println("Calidad = " + calidad);

        //return calidad;

        /*double[] posAct =evaluador.getPosicionActuadores();
        for(int i=0; i<evaluador.getPosicionActuadores().length; i++)
             System.out.print(" " + posAct[i]);
*/
        
    }

    public double getCalidad() {
        return calidad;
    }
}
