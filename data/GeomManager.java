package data;

import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JTextArea;

import analysis.BoundaryCondition;
import analysis.Node3D;
import analysis.ThermalTriMesh;
import analysis_mesh.AnalysisMesh;
import analysis_mesh.MeshCondition;
import geom.BSplineSurface;
import geom.BoundedSurface;
import geom.LineHull;
import geom.NURBArc;
import geom.ParamCurve;
import geom.Point3D;
import geom_funcs.Constraint;

public class GeomManager {
	//------------
	// Program entities
	private ArrayList<Point3D> pts;
	private ArrayList<ParamCurve> splines;
	private ArrayList<BSplineSurface> bsurfs;
	private ArrayList<BoundedSurface> tsurfs;
	private ArrayList<LineHull> hsurfs;
	private ArrayList<Constraint> constraints;
	private ArrayList<AnalysisMesh> meshii;
	private ArrayList<BoundaryCondition> boundaryconditions;
	private ArrayList<Material> materials;
	private ArrayList<MeshCondition> meshconditions;
	private int tag_counter;
	//private ArrayList<String> ent_names;
	private JTextArea outwindow;
	private JList ent_list;
	
	public GeomManager(JList in_list, JTextArea in_outwindow) {
		pts = new ArrayList<Point3D>();
		splines = new ArrayList<ParamCurve>();
		bsurfs = new ArrayList<BSplineSurface>();
		tsurfs = new ArrayList<BoundedSurface>();
		hsurfs = new ArrayList<LineHull>();
		constraints = new ArrayList<Constraint>();
		meshii = new ArrayList<AnalysisMesh>();
		boundaryconditions = new ArrayList<BoundaryCondition>();
		meshconditions = new ArrayList<MeshCondition>();
		materials = new ArrayList<Material>();
		tag_counter = 0; //this gets a +1 every time we add an entity to the ArrayLists
		//ent_names = new ArrayList<String>();
		ent_list = in_list;
		outwindow = in_outwindow;
	}
	public void updateGeometry() {
		GeometryUpdateFunctions.updateGeometry(this);
		/*for(int i=0;i<splines.size();i++) {
			if(splines.get(i).curvetype == ParamCurve.NURBARC) {
				NURBArc arc = (NURBArc) splines.get(i);
				arc.updateArc();
			}
		}
		for(int i=0;i<constraints.size();i++) {
			constraints.get(i).execute();
		}
		for(int i=0;i<tsurfs.size();i++) {
			tsurfs.get(i).updateMesh();
		}*/
	}
	public void updateMeshes() {
		//first remove all the nodal and element BC's
		ArrayList<BoundaryCondition> good_bcs = new ArrayList<BoundaryCondition>(); 
		for(int i=0;i<boundaryconditions.size();i++) {
			if(boundaryconditions.get(i).BCtype != 0)
				good_bcs.add(boundaryconditions.get(i));
			else
				removeEntity(boundaryconditions.get(i).tag);
		}
		boundaryconditions = new ArrayList<BoundaryCondition>();
		for(int i=0;i<good_bcs.size();i++) {
			boundaryconditions.add(good_bcs.get(i));
		}
		
		//now remesh all the meshii
		for(int i=0;i<meshii.size();i++) {
			meshii.get(i).generateMesh(this);
			meshii.get(i).applyMaterialProperties(this);
		}
	}
	public void updateMeshMaterialProperties() {
		//now remesh all the meshii
		for(int i=0;i<meshii.size();i++) {
			meshii.get(i).applyMaterialProperties(this);
		}
	}
	public void solveMeshes() {
		outwindow.append("There are " + Integer.toString(meshii.size()) + " meshii to solve\n");
		for(int i=0;i<meshii.size();i++) {
			meshii.get(i).solveMesh(this,outwindow);
		}
	}
	public void solveTransient() {
		outwindow.append("There are " + Integer.toString(meshii.size()) + " meshii to solve\n");
		for(int i=0;i<meshii.size();i++) {
			meshii.get(i).solveTransientMesh(this,50,1,0.00001,outwindow);
		}
	}	
	public void clear() {
		pts.clear();
		splines.clear();
		bsurfs.clear();
		hsurfs.clear();
		tsurfs.clear();
		constraints.clear();
		meshii.clear();
		boundaryconditions.clear();
		meshconditions.clear();
		materials.clear();
		tag_counter = 0;
		DefaultListModel dlm = (DefaultListModel) ent_list.getModel();
		dlm.removeAllElements();
	}
	// this function adds the entity to the feature tree
	private void addEntity(Entity ent) {
		DefaultListModel dlm = (DefaultListModel) ent_list.getModel();
		dlm.addElement(ent.name + " (" + Integer.toString(ent.tag) + ")");
	}
	// this function removes the entity from the feature tree
	private void removeEntity(int ent_tag) {
		DefaultListModel dlm = (DefaultListModel) ent_list.getModel();
		for(int i=0;i<dlm.size();i++) {
			String cent = (String) dlm.get(i);
			String[] parts = cent.split("\s++");
			String strnum = parts[1].replaceAll("\\(", "").replaceAll("\\)", "");
			outwindow.append(strnum + "\n");
			int ctag = Integer.valueOf(strnum);
			if(ctag == ent_tag) {
				dlm.remove(i);
				break;
			}
		}
	}
	//--------
	// access functions	
	public Entity getEntity(int tag) {
		Entity targ = null;
		for(int i=0;i<pts.size();i++) {
			if(pts.get(i).tag == tag)
				targ = pts.get(i);
		}
		if(targ == null) {
			for(int i=0;i<splines.size();i++) {
				if(splines.get(i).tag == tag)
					targ = splines.get(i);
			}
		}
		if(targ == null) {
			for(int i=0;i<bsurfs.size();i++) {
				if(bsurfs.get(i).tag == tag)
					targ = bsurfs.get(i);
			}
		}
		if(targ == null) {
			for(int i=0;i<tsurfs.size();i++) {
				if(tsurfs.get(i).tag == tag)
					targ = tsurfs.get(i);
			}
		}
		if(targ == null) {
			for(int i=0;i<hsurfs.size();i++) {
				if(hsurfs.get(i).tag == tag)
					targ = hsurfs.get(i);
			}
		}
		if(targ == null) {
			for(int i=0;i<constraints.size();i++) {
				if(constraints.get(i).tag == tag)
					targ = constraints.get(i);
			}
		}
		if(targ == null) {
			for(int i=0;i<meshii.size();i++) {
				if(meshii.get(i).tag == tag)
					targ = meshii.get(i);
			}
		}
		if(targ == null) {
			for(int i=0;i<boundaryconditions.size();i++) {
				if(boundaryconditions.get(i).tag == tag)
					targ = boundaryconditions.get(i);
			}
		}
		if(targ == null) {
			for(int i=0;i<meshconditions.size();i++) {
				if(meshconditions.get(i).tag == tag)
					targ = meshconditions.get(i);
			}
		}	
		return targ;
	}
	public Node3D getAnalysisNode(int tag) {
		Node3D node = null;
		for(int i=0;i<meshii.size();i++) {
			for(int j=0;j<meshii.get(i).nodes.size();j++) {
				if(meshii.get(i).nodes.get(j).tag == tag)
					node = meshii.get(i).nodes.get(j);
			}
		}
		return node;
	}
	public Material getMaterial(int index) {
		return materials.get(index);
	}
	public int materialCnt() {
		return materials.size();
	}
	public void addMaterial(Material mat) {
		outwindow.append("Adding material " + mat.matname + " to database\n");
		materials.add(mat);
	}
	public int getMaxTag() {
		int maxtag = -1;
		for(int i=0;i<pts.size();i++) {
			if(pts.get(i).tag > maxtag)
				maxtag = pts.get(i).tag;
		}
		for(int i=0;i<splines.size();i++) {
			if(splines.get(i).tag > maxtag)
				maxtag = splines.get(i).tag;
		}
		for(int i=0;i<bsurfs.size();i++) {
			if(bsurfs.get(i).tag > maxtag)
				maxtag = bsurfs.get(i).tag;
		}
		for(int i=0;i<tsurfs.size();i++) {
			if(tsurfs.get(i).tag > maxtag)
				maxtag = tsurfs.get(i).tag;
		}
		for(int i=0;i<constraints.size();i++) {
			if(constraints.get(i).tag > maxtag)
				maxtag = constraints.get(i).tag;
		}
		for(int i=0;i<meshii.size();i++) {
			if(meshii.get(i).tag > maxtag)
				maxtag = meshii.get(i).tag;
		}
		for(int i=0;i<boundaryconditions.size();i++) {
			if(boundaryconditions.get(i).tag > maxtag)
				maxtag = boundaryconditions.get(i).tag;
		}
		for(int i=0;i<meshconditions.size();i++) {
			if(meshconditions.get(i).tag > maxtag)
				maxtag = meshconditions.get(i).tag;
		}
		return maxtag;
	}
	
