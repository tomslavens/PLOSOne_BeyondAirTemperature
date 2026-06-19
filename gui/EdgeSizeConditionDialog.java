package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import geom.NURBArc;
import geom.ParamCurve;
import geom.Point3D;
import rendering.JDynomaticRenderer;

import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import java.awt.Dialog.ModalityType;
import java.awt.Dialog.ModalExclusionType;

public class EdgeSizeConditionDialog extends JDialog {

	public ArrayList<ParamCurve> curves;
	private final JPanel contentPanel = new JPanel();
	public JTextField length_field;
	public boolean ok_hit;
	public JDynomaticRenderer renderer;

	public EdgeSizeConditionDialog(ArrayList<ParamCurve> in_curves, JDynomaticRenderer in_renderer) {
		setModal(true);
		curves = in_curves;
		renderer = in_renderer;
		setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setAlwaysOnTop(true);
		setResizable(false);
		ok_hit = false;
		setTitle("Edge Size");
		setBounds(100, 100, 262, 169);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Length (cm)");
		lblNewLabel.setBounds(10, 11, 113, 14);
		contentPanel.add(lblNewLabel);
		
		length_field = new JTextField();
		DecimalFormat df = new DecimalFormat("#.###");
		length_field.setText(df.format(0.100));
		length_field.setBounds(130, 8, 77, 20);
		contentPanel.add(length_field);
		length_field.setColumns(10);
		
		JButton btnNewButton = new JButton("Preview");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				previewArc();
			}
		});
		btnNewButton.setBounds(118, 48, 89, 23);
		contentPanel.add(btnNewButton);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						ok_hit = true;
						renderer.clearPointBuffer();
						setVisible(false);
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						ok_hit = false;
						renderer.clearPointBuffer();
						setVisible(false);
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
	private void previewArc() {
		renderer.clearPointBuffer();
		for(int i=0;i<curves.size();i++) {
			double clength = curves.get(i).getCurveLength(); //get the length of the curve
			double cedge_size = Double.valueOf(length_field.getText());
			int chunks = (int) Math.floor(clength/cedge_size);		
			//now create a distribution of points along the curve by subdividing it by the edge_size
			ArrayList<Point3D> cpts = curves.get(i).getPointSet(chunks);
			for(int j=0;j<cpts.size();j++) {
				renderer.addPointToPointBuffer(cpts.get(j));
			}
		}
		renderer.invalidate();
		renderer.repaint();
	}
}
