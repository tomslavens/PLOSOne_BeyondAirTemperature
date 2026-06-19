package data;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.*;

import javax.imageio.*;
import javax.swing.JTextArea;

import analysis.BoundaryCondition;
import analysis_mesh.AnalysisMesh;
import geom.BSplineCurve;
import geom.BSplineSurface;
import geom.BoundedSurface;
import geom.Line3D;
import geom.LineHull;
import geom.NURBArc;
import geom.NURBCurve;
import geom.OffsetCurve;
import geom.ParamCurve;
import geom.Point3D;
import geom_funcs.Constraint;
import images.ImageProcessing;
import rendering.JDynomaticRenderer;

public class JDOFileIO {
	private static String POINTREGIONDELIMITER = "-pt-";
	private static String CURVEREGIONDELIMITER = "-crv-";
	private static String BSURFACEREGIONDELIMITER = "-bsurf-";
	private static String CONSTRAINTREGIONDELIMITER = "-constraint-";
	private static String TSURFACEREGIONDELIMITER = "-tsurf-";
	private static String HSURFACEREGIONDELIMITER = "-hsurf-";
	private static String MESHREGIONDELIMITER = "-mesh-";
	private static String BCREGIONDELIMITER = "-bc-";
	
	public static void writeJDOFile(String fname,GeomManager geom_manager, JTextArea outwindow) {
		outwindow.append("Writing out data to " + fname + "\n");
		
		try {
			//Open the BufferedWriter
			BufferedWriter bw = new BufferedWriter(new FileWriter(fname));			
			
			//first write out our points
			bw.write(POINTREGIONDELIMITER + "\n");
			for(int i=0;i<geom_manager.pntCnt();i++) {
				bw.write(geom_manager.pntAt(i).getFileString() + "\n");
			}
			bw.write(POINTREGIONDELIMITER + "\n");
			bw.flush();
			
			//second write out our curves
			for(int i=0;i<geom_manager.crvCnt();i++) {
				bw.write(CURVEREGIONDELIMITER + "\n");
				bw.write(geom_manager.crvAt(i).getFileString() + "\n");
				bw.write(CURVEREGIONDELIMITER + "\n");
			}
			bw.flush();
			//third write out our BSplineSurfaces
			for(int i=0;i<geom_manager.bsurfCnt();i++) {
				bw.write(BSURFACEREGIONDELIMITER + "\n");
				bw.write(geom_manager.bsrfAt(i).getFileString() + "\n");
				bw.write(BSURFACEREGIONDELIMITER + "\n");
			}
			bw.flush();
			//third.one write out our BoundedSurfaces
			for(int i=0;i<geom_manager.tSurfCnt();i++) {
				bw.write(TSURFACEREGIONDELIMITER + "\n");
				bw.write(geom_manager.tsrfAt(i).getFileString() + "\n");
				bw.write(TSURFACEREGIONDELIMITER + "\n");
			}
			//third.two write out our LineHulls
			for(int i=0;i<geom_manager.hSurfCnt();i++) {
				bw.write(HSURFACEREGIONDELIMITER + "\n");
				bw.write(geom_manager.hsrfAt(i).getFileString() + "\n");
				bw.write(HSURFACEREGIONDELIMITER + "\n");
			}
			bw.flush();
			//fourth write out our geometric constraints
			bw.write(CONSTRAINTREGIONDELIMITER + "\n");
			for(int i=0;i<geom_manager.constCnt();i++) {
				bw.write(geom_manager.constraitnAt(i).getFileString() + "\n");
			}
			bw.write(CONSTRAINTREGIONDELIMITER + "\n");
			bw.flush();
			//fifth write out our analysis meshes
			for(int i=0;i<geom_manager.meshCnt();i++) {
				bw.write(MESHREGIONDELIMITER + "\n");
				bw.write(geom_manager.meshAt(i).getFileString() + "\n");
				bw.write(MESHREGIONDELIMITER + "\n");
			}
			bw.flush();
			//sixth write out our analysis boundary condtions
			bw.write(BCREGIONDELIMITER + "\n");
			for(int i=0;i<geom_manager.bcCnt();i++) {
				bw.write(geom_manager.bcAt(i).getFileString() + "\n");
			}
			bw.write(BCREGIONDELIMITER + "\n");
			bw.flush();
			
			bw.close();
		} catch(Exception E) {
			outwindow.append("File write failed!\n");
			outwindow.append(E.toString() + "\n");
		}		
	}
	
