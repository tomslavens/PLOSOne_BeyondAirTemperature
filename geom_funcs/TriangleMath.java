package geom_funcs;

import java.util.ArrayList;

import data.Entity;
import geom.Point3D;

public class TriangleMath {
	public Point3D pt1;
	public Point3D pt2;
	public Point3D pt3;
	
	// inhereted from "Entity"
	//public int type;     // 0=Point3D, 1=BSplineCurve, 2=BSplineSurface, 3=NURBCurve, 4=Line3D, 5=constraint
	//public String name;
	//public int tag;

	public static Point3D getCentroid(Point3D pt1, Point3D pt2, Point3D pt3) {
		double Ax = pt1.x;
		double Ay = pt1.y;
		double Bx = pt2.x;
		double By = pt2.y;
		double Cx = pt3.x;
		double Cy = pt3.y;
		
		double cx = (Ax+Bx+Cx)/3.0;
		double cy = (Ay+By+Cy)/3.0;
		
		Point3D center = new Point3D(cx,cy,0.0);
		return center;
	}
	public static Point3D getCircumcenterXY(Point3D pt1, Point3D pt2, Point3D pt3) {
		double Ax = pt1.x;
		double Ay = pt1.y;
		double Bx = pt2.x;
		double By = pt2.y;
		double Cx = pt3.x;
		double Cy = pt3.y;
		
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
	public static boolean PointInTriangle(Point3D p, Point3D pt1, Point3D pt2, Point3D pt3) {
		Point3D p0 = pt1;
		Point3D p1 = pt2;
		Point3D p2 = pt3;
		
	    double s = p0.y * p2.x - p0.x * p2.y + (p2.y - p0.y) * p.x + (p0.x - p2.x) * p.y;
	    double t = p0.x * p1.y - p0.y * p1.x + (p0.y - p1.y) * p.x + (p1.x - p0.x) * p.y;

	    if ((s < 0) != (t < 0))
	        return false;

	    double A = -p1.y * p2.x + p0.y * (p2.x - p1.x) + p0.x * (p1.y - p2.y) + p1.x * p2.y;

	    return A < 0 ?
	            (s <= 0 && s + t >= A) :
	            (s >= 0 && s + t <= A);
	}
}
