package analysis;

import data.Entity;
import geom.Point3D;

public class Node3D extends Point3D {
	public int val_cnt; //we will define analysis variables (k, T, etc.) in a value array
	public double[] values;
	public int parent; //tag of the parent geometry, -1 if there is no parent
	public int bcflag; //boundary condition flag, 0=no BC, 1....other BC's
	//---Inhereted from Point3D
	//public double x;
	//public double y;
	//public double z;
	//---Inhereted from Entity---
	//public int type;
	//public int tag;
	//public String name;

	public Node3D() {
		val_cnt = 0;
		values = new double[val_cnt];
		x = 0.0;
		y = 0.0;
		z = 0.0;
		parent = -1;
		bcflag = 0;
		type = Entity.NODE;
		tag = 0;
		name = "Node";
	}
	public Node3D(double in_x, double in_y, double in_z) {
		val_cnt = 0;
		values = new double[val_cnt];
		x = in_x;
		y = in_y;
		z = in_z;
		bcflag = 0;
		parent = -1;
		type = Entity.NODE;
		tag = 0;
		name = "Node";
	}
	public Node3D(Point3D pt) {
		val_cnt = 0;
		values = new double[val_cnt];
		x = pt.x;
		y = pt.y;
		z = pt.z;
		bcflag = 0;
		parent = -1;
		type = Entity.NODE;
		tag = 0;
		name = "Node";
	}	
	public Node3D(double in_x, double in_y, double in_z, int in_val_cnt) {
		val_cnt = in_val_cnt;
		values = new double[val_cnt];
		for(int i=0;i<val_cnt;i++) {
			values[i] = 0.0;
		}
		x = in_x;
		y = in_y;
		z = in_z;
		bcflag = 0;
		parent = -1;
		type = Entity.NODE;
		tag = 0;
		name = "Node";
	}
	public Node3D(double in_x, double in_y, double in_z, double[] in_vals) {
		val_cnt = in_vals.length;
		values = new double[val_cnt];
		for(int i=0;i<val_cnt;i++) {
			values[i] = in_vals[i];
		}
		x = in_x;
		y = in_y;
		z = in_z;
		bcflag = 0;
		parent = -1;
		type = Entity.NODE;
		tag = 0;
		name = "Node";
	}
	public Node3D(Point3D pt, int in_val_cnt) {
		val_cnt = in_val_cnt;
		values = new double[val_cnt];
		for(int i=0;i<val_cnt;i++) {
			values[i] = 0.0;
		}
		x = pt.x;
		y = pt.y;
		z = pt.z;
		parent = -1;
		bcflag = 0;
		type = Entity.NODE;
		tag = 0;
		name = "Node";
	}
	//--------------------
	// Node3D File IO
	// Node3D File String Format)
	// ent_type,tag,name:val_cnt,bcflag,parent,x,y,z,value1,...,valueN
	public String getFileString() {
		String output = Integer.toString(type) + "," + Integer.toString(tag) + "," + name + ":";
		output = output + Integer.toString(val_cnt) + "," + Integer.toString(bcflag) + "," + Integer.toString(parent);
		output = output + "," + Double.toString(x) + "," + Double.toString(y) + "," + Double.toString(z);
		for(int i=0;i<val_cnt;i++) {
			output = output + "," + Double.toString(values[i]);
		}
		return output;
	}
	public Node3D(String filestring) {
		System.out.println(filestring);
		String[] parts = filestring.split(":");
		String[] header = parts[0].split(",");
		String[] data = parts[1].split(",");
		tag = Integer.valueOf(header[1]);
		type = Entity.NODE;
		name = "Node";
		
		val_cnt = Integer.valueOf(data[0]);
		bcflag = Integer.valueOf(data[1]);
		parent = Integer.valueOf(data[2]);
		x = Double.valueOf(data[3]);
		y = Double.valueOf(data[4]);;
		z = Double.valueOf(data[5]);;
		values = new double[val_cnt];
		for(int i=0;i<val_cnt;i++) {
			int ci = i+6;
			values[i] = Double.valueOf(data[ci]);;
		}
	}
}
