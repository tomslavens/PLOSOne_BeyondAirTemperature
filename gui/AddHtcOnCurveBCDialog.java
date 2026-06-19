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

public class AddHtcOnCurveBCDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	public JTextField temp_field;
	public boolean ok_hit;
	public JTextField htc_field;

	public AddHtcOnCurveBCDialog() {
		setModal(true);
		setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setAlwaysOnTop(true);
		setResizable(false);
		ok_hit = false;
		setTitle("Curve Htc BC");
		setBounds(100, 100, 279, 154);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Temperature (C)");
		lblNewLabel.setBounds(10, 11, 113, 14);
		contentPanel.add(lblNewLabel);
		
		temp_field = new JTextField();
		temp_field.setText("100");
		temp_field.setBounds(130, 8, 129, 20);
		contentPanel.add(temp_field);
		temp_field.setColumns(10);
		{
			JLabel lblHtcbtuhrftf = new JLabel("Htc (W/m^2*C)");
			lblHtcbtuhrftf.setBounds(10, 39, 113, 14);
			contentPanel.add(lblHtcbtuhrftf);
		}
		{
			htc_field = new JTextField();
			htc_field.setText("100");
			htc_field.setColumns(10);
			htc_field.setBounds(130, 36, 129, 20);
			contentPanel.add(htc_field);
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
