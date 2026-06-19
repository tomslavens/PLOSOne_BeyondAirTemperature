package images;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.JPanel;
import geom.Point3D;
import gui.ImportImageDialog;
import java.awt.event.MouseMotionAdapter;

public class ImagePreviewer extends JPanel {
	
	private DynoImage dyno;
	private boolean addPts;
	private ArrayList<Point3D> ptbuffer;
	private ImportImageDialog sid;

	private int ix; //location of start of image drawing
	private int iy; // location of start of image drawing
	private float zoom; // zoom into the image
	private int mx; //location of mouse when pressed
	private int my; // location of mouse when pressed
	
	public ImagePreviewer(ImportImageDialog in_sid, DynoImage in_dyno) {
		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				leftMouseDragged(e);
			}
		});
		dyno = in_dyno;
		addPts = false;
		ptbuffer = new ArrayList<Point3D>();
		sid = in_sid;
		ix = 0;
		iy = 0;
		zoom = 1.0f;
		mx = 0;
		my = 0;
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getButton() == MouseEvent.BUTTON1) {
					leftMouseClicked(e);
				}
				if(e.getButton() == MouseEvent.BUTTON3) {
					rightMouseClicked(e);
				}
			}
			@Override
			public void mousePressed(MouseEvent e) {
				mx = e.getX();
				my = e.getY();
			}
		});		
	}
	public void scaleImage() {
		addPts = true;
		ptbuffer.clear();
	}
	// load an image
	public void setImage(BufferedImage img) {
		dyno.image = img;

		int pxl_width = dyno.image.getWidth();   //true pixel width of image
		int pxl_height = dyno.image.getHeight(); //true pixel height of image
		int wndw_width = this.getWidth();
		int wndw_height = this.getHeight();
		
		int width_diff = Math.abs(pxl_width-wndw_width);
		int height_diff = Math.abs(pxl_height-wndw_height);

		// will set zoom so the image fits the best dimension
		if(width_diff < height_diff) {
			zoom = (float) wndw_width/pxl_width;
		} else {
			zoom = (float) wndw_height/pxl_height;
		}		
		
		this.invalidate();
		this.repaint();
	}
	private void leftMouseClicked(MouseEvent e) {
		int cx = e.getX();
		int cy = e.getY();
		if(addPts) {
			Point3D pt = new Point3D(cx,cy,0.0);
			ptbuffer.add(pt);
			this.invalidate();
			this.repaint();
		}
	}
	private void rightMouseClicked(MouseEvent e) {
		if(addPts) {
			/*//figure out the currently scaled width and height of the image
			int pxl_width = dyno.image.getWidth();
			int pxl_height = dyno.image.getHeight();
			int wndw_width = this.getWidth();
			int wndw_height = this.getHeight();	
			int width_diff = Math.abs(pxl_width-wndw_width);
			int height_diff = Math.abs(pxl_height-wndw_height);			
			int rend_width = 0;
			int rend_height = 0;			
			if(width_diff > height_diff) {
				double scalar = (double) wndw_width/pxl_width;
				rend_width = wndw_width;
				rend_height = (int) Math.round(scalar*pxl_height);
			} else {
				double scalar = (double) wndw_height/pxl_height;
				rend_width = (int) Math.round(scalar*wndw_width);
				rend_height = wndw_height;
			}			
			//now do the normal stuff
			addPts = false;
			//double pxl_dist = ptbuffer.get(0).distTo(ptbuffer.get(1));    // just the width of this pixel distance 
			double pxl_xdist = Math.abs(ptbuffer.get(0).x- ptbuffer.get(1).x);
			double pxl_ydist = Math.abs(ptbuffer.get(0).y- ptbuffer.get(1).y);
			double theta = Math.atan2(pxl_ydist, pxl_xdist); //interior angle between my x and y
						
			double real_dist = Double.valueOf(sid.length_field.getText()); // should be distance in real space (cm)
			double real_xdist = real_dist*Math.cos(theta);
			double real_ydist = real_dist*Math.sin(theta);
			double xscalar = real_xdist/pxl_xdist;
			double yscalar = real_ydist/pxl_ydist;*/

			double real_dist = Double.valueOf(sid.length_field.getText()); // should be distance in real space (cm)
			double pxl_dist = ptbuffer.get(0).distTo(ptbuffer.get(1)); //pixel distance in this image
			double img_pxl_dist = pxl_dist/zoom;
			double scalar = real_dist/img_pxl_dist;
			
			dyno.morphRealSize(scalar);
			//sid.outwindow.append("Real distanct " + Double.valueOf(real_dist)+ "\n");
			//sid.outwindow.append("Window pxl_dist " + Double.valueOf(pxl_dist)+ "\n");
			//sid.outwindow.append("Image pxl_dist " + Double.valueOf(img_pxl_dist)+ "\n");
			sid.outwindow.append("Photo width set to " + Double.valueOf(dyno.real_width)+ "\n");
			ptbuffer.clear();
			this.invalidate();
			this.repaint();
		}
	}
	private void leftMouseDragged(MouseEvent e) {
		int fme_width = this.getWidth();
		int fme_height = this.getHeight();
		int cx = e.getX();
		int cy = e.getY();

		int dx = cx-mx;
		int dy = cy-my;
		double dpz = 0.0;

		//first list check to see if it is screen movement
		if(e.isShiftDown()) {
			mx = cx;
			my = cy;
			ix = ix + dx;
			iy = iy + dy;
			this.invalidate();
			this.repaint();
		} else if(e.isControlDown()) {
			mx = cx;
			my = cy;
			zoom = (float) (zoom + (1.0*dy/fme_height));
			this.invalidate();
			this.repaint();
		} 
	}	
	
	@Override
    protected void paintComponent(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		//draw an image if we have one
		if(dyno.image != null) {
			int pxl_width = dyno.image.getWidth();
			int pxl_height = dyno.image.getHeight();
			//int wndw_width = this.getWidth();
			//int wndw_height = this.getHeight();
			
			//int width_diff = Math.abs(pxl_width-wndw_width);
			//int height_diff = Math.abs(pxl_height-wndw_height);
			
			int rend_width = (int) Math.round(pxl_width*zoom);
			int rend_height = (int) Math.round(pxl_height*zoom);
			g.drawImage(dyno.image,ix,iy,rend_width,rend_height,null);
			
			/*if(width_diff > height_diff) {
				double scalar = (double) wndw_width/pxl_width;
				int rend_width = wndw_width;
				int rend_height = (int) Math.round(scalar*pxl_height*zoom);
				g.drawImage(dyno.image,ix,iy,rend_width,rend_height,null);
			} else {
				double scalar = (double) wndw_height/pxl_height;
				int rend_width = (int) Math.round(scalar*wndw_width*zoom);
				int rend_height = wndw_height;
				g.drawImage(dyno.image,ix,iy,rend_width,rend_height,null);
			}*/
		}		
		
		//draw the scaling points we make
		g.setColor(Color.RED);
		for(int i=0;i<ptbuffer.size();i++) {
			int cx = (int) ptbuffer.get(i).x;
			int cy = (int) ptbuffer.get(i).y;
			g.fillRect(cx-2,cy-2, 4, 4);
		}
	}
}
