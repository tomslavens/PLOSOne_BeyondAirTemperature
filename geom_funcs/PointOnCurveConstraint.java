package geom_funcs;

import data.Entity;
import data.GeomManager;
import geom.Line3D;
import geom.ParamCurve;
import geom.Point3D;

public class PointOnCurveConstraint extends Constraint {
	// inhereted properties from "Entity"
	//public int type;
	//public String name;
	//public int tag;
	//inherited properties from "Constraints"
	//public int contype; // 1=make_vertical, 2=make_horizontal, 3=point_on_paramcurve
	//public Entity[] targets; //for this it only applies to a single Line3D object
	public double param;
	
	public PointOnCurveConstraint() {
		type = Entity.CONSTRAINT;
		name = "PointOnCurve_Constraint";
		tag = 0;
		contype = Constraint.POINTONCURVE;
		targets = new Entity[0];
		param = -1;
	}
	public PointOnCurveConstraint(Point3D pt, ParamCurve crv, double in_param) {
		type = Entity.CONSTRAINT;
		name = "PointOnCurve_Constraint";
		tag = 0;
		contype = Constraint.POINTONCURVE;
		targets = new Entity[] {pt,crv};
		param = in_param;
	}
	// for this constraint, we align the second point to the first in the X direction
	public void execute(GeomManager geom_manager) {
		Point3D target = (Point3D) targets[0];
		ParamCurve crv = (ParamCurve) targets[1];
		Point3D new_loc = crv.evaluate(param);
		target.x = new_loc.x;
		target.y = new_loc.y;
		target.z = new_loc.z;
	}
}
