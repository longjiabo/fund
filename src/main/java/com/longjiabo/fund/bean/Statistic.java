package com.longjiabo.fund.bean;

import com.longjiabo.fund.model.fund.History;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Statistic {

	private String code;
	private String name;
	private int days;
	private Date start;
	private Date end;
	private List<History> historys;
	private int upDays;
	private int downDays;
	private int holdDays;
	private double maxUpAmount;
	private double maxDownAmount;
	private int best;
	private int worst;
	private double average;
	private double standand;
	private int[] rank;
	private double[] amount;
	private List<Long> dates;
	private double totalAmount;

	public void addHistory(History h) {
		if (historys == null) {
			historys = new ArrayList<History>();
		}
		historys.add(h);
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getDays() {
		return days;
	}

	public void setDays(int days) {
		this.days = days;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public List<History> getHistorys() {
		return historys;
	}

	public void setHistorys(List<History> historys) {
		this.historys = historys;
	}

	public int getUpDays() {
		return upDays;
	}

	public void setUpDays(int upDays) {
		this.upDays = upDays;
	}

	public int getDownDays() {
		return downDays;
	}

	public void setDownDays(int downDays) {
		this.downDays = downDays;
	}

	public int getHoldDays() {
		return holdDays;
	}

	public void setHoldDays(int holdDays) {
		this.holdDays = holdDays;
	}

	public double getMaxUpAmount() {
		return maxUpAmount;
	}

	public void setMaxUpAmount(double maxUpAmount) {
		this.maxUpAmount = maxUpAmount;
	}

	public double getMaxDownAmount() {
		return maxDownAmount;
	}

	public void setMaxDownAmount(double maxDownAmount) {
		this.maxDownAmount = maxDownAmount;
	}

	public int getBest() {
		return best;
	}

	public void setBest(int best) {
		this.best = best;
	}

	public int getWorst() {
		return worst;
	}

	public void setWorst(int worst) {
		this.worst = worst;
	}

	public double getStandand() {
		return standand;
	}

	public void setStandand(double standand) {
		this.standand = standand;
	}

	public int[] getRank() {
		return rank;
	}

	public void setRank(int[] rank) {
		this.rank = rank;
	}

	public double[] getAmount() {
		return amount;
	}

	public void setAmount(double[] amount) {
		this.amount = amount;
	}

	public List<Long> getDates() {
		return dates;
	}

	public void setDates(List<Long> dates) {
		this.dates = dates;
	}

	public double getAverage() {
		return average;
	}

	public void setAverage(double average) {
		this.average = average;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

}
