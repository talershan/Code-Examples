package src;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

class DiceUtils {

    public static int[] rollDice(int n) {
        Random random = new Random();
        int[] rolls = new int[n];
        for (int i = 0; i < n; i++) {
            rolls[i] = random.nextInt(6) + 1; // Random number between 1 and 6
        }
        return rolls;
    }

    public static String analyzeRolls(int[] rolls) {
        Map<Integer, Integer> counts = new HashMap<>();
        for (int roll : rolls) {
            counts.put(roll, counts.getOrDefault(roll, 0) + 1);
        }

        int pairs = 0;
        int threeOfAKind = 0;
        int fourOfAKind = 0;
        int fiveOfAKind = 0;

        for (int count : counts.values()) {
            if (count == 2) {
                pairs++;
            } else if (count == 3) {
                threeOfAKind++;
            } else if (count == 4) {
                fourOfAKind++;
            } else if (count == 5) {
                fiveOfAKind++;
            }
        }

        Arrays.sort(rolls);
        boolean isLowStraight = Arrays.equals(rolls, new int[]{1, 2, 3, 4, 5});
        boolean isHighStraight = Arrays.equals(rolls, new int[]{2, 3, 4, 5, 6});

        if (fiveOfAKind > 0 || isLowStraight || isHighStraight) {
            return "Crit";
        }
        if (fourOfAKind > 0 || (threeOfAKind > 0 && pairs > 0)) {
            return "Full Success";
        }
        if (threeOfAKind > 0 || pairs == 2) {
            return "Partial Success";
        }
        return "Failure";
    }
}