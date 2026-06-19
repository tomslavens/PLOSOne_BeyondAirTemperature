package geom;

import java.util.ArrayList;

import javax.swing.JTextArea;

import data.Entity;
import linear_math.LinearFunctions;
import linear_math.Vector3D;

public abstract class ParamCurve extends Entity {
	public Point3D[] geompts;
	public int curvetype;
	//public int type;
	//public int tag;
	//public String name;
	public static int BSPLINECRV = 0;
	public static int NURBCRV = 1;
	public static int LINE3D = 2;
	public static int NURBARC = 3;
	public static int OFFSETCRV = 4;

	public abstract Point3D evaluate(double t, JTextArea outwindow);
	public abstract Point3D evaluate(double t);
	
	//==========================
	// Geom Calculations
	//distToPoint() - returns the closest distance to the given point
	public double distToPoint(Point3D pt) {
		double t = 0.0;
		double dt = 0.01;
		Point3D loc = evaluate(t);
		
		double dist = loc.distTo(pt);
		t = t+dt;
		while(t<=1.0) {
			loc = evaluate(t);
			double cdist = loc.distTo(pt);
			if(cdist < dist)
				dist = cdist;
			t = t+dt;
		}
		return dist;
	}
	//paramToPoint - this will return the parameter value that is the closest point to the curve
	public double paramToPoint(Point3D pt) {
		double t = 0.0;
		double dt = 0.01;
		Point3D loc = evaluate(t);
		double mint = -1;
		
		double dist = loc.distTo(pt);
		t = t+dt;
		while(t<=1.0) {
			loc = evaluate(t);
			double cdist = loc.distTo(pt);
			if(cdist < dist ) {
				dist = cdist;
				mint = t;
			}
			t = t+dt;
		}
		return mint;
	}
	//getNormAtParam - will calculate the normal in the X-Y plane at this point 
	public Vector3D getNormXYAtParam(double t) {
		Point3D pt1 = evaluate(t);
		Point3D pt2 = evaluate(t+0.005);
		Vector3D tangent = new Vector3D(pt1,pt2);
		return LinearFunctions.getXYPerpendicularVector(tangent);		
	}
	//getCurveLength - this will return the length of this cuve
	public double getCurveLength() {
		double tot_length = 0.0;
		double t = 0.0;
		double dt = 0.005;
		while(t<(1.0-dt)) {
			Point3D loc1 = evaluate(t);
			Point3D loc2 = evaluate(t+dt);
			
			double cdist = loc1.distTo(loc2);
			tot_length = tot_length+cdist;
			t = t+dt;
		}
		return tot_length;
	}
	//getPointSet - this will create a set of points along this curve that are spaced DIST apart
	public ArrayList<Point3D> getPointSet(double dist) {
		ArrayList<Point3D> pts = new ArrayList<Point3D>();		
		double t = 0.0;
		double dt = 0.0001;
		Point3D loc = evaluate(t);
		pts.add(loc);
		
		t=t+dt;
		Point3D nloc = evaluate(t);
		double ndist = nloc.distTo(loc);
		while(t<1.0) {
			if(ndist >= dist) {
				pts.add(nloc);
				loc = nloc;
			}
			t=t+dt;
			nloc = evaluate(t);
			ndist = nloc.distTo(loc);
		}
		return pts;
	}
	//getPointSet - this will create a set of points along this curve that are spaced DIST apart
	public ArrayList<Point3D> getPointSet(int pt_num) {
		ArrayList<Point3D> pts = new ArrayList<Point3D>();
		double t = 0.0;
		double dt = (double) 1.0/(pt_num-1);
		
		for(int i=0;i<pt_num;i++) {
			Point3D loc = evaluate(t);
			pts.add(loc);
			
			t=t+dt;
		}
		return pts;
	}
	public ArrayList<Line3D> getSegmentSet() {
		ArrayList<Line3D> segments = new ArrayList<Line3D>();
		double t = 0.0;
		double dt = 0.1;
		while(t<(1.0-dt)) {
			Point3D loc1 = evaluate(t);
			Point3D loc2 = evaluate(t+dt);
			
			Line3D line2 = new Line3D(loc1,loc2);
			segments.add(line2);
			t = t+dt;
		}
		return segments;
	}
	public ArrayList<Line3D> getSegmentSet(int cnt) {
		ArrayList<Line3D> segments = new ArrayList<Line3D>();
		double t = 0.0;
		double dt = 0.999/(cnt);
		while(t<(1.0-dt)) {
			Point3D loc1 = evaluate(t);
			Point3D loc2 = evaluate(t+dt);
			
			Line3D line2 = new Line3D(loc1,loc2);
			segments.add(line2);
			t = t+dt;
		}
		return segments;
	}
	public void flipPoints() {
		Point3D[] b = new Point3D[geompts.length];
        int j = geompts.length;
        for (int i = 0; i < geompts.length; i++) {
            b[j - 1] = geompts[i];
            j = j - 1;
        }
        geompts = b;
	}
	public boolean intersectsLineSegment(Point3D pt1, Point3D pt2) {
		boolean intersects = false;
		double t = 0.0;
		double dt = 0.1;
		Line3D line1 = new Line3D(pt1,pt2);
		while(t<(1.0-dt)) {
			Point3D loc1 = evaluate(t);
			Point3D loc2 = evaluate(t+dt);
			
			Line3D line2 = new Line3D(loc1,loc2);
			if(LinearFunctions.lineSegmentsIntersect(line1, line2)) {
				intersects = true;
				break;
			}			
			t=t+dt;
		}
		return intersects;
	}
}
