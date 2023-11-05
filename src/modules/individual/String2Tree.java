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

import java.util.ArrayList;
import java.util.List;
import modules.util.ChromoConversion;
import modules.util.SimulationConfiguration;
import org.apache.commons.math.util.ResizableDoubleArray;

/**
 *
 * @author fai
 */
public class String2Tree {

    private final int[] cromosomaInt;
    private final double[] cromosomaDouble;
    private int nModulesMax;
    private int nModules = 1;
    private int tipoModulo[];
    private int conexiones[];
    private int cara_padre[];
    private int cara_hijo[];
    private int phaseControl[];
    private double amplitudeControl[];
    private double angularFreqControl[];
    private double frequencyModulator[];
    private double amplitudeModulator[];
    private int nBase = 0;
    private int nSlider = 0;
    private int nTelescope = 0;
    private int nHingeAxial = 0;
    private int nHinge = 0;
    private int nEffector = 0;


    public String2Tree(String cromo) {

        cromosomaInt = ChromoConversion.str2int(cromo);
        cromosomaDouble = ChromoConversion.str2double(cromo);
        this.analisisCormosoma();
    }

    private void analisisCormosoma() {


        if ((cromosomaInt.length + 3) % 9 == 0) {
            this.nModulesMax = (cromosomaInt.length + 3) / 9;

        } else {

            if ((cromosomaInt.length + 3) % 6 == 0) {
                //System.out.println("String2Tree: cromosomaInt.length + 3) % 6 == 0");               
                this.nModulesMax = (cromosomaInt.length + 3) / 6;

            } else {
                if ((cromosomaInt.length + 3) % 5 == 0) {
                    //System.out.println("String2Tree: cromosomaInt.length + 3) % 5 == 0");                    
                    this.nModulesMax = (cromosomaInt.length + 3) / 5;
                  
                    System.err.println("String2Tree: Estas seguro de que la longitud del cromosoma es correcta??!!!; lenght: " + cromosomaInt.length);

                } else {


                    System.err.println("String2Tree: Error en la longitud del cromosoma; lenght: " + cromosomaInt.length);
                    for (int i = 0; i < cromosomaInt.length; i++) {
                        System.err.println(cromosomaInt[i] + " ");
                    }
                    System.exit(-1);
                }
            }

        }

        this.tipoModulo = new int[nModulesMax];
        this.conexiones = new int[nModulesMax - 1];
        this.cara_padre = new int[nModulesMax - 1];
        this.cara_hijo = new int[nModulesMax - 1];

        this.amplitudeControl = new double[nModulesMax];
        this.angularFreqControl = new double[nModulesMax];
        this.phaseControl = new int[nModulesMax];
        this.frequencyModulator = new double[nModulesMax];
        this.amplitudeModulator = new double[nModulesMax];

        //System.out.println("String2Tree: nModulesMax="+nModulesMax);
        for (int i = 0; i < nModulesMax; i++) {
            tipoModulo[i] = cromosomaInt[i];
//            switch (tipoModulo[i]) {
//                case 0:
//                    //solo contamos a la base si es el módulo inicial ya que no puede haber mas de dos bases
//                    if (i == 0) {
//                        nBase++;
//                    }
//                    break;
//                case 1:
//                    nSlider++;
//                    break;
//                case 2:
//                    nTelescope++;
//                    break;
//                case 3:
//                    nHingeAxial++;
//                    break;
//                case 4:
//                    nHinge++;
//                    break;
//                case 5:
//                    nEffector++;
//                    break;
//                case 6:
//                    /*Snake*/
//                    System.err.println("No esta implemenmtado el uso de modulos snake");
//                    System.exit(-1);
//                    break;
//            }
        }
        //Conexiones
        for (int i = 0; i < (nModulesMax - 1); i++) {
            conexiones[i] = cromosomaInt[nModulesMax + i];
            nModules += conexiones[i];
        }

        //Cara del padre
        for (int i = 0; i < (nModulesMax - 1); i++) {
            cara_padre[i] = cromosomaInt[2 * nModulesMax - 1 + i];
        }

        //Orientacion del modulo
        for (int i = 0; i < (nModulesMax - 1); i++) {
            cara_hijo[i] = cromosomaInt[3 * nModulesMax - 2 + i];
        }

        //Parametro de control: amplitud
        if ((cromosomaInt.length + 3) % 9 == 0) {
            for (int i = 0; i < (nModulesMax); i++) {
                amplitudeControl[i] = cromosomaDouble[4 * nModulesMax - 3 + i];
            }
        } else {
            //Suponemos que (cromosomaInt.length + 3) % 6 == 0
            for (int i = 0; i < (nModulesMax); i++) {
                this.amplitudeControl[i] = 0.0;
            }
        }

        //Parametro de control: frequencia angular
        if ((cromosomaInt.length + 3) % 9 == 0) {
            for (int i = 0; i < (nModulesMax); i++) {
                this.angularFreqControl[i] = cromosomaDouble[5 * nModulesMax - 3 + i];
            }
        } else {
            //Suponemos que (cromosomaInt.length + 3) % 6 == 0
            for (int i = 0; i < (nModulesMax); i++) {
                this.angularFreqControl[i] = 0.0;
            }
        }

        //Parametro de control: fase
        if ((cromosomaInt.length + 3) % 9 == 0) {
            for (int i = 0; i < (nModulesMax); i++) {
                phaseControl[i] = cromosomaInt[6 * nModulesMax - 3 + i];
            }
        } else {
            //Suponemos que (cromosomaInt.length + 3) % 6 == 0
            for (int i = 0; i < (nModulesMax); i++) {
                phaseControl[i] = cromosomaInt[4 * nModulesMax - 3 + i];
            }
        }

        //Parametro de control: modulador de la amplitud
        if ((cromosomaInt.length + 3) % 9 == 0) {
            for (int i = 0; i < (nModulesMax); i++) {
                this.amplitudeModulator[i] = cromosomaDouble[7 * nModulesMax - 3 + i];
            }
        } else {
            //Suponemos que (cromosomaInt.length + 3) % 6 == 0
            for (int i = 0; i < (nModulesMax); i++) {
                this.amplitudeModulator[i] = 0.0;
            }
        }

        if ((cromosomaInt.length + 3) % 9 == 0) {
            for (int i = 0; i < (nModulesMax); i++) {
                frequencyModulator[i] = cromosomaDouble[8 * nModulesMax - 3 + i];
            }
        } else {
            if ((cromosomaInt.length + 3) % 6 == 0) {
                for (int i = 0; i < (nModulesMax); i++) {
                    frequencyModulator[i] = cromosomaDouble[5 * nModulesMax - 3 + i];
                }
            } else {
                //Suponemos que (cromosomaInt.length + 3) % 5 == 0
                for (int i = 0; i < (nModulesMax); i++) {
                    frequencyModulator[i] = 0.0;
                }
            }
        }

//        if (nModules != (nBase + nSlider + nTelescope + nHingeAxial + nHinge + nEffector)) {
//            System.err.println("Error con el numero de módulos:");
//            System.err.println("nModules: " + nModules + ", base:" + nBase + ", nSlider:" + nSlider + ", nTelescope" + nTelescope);
//            System.err.println("nHingeAxial: " + nHingeAxial + ", nHinge" + nHinge + ", nEffector: " + nEffector);
//            System.exit(-1);
//        }


    }

