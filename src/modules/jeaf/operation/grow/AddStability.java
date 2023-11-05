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

import es.udc.gii.common.eaf.util.EAFRandom;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;
import modules.ModuleSetFactory;
import modules.individual.Connection;
import modules.individual.Node;
import modules.individual.TreeIndividual;
import modules.util.DepreciatedModuleRotation;
import modules.util.SimulationConfiguration;
import modules.util.exceptions.InconsistentDataException;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 *
 * @author fai
 */
public class AddStability extends GrowMutationOperation {

    //array de listas de vectores de las caras para cada módulo
    private List<ModuleFaces> carasModulos = new ArrayList<ModuleFaces>();
    private List<Vector3D> carasApoyo = new ArrayList<Vector3D>();
    private Vector3d min_pos = new Vector3d(),  max_pos = new Vector3d(),  dimensiones = new Vector3d();
    private Vector3d cdm = new Vector3d();
    private double masaTotal = 0;
    private List<ModuleFaces> faceToAddTelescope = new ArrayList<ModuleFaces>();
    private List<ModuleFaces> faceApoyo = new ArrayList<ModuleFaces>();
    private int nModulosApoyo = 0;
    private int nModulosEstable = 6;
    private int nModulesToAdd = 0;
    private int nModulesAdded = 0;

    FileOutputStream archivo;
    PrintStream printDebug = null;

    public AddStability() {
        try {
            archivo = new FileOutputStream("addStability_debug", true);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(AddStability.class.getName()).log(Level.SEVERE, null, ex);
        }
        printDebug = new PrintStream(archivo);
    }



    @Override
    public void run(TreeIndividual arbol) {


        //Hay que poner el arbol como "padre" antes de modificarlo
        this.setFatherIndividual(arbol);

        this.analisisEstabilidad(arbol);

        this.calculoPosiblesCarasApoyo();

        //Calculamos cuantos modulos estan actuando como apoyos
        this.nModulosApoyo = 0;
        int[] isApoyo = new int[16];
        for (ModuleFaces caras : this.faceApoyo) {
            isApoyo[caras.nod.getConstructionOrder()] = 1;
        }
        for (int i = 0; i < isApoyo.length; i++) {
            this.nModulosApoyo += isApoyo[i];
        }

        nModulesToAdd = nModulosEstable - nModulosApoyo;
        if(nModulesToAdd < 0)
            nModulesToAdd = 2;

        this.showDebugInformation();

        this.setIsWorking(false);

        for (int n = 0; n < nModulesToAdd; n++) {

            //TODO: Por ahora siempre ponemos modos telescopicos hacia abajo
            //TODO: Elegimos uno aleatoriamente (Hacerlo con algo de logica)
            int nModulos = arbol.getRootNode().getNumberModulesBranch();
            int nModulosMax = SimulationConfiguration.getMaxModules();
            if (this.faceToAddTelescope.size() > 0 && nModulos < nModulosMax) {
                
                //Calculamos el apoyo más conveniente intentando maximizar la
                //distancia entre el posible apoyo y los apoyos existentes
                //[OLD] int aleatorio = EAFRandom.nextInt(this.faceToAddTelescope.size());
                int mejorApoyo = calculoMejorApoyo();

                Node dadNode = this.faceToAddTelescope.get(mejorApoyo).nod;

                Node newNode = new Node(2, dadNode);
                int caraPadre = this.faceToAddTelescope.get(mejorApoyo).number.get(0);
                Connection conexion = new Connection(dadNode, newNode, caraPadre, 0);

                //añadimos el nuevo nodo
                this.addNode(dadNode, newNode, conexion);

                //actualizamos al individuo
                arbol.modifyChromosome();
                this.setIsWorking(true);
                this.nModulesAdded++;

                //ponemos esta posicion como apoyo y eliminamos esta posibilidad
                //ya que hemos añadido un módulo en esta conexion
                this.faceApoyo.add(faceToAddTelescope.get(mejorApoyo));
                this.faceToAddTelescope.remove(mejorApoyo);

            }

        }
    }

