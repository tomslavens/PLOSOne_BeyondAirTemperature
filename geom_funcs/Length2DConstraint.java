package geom_funcs;

import data.Entity;
import data.GeomManager;
import data.GeometryUpdateFunctions;
import geom.Line3D;
import linear_math.LinearFunctions;
import linear_math.Vector3D;

public class Length2DConstraint extends Constraint {
	// inhereted properties from "Entity"
	//public int type;
	//public String name;
	//public int tag;
	//inherited properties from "Constraints"
	//public int contype; // 1=make_vertical, 2=make_horizontal, 3=point_on_paramcurve
	//public Entity[] targets
	public Vector3D vector; //length of the line
	
	public Length2DConstraint() {
		type = Entity.CONSTRAINT;
		name = "Length_Constraint";
		tag = 0;
		contype = Constraint.LENGTH2D;
		targets = new Entity[0];
		vector = null;
	}
	public Length2DConstraint(Line3D in_line, Vector3D in_vector) {
		type = Entity.CONSTRAINT;
		name = "Length_Constraint";
		tag = 0;
		contype = Constraint.LENGTH2D;
		targets = new Entity[] {in_line};
		vector = in_vector;
	}
	
	
	// for this constraint, we offset the second point to the first point
	public void execute(GeomManager geom_manager) {
		Line3D target = (Line3D) targets[0];
		double dx = vector.u*vector.magnitude;
		double dy = vector.v*vector.magnitude;
		double dz = vector.w*vector.magnitude;
		
		target.geompts[1].x = target.geompts[0].x + dx;
		target.geompts[1].y = target.geompts[0].y + dy;
		target.geompts[1].z = target.geompts[0].z + dz;
		//LinearFunctions.offsetLinePoints(vector, target);
		
		boolean pt0_cnst = GeometryUpdateFunctions.ptIsConstrained(target.geompts[0].tag, geom_manager);
		boolean pt1_cnst = GeometryUpdateFunctions.ptIsConstrained(target.geompts[1].tag, geom_manager);		
		//if both points are unconstrained or pt0 is constrained with pt1 floating
		if((!pt0_cnst && !pt1_cnst) || (pt0_cnst && !pt1_cnst)) {
			target.geompts[1].x = target.geompts[0].x + dx;
			target.geompts[1].y = target.geompts[0].y + dy;
			target.geompts[1].z = target.geompts[0].z + dz;
		}
		//if pt1 is constrained but pt0 is not
		if((!pt0_cnst && pt1_cnst)) {
			target.geompts[0].x = target.geompts[1].x + dx;
			target.geompts[0].y = target.geompts[1].y + dy;
			target.geompts[0].z = target.geompts[1].z + dz;
		}
	}

}
