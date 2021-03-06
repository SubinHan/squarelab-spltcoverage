package lab.square.spltcoverage.core.antenna.model;

import java.util.Map;
import java.util.Objects;

public abstract class ExpressionNode {
	protected ExpressionNode left;
	protected ExpressionNode right;
	protected String value;
	
	abstract public boolean evaluate(Map<String, Boolean> featureSet);
	
	public String getValue() {
		return this.value;
	}
	
	public ExpressionNode getLeft() {
		return left;
	}
	
	public void setLeft(ExpressionNode left) {
		this.left = left;
	}
	
	public ExpressionNode getRight() {
		return right;
	}
	
	public void setRight(ExpressionNode right) {
		this.right = right;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o == null)
			return false;
		
		ExpressionNode node;
		try {
			node = (ExpressionNode)o;
		} catch (Exception e){
			return false;
		}
		
		boolean equals = true;
		if(this.left != null) {
			if(node.left != null) {
				if(!this.left.equals(node.left))
					equals = false;
			}
			else {
				equals = false;
			}
		}
		if(this.right != null) {
			if(node.right != null) {
				if(!this.right.equals(node.right))
					equals = false;
			}
			else {
				equals = false;
			}
		}
		if(!this.value.equals(node.value)) {
			equals = false;
		}
		
		return equals;
	}

	@Override
	public int hashCode() {
		if(isEndNode()) {
			return Objects.hash(value);
		}
		
		if(this.left == null)
			return this.right.hashCode();
		
		if(this.right == null)
			return this.left.hashCode();
		
		return Objects.hash(this.left.hashCode(), this.right.hashCode());
	}
	
	@Override
	public String toString() {
		String left = "";
		String right = "";
		
		if(this.left != null)
			left = this.left.toString();
		if(this.right != null)
			right = this.right.toString();
		
		return "(" + left + this.value + right + ")";
	}
	
	private boolean isEndNode() {
		return this.left == null && this.right == null;
	}
	
}