	public static void readJDOFile(String fname,GeomManager geom_manager, JTextArea outwindow) {
		outwindow.append("Reading data from " + fname + "\n");
		
		try {
			//Open the BufferedWriter
			BufferedReader br = new BufferedReader(new FileReader(fname));
			String line = br.readLine();
			while(line != null) {
				
				//if we have a point region
				if(line.equalsIgnoreCase(POINTREGIONDELIMITER)) {
					line = br.readLine();
					while(!line.equalsIgnoreCase(POINTREGIONDELIMITER)) {
						Point3D cpt = new Point3D(line);
						geom_manager.addPnt(cpt, cpt.tag);
						line = br.readLine();
					}
				}
				
				//if we have a curve region
				if(line.equalsIgnoreCase(CURVEREGIONDELIMITER)) {
					line = br.readLine();
					String curve_string = "";
					String curve_info = line;
					String[] headerparts = curve_info.split(":");
					String[] dataparts = headerparts[1].split(",");
					int curve_type = Integer.valueOf(dataparts[0]); //figure out our curve type
					while(!line.equalsIgnoreCase(CURVEREGIONDELIMITER)) {
						curve_string = curve_string + line + "\n";
						line = br.readLine();
					}
					if(curve_type == ParamCurve.BSPLINECRV) {
						BSplineCurve bspline = new BSplineCurve(curve_string,geom_manager);
						outwindow.append("Reading BSpline " + Integer.toString(bspline.tag)+ "\n");
						geom_manager.addCurve(bspline, bspline.tag);
					}
					if(curve_type == ParamCurve.NURBCRV) {
						NURBCurve nurb = new NURBCurve(curve_string,geom_manager);
						outwindow.append("Reading NURB " + Integer.toString(nurb.tag)+ "\n");
						geom_manager.addCurve(nurb, nurb.tag);
					}
					if(curve_type == ParamCurve.LINE3D) {
						Line3D pline = new Line3D(curve_string,geom_manager);
						outwindow.append("Reading Line3D " + Integer.toString(pline.tag)+ "\n");
						geom_manager.addCurve(pline, pline.tag);
					}
					if(curve_type == ParamCurve.NURBARC) {
						NURBArc nurb = new NURBArc(curve_string,geom_manager);
						outwindow.append("Reading NURBArc " + Integer.toString(nurb.tag)+ "\n");
						geom_manager.addCurve(nurb, nurb.tag);
					}
					if(curve_type == ParamCurve.OFFSETCRV) {
						OffsetCurve ocurve = new OffsetCurve(curve_string,geom_manager);
						outwindow.append("Reading OffsetCurve " + Integer.toString(ocurve.tag)+ "\n");
						geom_manager.addCurve(ocurve, ocurve.tag);
					}
				}
				
				//if we have a BSplineSurface region
				if(line.equalsIgnoreCase(BSURFACEREGIONDELIMITER)) {
					line = br.readLine();
					String surface_string = "";
					while(!line.equalsIgnoreCase(BSURFACEREGIONDELIMITER)) {
						surface_string = surface_string + line + "\n";
						line = br.readLine();
					}
					BSplineSurface bsurf = new BSplineSurface(surface_string,geom_manager);
					geom_manager.addBSplineSurface(bsurf, bsurf.tag);
				}
				
				//if we have a TSplineSurface region
				if(line.equalsIgnoreCase(TSURFACEREGIONDELIMITER)) {
					line = br.readLine();
					String surface_string = "";
					while(!line.equalsIgnoreCase(TSURFACEREGIONDELIMITER)) {
						surface_string = surface_string + line + "\n";
						line = br.readLine();
					}
					BoundedSurface tsurf = new BoundedSurface(surface_string,geom_manager);
					geom_manager.addBoundedSurface(tsurf, tsurf.tag);
				}
				
				//if we have a LineHull region
				if(line.equalsIgnoreCase(HSURFACEREGIONDELIMITER)) {
					line = br.readLine();
					String surface_string = "";
					while(!line.equalsIgnoreCase(HSURFACEREGIONDELIMITER)) {
						surface_string = surface_string + line + "\n";
						line = br.readLine();
					}
					LineHull hsurf = new LineHull(surface_string,geom_manager);
					geom_manager.addLineHull(hsurf, hsurf.tag);
				}
				
				//if we have a Constraint region
				if(line.equalsIgnoreCase(CONSTRAINTREGIONDELIMITER)) {
					line = br.readLine();
					while(!line.equalsIgnoreCase(CONSTRAINTREGIONDELIMITER)) {
						Constraint con = Constraint.getConstraintFromString(line,geom_manager);
						geom_manager.addConstraint(con, con.tag);
						line = br.readLine();
					}
				}
				
				//if we have a Mesh region
				if(line.equalsIgnoreCase(MESHREGIONDELIMITER)) {
					line = br.readLine();
					String mesh_string = "";
					while(!line.equalsIgnoreCase(MESHREGIONDELIMITER)) {
						mesh_string = mesh_string + line + "\n";
						line = br.readLine();
					}
					AnalysisMesh mesh = AnalysisMesh.getAnalysisMesh(mesh_string,geom_manager);
					geom_manager.addAnalysisMesh(mesh, mesh.tag);
				}
				
				//if we have the BC region
				if(line.equalsIgnoreCase(BCREGIONDELIMITER)) {
					line = br.readLine();
					while(!line.equalsIgnoreCase(BCREGIONDELIMITER)) {
						BoundaryCondition bc = BoundaryCondition.getBoundaryCondition(line, geom_manager);
						geom_manager.addBC(bc, bc.tag);
						line = br.readLine();
					}
				}
				
				line = br.readLine();
			}
			br.close();
		} catch(Exception E) {
			outwindow.append("File read failed!\n");
			outwindow.append(E.toString() + "\n");
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			E.printStackTrace(pw);
			int line = E.getStackTrace()[0].getLineNumber();
			outwindow.append(sw.toString() + "\n");
		}
	}
	
