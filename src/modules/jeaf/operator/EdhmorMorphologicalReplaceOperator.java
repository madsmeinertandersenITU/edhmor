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
package modules.jeaf.operator;

import es.udc.gii.common.eaf.algorithm.EvolutionaryAlgorithm;
import es.udc.gii.common.eaf.algorithm.operator.replace.ReplaceOperator;
import es.udc.gii.common.eaf.algorithm.population.Individual;
import es.udc.gii.common.eaf.algorithm.productTrader.IndividualsProductTrader;
import es.udc.gii.common.eaf.algorithm.productTrader.specification.BestIndividualSpecification;
import es.udc.gii.common.eaf.algorithm.productTrader.specification.WorstIndividualSpecification;
import es.udc.gii.common.eaf.util.EAFRandom;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import modules.jeaf.comparator.OrdenPoblacionalComparator;
import modules.jeaf.operation.MutationOperation;
import modules.jeaf.operation.grow.GrowMutationOperation;
import modules.jeaf.operation.grow.symmetry.SymmetryOperator;
import modules.individual.TreeIndividual;
import modules.jeaf.stoptest.HwEvalWallTime;
import modules.util.SimulationConfiguration;
import org.apache.commons.configuration.Configuration;

/**
 *
 * @author fai
 */
public class EdhmorMorphologicalReplaceOperator extends ReplaceOperator {

    private boolean fakeReparar = false;
    private int elitism = 1;
    private int applyEveryXGenerations = 7;
    private int nOperations = 1;
    private String strSymmetry;
    private List<String> operationStr = new ArrayList<String>();
    FileOutputStream archivo;
    PrintStream printDebug = null;

    public EdhmorMorphologicalReplaceOperator() {
        super();
        /*try {
            archivo = new FileOutputStream("morphologicalReplaceOperator_debug", true);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(EdhmorMorphologicalReplaceOperator.class.getName()).log(Level.SEVERE, null, ex);
        }
        printDebug = new PrintStream(archivo);
         */
    }

