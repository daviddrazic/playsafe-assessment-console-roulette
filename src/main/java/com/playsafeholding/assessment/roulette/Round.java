package com.playsafeholding.assessment.roulette;

import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.playsafeholding.assessment.roulette.model.Bet;
import com.playsafeholding.assessment.roulette.model.Player;

/**
 * Represents a betting round for a list of loaded players.
 * Winnings are calculated and bets cleared every 30 seconds.
 * 
 * @author daviddrazic
 *
 */
@Configuration
@EnableAsync
@EnableScheduling
public class Round {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Round.class);
	public static final String betPrompt = "Enter your bet in the format NAME BET AMOUNT\nor type END to finish: ";
	private final String playerDelimeter = ",";
	private final String betDelimeter = " ";
	private final String WIN = "WIN";
	private final String LOSE = "LOSE";
	private final BigDecimal loseAmount = new BigDecimal("0.0");

	private ArrayList<Player> players;
		
	public Round() {
		this.players = createPlayersFromFile(playerDelimeter);
	}
	
	@Async
	@Scheduled(fixedRate = 30000)
	public void endRound() {
		int winningNumber = new Random().nextInt(37);
		calculateWinnings(winningNumber);
		printRoundBets(winningNumber);
		printPlayerTotals();
		clearBets();
	}
	
	private void calculateWinnings(int winningNumber) {
		for (Player player:players) {
			for (Bet bet:player.getBets()) {
				if (StringUtils.isNumeric(bet.getBet()) && Integer.parseInt(bet.getBet()) == winningNumber) {
					bet.setOutcome(WIN);
					bet.setWinnings(bet.getAmount().multiply(new BigDecimal("36")));
				} else if (bet.getBet().toLowerCase().equals("even") && winningNumber % 2 == 0) {
					bet.setOutcome(WIN);
					bet.setWinnings(bet.getAmount().multiply(new BigDecimal("2")));
				} else if (bet.getBet().toLowerCase().equals("odd") && winningNumber % 2 != 0) {
					bet.setOutcome(WIN);
					bet.setWinnings(bet.getAmount().multiply(new BigDecimal("2")));
				} else {
					bet.setOutcome(LOSE);
					bet.setWinnings(loseAmount);
				}
				player.setTotalBet(player.getTotalBet().add(bet.getAmount()));
				player.setTotalWin(player.getTotalWin().add(bet.getWinnings()));
			}
		}		
	}
	
	public void addBet(String[] currentBet) {
		if (validateNewBet(currentBet)) {
			boolean playerFound = false;
			for (Player player:players) {
				if (player.getName().equals(currentBet[0])) {
					playerFound = true;
					player.addBet(new Bet(currentBet[1], new BigDecimal(currentBet[2])));
					break;
				}
			}
			if (!playerFound) {
				LOGGER.error("PLAYER NOT VALID");
				return;
			}
			LOGGER.info("BET PLACED");
		}
	}
	
	private boolean validateNewBet(String[] currentBet) {
		if (currentBet == null || currentBet.length != 3) {
			System.out.printf("INVALID ENTRY");
			return false;
		}
		
		if (!(currentBet[1].toLowerCase().equals("odd") 
				|| currentBet[1].toLowerCase().equals("even")
				|| (StringUtils.isNumeric(currentBet[1])
						&& Integer.parseInt(currentBet[1]) >= 1
						&& Integer.parseInt(currentBet[1]) <= 36))) {
			System.out.printf("INVALID BET");
			return false;
		}
		
		if (!NumberUtils.isCreatable(currentBet[2])) {
			System.out.printf("INVALID AMOUNT");
			return false;			
		}
		
		return true;
	}
	
	private void clearBets() {
		for (Player player:players) {
			player.clearBets();
		}		
	}
	
	public void playGame() {
		if (this.players.isEmpty()) {
			LOGGER.error("NO PLAYERS LOADED");
			return;
		}

		Pattern pattern = Pattern.compile(betDelimeter);
		String currentConsoleLine = "";
		BufferedReader reader =  new BufferedReader(new InputStreamReader(System.in)); 
		System.out.printf(betPrompt);
		try {
			while ((currentConsoleLine = reader.readLine()) != null) {
				String[] currentBet = pattern.split(currentConsoleLine);
				if (currentBet[0].toLowerCase().equals("end")) {
					return;
				}
				addBet(currentBet);
				System.out.printf(betPrompt);
			}
		}catch(IOException ioe) {
			LOGGER.error("EXCEPTION READING FROM CONSOLE");
		}finally {
			reader = null;
		}
	}

	
	private ArrayList<Player> createPlayersFromFile(String delimeter) {
		if (delimeter == null) {
			LOGGER.error("NO DELIMETER FOR FILE players.txt PROVIDED");
			return null;
		}
		Pattern pattern = Pattern.compile(delimeter);

		File playersFile = new File("players.txt");
		BufferedReader br = null;
		String currentLine = null;
		boolean successfullyAdded = true;
		ArrayList<Player> players = new ArrayList<Player>();
		
		try {
			br = new BufferedReader(new FileReader(playersFile));
			while ((currentLine = br.readLine()) != null && successfullyAdded) {
				String[] currentPlayer = pattern.split(currentLine);
				Player player = new Player(currentPlayer[0]);
				if (currentPlayer.length > 1 && currentPlayer[1] != null && NumberUtils.isCreatable(currentPlayer[1])) {
					player.setTotalWin(new BigDecimal(currentPlayer[1]));
				}
				if (currentPlayer.length > 2 && currentPlayer[2] != null && NumberUtils.isCreatable(currentPlayer[2])) {
					player.setTotalBet(new BigDecimal(currentPlayer[2]));
				}
				successfullyAdded = players.add(player);
			}
			br.close();
			LOGGER.error("PLAYERS SUCCESSFULLY LOADED FROM RESOURCE FILE players.txt");
		}catch (IOException ioe) {
			LOGGER.error("PLAYERS UNSUCCESSFULLY LOADED FROM RESOURCE FILE players.txt");
			ioe.printStackTrace();
		}finally {
			br = null;
		}
		return players;
	}
	
	private void printRoundBets(int winningNumber) {
		System.out.printf("Number: %d%n", winningNumber);
		System.out.printf("Player\tBet\tOutcome\tWinnings%n");
		System.out.printf("---%n");
		for (Player player:players) {
			for (Bet bet:player.getBets()) {
				System.out.printf(
							"%s\t%s\t%s\\t%f%n", 
							player.getName(), 
							bet.getBet(), 
							bet.getOutcome(),
							bet.getWinnings().doubleValue()
					);
			}
		}		
	}
	
	private void printPlayerTotals() {
		System.out.printf("Player\tTotal Win\tTotal Bet%n");
		System.out.printf("---%n");
		for (Player player:players) {
			System.out.printf(
				"%s\t%f\t%f%n", 
				player.getName(), 
				player.getTotalWin().doubleValue(), 
				player.getTotalBet().doubleValue());
		}
	}
}
