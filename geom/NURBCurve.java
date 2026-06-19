package geom;

import javax.swing.JTextArea;

import data.Entity;
import data.GeomManager;

public class NURBCurve extends ParamCurve {
	public int order;  //p polynomial order of the spline
	public double[] knots; //knot vector having p+n+1 knots
	public double[] weights; //weight vector
	//public Point3D[] geompts; //vector of 3D points
	//public int type;
	
	public NURBCurve() {
		order = 0;
		knots = new double[0];
		weights = new double[0];
		geompts = new Point3D[0];
		type = Entity.PARAMCURVE;
		curvetype = ParamCurve.NURBCRV;
		tag = 0;
		name = "NURBCurve";	
	}
	public NURBCurve(int in_order, int ptcnt) {
		order = in_order;
		knots = new double[ptcnt+in_order];
		weights = new double[ptcnt+in_order];
		geompts = new Point3D[ptcnt];
		type = Entity.PARAMCURVE;
		curvetype = ParamCurve.NURBCRV;
		tag = 0;
		name = "NURBCurve";
	}
	//------
	// evaluate(double param, JtextArea outwindow) - this will take a parameter and return the geometric point for this parameter
	public Point3D evaluate(double param, JTextArea outwindow) {
		double x = 0;
		double y = 0;
		double z = 0;
		
		double Nsum = 0.0;
		for(int i=0;i<geompts.length;i++) {
			double cN = NURB.getBasisFunction(i,param,order,knots,weights,outwindow);
			Nsum = Nsum + cN;
			x = x + geompts[i].x*cN;
			y = y + geompts[i].y*cN;
			z = z + geompts[i].z*cN;
		}
		Point3D eval = new Point3D(x,y,z);
		///System.out.println(Nsum);  // just a check to see if the recursion formula is correct....
		return eval;
	}
	// evaluateDerivative(param, derivative, outwindow) - evaluates the nth derivative of this BSpline at parameter value param
	public Point3D evaluateDerivative(double param, int derivative, JTextArea outwindow) {
		double x = 0;
		double y = 0;
		double z = 0;
		
		double Nsum = 0.0;
		for(int i=0;i<geompts.length;i++) {
			double cN = NURB.getBasisFirstDerivative(i,param,order,knots,weights,outwindow);
			Nsum = Nsum + cN;
			x = x + geompts[i].x*cN;
			y = y + geompts[i].y*cN;
			z = z + geompts[i].z*cN;
		}
		Point3D eval = new Point3D(x,y,z);
		///System.out.println(Nsum);  // just a check to see if the recursion formula is correct....
		return eval;
	}
	/*=================================
	 * NON-VERBOSE EVALUATE METHOD FOR PLOTTING STUFF	 * 
	 */
	public Point3D evaluate(double param) {
		double x = 0;
		double y = 0;
		double z = 0;
		
		double Nsum = 0.0;
		for(int i=0;i<geompts.length;i++) {
			double cN = NURB.getBasisFunction(i,param,order,knots,weights,null);
			Nsum = Nsum + cN;
			x = x + geompts[i].x*cN;
			y = y + geompts[i].y*cN;
			z = z + geompts[i].z*cN;
		}
		Point3D eval = new Point3D(x,y,z);
		///System.out.println(Nsum);  // just a check to see if the recursion formula is correct....
		return eval;
	}

	// this creates a uniform BSpline with acsending positive knot vector
	// the spline has n pts
	// 2 <= order <= n
	// knots have multiplicity of "order" at the ends
	// knot vector size is order+n+1
	public static NURBCurve createUniformOpenNURBCurve(int in_order, Point3D[] in_pts, JTextArea outwindow) {
		if(in_order > in_pts.length || in_order < 2) {
			in_order = 2;
			outwindow.append("WARNING: input order for spline is greater than # of control points or less than 2, order set to " + Integer.toString(in_order) + "\n");
		}
		NURBCurve spline = new NURBCurve(in_order, in_pts.length);
		spline.knots = new double[in_pts.length+in_order];
		spline.weights = new double[in_pts.length];
		for(int i=0;i<in_pts.length;i++) {
			spline.geompts[i] = in_pts[i];
			spline.weights[i] = 1.0;
		}
		//setup knot vector and weights
		for(int i=0;i<spline.knots.length;i++) {
			if(i>=0 && i<in_order)
				spline.knots[i] = 0.0;
			if(i>=in_order && i<in_pts.length)
				spline.knots[i] = (i+1) - in_order;
			if(i>=in_pts.length)
				spline.knots[i] = in_pts.length- in_order + 1;
		}
		//normalize the knot vector
		for(int i=0;i<spline.knots.length;i++) {
			spline.knots[i] = spline.knots[i]/(spline.knots[spline.knots.length-1]);
		}
		return spline;
	}
	
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
	
	//-----------
	// NURBCurve FileIO
	// NurbCurve File Structure
	// ent_type,tag,name:curve_type,order,knot_cnt,weight_cnt,pt_cnt
	// knot1,...,knotN
	// weight1,...,weightN
	// pt1,...ptN
	public String getFileString() {
		String output = Integer.toString(type) + "," + Integer.toString(tag) + "," + name + ":";
		output = output + Integer.toString(curvetype) + "," + Integer.toString(order) + "," + Integer.toString(knots.length) + "," + Integer.toString(weights.length) + "," + Integer.toString(geompts.length) + "\n";
		output = output + Double.toString(knots[0]);
		for(int i=1;i<knots.length;i++) {
			output = output + "," + Double.toString(knots[i]);
		}
		output = output + "\n";
		output = output + Double.toString(weights[0]);
		for(int i=1;i<weights.length;i++) {
			output = output + "," + Double.toString(weights[i]);
		}
		output = output + "\n";
		output = output + Integer.toString(geompts[0].tag);
		for(int i=1;i<geompts.length;i++) {
			output = output + "," + Integer.toString(geompts[i].tag);
		}
		return output;
	}
	public NURBCurve(String filestring, GeomManager geom_manager) {
		String[] parts = filestring.split(":");
		
		//parse header data
		String[] header = parts[0].split(",");
		type = Integer.valueOf(header[0]);
		tag = Integer.valueOf(header[1]);
		name = header[2];
		
		//parse the first data
		String[] data_lines = parts[1].split("\n");
		//parse the first data line
		String[] data = data_lines[0].split(",");
		curvetype = Integer.valueOf(data[0]);
		order = Integer.valueOf(data[1]);
		int knot_cnt = Integer.valueOf(data[2]);
		int weight_cnt = Integer.valueOf(data[3]);
		int pt_cnt = Integer.valueOf(data[4]);
		
		//parse the knot data
		data = data_lines[1].split(",");
		knots = new double[knot_cnt];
		for(int i=0;i<knot_cnt;i++) {
			knots[i] = Double.valueOf(data[i]);
		}
		//parse the weight data
		data = data_lines[2].split(",");
		weights = new double[weight_cnt];
		for(int i=0;i<weight_cnt;i++) {
			weights[i] = Double.valueOf(data[i]);
		}
		//parse the point data
		data = data_lines[3].split(",");
		geompts = new Point3D[pt_cnt];
		for(int i=0;i<pt_cnt;i++) {
			geompts[i] = (Point3D) geom_manager.getEntity(Integer.valueOf(data[i])); 
		}
	}
}
