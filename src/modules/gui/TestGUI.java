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
package modules.gui;

import es.udc.gii.common.eaf.util.EAFRandom;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTextField;
import modules.ModuleSetFactory;
import modules.evaluation.CoppeliaSimCreateRobot;
import modules.evaluation.CoppeliaSimEvaluator;
import modules.evaluation.CoppeliaSimulator;
import modules.individual.String2Tree;
import modules.individual.TreeIndividual;
import modules.util.*;

/**
 *
 * @author fai
 */
public class TestGUI extends javax.swing.JFrame {

    private String chromosome;
    private boolean visibility = true;
    private boolean useGazeboSimaulator = false;
    private int updateRate = 0;
    private TreeIndividual tree;
    private DeleteNode deleteNodeForm;
    private RobotFeaturesGUI featuresGUI;

    /**
     * Creates new form TestFrame
     */
    public TestGUI() {
        initComponents();
        jSlider1CiclosSim.setValue((int) SimulationConfiguration.getMaxSimulationTime() * 2);
        jLabel4.setText(Integer.toString(jSlider1CiclosSim.getValue() / 2));

        if (jRadioButton9.isSelected()) {
            SimulationConfiguration.setPoseFitness("BASE");
        }
        if (jRadioButton8.isSelected()) {
            SimulationConfiguration.setPoseFitness("COM");
        }

        String[] str = new String[17];
        str[0] = "distanceTravelled";
        str[1] = "distanceTravelledAndBrokenConnPenalty";
        str[2] = "useWalkDistance";
        str[3] = "useWalkDistance45Degrees";
        str[4] = "useWalkDistanceXMinusAbsY";
        str[5] = "useWalkDistanceX";
        str[6] = "useMaxHeight";
        str[7] = "useMaxHeightWithLowTurns";
        str[8] = "useMaxZMovement";
        str[9] = "useMaxTurn";
        str[10] = "useMaxTurnConCarga";
        str[11] = "usePathZ";
        str[12] = "useX-YWithoutFallStairs";
        str[13] = "useCargaWithoutFall";
        str[14] = "useCargaWithoutFallAndWithoutFallStairs";
        str[15] = "usePaintWall";
        str[16] = "useCleanFloor";
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(str));

