package geom;

import javax.swing.JTextArea;

/* NURB - holds the basis function for Non-uniform rational BSpline
 *    Leverages the BSpline basis along with the given weight to generate the NURB basis
 */
public class NURB {
	
	// NURB basis is defined as: R = weight*BsplineBasis/sum(weight*BsplineBasis)
	public static double getBasisFunction(int K, double t, int corder, double[] knots, double[] weights, JTextArea outwindow) {
		double wi = weights[K];
		double Bi = BSpline.getBasisFunction(K, t, corder, knots, outwindow);
		
		double summation = 0.0;
		for(int j=0;j<weights.length;j++) {
			double wj = weights[j];
			double Bj = BSpline.getBasisFunction(j, t, corder, knots, outwindow);
			summation = summation+(wj*Bj);
		}
		double R = (wi*Bi)/summation;
		return R;
	}
	
	//getBasisDerivative(int k, double param, JTextArea) - this will calculate the basis function First derivative
	//
	// dn/du = (hi*N'i)/sum(hi*Ni) - (hi*Ni)*sum(hi*Ni')/(sum(hi*Ni))^2
	public static double getBasisFirstDerivative(int K, double t, int order, double[] knots, double[] weights, JTextArea outwindow) {
		double dndu = 0.0;
		
		double hi = weights[K];
		double Ni = BSpline.getBasisFunction(K, t, order, knots, outwindow);
		double Nip = BSpline.getBasisFirstDerivative(K, t, order, knots, outwindow);
		
		//sum up hiNi
		double hiNi = 0.0;
		for(int i=0;i<weights.length;i++) {
			hiNi = hiNi + weights[i]*BSpline.getBasisFunction(K, t, order, knots, outwindow);
		}
		
		//sum up hiNi'
		double hiNip = 0.0;
		for(int i=0;i<weights.length;i++) {
			hiNip = hiNip + weights[i]*BSpline.getBasisFirstDerivative(i, t, order, knots, outwindow);
		}
		
		double A = 0.0;
		double B = 0.0;
		
		if(hiNi != 0.0) {
			A = (hi*Nip)/(hiNi);
			B = ((hi*Ni)*hiNip)/Math.pow(hiNi, 2.0);
		}
		dndu = A-B;

		return dndu;
	}

}
