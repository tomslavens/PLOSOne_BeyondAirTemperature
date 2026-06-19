package geom_funcs;

import data.Entity;
import data.GeomManager;
import data.GeometryUpdateFunctions;
import geom.Line3D;

public class ISOYConstraint extends Constraint {
	// inhereted properties from "Entity"
	//public int type;
	//public String name;
	//public int tag;
	//inherited properties from "Constraints"
	//public int contype; // 1=make_vertical, 2=make_horizontal, 3=point_on_paramcurve
	//public Entity[] targets; //for this it only applies to a single Line3D object
	
	public ISOYConstraint() {
		type = Entity.CONSTRAINT;
		name = "ISO-Y_Constraint";
		tag = 0;
		contype = Constraint.ISO_Y;
		targets = new Entity[0];
	}
	public ISOYConstraint(Line3D in_line) {
		type = Entity.CONSTRAINT;
		name = "ISO-Y_Constraint";
		tag = 0;
		contype = Constraint.ISO_Y;
		targets = new Entity[] {in_line};
	}
	// for this constraint, we align the second point to the first in the X direction
	public void execute(GeomManager geom_manager) {
		Line3D target = (Line3D) targets[0];
		boolean pt0_cnst = GeometryUpdateFunctions.ptIsConstrained(target.geompts[0].tag, geom_manager);
		boolean pt1_cnst = GeometryUpdateFunctions.ptIsConstrained(target.geompts[1].tag, geom_manager);
		
		//if both points are unconstrained or pt0 is constrained with pt1 floating
		if((!pt0_cnst && !pt1_cnst) || (pt0_cnst && !pt1_cnst))
			target.geompts[1].y = target.geompts[0].y;
		//if pt1 is constrained but pt0 is not
		if((!pt0_cnst && pt1_cnst))
			target.geompts[0].y = target.geompts[1].y;
	}
}