        String[] strBase = new String[23];
        strBase[0] = "default";
        strBase[1] = "baseEstandar";
        strBase[2] = "baseConMuros";
        strBase[3] = "baseConMurosOcultos";
        strBase[4] = "baseEscalera";
        strBase[5] = "baseEscaleraEstrecha";
        strBase[6] = "baseConCarga";
        strBase[7] = "sueloRugoso1";
        strBase[8] = "sueloRugoso2";
        strBase[9] = "sueloRugoso3";
        strBase[10] = "sueloRugoso4";
        strBase[11] = "sueloRugoso5";
        strBase[12] = "sueloRugoso3ConCarga";
        strBase[13] = "sueloRugoso4ConCarga";
        strBase[14] = "sueloRugoso5ConCarga";
        strBase[15] = "sueloRugoso3ConCargaPesada";
        strBase[16] = "sueloRugoso3ConCargaLiviana";
        strBase[17] = "sueloRugoso10ConCarga";
        strBase[18] = "sueloRugoso11ConCarga";
        //strBase[13] = "baseConGuiaConCarga";
        strBase[19] = "baseConSoporteConCarga";
        strBase[20] = "sueloConObstaculos";
        strBase[21] = "manipulator";
        strBase[22] = "manipulatorConPared";

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(strBase));

        String[] strModuleSet = new String[12];
        strModuleSet[0] = "EmergeAndCuboidBaseModules";
        strModuleSet[1] = "Emerge18AndCuboidBaseModules";
        strModuleSet[2] = "EmergeAndFlatBaseModules";
        strModuleSet[3] = "Emerge18AndFlatBaseModules";
        strModuleSet[4] = "OldEdhmorModules";
        strModuleSet[5] = "RealEdhmorModules";
        strModuleSet[6] = "TestModules";
        strModuleSet[7] = "RodrigoModules_1_2_3";
        strModuleSet[8] = "RodrigoModules_2_3";
        strModuleSet[9] = "RodrigoModules_1_2";
        strModuleSet[10] = "RodrigoModules_1";
        strModuleSet[11] = "RodrigoModules_2";
        moduleSetComboBox.setModel(new javax.swing.DefaultComboBoxModel(strModuleSet));

        deleteNodeForm = new DeleteNode(tree, this);
        featuresGUI = new RobotFeaturesGUI(this);

        this.setAlwaysOnTop(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jButton2 = new javax.swing.JButton();
        buttonGroup2 = new javax.swing.ButtonGroup();
        buttonGroup3 = new javax.swing.ButtonGroup();
        buttonGroup4 = new javax.swing.ButtonGroup();
        buttonGroup5 = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        runButton = new javax.swing.JButton();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jLabel2 = new javax.swing.JLabel();
        jSlider1CiclosSim = new javax.swing.JSlider();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        demo1Button = new javax.swing.JButton();
        demo2Button = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jRadioButton3 = new javax.swing.JRadioButton();
        jRadioButton4 = new javax.swing.JRadioButton();
        jRadioButton5 = new javax.swing.JRadioButton();
        jTextField2 = new javax.swing.JTextField();
        deleteButton = new javax.swing.JButton();
        AddNodeButton = new javax.swing.JButton();
        printButton = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jLabel9 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox();
        demo3Button = new javax.swing.JButton();
        demo4Button = new javax.swing.JButton();
        demo5Button = new javax.swing.JButton();
        jCheckBox5 = new javax.swing.JCheckBox();
        jCheckBox6 = new javax.swing.JCheckBox();
        jCheckBox7 = new javax.swing.JCheckBox();
        jLabel12 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        manipulator1Button = new javax.swing.JButton();
        manipulator2Button = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        jRadioButton6 = new javax.swing.JRadioButton();
        jRadioButton7 = new javax.swing.JRadioButton();
        jLabel16 = new javax.swing.JLabel();
        moduleSetComboBox = new javax.swing.JComboBox();
        randomButton = new javax.swing.JButton();
        jRadioButton8 = new javax.swing.JRadioButton();
        jRadioButton9 = new javax.swing.JRadioButton();
        jButtonBuild = new javax.swing.JButton();

        jButton2.setText("jButton2");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Test GUI");

        jLabel1.setText("Individual:");

        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        runButton.setText("Run");
        runButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runButtonActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setSelected(true);
        jRadioButton1.setText("True");
        jRadioButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton1ActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setText("False");
        jRadioButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton2ActionPerformed(evt);
            }
        });

        jLabel2.setText("Visibility:");

        jSlider1CiclosSim.setMinimum(1);
        jSlider1CiclosSim.setPaintLabels(true);
        jSlider1CiclosSim.setPaintTicks(true);
        jSlider1CiclosSim.setSnapToTicks(true);
        jSlider1CiclosSim.setValueIsAdjusting(true);
        jSlider1CiclosSim.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jSlider1CiclosSimMouseReleased(evt);
            }
        });

        jLabel3.setText("Simulation Time:");

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("0");
        jLabel4.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(102, 102, 102), 1, true));

        jLabel5.setText("Fitness:");

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("0");
        jLabel6.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(102, 102, 102), 1, true));
        jLabel6.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jButton3.setText("Delete");
        jButton3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButton3MouseReleased(evt);
            }
        });
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        demo1Button.setText("demo1");
        demo1Button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                demo1ButtonMouseReleased(evt);
            }
        });
        demo1Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                demo1ButtonActionPerformed(evt);
            }
        });

        demo2Button.setText("demo2");
        demo2Button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                demo2ButtonMouseReleased(evt);
            }
        });
        demo2Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                demo2ButtonActionPerformed(evt);
            }
        });

        jLabel7.setText("Update Rate:");

        buttonGroup2.add(jRadioButton3);
        jRadioButton3.setSelected(true);
        jRadioButton3.setText("Fast");
        jRadioButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton3ActionPerformed(evt);
            }
        });

        buttonGroup2.add(jRadioButton4);
        jRadioButton4.setText("Real Time          ");
        jRadioButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton4ActionPerformed(evt);
            }
        });

        buttonGroup2.add(jRadioButton5);
        jRadioButton5.setText("Custom");
        jRadioButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton5ActionPerformed(evt);
            }
        });

        jTextField2.setText("100");
        jTextField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField2ActionPerformed(evt);
            }
        });

        deleteButton.setText("Delete");
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        AddNodeButton.setText("AddNode");
        AddNodeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddNodeButtonActionPerformed(evt);
            }
        });

        printButton.setText("Features");
        printButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printButtonActionPerformed(evt);
            }
        });

        jLabel8.setText("Fitness Function:");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jLabel9.setText("Scene");

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        demo3Button.setText("demo3");
        demo3Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                demo3ButtonActionPerformed(evt);
            }
        });

        demo4Button.setText("demo4");
        demo4Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                demo4ButtonActionPerformed(evt);
            }
        });

        demo5Button.setText("demo5");
        demo5Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                demo5ButtonActionPerformed(evt);
            }
        });

        jCheckBox5.setText("AmplitudeControl");
        jCheckBox5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox5ActionPerformed(evt);
            }
        });

        jCheckBox6.setText("AngularFreqControl");
        jCheckBox6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox6ActionPerformed(evt);
            }
        });

        jCheckBox7.setSelected(true);
        jCheckBox7.setText("PhaseControl");
        jCheckBox7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox7ActionPerformed(evt);
            }
        });

        jLabel12.setText("Pose Update Rate:");

        jTextField5.setText("2.00");

        jLabel13.setText("Fitness parameter:");

        jTextField6.setText("2.5");
        jTextField6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField6ActionPerformed(evt);
            }
        });

        manipulator1Button.setText("manipulator");
        manipulator1Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manipulator1ButtonActionPerformed(evt);
            }
        });

        manipulator2Button.setText("manipulator2");
        manipulator2Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manipulator2ButtonActionPerformed(evt);
            }
        });

        jLabel15.setText("Simulator:");

        buttonGroup3.add(jRadioButton6);
        jRadioButton6.setText("Gazebo");
        jRadioButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton6ActionPerformed(evt);
            }
        });

        buttonGroup3.add(jRadioButton7);
        jRadioButton7.setSelected(true);
        jRadioButton7.setText("V-REP");
        jRadioButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton7ActionPerformed(evt);
            }
        });

        jLabel16.setText("Module Set:");

        moduleSetComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        moduleSetComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moduleSetComboBoxActionPerformed(evt);
            }
        });

        randomButton.setText("random");
        randomButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                randomButtonActionPerformed(evt);
            }
        });

        buttonGroup5.add(jRadioButton8);
        jRadioButton8.setText("COM");
        jRadioButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton8ActionPerformed(evt);
            }
        });

        buttonGroup5.add(jRadioButton9);
        jRadioButton9.setSelected(true);
        jRadioButton9.setText("BASE");
        jRadioButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton9ActionPerformed(evt);
            }
        });

        jButtonBuild.setText("Build");
        jButtonBuild.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBuildActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 1060, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel15)
                            .addComponent(jRadioButton7)
                            .addComponent(jRadioButton6)
                            .addComponent(runButton, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(deleteButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(AddNodeButton))
                            .addComponent(jLabel16)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(moduleSetComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(demo1Button)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(demo2Button))))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(printButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(jLabel12)
                                                    .addComponent(jLabel2)
                                                    .addComponent(jRadioButton1)
                                                    .addComponent(jRadioButton2))
                                                .addGap(19, 19, 19)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addGroup(layout.createSequentialGroup()
                                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                            .addComponent(jLabel7)
                                                            .addComponent(jRadioButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                            .addComponent(jRadioButton4))
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                            .addComponent(jSlider1CiclosSim, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                            .addGroup(layout.createSequentialGroup()
                                                                .addComponent(jLabel3)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                                    .addGroup(layout.createSequentialGroup()
                                                        .addComponent(jRadioButton5)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                            .addComponent(jButtonBuild))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(jLabel8)
                                                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addGroup(layout.createSequentialGroup()
                                                        .addComponent(jLabel13)
                                                        .addGap(3, 3, 3)
                                                        .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(jCheckBox7)
                                                    .addComponent(jCheckBox5)
                                                    .addComponent(jCheckBox6)
                                                    .addComponent(jLabel9)
                                                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jRadioButton8)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(jRadioButton9))))
                                    .addGroup(layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(demo3Button)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(demo4Button)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(demo5Button)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(manipulator1Button)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(manipulator2Button)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(randomButton)))
                                .addGap(233, 233, 233))))
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(demo1Button)
                    .addComponent(demo2Button)
                    .addComponent(demo3Button)
                    .addComponent(demo4Button)
                    .addComponent(demo5Button)
                    .addComponent(manipulator1Button)
                    .addComponent(manipulator2Button)
                    .addComponent(randomButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(jLabel9))
                        .addGap(8, 8, 8)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jSlider1CiclosSim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jCheckBox5))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jRadioButton8)
                                    .addComponent(jRadioButton9)))))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4))
                        .addGap(1, 1, 1)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jRadioButton3)
                                    .addComponent(jRadioButton1))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jRadioButton2)
                                    .addComponent(jRadioButton4))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jRadioButton5)
                                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jRadioButton7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jRadioButton6)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel16))))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel15)
                        .addComponent(jLabel2)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addComponent(jCheckBox6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheckBox7)
                        .addGap(51, 51, 51)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel13)
                            .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(16, 16, 16)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel12)
                                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(moduleSetComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(runButton, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButtonBuild, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(9, 9, 9)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(deleteButton)
                            .addComponent(AddNodeButton)
                            .addComponent(printButton)
                            .addComponent(jLabel6))))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        chromosome = ((JTextField) evt.getSource()).getText();
        jLabel6.setText(Double.toString(0.0));
        String2Tree string2tree = new String2Tree(jTextField1.getText());
        tree = string2tree.toTree();
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void runButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runButtonActionPerformed
        // Evaluate the robot:

        //Set the correct module set to employ
        String moduleSet = (String) moduleSetComboBox.getModel().getElementAt(moduleSetComboBox.getSelectedIndex());
        SimulationConfiguration.setModuleSet(moduleSet);
        SimulationConfiguration.setDebug(true);
        ModuleSetFactory.reloadModuleSet();

        //Set the control parameters to employ
        checkControlParameters();

        String world = (String) jComboBox2.getModel().getElementAt(jComboBox2.getSelectedIndex());

        chromosome = jTextField1.getText();
        //String2Tree string2tree = new String2Tree(chromosome);
        //tree = string2tree.toTree();

        SimulationConfiguration.setFitnessFunctionStr((String) jComboBox1.getModel().getElementAt(jComboBox1.getSelectedIndex()));
        List<String> worldBase = new ArrayList<String>();
        worldBase.add((String) jComboBox2.getModel().getElementAt(jComboBox2.getSelectedIndex()));
        SimulationConfiguration.setWorldsBase(worldBase);
        System.err.println(chromosome);
        if (chromosome != null && !chromosome.equals("")) {
            double[] chromosomeDouble = ChromoConversion.str2double(chromosome);
            CoppeliaSimEvaluator evaluator = new CoppeliaSimEvaluator(chromosomeDouble, worldBase.get(0), this.visibility);
            evaluator.setMaxSimulationTime(jSlider1CiclosSim.getValue() / 2);
            evaluator.setUpdateRate(updateRate);
            this.setAlwaysOnTop(false);
            double fitness = evaluator.evaluate();
            this.setAlwaysOnTop(true);

            jLabel6.setText(Double.toString(fitness));
        }
    }//GEN-LAST:event_runButtonActionPerformed

    private void jRadioButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton1ActionPerformed
        visibility = true;
    }//GEN-LAST:event_jRadioButton1ActionPerformed

    private void jRadioButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton2ActionPerformed
        visibility = false;
    }//GEN-LAST:event_jRadioButton2ActionPerformed

    private void jSlider1CiclosSimMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSlider1CiclosSimMouseReleased
        jLabel4.setText(Integer.toString(jSlider1CiclosSim.getValue() / 2));
}//GEN-LAST:event_jSlider1CiclosSimMouseReleased

    private void jButton3MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton3MouseReleased
        jTextField1.setText("");
        this.chromosome = "";
    }//GEN-LAST:event_jButton3MouseReleased

    private void demo1ButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_demo1ButtonMouseReleased
        /*this.chromosome = "0.0, 4.0, 4.0, 4.0, 4.0, 4.0, 4.0, 4.0, 4.0, 4.0, 4.0, 4.0, 4.0, 4.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0," +
        " 1.0, 1.0, 4.0, 7.0, 10.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0," +
        " 0.0, 0.0, 0.0, 0.0, 0.0, 180.0, 180.0, 180.0, 0.0, 180.0, 180.0, 180.0, 180.0, 180.0, 180.0, 180.0, 180.0";*/
        this.chromosome = "0.0 2.0 2.0 "
                + "2.0 0.0 "
                + "1.0 7.0 "
                + "2.0 2.0 "
                + "0.0 180.0 180.0";
        jTextField1.setText(this.chromosome);
    }//GEN-LAST:event_demo1ButtonMouseReleased

    private void demo2ButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_demo2ButtonMouseReleased
        this.chromosome = "0.0, 1.0, 1.0, 1.0, 1.0, 2.0, 2.0, 2.0, 2.0, 0.0,"
                + //10
                "4.0, 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0,"
                + //9
                " 1.0, 4.0, 7.0, 10.0, 12.0, 4.0, 12.0, 2.0, 0.0,"
                + //9
                "0.0, 5.0, 0.0, 5.0, 0.0, 0.0, 0.0, 0.0, 0.0,"
                + //9
                " 0.0, 0.0, 180.0, 180.0, 180.0, 0.0, 180.0, 0.0, 180.0, 0.0";   //10
        jTextField1.setText(this.chromosome);
    }//GEN-LAST:event_demo2ButtonMouseReleased

    private void jRadioButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton3ActionPerformed
        this.updateRate = 0;
    }//GEN-LAST:event_jRadioButton3ActionPerformed

    private void jRadioButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton4ActionPerformed
        this.updateRate = -1;
    }//GEN-LAST:event_jRadioButton4ActionPerformed

    private void jRadioButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton5ActionPerformed
        System.out.println(jTextField2.getText());
        this.updateRate = Integer.parseInt(jTextField2.getText().replaceAll(" ", ""));
    }//GEN-LAST:event_jRadioButton5ActionPerformed

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField2ActionPerformed
        System.out.println(jTextField2.getText());
        this.updateRate = Integer.parseInt(jTextField2.getText().replaceAll(" ", ""));
    }//GEN-LAST:event_jTextField2ActionPerformed

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed

        chromosome = jTextField1.getText();
        if (chromosome != null && !chromosome.equals("")) {

            String2Tree string2tree = new String2Tree(chromosome);

            deleteNodeForm.setTree(tree);
            deleteNodeForm.setVisible(true);
            this.setVisible(false);
        }
}//GEN-LAST:event_deleteButtonActionPerformed

    private void AddNodeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddNodeButtonActionPerformed
}//GEN-LAST:event_AddNodeButtonActionPerformed

    private void printButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printButtonActionPerformed

        chromosome = jTextField1.getText();
        if (chromosome != null && !chromosome.equals("")) {

            String2Tree string2tree = new String2Tree(chromosome);
            tree = string2tree.toTree();
            featuresGUI.showFeatures(tree);
            featuresGUI.setVisible(true);
            this.setVisible(false);
        }

    }//GEN-LAST:event_printButtonActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed

    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void demo3ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_demo3ButtonActionPerformed
        // Boton demo3:
        this.chromosome = "0.0, 3.0, 3.0, 4.0, 4.0, 4.0, 4.0, 4.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,"
                + " 2.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,"
                + " 8.0, 10.0, 9.0, 8.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,"
                + " 1.0, 1.0, 1.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,"
                + " 0.0, 42.42250898329225, 42.42250898329225, 194.64939899929797, 194.64939899929797, 110.11288017558527, 110.11288017558527, 68.49681559699827, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0";
        jTextField1.setText(this.chromosome);
    }//GEN-LAST:event_demo3ButtonActionPerformed

    private void demo4ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_demo4ButtonActionPerformed
        this.chromosome = "0.0, 4.0, 4.0, 3.0, 3.0, 4.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, "
                + "2.0, 1.0, 1.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, "
                + "5.0, 2.0, 1.0, 1.0, 6.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, "
                + "1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, "
                + "0.0, 129.92140605916103, 129.92140605916103, 345.49032496859047, 345.49032496859047, 163.4017202020253, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, "
                + "0.0, -0.06226086075692927, -0.06226086075692927, -0.1347533102374835, -0.1347533102374835, 0.48769680705016216, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0";
        jTextField1.setText(this.chromosome);
    }//GEN-LAST:event_demo4ButtonActionPerformed

    private void demo5ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_demo5ButtonActionPerformed
        this.chromosome = "0.0, 2.0, 1.0, 1.0, 2.0, 3.0, 4.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,"
                + " 1.0, 1.0, 1.0, 1.0, 2.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, "
                + "4.0, 9.0, 8.0, 5.0, 6.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, "
                + "2.0, 5.0, 5.0, 4.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, "
                + "37.87515809242915, 148.11557010119813, 140.11575866281677, 294.622740119388, 66.36826559708047, 258.01280372902505, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -0.04324740375041103, 0.4877556761588998, -0.23097819120981988, 0.3360408414519077, 0.1645258391870701, 0.1343523646243242, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0";
        jTextField1.setText(this.chromosome);
    }//GEN-LAST:event_demo5ButtonActionPerformed

    private void demo1ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_demo1ButtonActionPerformed

    }//GEN-LAST:event_demo1ButtonActionPerformed

    private void jCheckBox5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox5ActionPerformed
        if (jCheckBox5.getModel().isSelected()) {
            SimulationConfiguration.setUseAmplitudeControl(true);
        } else {
            SimulationConfiguration.setUseAmplitudeControl(false);
        }

    }//GEN-LAST:event_jCheckBox5ActionPerformed

    private void jCheckBox6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox6ActionPerformed
        if (jCheckBox6.getModel().isSelected()) {
            SimulationConfiguration.setUseAngularFControl(true);
        } else {
            SimulationConfiguration.setUseAngularFControl(false);
        }
    }//GEN-LAST:event_jCheckBox6ActionPerformed

    private void jCheckBox7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox7ActionPerformed
        if (jCheckBox7.getModel().isSelected()) {
            SimulationConfiguration.setUsePhaseControl(true);
        } else {
            SimulationConfiguration.setUsePhaseControl(false);
        }
    }//GEN-LAST:event_jCheckBox7ActionPerformed

    private void jTextField6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField6ActionPerformed

    }//GEN-LAST:event_jTextField6ActionPerformed

    private void manipulator1ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_manipulator1ButtonActionPerformed
        //Not evolved manipulator
        this.chromosome = "0.0, 1.0, 1.0, 1.0, 5.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, "
                + "1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, "
                + "0.0, 8.0, 13.0, 5.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, "
                + "6.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, "
                + "0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, "
                + //Amplitude
                "0.0, 0.5, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, "
                + //AngularFrequency
                "0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, "
                + //Phase
                "0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, "
                + //Amplitude Modulation
                "0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0";    //Frequency Modulation
        jTextField1.setText(this.chromosome);
    }//GEN-LAST:event_manipulator1ButtonActionPerformed

    private void manipulator2ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_manipulator2ButtonActionPerformed
        //Evolved manipulator
        this.chromosome = "0.0, 2.0, 3.0, 1.0, 5.0, 4.0, 5.0, 1.0, 2.0, 5.0, 5.0, 5.0, 0.0, 0.0, 0.0, 0.0,"
                + " 1.0, 3.0, 2.0, 0.0, 0.0, 1.0, 0.0, 1.0, 3.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,"
                + " 0.0, 4.0, 3.0, 9.0, 7.0, 2.0, 1.0, 3.0, 8.0, 6.0, 7.0, 0.0, 0.0, 0.0, 0.0, "
                + "3.0, 2.0, 6.0, 0.0, 0.0, 0.0, 6.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, "
                + "0.0, 0.9392896258890238, 0.26985161534089686, 0.003087118646490583, 0.09641935202185525, 0.5382117779368765, 0.8083634297890374, 0.9163149353923269, 0.04992967350507904, 0.20638562380612735, 0.8876345102002298, 0.6398698573431135, 0.0, 0.0, 0.0, 0.0, "
                + "0.0, 0.5888726165161785, 0.9475603944773892, 0.6331911142314298, 0.2601265154389011, 0.0447791866710644, 0.3343620704093818, 0.9384975078567179, 0.6278497762730983, 0.09327978039359264, 0.22689661124522464, 0.8882105424213241, 0.0, 0.0, 0.0, 0.0,"
                + " 0.0, 145.5880269122199, 246.90027519642604, 334.2317775220168, 206.54025818402553, 316.7244119525492, 122.9002816577246, 46.208641986182975, 84.92675282310678, 25.423278557671974, 106.60115237528215, 302.97802776100156, 0.0, 0.0, 0.0, 0.0, 0.0,"
                + " 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, "
                + "0.0, 2.883382948653865, 1.4856688540854375, -1.3800566722389727, -2.9921887036483703, 0.11679557422205544, -3.443484570493342, 0.7575679242535562, 2.468118742063883, -2.3771110907445694, 3.222842688927556, 0.9283005643038971, 0.0, 0.0, 0.0, 0.0";
        jTextField1.setText(this.chromosome);
    }//GEN-LAST:event_manipulator2ButtonActionPerformed

    private void jRadioButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton7ActionPerformed
        useGazeboSimaulator = false;
    }//GEN-LAST:event_jRadioButton7ActionPerformed

    private void jRadioButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton6ActionPerformed
        useGazeboSimaulator = true;
    }//GEN-LAST:event_jRadioButton6ActionPerformed

    private void randomButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_randomButtonActionPerformed
        EAFRandom.init();
        TreeIndividual randomTree = new TreeIndividual();
        randomTree.init(SimulationConfiguration.getMaxModules() * 9 - 3);
        randomTree.generate();
        this.chromosome = randomTree.toString();
        jTextField1.setText(this.chromosome);
    }//GEN-LAST:event_randomButtonActionPerformed

    private void demo2ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_demo2ButtonActionPerformed

    }//GEN-LAST:event_demo2ButtonActionPerformed

    private void moduleSetComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moduleSetComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_moduleSetComboBoxActionPerformed

    private void jRadioButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton9ActionPerformed
        SimulationConfiguration.setPoseFitness("BASE");
    }//GEN-LAST:event_jRadioButton9ActionPerformed

    private void jRadioButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton8ActionPerformed
        SimulationConfiguration.setPoseFitness("COM");
    }//GEN-LAST:event_jRadioButton8ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed

    /*Builds a robot in CoppeliaSim*/
    private void jButtonBuildActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBuildActionPerformed
        //Set the correct module set to employ
        String moduleSet = (String) moduleSetComboBox.getModel().getElementAt(moduleSetComboBox.getSelectedIndex());
        SimulationConfiguration.setModuleSet(moduleSet);
        SimulationConfiguration.setDebug(true);
        ModuleSetFactory.reloadModuleSet();

        SimulationConfiguration.setMaxModules(20);

        String world = (String) jComboBox2.getModel().getElementAt(jComboBox2.getSelectedIndex());

        chromosome = jTextField1.getText();
        //String2Tree string2tree = new String2Tree(chromosome);
        //tree = string2tree.toTree();

        List<String> worldBase = new ArrayList<String>();
        worldBase.add((String) jComboBox2.getModel().getElementAt(jComboBox2.getSelectedIndex()));
        SimulationConfiguration.setWorldsBase(worldBase);
        System.err.println(chromosome);

        if (chromosome != null && !chromosome.equals("")) {
            double[] chromosomeDouble = ChromoConversion.str2double(chromosome);
            CoppeliaSimulator coppeliaSimulator = SimulationConfiguration.getCoppeliaSim();
            if (coppeliaSimulator == null) {
                System.out.println("coppeliaSim simulator is null, connecting to coppeliaSim...");
                coppeliaSimulator = new CoppeliaSimulator();
                coppeliaSimulator.start();
                SimulationConfiguration.setCoppeliaSim(coppeliaSimulator);
            }
            CoppeliaSimCreateRobot createRobot = new CoppeliaSimCreateRobot(coppeliaSimulator.getCoppeliaSimApi(), 
                    coppeliaSimulator.getClientID(), chromosomeDouble, worldBase.get(0), false);
            createRobot.createRobot();
            
        }
    }//GEN-LAST:event_jButtonBuildActionPerformed

    public void setArbol(TreeIndividual arbol) {
        this.tree = arbol;
        jTextField1.setText(arbol.toString());
        this.setVisible(true);

    }

    private void checkControlParameters() {

        //Amplitude control
        if (jCheckBox5.getModel().isSelected()) {
            SimulationConfiguration.setUseAmplitudeControl(true);
        } else {
            SimulationConfiguration.setUseAmplitudeControl(false);
        }

        //Angular Frequency Control 
        if (jCheckBox6.getModel().isSelected()) {
            SimulationConfiguration.setUseAngularFControl(true);
        } else {
            SimulationConfiguration.setUseAngularFControl(false);
        }

        //Phase Control
        if (jCheckBox7.getModel().isSelected()) {
            SimulationConfiguration.setUsePhaseControl(true);
        } else {
            SimulationConfiguration.setUsePhaseControl(false);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new TestGUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AddNodeButton;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.ButtonGroup buttonGroup4;
    private javax.swing.ButtonGroup buttonGroup5;
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton demo1Button;
    private javax.swing.JButton demo2Button;
    private javax.swing.JButton demo3Button;
    private javax.swing.JButton demo4Button;
    private javax.swing.JButton demo5Button;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButtonBuild;
    private javax.swing.JCheckBox jCheckBox5;
    private javax.swing.JCheckBox jCheckBox6;
    private javax.swing.JCheckBox jCheckBox7;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JRadioButton jRadioButton4;
    private javax.swing.JRadioButton jRadioButton5;
    private javax.swing.JRadioButton jRadioButton6;
    private javax.swing.JRadioButton jRadioButton7;
    private javax.swing.JRadioButton jRadioButton8;
    private javax.swing.JRadioButton jRadioButton9;
    private javax.swing.JSlider jSlider1CiclosSim;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JButton manipulator1Button;
    private javax.swing.JButton manipulator2Button;
    private javax.swing.JComboBox moduleSetComboBox;
    private javax.swing.JButton printButton;
    private javax.swing.JButton randomButton;
    private javax.swing.JButton runButton;
    // End of variables declaration//GEN-END:variables
}
