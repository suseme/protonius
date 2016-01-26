package com.misday.pg;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

import com.misday.pg.util.TreeNode;

public class PGPrtl {
	public static final int TYPE_UNKNOWN = 0;
	public static final int TYPE_BYTE = 1;
	public static final int TYPE_CHAR = 2;
	public static final int TYPE_SHORT = 3;
	public static final int TYPE_INT = 4;
	public static final int TYPE_LONG = 5;
	public static final int TYPE_STRING = 6;
	public static final int TYPE_ARRAY = 7;
	
	public static final String KEY_TYPE = "type";
	public static final String KEY_SIZE = "size";
	public static final String KEY_ITEM = "item";

	public static Map<String, Integer> keywords;
	TreeNode mPrtlTreeRoot;
	JSONObject mPrtlJson;

	static {
		keywords = new HashMap<String, Integer>();

		keywords.put("byte", TYPE_BYTE);
		keywords.put("char", TYPE_CHAR);
		keywords.put("short", TYPE_SHORT);
		keywords.put("int", TYPE_INT);
		keywords.put("long", TYPE_LONG);
		keywords.put("string", TYPE_STRING);
		keywords.put("array", TYPE_ARRAY);
	};

	public static int getType(String value) {
		Set<String> keys = keywords.keySet();
		for (String key : keys) {
			if (value.startsWith(key)) {
				return keywords.get(key);
			}
		}
		return TYPE_UNKNOWN;
	}

	public static String getTypeName(int type) {
		Set<String> keys = keywords.keySet();
		for (String key : keys) {
			if (type == keywords.get(key)) {
				return key;
			}
		}

		return "UNKNOWN";
	}

	public static int getSize(int type) {
		switch (type) {
		case PGPrtl.TYPE_BYTE:
			return 1;
		case PGPrtl.TYPE_CHAR:
			return 1;
		case PGPrtl.TYPE_SHORT:
			return 2;
		case PGPrtl.TYPE_INT:
			return 4;
		case PGPrtl.TYPE_LONG:
			return 4;
		case PGPrtl.TYPE_STRING:
			return 1;
		case PGPrtl.TYPE_ARRAY:
			return 1;
		default:
			throw new PGException("unknown type, type = " + type);
		}
	}

	public PGPrtl(String strJson) {
		mPrtlTreeRoot = new TreeNode();
		mPrtlJson = new JSONObject(strJson);

		build(mPrtlTreeRoot, mPrtlJson);
	}

	public void build(TreeNode root, JSONObject json) throws PGException {
		Object value;
		TreeNode treeNode;
		PrtlField prtlField;

		final List<String> keys = json.keyList();

		for (String key : keys) {
			treeNode = new TreeNode();
			prtlField = new PrtlField();
			treeNode.obj = prtlField;
			root.addChildNode(treeNode);
			treeNode.setNodeName(key);
			prtlField.name = key;

			value = json.get(key);
			if (value instanceof JSONObject) {
				JSONObject jsonNode = (JSONObject) value;

				// type attribute
				String type = jsonNode.getString(KEY_TYPE);
				prtlField.type = getType(type);

				// size attribute
				value = jsonNode.get(KEY_SIZE);
				if (value instanceof String) { // expression
					prtlField.num = 0;
					prtlField.numExp = (String) value;
				} else if (value instanceof Integer) {
					prtlField.num = (Integer) value;
					prtlField.numExp = null;
				} else {
					// throw an exception.
					throw new PGException("size type unknown, now type is " + value.getClass().getName());
				}

				// item (optional)
				if (prtlField.type == PGPrtl.TYPE_ARRAY) {
					value = jsonNode.get(KEY_ITEM);
					if (value instanceof JSONObject) {
						build(treeNode, (JSONObject) value);
					} else {
						// throw an exception.
						throw new PGException("item type unknown, now type is " + value.getClass().getName());
					}
				}
			} else {
				// throw an exception.
				throw new PGException("field attribute type unknown, now type is " + value.getClass().getName());
			}
		}
	}

	public String toString() {
		StringBuffer strBuf = new StringBuffer();

		strBuf.append(mPrtlJson.toString());

		strBuf.append("\n--------\n");

		showChilds(mPrtlTreeRoot.getChildList(), strBuf);

		return strBuf.toString();
	}

	public void showChilds(List<TreeNode> childs, StringBuffer strBuf) {
		PrtlField prtlNode;

		for (TreeNode child : childs) {
			prtlNode = (PrtlField) child.obj;

			strBuf.append(prtlNode);
			strBuf.append("\n");

			if (!child.isLeaf()) {
				showChilds(child.getChildList(), strBuf);
			}
		}
	}

	public class PrtlField {
		public String name;
		public String numExp;
		public int type;
		public int num;

		public String toString() {
			StringBuffer sb = new StringBuffer();

			sb.append(name + ":");
			sb.append("\n  ");
			sb.append("type:" + getTypeName(type));
			sb.append("\n  ");
			sb.append("num:" + num);
			sb.append("\n  ");
			sb.append("numExp:" + numExp);

			return sb.toString();
		}
	}
}
