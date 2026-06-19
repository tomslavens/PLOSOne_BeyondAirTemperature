package analysis_mesh;

import java.util.ArrayList;

import data.Entity;
import geom.ParamCurve;

public abstract class MeshCondition extends Entity {
	public ArrayList<ParamCurve> targets;
	public int meshcondition_type;
	//inhereted from "Entity"
	//public int type;
	//public String name;
	//public int tag;
	public static int EDGESIZE;
	
	// this function will detect if the given target is one of the targets of this condition
	public boolean isConditionTarget(int tag) {
		boolean included = false;
		for(int i=0;i<targets.size();i++) {
			if(targets.get(i).tag == tag)
				included = true;
		}
		return included;
	}

	@Override
	public String getFileString() {
		// TODO Auto-generated method stub
		return null;
	}

}
