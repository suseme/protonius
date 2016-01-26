package com.misday.pg;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import java.awt.Toolkit;
import java.awt.Component;

import javax.swing.ScrollPaneConstants;

import com.misday.pg.util.PGExpress;
import com.misday.pg.util.TextUtils;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JRadioButton;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import java.awt.Dimension;

public class PGFrame extends JFrame {

	private JPanel contentPane;
	private JTable table;

	private JTextArea textArea_data;
	private JTextArea textArea_plain;

	private PGFrameModel frameModel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PGFrame frame = new PGFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public PGFrame() {
		frameModel = new PGFrameModel();

		setIconImage(Toolkit.getDefaultToolkit().getImage(PGFrame.class.getResource("/javax/swing/plaf/metal/icons/ocean/info.png")));
		setTitle("ProtoGenius");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));

		JPanel panel = new JPanel();
		panel.setAlignmentX(Component.LEFT_ALIGNMENT);
		contentPane.add(panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		JPanel panel_param = new JPanel();
		panel_param.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.add(panel_param);
		panel_param.setLayout(new BoxLayout(panel_param, BoxLayout.X_AXIS));
		
				JComboBox comboBox_encoding = new JComboBox();
				comboBox_encoding.setMaximumSize(new Dimension(200, 50));
				comboBox_encoding.setMaximumRowCount(1);
				comboBox_encoding.setModel(new DefaultComboBoxModel(new String[] { "UTF-8" }));
				comboBox_encoding.setSelectedIndex(0);
				panel_param.add(comboBox_encoding);
		final JRadioButton rdbtnBigendian = new JRadioButton("BigEndian");
		panel_param.add(rdbtnBigendian);
		rdbtnBigendian.setSelected(true);
		ButtonGroup endian_type = new ButtonGroup();
		endian_type.add(rdbtnBigendian);

		JRadioButton rdbtnLittleendian = new JRadioButton("LittleEndian");
		panel_param.add(rdbtnLittleendian);
		endian_type.add(rdbtnLittleendian);

		JPanel panel_data = new JPanel();
		panel_data.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.add(panel_data);
		panel_data.setLayout(new BoxLayout(panel_data, BoxLayout.X_AXIS));

		JScrollPane scrollPane_data = new JScrollPane();
		scrollPane_data.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		panel_data.add(scrollPane_data);

		textArea_data = new JTextArea();
		textArea_data.setRows(6);
		textArea_data.setLineWrap(true);
		scrollPane_data.setViewportView(textArea_data);

		JButton btn_parse = new JButton(">>>");
		panel_data.add(btn_parse);
		btn_parse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int row = table.getSelectedRow();
				if (row < 0) {
					// show popup
					JOptionPane.showMessageDialog(null, "No protocol selected ;-(", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}

				String data = textArea_data.getText();
				// TODO: big/little endian select.
				PGParser pg = new PGParser(rdbtnBigendian.isSelected());
				pg.setData(data);
				String defn = (String) (table.getModel().getValueAt(row, 2));
				pg.setPrtl(defn);
				pg.parse();
				textArea_plain.setText(pg.toString());
			}
		});

		JScrollPane scrollPane_plain = new JScrollPane();
		panel_data.add(scrollPane_plain);

		textArea_plain = new JTextArea();
		textArea_plain.setRows(6);
		textArea_plain.setLineWrap(true);
		scrollPane_plain.setViewportView(textArea_plain);

		JPanel panel_cfg = new JPanel();
		panel.add(panel_cfg);
		panel_cfg.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel_cfg.setLayout(new BoxLayout(panel_cfg, BoxLayout.X_AXIS));

		JScrollPane scrollPane_cfg = new JScrollPane();
		panel_cfg.add(scrollPane_cfg);

		table = new JTable();
		scrollPane_cfg.setViewportView(table);
		table.setModel(frameModel.loadData());
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		frameModel.setTableWeightAndHeight(table);
	}
}
