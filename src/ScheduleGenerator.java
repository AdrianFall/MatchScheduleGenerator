import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ScheduleGenerator {

	private static int numberOfDaysNeededToPlayAllMatches;
	private static ArrayList<Integer> randomlyChosenTeamNamesList;
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

		ArrayList<Map<String, String>> allMatchesCombinationList = generateAllMatchesCombinationList(teamList);
		/* System.out.println(allMatchesCombinationList); */

		generateSchedule(allMatchesCombinationList);

	}

	private static void generateSchedule(
			ArrayList<Map<String, String>> allMatchesCombinationList) {

	}

	private static ArrayList<Map<String, String>> generateAllMatchesCombinationList(
			ArrayList<String> teamList) {
		ArrayList<Map<String, String>> allMatchesCombinationList = new ArrayList<Map<String, String>>();
		ArrayList<String> allMatches = new ArrayList<String>();
		// Shuffle the team list
		Collections.shuffle(teamList);

		Map<String, Map<String, Map<String, String>>> mappedMatches = new HashMap<String, Map<String, Map<String, String>>>();

		// Generations Rules
		// A team plays one day HOME and the next AWAY
		// A team must face the same opponent at HOME and AWAY

		// When does a team face the same opponent, time-wise?
		// The algorithm used attempts to provide the best distribution of the
		// same opponent matches to the user as it can.
		// As an example assuming there are 22 days to play a league of 12
		// teams, when the algorithms picks up a team A on
		// the first day of the season against a team B, then the algorithm will
		// schedule a match between team B and team A
		// (notice team A is playing AWAY this time) on the 12th day of the
		// league. By saying team C is playing against
		// team V on 19th day of season, we should be able to infer that team V
		// has played team C on the 8th day of season.

		ArrayList<String> obtainedTeamList = new ArrayList<String>(teamList);

		// Generation with round-robin tournament algorithm
		String pivot = obtainedTeamList.get(0);
		obtainedTeamList.remove(0);
		boolean pivotPlayingHome = false;
		for (int i = 0; i < (teamList.size()-1); i++ ) {
			
			

			// Obtain to obtain the current teamNameMap
	/*		try {
				teamNameMap = mappedMatches.get("day" + (i + 1));
				if (teamNameMap == null)
					teamNameMap = new HashMap<String, Map<String, String>>();

			} catch (Exception e) {
				// keep the teamNameMap empty.
				teamNameMap = new HashMap<String, Map<String, String>>();
			}
			// Attempt to obtain the current match record map
			try {
				matchRecordMap = mappedMatches.get("day" + (i + 1)).get(
						teamName);
				if (matchRecordMap == null)
					matchRecordMap = new HashMap<String, String>();

			} catch (Exception e) {
				// keep the matchRecordMap empty.
				matchRecordMap = new HashMap<String, String>();
			}*/
			
			
			if (((i+1) % 2) == 1) pivotPlayingHome = true;
			else pivotPlayingHome = false;
			
			
			for (int j = 0; j < (teamList.size() / 2); j++) {
				Map<String, Map<String, String>> teamNameMap = new HashMap<String, Map<String, String>>();
				Map<String, String> matchRecordMap = new HashMap<String, String>();
				String home = null;
				String away = null;
				if ((((j+1) % 2) == 1) && pivotPlayingHome) {
					// Home
					if (j == 0) {
						System.out.println("j == 0");
						home = pivot;
						away = obtainedTeamList.get(obtainedTeamList.size()-1); 
					} else { // Stage 2 of algorithm
						home = obtainedTeamList.get(j-1);
						away = obtainedTeamList.get((obtainedTeamList.size()-1)-j);
					}
					
				} else if ((((j+1) % 2) == 0) && pivotPlayingHome) {
					// Away
					// Stage 2
						home = obtainedTeamList.get((obtainedTeamList.size()-1)-j);
						away = obtainedTeamList.get(j-1);
					
				} else if ((((j+1) % 2) == 0) && !pivotPlayingHome) {
					// Stage 2
					home = obtainedTeamList.get(j-1);
					away = obtainedTeamList.get((obtainedTeamList.size()-1)-j);
					
				} else if ((((j+1) % 2) == 1) && !pivotPlayingHome) {
					if (j == 0) {
						home = obtainedTeamList.get(obtainedTeamList.size()-1);
						away = pivot;
					} else { // Stage 2
						home = obtainedTeamList.get((obtainedTeamList.size()-1)-j);
						away = obtainedTeamList.get(j-1);
					}
				}
				if (home == null || away == null) System.err.println("SHIT");
				matchRecordMap.put("home", home);
				matchRecordMap.put("away", away);
				
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
				
				
				
				mappedMatches.put("day" + (i+1), teamNameMap);
				
				
				
			} // End looping for half of team size
			
			// Rotate the obtainedTeamList
			Collections.rotate(obtainedTeamList, 1);
			
		} // END for loop (teamList.size)
	/*	System.out.println(mappedMatches);*/
		 for (int i = 0; i < 11; i++) { System.out.println(pivot + " " + (i+1)
				 + mappedMatches.get("day"+(i+1)).get(pivot)); }
		return null;
	}
}
		
		