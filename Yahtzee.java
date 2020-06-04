package de.cbw.jav.yahtzee.fx;

import java.util.Arrays;
import java.util.stream.IntStream;


/**
 * Model class for Yahtzee matches.
 */
public class Yahtzee {
	private int aces;
	private int twos;
	private int threes;
	private int fours;
	private int fives;
	private int sixes;

	private int threeOfKind;
	private int fourOfKind;
	private int fullHouse;
	private int smallStraight;
	private int largeStraight;
	private int fiveOfKind;
	private int chance;
	
	
	private int highScores;


	public void reset () {
		this.aces = 0;
		this.twos = 0;
		this.threes = 0;
		this.fours = 0;
		this.fives = 0;
		this.sixes = 0;
		this.threeOfKind = 0;
		this.fourOfKind = 0;
		this.fullHouse = 0;
		this.smallStraight = 0;
		this.largeStraight = 0;
		this.fiveOfKind = 0;
		this.chance = 0;
	}


	public int getAces () {
		return this.aces;
	}


	public void setAces (final int[] dices) {
		this.aces = IntStream.of(dices).filter(dice -> dice == 1).sum();
	}


	public int getTwos () {
		return this.twos;
	}


	public void setTwos (final int[] dices) {
		this.twos = IntStream.of(dices).filter(dice -> dice == 2).sum();
	}


	public int getThrees () {
		return this.threes;
	}


	public void setThrees (final int[] dices) {
		this.threes = IntStream.of(dices).filter(dice -> dice == 3).sum();
	}


	public int getFours () {
		return this.fours;
	}


	public void setFours (final int[] dices) {
		this.fours = IntStream.of(dices).filter(dice -> dice == 4).sum();
	}


	public int getFives () {
		return this.fives;
	}


	public void setFives (final int[] dices) {
		this.fives = IntStream.of(dices).filter(dice -> dice == 5).sum();
	}


	public int getSixes () {
		return this.sixes;
	}


	public void setSixes (final int[] dices) {
		this.sixes = IntStream.of(dices).filter(dice -> dice == 6).sum();
	}


	public int getThreeOfKind () {
		return this.threeOfKind;
	}


	public void setThreeOfKind (final int[] dices) {
		for (int index = 1; index <= 6; ++index) {
			final int value = index;
			final int count = (int) IntStream.of(dices).filter(dice -> dice == value).count();
			if (count >= 3) {
				this.threeOfKind = IntStream.of(dices).sum();
				return;
			}
		}

		this.threeOfKind = 0;
	}


	public int getFourOfKind () {
		return this.fourOfKind;
	}


	public void setFourOfKind (final int[] dices) {
		for (int index = 1; index <= 6; ++index) {
			final int value = index;
			final int count = (int) IntStream.of(dices).filter(dice -> dice == value).count();
			if (count >= 4) {
				this.fourOfKind = IntStream.of(dices).sum();
				return;
			}
		}

		this.fourOfKind = 0;
	}


	public int getFullHouse () {
		return this.fullHouse;
	}


	public void setFullHouse (final int[] dices) {
		boolean twoOfKind = false, threeOfKind = false;
		for (int index = 1; index <= 6; ++index) {
			final int value = index;
			final int count = (int) IntStream.of(dices).filter(dice -> dice == value).count();
			if (count == 2)
				twoOfKind = true;
			if (count == 3)
				threeOfKind = true;
		}

		this.fullHouse = twoOfKind & threeOfKind ? 25 : 0;
	}


	public int getSmallStraight () {
		return this.smallStraight;
	}


	public void setSmallStraight (int[] dices) {
		dices = IntStream.of(dices).distinct().sorted().toArray();
		boolean smallStraight = dices.length >= 4;
		if (smallStraight) smallStraight =
				(dices[0] == 1 & dices[1] == 2 & dices[2] == 3 & dices[3] == 4) || (dices[0] == 2 & dices[1] == 3 & dices[2] == 4 & dices[3] == 5) || (dices[0] == 3 & dices[1] == 4 & dices[2] == 5 & dices[3] == 6)
				|| (dices.length == 5 && (dices[1] == 3 & dices[2] == 4 & dices[3] == 5 & dices[4] == 6));
		this.smallStraight = smallStraight ? 30 : 0;
	}


	public int getLargeStraight () {
		return this.largeStraight;
	}


	public void setLargeStraight (final int[] dices) {
		Arrays.sort(dices);
		final boolean largeStraight = (dices[0] == 1 & dices[1] == 2 & dices[2] == 3 & dices[3] == 4 & dices[4] == 5) | (dices[0] == 2 & dices[1] == 3 & dices[2] == 4 & dices[3] == 5 & dices[4] == 6);
		this.largeStraight = largeStraight ? 40 : 0;
	}


	public int getFiveOfKind () {
		return this.fiveOfKind;
	}


	public void setFiveOfKind (final int[] dices) {
		for (int index = 1; index <= 6; ++index) {
			final int value = index;
			final int count = (int) IntStream.of(dices).filter(dice -> dice == value).count();
			if (count == 5) {
				this.fiveOfKind = 50;
				return;
			}
		}

		this.fiveOfKind = 0;
	}


	public int getChance () {
		return this.chance;
	}


	public void setChance (final int[] dices) {
		this.chance = IntStream.of(dices).sum();
	}


	public int upperSectionSum () {
		return this.aces + this.twos + this.threes + this.fours + this.fives + this.sixes;
	}


	public int upperSectionBonus () {
		return this.upperSectionSum() >= 63 ? 35 : 0;
	}


	public int lowerSectionSum () {
		return this.threeOfKind + this.fourOfKind + this.fullHouse + this.smallStraight + this.largeStraight + this.fiveOfKind + this.chance;
	}


	public int upperSectionScore () {
		return this.upperSectionSum() + this.upperSectionBonus();
	}


	public int lowerSectionScore () {
		return this.lowerSectionSum();
	}


	public int total () {
		return this.upperSectionScore() + this.lowerSectionScore();
	}
	



	public int newScore () {
		// TODO Auto-generated method stub
		return this.upperSectionScore() + this.lowerSectionScore() + this.highScores;
	}
}