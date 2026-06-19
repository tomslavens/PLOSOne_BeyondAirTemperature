package geom;

import javax.swing.JTextArea;

import data.Entity;
import data.GeomManager;

public class Line3D extends ParamCurve {
	//public int curvetype;
	//public Point3D[] geompts;
	//public int type;
	//public int tag;
	//public String name;
	
	public Line3D() {
		geompts = new Point3D[0];
		type = Entity.PARAMCURVE;
		curvetype = ParamCurve.LINE3D;
		tag = 0;
		name = "Line3D";
	}
	public Line3D(Point3D pt1, Point3D pt2) {
		geompts = new Point3D[2];
		geompts[0] = pt1;
		geompts[1] = pt2;
		type = Entity.PARAMCURVE;
		curvetype = ParamCurve.LINE3D;
		tag = 0;
		name = "Line3D";
	}
	public Point3D evaluate(double t, JTextArea outwindow) {
		double dx = geompts[1].x-geompts[0].x;
		double dy = geompts[1].y-geompts[0].y;
		double dz = geompts[1].z-geompts[0].z;

		double x = geompts[0].x + t*dx;
		double y = geompts[0].y + t*dy;
		double z = geompts[0].z + t*dz;
		
		Point3D eval = new Point3D(x,y,z);
		///System.out.println(Nsum);  // just a check to see if the recursion formula is correct....
		return eval;
	}
	public Point3D evaluate(double t) {
		double dx = geompts[1].x-geompts[0].x;
		double dy = geompts[1].y-geompts[0].y;
		double dz = geompts[1].z-geompts[0].z;

		double x = geompts[0].x + t*dx;
		double y = geompts[0].y + t*dy;
		double z = geompts[0].z + t*dz;
		
		Point3D eval = new Point3D(x,y,z);
		///System.out.println(Nsum);  // just a check to see if the recursion formula is correct....
		return eval;
	}
	public Point3D getMidPoint() {
		double xmid = (geompts[0].x+geompts[1].x)/2.0;
		double ymid = (geompts[0].y+geompts[1].y)/2.0;
		double zmid = (geompts[0].z+geompts[1].z)/2.0;
		
		Point3D pt = new Point3D(xmid,ymid,zmid);
		return pt;
	}
	public double getLength() {
		double length = Math.sqrt(Math.pow((geompts[1].x-geompts[0].x), 2.0) + Math.pow((geompts[1].y-geompts[0].y), 2.0) + Math.pow((geompts[1].z-geompts[0].z), 2.0));
		return length;
	}
	
	//-----------
	// Line3D FileIO
	// Line3D File Structure
	// ent_type,tag,name:curve_type,pt1,pt2
	public String getFileString() {
		String output = Integer.toString(type) + "," + Integer.toString(tag) + "," + name + ":";
		output = output + Integer.toString(curvetype) + "," + Integer.toString(geompts[0].tag) + "," + Integer.toString(geompts[1].tag);
		return output;
	}
	public Line3D(String filestring, GeomManager geom_manager) {
		String[] parts = filestring.split(":");
		
		//parse header data
		String[] header = parts[0].split(",");
		type = Integer.valueOf(header[0]);
		tag = Integer.valueOf(header[1]);
		name = header[2];
		curvetype = ParamCurve.LINE3D;
		
		//parse the data
		String[] data = parts[1].split(",");
		int pt1 = Integer.valueOf(data[1].trim());
		int pt2 = Integer.valueOf(data[2].trim());
		geompts = new Point3D[2];
		geompts[0] = (Point3D) geom_manager.getEntity(pt1);
		geompts[1] = (Point3D) geom_manager.getEntity(pt2);
	}
}
