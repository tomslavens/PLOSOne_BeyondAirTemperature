package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import images.DynoImage;
import images.ImagePreviewer;

import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.awt.event.ActionEvent;
import java.awt.Dialog.ModalityType;
import java.awt.Dialog.ModalExclusionType;
import javax.swing.SwingConstants;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ImportImageDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	public JTextField length_field;
	public boolean ok_hit;
	private JTextField textField;
	private JTextField textField_1;
	private ImagePreviewer imager;
	public String fname;
	public JTextArea outwindow;
	public DynoImage dyno;

	public ImportImageDialog(JTextArea in_outwindow) {
		setModal(true);
		setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setAlwaysOnTop(true);
		setResizable(false);
		ok_hit = false;
		setTitle("Import Image");
		setBounds(100, 100, 1199, 866);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		outwindow = in_outwindow;
		dyno = new DynoImage();
		
		JLabel lblNewLabel = new JLabel("Point Distance (cm)");
		lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewLabel.setBounds(10, 11, 113, 14);
		contentPanel.add(lblNewLabel);
		
		length_field = new JTextField();
		DecimalFormat df = new DecimalFormat("#.###");
		length_field.setText(df.format(10));
		length_field.setBounds(130, 8, 77, 20);
		contentPanel.add(length_field);
		length_field.setColumns(10);
		
		JLabel lblXlocation = new JLabel("X-Location");
		lblXlocation.setHorizontalAlignment(SwingConstants.RIGHT);
		lblXlocation.setBounds(10, 48, 113, 14);
		contentPanel.add(lblXlocation);
		
		JLabel lblNewLabel_1_1 = new JLabel("Y-Location");
		lblNewLabel_1_1.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewLabel_1_1.setBounds(10, 73, 113, 14);
		contentPanel.add(lblNewLabel_1_1);
		
		textField = new JTextField();
		textField.setText("0");
		textField.setColumns(10);
		textField.setBounds(130, 45, 77, 20);
		contentPanel.add(textField);
		
		textField_1 = new JTextField();
		textField_1.setText("0");
		textField_1.setColumns(10);
		textField_1.setBounds(130, 73, 77, 20);
		contentPanel.add(textField_1);
		
		imager = new ImagePreviewer(this,dyno);
		imager.setBounds(217,8,966,785);
		contentPanel.add(imager);
		
		JButton btnNewButton = new JButton("Browse for File");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadImage();
			}
		});
		btnNewButton.setBounds(31, 104, 165, 23);
		contentPanel.add(btnNewButton);
		
		JButton btnSetScalePoints = new JButton("Set Scale Points");
		btnSetScalePoints.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scaleImage();
			}
		});
		btnSetScalePoints.setBounds(31, 138, 165, 23);
		contentPanel.add(btnSetScalePoints);
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
	private void loadImage() {
		JFileChooser filechooser = new JFileChooser();
		if(filechooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			fname = filechooser.getSelectedFile().getAbsolutePath();
			DynoImage cdyno = new DynoImage(fname,outwindow);
			imager.setImage(cdyno.image);
		}
	}
	private void scaleImage() {
		imager.scaleImage();
	}
}
