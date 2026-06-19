package data;

import java.util.ArrayList;

import analysis.BoundaryCondition;
import geom.NURBArc;
import geom.ParamCurve;
import geom.Point3D;
import geom_funcs.Constraint;

public class GeometryUpdateFunctions {

	public static void updateGeometry(GeomManager geom_manager) {
		for(int i=0;i<geom_manager.crvCnt();i++) {
			if(geom_manager.crvAt(i).curvetype == ParamCurve.NURBARC) {
				NURBArc arc = (NURBArc) geom_manager.crvAt(i);
				arc.updateArc();
			}
		}
		//---------
		// Geometry Constraint Update Region
		// Order:
		// 1. PointOnLocation
		// 2. ISOX/ISOY constraints
		// 3. Length2DConstraint
		// --ignore PtOnCurveConstraints--
		
		// 1. PointOnLocation
		for(int i=0;i<geom_manager.constCnt();i++) {
			Constraint craint = geom_manager.constraitnAt(i);
			if(craint.contype == Constraint.POINTATLOC)
				geom_manager.constraitnAt(i).execute(geom_manager);
		}
		// 2. ISOX/ISOY constraints
		for(int i=0;i<geom_manager.constCnt();i++) {
			Constraint craint = geom_manager.constraitnAt(i);
			if(craint.contype == Constraint.ISO_X || craint.contype == Constraint.ISO_Y)
				geom_manager.constraitnAt(i).execute(geom_manager);
		}
		// 3. Length2DConstraint
		for(int i=0;i<geom_manager.constCnt();i++) {
			Constraint craint = geom_manager.constraitnAt(i);
			if(craint.contype == Constraint.LENGTH2D)
				geom_manager.constraitnAt(i).execute(geom_manager);
		}
		//---------
		
		for(int i=0;i<geom_manager.tSurfCnt();i++) {
			geom_manager.tsrfAt(i).updateMesh();
		}
	}
	public static boolean ptIsConstrained(int pttag, GeomManager geom_manager) {
		boolean constrained = false;
		for(int i=0;i<geom_manager.constCnt();i++) {
			Constraint craint = geom_manager.constraitnAt(i);
			if(craint.contype == Constraint.POINTATLOC) {
				Point3D target = (Point3D) craint.targets[0];
				if(target.tag == pttag)
					constrained = true;
			}
		}
		return constrained;
	}
}
