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
package modules.jeaf.comparator;

import es.udc.gii.common.eaf.algorithm.population.Individual;
import es.udc.gii.common.eaf.config.Configurable;
import java.util.Comparator;
import modules.individual.TreeIndividual;
import org.apache.commons.configuration.Configuration;

/**
 *
 * @author fai
 */
public class OrdenPoblacionalComparator <T extends TreeIndividual> implements Comparator<T>{

    public int compare(T arg0, T arg1) {
        if(arg0.getPopullationOrder() < arg1.getPopullationOrder())
            return -1;
        else
            if(arg0.getPopullationOrder() > arg1.getPopullationOrder())
                return 1;
            else
                return 0;
    }



}
