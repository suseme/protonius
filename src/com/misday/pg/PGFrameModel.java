package com.misday.pg;
import java.io.File;
import java.io.IOException;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class PGFrameModel {
	private static final String TAG = "PGFrameModel";

	public DefaultTableModel loadData() {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		DefaultTableModel model = new DefaultTableModel(new String[] { "#", "Name", "Protocol" }, 0) {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new File("protocol_definition.xml"));

			NodeList list = doc.getElementsByTagName("item");

			for (int i = 0; i < list.getLength(); i++) {
				Element item = (Element) list.item(i);

				String name = item.getElementsByTagName("name").item(0).getFirstChild().getNodeValue();
				// Log.d(TAG, "name:" + name);
				String data = item.getElementsByTagName("defn").item(0).getFirstChild().getNodeValue();
				// Log.d(TAG, "data:" + data);

				Object[] rowData = new Object[] { i + 1, name, data };
				model.addRow(rowData);
			}
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return model;
	}

	public void setTableWeightAndHeight(JTable table) {
		int[][] width = { { 30, 40, 20 }, { 250, 400, 100 }, { 800, 2800, 100 }, };

		for (int i = 0; i < width.length; i++) {
			table.getColumnModel().getColumn(i).setPreferredWidth(width[i][0]);
			table.getColumnModel().getColumn(i).setMaxWidth(width[i][1]);
			table.getColumnModel().getColumn(i).setMinWidth(width[i][2]);
		}

		table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
	}
}
