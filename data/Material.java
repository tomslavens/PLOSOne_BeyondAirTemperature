package data;

public class Material extends Entity {
	public String matname; // name of the material
	public double density; // g/cm^3
	public double[][] thermal_conductivity; // [C}[W/mC]
	public double[][] specific_heat; // [C][J/kg*C]
	public double[][] coefficient_thermal_expansion; //[C][m/m*C]
	public double[][] elastic_modulus; // [C][GPa]
	//inhereted from "Entity"
	//public int type;     // 0=Point3D, 1=BSplineCurve, 2=BSplineSurface, 3=NURBCurve, 4=Line3D, 5=constraint
	//public String name;
	//public int tag;
	public static String[] PROPNAMES = new String[] {"Density","Thermal Conductivity","Specific Heat","Thermal Expansion Coefficient","Elastic Modulus"};

	// empty constructor
	public Material() {
		type = Entity.MATERIAL;
		name = "MaterialModel";
		tag = 0;
		matname = "Null";
		density = 7.85;
		thermal_conductivity = new double[2][2];
		thermal_conductivity[0][0] = 25.0;
		thermal_conductivity[0][1] = 0.1;
		thermal_conductivity[1][0] = 2500;
		thermal_conductivity[1][1] = 0.1;
		specific_heat = new double[2][2];
		specific_heat[0][0] = 25.0;
		specific_heat[0][1] = 0.1;
		specific_heat[1][0] = 2500;
		specific_heat[1][1] = 0.1;
		coefficient_thermal_expansion = new double[2][2];
		coefficient_thermal_expansion[0][0] = 100.0;
		coefficient_thermal_expansion[0][1] = .000001;
		coefficient_thermal_expansion[1][0] = 2500;
		coefficient_thermal_expansion[1][1] = .000001;
		elastic_modulus = new double[2][2];
		elastic_modulus[0][0] = 25.0;
		elastic_modulus[0][1] = 1.0;
		elastic_modulus[1][0] = 2500;
		elastic_modulus[1][1] = 1.0;
	}
	public Material(String filestring) {
		String[] lines = filestring.split("\n");
		
		String[] header = lines[0].split(":")[0].split(",");
		type = Integer.valueOf(header[0]);
		tag = Integer.valueOf(header[1]);
		name = header[2];
		String[] info = lines[0].split(":")[1].split(",");
		matname = info[0];
		density = Double.valueOf(info[1]);
		
		//parse the conductivity line
		String[] data = lines[1].split(";");
		thermal_conductivity = new double[data.length][2];
		for(int i=0;i<data.length;i++) {
			thermal_conductivity[i][0] = Double.valueOf(data[i].split(",")[0]);
			thermal_conductivity[i][1] = Double.valueOf(data[i].split(",")[1]);
		}
		//parse the specific heat line
		data = lines[2].split(";");
		specific_heat = new double[data.length][2];
		for(int i=0;i<data.length;i++) {
			specific_heat[i][0] = Double.valueOf(data[i].split(",")[0]);
			specific_heat[i][1] = Double.valueOf(data[i].split(",")[1]);
		}
		//parse the coefficient_thermal_expansion line
		data = lines[3].split(";");
		coefficient_thermal_expansion = new double[data.length][2];
		for(int i=0;i<data.length;i++) {
			coefficient_thermal_expansion[i][0] = Double.valueOf(data[i].split(",")[0]);
			coefficient_thermal_expansion[i][1] = Double.valueOf(data[i].split(",")[1]);
		}
		//parse the elasticity line
		data = lines[4].split(";");
		elastic_modulus = new double[data.length][2];
		for(int i=0;i<data.length;i++) {
			elastic_modulus[i][0] = Double.valueOf(data[i].split(",")[0]);
			elastic_modulus[i][1] = Double.valueOf(data[i].split(",")[1]);
		}
	}
	
