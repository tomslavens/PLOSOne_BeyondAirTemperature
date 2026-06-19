package gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import data.Entity;
import data.GeomManager;
import data.JDOFileIO;
import data.Material;
import geom.BSplineCurve;
import geom.BSplineSurface;
import geom.NURBArc;
import geom.ParamCurve;
import geom.Point3D;
import geom_funcs.Constraint;
import geom_funcs.Length2DConstraint;
import hiperfcomp.CPUMatrixSolver;
import images.DynoImage;
import images.ImageProcessing;
import linear_math.Matrix;
import rendering.JDynomaticRenderer;
import rendering.RendererOptionBank;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JTextField;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.event.ChangeListener;

import analysis.ThermalTriMesh;
import analysis_mesh.AnalysisMesh;

import javax.swing.event.ChangeEvent;
import javax.swing.JList;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.FileReader;

import javax.swing.JRadioButton;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.border.LineBorder;
import java.awt.Color;

public class MainWindow extends JFrame {

	private JPanel contentPane;
	private JScrollPane scrollPane;
	private JTextArea outwindow;
	private JDynomaticRenderer dynomaticRenderer;
	private JCheckBox nodequery_checkbox;
	private ButtonGroup contpallete_radgroup;
	private JRadioButton redblue_radbutt;
	private JRadioButton firefight_radbutt;
	private JRadioButton fallforward_radbutt;
	private ButtonGroup contplot_radgroup;
	private JRadioButton cplotnone_radbutt;
	private JRadioButton cplotsolution_radbutt;
	private JRadioButton cplotstiffness_radbutt;
	private JScrollPane ent_scrollpane;
	private JFileChooser filechooser;
	private boolean mult_core;
	private int core_targ;
	
