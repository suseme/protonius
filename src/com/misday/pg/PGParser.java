package com.misday.pg;

import java.nio.ByteBuffer;
import java.util.List;

import com.misday.pg.util.TextUtils;
import com.misday.pg.util.TreeNode;
import com.misday.pg.util.PGExpress;

public class PGParser implements PGExpress.VariableListener {
	
	private boolean mBigedian = true;

	private String mData;
	private byte[] mDataArray;
	private String mPrtlStr;
	private PGPrtl mPGPrtl;
	private TreeNode mDataFieldRoot;
	private int index = 0;

	PGParser(boolean bigedian) {
		mBigedian = bigedian;
		mDataFieldRoot = new TreeNode();
	}

	public void setData(String data) {
		mData = data;
		mDataArray = toByteArray(mData);
	}

	public void setPrtl(String prtlStr) {
		mPrtlStr = prtlStr;
		mPGPrtl = new PGPrtl(prtlStr);
	}

	public void parse() {
		index = 0;
		if (mDataArray.length > 0) {
			parse(mDataFieldRoot, mPGPrtl.mPrtlTreeRoot.getChildList());
		}
	}

	public void parse(TreeNode dataFieldRoot, List<TreeNode> prtlFieldNodes) {
		PGPrtl.PrtlField prtlField;
		TreeNode dataFieldNode;
		TreeNode itemDataFieldNode;
		DataField dataField;
		int size = 0;

		for (TreeNode prtlFieldNode : prtlFieldNodes) {
			prtlField = (PGPrtl.PrtlField) prtlFieldNode.obj;
			if (prtlField != null) {
				dataFieldNode = new TreeNode();
				dataField = new DataField();
				dataFieldNode.obj = dataField;
				dataField.name = prtlField.name;
				dataField.type = prtlField.type;

				if (prtlField.numExp != null) {
					prtlField.num = (int) PGExpress.calc(prtlField.numExp, dataFieldRoot, this);
				}

				switch (prtlField.type) {
				case PGPrtl.TYPE_BYTE:
				case PGPrtl.TYPE_CHAR:
				case PGPrtl.TYPE_SHORT:
				case PGPrtl.TYPE_INT:
				case PGPrtl.TYPE_LONG:
				case PGPrtl.TYPE_STRING:
					size = prtlField.num * PGPrtl.getSize(prtlField.type);
					parseField(dataField, size);
					break;

				case PGPrtl.TYPE_ARRAY:
					size = prtlField.num;
					for (int i = 0; i < size; i++) {
						itemDataFieldNode = new TreeNode();
						parse(itemDataFieldNode, prtlFieldNode.getChildList());
						dataFieldNode.appendChildNode(itemDataFieldNode);
					}
					break;

				default:
					// throws an exception.
					throw new PGException("unknown type, type = " + prtlField.type);
				}

				dataFieldRoot.appendChildNode(dataFieldNode);
			}
		}
	}

	public void parseField(DataField dataField, int size) {
		if (size > 0) {
			dataField.data = new byte[size];
			System.arraycopy(mDataArray, index, dataField.data, 0, size);
			index += size;

			switch (dataField.type) {
			case PGPrtl.TYPE_BYTE:
			case PGPrtl.TYPE_CHAR:
			case PGPrtl.TYPE_SHORT:
			case PGPrtl.TYPE_INT:
			case PGPrtl.TYPE_LONG:
				dataField.value = mBigedian ? toIntBigEndian(dataField.data) : toIntLittleEndian(dataField.data);
				break;

			case PGPrtl.TYPE_STRING:
				dataField.value = new String(dataField.data, TextUtils.UTF8);
				break;

			case PGPrtl.TYPE_ARRAY:
				break;
			default:
				// throws an exception.
				throw new PGException("unknown type, type = " + dataField.type);
			}
		}
	}

