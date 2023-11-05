package modules;

public class Emerge18AndCuboidBaseModuleSet extends EmergeAndCuboidBaseModuleSet {
	
	public Emerge18AndCuboidBaseModuleSet() {
	    //8 types of module with five connection faces and 2 orientations
	    this(8, 5, 2); 
		}
		
		public Emerge18AndCuboidBaseModuleSet(int modulesTypeNumber, int maxFaceNumberInOneModule, int maxOrientations) {
			// 8 types of module with 5 connection faces and 2 orientations
			super(modulesTypeNumber, maxFaceNumberInOneModule, maxOrientations);
			// The parent class sets all the properties of the normal module. Here, we only
			// update the
			// base module to use a cuboid base and add the extra base length modules.
			
			moduleName[1] = "emergeModuleAX18";
			moduleName[2] = "1-25TimesLength/emergeModule18";
			moduleName[3] = "1-5TimesLength/emergeModule18";
			moduleName[4] = "1-75TimesLength/emergeModule18";
			moduleName[5] = "2TimesLength/emergeModule18";
			moduleName[6] = "4TimesLength/emergeModule18";
			moduleName[7] = "8TimesLength/emergeModule18";
		}


}
