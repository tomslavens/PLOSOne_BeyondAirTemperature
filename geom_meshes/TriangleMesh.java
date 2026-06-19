package geom_meshes;

import geom.Line3D;
import geom.ParamCurve;
import geom.Point3D;
import geom_funcs.TriangleMath;
import linear_math.LinearFunctions;

import java.util.*;

import analysis.Node3D;
import data.Entity;
import data.GeomManager;

public class TriangleMesh extends Entity {
	public ArrayList<Triangle3R> triangles;
	public ArrayList<Point3D> pts;
	//inhereted from "Entity"
	//public int type;     // 0=Point3D, 1=BSplineCurve, 2=BSplineSurface, 3=NURBCurve, 4=Line3D, 5=constraint
	//public String name;
	//public int tag;
	
	public TriangleMesh() {
		triangles = new ArrayList<Triangle3R>();
		pts = new ArrayList<Point3D>();
		type = Entity.TRIANGLEMESH;
		name = "trianglemesh";
		tag = 0;
	}
	public String toString() {
		String output = "==="+name+"===\n";
		for(int i=0;i<triangles.size();i++) {
			String cline = pts.get(triangles.get(i).pt1).toString() + "||" + pts.get(triangles.get(i).pt2).toString() + "||" + pts.get(triangles.get(i).pt3).toString() + "\n";
			output = output + cline;
		}
		output = output+"======";
		return output;
	}
	
	/*-----
	 * create2DPointMeshBySize(BC_curves, elem_edge_size) - this will create a 2D point cloud to be used by the Delaunay triangulation
	 *     for creating a triangular mesh.
	 */
	public static ArrayList<Point3D> create2DPointMeshBySize(ArrayList<ParamCurve> in_curves, double edge_size) {
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
			//Point3D prev_start = in_curves.get(i-1).evaluate(0.0);
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
		
		//create pts on the curves based on the edge_size
		for(int i=0;i<in_curves.size();i++) {
			double clength = in_curves.get(i).getCurveLength(); //get the length of the curve
			int chunks = (int) Math.floor(clength/edge_size);			
			
			//now create a distribution of points along the curve by subdividing it by the edge_size
			ArrayList<Point3D> cpts = in_curves.get(i).getPointSet(chunks);
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
		int edge_pt_cnt = tot_pts.size(); //store the size of the edge pts
		
		//now create a grid of points
		Point3D cpt = new Point3D(min_x-10*edge_size,min_y-10*edge_size,0.0);
		int x_pts = (int) Math.floor(((max_x+10*edge_size)-(min_x-10*edge_size))/edge_size);
		int y_pts = (int) Math.floor(((max_y+10*edge_size)-(min_y-10*edge_size))/edge_size);
		ArrayList<Point3D> fill_pts = new ArrayList<Point3D>();
		for(int i=0;i<x_pts;i++) {
			double cx = cpt.x + i*edge_size;
			for(int j=0;j<y_pts;j++) {
				double cy = cpt.y + j*edge_size;
				Point3D gpt = new Point3D(cx,cy,0.0);
				fill_pts.add(gpt);
			}
		}
		//now add only the points inside our bounded curve set
		ArrayList<Line3D> segments = new ArrayList<Line3D>(); //get line-segment representation of the curve
		for(int i=0;i<in_curves.size();i++) {
			ArrayList<Line3D> csegs = in_curves.get(i).getSegmentSet();
			for(int j=0;j<csegs.size();j++) {
				segments.add(csegs.get(j));
			}
		}
		for(int i=0;i<fill_pts.size();i++) {
			Line3D triray = new Line3D(fill_pts.get(i), new Point3D(999999,fill_pts.get(i).y,0.0));
			int intersections = 0; //number of curves the center ray intersects
			for(int j=0;j<segments.size();j++) {
				if(LinearFunctions.lineSegmentsIntersect(triray, segments.get(j)))
					intersections = intersections+1;
			}
			if(intersections == 0 || intersections%2 == 0) {
				//System.out.println("Point OUT");
			} else {
				//check to see if the interior pts are too close to edge pts
				boolean close = false;
				for(int j=0;j<edge_pt_cnt;j++) {
					double cdist = tot_pts.get(j).distTo(fill_pts.get(i));
					if(cdist < 0.5*edge_size)
						close = true;
				}
				if(!close)
					tot_pts.add(fill_pts.get(i));
			}
		}
		
		//order and cull out any super-close points
		ArrayList<Point3D> clean_pts = new ArrayList<Point3D>();
		for(int i=0;i<tot_pts.size();i++) {
			boolean is_close = false;
			for(int j=i+1;j<tot_pts.size();j++) {
				double cdist = tot_pts.get(i).distTo(tot_pts.get(j));
				if(cdist < 0.001) {
					is_close = true;
				}
			}
			if(!is_close)
				clean_pts.add(tot_pts.get(i));
		}
		
		return clean_pts;
	}
	
	//-----------
	// TriangleMesh FileIO
	// TriangleMesh File Structure
	// ent_type,tag,name:tri_cnt,pt_cnt
	// pt1, ... , ptN
	// tri1
	// ...
	// triN
	public String getFileString() {
		String output = Integer.toString(type) + "," + Integer.toString(tag) + "," + name + ":";
		output = output + Integer.toString(triangles.size()) + "," + Integer.toString(pts.size()) + "\n";
		output = output + Integer.toString(pts.get(0).tag);
		for(int i=1;i<pts.size();i++) {
			output = output + "," + Integer.toString(pts.get(i).tag);
		}
		output = output + "\n";
		for(int i=0;i<triangles.size();i++) {
			output = output + triangles.get(i).getFileString();
		}		
		return output;
	}
	public TriangleMesh(String filestring, GeomManager geom_manager) {
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
		int tri_cnt = Integer.valueOf(data[0]);
		int pt_cnt = Integer.valueOf(data[0]);
		
		//parse the pt data
		data = data_lines[1].split(",");
		pts = new ArrayList<Point3D>();
		for(int i=0;i<pt_cnt;i++) {
			pts.add((Point3D) geom_manager.getEntity(Integer.valueOf(data[i])));
		}
		//parse the uknot data
		triangles = new ArrayList<Triangle3R>();
		for(int i=0;i<tri_cnt;i++) {
			int ci = i+2;
			String line = data_lines[ci];
			Triangle3R ctri = new Triangle3R(line);
			triangles.add(ctri);
		}		
	}
}
