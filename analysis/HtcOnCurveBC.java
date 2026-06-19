package analysis;

import java.util.ArrayList;

import data.Entity;
import data.GeomManager;

// HtcOnCurveBC - this BC applies a heat transfer coefficient on a ParamCurve
public class HtcOnCurveBC extends BoundaryCondition  {
	// --- inherited from Boundary Condition
	//public int BCtype;
	//public ArrayList<Entity> entities;
	//public int value_cnt;
	//public double[] values;
	// --- inhereted from Entity ---
	//public int type;
	//public int tag;
	//public String name;
	
	public HtcOnCurveBC() {
		BCtype = BoundaryCondition.HTCONCURVE;
		entities = new ArrayList<Entity>();
		type = Entity.BOUNDARYCONDITION;
		value_cnt = 2;
		values = new double[2];
		tag = 0;
		name = "HtcOnCurveBC";
	}
	public HtcOnCurveBC(double temp, double htc) {
		BCtype = BoundaryCondition.HTCONCURVE;
		entities = new ArrayList<Entity>();
		value_cnt = 2;
		values = new double[] {htc,temp};
		type = Entity.BOUNDARYCONDITION;
		tag = 0;
		name = "HtcOnCurveBC";
	}
	public int getEntityType() {
		return Entity.PARAMCURVE;
	}
	public double getTemp() {
		return values[1];
	}
	public void setTemp(double val) {
		values[1] = val;
	}	
	public double getHtc() {
		return values[0];
	}
	public void setHtc(double val) {
		values[0] = val;
	}	
}