    @Override
    public void repair(TreeIndividual arbol) {

        double diferenciaFitness = arbol.getFitness() - arbol.getFatherFitness();

        //El nuevo nodo aumento el fitness
        if (arbol.getFitness() > (arbol.getFatherFitness() /* *(1+this.incrementoSignificativo) */)) {
            //Llamamos a aumentar el fitnessContribution delnuevo modulo y borrar los isActiveContribution
            arbol.getRootNode().addFitnessContributionAndResetOperationalActive(diferenciaFitness);
        } else {// El nuevo nodo no aumento el fitness

            //No aumentamos el fitnessContribution borrarmos los isActiveContribution
            arbol.getRootNode().addFitnessContributionAndResetOperationalActive(0);
        }
        this.removeFatherIndividual(arbol);

    }

    private void analisisEstabilidad(TreeIndividual arbol) {

        //Variable que define cuando se considera que estan a la misma altura una cara y otra
        double toleranciaAltura = 0.02;
        DepreciatedModuleRotation rotaciones = new DepreciatedModuleRotation();
        List<Node> nodes = arbol.getListNode();

        for (Node node : nodes) {

            Vector3D posCara;
            ModuleFaces moduloFaces = new ModuleFaces();

            int tipoHijo = node.getType();

            if (node.getDad() != null) {
                try {
                    int tipoPadre = node.getDad().getType();
                    Connection conex = node.getDad().getConnection(node);
                    int caraPadre = conex.getDadFace();
                    //int cara_p = cara_padre[modulo - 1] % ModulesSet.getModulesFacesNumber(tipoPadre);
                    int caraHijo = conex.getChildrenOrientation();
                    //int cara_h = cara_hijo[(modulo - 1)] % ModulesSet.getModulesReducedFacesNumber(tipoHijo);
                    if (tipoHijo == 4) {
                        caraHijo = 0;
                    }
                    Vector3D normalCaraPadreAux = ModuleSetFactory.getModulesSet().getNormalFaceVector(tipoPadre, caraPadre);
                    if (normalCaraPadreAux == null) {
                        System.err.println("normalCaraPadreAux == null!!!!!!!!!!!!");
                    }
                    Vector3D normalCaraPadre = new Vector3D(normalCaraPadreAux.getX() * (-1), normalCaraPadreAux.getY() * (-1), normalCaraPadreAux.getZ() * (-1));
                    //Vector3d normalCaraPadre2 = ModulesSet.getNormalFaceVector(tipoPadre, caraPadre);
                    //normalCaraPadre2.negate();
                    //Ya estamos negandola en la linea anterior
                    //normalCaraPadre.negate();
//            System.out.println("VecNormalCaraPadre (negado): " + normalCaraPadre);
                    //Calculo de rpy
                    double[] rotacion;
                    rotacion = rotaciones.calculateRPY_Gazebo_oldMethod(caraHijo, normalCaraPadre);

                    node.setRpy(new Vector3D(rotacion[0], rotacion[1], rotacion[2]));
                    //******propiedades[modulo][1] = rotacion[0] + " " + rotacion[1] + " " + rotacion[2];
                    //Calculo del vector padre origen -> cara de union
                    Vector3D ocp = null;
                    //ocp = Test.vector_origen_cara[tipo_p][cara_p];+++++++++
                    ocp = ModuleSetFactory.getModulesSet().getOriginFaceVector(tipoPadre, caraPadre);


                    Vector3D och = null;
                    //Calculo del vector del modulo hijo de su origen -> cara de union
                    /*//TODO: Comprobar que todo esto es igual a la linea de abajo
                    if (caraHijo == 0) {
                    och = ModulesSet.getOriginFaceVector(tipoHijo, caraHijo);
                    } else {
                    if (caraHijo == 5 || caraHijo == 6) {
                    //Caras del actuador del slider
                    och = ModulesSet.getOriginFaceVector(tipoHijo, 10);
                    } else {
                    och = ModulesSet.getOriginFaceVector(tipoHijo, 1);
                    }
                    }
                     */
                    och = ModuleSetFactory.getModulesSet().getOriginFaceVector(tipoHijo, node.getAttachFaceToDad());


//            System.out.println("Vector o->cara_h: " + och);
//            System.out.println("Vector o->cara_p: " + ocp);
                    Vector3D och_rot = rotaciones.calculaRotacionRPY(och, rotacion);
                    och_rot.negate();
                    och_rot.add(ocp);
//            System.out.println("Vector o->origen_hijo: " + och_rot);
//            System.out.println("Cambio de union\n\n");


                    //*****
                    //propiedades[modulo][0] = och_rot.x + " " + och_rot.y + " " + och_rot.z;
                    node.setXyz(new Vector3D(och_rot.getX(), och_rot.getY(), och_rot.getZ()));


                    //*****
                    //rotacionModulo[modulo] = rotaciones.calculaMatrizRotacionGlobal(propiedades[modulo][1], rotacionModulo[modulo_padre[modulo]]);
                    node.setGlobalRpy(rotaciones.calculaMatrizRotacionGlobal(node.getRpy(), node.getDad().getGlobalRpy()));


                    //*********
                    //posicionModulo[modulo] = rotaciones.calculaRotacion(new Vector3d(och_rot.x, och_rot.y, och_rot.z), rotacionModulo[modulo_padre[modulo]]);
                    node.setXyz(rotaciones.calculaRotacion(new Vector3D(och_rot.getX(), och_rot.getY(), och_rot.getZ()), node.getDad().getGlobalRpy()));

                    //***********
                    //posicionModulo[modulo].add(posicionModulo[modulo_padre[modulo]]);
                    //TODO: Comprobar que actualiza bien el valor
                    node.getXyz().add(node.getDad().getXyz());

                    Vector3D aux = new Vector3D(node.getXyz().getX(), node.getXyz().getY(), node.getXyz().getZ());
                    //posicionModulo[modulo].get(aux);
                    //node.getXyz().get(aux);
                    double s = ModuleSetFactory.getModulesSet().getModulesMass(tipoHijo);
                    this.masaTotal += s;
                    aux.scalarMultiply(s);
                    this.cdm.add(new Vector3d(aux.getX(), aux.getY(), aux.getZ()));
                    //System.out.println("Masa Total: " + this.masaTotal + "; cdm: " + this.cdm);

                    moduloFaces.nod = node;
                    //Claculo de la cara mas baja para luego subir esa distancia el robot
                    //es decir, le damos un apoyo al robot
                    for (int cara = 0; cara < ModuleSetFactory.getModulesSet().getModulesFacesNumber(tipoHijo); cara++) {

                        if (node.isFaceFree(cara)) {
                            och = ModuleSetFactory.getModulesSet().getOriginFaceVector(tipoHijo, cara);
                            och_rot = rotaciones.calculaRotacion(och, node.getGlobalRpy());
                            och_rot.add(node.getXyz());

                            moduloFaces.number.add(cara);
                            moduloFaces.faces.add(och_rot);
                            Vector3D normalCara = rotaciones.calculaRotacion(ModuleSetFactory.getModulesSet().getNormalFaceVector(tipoHijo, cara), node.getGlobalRpy());
                            moduloFaces.normals.add(normalCara);
                            if (this.max_pos.x < och_rot.getX()) {
                                this.max_pos.x = och_rot.getX();
                            }
                            if (this.max_pos.y < och_rot.getY()) {
                                this.max_pos.y = och_rot.getY();
                            }
                            if (this.max_pos.z < och_rot.getZ()) {
                                this.max_pos.z = och_rot.getZ();
                            }
                            if (this.min_pos.x > och_rot.getX()) {
                                this.min_pos.x = och_rot.getX();
                            }
                            if (this.min_pos.y > och_rot.getY()) {
                                this.min_pos.y = och_rot.getY();
                            }
                            if (this.min_pos.z > (och_rot.getZ() + toleranciaAltura)) {
                                this.min_pos.z = och_rot.getZ();
                                carasApoyo.clear();
                            }
                            //Añadimos apoyos
                            if (och_rot.getZ() - this.min_pos.z < toleranciaAltura) {
                                carasApoyo.add((Vector3D) och_rot);
                            }

                        }
                    }

                    //Añadimos las caras de este modulo
                    this.carasModulos.add(moduloFaces);

                } catch (InconsistentDataException ex) {

                    Logger.getLogger(AddStability.class.getName()).log(Level.SEVERE, null, ex);
                    System.err.print(arbol.detailedToString());
                    System.exit(-1);
                }
            } else {//Aqui solo entramos en el primer módulo (no tiene padre)

                node.setXyz(new Vector3D(0, 0, 0));
                node.setRpy(new Vector3D(0, 0, 0));
                node.setGlobalRpy(new Matrix3d(1, 0, 0, 0, 1, 0, 0, 0, 1));
                moduloFaces.nod = node;
                for (int cara = 0; cara < ModuleSetFactory.getModulesSet().getModulesFacesNumber(tipoHijo); cara++) {
                    if (node.isFaceFree(cara)) {
                        moduloFaces.number.add(cara);
                        posCara = ModuleSetFactory.getModulesSet().getOriginFaceVector(tipoHijo, cara);
                        moduloFaces.faces.add(posCara);
                        moduloFaces.normals.add(ModuleSetFactory.getModulesSet().getNormalFaceVector(tipoHijo, cara));
                        if (this.max_pos.x < posCara.getX()) {
                            this.max_pos.x = posCara.getX();
                        }
                        if (this.max_pos.y < posCara.getY()) {
                            this.max_pos.y = posCara.getY();
                        }
                        if (this.max_pos.z < posCara.getZ()) {
                            this.max_pos.z = posCara.getZ();
                        }

                        if (this.min_pos.x > posCara.getX()) {
                            this.min_pos.x = posCara.getX();
                        }
                        if (this.min_pos.y > posCara.getY()) {
                            this.min_pos.y = posCara.getY();
                        }
                        if (this.min_pos.z > posCara.getZ()) {
                            this.min_pos.z = posCara.getZ();
                            carasApoyo.clear();
                        }
                        //Añadimos apoyos
                        if (posCara.getZ() - this.min_pos.z < toleranciaAltura) {
                            carasApoyo.add((Vector3D) posCara);
                        }
                    }
                }

                this.masaTotal += ModuleSetFactory.getModulesSet().getModulesMass(node.getType());
                this.carasModulos.add(moduloFaces);
            }
        }
        this.cdm.scale(1 / this.masaTotal);
        this.max_pos.get(this.dimensiones);
        this.dimensiones.sub(this.min_pos);



    }

