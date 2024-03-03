package expessionCalculation;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class CalculateUtil {

	List<String> variable;

	List<String> variableVal;

	LinkedList<String> formula;

	public List<String> getVariable() {
		return variable;
	}

	public List<String> getVariableVal() {
		return variableVal;
	}

	public List<String> getFormula() {
		return formula;
	}

	private void setVariable(String formula) {
		List<String> formulaList = Arrays.asList(formula.split(""));
		this.variable = new ArrayList<String>();

		StringBuilder variableName = new StringBuilder();
		int length = formulaList.size();
		String regex = "[a-zA-Z]";

		for (int i = 0; i < length; i++) {
			if (formulaList.get(i).matches(regex)) {
				variableName.append(formulaList.get(i));
				if (i == length - 1) {
					if (!this.variable.contains(variableName.toString())) {
						this.variable.add(variableName.toString());
					}
					variableName = new StringBuilder();
				}
			} else if (!variableName.isEmpty()) {
				if (!this.variable.contains(variableName.toString())) {
					this.variable.add(variableName.toString());
				}
				variableName = new StringBuilder();
			}
		}
	}

	private LinkedList<String> setPartFormula(String formula) {
//		((10000+2)*(2+2))/0.5+0^5
		List<String> formulaList = Arrays.asList(formula.split(""));
		LinkedList<String> partFormula = new LinkedList<String>();
		String function = "";
		boolean milti = false;
		int countLeftParentheses = 0;
		int countRightParentheses = 0;
		for (int i = 0; i < formulaList.size(); i++) {
			String s = formulaList.get(i);

			if (s.equals("(")) {
				if (countLeftParentheses != 0) {
					function += s;
				}
				countLeftParentheses++;
				milti = true;
			} else if (s.equals(")")) {
				countRightParentheses++;
				if (countRightParentheses != countLeftParentheses) {
					function += s;
				}
				if (countLeftParentheses == countRightParentheses) {
					partFormula.add(function);
					milti = false;
					function = "";
					countLeftParentheses = 0;
					countRightParentheses = 0;
				}

			} else {
				if (milti) {
					function += s;
				} else {
					if (isOperation(s)) {
						partFormula.add(s);
					} else {
						int nextIndex = i + 1;
						if (nextIndex < formulaList.size() && !isOperation(formulaList.get(nextIndex)))
							function += s;
						else {
							function += s;
							partFormula.add(function);
							function = "";
						}
					}
				}
			}
		}
		System.out.println(partFormula);
		return partFormula;
	}

	private void setFormula(String formula) {
		List<String> formulaList = Arrays.asList(formula.split(""));
		this.formula = new LinkedList<String>();
		String function = "";
		boolean milti = false;
		int countLeftParentheses = 0;
		int countRightParentheses = 0;
		System.out.println(formulaList);
		for (int i = 0; i < formulaList.size(); i++) {
			String s = formulaList.get(i);

			if (s.equals("(")) {
				if (countLeftParentheses != 0) {
					function += s;
				}
				countLeftParentheses++;
				milti = true;
			} else if (s.equals(")")) {
				countRightParentheses++;
				if (countRightParentheses != countLeftParentheses) {
					function += s;
				}
				if (countLeftParentheses == countRightParentheses) {
					this.formula.add(function);
					milti = false;
					function = "";
					countLeftParentheses = 0;
					countRightParentheses = 0;
				}
			} else {

				if (milti) {
					function += s;
				} else {
					if (isOperation(s)) {
						this.formula.add(s);
					} else {
						int nextIndex = i + 1;
						if (nextIndex < formulaList.size() && !isOperation(formulaList.get(nextIndex)))
							function += s;
						else {
							function += s;
							this.formula.add(function);
							function = "";
						}
					}
				}
			}
		}

	}

	private boolean isOperation(String s) {
		switch (s) {
		case "+":
			return true;
		case "-":
			return true;
		case "*":
			return true;
		case "/":
			return true;
		case "^":
			return true;
		default:
			return false;
		}
	}

	private void setVariableVal(Map<String, String> vals) {
		this.variableVal = new ArrayList<String>();
		for (String variableName : this.variable) {
			String val = vals.get(variableName);
			if (val != null) {
				this.variableVal.add(val);
			}
		}
	}

	private String runFormula() {

		for (int i = 0; i < this.formula.size(); i++) {
			String function = this.formula.get(i);
			try {
				if (!isOperation(function)) {
					new BigDecimal(function);
				}
			} catch (Exception e) {
				this.formula.remove(i);
				String regex = ".*[+\\-*/^].*";

				Pattern pattern = Pattern.compile(regex);
				if (pattern.matcher(function).matches()) {
					function = culculatePartExpression(function);
				} else {
					function = putVariable(function);
				}
				this.formula.add(i, function);

			}
		}
		System.out.println(this.formula);
		BigDecimal filnalResult = BigDecimal.ZERO;

		calculatPow(this.formula);
		calculatMultiply(this.formula);
		calculatDivide(this.formula);
		System.out.println(this.formula);
		filnalResult = filnalResult.add(calculatAdd(this.formula));
		filnalResult = filnalResult.add(calculatMinus(this.formula));

		System.out.println(filnalResult);
		return filnalResult.toString();
	}

	private String putVariable(String val) {
		String num = val;
		if (this.variable.contains(val)) {
			int index = this.variable.indexOf(val);
			num = this.variableVal.get(index);
		}
		return num.toString();
	}

	private String culculatePartExpression(String fraction) {
		String newExpression = "";
		for (int i = 0; i < fraction.length(); i++) {
			newExpression += putVariable(String.valueOf(fraction.charAt(i)));
		}
		System.out.println(fraction);
		BigDecimal filnalResult = BigDecimal.ZERO;

		LinkedList<String> partFormula = setPartFormula(newExpression);
		for (int i = 0; i < partFormula.size(); i++) {
			String ele = partFormula.get(i);
			if (!isOperation(ele)) {
				if (ele.contains("+")) {
					partFormula.remove(i);
					partFormula.add(i, calculatAdd(ele));
				} else if (ele.contains("-")) {
					partFormula.remove(i);
					partFormula.add(i, calculatMinus(ele));
				} else if (ele.contains("*")) {
					partFormula.remove(i);
					partFormula.add(i, calculatMutiply(ele));
				} else if (ele.contains("/")) {
					partFormula.remove(i);
					partFormula.add(i, calculatDivide(ele));
				} else if (ele.contains("^")) {
					partFormula.remove(i);
					partFormula.add(i, calculatPow(ele));
				}
			}
		}

		calculatPow(partFormula);
		calculatMultiply(partFormula);
		calculatDivide(partFormula);
		filnalResult = filnalResult.add(calculatAdd(partFormula));
		filnalResult = filnalResult.add(calculatMinus(partFormula));

		if (partFormula.size() == 1) {
			return partFormula.get(0);
		}
		return filnalResult.toString();
	}

	private void calculatPow(LinkedList<String> partFormula) {
		int startIndex = 0;
		int endIndex = 0;
		BigDecimal result = BigDecimal.ZERO;
		System.out.println(partFormula);
		while (partFormula.contains("^")) {
			int operationIndex = partFormula.indexOf("^");
			startIndex = operationIndex - 1;
			endIndex = operationIndex + 1;
			String baseString = partFormula.get(startIndex);
			String exponentString = partFormula.get(endIndex);

			BigDecimal base = new BigDecimal(putVariable(baseString));
			BigDecimal exponent = new BigDecimal(putVariable(exponentString));

			result = new BigDecimal(Math.pow(base.doubleValue(), exponent.doubleValue()), MathContext.DECIMAL128);

			partFormula.remove(startIndex);
			partFormula.remove(startIndex);
			partFormula.remove(startIndex);
			partFormula.add(startIndex, result.toString());
		}
	}

	private void calculatMultiply(LinkedList<String> partFormula) {
		int startIndex = 0;
		int endIndex = 0;
		BigDecimal result = BigDecimal.ZERO;
		while (partFormula.contains("*")) {
			int operationIndex = partFormula.indexOf("*");
			startIndex = operationIndex - 1;
			endIndex = operationIndex + 1;
			String baseString = partFormula.get(startIndex);
			String exponentString = partFormula.get(endIndex);

			BigDecimal base = new BigDecimal(putVariable(baseString));
			BigDecimal exponent = new BigDecimal(putVariable(exponentString));

			result = base.multiply((exponent), MathContext.DECIMAL128);

			partFormula.remove(startIndex);
			partFormula.remove(startIndex);
			partFormula.remove(startIndex);
			partFormula.add(startIndex, result.toString());
		}
	}

	private void calculatDivide(LinkedList<String> partFormula) {
		int startIndex = 0;
		int endIndex = 0;
		BigDecimal result = BigDecimal.ZERO;
		while (partFormula.contains("/")) {
			int operationIndex = partFormula.indexOf("/");
			startIndex = operationIndex - 1;
			endIndex = operationIndex + 1;
			String baseString = partFormula.get(startIndex);
			String exponentString = partFormula.get(endIndex);

			BigDecimal base = new BigDecimal(putVariable(baseString));
			BigDecimal exponent = new BigDecimal(putVariable(exponentString));

			result = base.divide((exponent), MathContext.DECIMAL128);

			partFormula.remove(startIndex);
			partFormula.remove(startIndex);
			partFormula.remove(startIndex);
			partFormula.add(startIndex, result.toString());
		}
	}

	private BigDecimal calculatAdd(LinkedList<String> partFormula) {
		int startIndex = 0;
		int endIndex = 0;
		BigDecimal result = BigDecimal.ZERO;
		while (partFormula.contains("+")) {
			int operationIndex = partFormula.indexOf("+");
			startIndex = operationIndex - 1;
			endIndex = operationIndex + 1;
			String baseString = partFormula.get(startIndex);
			String exponentString = partFormula.get(endIndex);

			BigDecimal base = new BigDecimal(putVariable(baseString));
			BigDecimal exponent = new BigDecimal(putVariable(exponentString));

			result = base.add((exponent), MathContext.DECIMAL128);

			partFormula.remove(startIndex);
			partFormula.remove(startIndex);
			partFormula.remove(startIndex);
			partFormula.add(startIndex, result.toString());
		}
		return result;
	}

	private String calculatMinus(String partFormula) {
		BigDecimal result = BigDecimal.ZERO;
		String[] numString = partFormula.split("-");
		BigDecimal front = new BigDecimal(putVariable(numString[0]));
		BigDecimal behind = new BigDecimal(putVariable(numString[1]));
		result = front.subtract(behind, MathContext.DECIMAL128);
		return result.toString();
	}

	private String calculatMutiply(String partFormula) {
		BigDecimal result = BigDecimal.ONE;
		String[] numString = partFormula.split("\\*");
		for (String s : numString) {
			result = result.multiply(new BigDecimal(s), MathContext.DECIMAL128);
		}
		return result.toString();
	}

	private String calculatDivide(String partFormula) {
		BigDecimal result = BigDecimal.ZERO;
		String[] numString = partFormula.split("/");
		BigDecimal divideChild = new BigDecimal(putVariable(numString[0]));
		BigDecimal divideParent = new BigDecimal(putVariable(numString[1]));
		result = divideChild.divide(divideParent, MathContext.DECIMAL128);
		return result.toString();
	}

	private String calculatPow(String partFormula) {
		BigDecimal result = BigDecimal.ZERO;
		String[] numString = partFormula.split("^");
		BigDecimal base = new BigDecimal(putVariable(numString[0]));
		BigDecimal exponent = new BigDecimal(putVariable(numString[1]));
		result = new BigDecimal(Math.pow(base.doubleValue(), exponent.doubleValue()), MathContext.DECIMAL128);

		return result.toString();
	}

	private String calculatAdd(String partFormula) {
		BigDecimal result = BigDecimal.ZERO;
		String[] numString = partFormula.split("\\+");
		for (String s : numString) {
			result = result.add(new BigDecimal(s));
		}
		return result.toString();
	}

	private BigDecimal calculatMinus(LinkedList<String> partFormula) {
		int startIndex = 0;
		int endIndex = 0;
		BigDecimal result = BigDecimal.ZERO;
		while (partFormula.contains("-")) {
			int operationIndex = partFormula.indexOf("-");
			startIndex = operationIndex - 1;
			endIndex = operationIndex + 1;
			String baseString = partFormula.get(startIndex);
			String exponentString = partFormula.get(endIndex);

			BigDecimal base = new BigDecimal(putVariable(baseString));
			BigDecimal exponent = new BigDecimal(putVariable(exponentString));

			result = base.subtract((exponent), MathContext.DECIMAL128);

			partFormula.remove(startIndex);
			partFormula.remove(startIndex);
			partFormula.remove(startIndex);
			partFormula.add(startIndex, result.toString());
		}
		return result;
	}

	public static void main(String[] args) {
		CalculateUtil calculate = new CalculateUtil();
		String formula = "(DWT+GT)*(y+2)/0.5+20^5/x+2132*2+DWT^(-0.477)+GT^(-0.477)";
		calculate.setFormula(formula);
		calculate.setVariable(formula);
		
		System.out.println(calculate.getFormula());
		
		Map<String, String> variableMap = new HashMap<String, String>();
		List<String> varList = calculate.getVariable();
		for (String val : varList) {
			switch (val) {
			case "DWT":
				variableMap.put(val, "20000");
				break;
			case "GT":
				variableMap.put(val, "35000");
				break;
			case "x":
				variableMap.put(val, "7000");
				break;
			case "y":
				variableMap.put(val, "100");
				break;

			default:
				break;
			}
		}
		calculate.setVariableVal(variableMap);

		calculate.runFormula();
	}
}