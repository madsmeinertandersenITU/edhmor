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
package modules.jeaf.application.de;

import java.util.List;
import modules.jeaf.application.edhmor.RoboticObjetiveFunction;
import modules.individual.Values2FeasibleValues;

/**
 *
 * @author fai
 */
public class DEWalkObjetiveFunction extends RoboticObjetiveFunction{
    
    @Override public List evaluate(double[] values) {
        
        //The values of the chromosome are between -1 and 1. We have to 
        //transform them to integers and we have to check that the morphology is
        //feasibe (we could have more childrens than connection faces, etc.)
        Values2FeasibleValues robot = new Values2FeasibleValues(values);

        return super.evaluate(robot.feasibleValues());

}
}


