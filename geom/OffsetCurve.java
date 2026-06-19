package geom;

import java.util.ArrayList;

import javax.swing.JTextArea;

import data.Entity;
import data.GeomManager;
import linear_math.LinearFunctions;
import linear_math.Vector3D;

//OffsetCurve - creates an offset curve in the primary planar surfaces
// uses the algorithm of Tiller & Hanson, "Offsets of Two-Dimensional Profiles", IEEE Computer Graphics & Applications Vol 4 Iss 9
public class OffsetCurve extends ParamCurve {
	//public Point3D[] geompts;
	public ParamCurve parent_curve;
	double xy_offset;  //distace to offset in x-y plane
	double xz_offset;  //distace to offset in x-z plane
	double yz_offset;  //distace to offset in y-z plane
	//public Point3D[] geompts; //vector of 3D points
	//public int type; //entity type
	//public int curvetype; //type of curve
	
	public OffsetCurve() {
		parent_curve = null;
		xy_offset = 0;
		xz_offset = 0;
		yz_offset = 0;
		type = Entity.PARAMCURVE;
		curvetype = ParamCurve.OFFSETCRV;
		geompts = new Point3D[0];
		tag = 0;
		name = "OffsetCurve";
	}
	public OffsetCurve(ParamCurve in_parent, double in_xyoffset,double in_xzoffset,double in_yzoffset) {
		parent_curve = in_parent;
		xy_offset = in_xyoffset;
		xz_offset = in_xzoffset;
		yz_offset = in_yzoffset;
		type = Entity.PARAMCURVE;
		curvetype = ParamCurve.OFFSETCRV;
		geompts = new Point3D[0];
		tag = 0;
		name = "OffsetCurve";
	}
	//------
	// evaluate(double param, JtextArea outwindow) - this will take a parameter and return the geometric point for this parameter
	public Point3D evaluate(double param, JTextArea outwindow) {
		//first calculate the root point
		Point3D loc = parent_curve.evaluate(param);
		//evaluate a point a little ahead
		Point3D loc2 = parent_curve.evaluate(param+0.001);
		Vector3D tanvect = new Vector3D(loc,loc2);
		Vector3D xyvect = LinearFunctions.getXYPerpendicularVector(tanvect);
		Vector3D xzvect = LinearFunctions.getXZPerpendicularVector(tanvect);
		Vector3D yzvect = LinearFunctions.getYZPerpendicularVector(tanvect);
		
		Point3D pt = LinearFunctions.offsetPointAlongVector(xyvect, loc, xy_offset);
		pt = LinearFunctions.offsetPointAlongVector(xyvect, pt, xz_offset);
		pt = LinearFunctions.offsetPointAlongVector(xyvect, pt, yz_offset);
		return pt;
	}
	/*=================================
	 * NON-VERBOSE EVALUATE METHOD FOR PLOTTING STUFF	 * 
	 */
	public Point3D evaluate(double param) {
		//first calculate the root point
		Point3D loc = parent_curve.evaluate(param);
		//evaluate a point a little ahead
		Point3D loc2 = parent_curve.evaluate(param+0.001);
		Vector3D tanvect = new Vector3D(loc,loc2);
		Vector3D xyvect = LinearFunctions.getXYPerpendicularVector(tanvect);
		Vector3D xzvect = LinearFunctions.getXZPerpendicularVector(tanvect);
		Vector3D yzvect = LinearFunctions.getYZPerpendicularVector(tanvect);

		Point3D pt = LinearFunctions.offsetPointAlongVector(xyvect, loc, xy_offset);
		pt = LinearFunctions.offsetPointAlongVector(xyvect, pt, xz_offset);
		pt = LinearFunctions.offsetPointAlongVector(xyvect, pt, yz_offset);
		return pt;
	}

	
	//-----------
	// OffsetCurve FileIO
	// OffsetCurve File Structure
	// ent_type,tag,name:curve_type,parent_tag,xy_offset,xz_offset,yz_offset
	public String getFileString() {
		String output = Integer.toString(type) + "," + Integer.toString(tag) + "," + name + ":";
		output = output + Integer.toString(curvetype) + "," + Integer.toString(parent_curve.tag) + "," + Double.toString(xy_offset) + "," + Double.toString(xz_offset) + "," + Double.toString(yz_offset);
		return output;
	}
	public OffsetCurve(String filestring, GeomManager geom_manager) {
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
		int parent_tag = Integer.valueOf(data[1]);
		geompts = new Point3D[0];
		xy_offset = Double.valueOf(data[2]);
		xz_offset = Double.valueOf(data[3]);
		yz_offset = Double.valueOf(data[4]);
		parent_curve = (ParamCurve) geom_manager.getEntity(parent_tag);
	}
}
