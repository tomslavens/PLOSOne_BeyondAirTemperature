package linear_math;

import geom.Line3D;
import geom.Point3D;

public class Vector3D {
	public double u; //unit vector in x
	public double v; //unit vector in y
	public double w; //unit vector in z
	public double magnitude; //magnitude of the vector

	//default is a unit vector in the x-direction
	public Vector3D() {
		u = 1.0;
		v = 0.0;
		w = 0.0;
		magnitude = 1.0;
	}
	public Vector3D(double in_u, double in_v, double in_w) {
		u = in_u;
		v = in_v;
		w = in_w;
		magnitude = Math.sqrt(u*u + v*v + w*w);
	}
	public Vector3D(double in_u, double in_v, double in_w, double in_mag) {
		u = in_u;
		v = in_v;
		w = in_w;
		magnitude = in_mag;
	}
	//add a two-point vector from pt1 to pt2
	public Vector3D(Point3D pt1, Point3D pt2) {
		magnitude = pt1.distTo(pt2); //calculate the magnitude of the vector
		u = (double) (pt2.x-pt1.x)/magnitude;
		v = (double) (pt2.y-pt1.y)/magnitude;
		w = (double) (pt2.z-pt1.z)/magnitude;
	}
	//add a vector based on a Line3D
	public Vector3D(Line3D line) {
		magnitude = line.geompts[0].distTo(line.geompts[1]);
		u = (double) (line.geompts[1].x - line.geompts[0].x)/magnitude;
		v = (double) (line.geompts[1].y - line.geompts[0].y)/magnitude;
		w = (double) (line.geompts[1].z - line.geompts[0].z)/magnitude;
	}
	public double dot(Vector3D v2) {
		double val = u*v2.u + v*v2.v + w*v2.w;
		return val;
	}
	public double magnitude() {
		double val = Math.sqrt(u*u + v*v + w*w);
		return val;
	}
	public Vector3D getReverseDirection() {
		Vector3D reverse = new Vector3D(-u,-v,-w);
		return reverse;
	}
	public String toString() {
		String output = "<" + Double.toString(u) + "," + Double.toString(v) + "," + Double.toString(w) + ">";
		return output;
	}
}
