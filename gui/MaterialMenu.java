package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.DefaultListModel;
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
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

import data.GeomManager;
import data.Material;

import java.awt.Color;
import javax.swing.JList;
import javax.swing.JTable;

public class MaterialMenu extends JDialog {

	private final JPanel contentPanel = new JPanel();
	public boolean ok_hit;
	public String fname;
	public JTextArea outwindow;
	public DynoImage dyno;
	private JTable prop_table;
	private JList<String> mat_list;
	private JList<String> prop_list;
	private GeomManager geom_manager;

	public MaterialMenu(GeomManager in_manager, JTextArea in_outwindow) {
		setModal(true);
		setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setAlwaysOnTop(true);
		setResizable(false);
		ok_hit = false;
		setTitle("Material Menu");
		setBounds(100, 100, 785, 542);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 11, 205, 362);
		contentPanel.add(scrollPane);
		
		mat_list = new JList<String>();
		mat_list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2)
					selectMaterial();
			}
		});
		scrollPane.setViewportView(mat_list);
		
		JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel.setBounds(225, 11, 527, 445);
		contentPanel.add(panel);
		panel.setLayout(null);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(10, 11, 183, 423);
		panel.add(scrollPane_1);
		
		prop_list = new JList<String>();
		prop_list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				propSelected();
			}
		});
		scrollPane_1.setViewportView(prop_list);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(203, 11, 314, 423);
		panel.add(scrollPane_2);
		
		prop_table = new JTable();
		scrollPane_2.setViewportView(prop_table);
		outwindow = in_outwindow;
		geom_manager = in_manager;
		dyno = new DynoImage();

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

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

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ok_hit = false;
				setVisible(false);
			}
		});
		cancelButton.setActionCommand("Cancel");
		buttonPane.add(cancelButton);
		
		//setup the lists of materials
		DefaultListModel<String> mat_model = new DefaultListModel<String>();
		mat_list.setModel(mat_model);
		for(int i=0;i<geom_manager.materialCnt();i++) {
			mat_model.addElement(geom_manager.getMaterial(i).matname);
		}
	}
	private void selectMaterial() {
		int index = mat_list.getSelectedIndex();
		DefaultListModel<String> prop_model = new DefaultListModel<String>();
		prop_list.setModel(prop_model);
		for(int i=0;i<Material.PROPNAMES.length;i++) {
			prop_model.addElement(Material.PROPNAMES[i]);
		}
		prop_list.setSelectedIndex(0);
	}
	private void propSelected() {
		if(mat_list.getSelectedIndex() >= 0 && prop_list.getSelectedIndex() >= 0) {
			DecimalFormat df = new DecimalFormat("#.###");
			String[] columns = new String[] {"Temp (C)",prop_list.getModel().getElementAt(prop_list.getSelectedIndex())};
			String[][] data = null;
			if(prop_list.getSelectedIndex() == 0) {
				data = new String[1][2];
				data[0][0] = df.format(25.0);
				data[0][1] = df.format(geom_manager.getMaterial(mat_list.getSelectedIndex()).density);
			}			
			if(prop_list.getSelectedIndex() == 1) {
				data = new String[geom_manager.getMaterial(mat_list.getSelectedIndex()).thermal_conductivity.length][2];
				for(int i=0;i<data.length;i++) {
					data[i][0] = df.format(geom_manager.getMaterial(mat_list.getSelectedIndex()).thermal_conductivity[i][0]);
					data[i][1] = df.format(geom_manager.getMaterial(mat_list.getSelectedIndex()).thermal_conductivity[i][1]);
				}
			}
			if(prop_list.getSelectedIndex() == 2) {
				data = new String[geom_manager.getMaterial(mat_list.getSelectedIndex()).specific_heat.length][2];
				for(int i=0;i<data.length;i++) {
					data[i][0] = df.format(geom_manager.getMaterial(mat_list.getSelectedIndex()).specific_heat[i][0]);
					data[i][1] = df.format(geom_manager.getMaterial(mat_list.getSelectedIndex()).specific_heat[i][1]);
				}
			}
			if(prop_list.getSelectedIndex() == 3) {
				data = new String[geom_manager.getMaterial(mat_list.getSelectedIndex()).coefficient_thermal_expansion.length][2];
				for(int i=0;i<data.length;i++) {
					data[i][0] = df.format(geom_manager.getMaterial(mat_list.getSelectedIndex()).coefficient_thermal_expansion[i][0]);
					data[i][1] = df.format(geom_manager.getMaterial(mat_list.getSelectedIndex()).coefficient_thermal_expansion[i][1]);
				}
			}
			if(prop_list.getSelectedIndex() == 4) {
				data = new String[geom_manager.getMaterial(mat_list.getSelectedIndex()).elastic_modulus.length][2];
				for(int i=0;i<data.length;i++) {
					data[i][0] = df.format(geom_manager.getMaterial(mat_list.getSelectedIndex()).elastic_modulus[i][0]);
					data[i][1] = df.format(geom_manager.getMaterial(mat_list.getSelectedIndex()).elastic_modulus[i][1]);
				}
			}
			DefaultTableModel dtm = new DefaultTableModel(data,columns);
			prop_table.setModel(dtm);
			this.invalidate();
			this.repaint();
		}
	}
}
