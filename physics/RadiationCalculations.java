package physics;

import geom.Line3D;
import geom.Point3D;
import linear_math.LinearFunctions;
import linear_math.Vector3D;

/*============
 * RadiationCalculations - this class contains static functions to calculate the heat flux between bodies 
 *    due to thermal radiation
 */

public class RadiationCalculations {
	public static double STEFANBOLTZMAN = 0.0000000567;  // W/(m^2*K^4) 
	
	

	// calcTotalBlackBodyRadiation(temp) - returns the total energy emitted by a black body
	public static double calcTotalBlackBodyRadiation(double temp) {
		double qdot = STEFANBOLTZMAN*Math.pow(temp,4.0); // W/m^2
		return qdot;
	}
	//calcSpectralBlackBodyRadiation(temp,wavelength) - returns the total energy emitted by a black body at this Wavelength
	public static double calcSpectralBlackBodyRadiation(double temp,double wavelength) {
		double c1 = 3.742e8; // W*um^4/m^2
		double c2 = 1.439e4; // um*K
		double lamT = wavelength*temp;
		double qdot = c1/(Math.pow(wavelength, 5.0)*(Math.exp(c2/lamT)-1));
		return qdot;
	}
	//calcIntegratedSpectralBlackBodyRadiation(temp,wavelength) - returns the total energy emitted by a black body up to this Wavelength
	public static double calcIntegratedSpectralBlackBodyRadiation(double temp,double wavelength) {
		double radfunction = BlackBodyRadiationFunctions.getFunction(temp, wavelength);
		double qdot = radfunction*STEFANBOLTZMAN*Math.pow(temp,4.0);
		return qdot;
	}
	//calcIntegratedSpectralBlackBodyRadiation(temp,wavelength1,wavelength2) - returns the total energy emitted by a black body between these wavelengths
	public static double calcIntegratedSpectralBlackBodyRadiation(double temp,double wavelength1, double wavelength2) {
		double radfunction1 = BlackBodyRadiationFunctions.getFunction(temp, wavelength1);
		double radfunction2 = BlackBodyRadiationFunctions.getFunction(temp, wavelength2);
		double raddiff = Math.abs(radfunction2-radfunction1);
		double qdot = raddiff*STEFANBOLTZMAN*Math.pow(temp,4.0);
		return qdot;
	}
	//calculateTotalBlackBodyRadiationIntensity(temp) - returns the radiation emitted per solid angle of the black body
	public static double calcTotalBlackBodyRadiationIntensity(double temp) {
		double qdot = calcTotalBlackBodyRadiation(temp)/Math.PI; // W/m^2
		return qdot;
	}
	//calcSpectralBlackBodyRadiationIntensity(temp,wavelength) - returns the total energy emitted by a black body at this Wavelength
	public static double calcSpectralBlackBodyRadiationIntensity(double temp,double wavelength) {
		double qdot = calcIntegratedSpectralBlackBodyRadiation(temp,wavelength);
		qdot = qdot/Math.PI;
		return qdot;
	}
	//calcIntegratedSpectralBlackBodyRadiation(temp,wavelength) - returns the total intensity emitted by a black body up to this Wavelength
	public static double calcIntegratedSpectralBlackBodyRadiationIntensity(double temp,double wavelength) {
		double qdot = calcIntegratedSpectralBlackBodyRadiation(temp,wavelength);
		qdot = qdot/Math.PI;
		return qdot;
	}
	//calcIntegratedSpectralBlackBodyRadiationIntensity(temp,wavelength) - returns the intensity emitted by a black body up to this Wavelength
	public static double calcIntegratedSpectralBlackBodyRadiationIntensity(double temp,double wavelength1, double wavelength2) {
		double qdot = calcIntegratedSpectralBlackBodyRadiation(temp,wavelength1,wavelength2);
		qdot = qdot/Math.PI;
		return qdot;
	}
	//calcRadiationLoad - this will calculate the heat projected from a black-body source to a surface element
	public static double calcBlackBodySurfaceRadiationLoad(Point3D targ, double targ_area, Vector3D targ_norm, double targ_distance, double targ_temp, double targ_absorbtivity, Point3D src, double src_area, Vector3D src_norm, double src_temp) {
		//first calculate the vector between the center point of the two surfaces
		Vector3D proj_vect = new Vector3D(targ,src);
		//calculate the angles bewtween the surface projections and the surface normals
		double targ_angle = LinearFunctions.calcVectorAngle(targ_norm, proj_vect);
		double src_angle = LinearFunctions.calcVectorAngle(src_norm, proj_vect);
		//calculate the solid angle subtended by the target area when viewed from the source area
		double wt = (targ_area*Math.cos(targ_angle))/Math.pow(targ_distance, 2.0);  // in steradians
		//calculate the radiation intensity of the source surface
		double src_intensity = calcTotalBlackBodyRadiationIntensity(src_temp);
		//calculate the radiation transmitted from the source surface to the target surface
		double Qdot = src_intensity*(src_area*Math.cos(src_angle))*wt;
		return Qdot;
	}
	//calcRadiationFluxLoad - this will calculate the heat flux from a black-body source to a surface element
	public static double calcBlackBodySurfaceRadiationFluxLoad(Point3D targ,Vector3D targ_norm,double targ_absorbtivity, Point3D src, double src_area, Vector3D src_norm, double src_temp) {
		double qdot = 0.0;
		double targ_distance = targ.distTo(src)/100; //convert cm to m
		//First determine if both faces are looking at each other by 
		// calculate the vector from the target loc to src loc and vice versa and
		// determine the projection angle between them
		Vector3D targ2src = new Vector3D(targ,src);
		targ2src.magnitude = 1.0; //make this a unit vector
		double targ_angle = LinearFunctions.calcVectorAngle(targ_norm, targ2src);
		Vector3D src2targ = new Vector3D(src,targ);
		src2targ.magnitude = 1.0; //make this a unit vector
		double src_angle = LinearFunctions.calcVectorAngle(src_norm, src2targ);

		if(targ_angle < 0.5*Math.PI && src_angle < 0.5*Math.PI) {
			//calculate the heat flux to the surface
			qdot = STEFANBOLTZMAN*Math.pow(src_temp, 4.0)*((Math.cos(targ_angle)*Math.cos(src_angle))/(Math.PI*Math.pow(targ_distance,2.0)))*src_area;
			qdot = Math.abs(qdot);
		}
		//debugging report:
		String output = "-------\nBBRadiationCalculation\n";
		output = output + "Src Location: " + src.toString() + "\n";
		output = output + "Src Vector: " + src_norm.toString() + "\n";
		output = output + "Src Area: " + Double.toString(src_area) + "\n";
		output = output + "Src Temp: " + Double.toString(src_temp) + "\n";
		output = output + "Targ Location: " + targ.toString() + "\n";
		output = output + "Targ Vector: " + targ_norm.toString() + "\n";
		output = output + "Targ Distance: " + Double.toString(targ_distance) + "\n";
		output = output + "Targ2Src Vector: " + targ2src.toString() + "\n";
		output = output + "Src2Targ Vector: " + src2targ.toString() + "\n";
		output = output + "Src Angle: " + Double.toString(src_angle) + "\n";
		output = output + "Targ Angle: " + Double.toString(targ_angle) + "\n";
		output = output + "qdot: " + Double.toString(qdot) + "\n";
		System.out.println(output);
		return qdot;
	}
}
