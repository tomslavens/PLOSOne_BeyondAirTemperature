package rendering;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.color.*;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import analysis.BBRadiationOnCurveBC;
import analysis.FluxOnCurveBC;
import analysis.HtcOnCurveBC;
import analysis.Node3D;
import analysis.TempOnCurveBC;
import analysis.TempOnNodeBC;
import analysis.ThermalTriMesh;
import analysis_mesh.EdgeSizeMeshCondition;
import data.Entity;
import data.GeomManager;
import geom.BSplineCurve;
import geom.BSplineSurface;
import geom.BoundedSurface;
import geom.Line3D;
import geom.LineHull;
import geom.NURBArc;
import geom.NURBCurve;
import geom.OffsetCurve;
import geom.ParamCurve;
import geom.Point3D;
import geom_funcs.ISOXConstraint;
import geom_funcs.ISOYConstraint;
import geom_funcs.Length2DConstraint;
import geom_funcs.PointAtLocationConstraint;
import geom_funcs.PointOnCurveConstraint;
import gui.Add3PointArcDialog;
import gui.AddBBRadOnCurveBCDialog;
import gui.AddFluxOnCurveBCDialog;
import gui.AddHtcOnCurveBCDialog;
import gui.AddOffsetCurveDialog;
import gui.AddTempOnCurveBCDialog;
import gui.AddTempOnNodeBCDialog;
import gui.EdgeSizeConditionDialog;
import gui.ImportImageDialog;
import gui.PointAtLocDialog;
import gui.RemoveCurveBCDialog;
import gui.ThermalTriMeshDialog;
import images.DynoImage;
import linear_math.Vector3D;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class JDynomaticRenderer extends JPanel {

	//private ArrayList<Point3D> pts;
	//private ArrayList<ParamCurve> splines;
	//private ArrayList<BSplineSurface> surfs;
	protected GeomManager geom_manager;
	protected double xmin;     // min x in our view
	protected double xmax;     // max x in our view
	protected double ymin;     // min y in our view
	protected double ymax;     // max y in our view
	protected int wwidth;   // the window width associated with xrange
	protected int wheight;  // the window height associated with yrange
	// editing flags
	protected boolean addPts;   //add pts flag
	protected boolean addBCrvs;  //add spline flag
	protected boolean addBSrfs;  //add surface flag
	protected boolean addNCrvs;  //add NURB curve flag
	protected boolean addLine;  //add Line flag
	protected boolean add3PArc; //add a NURB Arc
	protected boolean addOCurve; //add an offset Curve
	protected boolean addBoundedSurface;     //add a curve-bounded surface
	protected boolean addLineHull;     //add a LineHull surface
	protected boolean addISOXConstraint; //add an iso-X constraint to a line
	protected boolean addISOYConstraint; //add an iso-Y constraint to a line
	protected boolean addLength2DConstraint; //add a 2D constraint in the plane of view (X-Y)
	protected boolean addPointOnCurveConstraint; //adds a point on a curve constraint
	protected boolean addPointAtLocationConstraint; //adds a point on at a location constraint
	protected boolean addCurveSizeCondition;    //adds a element size constraint to a curve
	protected boolean addThermalMeshFromCurves; //adds a thermal mesh from a curve set
	protected boolean addNodeTemperatureBC;  //adds a temperature constraint to a mesh node
	protected boolean addCurveTemperatureBC; //adds a temperature constraint to geometry curves
	protected boolean addCurveHtcBC; //adds a Htc constraint to geometry curves
	protected boolean addCurveFluxBC; //adds a Htc constraint to geometry curves
	protected boolean addBBRadOnCurve; //adds a black body radiation load on to a curve
	protected boolean removeCurveBC; //removes the BC's associated with a curve
	protected boolean removePnt; //removes all the points selected
	protected boolean removeLineHull; //removes selected LineHull
	protected int sel_index;    //index of a selected pt
	protected int mx;           //screen location of the mouse when the left mouse button is pressed
	protected int my;           //screen location of the mouse when the left mouse button is pressed
	protected ArrayList<Point3D> ptbuffer; //array for adding points when creating a curve
	protected ArrayList<ParamCurve> crvbuffer; //array for adding curves when creating a surface
	protected ArrayList<Node3D> ndbuffer; //array for adding nodes when applying BC's
	protected JTextArea outwindow;
	protected boolean paint_node_solution; //paint the node solution
	protected boolean query_node_solution; //select mesh node solution value
	protected boolean paint_node_stiffness; //paint the first var in the stiffness matrix
	protected RendererOptionBank rendoptions; //this holds color defs and our options for plotting colors
	protected DynoImage image;               // image to put on the background
	protected boolean a_pressed;             // this is a boolean flag to allow us to keep tabs on the state of letter "a"
	
	public JDynomaticRenderer(GeomManager in_manager, JTextArea in_outwindow) {
		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				if(SwingUtilities.isLeftMouseButton(e)) {
					leftMouseDragged(e);
				}
				if(SwingUtilities.isMiddleMouseButton(e)) {
					centerMouseDragged(e);
				}
			}
		});
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getButton() == MouseEvent.BUTTON1) {
					leftMouseClicked(e);
				}
				if(e.getButton() == MouseEvent.BUTTON3) {
					rightMouseClicked(e);
				}
			}
			@Override
			public void mousePressed(MouseEvent e) {
				if(e.getButton() == MouseEvent.BUTTON1) {
					leftMousePressed(e);
				}
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				if(e.getButton() == MouseEvent.BUTTON1) {
					leftMouseReleased(e);
				}
			}
		});

        // Add a ComponentListener to the JFrame
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // This method is called when the JFrame is resized
            	/*if(wwidth > 0) {
            		double rwidth = Math.abs(xmax - xmin);
            		double wscale = (double) rwidth/wwidth;
            		double rheight = Math.abs(ymax - ymin);
            		double hscale = (double) rheight/wheight;
            		int winwidth = getWidth();
            		int winheight = getHeight();
            		System.out.println("here resize");

            		xmax = xmin + (wscale*winwidth);
            		wwidth = winwidth;
            		ymax = ymin + (hscale*winheight);
            		wheight = winheight;

            		String outlinex = "<" + Double.toString(xmin) + "," + Double.toString(xmax) + ">";
            		String outliney = "<" + Double.toString(ymin) + "," + Double.toString(ymax) + ">";
            		System.out.println(outlinex);
            		System.out.println(outliney);
            	} else {
            		wwidth = getWidth();
            		wheight = getHeight();
            	}*/
            }
        });		
		
		geom_manager = in_manager;
		outwindow = in_outwindow;
		//pts = in_pts;
		//splines = in_splines;
		//surfs = in_surfs;
		xmin = 0.0;
		xmax = 10.0;
		ymin = 0.0;
		ymax = 10.0;
		wwidth = this.getWidth();
		wheight = this.getHeight();
		image = new DynoImage();
		
		addPts = false;
		addBCrvs = false;
		addBSrfs = false;
		addNCrvs = false;
		addLine = false;
		add3PArc = false;
		addOCurve = false;
		addISOXConstraint = false;
		addISOYConstraint = false;
		addLength2DConstraint = false;
		addBoundedSurface = false;
		addLineHull = false;
		addCurveSizeCondition = false;
		addThermalMeshFromCurves = false;
		addPointOnCurveConstraint = false;
		addPointAtLocationConstraint = false;
		addNodeTemperatureBC = false;
		addCurveTemperatureBC = false;
		addCurveHtcBC = false;
		addCurveFluxBC = false;
		addBBRadOnCurve = false;
		removeCurveBC = false;
		removePnt= false;
		removeLineHull = false;
		ptbuffer = new ArrayList<Point3D>();
		crvbuffer = new ArrayList<ParamCurve>();
		ndbuffer = new ArrayList<Node3D>();
		sel_index = -1;
		mx = 0;
		my = 0;
		paint_node_solution = false;
		paint_node_stiffness = false;
		query_node_solution = false;
		rendoptions = new RendererOptionBank();
		a_pressed = false;
	}
	public void addPoint(Point3D pt) {
		geom_manager.addPnt(pt);
	}
	public void addPointToPointBuffer(Point3D pt) {
		ptbuffer.add(pt);
	}
	public void clearPointBuffer() {
		ptbuffer = new ArrayList<Point3D>();
	}
	public void addBSpline(BSplineCurve spline) {
		geom_manager.addCurve(spline);
	}
	public void setPaintNodeSolution(boolean val) {
		paint_node_solution = val;
	}
	public void setSelectNodeSolution(boolean val) {
		query_node_solution = val;
		ndbuffer = new ArrayList<Node3D>();
	}
	public void setPaintNodeStiffness(boolean val) {
		paint_node_stiffness = val;
	}	
	public void setContour(int val) {
		rendoptions.cont_option = val;
	}
	public void setImage(DynoImage img) {
		image = img;
	}
	public void setAPress(boolean val) {
		a_pressed = val;
	}
	
	@Override
    protected void paintComponent(Graphics g) {
		RendererPaintMethod.paintGeometry(g, this, outwindow);
	}
	public void addPoints() {
		outwindow.append("Adding pts\n");
		addPts = true;
	}
	public void addBCurve() {
		addBCrvs = true;
		ptbuffer = new ArrayList<Point3D>();
	}
	public void addLine() {
		addLine = true;
		ptbuffer = new ArrayList<Point3D>();
	}
	public void add3PArc() {
		add3PArc = true;
		ptbuffer = new ArrayList<Point3D>();
		outwindow.append("Select two points\n");
	}
	public void addBSurface() {
		addBSrfs = true;
		crvbuffer = new ArrayList<ParamCurve>();
	}
	public void addNCurve() {
		addNCrvs = true;
		ptbuffer = new ArrayList<Point3D>();
	}
	public void addOffsetCurve() {
		addOCurve = true;
		crvbuffer = new ArrayList<ParamCurve>();
	}
	public void addISOXConstraint() {
		addISOXConstraint = true;
		crvbuffer = new ArrayList<ParamCurve>();
	}
	public void addISOYConstraint() {
		addISOYConstraint = true;
		crvbuffer = new ArrayList<ParamCurve>();
	}
	public void addLength2DConstraint() {
		addLength2DConstraint = true;
		crvbuffer = new ArrayList<ParamCurve>();
	}
	public void addBoundedSurface() {
		addBoundedSurface = true;
		crvbuffer = new ArrayList<ParamCurve>();
	}
	public void addLineHull() {
		addLineHull = true;
		crvbuffer = new ArrayList<ParamCurve>();
	}	
	public void addPointOnCurveConstraint() {
		addPointOnCurveConstraint = true; 
		ptbuffer = new ArrayList<Point3D>();
		crvbuffer = new ArrayList<ParamCurve>();
	}
	public void addPointAtLocationConstraint() {
		addPointAtLocationConstraint = true;
		ptbuffer = new ArrayList<Point3D>();
	}
	public void addCurveSizeCondition() {
		addCurveSizeCondition = true;
		crvbuffer = new ArrayList<ParamCurve>();
	}
	public void addThermalMeshFromCurves() {
		addThermalMeshFromCurves = true;
		crvbuffer = new ArrayList<ParamCurve>();
	}
	public void addNodeTemperatureBC() {
		addNodeTemperatureBC = true;
		ndbuffer = new ArrayList<Node3D>();
	}
	public void addCurveTemperatureBC() {
		addCurveTemperatureBC = true;
		crvbuffer = new ArrayList<ParamCurve>();
	}
	public void addCurveHtcBC() {
		addCurveHtcBC = true;
		crvbuffer = new ArrayList<ParamCurve>();
	}
	public void addBBRadOnCurve() {
		addBBRadOnCurve = true;
		crvbuffer = new ArrayList<ParamCurve>();
	}
	public void addCurveFluxBC() {
		addCurveFluxBC = true;
		crvbuffer = new ArrayList<ParamCurve>();
	}
	public void removeCurveBC() {
		removeCurveBC = true;
		crvbuffer = new ArrayList<ParamCurve>();
	}
	public void removePoints() {
		outwindow.append("Removing Points!\n");
		removePnt = true;
		ptbuffer = new ArrayList<Point3D>();
	}
	public void removeLineHull() {
		outwindow.append("Pick lines of the LineHull you wish to delete\n");
		removeLineHull = true;
		crvbuffer = new ArrayList<ParamCurve>();
	}

	public void clearAddFlags() {
		addPts = false;
		addLine = false;
		add3PArc =false;
		addBCrvs = false;
		addNCrvs = false;
		addOCurve = false;
		addBSrfs = false;
		addBoundedSurface = false;
		addLineHull = false;
		addCurveSizeCondition = false;
		addThermalMeshFromCurves = false;
		addISOXConstraint = false;
		addISOYConstraint = false;
		addLength2DConstraint = false;
		addPointOnCurveConstraint = false;
		addPointAtLocationConstraint = false;
		addNodeTemperatureBC = false;
		addCurveTemperatureBC = false;
		addCurveHtcBC = false;
		addCurveFluxBC = false;
		addBBRadOnCurve = false;
		removeCurveBC = false;
		removePnt = false;
		removeLineHull = false;
	}
	public boolean noFlags() {
		boolean flag = false;
		if(!addPts && !addLine && !addBCrvs && !addNCrvs && !addBSrfs && !addBoundedSurface && !addLineHull && !addThermalMeshFromCurves && !addISOXConstraint && !addISOYConstraint && !addLength2DConstraint 
				&& !addPointOnCurveConstraint && !addNodeTemperatureBC && !addCurveTemperatureBC && !addCurveHtcBC && !addCurveFluxBC && !add3PArc && !addOCurve
				&& !addCurveSizeCondition && !addBBRadOnCurve && !removeCurveBC && !removePnt && !removeLineHull && !addPointAtLocationConstraint)
			flag = true;
		return flag;
	}
	public void clearBuffers() {
		ptbuffer = new ArrayList<Point3D>();
		crvbuffer = new ArrayList<ParamCurve>();	
	}
	private void leftMouseClicked(MouseEvent e) {
		if(e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1 && noFlags()) {
			outwindow.append("DOUBLE CLICK!\n");
			leftMouseDoubleClick(e);
		}
		
		int fme_width = this.getWidth();
		int fme_height = this.getHeight();
		double xscale = (double) fme_width/(xmax-xmin);
		double yscale = (double) fme_height/(ymax-ymin);
		int cx = e.getX();
		int cy = e.getY();
		
		double px = cx/xscale + xmin;
		double py = ymax - cy/yscale;
		//String output = "<" + Double.toString(px) + "," + Double.toString(py) + ">";
		//System.out.println(output);
		
		//String output = "Closest point @" + Integer.toString(nearpt);
		//System.out.println(output);		
		
		if(addPts) {
			Point3D cpt = new Point3D(px,py,0.0);
			geom_manager.addPnt(cpt);
		}
		if(addLine) {
			int nearpt = getClosestPoint(px,py);
			if(nearpt >= 0) {
				Point3D cpt = geom_manager.pntAt(nearpt);
				ptbuffer.add(cpt);
			}
		}
		if(add3PArc) {
			int nearpt = getClosestPoint(px,py);
			if(nearpt >= 0) {
				Point3D cpt = geom_manager.pntAt(nearpt);
				ptbuffer.add(cpt);
			}
		}
		if(addBCrvs) {
			int nearpt = getClosestPoint(px,py);
			if(nearpt >= 0) {
				Point3D cpt = geom_manager.pntAt(nearpt);
				ptbuffer.add(cpt);
			}
		}
		if(removePnt) {
			int nearpt = getClosestPoint(px,py);
			if(nearpt >= 0) {
				Point3D cpt = geom_manager.pntAt(nearpt);
				ptbuffer.add(cpt);
			}
		}		
		if(addNCrvs) {
			int nearpt = getClosestPoint(px,py);
			if(nearpt >= 0) {
				Point3D cpt = geom_manager.pntAt(nearpt);
				ptbuffer.add(cpt);
			}
		}
		if(addOCurve) {
			int nearcrv = getClosestCurve(px, py);
			if(nearcrv >=0) {
				outwindow.append("Curve @ " + Integer.toString(nearcrv) + "\n");
				if(geom_manager.crvAt(nearcrv).type == 1)
					crvbuffer.add(geom_manager.crvAt(nearcrv));
			}				
		}
		if(addBSrfs) {
			int nearcrv = getClosestCurve(px, py);
			if(nearcrv >=0) {
				outwindow.append("Curve @ " + Integer.toString(nearcrv) + "\n");
				//BSplineCurve ccrv = splines.get(nearcrv);  //before NURBS implementation
				if(geom_manager.crvAt(nearcrv).type == 1)
					crvbuffer.add(geom_manager.crvAt(nearcrv));
			}			
		}
		if(addBoundedSurface) {
			int nearcrv = getClosestCurve(px, py);
			if(nearcrv >=0) {
				outwindow.append("Curve @ " + Integer.toString(nearcrv) + "\n");
				//BSplineCurve ccrv = splines.get(nearcrv);  //before NURBS implementation
				crvbuffer.add(geom_manager.crvAt(nearcrv));
			}
		}
		if(addLineHull) {
			// adjusted to add a point instead of picking
			Point3D cpt = new Point3D(px,py,0.0);
			geom_manager.addPnt(cpt);
			ptbuffer.add(cpt);
			if(ptbuffer.size() > 2) {
				Line3D cline = new Line3D(cpt,ptbuffer.get(ptbuffer.size()-2));
				crvbuffer.add(cline);
			}
			/*int nearpt = getClosestPoint(px,py);
			if(nearpt >= 0) {
				Point3D cpt = geom_manager.pntAt(nearpt);
				ptbuffer.add(cpt);
			}*/			
		}
		if(addCurveSizeCondition) {
			int nearcrv = getClosestCurve(px, py);
			if(nearcrv >=0) {
				outwindow.append("Curve @ " + Integer.toString(nearcrv) + "\n");
				crvbuffer.add(geom_manager.crvAt(nearcrv));
			}
		}
		if(addThermalMeshFromCurves) {
			int nearcrv = getClosestCurve(px, py);
			if(nearcrv >=0) {
				outwindow.append("Curve @ " + Integer.toString(nearcrv) + "\n");
				crvbuffer.add(geom_manager.crvAt(nearcrv));
			}
		}
		//constraint applications
		if(addISOXConstraint) {
			int nearline = getClosestLine(px,py);
			if(nearline >=0) {
				outwindow.append("Curve @ " + Integer.toString(nearline) + "\n");
				if(geom_manager.crvAt(nearline).curvetype == ParamCurve.LINE3D) {
					addISOXConstraint = false;
					ISOXConstraint constraint = new ISOXConstraint((Line3D) geom_manager.crvAt(nearline));
					geom_manager.addConstraint(constraint);
				}
			}
		}
		if(addISOYConstraint) {
			int nearline = getClosestLine(px,py);
			if(nearline >=0) {
				outwindow.append("Curve @ " + Integer.toString(nearline) + "\n");
				if(geom_manager.crvAt(nearline).curvetype == ParamCurve.LINE3D) {
					addISOYConstraint = false;
					ISOYConstraint constraint = new ISOYConstraint((Line3D) geom_manager.crvAt(nearline));
					geom_manager.addConstraint(constraint);
				}
			}
		}
		if(addLength2DConstraint) {
			int nearline = getClosestLine(px,py);
			if(nearline >=0) {
				outwindow.append("Curve @ " + Integer.toString(nearline) + "\n");
				if(geom_manager.crvAt(nearline).curvetype == ParamCurve.LINE3D) {
					addLength2DConstraint = false;
					Vector3D vect = new Vector3D((Line3D) geom_manager.crvAt(nearline));
					Length2DConstraint constraint = new Length2DConstraint((Line3D) geom_manager.crvAt(nearline), vect);
					geom_manager.addConstraint(constraint);
				}
			}
		}
		if(addPointOnCurveConstraint) {
			if(ptbuffer.size() == 0) { //first select a point
				int nearpt = getClosestPoint(px,py);
				if(nearpt >= 0) {
					Point3D cpt = geom_manager.pntAt(nearpt);
					ptbuffer.add(cpt);
					outwindow.append("Select location on target curve to restrain to");
				}
			} else {
				int nearcrv = getClosestCurve(px, py);
				if(nearcrv >=0) {
					outwindow.append("Curve @ " + Integer.toString(nearcrv) + "\n");
					addPointOnCurveConstraint = false;
					double param = getClosestParam(geom_manager.crvAt(nearcrv), px, py);
					PointOnCurveConstraint poc = new PointOnCurveConstraint(ptbuffer.get(0),geom_manager.crvAt(nearcrv),param);
					clearAddFlags();
					geom_manager.addConstraint(poc);
					ptbuffer = new ArrayList<Point3D>();
					crvbuffer = new ArrayList<ParamCurve>();
				}
			}
		}
		if(addPointAtLocationConstraint) {
			int nearpt = getClosestPoint(px,py);
			if(nearpt >= 0) {
				Point3D cpt = geom_manager.pntAt(nearpt);
				ptbuffer.add(cpt);
			}
		}
		if(addNodeTemperatureBC) {
			Node3D nearnode = getClosestNode(px,py);
			if(nearnode != null) {
				ndbuffer.add(nearnode);
			}
		}
		if(addCurveTemperatureBC) {
			int nearcrv = getClosestCurve(px, py);
			if(nearcrv >=0) {
				outwindow.append("Curve @ " + Integer.toString(nearcrv) + "\n");
				crvbuffer.add(geom_manager.crvAt(nearcrv));
			}			
		}
		if(addCurveHtcBC) {
			int nearcrv = getClosestCurve(px, py);
			if(nearcrv >=0) {
				outwindow.append("Curve @ " + Integer.toString(nearcrv) + "\n");
				crvbuffer.add(geom_manager.crvAt(nearcrv));
			}
		}
		if(addBBRadOnCurve) {
			int nearcrv = getClosestCurve(px, py);
			if(nearcrv >=0) {
				outwindow.append("Curve @ " + Integer.toString(nearcrv) + "\n");
				crvbuffer.add(geom_manager.crvAt(nearcrv));
			}
		}
		if(addCurveFluxBC) {
			int nearcrv = getClosestCurve(px, py);
			if(nearcrv >=0) {
				outwindow.append("Curve @ " + Integer.toString(nearcrv) + "\n");
				crvbuffer.add(geom_manager.crvAt(nearcrv));
			}
		}
		if(removeCurveBC) {
			int nearcrv = getClosestCurve(px, py);
			if(nearcrv >=0) {
				outwindow.append("Curve @ " + Integer.toString(nearcrv) + "\n");
				crvbuffer.add(geom_manager.crvAt(nearcrv));
			}
		}
		if(removeLineHull) {
			int nearcrv = getClosestCurve(px, py);
			if(nearcrv >=0) {
				outwindow.append("Curve @ " + Integer.toString(nearcrv) + "\n");
				crvbuffer.add(geom_manager.crvAt(nearcrv));
			}
		}
		if(query_node_solution) {
			Node3D nearnode = getClosestNode(px,py);
			if(nearnode != null) {
				ndbuffer.add(nearnode);
			}
		}
		geom_manager.updateGeometry();
		this.invalidate();
		this.repaint();
	}
	private void rightMouseClicked(MouseEvent e) {		
		if(addPts) {
			outwindow.append("Done adding pts\n");
			clearAddFlags();
		}	
		if(addLine) {
			clearAddFlags();
			if(ptbuffer.size() >= 2) {
				Line3D cline = new Line3D(ptbuffer.get(0),ptbuffer.get(1));
				geom_manager.addCurve(cline);
			}
			ptbuffer = new ArrayList<Point3D>();
		}
		if(addBCrvs) {
			clearAddFlags();
			Point3D[] cpts = new Point3D[ptbuffer.size()];
			for(int i=0;i<cpts.length;i++) {
				cpts[i] = ptbuffer.get(i);
			}
			BSplineCurve new_curve = BSplineCurve.createUniformOpenBSpline(cpts.length-1, cpts, outwindow);
			geom_manager.addCurve(new_curve);
			ptbuffer = new ArrayList<Point3D>();
		}
		if(addOCurve) {
			clearAddFlags();
			ParamCurve target = crvbuffer.get(0);
			AddOffsetCurveDialog dial = new AddOffsetCurveDialog(target,geom_manager,this);
			dial.setVisible(true);
			if(dial.ok_hit) {
				double xyoffset = Double.valueOf(dial.xy_field.getText());
				double xzoffset = Double.valueOf(dial.xz_field.getText());
				double yzoffset = Double.valueOf(dial.yz_field.getText());
				OffsetCurve ocurve = new OffsetCurve(target,xyoffset,xzoffset,yzoffset);
				geom_manager.addCurve(ocurve);
			}
			crvbuffer = new ArrayList<ParamCurve>();
		}
		if(addNCrvs) {
			clearAddFlags();
			Point3D[] cpts = new Point3D[ptbuffer.size()];
			for(int i=0;i<cpts.length;i++) {
				cpts[i] = ptbuffer.get(i);
			}
			NURBCurve new_curve = NURBCurve.createUniformOpenNURBCurve(cpts.length-1, cpts, outwindow);
			geom_manager.addCurve(new_curve);
			ptbuffer = new ArrayList<Point3D>();
		}
		if(add3PArc) {
			clearAddFlags();
			if(ptbuffer.size() == 1) {
				Add3PointArcDialog dial = new Add3PointArcDialog(ptbuffer.get(0),ptbuffer.get(1),geom_manager,this);
				dial.setVisible(true);
				if(dial.ok_hit) {
					double radius = Double.valueOf(dial.temp_field.getText());
					NURBArc arc = new NURBArc(ptbuffer.get(0),ptbuffer.get(1),radius,dial.dir);
					geom_manager.addCurve(arc);
				}
			}
			ptbuffer = new ArrayList<Point3D>();
		}
		if(addBSrfs) {
			clearAddFlags();
			ArrayList<BSplineCurve> bcrvbuffer = new ArrayList<BSplineCurve>();
			for(int i=0;i<crvbuffer.size();i++) {
				bcrvbuffer.add((BSplineCurve) crvbuffer.get(i));
			}
			BSplineSurface surf = new BSplineSurface(bcrvbuffer);
			geom_manager.addBSplineSurface(surf);
			crvbuffer = new ArrayList<ParamCurve>();
		}
		if(addBoundedSurface) {
			clearAddFlags();
			BoundedSurface tsurf = BoundedSurface.createBoundedSurface(crvbuffer);
			geom_manager.addBoundedSurface(tsurf);
			crvbuffer = new ArrayList<ParamCurve>();
		}
		if(addLineHull) {
			clearAddFlags();
			LineHull tsurf = LineHull.createLineHullParam(ptbuffer,geom_manager);
			geom_manager.addLineHull(tsurf);
			ptbuffer = new ArrayList<Point3D>();
			crvbuffer = new ArrayList<ParamCurve>();
		}
		if(addCurveSizeCondition) {
			clearAddFlags();
			EdgeSizeConditionDialog dial = new EdgeSizeConditionDialog(crvbuffer, this);
			dial.setVisible(true);
			if(dial.ok_hit) {
				double edge_size = Double.valueOf(dial.length_field.getText());
				EdgeSizeMeshCondition mc = new EdgeSizeMeshCondition(crvbuffer,edge_size);
				geom_manager.addMC(mc);
			}
			crvbuffer = new ArrayList<ParamCurve>();
		}
		if(addPointAtLocationConstraint) {
			clearAddFlags();
			if(ptbuffer.size() > 0) {
				PointAtLocDialog dial = new PointAtLocDialog();
				dial.setFields(ptbuffer.get(0));
				dial.setVisible(true);
				if(dial.ok_hit) {
					double x = Double.valueOf(dial.x_field.getText());
					double y = Double.valueOf(dial.y_field.getText());
					double z = Double.valueOf(dial.z_field.getText());
					PointAtLocationConstraint palc = new PointAtLocationConstraint(ptbuffer.get(0),x,y,z);
					geom_manager.addConstraint(palc);
				}
			}
			ptbuffer = new ArrayList<Point3D>();
		}
		if(addThermalMeshFromCurves) {
			clearAddFlags();
			ThermalTriMeshDialog dial = new ThermalTriMeshDialog(geom_manager);
			dial.setVisible(true);
			if(dial.ok_hit) {
				double edge_size = Double.valueOf(dial.elem_field.getText());
				outwindow.append("Creating thermal mesh from curves!\n");
				outwindow.append("Element size is: " + Double.toString(edge_size) + "\n");
				ThermalTriMesh mesh = ThermalTriMesh.createThermalTriMesh(edge_size, crvbuffer,geom_manager);
				mesh.material = dial.mat_combo.getSelectedIndex();
				geom_manager.addAnalysisMesh(mesh);
				outwindow.append("Completed mesh generation\n");
			}
			crvbuffer = new ArrayList<ParamCurve>();
		}
		if(addNodeTemperatureBC) {
			clearAddFlags();
			AddTempOnNodeBCDialog dial = new AddTempOnNodeBCDialog();
			dial.setVisible(true);
			if(dial.ok_hit) {
				double temp = Double.valueOf(dial.temp_field.getText());
				outwindow.append("Applying temperature of " + Double.toString(temp) + "F to nodes!\n");
				TempOnNodeBC bc = new TempOnNodeBC(temp);
				for(int i=0;i<ndbuffer.size();i++) {
					bc.entities.add(ndbuffer.get(i));
				}
				geom_manager.addBC(bc);
			}
			ndbuffer = new ArrayList<Node3D>();
		}
		if(addCurveTemperatureBC) {
			clearAddFlags();
			AddTempOnCurveBCDialog dial = new AddTempOnCurveBCDialog();
			dial.setVisible(true);
			if(dial.ok_hit) {
				double temp = Double.valueOf(dial.temp_field.getText());
				outwindow.append("Applying temperature of " + Double.toString(temp) + "F to curves!\n");
				TempOnCurveBC bc = new TempOnCurveBC(temp);
				for(int i=0;i<crvbuffer.size();i++) {
					bc.entities.add(crvbuffer.get(i));
				}
				geom_manager.addBC(bc);
			}
			crvbuffer = new ArrayList<ParamCurve>();
		}
		if(addCurveHtcBC) {
			clearAddFlags();
			AddHtcOnCurveBCDialog dial = new AddHtcOnCurveBCDialog();
			dial.setVisible(true);
			if(dial.ok_hit) {
				double temp = Double.valueOf(dial.temp_field.getText());
				double htc = Double.valueOf(dial.htc_field.getText());
				outwindow.append("Applying Htc of " + Double.toString(htc) + "W/m^2*K to curves!\n");
				HtcOnCurveBC bc = new HtcOnCurveBC(temp,htc);
				for(int i=0;i<crvbuffer.size();i++) {
					bc.entities.add(crvbuffer.get(i));
				}
				geom_manager.addBC(bc);
			}
			crvbuffer = new ArrayList<ParamCurve>();
		}
		if(addBBRadOnCurve) {
			clearAddFlags();
			AddBBRadOnCurveBCDialog dial = new AddBBRadOnCurveBCDialog();
			dial.setVisible(true);
			if(dial.ok_hit) {
				double temp = Double.valueOf(dial.temp_field.getText());
				double area = Double.valueOf(dial.area_field.getText());
				double absorptivity = Double.valueOf(dial.abs_field.getText());
				double srcx = Double.valueOf(dial.x_field.getText())*100; //convert to cm
				double srcy = Double.valueOf(dial.y_field.getText())*100; //convert to cm
				double srcu = Double.valueOf(dial.u_field.getText());
				double srcv = Double.valueOf(dial.v_field.getText());
				outwindow.append("Applying BB Radiation to curves!\n");
				double[] src_loc = new double[] {srcx,srcy,0.0};
				double[] src_norm = new double[] {srcu,srcv,0.0};
				BBRadiationOnCurveBC bc = new BBRadiationOnCurveBC(absorptivity,area,temp,src_loc,src_norm);
				for(int i=0;i<crvbuffer.size();i++) {
					bc.entities.add(crvbuffer.get(i));
				}
				geom_manager.addBC(bc);
			}
			crvbuffer = new ArrayList<ParamCurve>();
		}
		if(addCurveFluxBC) {
			clearAddFlags();
			AddFluxOnCurveBCDialog dial = new AddFluxOnCurveBCDialog();
			dial.setVisible(true);
			if(dial.ok_hit) {
				double qdot = Double.valueOf(dial.temp_field.getText());
				outwindow.append("Applying qdot of " + Double.toString(qdot) + "W/m^2 to curves!\n");
				FluxOnCurveBC bc = new FluxOnCurveBC(qdot);
				for(int i=0;i<crvbuffer.size();i++) {
					bc.entities.add(crvbuffer.get(i));
				}
				geom_manager.addBC(bc);
			}
			crvbuffer = new ArrayList<ParamCurve>();
		}
		if(removeCurveBC) {
			clearAddFlags();
			RemoveCurveBCDialog dial = new RemoveCurveBCDialog();
			dial.setVisible(true);
			if(dial.ok_hit) {
				for(int i=0;i<crvbuffer.size();i++) {
					ParamCurve pc = crvbuffer.get(i);
					for(int j=0;j<geom_manager.bcCnt();j++) { //iterate through all BC's
						//outwindow.append("BC : " + geom_manager.bcAt(j).name + "\n");
						geom_manager.bcAt(j).removeEntity(pc.tag); // remove this curve from the BC target
					}
				}
				geom_manager.cleanBCs();
			}
			crvbuffer = new ArrayList<ParamCurve>();
		}
		if(removePnt) {
			clearAddFlags();
			for(int i=0;i<ptbuffer.size();i++) {
				geom_manager.removePoint(ptbuffer.get(i));
			}
			ptbuffer = new ArrayList<Point3D>();
		}
		if(removeLineHull) {
			clearAddFlags();
			ArrayList<LineHull> targs = new ArrayList<LineHull>();
			for(int i=0;i<crvbuffer.size();i++) {
				ParamCurve pc = crvbuffer.get(i);
				for(int j=0;j<geom_manager.hSurfCnt();j++) {
					boolean istarg = false;
					ArrayList<Line3D> clines = geom_manager.hsrfAt(j).curves;
					for(int k=0;k<clines.size();k++) {
						if(clines.get(k).tag == pc.tag) {
							istarg = true;
							break;
						}
					}
					if(istarg)
						targs.add(geom_manager.hsrfAt(j));
				}
			}
			for(int i=0;i<targs.size();i++) {
				geom_manager.removeLineHull(targs.get(i));
			}
			crvbuffer = new ArrayList<ParamCurve>();			
		}
		geom_manager.updateGeometry();
		this.invalidate();
		this.repaint();
	}
	private void leftMousePressed(MouseEvent e) {
		if(!addPts && !addBCrvs && !addNCrvs && !addBSrfs && !addLine) {
			int fme_width = this.getWidth();
			int fme_height = this.getHeight();
			double xscale = (double) fme_width/(xmax-xmin);
			double yscale = (double) fme_height/(ymax-ymin);
			int cx = e.getX();
			int cy = e.getY();
			
			double px = cx/xscale + xmin;
			double py = ymax - cy/yscale;
			mx = cx;
			my = cy;
			
			sel_index = getClosestPoint(px,py);
		}
	}
	private void leftMouseReleased(MouseEvent e) {
		if(!addPts && !addBCrvs && !addNCrvs && !addBSrfs&& !addLine) {
			sel_index = -1;
		}
	}
	private void leftMouseDragged(MouseEvent e) {
		int fme_width = this.getWidth();
		int fme_height = this.getHeight();
		double xscale = (double) fme_width/(xmax-xmin);
		double yscale = (double) fme_height/(ymax-ymin);
		int cx = e.getX();
		int cy = e.getY();

		int dx = cx-mx;
		int dy = cy-my;

		double dpx = (double) (dx/xscale);
		double dpy = (double) (dy/yscale);
		double dpz = 0.0;

		//first list check to see if it is screen movement
		if(e.isShiftDown()) {
			xmin = xmin-dpx;
			xmax = xmax-dpx;
			ymin = ymin+dpy;
			ymax = ymax+dpy;
			mx = cx;
			my = cy;
			this.invalidate();
			this.repaint();
		} else if(e.isControlDown()) {
			xmin = xmin-dpy;
			xmax = xmax+dpy;
			ymin = ymin-dpy;
			ymax = ymax+dpy;
			mx = cx;
			my = cy;
			this.invalidate();
			this.repaint();
		} else if(addLineHull && a_pressed) {
			// add a lot of points to the line hull
			double px = cx/xscale + xmin;
			double py = ymax - cy/yscale;			
			Point3D cpt = new Point3D(px,py,0.0);
			geom_manager.addPnt(cpt);
			ptbuffer.add(cpt);
			if(ptbuffer.size() > 2) {
				Line3D cline = new Line3D(cpt,ptbuffer.get(ptbuffer.size()-2));
				crvbuffer.add(cline);
			}
			geom_manager.updateGeometry();
			this.invalidate();
			this.repaint();
		} else { //otherwise we are moving things
			if(!addPts && !addBCrvs && !addNCrvs && !addBSrfs && !addLine && sel_index >=0 && sel_index < geom_manager.pntCnt()) {
				geom_manager.pntAt(sel_index).x = geom_manager.pntAt(sel_index).x + dpx;
				geom_manager.pntAt(sel_index).y = geom_manager.pntAt(sel_index).y - dpy;
				geom_manager.pntAt(sel_index).z = geom_manager.pntAt(sel_index).z + dpz;
				mx = cx;
				my = cy;
				geom_manager.updateGeometry();
				this.invalidate();
				this.repaint();
			}
		}
	}
	private void centerMouseDragged(MouseEvent e) {
		int fme_width = this.getWidth();
		int fme_height = this.getHeight();
		double xscale = (double) fme_width/(xmax-xmin);
		double yscale = (double) fme_height/(ymax-ymin);
		int cx = e.getX();
		int cy = e.getY();

		int dx = cx-mx;
		int dy = cy-my;

		double dpx = (double) (dx/xscale);
		double dpy = (double) (dy/yscale);
		double dpz = 0.0;

		if(addLineHull) {
			// add a lot of points to the line hull
			double px = cx/xscale + xmin;
			double py = ymax - cy/yscale;			
			Point3D cpt = new Point3D(px,py,0.0);
			geom_manager.addPnt(cpt);
			ptbuffer.add(cpt);
			if(ptbuffer.size() > 2) {
				Line3D cline = new Line3D(cpt,ptbuffer.get(ptbuffer.size()-2));
				crvbuffer.add(cline);
			}
			geom_manager.updateGeometry();
			this.invalidate();
			this.repaint();
		}
	}	
	private void leftMouseDoubleClick(MouseEvent e) {
		
	}
	private int getClosestPoint(double px, double py) {
		int fme_width = this.getWidth();
		//int fme_height = this.getHeight();
		double xscale = (double) fme_width/(xmax-xmin);
		
		int ptindex = -1;
		double mindist = (double) 15/xscale;
		
		for(int i=0;i<geom_manager.pntCnt();i++) {
			double cdist = geom_manager.pntAt(i).distTo(new Point3D(px,py,0.0));
			if(cdist < mindist) {
				mindist = cdist;
				ptindex = i;
			}
		}
		return ptindex;
	}
	private Node3D getClosestNode(double px, double py) {
		int fme_width = this.getWidth();
		//int fme_height = this.getHeight();
		double xscale = (double) fme_width/(xmax-xmin);
		double mindist = (double) 15/xscale;
		
		Node3D target = null;		
		for(int i=0;i<geom_manager.meshCnt();i++) {
			for(int j=0;j<geom_manager.meshAt(i).nodes.size();j++) {
				double cdist = geom_manager.meshAt(i).nodes.get(j).distTo(new Point3D(px,py,0.0));
				if(cdist < mindist) {
					mindist = cdist;
					target = geom_manager.meshAt(i).nodes.get(j);
				}
			}
		}
		return target;
	}	
	private int getClosestCurve(double px, double py) {
		int fme_width = this.getWidth();
		//int fme_height = this.getHeight();
		double xscale = (double) fme_width/(xmax-xmin);
		
		int ptindex = -1;
		double mindist = (double) 10/xscale;
		
		for(int i=0;i<geom_manager.crvCnt();i++) {
			double cdist = geom_manager.crvAt(i).distToPoint(new Point3D(px,py,0.0));
			if(cdist < mindist) {
				mindist = cdist;
				ptindex = i;
			}
		}
		return ptindex;
	}
	private int getClosestLine(double px, double py) {
		int fme_width = this.getWidth();
		//int fme_height = this.getHeight();
		double xscale = (double) fme_width/(xmax-xmin);
		
		int ptindex = -1;
		double mindist = (double) 10/xscale;
		
		for(int i=0;i<geom_manager.crvCnt();i++) {
			if(geom_manager.crvAt(i).curvetype == ParamCurve.LINE3D) {
				double cdist = geom_manager.crvAt(i).distToPoint(new Point3D(px,py,0.0));
				if(cdist < mindist) {
					mindist = cdist;
					ptindex = i;
				}
			}
		}
		return ptindex;
	}
	private double getClosestParam(ParamCurve crv, double px, double py) {
		double mint = crv.paramToPoint(new Point3D(px,py,0.0));
		return mint;
	}
	public DynoImage getImage() {
		return image;
	}
}
