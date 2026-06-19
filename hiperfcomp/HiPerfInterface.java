package hiperfcomp;

import javax.swing.JTextArea;

import analysis_mesh.AnalysisMesh;

public class HiPerfInterface extends Thread {
	public static int CPUMATRIXSOLVE = 1;
	public static int GPUMATRIXSOLVE = 2;
	
	public int solvetype; // type of solution to kick off
	public int threads;   // number of threads to solve with
	public double[][] A;  // stiffness matrix, N x N
	public double[][] b;  // force matrix, N x 1 
	public double[][] T;  // return vector
	public AnalysisMesh mesh; // the mesh that we can return values back to
	public JTextArea outwindow;
	
	public HiPerfInterface(int in_solvetype, int in_threads, AnalysisMesh in_mesh, JTextArea in_outwindow) {
		solvetype = in_solvetype;
		threads = in_threads;
		A = new double[0][0];
		b = new double[0][0];
		T = new double[0][0];
		mesh = in_mesh;
		outwindow = in_outwindow;
	}
	
	public void run() {
		if(A.length == 0 || b.length == 0) {
			outwindow.append("Linear equations not defined!!!\n");
		} else {
			if(solvetype == CPUMATRIXSOLVE) { //solve this on the CPU!!!!
				T = CPUMatrixSolver.iterativeCPUSolver(A, b, threads, outwindow);
				outwindow.append("Solution thread complete\n");
				for(int i=0;i<mesh.nodes.size();i++) {
					mesh.nodes.get(i).values[0] = T[i][0];
				}
			}
		}
	}
}
