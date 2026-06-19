package analysis;

import java.util.ArrayList;
import data.Entity;
import geom.Point3D;
import linear_math.Vector3D;
import physics.RadiationCalculations;

/*=================
 * BBRadiationOnCurveBC - this holds all the info needed to calculate a black body source projection onto a curve
 * This BC holds 9 values:
 * double targ_absorbtivity
 * double src_area
 * double src_temp
 * Point3D src 
 * Vector3D src_norm 
 */

public class BBRadiationOnCurveBC extends BoundaryCondition  {
	// --- inherited from Boundary Condition
	//public int BCtype;
	//public ArrayList<Entity> entities;
	//public int value_cnt;
	//public double[] values;
	// --- inhereted from Entity ---
	//public int type;
	//public int tag;
	//public String name;
	
	public BBRadiationOnCurveBC() {
		BCtype = BoundaryCondition.BBRADONCURVE;
		entities = new ArrayList<Entity>();
		type = Entity.BOUNDARYCONDITION;
		value_cnt = 9;
		values = new double[value_cnt];
		tag = 0;
		name = "BBRadOnCurveBC";
	}
	public BBRadiationOnCurveBC(double targ_absorp,double src_area,double src_temp,double[] src_loc, double[] src_norm) {
		BCtype = BoundaryCondition.BBRADONCURVE;
		entities = new ArrayList<Entity>();
		value_cnt = 9;
		values = new double[] {targ_absorp,src_area,src_temp,
				src_loc[0],src_loc[1],src_loc[2],
				src_norm[0],src_norm[1],src_norm[2]};
		type = Entity.BOUNDARYCONDITION;
		tag = 0;
		name = "BBRadOnCurveBC";
	}
	public Point3D getSrcPoint() {
		Point3D src = new Point3D(values[3],values[4],values[5]);
		return src;
	}
	public void setSrcPoint(Point3D pt) {
		values[3] = pt.x;
		values[4] = pt.y;
		values[5] = pt.z;
	}
	public Vector3D getSrcNorm() {
		Vector3D src = new Vector3D(values[6],values[7],values[8]);
		return src;
	}
	public void setSrcNorm(Vector3D vect) {
		values[6] = vect.u;
		values[7] = vect.v;
		values[8] = vect.w;
	}
	public double getSrcTemp() {
		return values[2];
	}
	public void setSrcTemp(double temp) {
		values[2] = temp;
	}
	public double getSrcArea() {
		return values[1];
	}
	public void setSrcArea(double area) {
		values[1] = area;
	}
	public double getTargAbsorbtivity() {
		return values[0];
	}
	public void setTargAbsorbtivity(double val) {
		values[0] = val;
	}
	public double getHeatFluxOnPt(Point3D targ, Vector3D targ_norm) {
		double qdot = RadiationCalculations.calcBlackBodySurfaceRadiationFluxLoad(targ,targ_norm,getTargAbsorbtivity(),getSrcPoint(),getSrcArea(),getSrcNorm(),getSrcTemp());
		return qdot;
	}
	public int getEntityType() {
		return Entity.PARAMCURVE;
	}
}
