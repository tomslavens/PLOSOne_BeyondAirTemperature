package geom;

import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JTextArea;

import data.Entity;
import data.GeomManager;
import geom_meshes.DelaunayTriangulation;
import geom_meshes.Triangle3R;
import geom_meshes.TriangleMesh;
import linear_math.LinearFunctions;

/*----------------
 * LineHull - this is a convex hull using a collection of ParamCurves
 */
public class LineHull extends Entity {
	public ArrayList<Line3D> curves;
	//inhereted from Entity
	//public int type;     // 0=Point3D, 1=BSplineCurve, 2=BSplineSurface, 3=NURBCurve, 4=Line3D, 5=constraint
	//public String name;
	//public int tag;

	public LineHull() {
		type = Entity.LINEHULL;
		name = "linehull";
		tag = 0;
		curves = new ArrayList<Line3D>();
	}
	public static LineHull createLineHull(ArrayList<Line3D> in_curves) {
		LineHull surf = new LineHull();
		surf.curves = in_curves;
		return surf;
	}
	public static LineHull createLineHullParam(ArrayList<ParamCurve> in_curves) {
		LineHull surf = new LineHull();
		for(int i=0;i<in_curves.size();i++) {
			surf.curves.add((Line3D) in_curves.get(i));
		}
		return surf;
	}
	public static LineHull createLineHullParam(ArrayList<Point3D> in_pts, GeomManager geom) {
		LineHull surf = new LineHull();
		for(int i=0;i<in_pts.size();i++) {
			if(i < in_pts.size()-1) {
				Line3D cline = new Line3D(in_pts.get(i),in_pts.get(i+1));
				geom.addCurve(cline);
				surf.curves.add(cline);
			} else {
				Line3D cline = new Line3D(in_pts.get(i),in_pts.get(0));
				geom.addCurve(cline);
				surf.curves.add(cline);
			}
		}
		return surf;
	}
	public boolean ptInHulle(Point3D pt) {
		boolean inhull = false;
		Line3D ray = new Line3D(pt, new Point3D(99999.0,0.0,0.0));
		int isections = 0;
		for(int i=0;i<curves.size();i++) {
			if(LinearFunctions.lineSegmentsIntersect(ray, curves.get(i)))
				isections = isections + 1;
		}
		if(isections == 1)
			inhull = true;
		return inhull;
	}
	public double getMinX() {
		double mx = curves.get(0).geompts[0].x;
		for(int i=0;i<curves.size();i++) {
			if(curves.get(i).geompts[0].x <mx)
				mx = curves.get(i).geompts[0].x;
			if(curves.get(i).geompts[1].x <mx)
				mx = curves.get(i).geompts[1].x;
		}
		return mx;
	}
	public double getMaxX() {
		double mx = curves.get(0).geompts[0].x;
		for(int i=0;i<curves.size();i++) {
			if(curves.get(i).geompts[0].x > mx)
				mx = curves.get(i).geompts[0].x;
			if(curves.get(i).geompts[1].x > mx)
				mx = curves.get(i).geompts[1].x;
		}
		return mx;
	}
	public double getMinY() {
		double my = curves.get(0).geompts[0].y;
		for(int i=0;i<curves.size();i++) {
			if(curves.get(i).geompts[0].y < my)
				my = curves.get(i).geompts[0].y;
			if(curves.get(i).geompts[1].y < my)
				my = curves.get(i).geompts[1].y;
		}
		return my;
	}
	public double getMaxY() {
		double my = curves.get(0).geompts[0].y;
		for(int i=0;i<curves.size();i++) {
			if(curves.get(i).geompts[0].y > my)
				my = curves.get(i).geompts[0].y;
			if(curves.get(i).geompts[1].y > my)
				my = curves.get(i).geompts[1].y;
		}
		return my;
	}
	
	
	//-----------
	// LineHull FileIO
	// LineHull File Structure
	// ent_type,tag,name:crv_cnt,curve1,...curveN
	public String getFileString() {
		String output = Integer.toString(type) + "," + Integer.toString(tag) + "," + name + ":";
		output = output +  Integer.toString(curves.size());
		for(int i=0;i<curves.size();i++) {
			output = output + "," + Integer.toString(curves.get(i).tag);
		}		
		return output;
	}
	public String getRawFileString() {
		String output = Integer.toString(curves.size()+1) + "\n"; // add the last point of the last line
		for(int i=0;i<curves.size();i++) {
			Point3D pt1 = curves.get(i).geompts[0];
			Point3D pt2 = curves.get(i).geompts[1];
			output = output + pt1.getRawFileString() + "\n";
			if(i==curves.size()-1)
				output = output + pt2.getRawFileString() + "\n";
		}		
		return output;
	}
	public String generateRawSplineFileString(int npts, JTextArea outwindow) {
		Point3D[] pts = new Point3D[curves.size()+1];
		for(int i=0;i<pts.length-1;i++) {
			pts[i] = curves.get(i).geompts[0];
		}
		pts[pts.length-1] = curves.get(curves.size()-1).geompts[1];
		BSplineCurve spline = BSplineCurve.createUniformOpenBSpline(2, pts, outwindow);
		
		double spline_length = spline.getCurveLength();
		double dx = (double) spline_length/npts;
		outwindow.append("Writing out line hull with " + String.valueOf(dx) + " sizing\n");
		ArrayList<Point3D> spts = spline.getPointSet(dx);
			
		String output = Integer.toString(spts.size()) + "\n"; // add the last point of the last line
		for(int i=0;i<spts.size();i++) {
			output = output + spts.get(i).getRawFileString() + "\n";
		}
		/*for(int i=0;i<pts.length;i++) {
			output = output + pts[i].getRawFileString() + "\n";
		}*/
		return output;
	}	
	public LineHull(String filestring, GeomManager geom_manager) {
		String[] parts = filestring.split(":");
		
		//parse header data
		String[] header = parts[0].split(",");
		type = Integer.valueOf(header[0]);
		tag = Integer.valueOf(header[1]);
		name = header[2];
		
		//parse the data
		String[] data = parts[1].split(",");
		int curve_cnt = Integer.valueOf(data[1]);
		curves = new ArrayList<Line3D>();
		for(int i=0;i<curve_cnt;i++) {
			int ci = i+3;
			curves.add((Line3D) geom_manager.getEntity(Integer.valueOf(data[ci])));
		}		
	}
}
