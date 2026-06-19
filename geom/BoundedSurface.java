package geom;

import java.util.ArrayList;
import java.util.Collections;

import data.Entity;
import data.GeomManager;
import geom_meshes.DelaunayTriangulation;
import geom_meshes.Triangle3R;
import geom_meshes.TriangleMesh;
import linear_math.LinearFunctions;

/*----------------
 * BoundedSurface - this is a geometry surface that fits a mesh of triangles to a collection of ParamCurves
 */
public class BoundedSurface extends Entity {
	public ArrayList<ParamCurve> curves;
	public TriangleMesh mesh;
	public double edgesize;
	//inhereted from Entity
	//public int type;     // 0=Point3D, 1=BSplineCurve, 2=BSplineSurface, 3=NURBCurve, 4=Line3D, 5=constraint
	//public String name;
	//public int tag;

	public BoundedSurface() {
		type = Entity.BOUNDEDSURF;
		name = "boundedsurf";
		tag = 0;
		curves = new ArrayList<ParamCurve>();
		mesh = new TriangleMesh();
		edgesize = 0.02;
	}
	//updateMesh() - called when geometry is changed
	public void updateMesh() {
		ArrayList<Point3D> clean_pts = BoundedSurface.createBoundedSurfacePtArray(curves);
		mesh = createBoundedSurfaceMesh(clean_pts,curves);
	}
	public static BoundedSurface createBoundedSurface(ArrayList<ParamCurve> in_curves) {
		//first get the edge points
		ArrayList<Point3D> clean_pts = BoundedSurface.createBoundedSurfacePtArray(in_curves);
		
		System.out.println("Here");
		System.out.println("Pt num:");
		System.out.println(clean_pts.size());
		TriangleMesh mesh = createBoundedSurfaceMesh(clean_pts, in_curves);
		
		//System.out.println(mesh.toString());
		System.out.println("Here2");
		BoundedSurface surf = new BoundedSurface();
		surf.curves = in_curves;
		surf.mesh = mesh;
		surf.edgesize = -1.0;
		System.out.println("Here3");
		return surf;
	}
	public static TriangleMesh createBoundedSurfaceMesh(ArrayList<Point3D> pts, ArrayList<ParamCurve> in_curves) {
		System.out.println("Here");
		System.out.println("Pt num:");
		System.out.println(pts.size());
		TriangleMesh mesh = DelaunayTriangulation.triangulateXY(pts);
		
		//now, since we have the parametric curves here, we can calculate and remove triangles outside our surface
		ArrayList<Triangle3R> inside_tris = new ArrayList<Triangle3R>(); //this will house the 'good triangles'
		ArrayList<Line3D> segments = new ArrayList<Line3D>(); //get line-segment representation of the curve
		for(int i=0;i<in_curves.size();i++) {
			ArrayList<Line3D> csegs = in_curves.get(i).getSegmentSet();
			for(int j=0;j<csegs.size();j++) {
				segments.add(csegs.get(j));
			}
		}
		for(int i=0;i<mesh.triangles.size();i++) {
			Point3D tricenter = mesh.triangles.get(i).getCentroid(pts);
			Line3D triray = new Line3D(tricenter, new Point3D(999999,tricenter.y,0.0));
			int intersections = 0; //number of curves the center ray intersects
			for(int j=0;j<segments.size();j++) {
				if(LinearFunctions.lineSegmentsIntersect(triray, segments.get(j)))
					intersections = intersections+1;
			}
			if(intersections == 0 || intersections%2 == 0) {
				//System.out.println("TRIANGLE OUT");
			} else {
				inside_tris.add(mesh.triangles.get(i));
			}
		}
		mesh.triangles = inside_tris;
		
		return mesh;
	}
	public static ArrayList<Point3D> createBoundedSurfacePtArray(ArrayList<ParamCurve> in_curves) {
		//first get the edge points
		ArrayList<Point3D> tot_pts = new ArrayList<Point3D>();
		double sum_x = 0.0;
		double sum_y = 0.0;
		double max_x = -9999999.0;
		double min_x = 9999999.0;
		double max_y = -9999999.0;
		double min_y = 9999999.0;
		
		//order the curves
		for(int i=1;i<in_curves.size();i++) {
			Point3D prev_start = in_curves.get(i-1).evaluate(0.0);
			Point3D prev_tail = in_curves.get(i-1).evaluate(1.0);
			Point3D curr_start = in_curves.get(i).evaluate(0.0);
			Point3D curr_tail = in_curves.get(i).evaluate(0.0);
			
			double cs_pt = prev_tail.distTo(curr_start);
			double ct_pt = prev_tail.distTo(curr_tail);
			//double ct_ps = curr_tail.distTo(prev_start);
			//double cs_ps = curr_start.distTo(prev_start);
			
			if(ct_pt < cs_pt) {
				in_curves.get(i).flipPoints();
			}			
		}
		
		//create pts
		for(int i=0;i<in_curves.size();i++) {
			ArrayList<Point3D> cpts = in_curves.get(i).getPointSet(20);
			for(int j=0;j<cpts.size();j++) {
				tot_pts.add(cpts.get(j));
				sum_x = sum_x + cpts.get(j).x;
				sum_y = sum_y + cpts.get(j).y;
				if(cpts.get(j).x > max_x)
					max_x = cpts.get(j).x;
				if(cpts.get(j).x < min_x)
					min_x = cpts.get(j).x;
				if(cpts.get(j).y > max_y)
					max_y = cpts.get(j).y;
				if(cpts.get(j).y < min_y)
					min_y = cpts.get(j).y;
			}
		}
		//order and cull out any super-close points
		ArrayList<Point3D> clean_pts = new ArrayList<Point3D>();
		ArrayList<Integer> double_pts = new ArrayList<Integer>();
		for(int i=0;i<tot_pts.size();i++) {
			for(int j=i+1;j<tot_pts.size();j++) {
				double cdist = tot_pts.get(i).distTo(tot_pts.get(j));
				if(cdist < 0.001) {
					double_pts.add(new Integer(j));
				}
			}
		}
		System.out.println("Double pts: " + Integer.toString(double_pts.size()));
		for(int i=0;i<tot_pts.size();i++) {
			boolean bad_pt = false;
			for(int j=0;j<double_pts.size();j++) {
				if(double_pts.get(j).intValue() == i) {
					bad_pt = true;
					break;
				}
			}
			if(!bad_pt)
				clean_pts.add(tot_pts.get(i));
		}

		//now produce a handful of center points
		double xmid =sum_x/tot_pts.size();
		double ymid =sum_y/tot_pts.size();
		double x1 = xmid + (max_x-xmid)/4.0;
		double y1 = ymid + (max_y-ymid)/4.0;
		double x2 = xmid - (xmid-min_x)/4.0;
		double y2 = ymid - (ymid-min_y)/4.0;
		Point3D p1 = new Point3D(x1,y1,0.0);
		Point3D p2 = new Point3D(xmid,ymid,0.0);
		Point3D p3 = new Point3D(x2,y2,0.0);
		clean_pts.add(p1);
		clean_pts.add(p2);
		clean_pts.add(p3);
		
		return clean_pts;
	}
	
