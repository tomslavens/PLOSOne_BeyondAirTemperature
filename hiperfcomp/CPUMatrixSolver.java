package hiperfcomp;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;

import javax.swing.JTextArea;

import linear_math.Matrix;

public class CPUMatrixSolver extends Thread {
	
	private double[][] MA; // stiffness matrx
	private double[][] Mb; // forcing matrix
	private double[][] Mx; // x-vector
	private double[] iMA; // inverse of the major diagonal; speeds calculations
	private double[] Nx; // new x-vector
	public volatile int ti;        // thread #
	private int thrdcnt;   // number of threads fired off
	public boolean throttle;
	
	// Constructor is used to define a threadable interface to run the solution in parallel on the CPU
	public CPUMatrixSolver(double[][] in_ma, double[][] in_mb, double[][] in_mx, double[] in_iMA, double[] in_nx, int in_ti, int in_thrdcnt) {
		MA = in_ma;
		Mb = in_mb;
		Mx = in_mx;
		iMA = in_iMA;
		Nx = in_nx;
		ti = in_ti;
		thrdcnt = in_thrdcnt;
		throttle = true;
	}

	// leverages Richardson's method to solve the system of equations Ax=b leveraging multiple threads to handle the inner product calculations
	public static double[][] iterativeCPUSolver(double[][] A, double[][] B, int threads, JTextArea outwindow) {	
		// first iteration
		double[][] x = new double[A[0].length][1];
		for(int i=0;i<x.length;i++) {
			x[i][0] = 50.0;
		}
		
		double[] xn = new double[A[0].length];
		double[] ima = new double[A.length];
		double cxn = 0.0;
		for(int i=0;i<x.length;i++) {
			cxn = B[i][0];
			for(int j=0;j<A[0].length;j++) {
				if(i != j)
					cxn = cxn - (A[i][j]*x[i][0]);
			}
			cxn = cxn*(1.0/A[i][i]);
			ima[i] = (double) (1.0)/A[i][i];
			xn[i] = cxn;
		}
		for(int i=0;i<x.length;i++) {
			x[i][0] = xn[i];
		}
		outwindow.append("First Iteration complete\n");
		//print2DArray(A,outwindow);
		//print2DArray(x,outwindow);
		
		// instantiate our error array
		long start = System.currentTimeMillis();
		double max_err = 100.0;
		int iter = 0;
		
		CPUMatrixSolver ms;
		ArrayList<CPUMatrixSolver> runners = new ArrayList<CPUMatrixSolver>();
		for(int t=0;t<threads;t++) {
			ms = new CPUMatrixSolver(A,B,x,ima,xn,t,threads);
			ms.start();
			runners.add(ms);
		}
		long th1 = 0;
		long th2 = 0;
		long th3 = 0;
		long cstart = 0;
		long cend = 0;
		while(max_err > 0.00001 && iter < 10000) {
			cstart = System.currentTimeMillis();
			for(int t=0;t<threads;t++) {
				runners.get(t).ti = t;
			}
			cend = System.currentTimeMillis();
			th1 = th1 + (cend-cstart);
			
			cstart = System.currentTimeMillis();			
			boolean isrunning = true;
			while(isrunning) {
				isrunning = false;
				for(int t=0;t<threads;t++) {
					if(runners.get(t).ti < A.length) {
						//System.out.println(runners.get(t).ti);
						isrunning = true;
					}
				}
			}			
			cend = System.currentTimeMillis();			
			th2 = th2 + (cend-cstart);
			
			cstart = System.currentTimeMillis();
			max_err = 0.0;
			for(int i=0;i<x.length;i++) {				
				double cer = Math.abs(xn[i] - x[i][0])/xn[i];
				if(cer > max_err)
					max_err = cer;
				x[i][0] = xn[i];
			}
			cend = System.currentTimeMillis();
			th3 = th3 + (cend-cstart);
			iter = iter + 1;
			//System.out.println("Iter: " + Integer.toString(iter) + " Ctime: " + Long.toString(cend-cstart));
		}
		//stop the threads
		for(int t=0;t<threads;t++) {
			runners.get(t).throttle = false;
		}
		outwindow.append("th1 ave" + Double.toString((double) th1/iter) + "\n");
		outwindow.append("th2 ave" + Double.toString((double) th2/iter) + "\n");
		outwindow.append("th3 ave" + Double.toString((double) th3/iter) + "\n");		
		
		//print2DArray(x,outwindow);
		outwindow.append("Err: " + Double.toString(max_err) + "\n");
		outwindow.append("Iter: " + Integer.toString(iter) + "\n");
		long end = System.currentTimeMillis();
		long duration = end - start;
		outwindow.append("Start Milli-time: " + Long.toString(start) + "\n");
		outwindow.append("End Milli-time: " + Long.toString(end) + "\n");
		outwindow.append("Duration Milli-time: " + Long.toString(duration) + "\n");
		
		//writeMatrix(A,"a.txt");
		//writeMatrix(B,"b.txt");
		//writeMatrix(x,"x.txt");
		return x;
	}
	// utilizes Richardson's Iteration method to iteratively solve the matrix
	public void run() {
		double txn = 0.0;
		double omega = 0.85;
		while(throttle) {
			//System.out.println("--" + Integer.toString(ti) + "--");
			if(ti < MA.length) {
				//System.out.println(ti);
				txn = Mb[ti][0];
				for(int j=0;j<MA[0].length;j++) {
					//if(ti != j) {  // Jacobi iteration
						txn = txn - MA[ti][j]*Mx[j][0];
					//}
				}
				//[ti] = txn*iMA[ti]; // Jacobi iteration
				Nx[ti] = Mx[ti][0] + omega*txn*iMA[ti];
				ti = ti + thrdcnt;
			} 
		}
	}
	
	private static void print2DArray(double[][] m, JTextArea outwindow) {
		String output = "[";
		for(int i=0;i<m.length;i++) {
			for(int j=0;j<m[0].length;j++) {
				output = output + Double.toString(m[i][j]) + ",  ";
			}
			if(i == m.length-1) {
				output = output + "]\n";
			} else {
				output = output + "\n";
			}
		}
		outwindow.append(output);
	}
	
    private static void writeMatrix(double[][] m, String fname) {
    	try {
    		BufferedWriter bw = new BufferedWriter(new FileWriter(fname));
    		int rows = m.length;
        	int cols = m[0].length;
        	for(int i=0;i<rows;i++) {
        		String output = " ";
        		for(int j=0;j<cols;j++) {
        			output = output + Double.toString(m[i][j]) + " ";
        		}
        		bw.write(output);
        		bw.newLine();
        		bw.flush();
        	}
        	bw.close();
    	} catch(Exception E) {
    		System.out.println(E.toString());
    	}
    }
}
