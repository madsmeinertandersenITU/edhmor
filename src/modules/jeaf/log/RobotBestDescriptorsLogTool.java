package modules.jeaf.log;

import es.udc.gii.common.eaf.algorithm.EvolutionaryAlgorithm;
import es.udc.gii.common.eaf.algorithm.population.Individual;
import es.udc.gii.common.eaf.algorithm.productTrader.IndividualsProductTrader;
import es.udc.gii.common.eaf.algorithm.productTrader.specification.BestIndividualSpecification;
import es.udc.gii.common.eaf.log.LogTool;

import java.util.List;
import java.util.Observable;
import modules.evaluation.CalculateModulePositions;
import modules.evaluation.staticFeatures.SymmetryFeature;
import modules.evaluation.staticFeatures.XSymmetryEvaluator;
import modules.evaluation.staticFeatures.YSymmetryEvaluator;
import modules.individual.TreeIndividual;
import modules.jeaf.application.edhmor.RoboticObjetiveFunction;

/**
 *
 * @author fai
 */
public class RobotBestDescriptorsLogTool extends LogTool{
    
    @Override
    public String getLogID() {
        return "RobotBestDescriptorsLogTool";
    }
    
    @Override
    public void update(Observable o, Object arg) {

    	EvolutionaryAlgorithm algorithm = (EvolutionaryAlgorithm) o;
        List<Individual> individuals;
        super.update(o, arg);

        if (algorithm.getState() == EvolutionaryAlgorithm.REPLACE_STATE && arg == null) {

            BestIndividualSpecification bestSpec =
                    new BestIndividualSpecification();
            Individual best;
            if (algorithm.getState() == EvolutionaryAlgorithm.REPLACE_STATE && arg == null) {
                best = IndividualsProductTrader.get(bestSpec,
                        algorithm.getPopulation().getIndividuals(), 1, algorithm.getComparator()).get(0);

                TreeIndividual bestTree = (TreeIndividual) best;
                CalculateModulePositions features = new CalculateModulePositions(bestTree.getChromosomeAt(0));
				/*
				 * String typePercStr = ""; double[] typePercentage =
				 * features.getTypePercentage(); for (int i = 0; i < typePercentage.length; i++)
				 * { typePercStr += typePercentage[i]; typePercStr += " - "; }
				 */
                
                //Symmetry
                SymmetryFeature xSymmetryFeature = new XSymmetryEvaluator();
                SymmetryFeature ySymmetryFeature = new YSymmetryEvaluator();
      	        double xSymmetryValue, ySymmetryValue;
      	        ySymmetryValue = ySymmetryFeature.getSymmetryMeasurement(bestTree);
                xSymmetryValue = xSymmetryFeature.getSymmetryMeasurement(bestTree);
      	        
      	        
                super.getLog().println(
                        algorithm.getGenerations() + " - " +
                        best.getFitness() + " - " +
                        + features.getnModules() + " - "
                        + features.getLimbDescriptor()+ " - "
                        + features.getCoverage() + " - "
                        + features.getXYProportion() + " - "
                        + features.getZProportion() + " - "
                        + xSymmetryValue + " - "
                        + ySymmetryValue + " - "
                        + best.getObjectives().get(RoboticObjetiveFunction.DFEATURES_BALANCE)
                        );
            }
        }
    }
    
}