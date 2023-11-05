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
import modules.ModuleSetFactory;
import modules.util.SimulationConfiguration;
import org.apache.commons.math.util.ResizableDoubleArray;

/**
 *
 * @author fai
 */
public class Values2FeasibleValues {

    private double[] cromosoma;
    private int nModulesMax = 16;
    private double[] tipoModulo,  conexiones,  caraPadre,  orientacion,  amplitudeControl,  angularFreqControl,  phaseControl,  amplitudeModulation,  frequencyModulation;
    private int[] validTipoModulo,  validConexiones,  validCaraPadre,  validOrientacion;
    private double[] validAmplitudeControl,  validAngularFreqControl,  validPhaseControl,  validAmplitudeModulation,  validFrequencyModulation;

    public Values2FeasibleValues(double[] values) {

        if ((values.length + 3) % 9 == 0) {
            this.nModulesMax = (values.length + 3) / 9;
        } else {

            if ((values.length + 3) % 6 == 0) {
                this.nModulesMax = (values.length + 3) / 6;

            } else {
                if ((values.length + 3) % 5 == 0) {
                    this.nModulesMax = (values.length + 3) / 5;

                } else {
                    if (values.length % 5 == 0) {
                        //Depreciado
                        System.err.println("CreateRobot: Estas seguro de que esto va bien. Creo que esto esta depreciado. Long cromo: " + values.length + " cromosoma%5=0");

                        //Metamodulo base como unicio del arbol morfologico
                        this.nModulesMax = (values.length / 5) + 1;


                    } else {
                        System.err.println("Create Robot: Error con la longitud del cromosoma; length: " + values.length);
                        for (int i = 0; i < values.length; i++) {
                            System.err.print(values[i] + ", ");
                        }
                        System.exit(-1);
                    }

                }
                if (SimulationConfiguration.isDebug()) {
                    for (int i = 0; i < values.length; i++) {
                        System.out.print(values[i] + ", ");
                    }
                }
            }
        }
        this.tipoModulo = new double[nModulesMax];
        this.conexiones = new double[nModulesMax];
        this.caraPadre = new double[nModulesMax];
        this.orientacion = new double[nModulesMax];
        this.amplitudeControl = new double[nModulesMax];
        this.angularFreqControl = new double[nModulesMax];
        this.phaseControl = new double[nModulesMax];
        this.amplitudeModulation = new double[nModulesMax];
        this.frequencyModulation = new double[nModulesMax];

        this.validTipoModulo = new int[nModulesMax];
        this.validConexiones = new int[nModulesMax];
        this.validCaraPadre = new int[nModulesMax];
        this.validOrientacion = new int[nModulesMax];
        this.validAmplitudeControl = new double[nModulesMax];
        this.validAngularFreqControl = new double[nModulesMax];
        this.validPhaseControl = new double[nModulesMax];
        this.validAmplitudeModulation = new double[nModulesMax];
        this.validFrequencyModulation = new double[nModulesMax];

        for (int i = 0; i < nModulesMax; i++) {
            tipoModulo[i] = values[i];
        }

        for (int i = 0; i < (nModulesMax - 1); i++) {
            conexiones[i] = values[nModulesMax + i];
        }

        for (int i = 0; i < (nModulesMax - 1); i++) {
            caraPadre[i] = values[2 * nModulesMax - 1 + i];
        }

        for (int i = 0; i < (nModulesMax - 1); i++) {
            orientacion[i] = values[3 * nModulesMax - 2 + i];
        }

        if ((values.length + 3) % 9 == 0) {

            //Parametros del control de la amplitud
            for (int i = 0; i < (nModulesMax); i++) {
                this.amplitudeControl[i] = values[4 * nModulesMax - 3 + i];
            }

            //Parametros del control de la frecuencia angular
            for (int i = 0; i < (nModulesMax); i++) {
                this.angularFreqControl[i] = values[5 * nModulesMax - 3 + i];
            }

            //Parametros del control de la fase
            for (int i = 0; i < (nModulesMax); i++) {
                this.phaseControl[i] = values[6 * nModulesMax - 3 + i];
            }

            //Parametros del control del modulador de la amplitud
            for (int i = 0; i < (nModulesMax); i++) {
                this.amplitudeModulation[i] = values[7 * nModulesMax - 3 + i];
            }

            //Parametros del control del modulador de la amplitud
            for (int i = 0; i < (nModulesMax); i++) {
                this.frequencyModulation[i] = values[8 * nModulesMax - 3 + i];
            }


        }


    }

