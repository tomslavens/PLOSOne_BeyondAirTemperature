package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import data.GeomManager;
import geom.NURBArc;
import geom.Point3D;
import rendering.JDynomaticRenderer;

import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.awt.event.ActionEvent;
import java.awt.Dialog.ModalityType;
import java.awt.Dialog.ModalExclusionType;
import javax.swing.JCheckBox;
import javax.swing.JSlider;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class Add3PointArcDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	public JTextField temp_field;
	public boolean ok_hit;
	public GeomManager geom_manager;
	public Point3D pt1;
	public Point3D pt3;
	public boolean preview_generated;
	public JDynomaticRenderer renderer;
	public JSlider slider;
	public DecimalFormat df;
	private JCheckBox prev_checkbox;
	public int dir;

	public Add3PointArcDialog(Point3D in_pt1, Point3D in_pt3, GeomManager in_geom_manager, JDynomaticRenderer in_rend) {
		setModal(true);
		geom_manager = in_geom_manager;
		pt1 = in_pt1;
		pt3 = in_pt3;
		dir = NURBArc.DIR1;
		renderer = in_rend;
		preview_generated = false;
		df = new DecimalFormat("#.##");

		setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setAlwaysOnTop(true);
		setResizable(false);
		ok_hit = false;
		setTitle("3-Point Arc");
		setBounds(100, 100, 306, 240);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Radius");
		lblNewLabel.setBounds(10, 11, 113, 14);
		contentPanel.add(lblNewLabel);
		
		temp_field = new JTextField();
		double in_rad = 2.0*pt1.distTo(pt3);
		temp_field.setText(df.format(in_rad));
		temp_field.setBounds(130, 8, 129, 20);
		contentPanel.add(temp_field);
		temp_field.setColumns(10);
		
		double pt_dist = pt1.distTo(pt3);
		int min_rad = (int) Math.ceil(100*(pt_dist/2.0));
		int max_rad = (int) Math.floor(100*(pt_dist*5));

		prev_checkbox = new JCheckBox("Preview");
		prev_checkbox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				previewArc();
			}
		});
		prev_checkbox.setBounds(197, 80, 97, 23);
		contentPanel.add(prev_checkbox);
		
		slider = new JSlider();
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				sliderMoved();
			}
		});
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.setBounds(48, 36, 200, 37);
		slider.setMinimum(min_rad);
		slider.setMaximum(max_rad);
		int set_val = (int) Math.round(100*in_rad);
		slider.setValue(set_val);
		contentPanel.add(slider);
		
		JButton btnNewButton = new JButton("Flip Vector");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				flipVectorButt();
			}
		});
		btnNewButton.setBounds(58, 80, 89, 23);
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
						if(preview_generated) {
							geom_manager.removeCrvAt(geom_manager.crvCnt()-1);
						}
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
						if(preview_generated) {
							geom_manager.removeCrvAt(geom_manager.crvCnt()-1);
						}
						setVisible(false);
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
	private void flipVectorButt() {
		if(dir == NURBArc.DIR1)
			dir = NURBArc.DIR2;
		else
			dir = NURBArc.DIR1;
		previewArc();
	}
	private void previewArc() {
		if(prev_checkbox.isSelected()) {
			double radius = Double.valueOf(temp_field.getText());
			NURBArc arc = new NURBArc(pt1,pt3,radius,dir);
			if(!preview_generated) {
				preview_generated = true;
				geom_manager.previewCurve(arc);
			} else {
				int curve_cnt = geom_manager.crvCnt();
				geom_manager.setCrvAt(curve_cnt-1, arc);
			}
		} else {
			if(preview_generated) {
				preview_generated = false;
				geom_manager.removeCrvAt(geom_manager.crvCnt()-1); //remove our preview curve
			}
		}
		renderer.invalidate();
		renderer.repaint();
	}
	private void sliderMoved() {
		double radius = (double) slider.getValue()/100;
		temp_field.setText(df.format(radius));
		previewArc();
	}
}
