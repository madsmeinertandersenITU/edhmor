package modules;

public class Emerge18ModuleSet extends EmergeModuleSet {
	
	/*Constructor for the EmergeModuleSet class*/
	   public Emerge18ModuleSet() {
	       //two types of module with four connection faces and 2 orientations
	       this(2, 4, 2); 
	   }
	   
	   public Emerge18ModuleSet(int modulesTypeNumber, int maxFaceNumberInOneModule, int maxOrientations) {

			super(modulesTypeNumber, maxFaceNumberInOneModule, maxOrientations);

			// The name of the different modules (they have to match with the name of
			// the file to load in the simulator)
			moduleName[0] = "emergeModule18";
		    moduleName[1] = "emergeModule18";
	   }

}
