package analysis;

import java.util.ArrayList;

import data.Entity;
import geom.Point3D;

public class TempOnNodeBC extends BoundaryCondition {
	// --- inherited from Boundary Condition
	//public int BCtype;
	//public ArrayList<Entity> entities;	
	// --- inhereted from Entity ---
	//public int type;
	//public int tag;
	//public String name;

	public TempOnNodeBC() {
		BCtype = BoundaryCondition.TEMPONNODE;
		entities = new ArrayList<Entity>();
		type = Entity.BOUNDARYCONDITION;
		value_cnt = 1;
		values = new double[1];
		tag = 0;
		name = "TempOnPointBC";
	}
	public TempOnNodeBC(double temp) {
		BCtype = BoundaryCondition.TEMPONNODE;
		entities = new ArrayList<Entity>();
		value_cnt = 1;
		values = new double[] {temp};
		type = Entity.BOUNDARYCONDITION;
		tag = 0;
		name = "TempOnPointBC";
	}
	public int getEntityType() {
		return Entity.NODE;
	}
	public double getTemp() {
		return values[0];
	}
	public void setTemp(double val) {
		values[0] = val;
	}
}
