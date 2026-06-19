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
import java.awt.SystemColor;
import javax.swing.SwingConstants;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;

public class SystemParametersMenu extends JDialog {

	private final JPanel contentPanel = new JPanel();
	public JTextField corecnt_field;
	public boolean ok_hit;
	public JTextField jvm_field;
	private JTextField curmem_field;
	public JComboBox corecnt_box;
	public JCheckBox multcore_checkbox;

	public SystemParametersMenu() {
		setModal(true);
		setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setAlwaysOnTop(true);
		setResizable(false);
		ok_hit = false;
		setTitle("System Parameters");
		setBounds(100, 100, 308, 236);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("System Cores");
		lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewLabel.setBounds(10, 11, 113, 14);
		contentPanel.add(lblNewLabel);
		
		corecnt_field = new JTextField();
		corecnt_field.setBackground(SystemColor.info);
		corecnt_field.setEditable(false);
		int core_cnt = Runtime.getRuntime().availableProcessors();
		corecnt_field.setText(Integer.toString(core_cnt));
		corecnt_field.setBounds(130, 8, 129, 20);
		contentPanel.add(corecnt_field);
		corecnt_field.setColumns(10);
		{
			JLabel lblHtcbtuhrftf = new JLabel("JVM Max Memory");
			lblHtcbtuhrftf.setHorizontalAlignment(SwingConstants.RIGHT);
			lblHtcbtuhrftf.setBounds(10, 39, 113, 14);
			contentPanel.add(lblHtcbtuhrftf);
		}
		{
			long jvm_mem = Runtime.getRuntime().maxMemory();
			jvm_field = new JTextField();
			jvm_field.setBackground(SystemColor.info);
			jvm_field.setEditable(false);
			jvm_field.setText(Long.toString(jvm_mem));
			jvm_field.setColumns(10);
			jvm_field.setBounds(130, 36, 129, 20);
			contentPanel.add(jvm_field);
		}
		
		JLabel lblCurrentMem = new JLabel("Current Mem Use");
		lblCurrentMem.setHorizontalAlignment(SwingConstants.RIGHT);
		lblCurrentMem.setBounds(10, 67, 113, 14);
		contentPanel.add(lblCurrentMem);
		
		curmem_field = new JTextField();
		long used_mem = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
		curmem_field.setText(Long.toString(used_mem));
		curmem_field.setEditable(false);
		curmem_field.setColumns(10);
		curmem_field.setBackground(SystemColor.info);
		curmem_field.setBounds(130, 64, 129, 20);
		contentPanel.add(curmem_field);
		{
			JLabel lblTargetCoreCount = new JLabel("Target Core Count");
			lblTargetCoreCount.setHorizontalAlignment(SwingConstants.RIGHT);
			lblTargetCoreCount.setBounds(10, 92, 113, 14);
			contentPanel.add(lblTargetCoreCount);
		}
		
		corecnt_box = new JComboBox();
		for(int i=0;i<core_cnt;i++) {
			corecnt_box.addItem(Integer.toString(i+1));
		}
		corecnt_box.setBounds(130, 88, 129, 22);
		contentPanel.add(corecnt_box);
		multcore_checkbox = new JCheckBox("Multicore Enable");
		multcore_checkbox.setBounds(130, 117, 129, 23);
		contentPanel.add(multcore_checkbox);
		
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
