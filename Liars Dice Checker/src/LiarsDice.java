import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.text.NumberFormat;

public class LiarsDice {

    public static void main(String[] args) {
        Map<String, Integer> rollCounts = new HashMap<>();
        rollCounts.put("Partial Success", 0);
        rollCounts.put("Full Success", 0);
        rollCounts.put("Crit", 0);
        rollCounts.put("Failure", 0);

        int totalRolls = 10000000;

        for (int i = 0; i < totalRolls; i++) {
            int[] diceRolls = rollDice(5);
            String result = analyzeRolls(diceRolls);
            rollCounts.put(result, rollCounts.get(result) + 1);
        }

        NumberFormat numberFormat = NumberFormat.getInstance();

        System.out.println("Results after 10,000,000 rolls:");
        for (Map.Entry<String, Integer> entry : rollCounts.entrySet()) {
            double percentage = (entry.getValue() * 100.0) / totalRolls;
            System.out.println(entry.getKey() + ": " + numberFormat.format(entry.getValue()) + " (" + String.format("%.2f", percentage) + "%)");
        }
    }

    // Rolls n d6 dice and returns an array of results
    public static int[] rollDice(int n) {
        Random random = new Random();
        int[] rolls = new int[n];
        for (int i = 0; i < n; i++) {
            rolls[i] = random.nextInt(6) + 1; // Random number between 1 and 6
        }
        return rolls;
    }

    // Analyzes the dice rolls for TTRPG-style results
    public static String analyzeRolls(int[] rolls) {
        // Count occurrences of each face value
        Map<Integer, Integer> counts = new HashMap<>();
        for (int roll : rolls) {
            counts.put(roll, counts.getOrDefault(roll, 0) + 1);
        }

        // Check for pairs, three-of-a-kind, four-of-a-kind, and five-of-a-kind
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

        // Check for straights
        Arrays.sort(rolls);
        boolean isLowStraight = Arrays.equals(rolls, new int[]{1, 2, 3, 4, 5});
        boolean isHighStraight = Arrays.equals(rolls, new int[]{2, 3, 4, 5, 6});

        // Determine TTRPG-style result
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