    public double[] feasibleValues() {


        convertControlParameters();

        normalizeTipoModulo();

        if(SimulationConfiguration.isFistModulesBase())
            this.validTipoModulo[0] = 0;

        List<Node> nodes = new ArrayList<Node>();
        Node rootNode = new Node(this.validTipoModulo[0], this.validAmplitudeControl[0],
                this.validAngularFreqControl[0], this.validPhaseControl[0],
                this.validAmplitudeModulation[0], this.validFrequencyModulation[0], null);
        nodes.add(rootNode);



        //generamos el arbol valido
        int iModuloGlobal=1;
        for (int modulo = 0; modulo < nodes.size() && nodes.size() < this.nModulesMax; modulo++) {
            Node dad = nodes.get(modulo);
            int tipoPadre = dad.getType();

            //comprobamos que el numero de hijos es posible
            int maxCaraPadre = ModuleSetFactory.getModulesSet().getModulesFacesNumber(tipoPadre);
            //Si no es un nodo raiz ya tiene ocupada una cara por la conexion con su padre
            if(dad.getDad()!=null)
                maxCaraPadre -= 1;
            int conexiones2Hijos = Math.min((int) Math.floor((maxCaraPadre + 1) * (this.conexiones[modulo] + 1) / 2), maxCaraPadre);

            while((nodes.size() + conexiones2Hijos) > this.nModulesMax){
                    conexiones2Hijos--;
            }
            if(conexiones2Hijos<0){
                    System.err.println("Imposible encontrar un numero de hijos valido: " + conexiones2Hijos);
                    System.exit(-1);
            }
            this.validConexiones[modulo] = conexiones2Hijos;
            for (int childrens = 0; childrens < this.validConexiones[modulo]; childrens++) {

                int tipoHijo = this.validTipoModulo[nodes.size()];
                //Ahora hay que ponerle el control que le corresponda
                //int control = this.control_modulo[nodes.size()];
                double phase = 0;
                double amplitude = 0.0, angFreq = 0.0, ampMod = 0.0, freqMod = 0.0;


                amplitude = this.validAmplitudeControl[iModuloGlobal];
                angFreq = this.validAngularFreqControl[iModuloGlobal];
                phase = this.validPhaseControl[iModuloGlobal];
                ampMod = this.validAmplitudeModulation[iModuloGlobal];
                freqMod = this.validFrequencyModulation[iModuloGlobal++];

                //comprobamos que la cara del padre es posible
                int caraPadreNode = Math.min( (int) Math.floor( (maxCaraPadre + 1) * (this.caraPadre[nodes.size() - 1] + 1) / 2), maxCaraPadre);
                while(!dad.isFaceFree(caraPadreNode)){
                    caraPadreNode++;
                    if(caraPadreNode>maxCaraPadre)
                        caraPadreNode=0;
                }

                int maxOrientations = ModuleSetFactory.getModulesSet().getModuleOrientations(tipoHijo);
                int orientacionHijo = Math.min( (int) Math.floor( (maxOrientations + 1) * (this.orientacion[nodes.size() - 1] + 1) / 2), maxOrientations);

                Node child = new Node(tipoHijo, amplitude, angFreq, phase, ampMod, freqMod, dad);
                Connection conexion = new Connection(dad, child, caraPadreNode, orientacionHijo);
                dad.addChildren(child, conexion);

                //SOLO PARA DEBUG
                //System.out.println(dad.toString());
                //System.out.println(child.toString());
                //System.out.println("\n");

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

        arbol.setRootNode(rootNode);

        arbol.modifyChromosome();

        


        return arbol.getChromosomeAt(0);
    }

    private void convertControlParameters() {

        double ampMax = SimulationConfiguration.getMaxAmplitudeControl();
        double ampMin = SimulationConfiguration.getMinAmplitudeControl();
        double freqMax = SimulationConfiguration.getMaxAngularFreqControl();
        double freqMin = SimulationConfiguration.getMinAngularFreqControl();
        double phaseMax = SimulationConfiguration.getMaxPhaseControl();
        double phaseMin = SimulationConfiguration.getMinPhaseControl();

        for (int i = 0; i < (nModulesMax); i++) {
            double tmp = normalize(this.amplitudeControl[i]);
            this.validAmplitudeControl[i] = tmp * (ampMax - ampMin) + ampMin;
        }
        for (int i = 0; i < (nModulesMax); i++) {
            double tmp = normalize(this.angularFreqControl[i]);
            this.validAngularFreqControl[i] = tmp * (freqMax - freqMin) + freqMin;
        }
        for (int i = 0; i < (nModulesMax); i++) {
            double tmp = normalize(this.phaseControl[i]);
            this.validPhaseControl[i] = tmp * (phaseMax - phaseMin) + phaseMin;
        }

    }

    private void normalizeTipoModulo(){
        int tipoModuloMax = SimulationConfiguration.getMaxTypeModules();
        int tipoModuloMin = SimulationConfiguration.getMinTypeModules();

        for (int i = 0; i < (nModulesMax); i++) {
            double tmp = normalize(this.tipoModulo[i]);
            this.validTipoModulo[i] = Math.min( (int) Math.floor( tmp * (tipoModuloMax - tipoModuloMin + 1) + tipoModuloMin), tipoModuloMin);
        }

    }

    private double normalize(double d){
        //Devuelve un valor entre 0 y 1 de los genes en vex de entre -1 y 1
        return ((d+1)/2);
    }
}
