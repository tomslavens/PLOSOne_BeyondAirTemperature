package analysis;

import java.util.ArrayList;

import data.Entity;
import data.GeomManager;

// HtcOnCurveBC - this BC applies a heat transfer coefficient on a ParamCurve
public class FluxOnCurveBC extends BoundaryCondition  {
	// --- inherited from Boundary Condition
	//public int BCtype;
	//public ArrayList<Entity> entities;
	//public int value_cnt;
	//public double[] values;
	// --- inhereted from Entity ---
	//public int type;
	//public int tag;
	//public String name;
	
	public FluxOnCurveBC() {
		BCtype = BoundaryCondition.FLUXONCURVE;
		entities = new ArrayList<Entity>();
		type = Entity.BOUNDARYCONDITION;
		value_cnt = 1;
		values = new double[1];
		tag = 0;
		name = "FluxOnCurveBC";
	}
	public FluxOnCurveBC(double qdot) {
		BCtype = BoundaryCondition.FLUXONCURVE;
		entities = new ArrayList<Entity>();
		value_cnt = 1;
		values = new double[] {qdot};
		type = Entity.BOUNDARYCONDITION;
		tag = 0;
		name = "FluxOnCurveBC";
	}
	public int getEntityType() {
		return Entity.PARAMCURVE;
	}
	public double getHeatFlux() {
		return values[0];
	}
	public void setHeatFlux(double val) {
		values[0] = val;
	}
}
