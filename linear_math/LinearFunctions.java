package linear_math;

import geom.Line3D;
import geom.Point3D;

//--------------
// This class contains linear algebra operators for Vectors and Matrices
public class LinearFunctions {

	//getXYPerpendicularVector - this will give the right-hand perpendicular vector in the X-Y plane for a given vector
	public static Vector3D getXYPerpendicularVector(Vector3D vect) {
		Vector3D perp_vector = new Vector3D(-vect.v, vect.u, vect.w,1.0);
		return perp_vector;
	}
	public static Vector3D getXZPerpendicularVector(Vector3D vect) {
		Vector3D perp_vector = new Vector3D(vect.w, vect.v, -vect.u,1.0);
		return perp_vector;
	}
	public static Vector3D getYZPerpendicularVector(Vector3D vect) {
		Vector3D perp_vector = new Vector3D(vect.u, -vect.w, vect.v,1.0);
		return perp_vector;
	}
	public static Point3D offsetPointAlongVector(Vector3D vect, Point3D pt) {
		double dx = vect.u*vect.magnitude;
		double dy = vect.v*vect.magnitude;
		double dz = vect.w*vect.magnitude;
		
		Point3D dpt = new Point3D(pt.x+dx, pt.y+dy, pt.z+dz);
		return dpt;
	}
	public static Point3D offsetPointAlongVector(Vector3D vect, Point3D pt, double dist) {
		double dx = vect.u*dist;
		double dy = vect.v*dist;
		double dz = vect.w*dist;
		
		Point3D dpt = new Point3D(pt.x+dx, pt.y+dy, pt.z+dz);
		return dpt;
	}
	public static Point3D midPoint(Point3D pt1, Point3D pt2) {
		double mx = (pt1.x+pt2.x)/2.0;
		double my = (pt1.y+pt2.y)/2.0;
		double mz = (pt1.z+pt2.z)/2.0;
		return new Point3D(mx,my,mz);
	}
	public static void offsetLinePoints(Vector3D vect, Line3D line) {
		double dx = vect.u*vect.magnitude;
		double dy = vect.v*vect.magnitude;
		double dz = vect.w*vect.magnitude;
		
		line.geompts[1].x = line.geompts[0].x + dx;
		line.geompts[1].y = line.geompts[0].y + dy;
		line.geompts[1].z = line.geompts[0].z + dz;
	}
	public static boolean lineSegmentsIntersect(Line3D line1, Line3D line2) {
		double px1_1 = line1.geompts[0].x;
		double py1_1 = line1.geompts[0].y;
		double px1_2 = line1.geompts[1].x;
		double py1_2 = line1.geompts[1].y;
		
		double tx1_1 = line2.geompts[0].x;
		double ty1_1 = line2.geompts[0].y;
		double tx1_2 = line2.geompts[1].x;
		double ty1_2 = line2.geompts[1].y;
		
		double aT = (px1_1-tx1_1)*(ty1_1-ty1_2)-(py1_1-ty1_1)*(tx1_1-tx1_2);
		double bT = (px1_1-px1_2)*(ty1_1-ty1_2)-(py1_1-py1_2)*(tx1_1-tx1_2);
		double aU = (px1_2-px1_1)*(py1_1-ty1_1)-(py1_2-py1_1)*(px1_1-tx1_1);
		
		double T = aT/bT;
		double U = aU/bT;
		
		boolean intersects = false;
		if(0.0 <= T && T <= 1.0 && 0.0 <= U && U <= 1.0)
			intersects = true;
		
		return intersects;
	}
	
    //calculate the determinant of a matrix
    public static double determinant(double[][] mat) {
        double result = 0;
        if (mat.length == 1) {
            result = mat[0][0];
            return result;
        }
        if (mat.length == 2) {
            result = mat[0][0] * mat[1][1] - mat[0][1] * mat[1][0];
            return result;
        }
        for (int i = 0; i < mat[0].length; i++) {
            double temp[][] = new double[mat.length - 1][mat[0].length - 1];
            for (int j = 1; j < mat.length; j++) {
                System.arraycopy(mat[j], 0, temp[j - 1], 0, i);
                System.arraycopy(mat[j], i + 1, temp[j - 1], i, mat[0].length - i - 1);
            }
            result += mat[0][i] * Math.pow(-1, i) * determinant(temp);
        }
        return result;
    }
    
    /*calculate the cross product of XY vector
    """ 2D cross product of OA and OB vectors,
    i.e. z-component of their 3D cross product.
   :param o: point O
   :param a: point A
   :param b: point B
   :return cross product of vectors OA and OB (OA x OB),
    positive if OAB makes a counter-clockwise turn,
    negative for clockwise turn, and zero
    if the points are colinear.*/
    public static double crossXY(Point3D o, Point3D a,Point3D b) {
    	double vax = a.x-o.x;
    	double vay = a.y-o.y;
    	double vbx = b.x-o.x;
    	double vby = b.y-o.y;
    	
    	double xprod = (vax*vby) - (vbx*vay);
    	return xprod;
    }
    
    // calcVectorAngle(Vect1, Vect2) - calculates the inner angle between two vectors
    public static double calcVectorAngle(Vector3D v1, Vector3D v2) {    	
    	double val = v1.dot(v2)/(v1.magnitude*v2.magnitude);
    	double angle = Math.acos(val);
    	return angle;
    }
    
    //get the average of an array
    public static double average(double[] v1) {
    	double ave = 0.0;
    	for(int i=0;i<v1.length;i++) {
    		ave = ave + v1[i];
    	}
    	ave = (double) ave/v1.length;
    	return ave;
    }
    //get the standard deviation of an array
    public static double stdev(double[] v1) {
    	double ave = average(v1);
    	double diffs = 0.0;
    	for(int i=0;i<v1.length;i++) {
    		diffs = diffs + Math.pow(v1[i]-ave, 2);
    	}
    	diffs = (double) diffs/v1.length;
    	double st_dev = Math.sqrt(diffs);
    	return st_dev;
    }
}
