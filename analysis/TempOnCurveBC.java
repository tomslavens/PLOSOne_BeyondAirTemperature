package analysis;

import java.util.ArrayList;
import data.Entity;
import data.GeomManager;

// TempOnCurveBC - this BC applies a constant temperature on a ParamCurve
public class TempOnCurveBC extends BoundaryCondition {
	// --- inherited from Boundary Condition
	//public int BCtype;
	//public ArrayList<Entity> entities;	
	// --- inhereted from Entity ---
	//public int type;
	//public int tag;
	//public String name;

	public TempOnCurveBC() {
		BCtype = BoundaryCondition.TEMPONCURVE;
		entities = new ArrayList<Entity>();
		type = Entity.BOUNDARYCONDITION;
		value_cnt = 1;
		values = new double[1];
		tag = 0;
		name = "TempOnCurveBC";
	}
	public TempOnCurveBC(double temp) {
		BCtype = BoundaryCondition.TEMPONCURVE;
		entities = new ArrayList<Entity>();
		value_cnt = 1;
		values = new double[] {temp};
		type = Entity.BOUNDARYCONDITION;
		tag = 0;
		name = "TempOnCurveBC";
	}
	public int getEntityType() {
		return Entity.PARAMCURVE;
	}
	public double getTemp() {
		return values[0];
	}
	public void setTemp(double val) {
		values[0] = val;
	}
}