    @Override
    protected List<Individual> replace(EvolutionaryAlgorithm algorithm, List<Individual> toPopulation) {
        //Los hijos están en las lista "toPopulation" y los padres originales en
        //"algorithm.getPopulation", se copia siempre el hijo y se llama a reparar
        //de esta manera segun lo especificado en la operacion efectuada se copia
        //el padre (el hijo guarda una copia de este con marcas de si esta activo)
        // o el hijo

        //Reparamos
        for (Individual ind : toPopulation) {
            TreeIndividual arbol = (TreeIndividual) ind;
            MutationOperation op = arbol.getOperation();
            if (op != null) {
                if (fakeReparar) {
                    op.fakeRepair(arbol);
                } else {
                    op.repair(arbol);
                }
            }
        }

        //Cojo al mejor de esta generacion:
        List<Individual> theBetterList;
        theBetterList = IndividualsProductTrader.get(
                new BestIndividualSpecification(),
                toPopulation, 1, algorithm.getComparator());

        //Al mejor lo marcamos como protected
        TreeIndividual theBest = (TreeIndividual) theBetterList.get(0);
        theBest.setIsProtected(true);

        //printDebug.println("\n\nIteracion " + algorithm.getGenerations() + ":");
        //printDebug.println("Mejor Individuo: ");
        //printDebug.println(theBest.detailedToString());
        if (algorithm.getGenerations() == 0) {
            int orden = 0;
            for (Individual ind : toPopulation) {
                TreeIndividual arbol = (TreeIndividual) ind;
                arbol.setPopullationOrder(orden);
                orden++;
            }
        }

        if (((algorithm.getGenerations() + 1) % applyEveryXGenerations) == 0) {

            //printDebug.println("\n\nVamos a plicar operaciones de simetria y estabilidad!!\n\n");
            //Quitamos las marcas de proteccion a todos
            for (Individual ind : toPopulation) {
                TreeIndividual arbol = (TreeIndividual) ind;
                arbol.setIsProtected(false);
            }

            //Cojo los mejores de esta generacion:
            List<Individual> betterList;
            List<Integer> ordenPobEliminados = new ArrayList<Integer>();
            betterList = IndividualsProductTrader.get(
                    new BestIndividualSpecification(),
                    toPopulation, this.elitism, algorithm.getComparator());

            //Marcamos como protegido al mejor de esta generacion
            TreeIndividual theBest2 = (TreeIndividual) betterList.get(0);
            theBest2.setIsProtected(true);

            //Solo cogemos la mitad de los mejores individuo el resto lo cogemos aleatoriamente
            for (int i = 0; i < this.elitism / 2; i++) {
                int a = EAFRandom.nextInt(algorithm.getPopulation().getSize());
                Individual treeToDoSymm = (Individual) toPopulation.get(a);
                betterList.set(this.elitism / 2 + i, treeToDoSymm);
            }
            //Borro los peores de esta generacion:
            List<Individual> worstList = IndividualsProductTrader.get(
                    new WorstIndividualSpecification(),
                    toPopulation, this.elitism, algorithm.getComparator());

            for (int i = 0; i < worstList.size(); i++) {
                Individual ind = worstList.get(i);
                TreeIndividual tree = (TreeIndividual) ind;
                ordenPobEliminados.add(tree.getPopullationOrder());
                toPopulation.remove(ind);
            }

            //Generamos los individuos modificados
            List<Individual> indVariations = new ArrayList<Individual>();
            for (int i = 0; i < betterList.size(); i++) {
                TreeIndividual arbol = (TreeIndividual) betterList.get(i).clone();

                //Lo modificamos
                GrowMutationOperation symOrStaOp;
                try {

                    symOrStaOp = (GrowMutationOperation) Class.forName(this.SeleccionOperacion()).newInstance();
                    arbol.setOperation(symOrStaOp);
                    //printDebug.println("Vamos a ejecutar la operacion:");
                    symOrStaOp.run(arbol);
                    //printDebug.println("Operacion ejecutada");

                    if (symOrStaOp.isWorking()) {
                        arbol.setIsProtected(true);
                    } else {
                        arbol.generate();
                    }

                    arbol.setPopullationOrder(ordenPobEliminados.get(i));

                } catch (Exception ex) {
                    Logger.getLogger(EdhmorMorphologicalReplaceOperator.class.getName()).log(Level.SEVERE, null, ex);
                    ex.printStackTrace();
                    System.exit(-1);

                }

                //y lo añadimos
                indVariations.add(arbol);
            }

            /* Evaluate all individuals in the same step. */
            algorithm.getEvaluationStrategy().evaluate(algorithm, indVariations,
                    algorithm.getProblem().getObjectiveFunctions(),
                    algorithm.getProblem().getConstraints());

            algorithm.setFEs(algorithm.getFEs() + indVariations.size());

            //Los reparamos: basicamente quitamos los isActiveContribution
            for (int i = 0; i < indVariations.size(); i++) {
                TreeIndividual arbolAux = (TreeIndividual) indVariations.get(i);
                MutationOperation op = arbolAux.getOperation();
                if (op != null) {
                    if (fakeReparar) {
                        op.fakeRepair(arbolAux);
                    } else {
                        op.repair(arbolAux);
                    }
                }
            }

            toPopulation.addAll(indVariations);

            //Ordenamos la lista por orden poblacional
            Collections.sort(toPopulation, new OrdenPoblacionalComparator());

            //Increase the wall time because of the assembly time
            for (Individual ind : toPopulation) {
                TreeIndividual tree = (TreeIndividual) ind;
                //x2 as we need to disassemble them and assemble the new morphologies 
                double modules = tree.getListNode().size() * 2; 
                double time = SimulationConfiguration.getAssemblyTimePerModule() * modules;
                HwEvalWallTime.increaseElapsedWallTime(time);
            }
        }

        return toPopulation;
    }

    private String SeleccionOperacion() {
        double suerte = EAFRandom.nextDouble();
        //printDebug.println("Seleccionamos la operacion:");
        if (this.nOperations > 1) {
            int i = 0;
            while (suerte > ((1 / (double) this.nOperations) * (i + 1))) {
                i++;
            }
            //printDebug.println("Operacion: " + i + " es la seleccionada");
            return this.operationStr.get(i);
        } else {
            //printDebug.println("Operacion: " + 0 + " es la seleccionada");
            return this.operationStr.get(0);
        }

    }

    @Override
    public void configure(Configuration conf) {
        super.configure(conf);
        if (conf.containsKey("fakeReparar")) {
            this.fakeReparar = true;
        }
        this.elitism = conf.getInt("Elitism");
        this.applyEveryXGenerations = conf.getInt("ApplyEveryXGenerations");
        strSymmetry = conf.getString("Symmetry");
        this.nOperations = conf.getInt("NOperations");
        for (int i = 0; i < nOperations; i++) {
            try {
                operationStr.add(conf.getString("Operation" + i));
                if (operationStr.get(i).isEmpty() || operationStr.get(i) == null || operationStr.get(i).equals("")) {
                    throw new Exception("Error cargando los achivos de mundo base: \n");
                }
            } catch (Exception ex) {
                Logger.getLogger(EdhmorMorphologicalReplaceOperator.class.getName()).log(Level.SEVERE, null, ex);
                //error cargando los parametros del control de la simulacion
                System.err.println("Error cargando los parametros del operador de reemplazo morfologico.");
                System.out.println(ex);
                System.exit(-1);
            }
        }

    }
}
