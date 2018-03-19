package com.longjiabo.fund.bean;

public class FundSum {

	private String code;
	private long start;
	private long end;
	private double added;
	private int upDay;
	private int balanceDay;
	private int downDay;
	private String name;

	public void addUp(int i) {
		upDay += i;
	}

	public void addBalance(int i) {
		balanceDay += i;
	}

	public void addDown(int i) {
		downDay += i;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public double getAdded() {
		return added;
	}

	public void setAdded(double added) {
		this.added = added;
	}

	public int getUpDay() {
		return upDay;
	}

	public void setUpDay(int upDay) {
		this.upDay = upDay;
	}

	public int getBalanceDay() {
		return balanceDay;
	}

	public void setBalanceDay(int balanceDay) {
		this.balanceDay = balanceDay;
	}

	public int getDownDay() {
		return downDay;
	}

	public void setDownDay(int downDay) {
		this.downDay = downDay;
	}

	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}

	public long getEnd() {
		return end;
	}

	public void setEnd(long end) {
		this.end = end;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
