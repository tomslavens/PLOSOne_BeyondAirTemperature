package analysis;

import java.util.ArrayList;
import data.Entity;
import data.GeomManager;

//======
// BoundaryCondition - abstract class defining basic functionality of our boundary conditions
public abstract class BoundaryCondition extends Entity {
	public int BCtype;
	public ArrayList<Entity> entities; // entities that this applies to
	public int value_cnt;
	public double[] values;
	// --- inhereted from Entity ---
	//public int type;
	//public int tag;
	//public String name;
	
	// BC Types
	public static int TEMPONNODE = 0;
	public static int TEMPONCURVE = 1;
	public static int HTCONCURVE = 2;
	public static int FLUXONCURVE = 3;
	public static int BBRADONCURVE = 4;
	
	public abstract int getEntityType();
	
	// remove an entity as a target of this BC
	public void removeEntity(int ent_tag) {
		for(int i=0;i<entities.size();i++) {
			if(entities.get(i).tag == ent_tag) {
				entities.remove(i);
				break;
			}
		}
	}

	//--------------------
	// BC File IO
	// BC File String Format)
	// ent_type,tag,name:bctype,value_cnt,ent_cnt,value1,..valueN,ent_tag1,...,ent_tagN
	public String getFileString() {
		String output = Integer.toString(type) + "," + Integer.toString(tag) + "," + name + ":";
		output = output + Integer.toString(BCtype) + "," + Integer.toString(value_cnt) +  "," + Integer.toString(entities.size());
		for(int i=0;i<value_cnt;i++) {
			output = output + "," + Double.toString(values[i]);
		}
		for(int i=0;i<entities.size();i++) {
			output = output + "," + Integer.toString(entities.get(i).tag);
		}		
		return output;
	}
	// reads a boundary condition from a filestring
	public static BoundaryCondition getBoundaryCondition(String filestring, GeomManager geom_manager) {
		String[] header = filestring.split(":")[0].split(",");
		String[] data = filestring.split(":")[1].split(",");
		
		int ctag = Integer.valueOf(header[1]);
		String cname = header[2];
		int cbctype = Integer.valueOf(data[0]);
		int cvalue_cnt = Integer.valueOf(data[1]);
		int cent_cnt = Integer.valueOf(data[2]);
		double[] cvals = new double[cvalue_cnt];
		int[] cents = new int[cent_cnt];
		for(int i=0;i<cvalue_cnt;i++) {
			int ci = i+3;
			cvals[i] = Double.valueOf(data[ci]);
		}
		for(int i=0;i<cent_cnt;i++) {
			int ci = i+3+cvalue_cnt;
			cents[i] = Integer.valueOf(data[ci]);
		}
		BoundaryCondition bc = null;
		if(cbctype == TEMPONNODE) {
			TempOnNodeBC tonbc = new TempOnNodeBC();
			tonbc.tag = ctag;
			tonbc.name = cname;
			tonbc.value_cnt = cvalue_cnt;
			tonbc.entities.add(geom_manager.getAnalysisNode(cents[0]));
			tonbc.values[0] = cvals[0];
			bc = tonbc;
		}
		if(cbctype == TEMPONCURVE) {
			TempOnCurveBC tocbc = new TempOnCurveBC();
			tocbc.tag = ctag;
			tocbc.name = cname;
			tocbc.value_cnt = cvalue_cnt;
			for(int i=0;i<cvals.length;i++) {
				tocbc.values[i] = cvals[i];
			}
			for(int i=0;i<cents.length;i++) {
				tocbc.entities.add(geom_manager.getEntity(cents[i]));
			}
			bc = tocbc;
		}
		if(cbctype == HTCONCURVE) {
			HtcOnCurveBC tocbc = new HtcOnCurveBC();
			tocbc.tag = ctag;
			tocbc.name = cname;
			tocbc.value_cnt = cvalue_cnt;
			for(int i=0;i<cvals.length;i++) {
				tocbc.values[i] = cvals[i];
			}
			for(int i=0;i<cents.length;i++) {
				tocbc.entities.add(geom_manager.getEntity(cents[i]));
			}
			bc = tocbc;
		}
		if(cbctype == FLUXONCURVE) {
			FluxOnCurveBC tocbc = new FluxOnCurveBC();
			tocbc.tag = ctag;
			tocbc.name = cname;
			tocbc.value_cnt = cvalue_cnt;
			for(int i=0;i<cvals.length;i++) {
				tocbc.values[i] = cvals[i];
			}
			for(int i=0;i<cents.length;i++) {
				tocbc.entities.add(geom_manager.getEntity(cents[i]));
			}
			bc = tocbc;
		}
		if(cbctype == BBRADONCURVE) {
			BBRadiationOnCurveBC tocbc = new BBRadiationOnCurveBC();
			tocbc.tag = ctag;
			tocbc.name = cname;
			tocbc.value_cnt = cvalue_cnt;
			for(int i=0;i<cvals.length;i++) {
				tocbc.values[i] = cvals[i];
			}
			for(int i=0;i<cents.length;i++) {
				tocbc.entities.add(geom_manager.getEntity(cents[i]));
			}
			bc = tocbc;
		}
		return bc;
	}
}