    private void calculoPosiblesCarasApoyo() {
        double tolerancia = 0.001;
        double rangoAltura = 0.12;
        double umbralAltura = this.min_pos.z + 0.8;
        for (int i = 0; i < this.carasModulos.size(); i++) {
            ModuleFaces carasModulo = this.carasModulos.get(i);
            for (int j = 0; j < carasModulo.faces.size(); j++) {
                if (carasModulo.normals.get(j).getZ() < (tolerancia - 1)) {
                    //Busco si añadiendo un telescopio aqui se conseguiría un punto de apoyo
                    if (carasModulo.faces.get(j).getZ() < umbralAltura + rangoAltura && carasModulo.faces.get(j).getZ() > umbralAltura - rangoAltura) {
                        this.faceToAddTelescope.add(carasModulo.getElement(j));
                    }
                    //Busco los puntos de apoyo disponibles
                    if (carasModulo.faces.get(j).getZ() < this.min_pos.z + rangoAltura * 2) {
                        this.faceApoyo.add(carasModulo.getElement(j));
                    }
                }

            }
        }

    }

    private int calculoMejorApoyo(){
        int mejorApoyo = 0;
        double distMax = 0;
        for (int i = 0; i < this.faceToAddTelescope.size(); i++) {
            double distanciaEntreApoyos = 0;
            for(ModuleFaces caras : this.faceApoyo)
                distanciaEntreApoyos += euclidianDistance(faceToAddTelescope.get(i), caras);
            if(distanciaEntreApoyos > distMax){
                distMax = distanciaEntreApoyos;
                mejorApoyo = i;
            }
        }

        return mejorApoyo;
    }

