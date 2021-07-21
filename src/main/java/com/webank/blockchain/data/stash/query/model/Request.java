package com.webank.blockchain.data.stash.query.model;

public class Request<T> {
	private String op;
	private T params;
	
	public String getOp() {
		return op;
	}
	public void setOp(String op) {
		this.op = op;
	}
	public T getParams() {
		return params;
	}
	public void setParams(T params) {
		this.params = params;
	}
}