	//----- Point3D stuff -----
	public void addPnt(Point3D pt) {
		pt.tag = tag_counter;
		tag_counter = tag_counter+1;
		pts.add(pt);
		addEntity(pt);
	}
	public void addPnt(Point3D pt, int in_tag) {
		pt.tag = in_tag;
		tag_counter = getMaxTag()+1;
		pts.add(pt);
		addEntity(pt);
	}
	public Point3D pntAt(int i) {
		return pts.get(i);
	}
	public int pntCnt() {
		return pts.size();
	}
	public void removePoint(Point3D dpt) {
		//first go through and see if this is tagged in a spline
		ArrayList<ParamCurve> dcrvs = new ArrayList<ParamCurve>(); // list of associated curves to delete
		for(int i=0;i<splines.size();i++) {
			for(int j=0;j<splines.get(i).geompts.length;j++) {
				if(splines.get(i).geompts[j].tag == dpt.tag) {
					outwindow.append("HERE!!!!");
					dcrvs.add(splines.get(i));
				}
			}
		}
		//now go through our BSpline surfaces and see who is associated with this pnt
		ArrayList<BSplineSurface> dbsurfs = new ArrayList<BSplineSurface>();
		for(int i=0;i<bsurfs.size();i++) {
			int u_lng = bsurfs.get(i).geompts.length;
			int v_lng = bsurfs.get(i).geompts[0].length;
			for(int u=0;u<u_lng;u++) {
				for(int v=0;v<v_lng;v++) {
					Point3D cpt = bsurfs.get(i).geompts[u][v];
					if(cpt.tag == dpt.tag) {
						dbsurfs.add(bsurfs.get(i));
						break;
					}
				}
			}
		}
		//next go through our bounded surfaces and see who is associated with this pt
		ArrayList<BoundedSurface> dtsurfs = new ArrayList<BoundedSurface>();
		for(int i=0;i<tsurfs.size();i++) {
			for(int j=0;j<tsurfs.get(i).curves.size();j++) {
				ParamCurve ccurve = tsurfs.get(i).curves.get(j);
				for(int k=0;k<ccurve.geompts.length;k++) {
					Point3D cpt = ccurve.geompts[k];
					if(cpt.tag == dpt.tag) {
						dtsurfs.add(tsurfs.get(i));
						break;
					}
				}
			}
		}
		//next go through our LineHulls and see who is associated with this pt
		ArrayList<LineHull> dhsurfs = new ArrayList<LineHull>();
		for(int i=0;i<hsurfs.size();i++) {
			for(int j=0;j<hsurfs.get(i).curves.size();j++) {
				ParamCurve ccurve = hsurfs.get(i).curves.get(j);
				for(int k=0;k<ccurve.geompts.length;k++) {
					Point3D cpt = ccurve.geompts[k];
					if(cpt.tag == dpt.tag) {
						dhsurfs.add(hsurfs.get(i));
						break;
					}
				}
			}
		}
		//next go through the constraints and see who is associated with this point
		ArrayList<Constraint> dconstraints = new ArrayList<Constraint>();
		for(int i=0;i<constraints.size();i++) {
			Entity[] ents = constraints.get(i).targets;
			for(int j=0;j<ents.length;j++) {
				if(ents[j].tag == dpt.tag) {
					dconstraints.add(constraints.get(i));
					break;
				}				
			}
		}
		//now remove these objects
		ArrayList<ParamCurve> nsplines = new ArrayList<ParamCurve>();
		for(int i=0;i<splines.size();i++) {
			boolean todelete = false;
			for(int j=0;j<dcrvs.size();j++) {
				if(dcrvs.get(j).tag == splines.get(i).tag) {
					todelete = true;
					break;
				}
			}
			if(!todelete) {
				nsplines.add(splines.get(i));
			}
		}
		for(int i=0;i<dcrvs.size();i++) {
			removeEntity(dcrvs.get(i).tag);
		}
		splines = nsplines;
		//-----
		ArrayList<BSplineSurface> nbsurfs = new ArrayList<BSplineSurface>();
		for(int i=0;i<bsurfs.size();i++) {
			boolean todelete = false;
			for(int j=0;j<dbsurfs.size();j++) {
				if(dbsurfs.get(j).tag == bsurfs.get(j).tag) {
					todelete = true;
					break;
				}
			}
			if(!todelete) {
				nbsurfs.add(bsurfs.get(i));
			}		
		}
		for(int i=0;i<dbsurfs.size();i++) {
			removeEntity(dbsurfs.get(i).tag);
		}
		bsurfs = nbsurfs;		
		//-----
		ArrayList<BoundedSurface> ntsurfs = new ArrayList<BoundedSurface>();
		for(int i=0;i<tsurfs.size();i++) {
			boolean todelete = false;
			for(int j=0;j<dtsurfs.size();j++) {
				if(dtsurfs.get(j).tag == tsurfs.get(j).tag) {
					todelete = true;
					break;
				}
			}
			if(!todelete) {
				ntsurfs.add(tsurfs.get(i));
			}		
		}
		for(int i=0;i<dtsurfs.size();i++) {
			removeEntity(dtsurfs.get(i).tag);
		}
		tsurfs = ntsurfs;
		//-----
		ArrayList<LineHull> nhsurfs = new ArrayList<LineHull>();
		for(int i=0;i<hsurfs.size();i++) {
			boolean todelete = false;
			for(int j=0;j<dhsurfs.size();j++) {
				if(dhsurfs.get(j).tag == hsurfs.get(i).tag) {
					todelete = true;
					break;
				}
			}
			if(!todelete) {
				nhsurfs.add(hsurfs.get(i));
			}
		}
		for(int i=0;i<dhsurfs.size();i++) {
			removeEntity(dhsurfs.get(i).tag);
		}
		hsurfs = nhsurfs;
		//-----
		ArrayList<Constraint> nconstraints = new ArrayList<Constraint>();
		for(int i=0;i<constraints.size();i++) {
			boolean todelete = false;
			for(int j=0;j<dconstraints.size();j++) {
				if(dconstraints.get(j).tag == constraints.get(i).tag) {
					todelete = true;
					break;
				}
			}
			if(!todelete) {
				nconstraints.add(constraints.get(i));
			}
		}
		for(int i=0;i<dconstraints.size();i++) {
			removeEntity(dconstraints.get(i).tag);
		}
		constraints = nconstraints;
		//-------
		ArrayList<Point3D> npts = new ArrayList<Point3D>();
		for(int i=0;i<pts.size();i++) {
			if(dpt.tag != pts.get(i).tag)
				npts.add(pts.get(i));
		}
		removeEntity(dpt.tag);
		pts = npts;
		this.updateGeometry();
	}
	public void removeLineHull(LineHull targ) {
		ArrayList<LineHull> nhulls = new ArrayList<LineHull>();
		for(int i=0;i<hsurfs.size();i++) {
			if(hsurfs.get(i).tag != targ.tag) {
				nhulls.add(hsurfs.get(i));
			}
		}
		hsurfs = nhulls;
		removeEntity(targ.tag);
	}
	