	public double getConductivity(double temp) {
		double value = thermal_conductivity[0][1];
		for(int i=0;i<thermal_conductivity.length-1;i++) {			
			if(temp >= thermal_conductivity[i][0] && temp < thermal_conductivity[i+1][0]) {
				double Th = thermal_conductivity[i+1][0];
				double Tl = thermal_conductivity[i][0];
				double kh = thermal_conductivity[i+1][1];
				double kl = thermal_conductivity[i][1];				
				value = kh - ((Th-temp)/(Th-Tl))*(kh-kl);
			}
		}
		return value;
	}
	public double getSpecificHeat(double temp) {
		double value = specific_heat[0][1];
		for(int i=0;i<specific_heat.length-1;i++) {			
			if(temp >= specific_heat[i][0] && temp < specific_heat[i+1][0]) {
				double Th = specific_heat[i+1][0];
				double Tl = specific_heat[i][0];
				double kh = specific_heat[i+1][1];
				double kl = specific_heat[i][1];				
				value = kh - ((Th-temp)/(Th-Tl))*(kh-kl);
			}
		}
		return value;
	}
	public double getCoefficientExpansion(double temp) {
		double value = coefficient_thermal_expansion[0][1];
		for(int i=0;i<coefficient_thermal_expansion.length-1;i++) {			
			if(temp >= coefficient_thermal_expansion[i][0] && temp < coefficient_thermal_expansion[i+1][0]) {
				double Th = coefficient_thermal_expansion[i+1][0];
				double Tl = coefficient_thermal_expansion[i][0];
				double kh = coefficient_thermal_expansion[i+1][1];
				double kl = coefficient_thermal_expansion[i][1];				
				value = kh - ((Th-temp)/(Th-Tl))*(kh-kl);
			}
		}
		return value;
	}
	public double getElasticity(double temp) {
		double value = elastic_modulus[0][1];
		for(int i=0;i<elastic_modulus.length-1;i++) {			
			if(temp >= elastic_modulus[i][0] && temp < elastic_modulus[i+1][0]) {
				double Th = elastic_modulus[i+1][0];
				double Tl = elastic_modulus[i][0];
				double kh = elastic_modulus[i+1][1];
				double kl = elastic_modulus[i][1];				
				value = kh - ((Th-temp)/(Th-Tl))*(kh-kl);
			}
		}
		return value;
	}

	@Override
	public String getFileString() {
		String output = Integer.toString(type) + "," + Integer.toString(tag) + "," + name + ":";
		output = output + matname + "," + Double.toString(density) + "\n";
		
		// add our thermal conductivity info
		String pair = Double.toString(thermal_conductivity[0][0]) + "," + Double.toString(thermal_conductivity[0][1]);
		output = output + pair;
		for(int i=1;i<thermal_conductivity.length;i++) {
			pair = Double.toString(thermal_conductivity[i][0]) + "," + Double.toString(thermal_conductivity[i][1]);
			output = output + ";" + pair;
		}
		
		// add our thermal specific heat info
		pair = Double.toString(specific_heat[0][0]) + "," + Double.toString(specific_heat[0][1]);
		output = output + "\n" + pair;
		for(int i=1;i<specific_heat.length;i++) {
			pair = Double.toString(specific_heat[i][0]) + "," + Double.toString(specific_heat[i][1]);
			output = output + ";" + pair;
		}
		
		// add our thermal expansion info
		pair = Double.toString(coefficient_thermal_expansion[0][0]) + "," + Double.toString(coefficient_thermal_expansion[0][1]);
		output = output + "\n" + pair;
		for(int i=1;i<coefficient_thermal_expansion.length;i++) {
			pair = Double.toString(coefficient_thermal_expansion[i][0]) + "," + Double.toString(coefficient_thermal_expansion[i][1]);
			output = output + ";" + pair;
		}
		
		// add our elastic modulus info
		pair = Double.toString(elastic_modulus[0][0]) + "," + Double.toString(elastic_modulus[0][1]);
		output = output + "\n" + pair;
		for(int i=1;i<elastic_modulus.length;i++) {
			pair = Double.toString(elastic_modulus[i][0]) + "," + Double.toString(elastic_modulus[i][1]);
			output = output + ";" + pair;
		}
		
		return output;
	}
	
}
