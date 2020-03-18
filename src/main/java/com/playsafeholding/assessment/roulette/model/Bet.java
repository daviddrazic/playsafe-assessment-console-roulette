package com.playsafeholding.assessment.roulette.model;

import java.math.BigDecimal;

/**
 * Model for a single bet.
 * 
 * @author daviddrazic
 *
 */
public class Bet {

	private String bet;
	private BigDecimal amount;
	private String outcome;
	private BigDecimal winnings;

	public Bet(String bet, BigDecimal amount) {
		this.bet = bet;
		this.amount = amount;
	}
	
	public String getBet() {
		return bet;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public String getOutcome() {
		return outcome;
	}

	public void setOutcome(String outcome) {
		this.outcome = outcome;
	}

	public BigDecimal getWinnings() {
		return winnings;
	}

	public void setWinnings(BigDecimal winnings) {
		this.winnings = winnings;
	}
}
