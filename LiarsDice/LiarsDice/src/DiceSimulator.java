package src;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JProgressBar;

public class DiceSimulator {

    public static String runSimulation(int numberOfDice, int totalRolls, JProgressBar progressBar, List<String> partialCriteria, List<String> fullCriteria, List<String> critCriteria, List<String> failureCriteria) {
        Map<String, Integer> rollCounts = new HashMap<>();
        rollCounts.put("Partial Success", 0);
        rollCounts.put("Full Success", 0);
        rollCounts.put("Crit", 0);
        rollCounts.put("Normal Failure", 0);
        


        for (int i = 0; i < totalRolls; i++) {
            if (Thread.currentThread().isInterrupted()) {
                return "Simulation was canceled.";
            }

            int[] diceRolls = DiceUtils.rollDice(numberOfDice);
            String result = DiceUtils.analyzeRolls(diceRolls, partialCriteria, fullCriteria, critCriteria, failureCriteria);
            rollCounts.put(result, rollCounts.getOrDefault(result, 0) + 1);

            if (totalRolls <= 100) {
                progressBar.setValue((int) (i * 100.0 / totalRolls));
            }
            else if (i % (totalRolls / 100) == 0) {
                progressBar.setValue((int) ((i / (double) totalRolls) * 100));
            }
        }

        progressBar.setValue(100);

        NumberFormat numberFormat = NumberFormat.getInstance();
        StringBuilder results = new StringBuilder("Results after " + numberFormat.format(totalRolls) + " rolls:\n"
         + numberOfDice + " dice rolling\n\n");

        for (Map.Entry<String, Integer> entry : rollCounts.entrySet()) {
            double percentage = (entry.getValue() * 100.0) / totalRolls;
            results.append(entry.getKey())
                    .append(": ")
                    .append(numberFormat.format(entry.getValue()))
                    .append(" (")
                    .append(String.format("%.2f", percentage))
                    .append("%)\n");
        }

        return results.toString();
    }
}
