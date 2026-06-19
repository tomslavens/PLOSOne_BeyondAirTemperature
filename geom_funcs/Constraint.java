package geom_funcs;

import data.Entity;
import data.GeomManager;
import geom.Line3D;
import geom.Point3D;
import linear_math.Vector3D;

public abstract class Constraint extends Entity {
	
	// inhereted properties from "Entity"
	//public int type;
	//public String name;
	//public int tag;
	public int contype; // 1=make_vertical, 2=make_horizontal, 3=make_iso, 4=length_dim, 5=length_in_space, 6=point_on_paramcurve
	public Entity[] targets;
	public static int ISO_X=1;
	public static int ISO_Y=2;
	public static int ISO_Z=3;
	public static int LENGTH2D=4;
	public static int LENGTH3D=5;
	public static int POINTONCURVE=6;
	public static int POINTATLOC=7;
	
	public abstract void execute(GeomManager geom_manager);

	//-----------
	// BoundedSurface FileIO
	// BoundedSurface File Structure
	// ent_type,tag,name:contype,targ_cnt,targ1,...,targ2
	public String getFileString() {
		String output = Integer.toString(type) + "," + Integer.toString(tag) + "," + name + ":";
		output = output + Integer.toString(contype) + "," + Integer.toString(targets.length);
		for(int i=0;i<targets.length;i++) {
			output = output + "," + Integer.toString(targets[i].tag);
		}		
		return output;
	}
	public static Constraint getConstraintFromString(String line, GeomManager geom_manager) {
		String[] header = line.split(":")[0].split(",");
		String[] data = line.split(":")[1].split(",");
		int type = Integer.valueOf(data[0]);
		Constraint con = null;
		if(type == ISO_X) {
			Line3D cent = (Line3D) geom_manager.getEntity(Integer.valueOf(data[2]));
			ISOXConstraint econ = new ISOXConstraint(cent);
			con = econ;
			con.tag = Integer.valueOf(header[1]);
		}
		if(type == ISO_Y) {
			Line3D cent = (Line3D) geom_manager.getEntity(Integer.valueOf(data[2]));
			ISOYConstraint econ = new ISOYConstraint(cent);
			con = econ;
			con.tag = Integer.valueOf(header[1]);
		}
		if(type == LENGTH2D) {
			Line3D cent = (Line3D) geom_manager.getEntity(Integer.valueOf(data[2]));
			Length2DConstraint econ = new Length2DConstraint(cent,new Vector3D(cent.geompts[0],cent.geompts[1]));
			con = econ;
			con.tag = Integer.valueOf(header[1]);
		}
		if(type == POINTATLOC) {
			Point3D cent = (Point3D) geom_manager.getEntity(Integer.valueOf(data[2]));
			PointAtLocationConstraint pcon = new PointAtLocationConstraint(cent, cent.x,cent.y,cent.z);
			con = pcon;
			con.tag = Integer.valueOf(header[1]);
		}
		if(type == POINTONCURVE) {
			con = new PointOnCurveConstraint();
			con.tag = Integer.valueOf(header[1]);
		}
		return con;
	}
}
