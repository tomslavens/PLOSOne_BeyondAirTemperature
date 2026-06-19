package analysis;

import javax.swing.JTextArea;

import hiperfcomp.CPUMatrixSolver;
import hiperfcomp.HiPerfInterface;
import linear_math.Matrix;

public class SteadyHeatTransfer2DFEA {

	// SolveSteadyStateTriMesh - this function will take a ThermalTriMesh and solve 
	public static void SolveSteadyStateTriMesh(ThermalTriMesh mesh, JTextArea outwindow) {
		//System.out.println("Mesh has " + Integer.toString(mesh.nodes.size()) + " nodes");
		//System.out.println("Mesh has " + Integer.toString(mesh.elems.size()) + " elements");
		outwindow.append("Mesh has " + Integer.toString(mesh.nodes.size()) + " nodes\n");
		outwindow.append("Mesh has " + Integer.toString(mesh.elems.size()) + " elements\n");		
		Matrix K = new Matrix(mesh.nodes.size(), mesh.nodes.size()); //create our empty stiffness matrix
		Matrix F = new Matrix(mesh.nodes.size(),1); //create our empty force vector

		//now setup the stiffness matrix and force vector
		for (int i = 0; i < mesh.elems.size(); i++) {
			double x1 = mesh.nodeAt(mesh.elemAt(i).pt1).x/100; //converting from cm to m
			double y1 = mesh.nodeAt(mesh.elemAt(i).pt1).y/100; //converting from cm to m
			double x2 = mesh.nodeAt(mesh.elemAt(i).pt2).x/100; //converting from cm to m
			double y2 = mesh.nodeAt(mesh.elemAt(i).pt2).y/100; //converting from cm to m
			double x3 = mesh.nodeAt(mesh.elemAt(i).pt3).x/100; //converting from cm to m
			double y3 = mesh.nodeAt(mesh.elemAt(i).pt3).y/100; //converting from cm to m

			double a1 = x2 * y3 - x3 * y2;
			double a2 = x3 * y1 - x1 * y3;
			double a3 = x1 * y2 - x2 * y1;
			double b1 = y2 - y3;
			double b2 = y3 - y1;
			double b3 = y1 - y2;
			double c1 = x3 - x2;
			double c2 = x1 - x3;
			double c3 = x2 - x1;

			double earea2 = Math.abs(0.5 * ((x1 * y2 - x2 * y1) + (x3 * y1 - x1 * y3) + (x2 * y3 - x3 * y2)));
			double k1 = mesh.nodeAt(mesh.elemAt(i).pt1).values[1];
			double k2 = mesh.nodeAt(mesh.elemAt(i).pt2).values[1];
			double k3 = mesh.nodeAt(mesh.elemAt(i).pt3).values[1];
			double k = (k1+k2+k3)/3.0; //take the mean of the 3 nodes as the overall conductivity of this element
			//double k = 1.0; //assume 1.0 as the conductivity of the element

			double l12 = Math.sqrt(Math.pow(x1 - x2, 2.0) + Math.pow(y1 - y2, 2.0)); //length of edge 1
			double l13 = Math.sqrt(Math.pow(x1 - x3, 2.0) + Math.pow(y1 - y3, 2.0)); //length of edge 2
			double l23 = Math.sqrt(Math.pow(x3 - x2, 2.0) + Math.pow(y3 - y2, 2.0)); //length of edge 3

			//calculate the element stiffness matrix values
			double K11 = (k / (4 * earea2)) * (b1 * b1 + c1 * c1);
			double K12 = (k / (4 * earea2)) * (b1 * b2 + c1 * c2);
			double K13 = (k / (4 * earea2)) * (b1 * b3 + c1 * c3);
			double K21 = (k / (4 * earea2)) * (b2 * b1 + c2 * c1);
			double K22 = (k / (4 * earea2)) * (b2 * b2 + c2 * c2);
			double K23 = (k / (4 * earea2)) * (b2 * b3 + c2 * c3);
			double K31 = (k / (4 * earea2)) * (b3 * b1 + c3 * c1);
			double K32 = (k / (4 * earea2)) * (b3 * b2 + c3 * c2);
			double K33 = (k / (4 * earea2)) * (b3 * b3 + c3 * c3);

			int n1i = mesh.elemAt(i).pt1;
			int n2i = mesh.elemAt(i).pt2;
			int n3i = mesh.elemAt(i).pt3;

			//apply to the stiffness matrix
			K.setValue(K.getValue(n1i,n1i) + K11, n1i, n1i);
			K.setValue(K.getValue(n1i,n2i) + K12, n1i, n2i);
			K.setValue(K.getValue(n1i,n3i) + K13, n1i, n3i);
			K.setValue(K.getValue(n2i,n1i) + K21, n2i, n1i);
			K.setValue(K.getValue(n2i,n2i) + K22, n2i, n2i);
			K.setValue(K.getValue(n2i,n3i) + K23, n2i, n3i);
			K.setValue(K.getValue(n3i,n1i) + K31, n3i, n1i);
			K.setValue(K.getValue(n3i,n2i) + K32, n3i, n2i);
			K.setValue(K.getValue(n3i,n3i) + K33, n3i, n3i);

			//Apply edge HTC's to the stiffness and force matrix
			int pt1_bcflag = mesh.nodeAt(mesh.elemAt(i).pt1).bcflag;
			int pt2_bcflag = mesh.nodeAt(mesh.elemAt(i).pt2).bcflag;
			int pt3_bcflag = mesh.nodeAt(mesh.elemAt(i).pt3).bcflag;
			
			//System.out.println("Applying convective BC's");			
			if((pt1_bcflag == ThermalTriMesh.CONVNODE || pt1_bcflag == ThermalTriMesh.CONVFLUX) && (pt2_bcflag == ThermalTriMesh.CONVNODE || pt2_bcflag == ThermalTriMesh.CONVFLUX)) {
				double ave_htc = (mesh.nodeAt(mesh.elemAt(i).pt1).values[3] + mesh.nodeAt(mesh.elemAt(i).pt2).values[3])/2.0;
				double ave_temp = (mesh.nodeAt(mesh.elemAt(i).pt1).values[4] + mesh.nodeAt(mesh.elemAt(i).pt2).values[4])/2.0;
				double hl12 = ave_htc*l12;
				K.setValue(K.getValue(n1i,n1i) + (2.0*hl12)/6.0, n1i, n1i);
				K.setValue(K.getValue(n2i,n2i) + (2.0*hl12)/6.0, n2i, n2i);
				K.setValue(K.getValue(n1i,n2i) + (1.0*hl12)/6.0, n1i, n2i);
				K.setValue(K.getValue(n2i,n1i) + (1.0*hl12)/6.0, n2i, n1i);
				F.setValue(F.getValue(mesh.elemAt(i).pt1, 0) + (hl12*ave_temp)/2.0, mesh.elemAt(i).pt1, 0);
				F.setValue(F.getValue(mesh.elemAt(i).pt2, 0) + (hl12*ave_temp)/2.0, mesh.elemAt(i).pt2, 0);
			}
			if((pt1_bcflag == ThermalTriMesh.CONVNODE || pt1_bcflag == ThermalTriMesh.CONVFLUX) && (pt3_bcflag == ThermalTriMesh.CONVNODE || pt3_bcflag == ThermalTriMesh.CONVFLUX)) {
				double ave_htc = (mesh.nodeAt(mesh.elemAt(i).pt1).values[3] + mesh.nodeAt(mesh.elemAt(i).pt3).values[3])/2.0;
				double ave_temp = (mesh.nodeAt(mesh.elemAt(i).pt1).values[4] + mesh.nodeAt(mesh.elemAt(i).pt3).values[4])/2.0;
				double hl13 = ave_htc*l13;
				K.setValue(K.getValue(n1i,n1i) + (2.0*hl13)/6.0, n1i, n1i);
				K.setValue(K.getValue(n3i,n3i) + (2.0*hl13)/6.0, n3i, n3i);
				K.setValue(K.getValue(n1i,n3i) + (1.0*hl13)/6.0, n1i, n3i);
				K.setValue(K.getValue(n3i,n1i) + (1.0*hl13)/6.0, n3i, n1i);
				F.setValue(F.getValue(mesh.elemAt(i).pt1, 0) + (hl13*ave_temp)/2.0, mesh.elemAt(i).pt1, 0);
				F.setValue(F.getValue(mesh.elemAt(i).pt3, 0) + (hl13*ave_temp)/2.0, mesh.elemAt(i).pt3, 0);
			}
			if((pt2_bcflag == ThermalTriMesh.CONVNODE || pt2_bcflag == ThermalTriMesh.CONVFLUX) && (pt3_bcflag == ThermalTriMesh.CONVNODE || pt3_bcflag == ThermalTriMesh.CONVFLUX)) {
				double ave_htc = (mesh.nodeAt(mesh.elemAt(i).pt2).values[3] + mesh.nodeAt(mesh.elemAt(i).pt3).values[3])/2.0;
				double ave_temp = (mesh.nodeAt(mesh.elemAt(i).pt2).values[4] + mesh.nodeAt(mesh.elemAt(i).pt3).values[4])/2.0;
				double hl23 = ave_htc*l23;
				K.setValue(K.getValue(n2i,n2i) + (2.0*hl23)/6.0, n2i, n2i);
				K.setValue(K.getValue(n3i,n3i) + (2.0*hl23)/6.0, n3i, n3i);
				K.setValue(K.getValue(n2i,n3i) + (1.0*hl23)/6.0, n2i, n3i);
				K.setValue(K.getValue(n3i,n2i) + (1.0*hl23)/6.0, n3i, n2i);
				F.setValue(F.getValue(mesh.elemAt(i).pt2, 0) + (hl23*ave_temp)/2.0, mesh.elemAt(i).pt2, 0);
				F.setValue(F.getValue(mesh.elemAt(i).pt3, 0) + (hl23*ave_temp)/2.0, mesh.elemAt(i).pt3, 0);
			}

			//System.out.println("Applying heat flux BC's");			
			double q12 = 0.0;
			double q23 = 0.0;
			double q13 = 0.0;
			if((pt1_bcflag == ThermalTriMesh.FLUXNODE || pt1_bcflag == ThermalTriMesh.CONVFLUX) && (pt2_bcflag == ThermalTriMesh.FLUXNODE || pt2_bcflag == ThermalTriMesh.CONVFLUX)) {
				q12 = l12*(mesh.nodeAt(mesh.elemAt(i).pt1).values[5] + mesh.nodeAt(mesh.elemAt(i).pt2).values[5])/4.0;
			}
			if((pt1_bcflag == ThermalTriMesh.FLUXNODE || pt1_bcflag == ThermalTriMesh.CONVFLUX) && (pt3_bcflag == ThermalTriMesh.FLUXNODE || pt3_bcflag == ThermalTriMesh.CONVFLUX)) {
				q13 = l13*(mesh.nodeAt(mesh.elemAt(i).pt1).values[5] + mesh.nodeAt(mesh.elemAt(i).pt3).values[5])/4.0;
			}
			if((pt2_bcflag == ThermalTriMesh.FLUXNODE || pt2_bcflag == ThermalTriMesh.CONVFLUX) && (pt3_bcflag == ThermalTriMesh.FLUXNODE || pt3_bcflag == ThermalTriMesh.CONVFLUX)) {
				q23 = l23*(mesh.nodeAt(mesh.elemAt(i).pt2).values[5] + mesh.nodeAt(mesh.elemAt(i).pt3).values[5])/4.0;
			}
			F.setValue(F.getValue(mesh.elemAt(i).pt1, 0) + q12 + q13 , mesh.elemAt(i).pt1, 0);
			F.setValue(F.getValue(mesh.elemAt(i).pt2, 0) + q12 + q23 , mesh.elemAt(i).pt2, 0);
			F.setValue(F.getValue(mesh.elemAt(i).pt3, 0) + q13 + q23 , mesh.elemAt(i).pt3, 0);

			//Apply temperature BC's to the stiffness and force matrix
			if (mesh.nodeAt(mesh.elemAt(i).pt1).bcflag == ThermalTriMesh.TEMPNODE) {
				//System.out.println("here1 " + Double.toString(mesh.nodeAt(mesh.elemAt(i).pt3).values[2]));
				for (int j = 0; j < mesh.nodes.size(); j++) {
					K.setValue(0.0, mesh.elemAt(i).pt1, j);
				}
				K.setValue(1.0, mesh.elemAt(i).pt1, mesh.elemAt(i).pt1);
				F.setValue(mesh.nodeAt(mesh.elemAt(i).pt1).values[2], mesh.elemAt(i).pt1, 0);
			}
			if (mesh.nodeAt(mesh.elemAt(i).pt2).bcflag == ThermalTriMesh.TEMPNODE) {
				//System.out.println("here2 " + Double.toString(mesh.nodeAt(mesh.elemAt(i).pt3).values[2]));
				for (int j = 0; j < mesh.nodes.size(); j++) {
					K.setValue(0.0, mesh.elemAt(i).pt2, j);
				}
				K.setValue(1.0, mesh.elemAt(i).pt2, mesh.elemAt(i).pt2);
				F.setValue(mesh.nodeAt(mesh.elemAt(i).pt2).values[2], mesh.elemAt(i).pt2, 0);
			}
			if (mesh.nodeAt(mesh.elemAt(i).pt3).bcflag == ThermalTriMesh.TEMPNODE) {
				//System.out.println("here3 " + Double.toString(mesh.nodeAt(mesh.elemAt(i).pt3).values[2]));
				for (int j = 0; j < mesh.nodes.size(); j++) {
					K.setValue(0.0, mesh.elemAt(i).pt3, j);
				}
				K.setValue(1.0, mesh.elemAt(i).pt3, mesh.elemAt(i).pt3);
				F.setValue(mesh.nodeAt(mesh.elemAt(i).pt3).values[2], mesh.elemAt(i).pt3, 0);
			}
		}

		//System.out.println("M= " + Integer.toString(K.data.length));
		//System.out.println("N= " + Integer.toString(K.data[0].length));
		//System.out.println("----");
		//System.out.println(F.toString());
		//System.out.println(K.toString());
		//System.out.println("----");
		outwindow.append("M= " + Integer.toString(K.data.length) + "\n");
		outwindow.append("N= " + Integer.toString(K.data[0].length)+ "\n");
		outwindow.append("----\n");
		//System.out.println(F.toString());
		//System.out.println(K.toString());
		outwindow.append("----\n");
		
		
		//Now, solve out matrices
		/*Matrix T = K.solve(F);
		System.out.println("Completed solution");
		//System.out.println(T.toString());
		System.out.println("----");
		for(int i=0;i<mesh.nodes.size();i++) {
			mesh.nodes.get(i).values[0] = T.getValue(i, 0);
		}*/
		/*if(K.data.length > 4000) {
			outwindow.append("Using iterative solver!\n");
			HiPerfInterface hiper = new HiPerfInterface(HiPerfInterface.CPUMATRIXSOLVE,10,mesh,outwindow);
			hiper.A = K.data;
			hiper.b = F.data;
			hiper.run();
		} else {*/
		long cstart = System.currentTimeMillis();
			outwindow.append("Using direct solver!\n");
			Matrix T = K.solve(F);
			System.out.println("Completed solution");
			//System.out.println(T.toString());
			System.out.println("----");
			for(int i=0;i<mesh.nodes.size();i++) {
				mesh.nodes.get(i).values[0] = T.getValue(i, 0);
			}
			long cend = System.currentTimeMillis();
			long duration = cend - cstart;
			outwindow.append("Duration: " + Long.toString(duration) + "\n");
		//}
		/*double[][] T = CPUMatrixSolver.iterativeCPUSolver(K.data, F.data, 10, outwindow);
		for(int i=0;i<mesh.nodes.size();i++) {
			mesh.nodes.get(i).values[0] = T[i][0];
		}*/
	}
}