	//write only points to a file
	public static void writePointFile(String fname,GeomManager geom_manager, JTextArea outwindow) {
		outwindow.append("Writing out data to " + fname + "\n");		
		try {
			//Open the BufferedWriter
			BufferedWriter bw = new BufferedWriter(new FileWriter(fname));			
			
			//write out our points
			for(int i=0;i<geom_manager.pntCnt();i++) {
				bw.write(geom_manager.pntAt(i).getRawFileString() + "\n");
			}
			bw.flush();			
			bw.close();
		} catch(Exception E) {
			outwindow.append("writePointFile() failed!\n");
			outwindow.append(E.toString() + "\n");
		}	
	}
	//write only LineHull points to a file
	public static void writeLineHullFile(String fname,GeomManager geom_manager, JDynomaticRenderer renderer, JTextArea outwindow) {
		outwindow.append("Writing out data to " + fname + "\n");		
		try {
			//Open the BufferedWriter
			BufferedWriter bw = new BufferedWriter(new FileWriter(fname));			
			
			//write out our LineHulls
			for(int i=0;i<geom_manager.hSurfCnt();i++) {
				//bw.write(geom_manager.hsrfAt(i).getRawFileString() + "\n");
				bw.write(geom_manager.hsrfAt(i).generateRawSplineFileString(1000, outwindow) + ">\n");
			}
			bw.flush();
			bw.close();
			
			// if renderer has an image - extract it to a *.png
			if(renderer.getImage().image != null) {
				outwindow.append("Exporting image regions!\n");
				for(int i=0;i<geom_manager.hSurfCnt();i++) {
					BufferedImage bi = ImageProcessing.extractImageRegion(geom_manager.hsrfAt(i), renderer.getImage(), outwindow);
					String gname = fname + ".png";
					File outputfile = new File(gname);
					ImageIO.write(bi, "png", outputfile);
					bi = null;
				}
			}
			
			// save a jdo file
			String jname = fname + ".jdo";
			writeJDOFile(jname,geom_manager,outwindow);	
		} catch(Exception E) {
			outwindow.append("writeLineHullFile) failed!\n");
			outwindow.append(E.toString() + "\n");
		}	
	}
	
}
