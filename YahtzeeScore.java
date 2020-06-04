package de.cbw.jav.yahtzee.fx;

import java.util.Comparator;


/**
 * Model class for Yahtzee high scores.
 */
public class YahtzeeScore implements Comparable<YahtzeeScore> {
	static public final Comparator<YahtzeeScore> COMPARATOR = Comparator.comparing(YahtzeeScore::getTotalScore).thenComparing(Comparator.comparing(YahtzeeScore::getTimestamp).reversed());

	private String userAlias;
	private int totalScore;
	private int upperSectionScore;
	private int lowerSectionScore;
	private long timestamp;


	public String getUserAlias () {
		return this.userAlias;
	}
	public void setUserAlias (String userAlias) {
		this.userAlias = userAlias;
	}

	public int getTotalScore () {
		return this.totalScore;
	}
	public void setTotalScore (int totalScore) {
		this.totalScore = totalScore;
	}

	public int getUpperSectionScore () {
		return this.upperSectionScore;
	}
	public void setUpperSectionScore (int upperSectionScore) {
		this.upperSectionScore = upperSectionScore;
	}

	public int getLowerSectionScore () {
		return this.lowerSectionScore;
	}
	public void setLowerSectionScore (int lowerSectionScore) {
		this.lowerSectionScore = lowerSectionScore;
	}

	public long getTimestamp () {
		return this.timestamp;
	}
	public void setTimestamp (long timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public int compareTo (final YahtzeeScore other) {
		return COMPARATOR.compare(this, other);
	}


	public String toJsonString () {
		return String.format("{ userAlias: \"%s\", upperSectionScore: %d, lowerSectionScore: %d, totalScore: %d, timestamp: %d }", this.userAlias, this.upperSectionScore, this.lowerSectionScore, this.totalScore, this.timestamp);
	}


	static public YahtzeeScore fromJsonString (final String json) {
		final int leftBracketOffset = json.indexOf('{'), rightBracketIndex = json.lastIndexOf('}');
		if (leftBracketOffset == -1 | rightBracketIndex == -1) throw new IllegalArgumentException();

		final String[] jsonProperties = json.substring(leftBracketOffset + 1, rightBracketIndex).split("[,:]");
		if (jsonProperties.length % 2 != 0) throw new IllegalArgumentException();

		final YahtzeeScore result = new YahtzeeScore();
		for (int index = 0; index < jsonProperties.length; index += 2) {
			final String key = jsonProperties[index].trim(), value = jsonProperties[index + 1].trim();
			switch (key) {
				case "userAlias":
					if (!value.startsWith("\"") | !value.endsWith("\"")) throw new IllegalArgumentException();	
					result.setUserAlias(value.substring(1, value.length() - 1));
					break;
				case "upperSectionScore":
					result.setUpperSectionScore(Integer.parseInt(value));
					break;
				case "lowerSectionScore":
					result.setLowerSectionScore(Integer.parseInt(value));
					break;
				case "totalScore":
					result.setTotalScore(Integer.parseInt(value));
					break;
				case "timestamp":
					result.setTimestamp(Long.parseLong(value));
					break;
				default:
					throw new IllegalArgumentException();
			}
		}

		return result;
	}
}