	public void addCurve(ParamCurve crv) {
		crv.tag = tag_counter;
		tag_counter = tag_counter+1;
		splines.add(crv);
		addEntity(crv);
	}
	public void addCurve(ParamCurve crv, int in_tag) {
		crv.tag = in_tag;
		tag_counter = getMaxTag()+1;
		splines.add(crv);
		addEntity(crv);
	}
	public void previewCurve(ParamCurve crv) {
		crv.tag = tag_counter;
		tag_counter = tag_counter+1;
		splines.add(crv);
	}
	public void setCrvAt(int i,ParamCurve crv) {
		crv.tag = tag_counter;
		tag_counter = tag_counter+1;
		splines.set(i, crv);
	}
	public void removeCrvAt(int i) {
		splines.remove(i);
	}
	public ParamCurve crvAt(int i) {
		return splines.get(i);
	}
	public int crvCnt() {
		return splines.size();
	}
	
	public void addBSplineSurface(BSplineSurface surf) {
		surf.tag = tag_counter;
		tag_counter = tag_counter+1;
		bsurfs.add(surf);
		addEntity(surf);
	}
	public void addBSplineSurface(BSplineSurface surf, int in_tag) {
		surf.tag = in_tag;
		tag_counter = getMaxTag()+1;
		bsurfs.add(surf);
		addEntity(surf);
	}
	public BSplineSurface bsrfAt(int i) {
		return bsurfs.get(i);
	}
	public int bsurfCnt() {
		return bsurfs.size();
	}
	
