package images;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JTextArea;

import geom.BSplineCurve;
import geom.BSplineSurface;
import geom.LineHull;
import geom.Point3D;
import linear_math.LinearFunctions;

public class ImageProcessing {
	
	public static ArrayList<Point3D> extractImageEdge(LineHull hsurf, DynoImage image) {
		ArrayList<Point3D> envelope = new ArrayList<Point3D>();
		double min_x = hsurf.getMinX();
		double max_x = hsurf.getMaxX();
		double min_y = hsurf.getMinY();
		double max_y = hsurf.getMaxY();
		Graphics g = image.image.getGraphics();
		
		for(int i=0;i<image.image.getHeight();i++) {
			//at each height within the hull do stuff
			double cy = image.real_height - i*image.scalar;
			if(cy >= min_y && cy < max_y) {
				ArrayList<Point3D> cline = new ArrayList<Point3D>(); // the horzontal list of pixles to do edge detection
				for(int j=0;j<image.image.getWidth();j++) {
					double cx = j*image.scalar;
					if(cx >= min_x && cx < max_x) {
						Point3D npt = new Point3D(cx,cy,0.0);
						if(hsurf.ptInHulle(npt)) { //if this point is in the hull
							//get current pixel color
							int RGBA = image.image.getRGB(j, i);
			                int alpha = (RGBA >> 24) & 255;
			                int red = (RGBA >> 16) & 255;
			                int green = (RGBA >> 8) & 255;
			                int blue = RGBA & 255;
			                int gscal = (int) (0.1111*(Math.round(0.6*red + 0.4*green + 0.11*blue))); 
			                
							npt.z = (double) gscal; //store the gray-scale value in the Z value
							cline.add(npt);
							
							//envelope.add(npt); // COMMENT ME OUT WHEN DONE
						}
					}
				}
				// now detect a sharp gradient in this line
				if(cline.size() > 10) {
					//double[] grad = new double[cline.size()-1];
					double[] grad = new double[cline.size()];
					for(int j=0;j<cline.size();j++) {
						if(j==0)
							grad[j] = (cline.get(j+1).z - cline.get(j).z)/1.0;
						if(j==cline.size()-1)
							grad[j] = (cline.get(j).z - cline.get(j-1).z)/1.0;
						if(j!=0 && j!=cline.size()-1)
							grad[j] = (cline.get(j+1).z - cline.get(j-1).z)/2.0;
					}
					double grad_mean = LinearFunctions.average(grad);
					double grad_std = LinearFunctions.stdev(grad);
					for(int j=0;j<grad.length;j++) {
						if(Math.abs(grad[j]) > 2.75*grad_std+grad_mean) {
							cline.get(j).z = 0.0;
							envelope.add(cline.get(j));
						}
					}
				}
			}
		}
		
		// vertical edge detect
		for(int i=0;i<image.image.getWidth();i++) {
			//at each width within the hull do stuff
			double cx = i*image.scalar;
			if(cx >= min_x && cx < max_x) {
				ArrayList<Point3D> cline = new ArrayList<Point3D>(); // the vertical list of pixles to do edge detection
				for(int j=0;j<image.image.getHeight();j++) {
					double cy = image.real_height - j*image.scalar;
					if(cy >= min_y && cy < max_y) {
						Point3D npt = new Point3D(cx,cy,0.0);
						if(hsurf.ptInHulle(npt)) { //if this point is in the hull
							//get current pixel color
							int RGBA = image.image.getRGB(i,j);
			                int alpha = (RGBA >> 24) & 255;
			                int red = (RGBA >> 16) & 255;
			                int green = (RGBA >> 8) & 255;
			                int blue = RGBA & 255;
			                int gscal = (int) (0.1111*(Math.round(0.6*red + 0.4*green + 0.11*blue))); 
			                
							npt.z = (double) gscal; //store the gray-scale value in the Z value
							cline.add(npt);
						}
					}
				}
				// now detect a sharp gradient in this line
				if(cline.size() > 10) {
					//double[] grad = new double[cline.size()-1];
					double[] grad = new double[cline.size()];
					for(int j=0;j<cline.size();j++) {
						if(j==0)
							grad[j] = (cline.get(j+1).z - cline.get(j).z)/1.0;
						if(j==cline.size()-1)
							grad[j] = (cline.get(j).z - cline.get(j-1).z)/1.0;
						if(j!=0 && j!=cline.size()-1)
							grad[j] = (cline.get(j+1).z - cline.get(j-1).z)/2.0;
					}
					double grad_mean = LinearFunctions.average(grad);
					double grad_std = LinearFunctions.stdev(grad);
					for(int j=0;j<grad.length;j++) {
						if(Math.abs(grad[j]) > 2.75*grad_std+grad_mean) {
							//Point3D npt = new Point3D(cx,cy,0.0);
							cline.get(j).z = 0.0;
							envelope.add(cline.get(j));
						}
					}
				}
			}
		}
		// 45 deg? edge detect
		for(int i=0;i<image.image.getWidth();i++) {
			//at each width within the hull do stuff
			double cx = i*image.scalar;
			if(cx >= min_x && cx < max_x) {
				ArrayList<Point3D> cline = new ArrayList<Point3D>(); // the vertical list of pixles to do edge detection
				for(int j=0;j<image.image.getHeight();j++) {
					double cy = image.real_height - j*image.scalar;
					if(cy >= min_y && cy < max_y) {
						Point3D npt = new Point3D(cx,cy,0.0);
						if(hsurf.ptInHulle(npt)) { //if this point is in the hull
							//get current pixel color
							int RGBA = image.image.getRGB(i,j);
			                int alpha = (RGBA >> 24) & 255;
			                int red = (RGBA >> 16) & 255;
			                int green = (RGBA >> 8) & 255;
			                int blue = RGBA & 255;
			                int gscal = (int) (0.1111*(Math.round(0.6*red + 0.4*green + 0.11*blue))); 
			                
							npt.z = (double) gscal; //store the gray-scale value in the Z value
							cline.add(npt);
						}
					}
				}
				// now detect a sharp gradient in this line
				if(cline.size() > 10) {
					//double[] grad = new double[cline.size()-1];
					double[] grad = new double[cline.size()];
					for(int j=0;j<cline.size();j++) {
						if(j==0)
							grad[j] = (cline.get(j+1).z - cline.get(j).z)/1.0;
						if(j==cline.size()-1)
							grad[j] = (cline.get(j).z - cline.get(j-1).z)/1.0;
						if(j!=0 && j!=cline.size()-1)
							grad[j] = (cline.get(j+1).z - cline.get(j-1).z)/2.0;
					}
					double grad_mean = LinearFunctions.average(grad);
					double grad_std = LinearFunctions.stdev(grad);
					for(int j=0;j<grad.length;j++) {
						if(Math.abs(grad[j]) > 2.75*grad_std+grad_mean) {
							//Point3D npt = new Point3D(cx,cy,0.0);
							cline.get(j).z = 0.0;
							envelope.add(cline.get(j));
						}
					}
				}
			}
		}		
		
		return envelope;
	}
	
