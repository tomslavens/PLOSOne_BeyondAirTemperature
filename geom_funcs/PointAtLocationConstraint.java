package geom_funcs;

import data.Entity;
import data.GeomManager;
import geom.Line3D;
import geom.ParamCurve;
import geom.Point3D;

public class PointAtLocationConstraint extends Constraint {
	// inhereted properties from "Entity"
	//public int type;
	//public String name;
	//public int tag;
	//inherited properties from "Constraints"
	//public int contype; // 1=make_vertical, 2=make_horizontal, 3=point_on_paramcurve
	//public Entity[] targets; //for this it only applies to a single Line3D object
	public Point3D location;
	
	public PointAtLocationConstraint() {
		type = Entity.CONSTRAINT;
		name = "PointAtLocation_Constraint";
		tag = 0;
		contype = Constraint.POINTATLOC;
		targets = new Entity[0];
		location = new Point3D(0,0,0);
	}
	public PointAtLocationConstraint(Point3D target, double x, double y, double z) {
		type = Entity.CONSTRAINT;
		name = "PointAtLocation_Constraint";
		tag = 0;
		contype = Constraint.POINTATLOC;
		targets = new Entity[] {target};
		location = new Point3D(x,y,z);
	}
	// for this constraint, we align the second point to the first in the X direction
	public void execute(GeomManager geom_manager) {
		Point3D target = (Point3D) targets[0];
		target.x = location.x;
		target.y = location.y;
		target.z = location.z;
	}
}
