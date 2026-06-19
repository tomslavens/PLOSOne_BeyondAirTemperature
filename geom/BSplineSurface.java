package geom;

import java.util.ArrayList;

import javax.swing.JTextArea;

import data.Entity;
import data.GeomManager;

public class BSplineSurface extends ParamSurface {
	//public int u_order; //order in the u-direction
	//public int v_order; //order in the v-direction
	//public double[] uknots; //knots for the u-direction
	//public double[] vknots; //knots for the v-direction
	//public Point3D[][] geompts; //control points of the surface in the [u][v] directions
	
	//empty constructor
	public BSplineSurface() {
		u_order = 0;
		v_order = 0;
		uknots = new double[0];
		vknots = new double[0];
		geompts = new Point3D[0][0];
		type = Entity.BSPLINESURFACE;
		tag = 0;
		name = "BSplineSurface";
	}
	//constructor utilizing input orders of the direction along with a set of points
	public BSplineSurface(int in_uorder, int in_vorder, Point3D[][] in_pts) {
		u_order = in_uorder;
		v_order = in_vorder;
		int u_lng = in_pts.length;
		int v_lng = in_pts[0].length;
		uknots = new double[u_order+u_lng];
		vknots = new double[v_order+v_lng];
		geompts = new Point3D[u_lng][v_lng];
		for(int i=0;i<u_lng;i++) {
			for(int j=0;j<v_lng;j++) {
				geompts[i][j] = in_pts[i][j];
			}
		}
		type = Entity.BSPLINESURFACE;
		tag = 0;
		name = "BSplineSurface";
	}
	//Uniform constructor using an array of BSplineCurves
	// u-direction is along the splines
	// v-direction is the perpendicular to the splines
	public BSplineSurface(ArrayList<BSplineCurve> curves) {
		type = Entity.BSPLINESURFACE;
		tag = 0;
		name = "BSplineSurface";
		int crv_cnt = curves.size();
		int crv_pts = curves.get(0).geompts.length;
		
		u_order = curves.get(0).order;
		v_order = crv_cnt - 1;
		
		uknots = new double[crv_pts+u_order]; //we use a typical uniform open knot
		vknots = new double[crv_cnt+v_order]; //  formulation for the knot vectors
		
		geompts = new Point3D[crv_pts][crv_cnt]; // new Point3D[u][v]
		
		//setup u-knot vector
		for(int i=0;i<uknots.length;i++) {
			if(i>=0 && i<u_order)
				uknots[i] = 0.0;
			if(i>=u_order && i<crv_pts)
				uknots[i] = (i+1) - u_order;
			if(i>=crv_pts)
				uknots[i] = crv_pts - u_order + 1;
		}
		//normalize the uknot vector
		for(int i=0;i<uknots.length;i++) {
			uknots[i] = uknots[i]/(uknots[uknots.length-1]);
		}
		//setup v-knot vector
		for(int i=0;i<vknots.length;i++) {
			if(i>=0 && i<v_order)
				vknots[i] = 0.0;
			if(i>=v_order && i<crv_cnt)
				vknots[i] = (i+1) - v_order;
			if(i>=crv_cnt)
				vknots[i] = crv_cnt - v_order + 1;
		}
		//normalize the vknot vector
		for(int i=0;i<vknots.length;i++) {
			vknots[i] = vknots[i]/(vknots[vknots.length-1]);
		}
		
		//fill the pt buffer
		for(int u=0;u<crv_pts;u++) {
			for(int v=0;v<crv_cnt;v++) {
				geompts[u][v] = curves.get(v).geompts[u];
			}
		}
	}
	public Point3D evaluate(double u, double v, JTextArea outwindow) {
		double x = 0.0;
		double y = 0.0;
		double z = 0.0;
		
		int ucnt = geompts.length;
		int vcnt = geompts[0].length;
		
		for(int i=0;i<ucnt;i++) {
			for(int j=0;j<vcnt;j++) {
				double N = BSpline.getBasisFunction(i, u, u_order, uknots, outwindow);
				double M = BSpline.getBasisFunction(j, v, v_order, vknots, outwindow);
				x = x + geompts[i][j].x*N*M;
				y = y + geompts[i][j].y*N*M;
				//z = z + geompts[i][j].z*N*M;
			}
		}
		Point3D pt = new Point3D(x,y,z);
		//outwindow.append("U: " + Double.toString(u) + ", V: " + Double.toString(v) + ", " + pt.toString() + "\n");
		return pt;
	}

	//-----------
	// BSplineSurface FileIO
	// BSplineSurface File Structure
	// ent_type,tag,name:u_order,v_order,uknot_cnt,vknot_cnt,u-pt-dim,v-pt-dim
	// uknot1, ... , uknotN
	// vknot1, ... , vknotN
	// pt_u1v1, pt_u1v2, ... , pt_u1vM
	// pt_u2v1, pt_u2v2, ... , pt_u2vM
	// ...
	// pt_uNv1, pt_uNv2, ... , pt_uNvM
	public String getFileString() {
		String output = Integer.toString(type) + "," + Integer.toString(tag) + "," + name + ":";
		output = output + Integer.toString(u_order) + "," + Integer.toString(v_order) + "," + Integer.toString(uknots.length) + "," + Integer.toString(vknots.length);
		output = output + "," + Integer.toString(geompts.length) + "," + Integer.toString(geompts[0].length) + "\n";
		output = output + Double.toString(uknots[0]);
		for(int i=1;i<uknots.length;i++) {
			output = output + "," + Double.toString(uknots[i]);
		}
		output = output + "\n" + Double.toString(vknots[0]);
		for(int i=1;i<vknots.length;i++) {
			output = output + "," + Double.toString(vknots[i]);
		}
		int M = geompts[0].length;
		int N = geompts.length;
		for(int i=0;i<N;i++) {
			output = output + "\n";
			output = output + Integer.toString(geompts[i][0].tag);
			for(int j=1;j<M;j++) {
				output = output +"," + Integer.toString(geompts[i][j].tag);
			}
		}		
		return output;
	}
	public BSplineSurface(String filestring, GeomManager geom_manager) {
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
		u_order = Integer.valueOf(data[0]);
		v_order = Integer.valueOf(data[1]);
		int uknot_cnt = Integer.valueOf(data[2]);
		int vknot_cnt = Integer.valueOf(data[3]);
		int N = Integer.valueOf(data[4]);
		int M = Integer.valueOf(data[5]);
		
		//parse the uknot data
		data = data_lines[1].split(",");
		uknots = new double[uknot_cnt];
		for(int i=0;i<uknot_cnt;i++) {
			uknots[i] = Double.valueOf(data[i]);
		}
		//parse the uknot data
		data = data_lines[2].split(",");
		vknots = new double[vknot_cnt];
		for(int i=0;i<vknot_cnt;i++) {
			vknots[i] = Double.valueOf(data[i]);
		}
		
		geompts = new Point3D[N][M];
		//parse the point data
		for(int i=0;i<N;i++) {
			int ci = i+3;
			data = data_lines[ci].split(",");
			for(int j=0;j<M;j++) {
				int cpt = Integer.valueOf(data[j]);
				Point3D pt = (Point3D) geom_manager.getEntity(cpt);
				geompts[i][j] = pt;
			}
		}		
	}
}