	/***
	 * Get brother field value.
	 * 
	 * @param name
	 *            <in> target field name
	 * @param data
	 *            <in> parameter in
	 * @return value of the field by name
	 */
	@Override
	public double getVariableValue(String name, Object data) {
		TreeNode dataFieldRoot = (TreeNode) data;
		if (dataFieldRoot != null) {
			List<TreeNode> brotherNodes = dataFieldRoot.getChildList();
			DataField dataField = null;
			if (brotherNodes != null) {
				for (TreeNode brotherNode : brotherNodes) {
					dataField = (DataField) brotherNode.obj;
					if (TextUtils.equal(name, dataField.name)) {
						return (Integer) dataField.value;
					}
				}
			}
		}

		return 0;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();

		// sb.append(mPGPrtl);
		// sb.append("\n--------\n");

		showChilds(mDataFieldRoot.getChildList(), sb);

		return sb.toString();
	}

	public void showChilds(List<TreeNode> treeNodes, StringBuffer strBuf) {
		DataField dataField;

		for (TreeNode treeNode : treeNodes) {
			dataField = (DataField) treeNode.obj;

			strBuf.append(dataField);
			strBuf.append("\n");

			if (!treeNode.isLeaf()) {
				showChilds(treeNode.getChildList(), strBuf);
			}
		}
	}

	public static int toIntBigEndian(byte[] array) {
		int val = 0;

		for (byte a : array) {
			val <<= 8;
			val |= a & 0xff;
		}

		return val;
	}
	
	public static int toIntLittleEndian(byte[] array) {
		int val = 0;
		int a = 0;
		
		for (int i = 0; i < array.length; i++) {
			a = array[i];
			a <<= (8 * i);
			val |= a;
		}

		return val;
	}

	public static byte[] toByteArray(String data) {
		data = data.replaceAll("\r", "");
		data = data.replaceAll("\n", "");
		data = data.replaceAll(" ", "");

		int length = data.length();
		ByteBuffer buf = ByteBuffer.allocate(length);

		char c;
		int b = 0;
		boolean meet = false;
		for (int i = 0; i < length; i++) {
			c = data.charAt(i);

			if (',' == c) {
				if (meet) {
					// add to buffer
					buf.put((byte) b);
					// clear state and data
					meet = false;
					b = 0;
				}
			} else if ('0' <= c && c <= '9') {
				b <<= 4;
				b |= (c - '0') & 0x0f;
				meet = true;
			} else if (('a' <= c && c <= 'f') || ('A' <= c && c <= 'F')) {
				b <<= 4;
				b |= (c - 'a' + 0xa) & 0xf;
				meet = true;
			} else {
				// ignore it
			}
		}

		if (meet) {
			// add to buffer
			buf.put((byte) b);
			// clear state and data
			meet = false;
			b = 0;
		}

		return buf.array();
	}

	private class DataField {
		String name;
		byte[] data;
		int type;
		Object value;

		public String toString() {
			StringBuffer sb = new StringBuffer();

			sb.append(name + ":\n  ");
			switch (type) {
			case PGPrtl.TYPE_BYTE:
			case PGPrtl.TYPE_SHORT:
			case PGPrtl.TYPE_INT:
			case PGPrtl.TYPE_LONG:
				sb.append(value);
				showByte(data, sb);
				break;
			case PGPrtl.TYPE_CHAR:
				if (data != null && data.length > 0) {
					for (byte d : data) {
						sb.append(String.format("%c", d));
					}
				}
				showByte(data, sb);
				break;
			case PGPrtl.TYPE_STRING:
				sb.append(value);
				showByte(data, sb);
				break;

			case PGPrtl.TYPE_ARRAY:
				// TODO: ???
				break;
			}

			return sb.toString();
		}

		private void showByte(byte[] bytes, StringBuffer sb) {
			sb.append(" [");
			if (data != null && data.length > 0) {
				for (byte d : data) {
					sb.append(String.format("%02X, ", d));
				}
			}
			sb.append("]");
		}
	}

}
