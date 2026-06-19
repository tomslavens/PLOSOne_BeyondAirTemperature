package analysis_mesh;

import java.util.ArrayList;

import data.Entity;
import geom.ParamCurve;

public class EdgeSizeMeshCondition extends MeshCondition {
	public double edge_size;
	//inhereted from MeshCondition
	//public ArrayList<ParamCurve> target;
	//public int meshcondition_type;
	//inhereted from "Entity"
	//public int type;
	//public String name;
	//public int tag;
	
	public EdgeSizeMeshCondition() {
		edge_size = 0.0;
		targets = new ArrayList<ParamCurve>();
		meshcondition_type = MeshCondition.EDGESIZE;
		type = Entity.MESHCONDITION;
		tag = 0;
		name = "EdgeSizeMeshCondition";
	}
	public EdgeSizeMeshCondition(double in_size) {
		edge_size = in_size;
		targets = new ArrayList<ParamCurve>();
		meshcondition_type = MeshCondition.EDGESIZE;
		type = Entity.MESHCONDITION;
		tag = 0;
		name = "EdgeSizeMeshCondition";
	}
	public EdgeSizeMeshCondition(ParamCurve in_targ, double in_edgesize) {
		edge_size = in_edgesize;
		targets = new ArrayList<ParamCurve>();
		targets.add(in_targ);
		meshcondition_type = MeshCondition.EDGESIZE;
		type = Entity.MESHCONDITION;
		tag = 0;
		name = "EdgeSizeMeshCondition";
	}
	public EdgeSizeMeshCondition(ArrayList<ParamCurve> in_targs, double in_edgesize) {
		edge_size = in_edgesize;
		targets = new ArrayList<ParamCurve>();
		for(int i=0;i<in_targs.size();i++) {
			targets.add(in_targs.get(i));
		}
		meshcondition_type = MeshCondition.EDGESIZE;
		type = Entity.MESHCONDITION;
		tag = 0;
		name = "EdgeSizeMeshCondition";
	}
}
