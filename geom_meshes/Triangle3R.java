package geom_meshes;

import java.util.ArrayList;

import data.Entity;
import geom.Point3D;
import geom_funcs.TriangleMath;

//------
// Triangle3D - this holds index values to reference a larger Point3D array
public class Triangle3R extends Entity {
	public int pt1;
	public int pt2;
	public int pt3;
	//inhereted from "Entity"
	//public int type;     // 0=Point3D, 1=BSplineCurve, 2=BSplineSurface, 3=NURBCurve, 4=Line3D, 5=constraint
	//public String name;
	//public int tag;
	
	public Triangle3R() {
		pt1=-1;
		pt2=-1;
		pt3=-1;
		tag = 0;
	}
	public Triangle3R(int in1, int in2, int in3) {
		pt1 = in1;
		pt2 = in2;
		pt3 = in3;
		
		int[] pts = new int[] {pt1,pt2,pt3};
		boolean swap = true;
		while(swap) {
			swap = false;
			for(int i=0;i<pts.length-1;i++) {
				if(pts[i+1] < pts[i]) {
					swap = true;
					int holder = pts[i];
					pts[i] = pts[i+1];
					pts[i+1] = holder;
				}
			}
		}
	}
	public int[] getEdge1() {
		int[] edge = new int[] {pt1,pt2};
		return edge;
	}
	public Point3D getEdge1Midpnt(ArrayList<Point3D> pts) {
		double xmid = (pts.get(pt1).x + pts.get(pt2).x)/2.0;
		double ymid = (pts.get(pt1).y + pts.get(pt2).y)/2.0;
		double zmid = (pts.get(pt1).z + pts.get(pt2).z)/2.0;
		
		Point3D midpt = new Point3D(xmid,ymid,zmid);
		return midpt;
	}
	public int[] getEdge2() {
		int[] edge = new int[] {pt1,pt3};
		return edge;
	}
	public Point3D getEdge2Midpnt(ArrayList<Point3D> pts) {
		double xmid = (pts.get(pt1).x + pts.get(pt3).x)/2.0;
		double ymid = (pts.get(pt1).y + pts.get(pt3).y)/2.0;
		double zmid = (pts.get(pt1).z + pts.get(pt3).z)/2.0;
		
		Point3D midpt = new Point3D(xmid,ymid,zmid);
		return midpt;
	}
	public int[] getEdge3() {
		int[] edge = new int[] {pt2,pt3};
		return edge;
	}
	public Point3D getEdge3Midpnt(ArrayList<Point3D> pts) {
		double xmid = (pts.get(pt2).x + pts.get(pt3).x)/2.0;
		double ymid = (pts.get(pt2).y + pts.get(pt3).y)/2.0;
		double zmid = (pts.get(pt2).z + pts.get(pt3).z)/2.0;
		
		Point3D midpt = new Point3D(xmid,ymid,zmid);
		return midpt;
	}
	/*public TriangleMath getPtTriangle(ArrayList<Point3D> pts) {
		TriangleMath tri = new TriangleMath(pts.get(pt1),pts.get(pt2),pts.get(pt3));
		return tri;
	}*/
	public Point3D getCircumcenterXY(ArrayList<Point3D> pts) {
		double Ax = pts.get(pt1).x;
		double Ay = pts.get(pt1).y;
		double Bx = pts.get(pt2).x;
		double By = pts.get(pt2).y;
		double Cx = pts.get(pt3).x;
		double Cy = pts.get(pt3).y;
		
		double D = 2*(Ax*(By-Cy) + Bx*(Cy-Ay) + Cx*(Ay-By));
		double ux1 = ((Ax*Ax)+(Ay*Ay))*(By-Cy);
		double ux2 = ((Bx*Bx)+(By*By))*(Cy-Ay);
		double ux3 = ((Cx*Cx)+(Cy*Cy))*(Ay-By);
		double ux = (1/D)*(ux1+ux2+ux3);
		double uy1 = ((Ax*Ax)+(Ay*Ay))*(Cx-Bx);
		double uy2 = ((Bx*Bx)+(By*By))*(Ax-Cx);
		double uy3 = ((Cx*Cx)+(Cy*Cy))*(Bx-Ax);
		double uy = (1/D)*(uy1+uy2+uy3);
		Point3D center = new Point3D(ux,uy,0.0);
		return center;
	}
	public Point3D getCentroid(ArrayList<Point3D> pts) {
		double Ax = pts.get(pt1).x;
		double Ay = pts.get(pt1).y;
		double Bx = pts.get(pt2).x;
		double By = pts.get(pt2).y;
		double Cx = pts.get(pt3).x;
		double Cy = pts.get(pt3).y;
		
		double cx = (Ax+Bx+Cx)/3.0;
		double cy = (Ay+By+Cy)/3.0;
		
		Point3D center = new Point3D(cx,cy,0.0);
		return center;
	}
	public boolean PointInTriangle(Point3D p, ArrayList<Point3D> pts) {
		Point3D p0 = pts.get(pt1);
		Point3D p1 = pts.get(pt2);
		Point3D p2 = pts.get(pt3);
		
	    double s = p0.y * p2.x - p0.x * p2.y + (p2.y - p0.y) * p.x + (p0.x - p2.x) * p.y;
	    double t = p0.x * p1.y - p0.y * p1.x + (p0.y - p1.y) * p.x + (p1.x - p0.x) * p.y;

	    if ((s < 0) != (t < 0))
	        return false;

	    double A = -p1.y * p2.x + p0.y * (p2.x - p1.x) + p0.x * (p1.y - p2.y) + p1.x * p2.y;

	    return A < 0 ?
	            (s <= 0 && s + t >= A) :
	            (s >= 0 && s + t <= A);
	}
	//--------------------
	// Triangle3R File IO
	// Triangle3R File String Format)
	// ent_type,tag,name:pt1,pt2,pt3
	public String getFileString() {
		String output = Integer.toString(type) + "," + Integer.toString(tag) + "," + name + ":";
		output = output + Integer.toString(pt1) + "," + Integer.toString(pt2) + "," + Integer.toString(pt3);
		return output;
	}
	public Triangle3R(String filestring) {
		String[] parts = filestring.split(":");
		String[] header = parts[0].split(",");
		String[] data = parts[1].split(",");
		pt1=Integer.valueOf(data[0]);
		pt2=Integer.valueOf(data[1]);
		pt3=Integer.valueOf(data[2]);
		tag = Integer.valueOf(header[1]);
		type = Entity.TRIELEM;
		name = "TriElem";
	}
}
