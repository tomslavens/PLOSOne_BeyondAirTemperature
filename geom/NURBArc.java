package geom;

import javax.swing.JTextArea;

import data.Entity;
import data.GeomManager;
import linear_math.LinearFunctions;
import linear_math.Vector3D;

public class NURBArc extends ParamCurve {
	public int order;  //p polynomial order of the spline
	public double[] knots; //knot vector having p+n+1 knots
	public double[] weights; //weight vector
	public double radius;    //radius of the curve
	public double stangle;   //angle subtended by this arc (in radians)
	public int dir; //dir = 1, mid-to-pt1, dir = 2 mid-to-pt3
	//public Point3D[] geompts; //vector of 3D points
	//public int type; //entity type
	//public int curvetype; //type of curve
	public static int DIR1=1;
	public static int DIR2=2;
	
	public NURBArc() {
		order = 3;
		knots = new double[6];
		radius= 0;
		stangle = 0;
		dir = 1;
		weights = new double[3];
		geompts = new Point3D[3];
		type = Entity.PARAMCURVE;
		curvetype = ParamCurve.NURBARC;
		tag = 0;
		name = "NURBArc";
	}
	public NURBArc(Point3D pt1, Point3D pt3, double in_rad) {
		order = 3;
		knots = new double[] {0,0,0,1,1,1};
		radius = in_rad;
		dir = 1;
		stangle = NURBArc.calculateSubtendedAngle(pt1, pt3, in_rad);
		weights = new double[] {1,Math.cos(0.5*stangle),1};
		Point3D pt2 = NURBArc.calculateBridgePoint(pt1, pt3, radius, dir);
		geompts = new Point3D[] {pt1,pt2,pt3};
		type = Entity.PARAMCURVE;
		curvetype = ParamCurve.NURBARC;
		tag = 0;
		name = "NURBArc";
	}
	public NURBArc(Point3D pt1, Point3D pt3, double in_rad, int in_dir) {
		order = 3;
		knots = new double[] {0,0,0,1,1,1};
		radius = in_rad;
		dir = in_dir;
		stangle = NURBArc.calculateSubtendedAngle(pt1, pt3, in_rad);
		weights = new double[] {1,Math.cos(0.5*stangle),1};
		Point3D pt2 = NURBArc.calculateBridgePoint(pt1, pt3, radius,dir);
		geompts = new Point3D[] {pt1,pt2,pt3};
		type = Entity.PARAMCURVE;
		curvetype = ParamCurve.NURBARC;
		tag = 0;
		name = "NURBArc";
	}
	//this is called to update the bridge-point if the other points or radius are adjusted
	public void updateArc() {
		stangle = NURBArc.calculateSubtendedAngle(geompts[0],geompts[2], radius);
		geompts[1] = NURBArc.calculateBridgePoint(geompts[0],geompts[2], radius,dir);
		weights = new double[] {1,Math.cos(0.5*stangle),1};
	}
	public static double calculateSubtendedAngle(Point3D pt1, Point3D pt3, double radius) {
		//first calculate the offset of the bridgepoint
		double ptdist = pt1.distTo(pt3); //get the distance between pt1 & pt3
		double alpha = Math.acos((ptdist/2.0)/radius); //calculate the near-arc angle
		double stangle = 2*(Math.PI - 0.5*Math.PI - alpha); //calculate the angle subtended by pt1 and pt3
		return stangle;
	}
	public static Point3D calculateBridgePoint(Point3D pt1, Point3D pt3, double radius, double dir) {
		//first calculate the offset of the bridge point
		double ptdist = pt1.distTo(pt3); //get the distance between pt1 & pt3
		double alpha = Math.acos((ptdist/2.0)/radius); //calculate the near-arc angle
		double offset = (ptdist/2.0)/Math.tan(alpha); //offset from the midpoint to the bridge point		
		
		Point3D midpoint = LinearFunctions.midPoint(pt1, pt3); //get the mid-point
		Vector3D arcdir = null;
		if(dir == NURBArc.DIR1)
			arcdir = new Vector3D(midpoint,pt1); //calculate the vector from midpoint to pt1
		if(dir == NURBArc.DIR2)
			arcdir = new Vector3D(midpoint,pt3); //calculate the vector from midpoint to pt3
		Vector3D centerdir = LinearFunctions.getXYPerpendicularVector(arcdir); //get the vector pointing to the middle of this arc
		Point3D bridgept = LinearFunctions.offsetPointAlongVector(centerdir, midpoint,offset);
		
		return bridgept;
	}
	
	@Override
	public Point3D evaluate(double t, JTextArea outwindow) {
		//update our bridge point to correlate to the radius
		updateArc();
		
		double x = 0;
		double y = 0;
		double z = 0;
		
		double Nsum = 0.0;
		for(int i=0;i<geompts.length;i++) {
			double cN = NURB.getBasisFunction(i,t,order,knots,weights,outwindow);
			Nsum = Nsum + cN;
			x = x + geompts[i].x*cN;
			y = y + geompts[i].y*cN;
			z = z + geompts[i].z*cN;
		}
		Point3D eval = new Point3D(x,y,z);
		return eval;
	}
	@Override
	public Point3D evaluate(double t) {
		double x = 0;
		double y = 0;
		double z = 0;
		
		double Nsum = 0.0;
		for(int i=0;i<geompts.length;i++) {
			double cN = NURB.getBasisFunction(i,t,order,knots,weights,null);
			Nsum = Nsum + cN;
			x = x + geompts[i].x*cN;
			y = y + geompts[i].y*cN;
			z = z + geompts[i].z*cN;
		}
		Point3D eval = new Point3D(x,y,z);
		///System.out.println(Nsum);  // just a check to see if the recursion formula is correct....
		return eval;
	}
	//-----------
	// NURBArc FileIO
	// NurbArc File Structure
	// ent_type,tag,name:curve_type,order,knot_cnt,weight_cnt,pt_cnt,radius,stangle,dir
	// knot1,...,knotN
	// weight1,...,weightN
	// pt1,...ptN
	@Override
	public String getFileString() {
		String output = Integer.toString(type) + "," + Integer.toString(tag) + "," + name + ":";
		output = output + Integer.toString(curvetype) + "," + Integer.toString(order) + "," + Integer.toString(knots.length) + "," + Integer.toString(weights.length) + "," + Integer.toString(geompts.length) + "," + Double.toString(radius) + "," + Double.toString(stangle) + "," + Integer.toString(dir) + "\n";
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
	public NURBArc(String filestring, GeomManager geom_manager) {
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
		radius = Double.valueOf(data[5]);
		stangle = Double.valueOf(data[6]);
		dir = Integer.valueOf(data[7]);
		
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
		geompts = new Point3D[3];
		Point3D pt1 = (Point3D) (Point3D) geom_manager.getEntity(Integer.valueOf(data[0]));
		Point3D pt3 = (Point3D) (Point3D) geom_manager.getEntity(Integer.valueOf(data[2]));
		Point3D pt2 = NURBArc.calculateBridgePoint(pt1, pt3, radius,dir);
		geompts = new Point3D[] {pt1,pt2,pt3};
	}
}
