package com.playsafeholding.assessment.roulette.model;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Model for each Player.
 * 
 * @author daviddrazic
 *
 */
public class Player {

	private String name;
	private BigDecimal totalWin;
	private BigDecimal totalBet;
	private ArrayList<Bet> bets;

	public Player(String name) {
		this.name = name;
		this.bets = new ArrayList<Bet>();
		this.totalWin = new BigDecimal("0.0");
		this.totalBet = new BigDecimal("0.0");
	}

	public String getName() {
		return name;
	}

	public BigDecimal getTotalWin() {
		return totalWin;
	}

	public void setTotalWin(BigDecimal totalWin) {
		this.totalWin = totalWin;
	}

	public BigDecimal getTotalBet() {
		return totalBet;
	}

	public void setTotalBet(BigDecimal totalBet) {
		this.totalBet = totalBet;
	}

	public ArrayList<Bet> getBets() {
		return bets;
	}

	public void addBet(Bet bet) {
		this.totalBet.add(bet.getAmount());
		this.bets.add(bet);
	}
	
	public void clearBets() {
		this.bets = new ArrayList<Bet>();
	}
}
