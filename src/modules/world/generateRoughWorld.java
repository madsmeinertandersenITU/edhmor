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
package modules.world;

import es.udc.gii.common.eaf.exception.ConfigurationException;
import es.udc.gii.common.eaf.util.EAFRandom;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.configuration.XMLConfiguration;

/**
 *
 * @author fai
 */
public class generateRoughWorld {



    public static void main(String[] args){
    EAFRandom.init();

    String worldbase = "baseEstandar.world";
    File file = new File("./rugoso.world");
    file.delete();
    int nObstaculos = 50;
    double maxDimensionesBoxXY = 0.4,  minDimensionesBoxXY = 0.1, maxDimensionesBoxZ = 0.10, minDimensionesBoxZ = 0.03;
    double maxZoneX = 8, minZoneX=-2, maxZoneY=2.5, minZoneY=-2.5;

        try {

            XMLConfiguration config;

            config = new XMLConfiguration("./" + "worlds/" + worldbase);

            config.addProperty("PropiedadesMundoRugoso" + "(-1)[@name]", "prop");
            config.addProperty("PropiedadesMundoRugoso.maxDimensionesBoxXY" + "(-1)", maxDimensionesBoxXY);
            config.addProperty("PropiedadesMundoRugoso.minDimensionesBoxXY" + "(-1)", minDimensionesBoxXY);
            config.addProperty("PropiedadesMundoRugoso.maxDimensionesBoxZ" + "(-1)", maxDimensionesBoxZ);
            config.addProperty("PropiedadesMundoRugoso.minDimensionesBoxZ" + "(-1)", minDimensionesBoxZ);
            config.addProperty("PropiedadesMundoRugoso.maxZoneX" + "(-1)", maxZoneX);
            config.addProperty("PropiedadesMundoRugoso.minZoneX" + "(-1)", minZoneX);
            config.addProperty("PropiedadesMundoRugoso.maxZoneY" + "(-1)", maxZoneY);
            config.addProperty("PropiedadesMundoRugoso.minZoneY" + "(-1)", minZoneY);
            config.addProperty("PropiedadesMundoRugoso.nObstaculos" + "(-1)", nObstaculos);

            config.setProperty("physics:ode.stepTime", 0.01);
            config.setProperty("physics:ode.cfm", 0.01);
            config.setProperty("physics:ode.erp", 0.5);

            double x, y, z;

            //Bucle para cada modulo para añadir los xyz y el rpy
            for (int m = 0; m < nObstaculos; m++) {

                //Creamos el model:physical
                String modelName = "model:physical";
                
                config.addProperty(modelName + "(-1)[@name]", "obstaculo" + m);
                
                x = EAFRandom.nextDouble()*(maxZoneX -minZoneX) + minZoneX;
                y = EAFRandom.nextDouble()*(maxZoneY -minZoneY) + minZoneY;
                
                //Altura del obstaculo, necesria saberla para situar la caja corectamente
                z = EAFRandom.nextDouble()*(maxDimensionesBoxZ - minDimensionesBoxZ) + minDimensionesBoxZ;

                config.addProperty(modelName + ".xyz" + "(-1)", x + " " + y + " " + z/2);
                config.addProperty(modelName + ".rpy" + "(-1)", "0 0 0");
                config.addProperty(modelName + ".static" + "(-1)", "true");

                modelName += ".body:box";
                config.addProperty(modelName + "(-1)[@name]", "body_obstaculo" + m);

                modelName +=".geom:box";
                config.addProperty(modelName + "(-1)[@name]", "geom_obstaculo" + m);
                config.addProperty(modelName + ".xyz" + "(-1)", "0 0 0");
                config.addProperty(modelName + ".rpy" + "(-1)", "0 0 0");
                x = EAFRandom.nextDouble()*(maxDimensionesBoxXY-minDimensionesBoxXY) + minDimensionesBoxXY;
                y = EAFRandom.nextDouble()*(maxDimensionesBoxXY-minDimensionesBoxXY) + minDimensionesBoxXY;
                config.addProperty(modelName + ".size" + "(-1)", x + " " + y + " " +z);
                config.addProperty(modelName + ".mass" + "(-1)", 1);

                modelName +=".visual";
                //config.addProperty(modelName + "(-1)", "body_obstaculo" + m);
                config.addProperty(modelName + ".mesh" + "(-1)", "unit_box");
                config.addProperty(modelName + ".size" + "(-1)", x + " " + y + " " +z);
                String material = "Gazebo/test";
                switch(m%2){
                    case 0:  material = "Gazebo/test"; break;
                    case 1:  material = "Gazebo/Grey"; break;
                }
                config.addProperty(modelName + ".material" + "(-1)", material);
            }


/*
            for (int m = 0; m < nModules; m++) {
                String modelName = "model:physical";
                for (int i = 0; i < nivel[m]; i++) {
                    modelName += ".model:physical";
                    modelName += "(" + indice_hijo[treeMatrix[i + 1][m]] + ")";
                }
                if (m != 0) {
                    //El modulo cero no tiene padre y por tanto no se une a nadie
                    config.addProperty(modelName + ".attach(-1).parentBody", prop_enlace[m][2]);
                    config.addProperty(modelName + ".attach.myBody", prop_enlace[m][3]);
                }
                config.addProperty(modelName + ".include" + "(-1)[@embedded]", "true");
                config.addProperty(modelName + ".include.xi:include" + "(-1)[@href]", "models/" + getNameModulo(m) + "/" + getNameModuloNumber(m) + ".model");
                actualiza_indices_modulos(m);
            }
  */

            try {
                config.save(file);
            } catch (ConfigurationException cex) {
                System.err.println(cex);
            }
        } catch (org.apache.commons.configuration.ConfigurationException ex) {
            Logger.getLogger(generateRoughWorld.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ConfigurationException cex) {
            System.out.println(cex);
        }


    }
}
