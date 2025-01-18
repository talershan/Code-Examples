package src;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class DiceUtils {

    public static int[] rollDice(int n) {
        Random random = new Random();
        int[] rolls = new int[n];
        for (int i = 0; i < n; i++) {
            rolls[i] = random.nextInt(6) + 1;
        }
        return rolls;
    }

    public static String analyzeRolls(int[] rolls, List<String> partialCriteria, List<String> fullCriteria, List<String> critCriteria, List<String> failureCriteria) {
        Map<Integer, Integer> counts = new HashMap<>();
        for (int roll : rolls) {
            counts.put(roll, counts.getOrDefault(roll, 0) + 1);
        }

        int pairs = 0, threeOfAKind = 0, fourOfAKind = 0, fiveOfAKind = 0;

        for (int count : counts.values()) {
            if (count == 2) pairs++;
            if (count == 3) threeOfAKind++;
            if (count == 4) fourOfAKind++;
            if (count == 5) fiveOfAKind++;
        }

        boolean straight = isStraight(rolls);
        boolean fullHouse = threeOfAKind == 1 && pairs >= 1;

        if (matchesCriteria(critCriteria, pairs, threeOfAKind, fourOfAKind, fiveOfAKind, fullHouse, straight)) return "Crit";
        if (matchesCriteria(fullCriteria, pairs, threeOfAKind, fourOfAKind, fiveOfAKind, fullHouse, straight)) return "Full Success";
        if (matchesCriteria(partialCriteria, pairs, threeOfAKind, fourOfAKind, fiveOfAKind, fullHouse, straight)) return "Partial Success";
        if (matchesCriteria(failureCriteria, pairs, threeOfAKind, fourOfAKind, fiveOfAKind, fullHouse, straight)) return "Normal Failure";

        return "Critical Failure";
    }

    private static boolean matchesCriteria(List<String> criteria, int pairs, int threeOfAKind, int fourOfAKind, int fiveOfAKind, boolean fullHouse, boolean straight) {
        for (String criterion : criteria) {
            switch (criterion) {
                case "Pair":
                    if (pairs > 0) return true;
                    break;
                case "Two Pair":
                    if (pairs >= 2) return true;
                    break;
                case "Three of a Kind":
                    if (threeOfAKind > 0) return true;
                    break;
                case "Four of a Kind":
                    if (fourOfAKind > 0) return true;
                    break;
                case "Full House":
                    if (fullHouse) return true;
                    break;
                case "Straight":
                    if (straight) return true;
                    break;
                case "Five of a Kind":
                    if (fiveOfAKind > 0) return true;
                    break;
            }
            
        }
        return false;
    }

    private static boolean isStraight(int[] rolls) {
        int[] counts = new int[6];
        for (int roll : rolls) counts[roll - 1]++;
        if (counts[0] == 1 && counts[1] == 1 && counts[2] == 1 && counts[3] == 1 && counts[4] == 1) return true; // Low Straight
        if (counts[1] == 1 && counts[2] == 1 && counts[3] == 1 && counts[4] == 1 && counts[5] == 1) return true; // High Straight
        return false;
    }
}
