package geom;

import data.Entity;

public class Point3D extends Entity {
	public double x;
	public double y;
	public double z;
	//public int type;
	//public int tag;
	//public String name;
	
	public Point3D() {
		x = 0;
		y = 0;
		z = 0;
		type = 0;
		tag = 0;
		name = "Point3D";
	}
	public Point3D(double in_x, double in_y, double in_z) {
		x = in_x;
		y = in_y;
		z = in_z;
		type = 0;
		tag = 0;
		name = "Point3D";
	}
	public String toString() {
		String output = "<" + Double.toString(x) + "," + Double.toString(y) + "," + Double.toString(z) + ">";
		return output;
	}
	public double distTo(Point3D cpt) {
		double dist = Math.sqrt(Math.pow(x-cpt.x, 2) + Math.pow(y-cpt.y, 2) + Math.pow(z-cpt.z, 2));
		return dist;
	}
	public double sqareDistTo(Point3D cpt) {
		double dist = Math.pow(x-cpt.x, 2) + Math.pow(y-cpt.y, 2) + Math.pow(z-cpt.z, 2);
		return dist;
	}
	
	//-----------
	// Point3D FileIO
	// Point3D File Structure
	// ent_type,tag,name:x,y,z
	public String getFileString() {
		String output = Integer.toString(type) + "," + Integer.toString(tag) + "," + name + ":";
		output = output + Double.toString(x) + "," + Double.toString(y) + "," + Double.toString(z);
		return output;
	}
	public String getRawFileString() {
		String output = Double.toString(x) + "," + Double.toString(y) + "," + Double.toString(z);
		return output;
	}
	public Point3D(String filestring) {
		String[] parts = filestring.split(":");
		String[] header = parts[0].split(",");
		String[] data = parts[1].split(",");
		tag = Integer.valueOf(header[1]);
		type = Entity.POINT3D;
		name = "Point3D";

		x = Double.valueOf(data[0]);
		y = Double.valueOf(data[1]);
		z = Double.valueOf(data[2]);
	}
}
