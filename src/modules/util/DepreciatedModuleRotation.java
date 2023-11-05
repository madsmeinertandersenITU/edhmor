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
package modules.util;

import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;


/**
 *
 * @author fai
 */
public class DepreciatedModuleRotation {

    public Vector3D calculaRotacion(Vector3D vector, Matrix3d rot_rpy) {

        Vector3d vectorAux = new Vector3d(vector.getX(), vector.getY(), vector.getZ());
        Vector3d aux1 = new Vector3d();
        Vector3d aux2 = new Vector3d();
        Vector3d aux3 = new Vector3d();
        rot_rpy.getRow(0, aux1);
        rot_rpy.getRow(1, aux2);
        rot_rpy.getRow(2, aux3);
        
        
        Vector3D vector_rot = new Vector3D(aux1.dot(vectorAux), aux2.dot(vectorAux), aux3.dot(vectorAux));

        return vector_rot;
    }

    public Vector3D calculaRotacionRPY(Vector3D vector, double[] rotacion) {

        Vector3d vectorAux = new Vector3d(vector.getX(), vector.getY(), vector.getZ());
        
        //Convertimos las rotaciones de grados a radianes
        for (int i = 0; i < 3; i++) {
            rotacion[i] = rotacion[i] * Math.PI / 180;
        }

        Matrix3d rot_rpy = new Matrix3d(Math.cos(rotacion[2]) * Math.cos(rotacion[1]),
                Math.cos(rotacion[2]) * Math.sin(rotacion[1]) * Math.sin(rotacion[0]) - Math.sin(rotacion[2]) * Math.cos(rotacion[0]),
                Math.cos(rotacion[2]) * Math.sin(rotacion[1]) * Math.cos(rotacion[0]) + Math.sin(rotacion[2]) * Math.sin(rotacion[0]),
                Math.sin(rotacion[2]) * Math.cos(rotacion[1]),
                Math.sin(rotacion[2]) * Math.sin(rotacion[1]) * Math.sin(rotacion[0]) + Math.cos(rotacion[2]) * Math.cos(rotacion[0]),
                Math.sin(rotacion[2]) * Math.sin(rotacion[1]) * Math.cos(rotacion[0]) - Math.cos(rotacion[2]) * Math.sin(rotacion[0]),
                -1 * Math.sin(rotacion[1]),
                Math.cos(rotacion[1]) * Math.sin(rotacion[0]),
                Math.cos(rotacion[1]) * Math.cos(rotacion[0]));

//     System.out.println("MATRIZ ROTACION: " + aux1);
//     System.out.println("MATRIZ ROTACION: " + aux2);
//     System.out.println("MATRIZ ROTACION: " + aux3);
        Vector3d aux1 = new Vector3d();
        Vector3d aux2 = new Vector3d();
        Vector3d aux3 = new Vector3d();
        rot_rpy.getRow(0, aux1);
        rot_rpy.getRow(1, aux2);
        rot_rpy.getRow(2, aux3);

        Vector3D vector_rot = new Vector3D(aux1.dot(vectorAux), aux2.dot(vectorAux), aux3.dot(vectorAux));
//      System.out.println("Vector o->cara_h ya rotado: " + vector_rot);
        return vector_rot;
    }
//
//    private Vector3d calculaRotacion(Vector3d vector, String rotacion) {
//        return calculaRotacion(vector, stringToDouble(rotacion));
//    }

//    private Vector3d calculaRotacion(Vector3d vector, Vector3d vecRot) {
//        double rot[] = new double[3];
//        rot[0] = vecRot.x;
//        rot[1] = vecRot.y;
//        rot[2] = vecRot.z;
//
//        return calculaRotacion(vector, rot);
//    }
    private double[] stringToDouble(String rotacion) {

        int i = rotacion.indexOf(" ");
        int j = rotacion.lastIndexOf(" ");
        double rotacionDouble[] = new double[3];
        rotacionDouble[0] = Double.valueOf(rotacion.substring(0, i)).doubleValue();
        rotacionDouble[1] = Double.valueOf(rotacion.substring(i + 1, j)).doubleValue();
        rotacionDouble[2] = Double.valueOf(rotacion.substring(j + 1)).doubleValue();
        return rotacionDouble;

    }

