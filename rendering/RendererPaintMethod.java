package rendering;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.text.DecimalFormat;

import javax.swing.JTextArea;

import analysis.BBRadiationOnCurveBC;
import analysis.BoundaryCondition;
import analysis.Node3D;
import data.GeomManager;
import geom.Line3D;
import geom.ParamCurve;
import geom.Point3D;
import geom_funcs.Constraint;
import geom_funcs.Length2DConstraint;
import geom_funcs.PointAtLocationConstraint;
import linear_math.LinearFunctions;
import linear_math.Vector3D;

public class RendererPaintMethod {

	public static void paintGeometry(Graphics g, JDynomaticRenderer window, JTextArea outwindow) {
		int fme_width = window.getWidth();
		int fme_height = window.getHeight();
		double xscale = (double) fme_width/(window.xmax-window.xmin);
		double yscale = (double) fme_height/(window.ymax-window.ymin);
		
		double con_length = 10/xscale;

		g.setColor(Color.WHITE);
		g.fillRect(0, 0, fme_width, fme_height);
		g.setColor(Color.BLACK);
		g.drawRect(0, 0, fme_width-1, fme_height-1);
		
		//draw an image if we have one
		if(window.image.image != null) {
			int pxl_width = window.image.image.getWidth();
			int pxl_height = window.image.image.getHeight();
			double real_width = window.image.real_width;
			double real_height = window.image.real_height;
			
			int cx1 = (int) Math.floor((double) (0.0-window.xmin)*xscale);
			int cy1 = (int) Math.floor((double) (window.ymax - real_height)*yscale);		
			int rend_width = (int) Math.floor((double) real_width*xscale);
			int rend_height = (int) Math.floor((double) real_height*yscale);
			
			g.drawImage(window.image.image, cx1,cy1,rend_width,rend_height,null);
		}
		
		//draw our surfaces
		g.setColor(Color.GRAY);
		for(int i=0;i<window.geom_manager.bsurfCnt();i++) {
			double u = 0.0;
			double du = 0.1;
			while(u<=(1.0)) {
				double v = 0.0;
				double dv = 0.05;
				while(v<=(1.0)) {
					Point3D cpt1 = window.geom_manager.bsrfAt(i).evaluate(u, v, outwindow);
					Point3D cpt2 = window.geom_manager.bsrfAt(i).evaluate(u, v+dv, outwindow);
					int cx1 = (int) Math.floor((double) (cpt1.x-window.xmin)*xscale);
					int cy1 = (int) Math.floor((double) (window.ymax - cpt1.y)*yscale);
					int cx2 = (int) Math.floor((double) (cpt2.x-window.xmin)*xscale);
					int cy2 = (int) Math.floor((double) (window.ymax - cpt2.y)*yscale);
					g.drawLine(cx1, cy1, cx2, cy2);
					v = v+dv;
				}
				u = u+du;
			}
		}
		//draw our boundedsurfaces
		for(int i=0;i<window.geom_manager.tSurfCnt();i++) {
			int num_tri = window.geom_manager.tsrfAt(i).mesh.triangles.size();
			for(int j=0;j<num_tri;j++) {
				int ipt1 = window.geom_manager.tsrfAt(i).mesh.triangles.get(j).pt1;
				int ipt2 = window.geom_manager.tsrfAt(i).mesh.triangles.get(j).pt2;
				int ipt3 = window.geom_manager.tsrfAt(i).mesh.triangles.get(j).pt3;
				Point3D cpt1 = window.geom_manager.tsrfAt(i).mesh.pts.get(ipt1);
				Point3D cpt2 = window.geom_manager.tsrfAt(i).mesh.pts.get(ipt2);
				Point3D cpt3 = window.geom_manager.tsrfAt(i).mesh.pts.get(ipt3);
				int cx1 = (int) Math.floor((double) (cpt1.x-window.xmin)*xscale);
				int cy1 = (int) Math.floor((double) (window.ymax - cpt1.y)*yscale);
				int cx2 = (int) Math.floor((double) (cpt2.x-window.xmin)*xscale);
				int cy2 = (int) Math.floor((double) (window.ymax - cpt2.y)*yscale);
				int cx3 = (int) Math.floor((double) (cpt3.x-window.xmin)*xscale);
				int cy3 = (int) Math.floor((double) (window.ymax - cpt3.y)*yscale);
				g.drawLine(cx1, cy1, cx2, cy2);
				g.drawLine(cx1, cy1, cx3, cy3);
				g.drawLine(cx3, cy3, cx2, cy2);
				//g.setColor(Color.darkGray);
				//g.fillRect(cx1, cy1, 4, 4);
				//g.fillRect(cx2, cy2, 4, 4);
				//g.fillRect(cx2, cy2, 4, 4);
				//Point3D tri_center = window.geom_manager.tsurfs.get(i).mesh.triangles.get(j).getCentroid(window.geom_manager.tsurfs.get(i).mesh.pts);
				//int tx1 = (int) Math.floor((double) (tri_center.x-window.xmin)*xscale);
				//int ty1 = (int) Math.floor((double) (window.ymax - tri_center.y)*yscale);
				//g.setColor(Color.GREEN);
				//g.fillRect(tx1, ty1, 4, 4);
				//int[] xs = new int[] {cx1,cx2,cx3};
				//int[] ys = new int[] {cy1,cy2,cy3};
				//g.fillPolygon(xs, ys, 3);
				g.setColor(Color.GRAY);
			}
		}
		//draw our boundedsurfaces
		for(int i=0;i<window.geom_manager.hSurfCnt();i++) {
			Color hedges = new Color(86,124,150,59);
			float lineWidth = 5.0f; // 5 pixels thick
		    BasicStroke stroke = new BasicStroke(lineWidth);
		    Graphics2D g2d = (Graphics2D) g;
		    g2d.setStroke(stroke);
		    
			g.setColor(hedges);
			int num_lines = window.geom_manager.hsrfAt(i).curves.size();
			for(int j=0;j<num_lines;j++) {
				Point3D cpt1 = window.geom_manager.hsrfAt(i).curves.get(j).geompts[0];
				Point3D cpt2 = window.geom_manager.hsrfAt(i).curves.get(j).geompts[1];
				int cx1 = (int) Math.floor((double) (cpt1.x-window.xmin)*xscale);
				int cy1 = (int) Math.floor((double) (window.ymax - cpt1.y)*yscale);
				int cx2 = (int) Math.floor((double) (cpt2.x-window.xmin)*xscale);
				int cy2 = (int) Math.floor((double) (window.ymax - cpt2.y)*yscale);
				g2d.drawLine(cx1, cy1, cx2, cy2);
			}
			stroke = new BasicStroke(1.0f);
			g2d.setStroke(stroke);
		}
		
		//draw our meshii
		if(window.paint_node_solution) {
			//get the min/max value of the data
			double max_val = -9999999;
			double min_val = 9999999;
			for(int i=0;i<window.geom_manager.meshCnt();i++) {
				for(int j=0;j<window.geom_manager.meshAt(i).nodes.size();j++) {
					double cval = window.geom_manager.meshAt(i).nodes.get(j).values[0];
					if(cval<min_val)
						min_val=cval;
					if(cval>max_val)
						max_val=cval;
				}
			}
			min_val = 0.99*min_val;
			max_val = 1.01*max_val;
			//just gonna do a gradiation based on the min/max
			for(int i=0;i<window.geom_manager.meshCnt();i++) {
				int num_tri = window.geom_manager.meshAt(i).elems.size();
				for(int j=0;j<num_tri;j++) {
					int ipt1 = window.geom_manager.meshAt(i).elems.get(j).pt1;
					int ipt2 = window.geom_manager.meshAt(i).elems.get(j).pt2;
					int ipt3 = window.geom_manager.meshAt(i).elems.get(j).pt3;
					Node3D cpt1 = window.geom_manager.meshAt(i).nodes.get(ipt1);
					Node3D cpt2 = window.geom_manager.meshAt(i).nodes.get(ipt2);
					Node3D cpt3 = window.geom_manager.meshAt(i).nodes.get(ipt3);
					int cx1 = (int) Math.floor((double) (cpt1.x-window.xmin)*xscale);
					int cy1 = (int) Math.floor((double) (window.ymax - cpt1.y)*yscale);
					int cx2 = (int) Math.floor((double) (cpt2.x-window.xmin)*xscale);
					int cy2 = (int) Math.floor((double) (window.ymax - cpt2.y)*yscale);
					int cx3 = (int) Math.floor((double) (cpt3.x-window.xmin)*xscale);
					int cy3 = (int) Math.floor((double) (window.ymax - cpt3.y)*yscale);
					
					double pt1val = cpt1.values[0];
					double pt2val = cpt2.values[0];
					double pt3val = cpt3.values[0];
					double ave_val = (pt1val+pt2val+pt3val)/3.0;
					g.setColor(window.rendoptions.getContorColor(ave_val, min_val, max_val));
					int[] x = new int[] {cx1,cx2,cx3};
					int[] y = new int[] {cy1,cy2,cy3};
					g.fillPolygon(x, y, 3);
				}
			}
			RendererPaintMethod.paintContourBand(min_val,max_val,g,window,outwindow);
		}
		// here we paint the nodal stiffness variable
		if(window.paint_node_stiffness) {
			//get the min/max value of the data
			double max_val = -9999999;
			double min_val = 9999999;
			for(int i=0;i<window.geom_manager.meshCnt();i++) {
				for(int j=0;j<window.geom_manager.meshAt(i).nodes.size();j++) {
					double cval = window.geom_manager.meshAt(i).nodes.get(j).values[1];
					if(cval<min_val)
						min_val=cval;
					if(cval>max_val)
						max_val=cval;
				}
			}
			min_val = 0.99*min_val;
			max_val = 1.01*max_val;
			//just gonna do a gradiation based on the min/max
			for(int i=0;i<window.geom_manager.meshCnt();i++) {
				int num_tri = window.geom_manager.meshAt(i).elems.size();
				for(int j=0;j<num_tri;j++) {
					int ipt1 = window.geom_manager.meshAt(i).elems.get(j).pt1;
					int ipt2 = window.geom_manager.meshAt(i).elems.get(j).pt2;
					int ipt3 = window.geom_manager.meshAt(i).elems.get(j).pt3;
					Node3D cpt1 = window.geom_manager.meshAt(i).nodes.get(ipt1);
					Node3D cpt2 = window.geom_manager.meshAt(i).nodes.get(ipt2);
					Node3D cpt3 = window.geom_manager.meshAt(i).nodes.get(ipt3);
					int cx1 = (int) Math.floor((double) (cpt1.x-window.xmin)*xscale);
					int cy1 = (int) Math.floor((double) (window.ymax - cpt1.y)*yscale);
					int cx2 = (int) Math.floor((double) (cpt2.x-window.xmin)*xscale);
					int cy2 = (int) Math.floor((double) (window.ymax - cpt2.y)*yscale);
					int cx3 = (int) Math.floor((double) (cpt3.x-window.xmin)*xscale);
					int cy3 = (int) Math.floor((double) (window.ymax - cpt3.y)*yscale);
					
					double pt1val = cpt1.values[1];
					double pt2val = cpt2.values[1];
					double pt3val = cpt3.values[1];
					double ave_val = (pt1val+pt2val+pt3val)/3.0;
					g.setColor(window.rendoptions.getContorColor(ave_val, min_val, max_val));
					int[] x = new int[] {cx1,cx2,cx3};
					int[] y = new int[] {cy1,cy2,cy3};
					g.fillPolygon(x, y, 3);
				}
			}
			RendererPaintMethod.paintContourBand(min_val,max_val,g,window,outwindow);
		}
		//or just draw the mesh
		if(!window.paint_node_stiffness && !window.paint_node_solution) {		
			g.setColor(window.rendoptions.MESH_COLOR);
			for(int i=0;i<window.geom_manager.meshCnt();i++) {
				int num_tri = window.geom_manager.meshAt(i).elems.size();
				for(int j=0;j<num_tri;j++) {
					int ipt1 = window.geom_manager.meshAt(i).elems.get(j).pt1;
					int ipt2 = window.geom_manager.meshAt(i).elems.get(j).pt2;
					int ipt3 = window.geom_manager.meshAt(i).elems.get(j).pt3;
					Point3D cpt1 = window.geom_manager.meshAt(i).nodes.get(ipt1);
					Point3D cpt2 = window.geom_manager.meshAt(i).nodes.get(ipt2);
					Point3D cpt3 = window.geom_manager.meshAt(i).nodes.get(ipt3);
					int cx1 = (int) Math.floor((double) (cpt1.x-window.xmin)*xscale);
					int cy1 = (int) Math.floor((double) (window.ymax - cpt1.y)*yscale);
					int cx2 = (int) Math.floor((double) (cpt2.x-window.xmin)*xscale);
					int cy2 = (int) Math.floor((double) (window.ymax - cpt2.y)*yscale);
					int cx3 = (int) Math.floor((double) (cpt3.x-window.xmin)*xscale);
					int cy3 = (int) Math.floor((double) (window.ymax - cpt3.y)*yscale);
					g.drawLine(cx1, cy1, cx2, cy2);
					g.drawLine(cx1, cy1, cx3, cy3);
					g.drawLine(cx3, cy3, cx2, cy2);
				}
			}
		}
		//draw our queried node solution
		if(window.query_node_solution) {
			g.setColor(Color.BLACK);
			for(int i=0;i<window.ndbuffer.size();i++) {
				int cx1 = (int) Math.floor((double) (window.ndbuffer.get(i).x-window.xmin)*xscale);
				int cy1 = (int) Math.floor((double) (window.ymax - window.ndbuffer.get(i).y)*yscale);
				double val = window.ndbuffer.get(i).values[0];
				cx1 = cx1+4;
				g.drawString(Double.toString(val), cx1, cy1);
			}
		}
		
		//draw our pts	
		//g.setColor(Color.BLACK);
		g.setColor(new Color(238,144,51));
		for(int i=0;i<window.geom_manager.pntCnt();i++) {
			int cx = (int) Math.floor((double) (window.geom_manager.pntAt(i).x-window.xmin)*xscale);
			int cy = (int) Math.floor((double) (window.ymax - window.geom_manager.pntAt(i).y)*yscale);
			if(window.geom_manager.pntAt(i).z > 0.0) {
				int gray = (int) Math.round(window.geom_manager.pntAt(i).z);
				g.setColor(new Color(gray,gray,gray));
			}
			g.fillRect(cx, cy, 4, 4);
			g.setColor(new Color(238,144,51));
		}
		
		//draw our splines
		g.setColor(Color.BLACK);
		for(int i=0;i<window.geom_manager.crvCnt();i++) {
			//change color for spline type
			if(window.geom_manager.crvAt(i).curvetype == ParamCurve.NURBCRV)
				g.setColor(Color.DARK_GRAY);
			else
				g.setColor(Color.BLACK);
			for(int j=0;j<window.geom_manager.crvAt(i).geompts.length;j++) {
				int cx = (int) Math.floor((double) (window.geom_manager.crvAt(i).geompts[j].x-window.xmin)*xscale);
				int cy = (int) Math.floor((double) (window.ymax - window.geom_manager.crvAt(i).geompts[j].y)*yscale);
				g.fillRect(cx, cy, 4, 4);
			}
			double t = 0.0;
			double dt = 0.01;
			while(t<=1.0) {
				Point3D cpt1 = window.geom_manager.crvAt(i).evaluate(t);
				Point3D cpt2 = window.geom_manager.crvAt(i).evaluate(t+dt);
				int cx1 = (int) Math.floor((double) (cpt1.x-window.xmin)*xscale);
				int cy1 = (int) Math.floor((double) (window.ymax - cpt1.y)*yscale);
				int cx2 = (int) Math.floor((double) (cpt2.x-window.xmin)*xscale);
				int cy2 = (int) Math.floor((double) (window.ymax - cpt2.y)*yscale);
				g.drawLine(cx1, cy1, cx2, cy2);
				t = t+dt;
			}
		}
		
		//draw out constraints
		g.setColor(window.rendoptions.CONSTRAINT_COLOR);
		for(int i=0;i<window.geom_manager.constCnt();i++) {
			Constraint craint = window.geom_manager.constraitnAt(i);
			if(craint.contype == Constraint.ISO_X || craint.contype == Constraint.ISO_Y || craint.contype == Constraint.ISO_Z) {
				//we are going to try and draw an arrow along the line
				//first grab the line vector in our drawing plane
				Line3D target = (Line3D) craint.targets[0];
				Vector3D line_vector = new Vector3D(target);
				//get the planar perpendicular line
				Vector3D perp_vector = LinearFunctions.getXYPerpendicularVector(line_vector);
				Point3D mid_pt = target.getMidPoint();
				Point3D bk_pt = LinearFunctions.offsetPointAlongVector(line_vector, mid_pt, -1.0*con_length);
				Point3D sd_pt = LinearFunctions.offsetPointAlongVector(perp_vector, bk_pt, -1.0*con_length);
				int cx1 = (int) Math.floor((double) (mid_pt.x-window.xmin)*xscale);
				int cy1 = (int) Math.floor((double) (window.ymax - mid_pt.y)*yscale);
				int cx2 = (int) Math.floor((double) (sd_pt.x-window.xmin)*xscale);
				int cy2 = (int) Math.floor((double) (window.ymax - sd_pt.y)*yscale);
				g.drawLine(cx1, cy1, cx2, cy2);
				sd_pt = LinearFunctions.offsetPointAlongVector(perp_vector, bk_pt, con_length);
				cx2 = (int) Math.floor((double) (sd_pt.x-window.xmin)*xscale);
				cy2 = (int) Math.floor((double) (window.ymax - sd_pt.y)*yscale);
				g.drawLine(cx1, cy1, cx2, cy2);
			}
			if(craint.contype == Constraint.LENGTH2D) {
				//first grab the line vector in our drawing plane
				Line3D target = (Line3D) craint.targets[0];
				Length2DConstraint lconst = (Length2DConstraint) craint;
				Vector3D line_vector = lconst.vector;
				//get the planar perpendicular line
				Vector3D perp_vector = LinearFunctions.getXYPerpendicularVector(line_vector);
				Point3D mid_pt = target.getMidPoint();
				
				//make pts adjacent to the beginning and end points of the line
				Point3D aft_pt1 = LinearFunctions.offsetPointAlongVector(perp_vector, target.geompts[0], -1.0*con_length);
				Point3D aft_pt2 = LinearFunctions.offsetPointAlongVector(perp_vector, target.geompts[0], -0.05*con_length);
				Point3D fwd_pt1 = LinearFunctions.offsetPointAlongVector(perp_vector, target.geompts[1], -1.0*con_length);
				Point3D fwd_pt2 = LinearFunctions.offsetPointAlongVector(perp_vector, target.geompts[1], -0.05*con_length);
				Point3D lbl_pt = LinearFunctions.offsetPointAlongVector(perp_vector, mid_pt, -1.5*con_length);
				int cx1 = (int) Math.floor((double) (aft_pt1.x-window.xmin)*xscale);
				int cy1 = (int) Math.floor((double) (window.ymax - aft_pt1.y)*yscale);
				int cx2 = (int) Math.floor((double) (aft_pt2.x-window.xmin)*xscale);
				int cy2 = (int) Math.floor((double) (window.ymax - aft_pt2.y)*yscale);
				g.drawLine(cx1, cy1, cx2, cy2);
				cx1 = (int) Math.floor((double) (fwd_pt1.x-window.xmin)*xscale);
				cy1 = (int) Math.floor((double) (window.ymax - fwd_pt1.y)*yscale);
				cx2 = (int) Math.floor((double) (fwd_pt2.x-window.xmin)*xscale);
				cy2 = (int) Math.floor((double) (window.ymax - fwd_pt2.y)*yscale);
				g.drawLine(cx1, cy1, cx2, cy2);
				cx1 = (int) Math.floor((double) (fwd_pt1.x-window.xmin)*xscale);
				cy1 = (int) Math.floor((double) (window.ymax - fwd_pt1.y)*yscale);
				cx2 = (int) Math.floor((double) (aft_pt1.x-window.xmin)*xscale);
				cy2 = (int) Math.floor((double) (window.ymax - aft_pt1.y)*yscale);
				g.drawLine(cx1, cy1, cx2, cy2);
				cx1 = (int) Math.floor((double) (lbl_pt.x-window.xmin)*xscale);
				cy1 = (int) Math.floor((double) (window.ymax - lbl_pt.y)*yscale);
				DecimalFormat df = new DecimalFormat("#.##");
				g.drawString(df.format(lconst.vector.magnitude), cx1, cy1);
			}
			if(craint.contype == Constraint.POINTATLOC) {
				//first grab the target Point
				Point3D target = (Point3D) craint.targets[0];
				PointAtLocationConstraint palc = (PointAtLocationConstraint) craint;
				double x = palc.location.x;
				double y = palc.location.x;
				int cx1 = (int) Math.floor((double) (target.x-window.xmin)*xscale);
				int cy1 = (int) Math.floor((double) (window.ymax - target.y)*yscale);
				cx1 = cx1-16;
				cy1 = cy1+16;
				DecimalFormat df = new DecimalFormat("#.##");
				String loc = "<" + df.format(x) + "," + df.format(y) + ">";
				g.drawString(loc, cx1, cy1);
			}
		}
		
		//draw out Boundary Conditions
		for(int i=0;i<window.geom_manager.bcCnt();i++) {
			BoundaryCondition bc = window.geom_manager.bcAt(i);
			if(bc.BCtype == BoundaryCondition.TEMPONNODE) {
				g.setColor(window.rendoptions.BC_COLOR1);
				for(int j=0;j<bc.entities.size();j++) {
					Node3D cnode = (Node3D) bc.entities.get(j);
					int cx1 = (int) Math.floor((double) (cnode.x-window.xmin)*xscale)-3;
					int cy1 = (int) Math.floor((double) (window.ymax - cnode.y)*yscale)-3;
					int cx2 = (int) Math.floor((double) (cnode.x-window.xmin)*xscale)+3;
					int cy2 = (int) Math.floor((double) (window.ymax - cnode.y)*yscale)+3;
					int cx3 = (int) Math.floor((double) (cnode.x-window.xmin)*xscale)-3;
					int cy3 = (int) Math.floor((double) (window.ymax - cnode.y)*yscale)+3;
					int cx4 = (int) Math.floor((double) (cnode.x-window.xmin)*xscale)+3;
					int cy4 = (int) Math.floor((double) (window.ymax - cnode.y)*yscale)-3;
					g.drawLine(cx1, cy1, cx2, cy2);
					g.drawLine(cx3, cy3, cx4, cy4);
				}
			}
			if(bc.BCtype == BoundaryCondition.TEMPONCURVE) {
				g.setColor(window.rendoptions.BC_COLOR1);
				for(int j=0;j<bc.entities.size();j++) {
					ParamCurve curve = (ParamCurve) bc.entities.get(j); 
					double t = 0.0;
					double dt = 0.01;
					while(t<=1.0) {
						Point3D cpt1 = curve.evaluate(t);
						Point3D cpt2 = curve.evaluate(t+dt);
						int cx1 = (int) Math.floor((double) (cpt1.x-window.xmin)*xscale);
						int cy1 = (int) Math.floor((double) (window.ymax - cpt1.y)*yscale);
						int cx2 = (int) Math.floor((double) (cpt2.x-window.xmin)*xscale);
						int cy2 = (int) Math.floor((double) (window.ymax - cpt2.y)*yscale);
						g.drawLine(cx1, cy1, cx2, cy2);
						t = t+dt;
					}
				}
			}
			if(bc.BCtype == BoundaryCondition.HTCONCURVE) {
				g.setColor(RendererOptionBank.BC_COLOR2);
				for(int j=0;j<bc.entities.size();j++) {
					ParamCurve curve = (ParamCurve) bc.entities.get(j); 
					double t = 0.0;
					double dt = 0.01;
					while(t<=1.0) {
						Point3D cpt1 = curve.evaluate(t);
						Point3D cpt2 = curve.evaluate(t+dt);
						int cx1 = (int) Math.floor((double) (cpt1.x-window.xmin)*xscale);
						int cy1 = (int) Math.floor((double) (window.ymax - cpt1.y)*yscale);
						int cx2 = (int) Math.floor((double) (cpt2.x-window.xmin)*xscale);
						int cy2 = (int) Math.floor((double) (window.ymax - cpt2.y)*yscale);
						g.drawLine(cx1, cy1, cx2, cy2);
						t = t+dt;
					}
				}
			}
			if(bc.BCtype == BoundaryCondition.BBRADONCURVE) {
				g.setColor(RendererOptionBank.BC_COLOR2);
				for(int j=0;j<bc.entities.size();j++) {
					ParamCurve curve = (ParamCurve) bc.entities.get(j); 
					double t = 0.0;
					double dt = 0.01;
					int counter = 0;
					while(t<=1.0) {
						Point3D cpt1 = curve.evaluate(t);
						Point3D cpt2 = curve.evaluate(t+dt);
						int cx1 = (int) Math.floor((double) (cpt1.x-window.xmin)*xscale);
						int cy1 = (int) Math.floor((double) (window.ymax - cpt1.y)*yscale);
						int cx2 = (int) Math.floor((double) (cpt2.x-window.xmin)*xscale);
						int cy2 = (int) Math.floor((double) (window.ymax - cpt2.y)*yscale);
						g.drawLine(cx1, cy1, cx2, cy2);
						if(counter%10 == 0) {
							BBRadiationOnCurveBC radbc = (BBRadiationOnCurveBC) bc; 
							//Vector3D norm = curve.getNormXYAtParam(t);
							Vector3D dir = new Vector3D(cpt1,radbc.getSrcPoint());
							Point3D cpt3 = LinearFunctions.offsetPointAlongVector(dir, cpt1, curve.getCurveLength()/20);
							int cx3 = (int) Math.floor((double) (cpt3.x-window.xmin)*xscale);
							int cy3 = (int) Math.floor((double) (window.ymax - cpt3.y)*yscale);
							g.drawLine(cx1, cy1, cx3, cy3);
						}
						counter = counter+1;
						t = t+dt;
					}
				}
			}
		}
		
		//draw any selected pts or curves
		g.setColor(Color.RED);
		for(int i=0;i<window.ptbuffer.size();i++) {
			int cx = (int) Math.floor((double) (window.ptbuffer.get(i).x-window.xmin)*xscale);
			int cy = (int) Math.floor((double) (window.ymax - window.ptbuffer.get(i).y)*yscale);
			g.fillRect(cx, cy, 4, 4);
		}
		for(int i=0;i<window.crvbuffer.size();i++) {
			double t = 0.0;
			double dt = 0.01;
			while(t<(1.0-dt)) {
				Point3D cpt1 = window.crvbuffer.get(i).evaluate(t);
				Point3D cpt2 = window.crvbuffer.get(i).evaluate(t+dt);
				int cx1 = (int) Math.floor((double) (cpt1.x-window.xmin)*xscale);
				int cy1 = (int) Math.floor((double) (window.ymax - cpt1.y)*yscale);
				int cx2 = (int) Math.floor((double) (cpt2.x-window.xmin)*xscale);
				int cy2 = (int) Math.floor((double) (window.ymax - cpt2.y)*yscale);
				g.drawLine(cx1, cy1, cx2, cy2);
				t = t+dt;
			}
		}
		for(int i=0;i<window.ndbuffer.size();i++) {
			int cx = (int) Math.floor((double) (window.ndbuffer.get(i).x-window.xmin)*xscale);
			int cy = (int) Math.floor((double) (window.ymax - window.ndbuffer.get(i).y)*yscale);
			g.fillRect(cx, cy, 4, 4);
		}
		//draw the point highlighted when dragging them around
		g.setColor(window.rendoptions.SELECTED_COLOR);
		if(window.sel_index >= 0 && window.sel_index < window.geom_manager.pntCnt()) {
			int cx = (int) Math.floor((double) (window.geom_manager.pntAt(window.sel_index).x-window.xmin)*xscale);
			int cy = (int) Math.floor((double) (window.ymax - window.geom_manager.pntAt(window.sel_index).y)*yscale);
			g.fillRect(cx, cy, 4, 4);
		}
		
		//draw the frame
		g.setColor(Color.BLACK);
		g.drawRect(0, 0, fme_width-1, fme_height-1);
	}
	// paint the contour bands and labels at the bottom of the window
	private static void paintContourBand(double min, double max, Graphics g, JDynomaticRenderer window, JTextArea outwindow) {
		int box_size = 30;
		int color_count= 9;
		DecimalFormat df = new DecimalFormat("#.##");
		double dval = (max-min)/color_count;
		double val = min*1.00001;
		int fme_width = window.getWidth();
		int fme_height = window.getHeight();
		//int start_x = (int) Math.floor(fme_width/2.0) - box_size*4;
		int start_x = box_size/2;
		int start_y = (int) Math.floor(fme_height/2.0) - box_size*4;
		//int start_y = fme_height - 2*box_size;
		
		if(max > min) {
			for(int i=0;i<color_count;i++) {
				double cval = val+(i*dval);
				g.setColor(window.rendoptions.getContorColor(cval, min, max));
				g.fillRect(start_x, start_y+i*box_size, box_size, box_size);
				g.setColor(Color.BLACK);
				g.drawRect(start_x, start_y+i*box_size, box_size, box_size);
				int text_start = (int) Math.floor(start_x+1.1*box_size);
				g.drawString(df.format(cval), text_start, start_y+i*box_size+3);
			}
			double cval = val+(color_count*dval);
			g.setColor(Color.BLACK);
			int text_start = (int) Math.floor(start_x+1.1*box_size);
			g.drawString(df.format(cval), text_start, start_y+color_count*box_size+3);
		}
	}
	
}
