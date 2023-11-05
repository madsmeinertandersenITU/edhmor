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
package modules.jeaf.operation.grow;

import modules.jeaf.operation.MutationOperation;
import modules.individual.Connection;
import modules.individual.Node;

/**
 *
 * @author fai
 */
public abstract class GrowMutationOperation extends MutationOperation {

    protected void addNode(Node dadNode, Node newNode){

        //marcamos como activo el nuevo nodo.
        newNode.setIsOperationalActive(true);

        //añadirlo aleatoriamente a otro de los nodos del arbol
        dadNode.addChildren(newNode);

        
    }

    
        protected void addNode(Node dadNode, Node newNode, Connection conexion){

        //marcamos como activo el nuevo nodo.
        newNode.setIsOperationalActive(true);

        //añadirlo a otro de los nodos del arbol con la conexion dada
        dadNode.addChildren(newNode, conexion);


    }

}
