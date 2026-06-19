package images;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JTextArea;

public class DynoImage {
	public BufferedImage image; //data of the image
	public float real_width; //going to use this to keep a size of the image
	public float real_height; //going to use this to keep a size of the image
	public double scalar;    //how much this image is scaled to fit into the renderer

	public DynoImage() {
		image = null;
		real_width = 10.0f;
		real_height = 10.0f;
		scalar = 1.0;
	}
	public DynoImage(String fname, JTextArea outwindow) {
		image = null;
		try {
			image = ImageIO.read(new File(fname));
			outwindow.append("Read in " + fname +"\n");
		} catch(Exception E) {
			outwindow.append("Image file read unsuccessful\n");
			outwindow.append(E.toString() + "\n");
		}
		float hw_ratio = (float) image.getHeight()/image.getWidth();
		real_width = 10.0f;
		real_height = real_width*hw_ratio;
		scalar = 10.0/image.getWidth();
	}
	public void morphRealSize(double in_scalar) {
		real_width = (float) in_scalar*image.getWidth();
		real_height = (float) in_scalar*image.getHeight();
		scalar = in_scalar;
	}
}
