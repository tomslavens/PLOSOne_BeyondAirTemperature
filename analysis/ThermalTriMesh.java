package analysis;

import java.util.ArrayList;

import javax.swing.JTextArea;

import analysis_mesh.AnalysisMesh;
import analysis_mesh.AnalysisMeshFunctions;
import data.Entity;
import data.GeomManager;
import data.Material;
import geom.Line3D;
import geom.ParamCurve;
import geom.Point3D;
import geom_funcs.TriangleMath;
import geom_meshes.DelaunayTriangulation;
import geom_meshes.Triangle3R;
import geom_meshes.TriangleMesh;
import linear_math.LinearFunctions;
import linear_math.Vector3D;

public class ThermalTriMesh extends AnalysisMesh {
	// --inhereted from AnalysisMesh--
	//public ArrayList<ParamCurve> curves;
	//public ArrayList<Node3D> nodes;
	//public ArrayList<Triangle3R> elems;
	//public double edge_size;
	//public int var_length;
	//public String[] var_names;
	// --- inhereted from Entity ---
	//public int type;
	//public int tag;
	//public String name;
	public static int TEMPNODE = 1; //flag value for a temp-on-node BC
	public static int CONVNODE = 2; //flag value for an htc-on-node BC
	public static int FLUXNODE = 3; //flag value for qdot-on-node BC
	public static int CONVFLUX = 4; //flag value for qdot+htc on node BC
	
	public ThermalTriMesh() {
		curves = new ArrayList<ParamCurve>();
		nodes = new ArrayList<Node3D>();
		elems = new ArrayList<TriElem>();
		edge_size = 0.100;
		node_var_length = 8; // number of properties on our nodes
		node_var_names = new String[] {"TEMP","CONDUCTIVTY","BCTEMP","HCONV","HTEMP","QDOT","DENSITY","SPECHEAT"};
		type = Entity.ANALYSISMESH;
		tag = 0;
		name = "ThermalTriMesh";
		mesh_type = AnalysisMesh.THERMALTRIMESH;
		material = -1;
	}
	