    public Matrix3d calculaMatrizRotacionGlobal(String rotModulo, Matrix3d matrizRot) {
        double[] rotacion = stringToDouble(rotModulo);
        return calculaMatrizRotacionGlobal(rotacion, matrizRot);
    }

    public Matrix3d calculaMatrizRotacionGlobal(Vector3D rotModulo, Matrix3d matrizRot) {
        double[] rotacion = new double[3];
        rotacion[0] = rotModulo.getX();
        rotacion[1] = rotModulo.getY();
        rotacion[2] = rotModulo.getZ();
        return calculaMatrizRotacionGlobal(rotacion, matrizRot);
    }

    public Matrix3d calculaMatrizRotacionGlobal(double[] rotacion, Matrix3d matrizRot) {

        //Convertimos las rotaciones de grados a radianes
        for (int i = 0; i < 3; i++) {
            rotacion[i] = rotacion[i] * Math.PI / 180;
        }

        Matrix3d rot_rpy = new Matrix3d(Math.cos(rotacion[2]) * Math.cos(rotacion[1]),
                Math.cos(rotacion[2]) * Math.sin(rotacion[1]) * Math.sin(rotacion[0]) - Math.sin(rotacion[2]) * Math.cos(rotacion[0]),
                Math.cos(rotacion[2]) * Math.sin(rotacion[1]) * Math.cos(rotacion[0]) + Math.sin(rotacion[2]) * Math.sin(rotacion[0]),
                Math.sin(rotacion[2]) * Math.cos(rotacion[1]),
                Math.sin(rotacion[2]) * Math.sin(rotacion[1]) * Math.sin(rotacion[0]) + Math.cos(rotacion[2]) * Math.cos(rotacion[0]),
                Math.sin(rotacion[2]) * Math.sin(rotacion[1]) * Math.cos(rotacion[0]) - Math.cos(rotacion[2]) * Math.sin(rotacion[0]),
                -1 * Math.sin(rotacion[1]),
                Math.cos(rotacion[1]) * Math.sin(rotacion[0]),
                Math.cos(rotacion[1]) * Math.cos(rotacion[0]));

        Vector3d col1 = new Vector3d();
        Vector3d col2 = new Vector3d();
        Vector3d col3 = new Vector3d();
        rot_rpy.getColumn(0, col1);
        rot_rpy.getColumn(1, col2);
        rot_rpy.getColumn(2, col3);

        Vector3d row1 = new Vector3d();
        Vector3d row2 = new Vector3d();
        Vector3d row3 = new Vector3d();
        matrizRot.getRow(0, row1);
        matrizRot.getRow(1, row2);
        matrizRot.getRow(2, row3);

        Matrix3d result;

        result = new Matrix3d(row1.dot(col1), row1.dot(col2), row1.dot(col3),
                row2.dot(col1), row2.dot(col2), row2.dot(col3),
                row3.dot(col1), row3.dot(col2), row3.dot(col3));
        return result;

    }