	public void addBoundedSurface(BoundedSurface surf) {
		surf.tag = tag_counter;
		tag_counter = tag_counter+1;
		tsurfs.add(surf);
		addEntity(surf);
	}
	public void addBoundedSurface(BoundedSurface surf, int in_tag) {
		surf.tag = in_tag;
		tag_counter = getMaxTag()+1;
		tsurfs.add(surf);
		addEntity(surf);
	}
	public BoundedSurface tsrfAt(int i) {
		return tsurfs.get(i);
	}
	public int tSurfCnt() {
		return tsurfs.size();
	}
	
	public void addLineHull(LineHull surf) {
		surf.tag = tag_counter;
		tag_counter = tag_counter+1;
		hsurfs.add(surf);
		addEntity(surf);
	}
	public void addLineHull(LineHull surf, int in_tag) {
		surf.tag = in_tag;
		tag_counter = getMaxTag()+1;
		hsurfs.add(surf);
		addEntity(surf);
	}
	public LineHull hsrfAt(int i) {
		return hsurfs.get(i);
	}
	public int hSurfCnt() {
		return hsurfs.size();
	}
	
	public void addConstraint(Constraint conts) {
		conts.tag = tag_counter;
		tag_counter = tag_counter+1;
		constraints.add(conts);
		addEntity(conts);
	}
	public void addConstraint(Constraint conts, int in_tag) {
		conts.tag = in_tag;
		tag_counter = getMaxTag()+1;
		constraints.add(conts);
		addEntity(conts);
	}
	public Constraint constraitnAt(int i) {
		return constraints.get(i);
	}
	public int constCnt() {
		return constraints.size();
	}
	
