package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import data.GeomManager;

import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Dialog.ModalityType;
import java.awt.Dialog.ModalExclusionType;
import javax.swing.JComboBox;

public class ThermalTriMeshDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	public JTextField elem_field;
	public JTextField cond_field;
	public JComboBox mat_combo;
	public boolean ok_hit;

	public ThermalTriMeshDialog(GeomManager geom_manager) {
		setModal(true);
		setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setAlwaysOnTop(true);
		setResizable(false);
		ok_hit = false;
		setTitle("Thermal Triangle Mesh");
		setBounds(100, 100, 233, 250);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Element Size (cm)");
		lblNewLabel.setBounds(10, 11, 153, 14);
		contentPanel.add(lblNewLabel);
		
		elem_field = new JTextField();
		elem_field.setText("0.100");
		elem_field.setBounds(169, 9, 52, 20);
		contentPanel.add(elem_field);
		elem_field.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("Conductivity (W/m*K)");
		lblNewLabel_1.setBounds(10, 47, 153, 14);
		contentPanel.add(lblNewLabel_1);
		
		cond_field = new JTextField();
		cond_field.setText("0.100");
		cond_field.setBounds(169, 45, 52, 20);
		contentPanel.add(cond_field);
		cond_field.setColumns(10);
		
		JLabel lblNewLabel_1_1 = new JLabel("Material");
		lblNewLabel_1_1.setBounds(10, 84, 72, 14);
		contentPanel.add(lblNewLabel_1_1);
		
		mat_combo = new JComboBox();
		//mat_combo.addItem("Null Material");
		for(int i=0;i<geom_manager.materialCnt();i++) {
			mat_combo.addItem(geom_manager.getMaterial(i).matname);
		}
		mat_combo.setSelectedIndex(0);
		mat_combo.setBounds(120, 80, 101, 22);
		contentPanel.add(mat_combo);
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
}