    public double[] calculateRPY_Gazebo_oldMethod(int orientation, Vector3D parentFaceNormal) {
        
        //Negate the vector as we have to face both connection faces
        parentFaceNormal = parentFaceNormal.negate();
        
        double rotacion[] = {0.0, 0.0, 0.0};  //Rotaciones del modulo en grados
        if (orientation != 0) {
            //Rotamos siempre la cara1 (o la 10 si es el actuador del slider)
            if (parentFaceNormal.getX() == 0) {
                if (parentFaceNormal.getY() == 0) {
                    //Lo ponemos en la orientacion apropiada
                    if (parentFaceNormal.getZ() == 1) {
                        //Direccion Z
                        rotacion[0] = 0.0;
                    } else {
                        //Direccion -Z
                        rotacion[0] = 180.0;
                    }
                    //y rotamos en el eje Z segun la cara que sea
                    if (orientation == 1 || orientation == 5) {
                        rotacion[2] = 0.0;
                    } else {
                        if (orientation == 2 || orientation == 6) {
                            rotacion[2] = 90.0;
                        } else {
                            rotacion[2] = (orientation - 1) * 90.0;
                        }
                    }
                } else {
                    //Lo ponemos en la orientacion apropiada
                    if (parentFaceNormal.getY() == 1) {
                        //Direccion Y
                        rotacion[0] = 270.0;
                    } else {
                        //Direccion -Y
                        rotacion[0] = 90.0;
                    }
                    //y rotamos en el eje Y segun la cara que sea
                    if (orientation == 1 || orientation == 5) {
                        rotacion[1] = 0.0;
                    } else {
                        if (orientation == 2 || orientation == 6) {
                            rotacion[1] = 90.0;
                        } else {
                            rotacion[1] = (orientation - 1) * 90.0;
                        }
                    }
                }

            } else {
                if (parentFaceNormal.getX() == 1) {
                    //Direccion X
                    if (orientation == 1 || orientation == 5) {
                        rotacion[0] = 0.0;
                        rotacion[1] = 90.0;
                    } else {
                        if (orientation == 2 || orientation == 6) {
                            rotacion[0] = 270.0;
                            rotacion[2] = 270.0;
                        } else {
                            if (orientation == 3) {
                                rotacion[0] = 180.0;
                                rotacion[1] = 270.0;
                            } else {
                                if (orientation == 4) {
                                    rotacion[0] = 90.0;
                                    rotacion[2] = 90.0;
                                } else {
                                    System.err.println("Error calculando rpy");
                                }
                            }

                        }
                    }

                } else {
                    //Direccion -X
                    if (orientation == 1 || orientation == 5) {
                        //rotacion[0] = 0.0;
                        //rotacion[1] = 270.0;
                        rotacion[0] = 180;
                        rotacion[1] = 90;
                    } else {
                        if (orientation == 2 || orientation == 6) {
                            //rotacion[0] = 270.0;
                            //rotacion[2] = 90.0;
                            rotacion[0] = 90;
                            rotacion[2] = 270;
                        } else {
                            if (orientation == 3) {
                                //rotacion[0] = 180.0;
                                //rotacion[1] = 90.0;
                                rotacion[1] = 270;
                            } else {
                                if (orientation == 4) {
                                    //rotacion[0] = 90.0;
                                    //rotacion[2] = 270.0;
                                    rotacion[0] = 270;
                                    rotacion[2] = 90;
                                } else {
                                    System.err.println("Error calculando rpy");
                                }
                            }

                        }
                    }

                }

            }

        } else {
            //cara_h=0 y rotamos en consecuencia
            //llega con una rotacion
            if (parentFaceNormal.getX() == 0) {
                if (parentFaceNormal.getY() == 0) {
                    //Rotamos alrededor del eje Y
                    if (parentFaceNormal.getZ() == 1) {
                        //Direccion Z
                        rotacion[1] = 90.0;
                    } else {
                        //Direccion -Z
                        rotacion[1] = 270.0;
                    }
                } else {
                    //Rotamos alrededor del eje Z
                    if (parentFaceNormal.getY() == 1) {
                        //Direccion Y
                        rotacion[2] = 270.0;
                    } else {
                        //Direccion -Y
                        rotacion[2] = 90.0;
                    }

                }
            } else {
                //Rotamos alrededor del eje Z
                if (parentFaceNormal.getX() == 1) {
                    //Direccion X
                    rotacion[2] = 180.0;
                } else {
                    //Direccion -X
                    rotacion[2] = 0.0;
                }
            }
        }
        return rotacion;
    }

