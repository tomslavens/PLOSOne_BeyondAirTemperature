package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import geom.Point3D;

import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.awt.event.ActionEvent;
import java.awt.Dialog.ModalityType;
import java.awt.Dialog.ModalExclusionType;
import java.awt.SystemColor;

public class PointAtLocDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	public JTextField x_field;
	public boolean ok_hit;
	public JTextField y_field;
	public JTextField z_field;

	public PointAtLocDialog() {
		setModal(true);
		setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setAlwaysOnTop(true);
		setResizable(false);
		ok_hit = false;
		setTitle("Point Location");
		setBounds(100, 100, 262, 178);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("x (cm)");
		lblNewLabel.setBounds(10, 11, 113, 14);
		contentPanel.add(lblNewLabel);
		
		x_field = new JTextField();
		x_field.setText("0");
		x_field.setBounds(130, 8, 77, 20);
		contentPanel.add(x_field);
		x_field.setColumns(10);
		{
			JLabel lblYcm = new JLabel("y (cm)");
			lblYcm.setBounds(10, 38, 113, 14);
			contentPanel.add(lblYcm);
		}
		{
			y_field = new JTextField();
			y_field.setText("0");
			y_field.setColumns(10);
			y_field.setBounds(130, 35, 77, 20);
			contentPanel.add(y_field);
		}
		{
			JLabel lblZcm = new JLabel("z (cm)");
			lblZcm.setBounds(10, 66, 113, 14);
			contentPanel.add(lblZcm);
		}
		{
			z_field = new JTextField();
			z_field.setBackground(SystemColor.info);
			z_field.setEditable(false);
			z_field.setText("0");
			z_field.setColumns(10);
			z_field.setBounds(130, 63, 77, 20);
			contentPanel.add(z_field);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						ok_hit = true;
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
						setVisible(false);
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
	public void setFields(Point3D pt) {
		DecimalFormat df = new DecimalFormat("#.###");
		x_field.setText(df.format(pt.x));
		y_field.setText(df.format(pt.y));
		z_field.setText(df.format(pt.z));
	}
}
