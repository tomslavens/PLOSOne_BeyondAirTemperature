package physics;

/*=====
 * RadiationFunctions - this contains data for calculations of black body radiation functions from Cengel's "Heat Transfer, A Practical Approach"
 */

public class BlackBodyRadiationFunctions {

	private static double[] LAMBDAT = new double[] {200,400,600,800,1000,1200,1400,1600,1800,2000,2200,2400,2600,2800,3000,3200,3400,3600,3800,4000,4200,
			4400,4600,4800,5000,5200,5400,5600,5800,6000,6200,6400,6600,6800,7000,7200,7400,7600,7800,8000,8500,9000,9500,10000,
			10500,11000,11500,12000,13000,14000,15000,16000,18000,20000,25000,30000,40000,50000,75000,100000};
	private static double[] FLAMBDA = new double[] {0,0,0,0.000016,0.000321,0.002134,0.00779,0.019718,0.039341,0.066728,0.100888,0.140256,0.18312,0.227897,
			0.273232,0.318102,0.361735,0.403607,0.443382,0.480877,0.516014,0.548796,0.57928,0.607559,0.633747,0.65897,0.68036,0.701046,0.720158,0.737818,
			0.75414,0.769234,0.783199,0.796129,0.808109,0.819217,0.829527,0.839102,0.848005,0.856288,0.874608,0.890029,0.903085,0.914199,0.92371,0.93189,
			0.939959,0.945098,0.955139,0.962898,0.969981,0.973814,0.98086,0.985602,0.992215,0.99534,0.997967,0.998953,0.999713,0.999905};

	// returns the factor based on linear interpolation
	public static double getFunction(double temp, double wavelength) {
		double lamT = wavelength*temp;
		double flamb = 0.0; 
		for(int i=0;i<LAMBDAT.length-1;i++) {
			double clamt0 = LAMBDAT[i];
			double cflam0 = FLAMBDA[i];
			double clamt1 = LAMBDAT[i+1];
			double cflam1 = FLAMBDA[i+1];
			
			// if our value is between two other LAMBDAT values
			if(lamT >= clamt0 && lamT < clamt1) {
				flamb = cflam1 - ((cflam1-cflam0)/(clamt1-clamt0))*(clamt1-clamt0);
			}
		}
		return flamb;
	}
}
