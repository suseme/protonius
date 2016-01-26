package com.misday.pg.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

public class PGExpress {
	public static double calc(String expr, Object data, VariableListener getVariableListener) {
		try {
			Stack<Double> stack = new Stack<Double>();
			ArrayList<Object> list = tokener(expr);
			for (Object obj : list) {
				// System.out.println(obj);
				if (obj instanceof String) {
					String s = (String) obj;
					// get value from tree;
					if (getVariableListener != null) {
						stack.push((double) getVariableListener.getVariableValue(s, data));
					} else {
						stack.push(0.0);
					}
				} else if (obj instanceof Double) {
					stack.push((Double) obj);
				} else if (obj instanceof Character) {
					char c = (Character) obj;
					Double b = stack.pop();
					Double a = stack.pop();
					switch (c) {
					case '+':
						stack.push(a + b);
						break;
					case '-':
						stack.push(a - b);
						break;
					case '*':
						stack.push(a * b);
						break;
					case '/':
						stack.push(a / b);
						break;
					}
				}
			}

			return stack.pop();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	public static ArrayList<Object> tokener(String exp) throws IOException {
		PGTokener tok = new PGTokener(exp);
		char c;
		Stack<Character> stack = new Stack<Character>();
		ArrayList<Object> list = new ArrayList<Object>();

		for (;;) {
			c = tok.nextClean();
			switch (c) {
			case 0:
				while (!stack.isEmpty()) {
					list.add(stack.pop());
				}
				return list;
			case '(':
				stack.push(c);
				break;
			case ')':
				while ((c = stack.pop()) != '(') {
					list.add(c);
				}
				break;

			case '@':
				list.add(tok.nextString('$'));
				break;

			case '$':
				break;

			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
			case '.':
				tok.back(); // back up a char
				list.add(tok.nextValue());
				break;

			case '+':
			case '-':
				while (!stack.isEmpty() && (stack.peek() == '*' || stack.peek() == '/' || stack.peek() == '+' || stack.peek() == '-')) {
					list.add(stack.pop());
				}

				stack.push(c);
				break;

			case '*':
			case '/':
				while (!stack.isEmpty() && (stack.peek() == '*' || stack.peek() == '/')) {
					list.add(stack.pop());
				}

				stack.push(c);
				break;

			default:
				break;
			}
		}
	}

	public interface VariableListener {
		public double getVariableValue(String name, Object data);
	}
}