	public void addAnalysisMesh(AnalysisMesh mesh) {
		mesh.tag = tag_counter;
		tag_counter = tag_counter+1;
		meshii.add(mesh);
		addEntity(mesh);
	}
	public void addAnalysisMesh(AnalysisMesh mesh, int in_tag) {
		mesh.tag = in_tag;
		tag_counter = getMaxTag()+1;
		meshii.add(mesh);
		addEntity(mesh);
	}
	public AnalysisMesh meshAt(int i) {
		return meshii.get(i);
	}
	public int meshCnt() {
		return meshii.size();
	}
	
	// ------------
	// Boundary condition functions
	public void addBC(BoundaryCondition bc) {
		bc.tag = tag_counter;
		tag_counter = tag_counter+1;
		boundaryconditions.add(bc);
		addEntity(bc);
	}
	public void addBC(BoundaryCondition bc, int in_tag) {
		bc.tag = in_tag;
		tag_counter = getMaxTag()+1;
		boundaryconditions.add(bc);
		addEntity(bc);
	}
	public void removeBC(int in_tag) {
		for(int i=0;i<boundaryconditions.size();i++) {
			if(boundaryconditions.get(i).tag == in_tag) {
				boundaryconditions.remove(i);
				removeEntity(in_tag);
				break;
			}
		}
	}	
	public BoundaryCondition bcAt(int i) {
		return boundaryconditions.get(i);
	}
	public int bcCnt() {
		return boundaryconditions.size();
	}
	// this is called after deleting a BC those with no targets are removed
	public void cleanBCs() {
		ArrayList<Integer> targ_tags = new ArrayList<Integer>();
		for(int i=0;i<boundaryconditions.size();i++) {
			if(boundaryconditions.get(i).entities.size() < 1)
				targ_tags.add(boundaryconditions.get(i).tag);
		}
		for(int i=0;i<targ_tags.size();i++) {
			removeBC(targ_tags.get(i));
			removeEntity(targ_tags.get(i));
		}
	}
	public void replaceBCList(ArrayList<BoundaryCondition> new_list) {
		boundaryconditions = new_list;
	}
	
	public void addMC(MeshCondition mc) {
		mc.tag = tag_counter;
		tag_counter = tag_counter+1;
		meshconditions.add(mc);
		addEntity(mc);
	}
	public void addMC(MeshCondition mc, int in_tag) {
		mc.tag = in_tag;
		tag_counter = getMaxTag()+1;
		meshconditions.add(mc);
		addEntity(mc);
	}
	public MeshCondition mcAt(int i) {
		return meshconditions.get(i);
	}
	public int mcCnt() {
		return meshconditions.size();
	}
}