	//Data and Geometry Manager
	private GeomManager geom_manager;
	private JList ent_list;
	//------------
	// Program entities
	//private ArrayList<Point3D> pts;
	//private ArrayList<ParamCurve> splines;
	//private ArrayList<BSplineSurface> bsurfs;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow frame = new MainWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainWindow() {
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				keyPressedAction(e);
			}
			@Override
			public void keyReleased(KeyEvent e) {
				keyReleasedAction(e);
			}
		});
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				setSizes();
				dynomaticRenderer.invalidate();
				dynomaticRenderer.repaint();
			}
		});
				
		setTitle("JDynomatic");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1071, 814);
		setFocusable(true);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		filechooser = new JFileChooser();
		mult_core = false;
		core_targ = 1;
		
		//-----------
		//instantiate our data
		outwindow = new JTextArea();
		outwindow.setEditable(false);
		ent_scrollpane = new JScrollPane();
		ent_scrollpane.setBounds(891, 15, 150, 568);
		contentPane.add(ent_scrollpane);
		
		DefaultListModel dlm = new DefaultListModel();
		ent_list = new JList(dlm);
		ent_list.setBorder(new LineBorder(new Color(0, 0, 0)));
		ent_list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2)
					entListSelect();
			}
		});
		ent_scrollpane.setViewportView(ent_list);
		geom_manager = new GeomManager(ent_list,outwindow);
		//-----------
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnNewMenu = new JMenu("File");
		menuBar.add(mnNewMenu);
		
		JMenuItem mntmNewMenuItem_16 = new JMenuItem("Save");
		mntmNewMenuItem_16.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveFileButt();
			}
		});
		mnNewMenu.add(mntmNewMenuItem_16);
		
		JMenuItem mntmNewMenuItem_17 = new JMenuItem("Read");
		mntmNewMenuItem_17.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				readFileButt();
			}
		});
		mnNewMenu.add(mntmNewMenuItem_17);
		
		JMenuItem mntmNewMenuItem_22 = new JMenuItem("System Setup");
		mntmNewMenuItem_22.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				systemSetupButt();
			}
		});
		
		JMenuItem mntmExportPoints = new JMenuItem("Export Points");
		mntmExportPoints.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exportPoints();
			}
		});
		mnNewMenu.add(mntmExportPoints);
		
		JMenuItem mntmExportLinehulls = new JMenuItem("Export LineHulls");
		mntmExportLinehulls.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exportLineHulls();
			}
		});
		mnNewMenu.add(mntmExportLinehulls);
		mnNewMenu.add(mntmNewMenuItem_22);
		
		JMenu mnNewMenu_10 = new JMenu("Image");
		menuBar.add(mnNewMenu_10);
		
		JMenuItem mntmNewMenuItem_25 = new JMenuItem("Import Image");
		mntmNewMenuItem_25.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				importImageButt();
			}
		});
		mnNewMenu_10.add(mntmNewMenuItem_25);
		
		JMenuItem mntmNewMenuItem_26 = new JMenuItem("Clear Image");
		mntmNewMenuItem_26.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clearImageButt();
			}
		});
		mnNewMenu_10.add(mntmNewMenuItem_26);
		
		JMenuItem mntmExtractImageEdge = new JMenuItem("Extract Image Edge in Surface");
		mntmExtractImageEdge.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				extractImageEdge();
			}
		});
		mntmExtractImageEdge.setToolTipText("Select a BSurface to extract the underlying image and perform an edge fit around it");
		mnNewMenu_10.add(mntmExtractImageEdge);
		
		JMenuItem mntmExtractEdgeWith = new JMenuItem("Extract Edge with RGB");
		mntmExtractEdgeWith.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				extractImageEdgeRGB();
			}
		});
		mnNewMenu_10.add(mntmExtractEdgeWith);
		
		JMenu mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);
		
		JMenuItem mntmBubbleRemovePoints = new JMenuItem("Remove Points");
		mntmBubbleRemovePoints.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removePoints();
			}
		});
		mnEdit.add(mntmBubbleRemovePoints);
		
		JMenuItem mntmRemoveLineHull = new JMenuItem("Remove Line Hull");
		mntmRemoveLineHull.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeLineHull();
			}
		});
		mnEdit.add(mntmRemoveLineHull);
		
		JMenu mnNewMenu_1 = new JMenu("Geometry");
		menuBar.add(mnNewMenu_1);
		
		JMenuItem mntmNewMenuItem = new JMenuItem("Add Points");
		mntmNewMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addPointsButt();
			}
		});
		
		JMenuItem mntmNewMenuItem_4 = new JMenuItem("Clear Geometry");
		mntmNewMenuItem_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clearGeomButt();
			}
		});
		mnNewMenu_1.add(mntmNewMenuItem_4);
		mnNewMenu_1.add(mntmNewMenuItem);
		
		JMenuItem mntmNewMenuItem_1 = new JMenuItem("Add BSpline Curve");
		mntmNewMenuItem_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addCurveButt();
			}
		});
		
		JMenuItem mntmNewMenuItem_5 = new JMenuItem("Add Line");
		mntmNewMenuItem_5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addLineButt();
			}
		});
		mnNewMenu_1.add(mntmNewMenuItem_5);
		mnNewMenu_1.add(mntmNewMenuItem_1);
		
		JMenuItem mntmNewMenuItem_3 = new JMenuItem("Add NURB Curve");
		mntmNewMenuItem_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addNURBCurveButt();
			}
		});
		mnNewMenu_1.add(mntmNewMenuItem_3);
		
		JMenuItem mntmNewMenuItem_9 = new JMenuItem("Add Bounded Surface");
		mntmNewMenuItem_9.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addBoundedSurfaceButt();
			}
		});
		
		JMenuItem mntmNewMenuItem_20 = new JMenuItem("Add 3-point Arc");
		mntmNewMenuItem_20.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addNURBArcButt();
			}
		});
		mnNewMenu_1.add(mntmNewMenuItem_20);
		
		JMenuItem mntmNewMenuItem_23 = new JMenuItem("Add Offset Curve");
		mntmNewMenuItem_23.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addOffsetCurveButt();
			}
		});
		mnNewMenu_1.add(mntmNewMenuItem_23);
		
		JMenuItem mntmNewMenuItem_2 = new JMenuItem("Add BSpline Surface");
		mntmNewMenuItem_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addSurfButt();
			}
		});
		mnNewMenu_1.add(mntmNewMenuItem_2);
		mnNewMenu_1.add(mntmNewMenuItem_9);
		
		JMenuItem mntmAddLineHull = new JMenuItem("Add Line Hull");
		mntmAddLineHull.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addLineHullButt();
			}
		});
		mnNewMenu_1.add(mntmAddLineHull);
		
		JMenu mnNewMenu_2 = new JMenu("Geom Constraints");
		menuBar.add(mnNewMenu_2);
		
		JMenuItem mntmNewMenuItem_6 = new JMenuItem("Add ISO-X Constraint");
		mntmNewMenuItem_6.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addISOXConstraintButt();
			}
		});
		mnNewMenu_2.add(mntmNewMenuItem_6);
		
		JMenuItem mntmNewMenuItem_7 = new JMenuItem("Add ISO-Y Constraint");
		mntmNewMenuItem_7.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addISOYConstraintButt();
			}
		});
		mnNewMenu_2.add(mntmNewMenuItem_7);
		
		JMenuItem mntmNewMenuItem_8 = new JMenuItem("Add 2D Length Constraint");
		mntmNewMenuItem_8.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addLength2DConstraintButt();
			}
		});
		mnNewMenu_2.add(mntmNewMenuItem_8);
		
		JMenuItem mntmNewMenuItem_10 = new JMenuItem("Add Point-On-Curve Constraint");
		mntmNewMenuItem_10.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addPointOnCurveConstraintButt();
			}
		});
		
		JMenuItem mntmNewMenuItem_29 = new JMenuItem("Add Point-At-Location Constraint");
		mntmNewMenuItem_29.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addPointAtLocationConstraintButt();
			}
		});
		mnNewMenu_2.add(mntmNewMenuItem_29);
		mnNewMenu_2.add(mntmNewMenuItem_10);
		
		JMenu mnNewMenu_9 = new JMenu("Materials");
		menuBar.add(mnNewMenu_9);
		
		JMenuItem mntmNewMenuItem_19 = new JMenuItem("Update Mesh Material Properties");
		mntmNewMenuItem_19.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateMeshMaterialProperties();
			}
		});
		
		JMenuItem mntmNewMenuItem_28 = new JMenuItem("Material Menu");
		mntmNewMenuItem_28.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openMaterialMenu();
			}
		});
		mnNewMenu_9.add(mntmNewMenuItem_28);
		mnNewMenu_9.add(mntmNewMenuItem_19);
		
		JMenu mnNewMenu_3 = new JMenu("Analysis");
		menuBar.add(mnNewMenu_3);
		
		JMenu mnNewMenu_5 = new JMenu("Meshes");
		mnNewMenu_3.add(mnNewMenu_5);
		
		JMenuItem mntmNewMenuItem_24 = new JMenuItem("Add Curve Size Condition");
		mntmNewMenuItem_24.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addCurveSizeConditionButt();
			}
		});
		mnNewMenu_5.add(mntmNewMenuItem_24);
		
		JMenuItem mntmNewMenuItem_11 = new JMenuItem("Create Thermal Mesh from Curves");
		mnNewMenu_5.add(mntmNewMenuItem_11);
		
		JMenuItem mntmNewMenuItem_12 = new JMenuItem("Update Analysis Meshes");
		mnNewMenu_5.add(mntmNewMenuItem_12);
		mntmNewMenuItem_12.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateMeshes();
			}
		});
		mntmNewMenuItem_11.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createThermalMeshFromCurves();
			}
		});
		
		JMenu mnNewMenu_6 = new JMenu("Boundary Conditions");
		mnNewMenu_3.add(mnNewMenu_6);
		
		JMenuItem mntmNewMenuItem_13 = new JMenuItem("Add Node Temperature BC");
		mnNewMenu_6.add(mntmNewMenuItem_13);
		
		JMenuItem mntmNewMenuItem_14 = new JMenuItem("Add Curve Temperature BC");
		mnNewMenu_6.add(mntmNewMenuItem_14);
		
		JMenuItem mntmNewMenuItem_18 = new JMenuItem("Add Curve Htc BC");
		mnNewMenu_6.add(mntmNewMenuItem_18);
		
		JMenuItem mntmNewMenuItem_21 = new JMenuItem("Add Curve Flux BC");
		mntmNewMenuItem_21.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addCurveFluxBC();
			}
		});
		mnNewMenu_6.add(mntmNewMenuItem_21);
		
		JMenuItem mntmNewMenuItem_27 = new JMenuItem("Add Black Body Load On Curve BC");
		mntmNewMenuItem_27.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addBBRadOnCurve();
			}
		});
		mnNewMenu_6.add(mntmNewMenuItem_27);
		
		JMenuItem mntmRemoveCurveBc = new JMenuItem("Remove Curve BC");
		mntmRemoveCurveBc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeCurveBC();
			}
		});
		mnNewMenu_6.add(mntmRemoveCurveBc);
		
		JMenuItem mntmNewMenuItem_15 = new JMenuItem("Solve Analysis");
		mnNewMenu_3.add(mntmNewMenuItem_15);
		
		JMenuItem mntmSolveTransient = new JMenuItem("Solve Transient");
		mntmSolveTransient.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				solveTransient();
			}
		});
		mnNewMenu_3.add(mntmSolveTransient);
		mntmNewMenuItem_15.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				solveMeshes();
			}
		});
		mntmNewMenuItem_18.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addCurveHtcBC();
			}
		});
		mntmNewMenuItem_14.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addCurveTempBC();
			}
		});
		mntmNewMenuItem_13.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addNodeTempBC();
			}
		});
		
		JMenu mnNewMenu_4 = new JMenu("View");
		menuBar.add(mnNewMenu_4);
		
		JMenu mnNewMenu_7 = new JMenu("Contor Pallette");
		mnNewMenu_4.add(mnNewMenu_7);
		
		redblue_radbutt = new JRadioButton("Red-Blue Continuous");
		redblue_radbutt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				contRadioButtHit();
			}
		});
		redblue_radbutt.setSelected(true);
		mnNewMenu_7.add(redblue_radbutt);
		
		firefight_radbutt = new JRadioButton("Fire Fight");
		firefight_radbutt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				contRadioButtHit();
			}
		});
		mnNewMenu_7.add(firefight_radbutt);
		
		fallforward_radbutt = new JRadioButton("Fall Forward");
		fallforward_radbutt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				contRadioButtHit();
			}
		});
		mnNewMenu_7.add(fallforward_radbutt);
		
		JMenu mnNewMenu_8 = new JMenu("Contour Plot");
		mnNewMenu_4.add(mnNewMenu_8);
		
		cplotsolution_radbutt = new JRadioButton("Plot Solution");
		cplotsolution_radbutt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cplotRadioButtHit();
			}
		});
		
		cplotnone_radbutt = new JRadioButton("None");
		cplotnone_radbutt.setSelected(true);
		cplotnone_radbutt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cplotRadioButtHit();
			}
		});
		mnNewMenu_8.add(cplotnone_radbutt);
		mnNewMenu_8.add(cplotsolution_radbutt);
		
		cplotstiffness_radbutt = new JRadioButton("Plot Node Stiffness");
		cplotstiffness_radbutt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cplotRadioButtHit();
			}
		});
		mnNewMenu_8.add(cplotstiffness_radbutt);
		
		nodequery_checkbox = new JCheckBox("Query Node Mode");
		nodequery_checkbox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				queryNodeCheckChanged();
			}
		});
		mnNewMenu_4.add(nodequery_checkbox);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 580, 1035, 162);
		contentPane.add(scrollPane);
		
		scrollPane.setViewportView(outwindow);
		
		dynomaticRenderer = new JDynomaticRenderer(geom_manager,outwindow);
		dynomaticRenderer.setBounds(187, 11, 858, 558);
		contentPane.add(dynomaticRenderer);
		
		contpallete_radgroup = new ButtonGroup();
		contpallete_radgroup.add(redblue_radbutt);
		contpallete_radgroup.add(firefight_radbutt);
		contpallete_radgroup.add(fallforward_radbutt);
		
		contplot_radgroup = new ButtonGroup();
		contplot_radgroup.add(cplotnone_radbutt);
		contplot_radgroup.add(cplotsolution_radbutt);
		contplot_radgroup.add(cplotstiffness_radbutt);
		setSizes();

		//read the config file
		readConfigFile();
	}
	//this function resizes the render and message window depending on frame size
	private void setSizes() {
		int frame_width = this.getWidth();
		int frame_height = this.getHeight();
		int left_offset = 15;
		int top_offset = 15;
		int right_offset = 190;
		int render_bottom_offset = 250; //bottom of the renderer
		dynomaticRenderer.setBounds(left_offset, top_offset, (frame_width-(right_offset+left_offset)), (frame_height-render_bottom_offset));
		int scroll_height = frame_height - (frame_height-render_bottom_offset) - 7*top_offset;
		scrollPane.setBounds(left_offset,(frame_height-render_bottom_offset)+2*top_offset,frame_width-3*left_offset,scroll_height);
		ent_scrollpane.setBounds((frame_width-(right_offset+left_offset))+20,top_offset,frame_width-((frame_width-(right_offset+left_offset))+20)-20,(frame_height-render_bottom_offset)+1);
	}
	private void addPointsButt() {
		dynomaticRenderer.addPoints();
	}
	private void addCurveButt() {
		dynomaticRenderer.addBCurve();
	}
	private void addSurfButt() {
		dynomaticRenderer.addBSurface();
	}
	private void addNURBCurveButt() {
		dynomaticRenderer.addNCurve();
	}
	private void addLineButt() {
		dynomaticRenderer.addLine();
	}
	private void addNURBArcButt() {
		dynomaticRenderer.add3PArc();
	}
	private void addOffsetCurveButt() {
		dynomaticRenderer.addOffsetCurve();
	}
	private void clearGeomButt() {
		outwindow.append("Clearing geometry\n");
		geom_manager.clear();
		readConfigFile();
	}
	private void addISOXConstraintButt() {
		dynomaticRenderer.addISOXConstraint();
	}
	private void addISOYConstraintButt() {
		dynomaticRenderer.addISOYConstraint();
	}
	private void addLength2DConstraintButt() {
		dynomaticRenderer.addLength2DConstraint();
	}
	private void addBoundedSurfaceButt() {
		dynomaticRenderer.addBoundedSurface();
	}
	private void addLineHullButt() {
		dynomaticRenderer.addLineHull();
	}
	private void addPointOnCurveConstraintButt() {
		dynomaticRenderer.addPointOnCurveConstraint();
	}
	private void addPointAtLocationConstraintButt() {
		dynomaticRenderer.addPointAtLocationConstraint();
	}
	private void addCurveSizeConditionButt() {
		dynomaticRenderer.addCurveSizeCondition();
	}
	private void createThermalMeshFromCurves() {
		dynomaticRenderer.addThermalMeshFromCurves();
	}
	private void updateMeshes() {
		outwindow.append("Updating analysis meshes!\n");
		geom_manager.updateMeshes();
	}
	private void addNodeTempBC() {
		dynomaticRenderer.addNodeTemperatureBC();
	}
	private void addCurveTempBC() {
		dynomaticRenderer.addCurveTemperatureBC();
	}
	private void addBBRadOnCurve() {
		dynomaticRenderer.addBBRadOnCurve();
	}
	private void removeCurveBC() {
		dynomaticRenderer.removeCurveBC();
	}
	private void solveMeshes() {
		geom_manager.solveMeshes();
	}
	private void solveTransient() {
		geom_manager.solveTransient();
	}
	private void queryNodeCheckChanged() {
		dynomaticRenderer.setSelectNodeSolution(nodequery_checkbox.isSelected());
	}
	private void removePoints() {
		dynomaticRenderer.removePoints();
	}
	private void removeLineHull() {
		dynomaticRenderer.removeLineHull();
	}
	private void extractImageEdge() {
		for(int i=0;i<geom_manager.hSurfCnt();i++) {
			ArrayList<Point3D> npts = ImageProcessing.extractImageEdge(geom_manager.hsrfAt(i),dynomaticRenderer.getImage());
			for(int j=0;j<npts.size();j++) {
				geom_manager.addPnt(npts.get(j));
			}
		}
		dynomaticRenderer.invalidate();
		dynomaticRenderer.repaint();
	}
	private void extractImageEdgeRGB() {
		for(int i=0;i<geom_manager.hSurfCnt();i++) {
			ArrayList<Point3D> npts = ImageProcessing.extractImageEdgeRGB(geom_manager.hsrfAt(i),dynomaticRenderer.getImage());
			for(int j=0;j<npts.size();j++) {
				geom_manager.addPnt(npts.get(j));
			}
		}
		dynomaticRenderer.invalidate();
		dynomaticRenderer.repaint();
	}	
	private void contRadioButtHit() {
		if(redblue_radbutt.isSelected())
			dynomaticRenderer.setContour(RendererOptionBank.CONT_CONTINUOUS_REDBLUE);
		if(firefight_radbutt.isSelected())
			dynomaticRenderer.setContour(RendererOptionBank.CONT_DESCRETE_FIREFIGHT);
		if(fallforward_radbutt.isSelected())
			dynomaticRenderer.setContour(RendererOptionBank.CONT_DESCRETE_FALLFORWARD);
		dynomaticRenderer.invalidate();
		dynomaticRenderer.repaint();
	}
	private void cplotRadioButtHit() {
		if(cplotnone_radbutt.isSelected()) {
			dynomaticRenderer.setPaintNodeSolution(false);
			dynomaticRenderer.setPaintNodeStiffness(false);
		}
		if(cplotsolution_radbutt.isSelected()) {
			dynomaticRenderer.setPaintNodeSolution(true);
			dynomaticRenderer.setPaintNodeStiffness(false);
		}
		if(this.cplotstiffness_radbutt.isSelected()) {
			dynomaticRenderer.setPaintNodeSolution(false);
			dynomaticRenderer.setPaintNodeStiffness(true);
		}
		dynomaticRenderer.invalidate();
		dynomaticRenderer.repaint();
	}
	private void saveFileButt() {
		if(filechooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			String fname = filechooser.getSelectedFile().getAbsolutePath();
			JDOFileIO.writeJDOFile(fname, geom_manager, outwindow);
		}
	}
	private void readFileButt() {
		//JDOFileIO.readJDOFile("TestFile.jdo", geom_manager, outwindow);
		//JDOFileIO.readJDOFile("/home/tomslavens/MessupSave", geom_manager, outwindow);
		if(filechooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			String fname = filechooser.getSelectedFile().getAbsolutePath();
			JDOFileIO.readJDOFile(fname, geom_manager, outwindow);
		}		
	}
	private void exportPoints() {
		if(filechooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			String fname = filechooser.getSelectedFile().getAbsolutePath();
			JDOFileIO.writePointFile(fname, geom_manager, outwindow);
		}
	}
	private void exportLineHulls() {
		if(filechooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			String fname = filechooser.getSelectedFile().getAbsolutePath();
			JDOFileIO.writeLineHullFile(fname, geom_manager, dynomaticRenderer, outwindow);
		}
	}
	private void addCurveHtcBC() {
		dynomaticRenderer.addCurveHtcBC();
	}
	private void addCurveFluxBC() {
		dynomaticRenderer.addCurveFluxBC();
	}
	private void openMaterialMenu() {
		MaterialMenu dial = new MaterialMenu(geom_manager,outwindow);
		dial.setVisible(true);
	}
	private void updateMeshMaterialProperties() {
		geom_manager.updateMeshMaterialProperties();
	}
	private void systemSetupButt() {
		SystemParametersMenu spm = new SystemParametersMenu();
		spm.corecnt_box.setSelectedIndex(core_targ-1);
		spm.multcore_checkbox.setSelected(mult_core);
		spm.setVisible(true);
		if(spm.ok_hit) {
			core_targ = spm.corecnt_box.getSelectedIndex()+1;
			mult_core = spm.multcore_checkbox.isSelected();
		}
	}
	private void importImageButt() {
		ImportImageDialog dial = new ImportImageDialog(outwindow);
		dial.setVisible(true);
		if(dial.ok_hit) {
			dynomaticRenderer.setImage(dial.dyno);
			dynomaticRenderer.invalidate();
			dynomaticRenderer.repaint();
		}
	}
	private void clearImageButt() {
		dynomaticRenderer.setImage(new DynoImage());
		dynomaticRenderer.invalidate();
		dynomaticRenderer.repaint();
	}
	private void entListSelect() {
		if(ent_list.getSelectedIndex() >= 0) {
			DefaultListModel dlm = (DefaultListModel) ent_list.getModel();
			String item = (String) dlm.getElementAt(ent_list.getSelectedIndex());
			int tag = Integer.valueOf(item.split("[()]")[1]);
			Entity ent = geom_manager.getEntity(tag);
			MainWindowFunctions.entListSelection(ent, geom_manager, dynomaticRenderer);
		}
	}
	private void keyPressedAction(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			//outwindow.append("Esc pressed\n");
			dynomaticRenderer.clearAddFlags();
			dynomaticRenderer.clearBuffers();
			dynomaticRenderer.invalidate();
			dynomaticRenderer.repaint();
		}
		if(e.getKeyChar() == 'a') {
			dynomaticRenderer.setAPress(true);
		}
	}
	private void keyReleasedAction(KeyEvent e) {
		if(e.getKeyChar() == 'a') {
			dynomaticRenderer.setAPress(false);
		}
	}
	private void readConfigFile() {
		Material nullmat = new Material();
		geom_manager.addMaterial(nullmat);
		try {
			BufferedReader br = new BufferedReader(new FileReader("jdynoconfig.txt"));
			//each line is a link to a material file (right now)
			String line = br.readLine();
			while(line != null) {
				outwindow.append("Reading material file: " + line + "\n");
				BufferedReader matfile = new BufferedReader(new FileReader(line));
				String cline = matfile.readLine();
				String outline = "";
				while(cline != null) {
					outline = outline + cline +"\n";
					cline = matfile.readLine();
				}
				matfile.close();
				Material mat = new Material(outline);
				geom_manager.addMaterial(mat);
				line = br.readLine();
			}
			br.close();
		} catch(Exception E) {
			outwindow.append("Could not read config file!\n");
			outwindow.append(E.toString() + "\n");
		}
	}
}
