package analysis_mesh;

import java.util.ArrayList;

import analysis.Node3D;
import data.GeomManager;
import geom.Line3D;
import geom.ParamCurve;
import geom.Point3D;
import linear_math.LinearFunctions;
import linear_math.Vector3D;

public class AnalysisMeshFunctions {

	/*-----
	 * create2DPointMeshBySize(BC_curves, elem_edge_size) - this will create a 2D point cloud to be used by the Delaunay triangulation
	 *     for creating a triangular mesh. This version creates Node3D's so we can keep tabs of edge nodes
	 *     Creates in within the X-Y plane
	 */
	public static ArrayList<Node3D> create2DNodeMeshBySize(ArrayList<ParamCurve> in_curves, double edge_size, int val_length, GeomManager geom_manager) {
		//first get the edge points
		ArrayList<Node3D> tot_pts = new ArrayList<Node3D>();
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
		EdgeSizeMeshCondition mc = new EdgeSizeMeshCondition(edge_size);
		for(int i=0;i<in_curves.size();i++) {
			double cedge_size = edge_size; //as a default, we will break this up per the given element edge size
			
			//look to see if there is an existing EdgeSizeMeshCondition for this curve
			boolean hascondition = false; //this flag will tell us if we need to create a condition for this curve
			for(int j=0;j<geom_manager.mcCnt();j++) {
				int ctag =in_curves.get(i).tag;
				if(geom_manager.mcAt(j).isConditionTarget(ctag) && geom_manager.mcAt(j).meshcondition_type == MeshCondition.EDGESIZE) {
					EdgeSizeMeshCondition cmc = (EdgeSizeMeshCondition) geom_manager.mcAt(j);
					cedge_size = mc.edge_size;
				}
			}
			//if there is no condition, we create one based on the passed element edge size
			if(!hascondition) {
				//mc.targets.add(in_curves.get(i));
			}
			
			double clength = in_curves.get(i).getCurveLength(); //get the length of the curve
			int chunks = (int) Math.floor(clength/cedge_size);
			
			//now create a distribution of points along the curve by subdividing it by the edge_size
			ArrayList<Point3D> cpts = in_curves.get(i).getPointSet(chunks);
			int parent_tag = in_curves.get(i).tag;  //grab the tag of the parent geometry for these nodes
			for(int j=0;j<cpts.size();j++) {
				Node3D cnode = new Node3D(cpts.get(j),val_length);
				cnode.parent = parent_tag;
				tot_pts.add(cnode);
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
		if(mc.targets.size()>0)
			geom_manager.addMC(mc);
		
		//now create a grid of points
		Point3D cpt = new Point3D(min_x-10*edge_size,min_y-10*edge_size,0.0);
		int x_pts = (int) Math.floor(((max_x+10*edge_size)-(min_x-10*edge_size))/edge_size);
		int y_pts = (int) Math.floor(((max_y+10*edge_size)-(min_y-10*edge_size))/edge_size);
		ArrayList<Node3D> fill_pts = new ArrayList<Node3D>();
		for(int i=0;i<x_pts;i++) {
			double cx = cpt.x + i*edge_size;
			for(int j=0;j<y_pts;j++) {
				double cy = cpt.y + j*edge_size;
				Node3D gpt = new Node3D(cx,cy,0.0,val_length);
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
		ArrayList<Node3D> clean_pts = new ArrayList<Node3D>();
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
		//set tags to index in node array
		for(int i=0;i<clean_pts.size();i++) {
			clean_pts.get(i).tag = i;
		}		
		return clean_pts;
	}
	
	//getNodeNorm - this will get the normal vector pointing outside of the mesh 
	public static Vector3D getNodeNorm(Point3D pt, ParamCurve curve, ArrayList<ParamCurve> boundaries) {
		double nparam = curve.paramToPoint(pt);
		Vector3D pnorm = curve.getNormXYAtParam(nparam); // normal vector along curve
		Vector3D nnorm = pnorm.getReverseDirection();    // reverse vector
		Line3D pline = new Line3D(LinearFunctions.offsetPointAlongVector(pnorm, pt, 0.00001),LinearFunctions.offsetPointAlongVector(pnorm, pt, 9999999999.0));
		Line3D nline = new Line3D(LinearFunctions.offsetPointAlongVector(nnorm, pt, 0.00001),LinearFunctions.offsetPointAlongVector(nnorm, pt, 9999999999.0));
		int p_intersections = 0;
		int n_intersections = 0;
		for(int i=0;i<boundaries.size();i++) {
			ArrayList<Line3D> segments = boundaries.get(i).getSegmentSet();
			for(int j=0;j<segments.size();j++) {
				if(LinearFunctions.lineSegmentsIntersect(pline, segments.get(j)))
					p_intersections = p_intersections+1;
				if(LinearFunctions.lineSegmentsIntersect(nline, segments.get(j)))
					n_intersections = n_intersections+1;
			}
		}
		if(p_intersections == 0 || p_intersections%2 == 0)
			return pnorm;
		else
			return nnorm;
	}
	//getIntersectionNumber - returns the number of intersections the ray given by a point and vector have with this mesh
	public static int getIntersectionNumber(Point3D pt, Vector3D dir, ParamCurve boundary) {
		Line3D ray = new Line3D(pt, LinearFunctions.offsetPointAlongVector(dir,pt,1000000000));
		int intersections = 0;
		ArrayList<Line3D> segments = boundary.getSegmentSet();
		for(int j=0;j<segments.size();j++) {
			if(LinearFunctions.lineSegmentsIntersect(ray, segments.get(j)))
				intersections = intersections+1;
		}
		return intersections;
	}
	//getIntersectionNumber - returns the number of intersections the ray given by a point and vector have with this mesh
	public static int getIntersectionNumber(Point3D pt, Vector3D dir, ArrayList<ParamCurve> boundaries) {
		Line3D ray = new Line3D(pt, LinearFunctions.offsetPointAlongVector(dir,pt,1000000000));
		int intersections = 0;
		for(int i=0;i<boundaries.size();i++) {
			ArrayList<Line3D> segments = boundaries.get(i).getSegmentSet();
			for(int j=0;j<segments.size();j++) {
				if(LinearFunctions.lineSegmentsIntersect(ray, segments.get(j)))
					intersections = intersections+1;
			}
		}
		return intersections;
	}
	//getIntersectionNumber - returns the number of intersections the ray given by a line with this mesh
	public static int getIntersectionNumber(Line3D ray, ArrayList<ParamCurve> boundaries) {
		int intersections = 0;
		for(int i=0;i<boundaries.size();i++) {
			ArrayList<Line3D> segments = boundaries.get(i).getSegmentSet();
			for(int j=0;j<segments.size();j++) {
				if(LinearFunctions.lineSegmentsIntersect(ray, segments.get(j)))
					intersections = intersections+1;
			}
		}
		return intersections;
	}
	//getMaxSolutionDelta - returns the difference between the solution values between two node arrays
	public static double getMaxSolutionDelta(ArrayList<Node3D> osol, ArrayList<Node3D> csol) {
		double max = 0.0;
		for(int i=0;i<osol.size();i++) {
			double cval = Math.abs(osol.get(i).values[0] - csol.get(i).values[0]);
			if(cval > max)
				max = cval;
		}
		return max;
	}
	//getNodeArrayCopy - returns a copy of a node array
	public static ArrayList<Node3D> getNodeArrayCopy(ArrayList<Node3D> csol) {
		ArrayList<Node3D> nsol = new ArrayList<Node3D>();
		for(int i=0;i<csol.size();i++) {
			nsol.add(csol.get(i));
		}
		return nsol;
	}
}