	//-----------
	// BoundedSurface FileIO
	// BoundedSurface File Structure
	// ent_type,tag,name:edgesize,crv_cnt,mesh_tag,curve1,...curveN
	public String getFileString() {
		String output = Integer.toString(type) + "," + Integer.toString(tag) + "," + name + ":";
		output = output + Double.toString(edgesize) + "," + Integer.toString(curves.size()) + "," + Integer.toString(mesh.tag);
		for(int i=0;i<curves.size();i++) {
			output = output + "," + Integer.toString(curves.get(i).tag);
		}		
		return output;
	}
	public BoundedSurface(String filestring, GeomManager geom_manager) {
		String[] parts = filestring.split(":");
		
		//parse header data
		String[] header = parts[0].split(",");
		type = Integer.valueOf(header[0]);
		tag = Integer.valueOf(header[1]);
		name = header[2];
		
		//parse the data
		String[] data = parts[1].split(",");
		edgesize = Double.valueOf(data[0]);
		int curve_cnt = Integer.valueOf(data[1]);
		int mesh_tag = Integer.valueOf(data[2]);
		mesh = (TriangleMesh) geom_manager.getEntity(mesh_tag);
		curves = new ArrayList<ParamCurve>();
		for(int i=0;i<curve_cnt;i++) {
			int ci = i+3;
			curves.add((ParamCurve) geom_manager.getEntity(Integer.valueOf(data[ci])));
		}		
	}
}