	public static ArrayList<Point3D> extractImageEdgeRGB(LineHull hsurf, DynoImage image) {
		ArrayList<Point3D> envelope = new ArrayList<Point3D>();
		double min_x = hsurf.getMinX();
		double max_x = hsurf.getMaxX();
		double min_y = hsurf.getMinY();
		double max_y = hsurf.getMaxY();
		Graphics g = image.image.getGraphics();
		
		for(int i=0;i<image.image.getHeight();i++) {
			//at each height within the hull do stuff
			double cy = image.real_height - i*image.scalar;
			if(cy >= min_y && cy < max_y) {
				ArrayList<Point3D> cline = new ArrayList<Point3D>(); // the horzontal list of pixles to do edge detection
				ArrayList<Point3D> gline = new ArrayList<Point3D>(); // the horzontal list of pixles to do edge detection
				double cx = 0.0;
				for(int j=0;j<image.image.getWidth();j++) {
					cx = j*image.scalar;
					if(cx >= min_x && cx < max_x) {
						Point3D npt = new Point3D(cx,cy,0.0);
						Point3D gpt = new Point3D(cx,cy,0.0);
						if(hsurf.ptInHulle(npt)) { //if this point is in the hull
							//get current pixel color and store it in the new point
							int RGBA = image.image.getRGB(j, i);
			                int alpha = (RGBA >> 24) & 255;
			                int red = (RGBA >> 16) & 255;
			                int green = (RGBA >> 8) & 255;
			                int blue = RGBA & 255;
			                
			                npt.x = (double) red;
			                npt.y = (double) green;
							npt.z = (double) blue; //store the gray-scale value in the Z value
							cline.add(npt);
							gline.add(gpt);
							
							//envelope.add(npt); // COMMENT ME OUT WHEN DONE
						}
					}
				}
				// now detect a sharp gradient in this line
				if(cline.size() > 10) {
					double[] grad = new double[cline.size()];
					for(int j=0;j<cline.size();j++) {
						if(j==0)
							grad[j] = cline.get(j+1).distTo(cline.get(j));
						if(j==cline.size()-1)
							grad[j] = cline.get(j).distTo(cline.get(j-1));
						if(j!=0 && j!=cline.size()-1)
							grad[j] = (cline.get(j+1).distTo(cline.get(j-1)))/2.0;
					}
					double grad_mean = LinearFunctions.average(grad);
					double grad_std = LinearFunctions.stdev(grad);
					for(int j=0;j<grad.length;j++) {
						if(Math.abs(grad[j]) > 2.75*grad_std+grad_mean) {
							cline.get(j).x = cx;
							cline.get(j).y = cy;
							cline.get(j).z = 0.0;
							envelope.add(gline.get(j));
						}
					}
				}
			}
		}
		
		// vertical edge detect
		for(int i=0;i<image.image.getWidth();i++) {
			//at each width within the hull do stuff
			double cx = i*image.scalar;
			if(cx >= min_x && cx < max_x) {
				ArrayList<Point3D> cline = new ArrayList<Point3D>(); // the vertical list of pixles to do edge detection
				ArrayList<Point3D> gline = new ArrayList<Point3D>(); // the horzontal list of pixles to do edge detection
				double cy = 0.0;
				for(int j=0;j<image.image.getHeight();j++) {
					cy = image.real_height - j*image.scalar;
					if(cy >= min_y && cy < max_y) {
						Point3D npt = new Point3D(cx,cy,0.0);
						Point3D gpt = new Point3D(cx,cy,0.0);
						if(hsurf.ptInHulle(npt)) { //if this point is in the hull
							//get current pixel color
							int RGBA = image.image.getRGB(i,j);
			                int alpha = (RGBA >> 24) & 255;
			                int red = (RGBA >> 16) & 255;
			                int green = (RGBA >> 8) & 255;
			                int blue = RGBA & 255;

			                npt.x = (double) red;
			                npt.y = (double) green;
							npt.z = (double) blue; //store the gray-scale value in the Z value
							cline.add(npt);
							gline.add(gpt);
						}
					}
				}
				// now detect a sharp gradient in this line
				if(cline.size() > 10) {
					//double[] grad = new double[cline.size()-1];
					double[] grad = new double[cline.size()];
					for(int j=0;j<cline.size();j++) {
						if(j==0)
							grad[j] = cline.get(j+1).distTo(cline.get(j));
						if(j==cline.size()-1)
							grad[j] = cline.get(j).distTo(cline.get(j-1));
						if(j!=0 && j!=cline.size()-1)
							grad[j] = (cline.get(j+1).distTo(cline.get(j-1)))/2.0;
					}
					double grad_mean = LinearFunctions.average(grad);
					double grad_std = LinearFunctions.stdev(grad);
					for(int j=0;j<grad.length;j++) {
						if(Math.abs(grad[j]) > 2.75*grad_std+grad_mean) {
							cline.get(j).x = cx;
							cline.get(j).y = cy;
							cline.get(j).z = 0.0;
							envelope.add(gline.get(j));
						}
					}
				}
			}
		}
		
		
		return envelope;
	}
	public static BufferedImage extractImageRegion(LineHull hsurf, DynoImage image, JTextArea outwindow) {
		ArrayList<Point3D> envelope = new ArrayList<Point3D>();
		double min_x = hsurf.getMinX(); // these are all real coordinates in cm
		double max_x = hsurf.getMaxX();
		double min_y = hsurf.getMinY();
		double max_y = hsurf.getMaxY();
		double xrange = max_x-min_x;
		double yrange = max_y-min_y;
		min_x = min_x - 0.05*xrange;
		max_x = max_x + 0.05*xrange;
		xrange = max_x-min_x;
		min_y = min_y - 0.05*yrange;
		max_y = max_y + 0.05*yrange;
		yrange = max_y-min_y; // update the x and y ranges
		
		int xpxl = (int) Math.round(xrange/image.scalar);  // pixel sizes of the region
		int ypxl = (int) Math.round(yrange/image.scalar);
		
		// setup the graphics to interact with and the BufferedImage to return
		BufferedImage bi = new BufferedImage(xpxl,ypxl,BufferedImage.TYPE_INT_ARGB);
		outwindow.append("Extracting from image with size <" + String.valueOf(image.image.getWidth()) + "," + String.valueOf(image.image.getHeight()) + ">\n");
		outwindow.append("Created image with size <" + String.valueOf(bi.getWidth()) + "," + String.valueOf(bi.getHeight()) + ">\n");
		Graphics gi = bi.createGraphics();
		
		// iterate and make sure we are within the window - if so we paint it white
		// if we are within the hull, get the images RGB
		int py = 0; // y location within the output image
		for(int i=0;i<image.image.getHeight();i++) {
			double cy = image.real_height - i*image.scalar;
			if(cy >= min_y && cy < max_y) {				
				int px = 0; // x location within the output image
				for(int j=0;j<image.image.getWidth();j++) {
					double cx = j*image.scalar;
					if(cx >= min_x && cx < max_x) {
						Point3D npt = new Point3D(cx,cy,0.0);
						int alpha = 255;
		                int red =  255;
		                int green = 255;
		                int blue = 255;							
						if(hsurf.ptInHulle(npt)) { //if this point is in the hull
							int RGBA = image.image.getRGB(j,i);
			                alpha = (RGBA >> 24) & 255;
			                red = (RGBA >> 16) & 255;
			                green = (RGBA >> 8) & 255;
			                blue = RGBA & 255;
						}
						//outwindow.append("Extracting from pixel [" + String.valueOf(j) + "," + String.valueOf(i) + "]\n");
						//outwindow.append("Writing to pixel [" + String.valueOf(px) + "," + String.valueOf(py) + "]\n");
						//outwindow.append("----\n");
						gi.setColor(new Color(red,green,blue));
						gi.drawRect(px, py, 1, 1);
						px = px+1;
					}
				}
				py = py+1;
			}
		}
		for(int i=0;i<hsurf.curves.size();i++) {
			double cx = hsurf.curves.get(i).geompts[0].x;
			double cy = hsurf.curves.get(i).geompts[0].y;
			double cx2 = hsurf.curves.get(i).geompts[1].x;
			double cy2 = hsurf.curves.get(i).geompts[1].y;
			
			cx = cx - min_x; //shift the point to the image relative graphical space
			cy = cy - min_y;
			cx2 = cx2 - min_x; //shift the point to the image relative graphical space
			cy2 = cy2 - min_y;
			int pi = (int) Math.round(cx/image.scalar);
			int pj = (int) Math.round((cy - yrange)/(-image.scalar));
			int pi2 = (int) Math.round(cx2/image.scalar);
			int pj2 = (int) Math.round((cy2 - yrange)/(-image.scalar));
			//outwindow.append("-LINE-\n");
			//outwindow.append("Writing to pixel [" + String.valueOf(pi) + "," + String.valueOf(pj) + "]\n");			
			gi.setColor(Color.BLACK);
			gi.fillRect(pi, pj, 2, 2);
			gi.drawLine(pi, pj, pi2, pj2);
		}
		return bi;
	}	
	
}
