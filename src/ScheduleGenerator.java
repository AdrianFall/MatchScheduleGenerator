import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ScheduleGenerator {

	private static int numberOfDaysNeededToPlayAllMatches;
	public static final String MAP_OPPONENT_ONE = "opponent_one";
	public static final String MAP_OPPONENT_TWO = "opponent_TWO";

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

		Map<String, Map<String, Map<String, String>>> allMatchesCombinationList = generateAllMatchesCombinationList(teamList);
		/* System.out.println(allMatchesCombinationList); */
		
		printMatches(allMatchesCombinationList, teamList);


	}


	private static Map<String, Map<String, Map<String, String>>> generateAllMatchesCombinationList(
			ArrayList<String> teamList) {
		
		ArrayList<Map<String, String>> allMatchesCombinationList = new ArrayList<Map<String, String>>();
		ArrayList<String> allMatches = new ArrayList<String>();
		//FIXME Shuffle the team list (REMEMBER TO DELETE IN REAL PROJECT)
		Collections.shuffle(teamList);

		Map<String, Map<String, Map<String, String>>> mappedMatches = new HashMap<String, Map<String, Map<String, String>>>();

		// Generations Rules
		// A pivot team (i.e. the first grabbed team by algorithm) must play 
		// one day home and the next away, for all the season.
		// Every team must face the same opponent at home and away.
		// Every team must have equal amount of home and away matches during the season.
		// Any team should not be playing more than three matches at home or away in a row. 

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
		System.out.println(obtainedTeamList);
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
			System.out.println(obtainedTeamList);

		} // END for loop (teamList.size)
		/* System.out.println(mappedMatches); */

		// Print to console all pivot's mapped matches
		for (int i = 0; i < numberOfDaysNeededToPlayAllMatches; i++) {
			System.out.println(pivot + " " + (i + 1)
					+ mappedMatches.get("day" + (i + 1)).get(pivot));
		
		}
		return mappedMatches;
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

}
