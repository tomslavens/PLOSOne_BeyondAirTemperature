package geom_meshes;

import java.util.ArrayList;
import java.util.Collections;

import analysis.Node3D;
import analysis.TriElem;
import geom.Point3D;
import geom_funcs.TriangleMath;

public class DelaunayTriangulation {
	
	public static TriangleMesh triangulateXY(ArrayList<Point3D> pts) {
		ArrayList<Triangle3R> mesh = new ArrayList<Triangle3R>();
		
		System.out.println("Starting Triangulation");
		System.out.println("There are " + Integer.toString(pts.size()) + " points");
		//Init the pts into ascending X
		boolean swap = true;
		while(swap) {
			swap = false;
			for(int i=0;i<pts.size()-1;i++) {
				if(pts.get(i+1).x<pts.get(i).x) {
					swap = true;
					Collections.swap(pts, i, i+1);
				}
			}
		}
		
		//first we need to calcualte the "Super Triangle" to encompass all points
		double min_x = 9999999.0;
		double max_x = -min_x;
		double min_y = min_x;
		double max_y = -min_y;
		
		for(int i=0;i<pts.size();i++) {
			double cx = pts.get(i).x;
			double cy = pts.get(i).y;
			if(cx < min_x)
				min_x = cx;
			if(cx > max_x)
				max_x = cx;
			if(cy < min_y)
				min_y = cy;
			if(cy > max_y)
				max_y = cy;
		}
		//now elongate the pts
		double super_mult = 0.75;
		min_x = min_x-(super_mult*Math.abs(max_y));
		max_x = max_x+(super_mult*Math.abs(max_x));
		min_y = min_y-(super_mult*Math.abs(min_y));
		max_y = max_y+(super_mult*Math.abs(max_y));
		Point3D spt1 = new Point3D(min_x,min_y,0.0);
		Point3D spt2 = new Point3D(max_x,min_y,0.0);
		Point3D spt3 = new Point3D((max_x+min_x)/2.0,max_y,0.0);
		//add the super triangle pts to the front of our point array
		ArrayList<Point3D> spts = new ArrayList<Point3D>();
		spts.add(spt1);
		spts.add(spt2);
		spts.add(spt3);
		for(int i=0;i<pts.size();i++) {
			spts.add(pts.get(i));
		}
		Triangle3R supertri = new Triangle3R(0,1,2);
		mesh.add(supertri);
		System.out.println("SUPER TRI CREATED!");
		
		//now add our points 1-by-1
		for(int i=3;i<spts.size();i++) {
		//for(int i=3;i<6;i++) {
			//System.out.println("i: " + Integer.toString(i));
			Point3D cpt = spts.get(i);
			ArrayList<int[]> badedges = new ArrayList<int[]>();
			ArrayList<Triangle3R> new_mesh = new ArrayList<Triangle3R>();
			for(int j=0;j<mesh.size();j++) {
				if(DelaunayTriangulation.isWithinXYTriangle(cpt,mesh.get(j),spts)) {
					//System.out.println("Within");
					badedges.add(mesh.get(j).getEdge1());
					badedges.add(mesh.get(j).getEdge2());
					badedges.add(mesh.get(j).getEdge3());
				} else {
					new_mesh.add(mesh.get(j));
				}
			}
			//System.out.println("Bad edges: " + Integer.toString(badedges.size()));
			//remove all the interior edges
			ArrayList<int[]> goodedges = new ArrayList<int[]>();
			for(int j=0;j<badedges.size();j++) {
				boolean repeated = false;
				int[] cedge = badedges.get(j);
				//System.out.println("<" + Integer.toString(cedge[0]) + "," + Integer.toString(cedge[1]) + ">");
				for(int k=0;k<badedges.size();k++) {
					if(cedge[0] == badedges.get(k)[0] && cedge[1] == badedges.get(k)[1] && k != j) {
						repeated = true;
						break;
					}
				}
				if(!repeated)
					goodedges.add(cedge);
			}
			//create new triangles from the good edges
			for(int j=0;j<goodedges.size();j++) {
				Triangle3R ctri = new Triangle3R(goodedges.get(j)[0],goodedges.get(j)[1],i);
				new_mesh.add(ctri);
			}
			
			mesh = new_mesh;
			//System.out.println("Mesh Size: " + Integer.toString(mesh.size()));
		}
		
		//clean up the super triangle points
		ArrayList<Triangle3R> nonsuper_mesh = new ArrayList<Triangle3R>();
		for(int i=0;i<mesh.size();i++) {
			Triangle3R ctri = mesh.get(i);
			if(ctri.pt1 != 0 && ctri.pt2 != 0 && ctri.pt3 != 0 && ctri.pt1 != 1 && ctri.pt2 != 1 && ctri.pt3 != 1 && ctri.pt1 != 2 && ctri.pt2 != 2 && ctri.pt3 != 2)
				nonsuper_mesh.add(new Triangle3R(ctri.pt1-3,ctri.pt2-3,ctri.pt3-3));
		}
		
		// !!!! I don't think I'm using this code.....
		/*//clean up overlapping triangles
		int[] intersections = new int[nonsuper_mesh.size()];
		for(int i=0;i<nonsuper_mesh.size();i++) {
			Triangle3R ctri = nonsuper_mesh.get(i);
			int isections = 0;
			for(int j=0;j<nonsuper_mesh.size();j++) {
				if(i!=j) {
					Triangle3R itri = nonsuper_mesh.get(j);
					if(ctri.PointInTriangle(itri.getCentroid(pts), pts))
						isections = isections+1;
				}
			}
			intersections[i] = isections;
		}
		ArrayList<Triangle3R> clean_mesh = new ArrayList<Triangle3R>();
		for(int i=0;i<nonsuper_mesh.size();i++) {
			//if(intersections[i] <=1)
				clean_mesh.add(nonsuper_mesh.get(i));
		}*/
		
		TriangleMesh trimesh = new TriangleMesh();
		trimesh.pts = pts;
		//trimesh.triangles = clean_mesh;
		trimesh.triangles = nonsuper_mesh;
		
		return trimesh;
	}
	public static ArrayList<TriElem> triangulateXYNodes(ArrayList<Node3D> pts,int val_length) {
		ArrayList<TriElem> mesh = new ArrayList<TriElem>();
		
		System.out.println("Starting Node-Element Triangulation");
		System.out.println("There are " + Integer.toString(pts.size()) + " points");
		//Init the pts into ascending X
		boolean swap = true;
		while(swap) {
			swap = false;
			for(int i=0;i<pts.size()-1;i++) {
				if(pts.get(i+1).x<pts.get(i).x) {
					swap = true;
					Collections.swap(pts, i, i+1);
				}
			}
		}
		
		//first we need to calcualte the "Super Triangle" to encompass all points
		double min_x = 9999999.0;
		double max_x = -min_x;
		double min_y = min_x;
		double max_y = -min_y;
		
		for(int i=0;i<pts.size();i++) {
			double cx = pts.get(i).x;
			double cy = pts.get(i).y;
			if(cx < min_x)
				min_x = cx;
			if(cx > max_x)
				max_x = cx;
			if(cy < min_y)
				min_y = cy;
			if(cy > max_y)
				max_y = cy;
		}
		//now elongate the pts
		double super_mult = 0.75;
		min_x = min_x-(super_mult*Math.abs(max_y));
		max_x = max_x+(super_mult*Math.abs(max_x));
		min_y = min_y-(super_mult*Math.abs(min_y));
		max_y = max_y+(super_mult*Math.abs(max_y));
		Node3D spt1 = new Node3D(min_x,min_y,0.0,val_length);
		Node3D spt2 = new Node3D(max_x,min_y,0.0,val_length);
		Node3D spt3 = new Node3D((max_x+min_x)/2.0,max_y,0.0,val_length);
		//add the super triangle pts to the front of our point array
		ArrayList<Node3D> spts = new ArrayList<Node3D>();
		spts.add(spt1);
		spts.add(spt2);
		spts.add(spt3);
		for(int i=0;i<pts.size();i++) {
			spts.add(pts.get(i));
		}
		TriElem supertri = new TriElem(0,1,2); //create the super triangle
		mesh.add(supertri);
		System.out.println("SUPER TRI CREATED!");
		
		//now add our points 1-by-1
		for(int i=3;i<spts.size();i++) {
		//for(int i=3;i<6;i++) {
			//System.out.println("i: " + Integer.toString(i));
			Node3D cpt = spts.get(i);
			ArrayList<int[]> badedges = new ArrayList<int[]>();
			ArrayList<TriElem> new_mesh = new ArrayList<TriElem>();
			for(int j=0;j<mesh.size();j++) {
				if(DelaunayTriangulation.isWithinXYTriangle(cpt,mesh.get(j),spts)) {
					//System.out.println("Within");
					badedges.add(mesh.get(j).getEdge1());
					badedges.add(mesh.get(j).getEdge2());
					badedges.add(mesh.get(j).getEdge3());
				} else {
					new_mesh.add(mesh.get(j));
				}
			}
			//System.out.println("Bad edges: " + Integer.toString(badedges.size()));
			//remove all the interior edges
			ArrayList<int[]> goodedges = new ArrayList<int[]>();
			for(int j=0;j<badedges.size();j++) {
				boolean repeated = false;
				int[] cedge = badedges.get(j);
				//System.out.println("<" + Integer.toString(cedge[0]) + "," + Integer.toString(cedge[1]) + ">");
				for(int k=0;k<badedges.size();k++) {
					if(cedge[0] == badedges.get(k)[0] && cedge[1] == badedges.get(k)[1] && k != j) {
						repeated = true;
						break;
					}
				}
				if(!repeated)
					goodedges.add(cedge);
			}
			//create new triangles from the good edges
			for(int j=0;j<goodedges.size();j++) {
				TriElem ctri = new TriElem(goodedges.get(j)[0],goodedges.get(j)[1],i);
				new_mesh.add(ctri);
			}			
			mesh = new_mesh;
			//System.out.println("Mesh Size: " + Integer.toString(mesh.size()));
		}
		
		//clean up the super triangle points
		ArrayList<TriElem> nonsuper_mesh = new ArrayList<TriElem>();
		for(int i=0;i<mesh.size();i++) {
			TriElem ctri = mesh.get(i);
			if(ctri.pt1 != 0 && ctri.pt2 != 0 && ctri.pt3 != 0 && ctri.pt1 != 1 && ctri.pt2 != 1 && ctri.pt3 != 1 && ctri.pt1 != 2 && ctri.pt2 != 2 && ctri.pt3 != 2)
				nonsuper_mesh.add(new TriElem(ctri.pt1-3,ctri.pt2-3,ctri.pt3-3));
		}
		return nonsuper_mesh;
	}
	public static Triangle3R[] reTriangulate(int pt, Triangle3R tri) {
		Triangle3R tri1 = new Triangle3R(tri.pt1,tri.pt2,pt);
		Triangle3R tri2 = new Triangle3R(tri.pt1,tri.pt3,pt);
		Triangle3R tri3 = new Triangle3R(tri.pt3,tri.pt2,pt);
		Triangle3R[] tessel = new Triangle3R[] {tri1,tri2,tri3};
		return tessel;
	}
	public static boolean intInArray(int j, ArrayList<Integer> ints) {
		boolean within = false;
		for(int i=0;i<ints.size();i++) {
			if(j == ints.get(i).intValue()) {
				within = true;
				break;
			}
		}
		return within;
	}
	
	//insWithinXYTrianle - determines if the point is within the XY projection of the triangle
	public static boolean isWithinXYTriangle(Point3D pt, Triangle3R tri, ArrayList<Point3D> pts) {
		Point3D tcenter = tri.getCircumcenterXY(pts);
		double circleradius = tcenter.sqareDistTo(pts.get(tri.pt1));
		double centerdist = tcenter.sqareDistTo(pt);
		boolean within = false;
		if(centerdist < circleradius)
			within = true;
		return within;
	}
	//insWithinXYTrianle - determines if the point is within the XY projection of the triangle
	public static boolean isWithinXYTriangle(Node3D pt, TriElem tri, ArrayList<Node3D> pts) {
		Point3D tcenter = TriangleMath.getCircumcenterXY(pts.get(tri.pt1),pts.get(tri.pt2),pts.get(tri.pt3));
		double circleradius = tcenter.sqareDistTo(pts.get(tri.pt1));
		double centerdist = tcenter.sqareDistTo(pt);
		boolean within = false;
		if(centerdist < circleradius)
			within = true;
		return within;
	}	
}
