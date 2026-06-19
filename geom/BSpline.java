package geom;

import javax.swing.JTextArea;

public class BSpline {
	
	//getBasisFunction(double param) - this will calculate the basis function for a given parameter
	// This uses the Cox-de Boor recursion formula to calculate the basis function (N) of the polynomial order
	// It requires a triangular dependency:
	// N(K,order)
	// N(K, order-1), N(K+1,order-1)
	// N(K, order-2), N(K+1,order-2), N(K+2,order-2)
	// ......
	// N(K,0),...,N(K+order,0)
	public static double getBasisFunction(int K, double t, int corder, double[] knots, JTextArea outwindow) {
		int base_fcns = corder;
		double[] N = new double[base_fcns]; // for a given order, we need to calculate this number of zero order functions
		for(int i=0;i<N.length;i++) {
			N[i] = 0.0;
		}
		//outwindow.append("-----\n");
		//outwindow.append("Evaluating param " + Double.toString(t) + " at basis " + Integer.toString(K) + "\n");
		//outwindow.append("Polynomial order is " + Integer.toString(order) + "\n");		
		
		// Now calculate the basis function K of BSpline 'order'
		//   Given the triangular calculation detailed above, we need to
		//   perform the non-p=0 calculations one less time for each
		//   higher order
		for(int p=1;p<=corder;p++) { // i index is the basis count
			for(int j=0;j<(base_fcns-(p-1));j++) { //j index is the knot expansion
				int I = K+j;
				
				//outwindow.append(Double.toString(I) + "\n");
				if(p==1) { //do the zero basis function stuff
					//System.out.println("-----");
					//System.out.println("Param: " + Double.toString(t));
					//System.out.println("Zeroth: " + Double.toString(knots[I]) + "," + Double.toString(knots[I+1]));
					if(t >= knots[I] && t < knots[I+1])
						N[j] = 1.0;
					else
						N[j] = 0.0;
					if(t > 0.9999999 && knots[I+1] > 0.9999999)
						N[j] = 1.0;
					//System.out.println("N: " + Double.toString(N[j]));
				} else { //do the non-zero basis function stuff
					double ui = knots[I]; // knots at our basis function index
					double uipm1 = knots[I+p-1]; // knots at our basis function index+current order - 1
					double ui1 = knots[I+1]; // knots at our basis function index+1
					double uip = knots[I+p]; // knots at our basis function index+current order
					//double uip1 = knots[I+p+1]; // knots at our basis function index+current order + 1
					
					String output = "ui: " + Double.toString(ui) + "\n";
					output = output + "uipm1: " + Double.toString(uipm1) + "\n";
					output = output + "ui1: " + Double.toString(ui1) + "\n";
					output = output + "uip: " + Double.toString(uip) + "\n";
					//System.out.println(output);
					//outwindow.append(output);
					
					double A = 0.0;
					double B = 0.0;
					
					if(N[j] != 0.0 && (uipm1-ui) != 0.0)
						A = (t-ui)/(uipm1-ui);
					if(N[j+1] != 0.0 && (uip-ui1) != 0.0)
						B = (uip - t)/(uip-ui1);
					N[j] = A*N[j] + B*N[j+1];
				}
			}
			//outwindow.append("Basis functions at order " + Integer.toString(p) + ": ");
			//for(int i=0;i<N.length;i++) {
			//	outwindow.append(Double.toString(N[i]) + ", ");
			//}
			//outwindow.append("\n");
		}
		
		return N[0];
	}
	
	//getBasisDerivative(int k, double param, JTextArea) - this will calculate the basis function derivative
	public static double getBasisFirstDerivative(int K, double t, int order, double[] knots, JTextArea outwindow) {
		double dndu = 0.0;

		double Nipm1 = BSpline.getBasisFunction(K,t,order-1,knots,outwindow);
		double Ni1pm1 = BSpline.getBasisFunction(K+1,t,order-1,knots,outwindow);
		double ui = knots[K]; // knots at our basis function index
		double uipm1 = knots[K+order-1];
		double ui1 = knots[K+1]; // knots at our basis function index+1
		double uip = knots[K+order]; // knots at our basis function index+current order
		double A = 0.0;
		double B = 0.0;
		if((uipm1-ui) != 0.0)
			A = order/(uipm1-ui);
		if((uip-ui1) != 0.0)
			B = order/(uip-ui1);
		dndu = A*Nipm1 - B*Ni1pm1;

		return dndu;
	}
	//getBasisDerivative(int k, double param, JTextArea) - this will calculate the basis function derivative
	public static double getBasisNDerivative(int K, double t, int order, int n, double[] knots, JTextArea outwindow) {
		if(n <= 0 || n > order) {
			n = 1;
			outwindow.append("WARNING: input derivative for spline is greater than order or <=0 derivative set to 1\n");
		}	

		//initialize array based on the first derivative
		double[] dndu = new double[n];
		for(int i=0;i<dndu.length;i++) {
			dndu[i] = BSpline.getBasisFirstDerivative(K,t,order,knots,outwindow);
		}

		//iterate for higher derivatives
		for(int i=2;i<=n;i++) {
			for(int D=0;D<(dndu.length-(i-1));D++) {
				double ui = knots[K]; // knots at our basis function index
				double uipm1 = knots[K+order-1];
				double ui1 = knots[K+1]; // knots at our basis function index+1
				double uip = knots[K+order]; // knots at our basis function index+current order
				double A = 0.0;
				double B = 0.0;
				if((uipm1-ui) != 0.0)
					A = order/(uipm1-ui);
				if((uip-ui1) != 0.0)
					B = order/(uip-ui1);
				dndu[D] = A*dndu[D] - B*dndu[D+1];
			}
		}
		return dndu[0];
	}

}
