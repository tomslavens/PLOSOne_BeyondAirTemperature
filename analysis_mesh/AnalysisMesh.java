package analysis_mesh;

import java.util.ArrayList;

import javax.swing.JTextArea;

import analysis.Node3D;
import analysis.ThermalTriMesh;
import analysis.TriElem;
import data.Entity;
import data.GeomManager;
import geom.ParamCurve;

public abstract class AnalysisMesh extends Entity  {
	public ArrayList<ParamCurve> curves; //curve geometry that defines mesh
	public ArrayList<Node3D> nodes;      //the nodes of the mesh
	public ArrayList<TriElem> elems;     //the elements of the mesh
	public double edge_size;             //the intended edge size of the mesh
	public int node_var_length;          //the number of variables attached to the nodes 
	public String[] node_var_names;      //the names of the node variables
	public int mesh_type;                //the type of mesh this is
	public int material;                 //index of the material this mesh has
	// inhereted from Entity
	//public int type;
	//public int tag;
	//public String name;
	public static int THERMALTRIMESH = 1;   
	
	public abstract void solveMesh(GeomManager geom_manager,JTextArea outwindow);
	public abstract void solveTransientMesh(GeomManager geom_manager, double init_temp, double time, double dt, JTextArea outwindow); 
	public abstract void generateMesh(GeomManager geom_manager);	
	public abstract void applyBCs(GeomManager geom_manager);
	public abstract void applyMaterialProperties(GeomManager geom_manager);
	
	//--------------------
	// AnalysisMesh File IO
	// AnalysisMesh File String Format)
	// ent_type,tag,name:mesh_type,edge_size,curve_cnt,node_cnt,node_var_length,elem_cnt,node_var_name1,...,node_var_nameN
	// curve1,...,curveN
	// node1
	// ....
	// nodeN
	// elem1
	// ....
	// elemN
	public String getFileString() {
		String output = Integer.toString(type) + "," + Integer.toString(tag) + "," + name + ":";
		output = output + Integer.toString(mesh_type) + "," + Double.toString(edge_size) + "," + Integer.toString(curves.size());
		output = output +  "," + Integer.toString(nodes.size()) + "," + Integer.toString(node_var_length) + "," + Integer.toString(elems.size()) + "," + Integer.toString(material);
		for(int i=0;i<node_var_length;i++) {
			output = output + "," + node_var_names[i];
		}
		output = output + "\n";
		output = output + Integer.toString(curves.get(0).tag);
		for(int i=1;i<curves.size();i++) {
			output = output + "," + Integer.toString(curves.get(i).tag);
		}
		output = output + "\n";
		for(int i=0;i<nodes.size();i++) {
			output = output + nodes.get(i).getFileString() + "\n";
		}
		for(int i=0;i<elems.size();i++) {
			output = output + elems.get(i).getFileString() + "\n";
		}
		return output;
	}
	public static AnalysisMesh getAnalysisMesh(String filestring, GeomManager geom_manager) {
		AnalysisMesh mesh = null;
		String[] data_lines = filestring.split("\n");
		
		//lets parse our first line
		String header_line = data_lines[0];
		String[] parts = header_line.split(":");
		
		//parse header info
		String[] header = parts[0].split(",");
		int type = Integer.valueOf(header[0]);
		int tag = Integer.valueOf(header[1]);
		String name = header[2];
		
		//parse the header data
		String[] data = parts[1].split(",");
		int mesh_type = Integer.valueOf(data[0]);
		double edge_size = Double.valueOf(data[1]);
		int crv_cnt = Integer.valueOf(data[2]);
		int node_cnt = Integer.valueOf(data[3]);
		int node_var_length = Integer.valueOf(data[4]);
		int elem_cnt = Integer.valueOf(data[5]);
		int material = Integer.valueOf(data[6]);
		String[] var_names = new String[node_var_length];
		for(int i=0;i<node_var_length;i++) {
			int ci = i+6;
			var_names[i] = data[ci];
		}
		
		//now parse the curve info
		String[] crv_tags = data_lines[1].split(",");
		ArrayList<ParamCurve> curves = new ArrayList<ParamCurve>();
		for(int i=0;i<crv_tags.length;i++) {
			ParamCurve ccurve = (ParamCurve) geom_manager.getEntity(Integer.valueOf(crv_tags[i]));
			curves.add(ccurve);
		}		
		//parse our node info
		ArrayList<Node3D> nodes = new ArrayList<Node3D>();
		for(int i=0;i<node_cnt;i++) {
			int ci = i+2;
			String cline = data_lines[ci];
			Node3D cnode = new Node3D(cline);
			nodes.add(cnode);
		}
		//parse the elements
		ArrayList<TriElem> elems = new ArrayList<TriElem>();
		for(int i=0;i<elem_cnt;i++) {
			int ci = i+2+node_cnt;
			String cline = data_lines[ci];
			TriElem celem = new TriElem(cline);
			elems.add(celem);
		}
		if(mesh_type == AnalysisMesh.THERMALTRIMESH) {
			ThermalTriMesh ttm = new ThermalTriMesh();
			ttm.tag = tag;
			ttm.type = type;
			ttm.name = name;
			ttm.edge_size = edge_size;
			ttm.material = material;
			ttm.curves = curves;
			ttm.nodes = nodes;
			ttm.elems = elems;
			mesh = ttm;
		}
		return mesh;
	}
}
