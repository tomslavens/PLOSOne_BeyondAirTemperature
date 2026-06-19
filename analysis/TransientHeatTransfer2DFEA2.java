package analysis;

import javax.swing.JTextArea;

import hiperfcomp.CPUMatrixSolver;
import hiperfcomp.HiPerfInterface;
import jnigpu.JGPUInterface;
import linear_math.Matrix;

public class TransientHeatTransfer2DFEA2 {

	// SolveSteadyStateTriMesh - this function will take a ThermalTriMesh and solve 
	public static void SolveTransientTriMesh(ThermalTriMesh mesh, double init_temp, double time, double dt, JTextArea outwindow) {
		//System.out.println("Mesh has " + Integer.toString(mesh.nodes.size()) + " nodes");
		//System.out.println("Mesh has " + Integer.toString(mesh.elems.size()) + " elements");
		outwindow.append("Transient solver started!\n");
		outwindow.append("Mesh has " + Integer.toString(mesh.nodes.size()) + " nodes\n");
		outwindow.append("Mesh has " + Integer.toString(mesh.elems.size()) + " elements\n");		
		Matrix M = new Matrix(mesh.nodes.size(), mesh.nodes.size()); //create our empty Mass matrix
		Matrix K = new Matrix(mesh.nodes.size(), mesh.nodes.size()); //create our empty Diffusion matrix
		Matrix H = new Matrix(mesh.nodes.size(), mesh.nodes.size()); //create our empty Convection matrix
		Matrix F = new Matrix(mesh.nodes.size(),1); //create our empty force vector
		Matrix T = new Matrix(mesh.nodes.size(),1); //create our empty Temp vector

		//now setup the mass, diffusion matrix, and force vector
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
			double k1 = mesh.nodeAt(mesh.elemAt(i).pt1).values[1]; //conductivity of node 1
			double k2 = mesh.nodeAt(mesh.elemAt(i).pt2).values[1]; //conductivity of node 2
			double k3 = mesh.nodeAt(mesh.elemAt(i).pt3).values[1]; //conductivity of node 3
			double k = (k1+k2+k3)/3.0; //take the mean of the 3 nodes as the overall conductivity of this element
			double rho1 = mesh.nodeAt(mesh.elemAt(i).pt1).values[6]; //density of node 1
			double rho2 = mesh.nodeAt(mesh.elemAt(i).pt2).values[6]; //density of node 2
			double rho3 = mesh.nodeAt(mesh.elemAt(i).pt3).values[6]; //density of node 3
			double rho = 1000*(rho1+rho2+rho3)/3.0; //take the mean of the 3 nodes as the overall density of this element (kg/m^3)
			double cp1 = mesh.nodeAt(mesh.elemAt(i).pt1).values[7]; //specific heat of node 1
			double cp2 = mesh.nodeAt(mesh.elemAt(i).pt2).values[7]; //specific heat of node 2
			double cp3 = mesh.nodeAt(mesh.elemAt(i).pt3).values[7]; //specific heat of node 3
			double cp = 1000*(cp1+cp2+cp3)/3.0; //take the mean of the 3 nodes as the overall Cp of this element
			double alpha = k/(rho*cp);
			alpha = 1e-5;

			double l12 = Math.sqrt(Math.pow(x1 - x2, 2.0) + Math.pow(y1 - y2, 2.0)); //length of edge 1
			double l13 = Math.sqrt(Math.pow(x1 - x3, 2.0) + Math.pow(y1 - y3, 2.0)); //length of edge 2
			double l23 = Math.sqrt(Math.pow(x3 - x2, 2.0) + Math.pow(y3 - y2, 2.0)); //length of edge 3

			//calculate the element mass matrix values
			double Mfactor = earea2/12;
			double M11 = Mfactor*2.0;
			double M12 = Mfactor;
			double M13 = Mfactor;
			double M21 = Mfactor;
			double M22 = Mfactor*2.0;
			double M23 = Mfactor;
			double M31 = Mfactor;
			double M32 = Mfactor;
			double M33 = Mfactor*2.0;

			int n1i = mesh.elemAt(i).pt1;
			int n2i = mesh.elemAt(i).pt2;
			int n3i = mesh.elemAt(i).pt3;

			//apply to the mass matrix
			M.setValue(M.getValue(n1i,n1i) + M11, n1i, n1i);
			M.setValue(M.getValue(n1i,n2i) + M12, n1i, n2i);
			M.setValue(M.getValue(n1i,n3i) + M13, n1i, n3i);
			M.setValue(M.getValue(n2i,n1i) + M21, n2i, n1i);
			M.setValue(M.getValue(n2i,n2i) + M22, n2i, n2i);
			M.setValue(M.getValue(n2i,n3i) + M23, n2i, n3i);
			M.setValue(M.getValue(n3i,n1i) + M31, n3i, n1i);
			M.setValue(M.getValue(n3i,n2i) + M32, n3i, n2i);
			M.setValue(M.getValue(n3i,n3i) + M33, n3i, n3i);
			
			//calculate the diffusion matrix
			double Kfactor = alpha/(4.0*earea2);
			double K11 = Kfactor * (b1 * b1 + c1 * c1);
			double K12 = Kfactor * (b1 * b2 + c1 * c2);
			double K13 = Kfactor * (b1 * b3 + c1 * c3);
			double K21 = Kfactor * (b2 * b1 + c2 * c1);
			double K22 = Kfactor * (b2 * b2 + c2 * c2);
			double K23 = Kfactor * (b2 * b3 + c2 * c3);
			double K31 = Kfactor * (b3 * b1 + c3 * c1);
			double K32 = Kfactor * (b3 * b2 + c3 * c2);
			double K33 = Kfactor * (b3 * b3 + c3 * c3);

			//apply to the diffusion matrix
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
				H.setValue(H.getValue(n1i,n1i) + (2.0*hl12)/6.0, n1i, n1i);
				H.setValue(H.getValue(n2i,n2i) + (2.0*hl12)/6.0, n2i, n2i);
				H.setValue(H.getValue(n1i,n2i) + (1.0*hl12)/6.0, n1i, n2i);
				H.setValue(H.getValue(n2i,n1i) + (1.0*hl12)/6.0, n2i, n1i);
				F.setValue(F.getValue(mesh.elemAt(i).pt1, 0) + (hl12*ave_temp)/2.0, mesh.elemAt(i).pt1, 0);
				F.setValue(F.getValue(mesh.elemAt(i).pt2, 0) + (hl12*ave_temp)/2.0, mesh.elemAt(i).pt2, 0);
			}
			if((pt1_bcflag == ThermalTriMesh.CONVNODE || pt1_bcflag == ThermalTriMesh.CONVFLUX) && (pt3_bcflag == ThermalTriMesh.CONVNODE || pt3_bcflag == ThermalTriMesh.CONVFLUX)) {
				double ave_htc = (mesh.nodeAt(mesh.elemAt(i).pt1).values[3] + mesh.nodeAt(mesh.elemAt(i).pt3).values[3])/2.0;
				double ave_temp = (mesh.nodeAt(mesh.elemAt(i).pt1).values[4] + mesh.nodeAt(mesh.elemAt(i).pt3).values[4])/2.0;
				double hl13 = ave_htc*l13;
				H.setValue(H.getValue(n1i,n1i) + (2.0*hl13)/6.0, n1i, n1i);
				H.setValue(H.getValue(n3i,n3i) + (2.0*hl13)/6.0, n3i, n3i);
				H.setValue(H.getValue(n1i,n3i) + (1.0*hl13)/6.0, n1i, n3i);
				H.setValue(H.getValue(n3i,n1i) + (1.0*hl13)/6.0, n3i, n1i);
				F.setValue(F.getValue(mesh.elemAt(i).pt1, 0) + (hl13*ave_temp)/2.0, mesh.elemAt(i).pt1, 0);
				F.setValue(F.getValue(mesh.elemAt(i).pt3, 0) + (hl13*ave_temp)/2.0, mesh.elemAt(i).pt3, 0);
			}
			if((pt2_bcflag == ThermalTriMesh.CONVNODE || pt2_bcflag == ThermalTriMesh.CONVFLUX) && (pt3_bcflag == ThermalTriMesh.CONVNODE || pt3_bcflag == ThermalTriMesh.CONVFLUX)) {
				double ave_htc = (mesh.nodeAt(mesh.elemAt(i).pt2).values[3] + mesh.nodeAt(mesh.elemAt(i).pt3).values[3])/2.0;
				double ave_temp = (mesh.nodeAt(mesh.elemAt(i).pt2).values[4] + mesh.nodeAt(mesh.elemAt(i).pt3).values[4])/2.0;
				double hl23 = ave_htc*l23;
				H.setValue(H.getValue(n2i,n2i) + (2.0*hl23)/6.0, n2i, n2i);
				H.setValue(H.getValue(n3i,n3i) + (2.0*hl23)/6.0, n3i, n3i);
				H.setValue(H.getValue(n2i,n3i) + (1.0*hl23)/6.0, n2i, n3i);
				H.setValue(H.getValue(n3i,n2i) + (1.0*hl23)/6.0, n3i, n2i);
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
					M.setValue(0.0, mesh.elemAt(i).pt1, j);
				}
				M.setValue(1.0, mesh.elemAt(i).pt1, mesh.elemAt(i).pt1);
				F.setValue(mesh.nodeAt(mesh.elemAt(i).pt1).values[2], mesh.elemAt(i).pt1, 0);
			}
			if (mesh.nodeAt(mesh.elemAt(i).pt2).bcflag == ThermalTriMesh.TEMPNODE) {
				//System.out.println("here2 " + Double.toString(mesh.nodeAt(mesh.elemAt(i).pt3).values[2]));
				for (int j = 0; j < mesh.nodes.size(); j++) {
					M.setValue(0.0, mesh.elemAt(i).pt2, j);
				}
				M.setValue(1.0, mesh.elemAt(i).pt2, mesh.elemAt(i).pt2);
				F.setValue(mesh.nodeAt(mesh.elemAt(i).pt2).values[2], mesh.elemAt(i).pt2, 0);
			}
			if (mesh.nodeAt(mesh.elemAt(i).pt3).bcflag == ThermalTriMesh.TEMPNODE) {
				//System.out.println("here3 " + Double.toString(mesh.nodeAt(mesh.elemAt(i).pt3).values[2]));
				for (int j = 0; j < mesh.nodes.size(); j++) {
					M.setValue(0.0, mesh.elemAt(i).pt3, j);
				}
				M.setValue(1.0, mesh.elemAt(i).pt3, mesh.elemAt(i).pt3);
				F.setValue(mesh.nodeAt(mesh.elemAt(i).pt3).values[2], mesh.elemAt(i).pt3, 0);
			}
		}

		//System.out.println("M= " + Integer.toString(K.data.length));
		//System.out.println("N= " + Integer.toString(K.data[0].length));
		//System.out.println("----");
		//System.out.println(F.toString());
		//System.out.println(K.toString());
		//System.out.println("----");
		outwindow.append("M= " + Integer.toString(M.data.length) + "\n");
		outwindow.append("N= " + Integer.toString(M.data[0].length)+ "\n");
		outwindow.append("----\n");
		//System.out.println(F.toString());
		//System.out.println(K.toString());
		outwindow.append("----\n");
		
		//setup our initial temperature guess
		for(int i=0;i<mesh.nodes.size();i++) {
			T.setValue(init_temp, i, 0);
		}
		outwindow.append("Stepup of Matrices complete\n");
		
		//Now, solve out matrices
		long cstart = System.currentTimeMillis();
		// create matrix Kprime = K + H
		Matrix.writeMatrix(M.data,"M.txt");
		Matrix.writeMatrix(K.data,"K.txt");
		Matrix.writeMatrix(H.data,"H.txt");
		Matrix.writeMatrix(F.data,"F.txt");

	
		for(int g=0;g<120000;g++) {
			Matrix Kprime = K.plus(H);
			Matrix Minv = M.inverse();
			Matrix p1 = Minv.times(Kprime);
			p1 = p1.times(T);
			Matrix p2 = Minv.times(F);
			Matrix p3 = p2.minus(p1);
			p3 = p3.times(dt);
			T = T.plus(p3);
		}
		
		for(int i=0;i<mesh.nodes.size();i++) {
			mesh.nodes.get(i).values[0] = T.getValue(i, 0);
		}

		long cend = System.currentTimeMillis();
		long duration = cend - cstart;
		outwindow.append("Duration: " + Long.toString(duration) + "\n");
	}
}