    public TreeIndividual toTree() {
        List<Node> nodes = new ArrayList<Node>();
        Node rootNode = new Node(this.tipoModulo[0], this.amplitudeControl[0],
                this.angularFreqControl[0], this.phaseControl[0], this.amplitudeModulator[0], this.frequencyModulator[0], null);
        nodes.add(rootNode);
        //indices de modulos
        int iS = 0, iT = 0, iHA = 0, iH = 0, iE = 0;
        int iModuloGlobal=1;
        for (int modulo = 0; modulo < nodes.size() && nodes.size() < this.nModulesMax; modulo++) {
            Node dad = nodes.get(modulo);
            for (int childrens = 0; childrens < this.conexiones[modulo]; childrens++) {

                int tipo = this.tipoModulo[nodes.size()];
                //Ahora hay que ponerle el control que le corresponda
                //int control = this.control_modulo[nodes.size()];
                int phase = 0;
                double amplitude = 0.0, angFreq = 0.0, ampMod = 0.0, freqMod = 0.0;
                

                amplitude = this.amplitudeControl[iModuloGlobal];
                angFreq = this.angularFreqControl[iModuloGlobal];
                phase = this.phaseControl[iModuloGlobal];
                ampMod = this.amplitudeModulator[iModuloGlobal];
                freqMod = this.frequencyModulator[iModuloGlobal++];

                int caraPadre = this.cara_padre[nodes.size() - 1];
                int orientacionHijo = this.cara_hijo[nodes.size() - 1];
                Node child = new Node(tipo, amplitude, angFreq, phase, ampMod, freqMod, dad);
                Connection conexion = new Connection(dad, child, caraPadre, orientacionHijo);
                dad.addChildren(child, conexion);

                //SOLO PARA DEBUG
//                System.out.println(dad.toString());
//                System.out.println(child.toString());
//                System.out.println("\n");

                nodes.add(child);
            }

        }


        TreeIndividual arbol = new TreeIndividual();
        ResizableDoubleArray[] dobles = new ResizableDoubleArray[1];
        dobles[0] = new ResizableDoubleArray(this.nModulesMax * 9 - 3);

        for (int i = 0; i < (this.nModulesMax * 9 - 3); i++) {
            dobles[0].setElement(i, 0.0);
        }

        arbol.setChromosomes(dobles);
        SimulationConfiguration.setMaxModules(this.nModulesMax);
        arbol.setRootNode(rootNode);

        arbol.modifyChromosome();

        return arbol;
    }
}
