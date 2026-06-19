package rendering;

import java.awt.Color;

public class RendererOptionBank {

	public int cont_option;
	public static int CONT_CONTINUOUS_REDBLUE = 1; //use the red-to-blue coloring
	public static int CONT_DESCRETE_FIREFIGHT = 2; //use the discrete color array
	public static int CONT_DESCRETE_FALLFORWARD = 3; //use the discrete color array
	
	public static Color SELECTED_COLOR = new Color(80,199,37);
	public static Color CONSTRAINT_COLOR = new Color(255,137,111);
	public static Color BC_COLOR1 = new Color(236,126,221);
	public static Color BC_COLOR2 = new Color(0,255,196);
	public static Color MESH_COLOR = Color.getHSBColor(0.180f, 0.41f, 0.85f);
	public static Color[] FIREFIGHT = new Color[] {new Color(255,247,236), new Color(254,232,200),new Color(253,212,158), 
			new Color(253,187,132),new Color(252,141,89),new Color(239,101,72),new Color(215,48,31), new Color(179,0,0), new Color(127,0,0)};
	public static Color[] FALLBACKWARD = new Color[] {new Color(99,22,22), new Color(165,52,30),new Color(197,91,52), 
			new Color(227,126,79),new Color(247,151,38),new Color(214,163,7),new Color(183,146,33), new Color(141,131,40), new Color(100,108,24)};
	public static Color[] FALLFORWARD = new Color[] {new Color(100,108,24),new Color(141,131,40),new Color(183,146,33),new Color(214,163,7),
			new Color(247,151,38),new Color(227,126,79),new Color(197,91,52),new Color(165,52,30),new Color(99,22,22)};
	
	public RendererOptionBank() {
		cont_option = 1;
	}
	public Color getContorColor(double value, double min, double max) {
		Color output = new Color(0,0,0);
		if(cont_option == CONT_CONTINUOUS_REDBLUE) {
			double loc1 = (value-min)/(max-min);
			int red_val = (int) (Math.floor(255*loc1));
			int blue_val = 0;
			if(loc1 > 0.00001)
				blue_val = (int) (Math.floor(255*(1.0-loc1)));
			output = new Color(red_val,0,blue_val);
		}
		if(cont_option == CONT_DESCRETE_FIREFIGHT) {
			double dx = (double) (max-min)/(FIREFIGHT.length);
			for(int i=0;i<FIREFIGHT.length;i++) {
				if(value >= min+(i*dx) && value < max+((i+1)*dx))
					output = FIREFIGHT[i];
			}
		}
		if(cont_option == CONT_DESCRETE_FALLFORWARD) {
			double dx = (double) (max-min)/(FALLFORWARD.length);
			for(int i=0;i<FALLFORWARD.length;i++) {
				if(value >= min+(i*dx) && value < max+((i+1)*dx))
					output = FALLFORWARD[i];
			}
		}
		return output;
	}
}