	//solveMesh() - will solve the thermal solution on this mesh
	public void solveMesh(GeomManager geom_manager, JTextArea outwindow) {
		applyBCs(geom_manager);
		applyMaterialProperties(geom_manager);
		int bcnodes = 0;
		for(int i=0;i<nodes.size();i++) {
			if(nodes.get(i).bcflag > 0)
				bcnodes = bcnodes+1;
		}
		outwindow.append("There are BC nodes of " + Integer.toString(bcnodes) + "\n");
		double maxdiff = 9999;
		int iter = 0;
		SteadyHeatTransfer2DFEA.SolveSteadyStateTriMesh(this,outwindow);
		applyMaterialProperties(geom_manager); //update our material properties based on temperature
		while(maxdiff > 0.1 && iter < 4) {
			ArrayList<Node3D> csol = AnalysisMeshFunctions.getNodeArrayCopy(nodes); //copy current solution
			SteadyHeatTransfer2DFEA.SolveSteadyStateTriMesh(this,outwindow);
			applyMaterialProperties(geom_manager); //update our material properties based on temperature
			maxdiff = AnalysisMeshFunctions.getMaxSolutionDelta(csol, nodes);
			iter = iter+1;
			outwindow.append("Solution iteration " + Integer.toString(iter) + " complete! Max error is " + Double.toString(maxdiff) + "\n");
		}
	}
	//solveMesh() - will solve the thermal solution on this mesh
	public void solveTransientMesh(GeomManager geom_manager, double init_temp, double time, double dt, JTextArea outwindow) {
		applyBCs(geom_manager);
		applyMaterialProperties(geom_manager);
		int bcnodes = 0;
		for(int i=0;i<nodes.size();i++) {
			if(nodes.get(i).bcflag > 0)
				bcnodes = bcnodes+1;
		}
		outwindow.append("There are BC nodes of " + Integer.toString(bcnodes) + "\n");
		TransientHeatTransfer2DFEA2.SolveTransientTriMesh(this,init_temp,time,dt,outwindow);
	}	
	//generateMesh() - this will create nodes and elements based on current edge_size and curves
	public void generateMesh(GeomManager geom_manager) {
		//First create the point grid with the given edge_size
		nodes = new ArrayList<Node3D>();
		nodes = AnalysisMeshFunctions.create2DNodeMeshBySize(curves,edge_size,node_var_length,geom_manager);  //create the nodes of the mesh
		elems = new ArrayList<TriElem>();
		elems = DelaunayTriangulation.triangulateXYNodes(nodes, node_var_length);       //create the triangle elements of the mesh
		
		//offset the node & elem numbering
		int adder = 1000*geom_manager.meshCnt();
		for(int i=0;i<nodes.size();i++) {
			nodes.get(i).tag = nodes.get(i).tag+adder; 
		}
		for(int i=0;i<elems.size();i++) {
			elems.get(i).tag = i+adder; 
		}

		//now, since we have the parametric curves here, we can calculate and remove triangles outside our surface
		//create segments
		ArrayList<TriElem> inside_tris = new ArrayList<TriElem>(); //this will house the 'good triangles'
		ArrayList<Line3D> segments = new ArrayList<Line3D>(); //get line-segment representation of the curve
		for(int i=0;i<curves.size();i++) {
			ArrayList<Line3D> csegs = curves.get(i).getSegmentSet(35);
			for(int j=0;j<csegs.size();j++) {
				segments.add(csegs.get(j));
			}
		}
		//create horizontal rays and see how many interesections there are
		for(int i=0;i<elems.size();i++) {
			Point3D tricenter = TriangleMath.getCentroid(nodes.get(elems.get(i).pt1), nodes.get(elems.get(i).pt2), nodes.get(elems.get(i).pt3));
			Line3D triray_plus = new Line3D(tricenter, new Point3D(999999,tricenter.y,0.0));
			Line3D triray_minus = new Line3D(tricenter, new Point3D(-999999,tricenter.y,0.0));
			int intersections_plus = 0; //number of curves the center ray intersects
			int intersections_minus = 0; //number of curves the center ray intersects
			for(int j=0;j<segments.size();j++) {
				if(LinearFunctions.lineSegmentsIntersect(triray_plus, segments.get(j)))
					intersections_plus = intersections_plus+1;
				if(LinearFunctions.lineSegmentsIntersect(triray_minus, segments.get(j)))
					intersections_minus = intersections_minus+1;
			}
			if(intersections_plus == 0 || intersections_plus%2 == 0 || intersections_minus == 0 || intersections_minus%2 == 0) {
				//System.out.println("TRIANGLE OUT");
			} else {
				inside_tris.add(elems.get(i));
			}
		}
		elems = inside_tris;
	}
	// Use the BC's defined in the GeomManager to update our BC's
	public void applyBCs(GeomManager geom_manager) {
		// first lets whipe all the element BC data
		for(int i=0;i<elems.size();i++) {
			elems.get(i).clearBCs();
		}
		//next lets whipe all the node BC data
		for(int i=0;i<nodes.size();i++) {
			nodes.get(i).values = new double[node_var_length];
			nodes.get(i).bcflag = 0; //reset the BC flag
			for(int j=0;j<node_var_length;j++) {
				nodes.get(i).values[j] = 0.0;
			}
		}
		//now start re-applying BC's
		//first, apply the convection data to the nodes
		for(int i=0;i<geom_manager.bcCnt();i++) {
			if(geom_manager.bcAt(i).BCtype == BoundaryCondition.HTCONCURVE) {
				for(int j=0;j<geom_manager.bcAt(i).entities.size();j++) {
					int curve_tag = geom_manager.bcAt(i).entities.get(j).tag; //the current curve
					//assign node Htc values to the appropriate nodes
					for(int k=0;k<nodes.size();k++) {
						if(nodes.get(k).parent == curve_tag) {
							nodes.get(k).values[3] = geom_manager.bcAt(i).values[0]; // set HCONV to a non-zero value
							nodes.get(k).values[4] = geom_manager.bcAt(i).values[1]; // set HTEMP to a non-zero value
							nodes.get(k).bcflag = ThermalTriMesh.CONVNODE;
							System.out.println("Applying htc on node: " + Double.toString(nodes.get(k).values[3]));
						}
					}
				}
			}
		}
		//next, apply the Black Body Radiation flux data to the nodes
		for(int i=0;i<geom_manager.bcCnt();i++) {
			if(geom_manager.bcAt(i).BCtype == BoundaryCondition.BBRADONCURVE) {
				BBRadiationOnCurveBC rad = (BBRadiationOnCurveBC) geom_manager.bcAt(i); //get access to class-specific functions
				for(int j=0;j<geom_manager.bcAt(i).entities.size();j++) {
					int curve_tag = geom_manager.bcAt(i).entities.get(j).tag; //the current curve
					//assign node flux values to the appropriate nodes
					for(int k=0;k<nodes.size();k++) {
						if(nodes.get(k).parent == curve_tag) {
							Point3D npt = new Point3D(nodes.get(k).x,nodes.get(k).y,nodes.get(k).z);
							ParamCurve curve = (ParamCurve) geom_manager.getEntity(curve_tag);
							
							Line3D targ2src = new Line3D(rad.getSrcPoint(),npt);
							int mesh_intersections = AnalysisMeshFunctions.getIntersectionNumber(targ2src, curves);
							
							if(mesh_intersections <= 1) {
								Vector3D nd_norm = AnalysisMeshFunctions.getNodeNorm(npt, curve, curves);
								double qdot = rad.getHeatFluxOnPt(npt, nd_norm);
								nodes.get(k).values[5] = nodes.get(k).values[5] + qdot; // set QDOT to a non-zero value
								if(nodes.get(k).bcflag == ThermalTriMesh.CONVNODE)
									nodes.get(k).bcflag = ThermalTriMesh.CONVFLUX;
								else
									nodes.get(k).bcflag = ThermalTriMesh.FLUXNODE;
								System.out.println("Applying radiation flux on node: " + Double.toString(nodes.get(k).values[5]));
							} else {
								System.out.println("Node was shielded from radiation");
							}
						}
					}
				}
			}
		}
		//next, apply the heat flux data to the nodes
		for(int i=0;i<geom_manager.bcCnt();i++) {
			if(geom_manager.bcAt(i).BCtype == BoundaryCondition.FLUXONCURVE) {
				for(int j=0;j<geom_manager.bcAt(i).entities.size();j++) {
					int curve_tag = geom_manager.bcAt(i).entities.get(j).tag; //the current curve
					//assign node flux values to the appropriate nodes
					for(int k=0;k<nodes.size();k++) {
						if(nodes.get(k).parent == curve_tag) {
							nodes.get(k).values[5] = nodes.get(k).values[5] + geom_manager.bcAt(i).values[0]; // set QDOT to a non-zero value
							if(nodes.get(k).bcflag == ThermalTriMesh.CONVNODE || nodes.get(k).bcflag == ThermalTriMesh.CONVFLUX)
								nodes.get(k).bcflag = ThermalTriMesh.CONVFLUX;
							else
								nodes.get(k).bcflag = ThermalTriMesh.FLUXNODE;
							nodes.get(k).bcflag = ThermalTriMesh.FLUXNODE;
							System.out.println("Applying flux on node: " + Double.toString(nodes.get(k).values[5]));
						}
					}
				}
			}
		}
		//next, apply temperature on curve BC's
		for(int i=0;i<geom_manager.bcCnt();i++) {
			if(geom_manager.bcAt(i).BCtype == BoundaryCondition.TEMPONCURVE) {
				for(int j=0;j<geom_manager.bcAt(i).entities.size();j++) {
					int curve_tag = geom_manager.bcAt(i).entities.get(j).tag; //the current curve
					//assign node BCTemp values to the appropriate nodes
					for(int k=0;k<nodes.size();k++) {
						if(nodes.get(k).parent == curve_tag) {
							nodes.get(k).values[2] = geom_manager.bcAt(i).values[0]; // set BCTEMP to a non-zero value
							nodes.get(k).bcflag = ThermalTriMesh.TEMPNODE;
							System.out.println("Applying temp on node: " + Double.toString(nodes.get(k).values[2]));
						}
					}
				}
			}
		}
		//next, apply temperature on node BC's
		for(int i=0;i<geom_manager.bcCnt();i++) {
			if(geom_manager.bcAt(i).BCtype == BoundaryCondition.TEMPONNODE) {
				for(int j=0;j<geom_manager.bcAt(i).entities.size();j++) {
					Node3D cnode = (Node3D) geom_manager.bcAt(i).entities.get(j);
					cnode.values[2] = geom_manager.bcAt(i).values[0]; // set BCTEMP to a non-zero value
					cnode.bcflag = ThermalTriMesh.TEMPNODE;
					System.out.println("Applying temp on node: " + Double.toString(cnode.values[2]));
				}
			}
		}
	}
	//Access functions
	public Node3D nodeAt(int i) {
		return nodes.get(i);
	}
	public TriElem elemAt(int i) {
		return elems.get(i);
	}
	
	//createThermalTriMesh(double edge_size, ArrayList<ParamCurve> curves) - creates a triangle mesh for
	//   analysis using given edge size and curve set
	public static ThermalTriMesh createThermalTriMesh(double in_edge_size, ArrayList<ParamCurve> in_curves, GeomManager geom_manager) {
		ThermalTriMesh mesh = new ThermalTriMesh();
		mesh.edge_size = in_edge_size;
		for(int i=0;i<in_curves.size();i++) {
			mesh.curves.add(in_curves.get(i));
		}
		mesh.generateMesh(geom_manager);
		
		return mesh;
	}

	@Override
	public void applyMaterialProperties(GeomManager geom_manager) {
		if(material != -1) {
			Material mat = geom_manager.getMaterial(material);
			for(int i=0;i<nodes.size();i++) {
				nodes.get(i).values[1] = mat.getConductivity(nodes.get(i).values[0]);
				nodes.get(i).values[6] = mat.density;
				nodes.get(i).values[7] = mat.getSpecificHeat(nodes.get(i).values[0]);
			}
		}
	}
}