    private double euclidianDistance(ModuleFaces a, ModuleFaces b){
        double dist = Math.pow(a.faces.get(0).getX() - b.faces.get(0).getX(), 2);
        dist += Math.pow(a.faces.get(0).getY() - b.faces.get(0).getY(), 2);
        return Math.sqrt(dist);
    }

    @Override
    public boolean isMandatory() {
        return true;
    }

    @Override
    public String toString() {
        String str = "AddStability";
        if (this.isWorking()) {
            str += "   (isWorking)";
        } else {
            str += "   (NOT Working)";
        }

        str += "\nnModulosApoyo (Inicial): " + this.nModulosApoyo;
        str += "\nnfacesApoyo (Final): " + this.faceApoyo.size();
        str += "\nnfacesToAddTelescope (Final): " + this.faceToAddTelescope.size();
        str += "\nnModulesToAdd (Inicial): " + this.nModulesToAdd;
        str += "\nnModulesAdded (Final): " + this.nModulesAdded;

        return str;
    }

    private class ModuleFaces {

        public void ModuleFaces() {
        }
        public Node nod;
        public List<Integer> number = new ArrayList<Integer>();
        public List<Vector3D> faces = new ArrayList<Vector3D>();
        public List<Vector3D> normals = new ArrayList<Vector3D>();

