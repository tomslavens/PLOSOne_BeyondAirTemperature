package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Dialog.ModalityType;
import java.awt.Dialog.ModalExclusionType;

public class AddBBRadOnCurveBCDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	public JTextField temp_field;
	public boolean ok_hit;
	public JTextField abs_field;
	public JTextField area_field;
	public JTextField x_field;
	public JTextField y_field;
	public JTextField v_field;
	public JTextField u_field;

	public AddBBRadOnCurveBCDialog() {
		setModal(true);
		setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setAlwaysOnTop(true);
		setResizable(false);
		ok_hit = false;
		setTitle("Black Body Load on Curve BC");
		setBounds(100, 100, 438, 293);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Source Temperature (K)");
		lblNewLabel.setBounds(10, 79, 157, 14);
		contentPanel.add(lblNewLabel);
		
		temp_field = new JTextField();
		temp_field.setText("100");
		temp_field.setBounds(207, 73, 129, 20);
		contentPanel.add(temp_field);
		temp_field.setColumns(10);
		{
			JLabel lblHtcbtuhrftf = new JLabel("Target Absorptivity");
			lblHtcbtuhrftf.setBounds(10, 17, 157, 14);
			contentPanel.add(lblHtcbtuhrftf);
		}
		{
			abs_field = new JTextField();
			abs_field.setText("1");
			abs_field.setColumns(10);
			abs_field.setBounds(207, 11, 129, 20);
			contentPanel.add(abs_field);
		}
		{
			JLabel lblSourceArea = new JLabel("Source Area");
			lblSourceArea.setBounds(10, 48, 157, 14);
			contentPanel.add(lblSourceArea);
		}
		{
			area_field = new JTextField();
			area_field.setText("1");
			area_field.setColumns(10);
			area_field.setBounds(207, 42, 129, 20);
			contentPanel.add(area_field);
		}
		{
			JLabel lblSourceLocationm = new JLabel("Source Location (m)");
			lblSourceLocationm.setBounds(10, 131, 157, 14);
			contentPanel.add(lblSourceLocationm);
		}
		{
			x_field = new JTextField();
			x_field.setText("0");
			x_field.setColumns(10);
			x_field.setBounds(207, 125, 53, 20);
			contentPanel.add(x_field);
		}
		{
			y_field = new JTextField();
			y_field.setText("0");
			y_field.setColumns(10);
			y_field.setBounds(270, 125, 53, 20);
			contentPanel.add(y_field);
		}
		{
			v_field = new JTextField();
			v_field.setText("0");
			v_field.setColumns(10);
			v_field.setBounds(270, 156, 53, 20);
			contentPanel.add(v_field);
		}
		{
			u_field = new JTextField();
			u_field.setText("0");
			u_field.setColumns(10);
			u_field.setBounds(207, 156, 53, 20);
			contentPanel.add(u_field);
		}
		{
			JLabel lblSourceDirection = new JLabel("Source Direction");
			lblSourceDirection.setBounds(10, 162, 157, 14);
			contentPanel.add(lblSourceDirection);
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
}
