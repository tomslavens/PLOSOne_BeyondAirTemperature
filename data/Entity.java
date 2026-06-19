package data;

public abstract class Entity {
	
	public int type;     // 0=Point3D, 1=BSplineCurve, 2=BSplineSurface, 3=NURBCurve, 4=Line3D, 5=constraint
	public String name;
	public int tag;
	public static int POINT3D = 0;
	public static int PARAMCURVE = 1;
	//public static int BSPLINECURVE = 1;
	public static int BSPLINESURFACE = 2;
	//public static int NURBCURVE = 3;
	//public static int LINE3D = 4;
	public static int CONSTRAINT = 5;
	public static int TRIANGLE = 6;
	public static int TRIANGLEMESH = 7;
	public static int BOUNDEDSURF = 8;
	public static int NODE = 9;
	public static int ANALYSISMESH = 10;
	public static int BOUNDARYCONDITION = 11;
	public static int TRIELEM = 12;
	public static int MATERIAL = 13;
	public static int MESHCONDITION = 14;
	public static int LINEHULL = 15;
	
	public abstract String getFileString();
}
