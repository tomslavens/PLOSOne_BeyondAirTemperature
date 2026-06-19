package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import data.GeomManager;
import geom.NURBArc;
import geom.OffsetCurve;
import geom.ParamCurve;
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
import javax.swing.SwingConstants;

public class AddOffsetCurveDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	public JTextField xy_field;
	public boolean ok_hit;
	public GeomManager geom_manager;
	public ParamCurve target;
	public boolean preview_generated;
	public JDynomaticRenderer renderer;
	public DecimalFormat df;
	private JCheckBox prev_checkbox;
	public JTextField xz_field;
	public JTextField yz_field;

	public AddOffsetCurveDialog(ParamCurve in_target, GeomManager in_geom_manager, JDynomaticRenderer in_rend) {
		setModal(true);
		geom_manager = in_geom_manager;
		target = in_target;
		renderer = in_rend;
		preview_generated = false;
		df = new DecimalFormat("#.##");

		setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setAlwaysOnTop(true);
		setResizable(false);
		ok_hit = false;
		setTitle("Offset Curve");
		setBounds(100, 100, 235, 241);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("XY Offset");
		lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewLabel.setBounds(10, 11, 59, 14);
		contentPanel.add(lblNewLabel);
		
		xy_field = new JTextField();
		xy_field.setText(df.format(0.1));
		xy_field.setBounds(79, 11, 129, 20);
		contentPanel.add(xy_field);
		xy_field.setColumns(10);

		prev_checkbox = new JCheckBox("Preview");
		prev_checkbox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				previewArc();
			}
		});
		prev_checkbox.setBounds(111, 94, 97, 23);
		contentPanel.add(prev_checkbox);
		
		JLabel lblXzOffset = new JLabel("XZ Offset");
		lblXzOffset.setHorizontalAlignment(SwingConstants.RIGHT);
		lblXzOffset.setBounds(10, 39, 59, 14);
		contentPanel.add(lblXzOffset);
		
		xz_field = new JTextField();
		xz_field.setText("0");
		xz_field.setColumns(10);
		xz_field.setBounds(79, 39, 129, 20);
		contentPanel.add(xz_field);
		
		JLabel lblYzOffset = new JLabel("YZ Offset");
		lblYzOffset.setHorizontalAlignment(SwingConstants.RIGHT);
		lblYzOffset.setBounds(10, 67, 59, 14);
		contentPanel.add(lblYzOffset);
		
		yz_field = new JTextField();
		yz_field.setText("0");
		yz_field.setColumns(10);
		yz_field.setBounds(79, 67, 129, 20);
		contentPanel.add(yz_field);
		
		JButton btnNewButton = new JButton("Update Preview");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				previewArc();
			}
		});
		btnNewButton.setBounds(66, 145, 142, 23);
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
	private void previewArc() {
		if(prev_checkbox.isSelected()) {
			double xyoffset = Double.valueOf(xy_field.getText());
			double xzoffset = Double.valueOf(xz_field.getText());
			double yzoffset = Double.valueOf(yz_field.getText());
			OffsetCurve ocurve = new OffsetCurve(target,xyoffset,xzoffset,yzoffset);
			if(!preview_generated) {
				preview_generated = true;
				geom_manager.previewCurve(ocurve);
			} else {
				int curve_cnt = geom_manager.crvCnt();
				geom_manager.setCrvAt(curve_cnt-1, ocurve);
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
}
