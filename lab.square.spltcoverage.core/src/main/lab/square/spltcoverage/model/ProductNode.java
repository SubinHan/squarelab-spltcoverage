package lab.square.spltcoverage.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.jacoco.core.analysis.IClassCoverage;

import lab.square.spltcoverage.utils.Tools;

public class ProductNode {
	private Collection<ProductNode> parents;
	private Collection<ProductNode> children;
	private FeatureSet featureSet;
	private ProductCoverage productCoverage;
	private int level;
	
	public ProductNode() {
		this.parents = new HashSet<>();
		this.children = new HashSet<>();
		this.featureSet = new FeatureSet();
	}
	
	public ProductNode(FeatureSet featureSet) {
		this();
		this.featureSet = featureSet;
	}
	
	public ProductNode(ProductCoverage product) {
		this();
		this.productCoverage = product;
		if(product != null)
			this.featureSet = product.getFeatureSet();
	}
	
	public void addParent(ProductNode parent) {
		if(!parents.contains(parent))
			parents.add(parent);
	}
	
	public void addChild(ProductNode child) {
		if(!children.contains(child))
			children.add(child);
	}
	
	public void setLevel(int level) {
		this.level = level;
	}
	
	public ProductCoverage getProductCoverage() {
		return this.productCoverage;
	}
	
	public Collection<ProductNode> getChildren(){
		return this.children;
	}
	
	public Collection<ProductNode> getParents(){
		return this.parents;
	}
	
	public int getLevel() {
		return this.level;
	}	
	
	public FeatureSet getFeatureSet(){
		return this.featureSet;
	}
	
	public boolean isCoveredMoreThanParent() {
		if(this.parents == null)
			return false;
		if(this.parents.isEmpty())
			return false;
		if(productCoverage == null)
			return false;
		
		for(ProductNode parent : this.parents) {
			if(parent == null)
				continue;
			
			if (productCoverage.equals(parent.getProductCoverage())) {
				return false;
			}
		}		
		
		return true;
	}

	private double findLineRatioWtihClassName(ProductNode parent, IClassCoverage cc) {
		Collection<IClassCoverage> parentClassCoverages = parent.getProductCoverage().getClassCoverages();
		
		for(IClassCoverage pcc : parentClassCoverages) {
			if(pcc.getName().equals(cc.getName()))
				return pcc.getLineCounter().getCoveredRatio();
		}
		
		return 0.f;
	}

	public void fillCoverageRecursivelyIfEmpty(SplCoverage splCoverage) {
		if(this.productCoverage != null)
			return;
		
		this.productCoverage = findProductCoverage(splCoverage);
		
		for(ProductNode child : this.getChildren()) {
			child.fillCoverageRecursivelyIfEmpty(splCoverage);
		}
	}

	private ProductCoverage findProductCoverage(SplCoverage splCoverage) {
		for(ProductCoverage each : splCoverage.getProductCoverages()) {
			if (featureSet.equals(each.getFeatureSet())) {
				return each;
			}
		}
		
		return null;
	}
	

}