        public ModuleFaces getElement(int i) {
            ModuleFaces mod = new ModuleFaces();
            mod.nod = this.nod;
            mod.number.add(this.number.get(i));
            mod.faces.add(this.faces.get(i));
            mod.normals.add(this.normals.get(i));
            return mod;
        }
    }

    private void showDebugInformation() {

        System.out.println("Masa total: " + this.masaTotal);
        System.out.println("Centro de gravedad: " + this.cdm + "\n");
        System.out.println("Numero de módulos de apoyo: " + this.nModulosApoyo);
        System.out.println("Numero de módulos para que sea estable: " + this.nModulosEstable);
        System.out.println("Caras y normales:");
        for (int i = 0; i < this.carasModulos.size(); i++) {
            System.out.println("\n\nModulo: " + i);
            for (int j = 0; j < this.carasModulos.get(i).faces.size(); j++) {
                System.out.println("Cara: " + j + ", pos: " + this.carasModulos.get(i).faces.get(j) + ", normal: " + this.carasModulos.get(i).normals.get(j));
            }
        }
        System.out.println("\nCaras de apoyo (" + this.carasApoyo.size() + "): ");
        for (Vector3D caras : this.carasApoyo) {
            System.out.println(caras);
        }

        System.out.println("\nCaras de apoyo bien calculadas!!! (" + this.faceApoyo.size() + "): ");
        for (ModuleFaces caras : this.faceApoyo) {
            System.out.println("Modulo: " + caras.nod.getConstructionOrder() + "numero de cara: " + caras.number.get(0) + ", pos: " + caras.faces.get(0));
        }

        System.out.println("\nPosibles caras de apoyo (telescope) (" + this.faceToAddTelescope.size() + "): ");
        for (ModuleFaces caras : this.faceToAddTelescope) {
            System.out.println("Modulo: " + caras.nod.getConstructionOrder() + "numero de cara: " + caras.number.get(0) + ", pos: " + caras.faces.get(0));
        }
    }

    @Override
    public void configure(Configuration conf) {
    }
}

