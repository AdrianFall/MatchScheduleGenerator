import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ScheduleGenerator {

	private static int numberOfDaysNeededToPlayAllMatches;

	public static void main(String[] args) {
		ArrayList<String> teamList = new ArrayList<String>();

		teamList.add("Arsenal");
		teamList.add("Hull");
		teamList.add("City");
		teamList.add("Man");
		teamList.add("Fullham");
		teamList.add("Spurs");
		teamList.add("Stoke");
		teamList.add("Chelsky");
		teamList.add("Aston Villa");
		teamList.add("Everton");
		teamList.add("QPR");
		teamList.add("Southampton");

		numberOfDaysNeededToPlayAllMatches = (teamList.size() - 1) * 2;
		/* System.out.println(numberOfDaysNeededToPlayAllMatches); */

		Map<String, Map<String, Map<String, String>>> allMatchesCombinationList;
		try {
			allMatchesCombinationList = generateAllMatchesCombinationList(teamList);
			printMatches(allMatchesCombinationList, teamList);
			test(allMatchesCombinationList, teamList);
		} catch (ScheduleGenerationException sge) {
			sge.printStackTrace();
		}
		/* System.out.println(allMatchesCombinationList); */
	}

	private static void test(Map<String, Map<String, Map<String, String>>> allMatchesCombinationList, ArrayList<String> teamList) throws ScheduleGenerationException {
		// Loop through all the teams
		for (int i = 0; i < teamList.size(); i++) {
			String currentTeamName = teamList.get(i);
			String lastPlayedMatchVenue = "none";
			int amountOfHomeMatches = 0;
			int amountOfAwayMatches = 0;
			int amountOfMatchesInARowAtTheSameVenue = 0;
			int highestAmountOfMatchesInARowAtTheSameVenue = 0;
			System.out.println("               " + currentTeamName);
			// Loop through all the matches in a day
			for (int j = 0; j < numberOfDaysNeededToPlayAllMatches; j++) {
				
				
				String home = allMatchesCombinationList.get("day" + (j+1)).get(currentTeamName).get("home");
				String away = allMatchesCombinationList.get("day" + (j+1)).get(currentTeamName).get("away");
				
				// For the first half of the season, make a check
				// whether the above obtained teams (home & away)
				// are playing each other in half a season time
				// (this time at the different venue)
				if (j < (numberOfDaysNeededToPlayAllMatches/2)) {
					String halfASeasonTimeHome = allMatchesCombinationList.get("day" + ((j+1) + (numberOfDaysNeededToPlayAllMatches/2))).get(currentTeamName).get("home");
					String halfASeasonTimeAway = allMatchesCombinationList.get("day" + ((j+1) + (numberOfDaysNeededToPlayAllMatches/2))).get(currentTeamName).get("away");
					
					if (home != halfASeasonTimeAway || away != halfASeasonTimeHome) 
							throw new ScheduleGenerationException("The team " 
																  + home 												
																  + " facing "										
																  + away 										
																  + " on day " 										
																  + (j+1) 											
																  + " is not facing the same opponent in half a season time (i.e. on day " 												 
																  + (j+numberOfDaysNeededToPlayAllMatches/2) 
																  + "), since " 
																  + halfASeasonTimeAway 
																  + " is playing away facing " 
																  + halfASeasonTimeHome 
																  + " and " 
																  + allMatchesCombinationList.get("day" + ((j+1) + (numberOfDaysNeededToPlayAllMatches/2))).get(away).get("home") 
																  + " is playing home facing " 
																  + allMatchesCombinationList.get("day" + ((j+1) + (numberOfDaysNeededToPlayAllMatches/2))).get(away).get("away"));
				}
				
				// Calculate the number of games played home & away 
				// + counter for highest amount of matches player in a row
				// at the same venue
				if (home.equals(currentTeamName)) {
					amountOfHomeMatches++;
					if (lastPlayedMatchVenue.equals("none")) lastPlayedMatchVenue = "home";
					
					if (lastPlayedMatchVenue.equals("away")) amountOfMatchesInARowAtTheSameVenue = 0;
			
					else if (lastPlayedMatchVenue.equals("home")) {
						amountOfMatchesInARowAtTheSameVenue++;
						if (amountOfMatchesInARowAtTheSameVenue > highestAmountOfMatchesInARowAtTheSameVenue) {
							highestAmountOfMatchesInARowAtTheSameVenue = new Integer(amountOfMatchesInARowAtTheSameVenue);
						}
					}
					lastPlayedMatchVenue = "home";
				} else if (away.equals(currentTeamName)) {
					amountOfAwayMatches++;
					if (lastPlayedMatchVenue.equals("none")) lastPlayedMatchVenue = "away";
				
					if (lastPlayedMatchVenue.equals("away")) {
						amountOfMatchesInARowAtTheSameVenue++;
						if (amountOfMatchesInARowAtTheSameVenue > highestAmountOfMatchesInARowAtTheSameVenue) {
							highestAmountOfMatchesInARowAtTheSameVenue = new Integer(amountOfMatchesInARowAtTheSameVenue);
						}
					} else if (lastPlayedMatchVenue.equals("home")) amountOfMatchesInARowAtTheSameVenue = 0;
					
					lastPlayedMatchVenue = "away";
				}
				
			} // END looping through all the matches in a day
			
			// Determine whether the current team has equal amount of home and away matches in a season.
			if (amountOfHomeMatches != amountOfAwayMatches) throw new ScheduleGenerationException("The team " + currentTeamName + " has insufficient amount of games, since the home (" + amountOfHomeMatches + ") is not equal to away (" + amountOfAwayMatches + ") amount of matches.");
			
			// Determine whether the current team has as many matches as the number of days in a season.
			if ((amountOfHomeMatches+amountOfAwayMatches) != numberOfDaysNeededToPlayAllMatches) throw new ScheduleGenerationException("The team " + currentTeamName + " has insufficient amount of games (" + (amountOfHomeMatches + amountOfAwayMatches) + ") comparing to days needed to play all matches (" + numberOfDaysNeededToPlayAllMatches + ").");
			
			// Determine whether the team has three or more matches in a row at the same venue.
			if(amountOfMatchesInARowAtTheSameVenue >= 3) throw new ScheduleGenerationException("The team has " + amountOfMatchesInARowAtTheSameVenue + " matches in a row at the same venue.");
			
			
			
			System.out.println(currentTeamName + " number of home matches = " + amountOfHomeMatches + " .. away matches = " + amountOfAwayMatches);
			System.out.println("Highest number of games played in the same venue in a row is " + highestAmountOfMatchesInARowAtTheSameVenue);

			System.out.println();
			
		} // END looping through the teams
	}
	

	/**
	 * Prints the matches to a text file
	 * @param mappedMatches - the generated map of matches by the algorithm
	 * @param teamList - array list with the team names
	 */
	private static void printMatches(
			Map<String, Map<String, Map<String, String>>> mappedMatches,
			ArrayList<String> teamList) {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(
					"C:/Users/Adrian/Desktop/schedule.txt", "UTF-8");
			// Print all days in the season
			for (int i = 0; i < numberOfDaysNeededToPlayAllMatches; i++) {
				
				writer.println("*************DAY" + (i + 1) + " ************");
				ArrayList<String> repetitionCheckList = new ArrayList<String>(); 
				for (int j = 0; j < teamList.size(); j++) {
					String home = mappedMatches.get("day" + (i+1)).get(teamList.get(j)).get("home");
					String away = mappedMatches.get("day" + (i+1)).get(teamList.get(j)).get("away");
					if (!repetitionCheckList.contains(home) && !repetitionCheckList.contains(away)) {
						writer.println(home + " VS " + away);
						repetitionCheckList.add(home);
						repetitionCheckList.add(away);
					}
				}
			} // END printing all days in the season
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			writer.close();
		}
	} // END method printMatches

	private static Map<String, Map<String, Map<String, String>>> generateAllMatchesCombinationList(
			ArrayList<String> teamList) throws ScheduleGenerationException {
		
		if (teamList.size() % 2 != 0) throw new ScheduleGenerationException("The generation couldn't be processed since the team list size was not an even number. The team list size = " + teamList.size());
	
		//FIXME Shuffle the team list (REMEMBER TO DELETE IN REAL PROJECT)
		Collections.shuffle(teamList);

		Map<String, Map<String, Map<String, String>>> mappedMatches = new HashMap<String, Map<String, Map<String, String>>>();

		// Generations Rules
		// A pivot team (i.e. the first grabbed team by algorithm) must play 
		// one day home and the next away, for all the season.
		// Every team must face the same opponent at home and away.
		// Every team must have equal amount of home and away matches during the season.
		// Any team should not be playing three or more matches at home or away in a row. 

		// When does a team face the same opponent, time-wise?
		// The algorithm used attempts to provide the best distribution of the
		// same opponent matches to the user as it can.
		// As an example assuming there are 22 days to play a league of 12
		// teams, when the algorithms picks up a team A on
		// the first day of the season against a team B, then the algorithm will
		// schedule a match between team B and team A
		// (notice team A is playing AWAY this time) on the 12th day of the
		// season. By saying team C is playing against
		// team V on 19th day of season, we should be able to infer that team V
		// has played team C on the 8th day of season.

		ArrayList<String> obtainedTeamList = new ArrayList<String>(teamList);

		// Generation with round-robin tournament algorithm
		// Obtain the pivot and remove it from the obtainedTeamList
		String pivot = obtainedTeamList.get(0);
		obtainedTeamList.remove(0);
		/*System.out.println(obtainedTeamList);*/
		boolean pivotPlayingHome = false;
		for (int i = 0; i < (teamList.size() - 1); i++) {

			if (((i + 1) % 2) == 1)
				pivotPlayingHome = true;
			else
				pivotPlayingHome = false;

			for (int j = 0; j < (teamList.size() / 2); j++) {
				Map<String, Map<String, String>> teamNameMap = new HashMap<String, Map<String, String>>();
				Map<String, String> matchRecordMap = new HashMap<String, String>();
				Map<String, String> secondSeasonMatchRecordMap = new HashMap<String, String>();
				String home = null;
				String away = null;
				if (pivotPlayingHome) {
					// Home
					if (j == 0) {
						home = pivot;
						away = obtainedTeamList
								.get(obtainedTeamList.size() - 1);
					} else { // Stage 2 of algorithm
						home = obtainedTeamList.get(j - 1);
						away = obtainedTeamList
								.get((obtainedTeamList.size() - 1) - j);
					}

			
				} else if (!pivotPlayingHome) {
					if (j == 0) {
						home = obtainedTeamList
								.get(obtainedTeamList.size() - 1);
						away = pivot;
					} else { // Stage 2
						home = obtainedTeamList
								.get((obtainedTeamList.size() - 1) - j);
						away = obtainedTeamList.get(j - 1);
					}
				}
				matchRecordMap.put("home", home);
				matchRecordMap.put("away", away);

				secondSeasonMatchRecordMap.put("home", away);
				secondSeasonMatchRecordMap.put("away", home);

				// Obtain to obtain the current teamNameMap
				try {
					teamNameMap = mappedMatches.get("day" + (i + 1));
					if (teamNameMap == null)
						teamNameMap = new HashMap<String, Map<String, String>>();

				} catch (Exception e) {
					// keep the teamNameMap empty.
					teamNameMap = new HashMap<String, Map<String, String>>();
				}

				teamNameMap.put(home, matchRecordMap);
				teamNameMap.put(away, matchRecordMap);

				mappedMatches.put("day" + (i + 1), teamNameMap);

				// Check if there exists a mapped day (in the 2nd half of season)  
				try {
					teamNameMap = mappedMatches.get("day" + (i + teamList.size()));
					if (teamNameMap == null)
						teamNameMap = new HashMap<String, Map<String, String>>();

				} catch (Exception e) {
					// keep the teamNameMap empty.
					teamNameMap = new HashMap<String, Map<String, String>>();
				}
				// Allocate the match 
				teamNameMap.put(home, secondSeasonMatchRecordMap);
				teamNameMap.put(away, secondSeasonMatchRecordMap);
				mappedMatches.put("day" + (i + teamList.size()), teamNameMap);

			} // End looping for half of team size

			// Rotate the obtainedTeamList (part of the round-robin tournament scheduling algorithm, i.e. )
			Collections.rotate(obtainedTeamList, 1);

		} // END for loop (teamList.size)
		return mappedMatches;
	}
}
