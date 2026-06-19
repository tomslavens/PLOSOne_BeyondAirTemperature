package gui;

import analysis.*;
import analysis_mesh.*;
import data.*;
import geom.*;
import geom_funcs.*;
import linear_math.Vector3D;
import rendering.*;

public class MainWindowFunctions {

	public static void entListSelection(Entity ent, GeomManager geom_manager, JDynomaticRenderer dynomaticRenderer) {
		if(ent.type == Entity.ANALYSISMESH) {
			AnalysisMesh amesh = (AnalysisMesh) ent;
			if(amesh.mesh_type == AnalysisMesh.THERMALTRIMESH) {
				ThermalTriMesh trimesh = (ThermalTriMesh) amesh;
				double edge_size = trimesh.edge_size;
				int mat_index = trimesh.material;
				ThermalTriMeshDialog dial = new ThermalTriMeshDialog(geom_manager);
				dial.elem_field.setText(Double.toString(edge_size));
				dial.mat_combo.setSelectedIndex(mat_index);
				dial.setVisible(true);
				if(dial.ok_hit) {
					trimesh.edge_size = Double.valueOf(dial.elem_field.getText());
					trimesh.material = dial.mat_combo.getSelectedIndex();
					trimesh.generateMesh(geom_manager);
					trimesh.applyMaterialProperties(geom_manager);
				}
			}
		}
		if(ent.type == Entity.CONSTRAINT) {
			Constraint craint = (Constraint) ent;
			if(craint.contype == Constraint.LENGTH2D) {
				Length2DConstraint lraint = (Length2DConstraint) ent;
				LengthConstraintDialog dial = new LengthConstraintDialog(lraint.vector.magnitude);
				dial.setVisible(true);
				if(dial.ok_hit) {
					double new_length = Double.valueOf(dial.length_field.getText());
					lraint.vector.magnitude = new_length;
					geom_manager.updateGeometry();
					dynomaticRenderer.invalidate();
					dynomaticRenderer.repaint();
				}
			}
		}
		if(ent.type == Entity.PARAMCURVE) {
			ParamCurve curve = (ParamCurve) ent;
			if(curve.curvetype == ParamCurve.NURBARC) {
				NURBArc arc = (NURBArc) curve;
				Add3PointArcDialog dial = new Add3PointArcDialog(arc.geompts[0],arc.geompts[2],geom_manager,dynomaticRenderer);
				dial.setVisible(true);
				if(dial.ok_hit) {
					double radius = Double.valueOf(dial.temp_field.getText());
					arc.radius = radius;
					geom_manager.updateGeometry();
				}
			}
		}
		if(ent.type == Entity.BOUNDARYCONDITION) {
			BoundaryCondition bc = (BoundaryCondition) ent;
			if(bc.BCtype == BoundaryCondition.HTCONCURVE) {
				HtcOnCurveBC htc = (HtcOnCurveBC) bc;
				AddHtcOnCurveBCDialog dial = new AddHtcOnCurveBCDialog();
				dial.temp_field.setText(Double.toString(htc.getTemp()));
				dial.htc_field.setText(Double.toString(htc.getTemp()));
				dial.setVisible(true);
				if(dial.ok_hit) {
					htc.setTemp(Double.valueOf(dial.temp_field.getText()));
					htc.setHtc(Double.valueOf(dial.htc_field.getText()));
				}
			}
			if(bc.BCtype == BoundaryCondition.FLUXONCURVE) {
				FluxOnCurveBC flux = (FluxOnCurveBC) bc;
				AddFluxOnCurveBCDialog dial = new AddFluxOnCurveBCDialog();
				dial.temp_field.setText(Double.toString(flux.getHeatFlux()));
				dial.setVisible(true);
				if(dial.ok_hit) {
					flux.setHeatFlux(Double.valueOf(dial.temp_field.getText()));
				}
			}
			if(bc.BCtype == BoundaryCondition.TEMPONCURVE) {
				TempOnCurveBC temp = (TempOnCurveBC) bc;
				AddTempOnCurveBCDialog dial = new AddTempOnCurveBCDialog();
				dial.temp_field.setText(Double.toString(temp.getTemp()));
				dial.setVisible(true);
				if(dial.ok_hit) {
					temp.setTemp(Double.valueOf(dial.temp_field.getText()));
				}
			}
			if(bc.BCtype == BoundaryCondition.TEMPONNODE) {
				TempOnNodeBC temp = (TempOnNodeBC) bc;
				AddTempOnNodeBCDialog dial = new AddTempOnNodeBCDialog();
				dial.temp_field.setText(Double.toString(temp.getTemp()));
				dial.setVisible(true);
				if(dial.ok_hit) {
					temp.setTemp(Double.valueOf(dial.temp_field.getText()));
				}
			}
			if(bc.BCtype == BoundaryCondition.BBRADONCURVE) {
				BBRadiationOnCurveBC rad = (BBRadiationOnCurveBC) bc;
				AddBBRadOnCurveBCDialog dial = new AddBBRadOnCurveBCDialog();
				dial.abs_field.setText(Double.toString(rad.getTargAbsorbtivity()));
				dial.area_field.setText(Double.toString(rad.getSrcArea()));
				dial.temp_field.setText(Double.toString(rad.getSrcTemp()));
				dial.x_field.setText(Double.toString(rad.getSrcPoint().x/100));
				dial.y_field.setText(Double.toString(rad.getSrcPoint().y/100));
				dial.u_field.setText(Double.toString(rad.getSrcNorm().u));
				dial.v_field.setText(Double.toString(rad.getSrcNorm().v));
				dial.setVisible(true);
				if(dial.ok_hit) {
					rad.setTargAbsorbtivity(Double.valueOf(dial.abs_field.getText()));
					rad.setSrcArea(Double.valueOf(dial.area_field.getText()));
					rad.setSrcTemp(Double.valueOf(dial.temp_field.getText()));
					double ptx = Double.valueOf(dial.x_field.getText())*100; //convert to cm
					double pty = Double.valueOf(dial.y_field.getText())*100; //convert to cm
					double nmu = Double.valueOf(dial.u_field.getText());
					double nmv = Double.valueOf(dial.v_field.getText());
					rad.setSrcPoint(new Point3D(ptx,pty,0.0));
					rad.setSrcNorm(new Vector3D(nmu,nmv,0.0));
				}
			}
		}
	}
}
