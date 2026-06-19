package analysis;

import java.util.ArrayList;

import data.Entity;
import geom.Point3D;
import geom_meshes.Triangle3R;

/*-----
TriElem - this element contains the connectivity information for a triangular element
-------*/
public class TriElem extends Triangle3R {
	public int neighbor1;  //neighbor from edge 1 (pt1-pt2)
	public int neighbor2;  //neighbor from edge 2 (pt1-pt3)
	public int neighbor3;  //neighbor from edge 3 (pt2-pt3)
	public int edge1BC_flag;   //flag to determine BC type, 0=none,1...others
	public int edge2BC_flag;   //flag to determine BC type, 0=none,1...others
	public int edge3BC_flag;   //flag to determine BC type, 0=none,1...others
	//inhereted from Triangle3R
	//public int pt1;
	//public int pt2;
	//public int pt3;
	//inhereted from "Entity"
	//public int type;     // 0=Point3D, 1=BSplineCurve, 2=BSplineSurface, 3=NURBCurve, 4=Line3D, 5=constraint
	//public String name;
	//public int tag;
	
	public TriElem() {
		neighbor1 = -1;
		neighbor2 = -1;
		neighbor3 = -1;
		edge1BC_flag = 0;
		edge2BC_flag = 0;
		edge3BC_flag = 0;
		pt1=-1;
		pt2=-1;
		pt3=-1;
		tag = 0;
		type = Entity.TRIELEM;
		name = "TriElem";
	}
	public TriElem(int in1, int in2, int in3) {
		neighbor1 = -1;
		neighbor2 = -1;
		neighbor3 = -1;
		edge1BC_flag = 0;
		edge2BC_flag = 0;
		edge3BC_flag = 0;
		pt1 = in1;
		pt2 = in2;
		pt3 = in3;
		tag = 0;
		type = Entity.TRIELEM;
		name = "TriElem";
		
		// order our points from least to greatest index
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
	public void findNeighborIndices(ArrayList<TriElem> elems) {
		int[] edge1 = getEdge1();
		int[] edge2 = getEdge2();
		int[] edge3 = getEdge3();
		edge1BC_flag = 0;
		edge2BC_flag = 0;
		edge3BC_flag = 0;
		
		for(int i=0;i<elems.size();i++) {
			int[] cedge1 = elems.get(i).getEdge1();
			int[] cedge2 = elems.get(i).getEdge2();
			int[] cedge3 = elems.get(i).getEdge3();
			
			//check to see if this is neighbor 1
			if((edge1[0] == cedge1[0] && edge1[1] == cedge1[1]) || (edge1[0] == cedge2[0] && edge1[1] == cedge2[1]) || (edge1[0] == cedge3[0] && edge1[1] == cedge3[1]))
				neighbor1 = i;
			//check to see if this is neighbor 2
			if((edge2[0] == cedge1[0] && edge2[1] == cedge1[1]) || (edge2[0] == cedge2[0] && edge2[1] == cedge2[1]) || (edge2[0] == cedge3[0] && edge2[1] == cedge3[1]))
				neighbor2 = i;
			//check to see if this is neighbor 3
			if((edge3[0] == cedge1[0] && edge3[1] == cedge1[1]) || (edge3[0] == cedge2[0] && edge3[1] == cedge2[1]) || (edge3[0] == cedge3[0] && edge3[1] == cedge3[1]))
				neighbor3 = i;
		}
	}
	public void clearBCs() {
		edge1BC_flag = 0;
		edge2BC_flag = 0;
		edge3BC_flag = 0;
	}
	//--------------------
	// TriElem File IO
	// TriElem File String Format)
	// ent_type,tag,name:pt1,pt2,pt3,neighbor1,neighbor2,neighbor3,bcflag1,bcflag2,bcflag3
	public String getFileString() {
		String output = Integer.toString(type) + "," + Integer.toString(tag) + "," + name + ":";
		output = output + Integer.toString(pt1) + "," + Integer.toString(pt2) + "," + Integer.toString(pt3);
		output = output + "," + Integer.toString(neighbor1) + "," + Integer.toString(neighbor2) + "," + Integer.toString(neighbor3);
		output = output + "," + Integer.toString(edge1BC_flag) + "," + Integer.toString(neighbor2) + "," + Integer.toString(neighbor3);
		return output;
	}
	public TriElem(String filestring) {
		String[] parts = filestring.split(":");
		String[] header = parts[0].split(",");
		String[] data = parts[1].split(",");
		pt1=Integer.valueOf(data[0]);
		pt2=Integer.valueOf(data[1]);
		pt3=Integer.valueOf(data[2]);		
		neighbor1 = Integer.valueOf(data[3]);
		neighbor2 = Integer.valueOf(data[4]);;
		neighbor3 = Integer.valueOf(data[5]);;
		edge1BC_flag = Integer.valueOf(data[6]);;
		edge2BC_flag = Integer.valueOf(data[7]);;
		edge3BC_flag = Integer.valueOf(data[8]);;
		tag = Integer.valueOf(header[1]);
		type = Entity.TRIELEM;
		name = "TriElem";
	}
}
