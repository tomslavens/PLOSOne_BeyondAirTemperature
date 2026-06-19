package geom;

import javax.swing.JTextArea;

import data.Entity;

public abstract class ParamSurface extends Entity  {
	public int u_order; //order in the u-direction
	public int v_order; //order in the v-direction
	public double[] uknots; //knots for the u-direction
	public double[] vknots; //knots for the v-direction
	public Point3D[][] geompts; //control points of the surface in the [u][v] directions
	
	public abstract Point3D evaluate(double u, double v, JTextArea outwindow);
	
}