    public double[] calculateRPY_CoppeliaSim_oldMethod(int type, int orientation, Vector3D normalCaraPadre) {
        
        //Negate the vector as we have to face both connection faces
        normalCaraPadre = normalCaraPadre.negate();

        double rotacion[] = {0.0, 0.0, 0.0};  //Rotaciones del modulo en grados
        if (orientation != 0) {
            //Rotamos siempre la cara1 (o la 10 si es el actuador del slider)
            if (normalCaraPadre.getX() == 0) {
                if (normalCaraPadre.getY() == 0) {
                    //Lo ponemos en la orientacion apropiada
                    //y rotamos en el eje Z segun la cara que sea
                    if (orientation == 1 || orientation == 5) {
                        if (normalCaraPadre.getZ() == 1) {
                            //Direccion Z
                            rotacion[0] = 0.0;
                            rotacion[2] = 0.0;
                        } else {
                            //Direccion -Z
                            rotacion[0] = 180.0;
                            rotacion[2] = 0.0;
                        }
                    } else {
                        if (orientation == 2 || orientation == 6) {
                            if (normalCaraPadre.getZ() == 1) {
                                //Direccion Z
                                rotacion[0] = 0.0;
                                rotacion[2] = 90.0;
                            } else {
                                //Direccion -Z
                                rotacion[0] = -180.0; //rotacion[0] = 180.0;
                                rotacion[2] = -90.0;   //rotacion[2] = 90.0;
                            }
                        } else {
                            if (orientation == 3) {
                                if (normalCaraPadre.getZ() == 1) {
                                    //Direccion Z
                                    rotacion[0] = 0.0;
                                    rotacion[2] = (orientation - 1) * 90.0;
                                } else {
                                    //Direccion -Z
                                    rotacion[0] = 180.0;    //rotacion[0] = 180.0;
                                    rotacion[2] = 180.0;//rotacion[2] = (cara_h - 1) * 90.0;
                                }
                            }
                            if (orientation == 4) {
                                if (normalCaraPadre.getZ() == 1) {
                                    //Direccion Z
                                    rotacion[0] = 0.0;
                                    rotacion[2] = (orientation - 1) * 90.0;
                                } else {
                                    //Direccion -Z
                                    rotacion[0] = 180.0;//rotacion[0] = 180.0;
                                    rotacion[2] = 90;//rotacion[2] = (cara_h - 1) * 90.0;
                                }
                            }

                        }
                    }
                } else {
                    //Lo ponemos en la orientacion apropiada
                    //y rotamos en el eje Y segun la cara que sea
                    if (orientation == 1 || orientation == 5) {
                        if (normalCaraPadre.getY() == 1) {
                            //Direccion Y
                            rotacion[0] = 270.0;
                            rotacion[1] = 0.0;
                        } else {
                            //Direccion -Y
                            rotacion[0] = 90.0;
                            rotacion[1] = 0.0;
                        } 
                    } else {
                        if (orientation == 2 || orientation == 6) {
                            if (normalCaraPadre.getY() == 1) {
                                //Direccion Y
                                rotacion[0] = -90;// rotacion[0] = 270.0;
                                rotacion[2] = 90.0; //rotacion[1] = 90.0;
                            } else {
                                //Direccion -Y
                                rotacion[0] = 90.0; //rotacion[0] = 90.0;
                                rotacion[2] = -90.0; //rotacion[1] = 90.0;
                            }
                        } else {
                            if(orientation == 3){
                                if (normalCaraPadre.getY() == 1) {
                                    //Direccion Y
                                    rotacion[0] = -90.0;//rotacion[0] = 270.0;
                                    rotacion[2] = 180.0;//rotacion[1] = (cara_h - 1) * 90.0;
                                } else {
                                    //Direccion -Y
                                    rotacion[0] = 90.0;//rotacion[0] = 90.0;
                                    rotacion[2] = 180.0;//rotacion[1] = (cara_h - 1) * 90.0;
                                }
                            }
                            if(orientation == 4){
                                if (normalCaraPadre.getY() == 1) {
                                    //Direccion Y
                                    rotacion[0] = -90.0;//rotacion[0] = 270.0;
                                    rotacion[2] = -90.0;// rotacion[1] = (cara_h - 1) * 90.0;
                                } else {
                                    //Direccion -Y
                                    rotacion[0] = 90.0; //rotacion[0] = 90.0;
                                    rotacion[2] = 90.0;// rotacion[1] = (cara_h - 1) * 90.0;
                                }
                            }
                        }
                    }
                }

            } else {
                if (normalCaraPadre.getX() == 1) {
                    //Direccion X
                    if (orientation == 1 || orientation == 5) {
                        rotacion[0] = 0.0;
                        rotacion[1] = 90.0;
                    } else {
                        if (orientation == 2 || orientation == 6) {
                            rotacion[1] = 90.0;//rotacion[0] = 270.0;
                            rotacion[2] = -90.0;//rotacion[2] = 270.0;
                        } else {
                            if (orientation == 3) {
                                rotacion[1] = 90.0;//rotacion[0] = 180.0;
                                rotacion[2] = 180.0;//rotacion[1] = 270.0;
                            } else {
                                if (orientation == 4) {
                                    rotacion[1] = 90.0;//rotacion[0] = 90.0;
                                    rotacion[2] = 90.0;//rotacion[2] = 90.0;
                                } else {
                                    System.err.println("Error calculando rpy");
                                }
                            }

                        }
                    }

                } else {
                    //Direccion -X
                    if (orientation == 1 || orientation == 5) {
                        //rotacion[0] = 0.0;
                        //rotacion[1] = 270.0;
                        rotacion[1] = -90;//rotacion[0] = 180;
                        rotacion[2] = 180;//rotacion[1] = 90;
                    } else {
                        if (orientation == 2 || orientation == 6) {
                            //rotacion[0] = 270.0;
                            //rotacion[2] = 90.0;
                            rotacion[1] = -90;//rotacion[0] = 90;
                            rotacion[2] = -90;//rotacion[2] = 270;
                        } else {
                            if (orientation == 3) {
                                //rotacion[0] = 180.0;
                                //rotacion[1] = 90.0;
                                rotacion[1] = 270;
                            } else {
                                if (orientation == 4) {
                                    //rotacion[0] = 90.0;
                                    //rotacion[2] = 270.0;
                                    rotacion[1] = -90;//rotacion[0] = 270;
                                    rotacion[2] = 90;//rotacion[2] = 90;
                                } else {
                                    System.err.println("Error calculando rpy");
                                }
                            }

                        }
                    }

                }

            }

        } else {
            //cara_h=0 y rotamos en consecuencia
            //llega con una rotacion
            if (normalCaraPadre.getX() == 0) {
                if (normalCaraPadre.getY() == 0) {
                    //Rotamos alrededor del eje Y
                    if (normalCaraPadre.getZ() == 1) {
                        //Direccion Z
                        rotacion[1] = 90.0;
                    } else {
                        //Direccion -Z
                        rotacion[1] = 270.0;
                    }
                } else {
                    //Rotamos alrededor del eje Z
                    if (normalCaraPadre.getY() == 1) {
                        //Direccion Y
                        rotacion[2] = 270.0;
                    } else {
                        //Direccion -Y
                        rotacion[2] = 90.0;
                    }

                }
            } else {
                //Rotamos alrededor del eje Z
                if (normalCaraPadre.getX() == 1) {
                    //Direccion X
                    rotacion[2] = 180.0;
                } else {
                    //Direccion -X
                    rotacion[2] = 0.0;
                }
            }
        }
        
        //System.out.println("Type: " + type + ", orientation: " + orientation + ", normal" + normalCaraPadre.toString());
        //System.out.println("RPY Rotation (old method): " + rotacion[0] + " "+ rotacion[1] + " " + rotacion[2]);
        return rotacion;
    }
    
}
