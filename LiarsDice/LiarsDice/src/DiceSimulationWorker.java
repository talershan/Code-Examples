package src;

import javax.swing.*;
import java.util.List;

public class DiceSimulationWorker extends SwingWorker<String, Void> {

    private final int totalRolls;
    private final JProgressBar progressBar;
    private final List<String> partialCriteria;
    private final List<String> fullCriteria;
    private final List<String> critCriteria;
    private final JTextArea resultsArea;
    private final JButton simulateButton;
    private final JButton cancelButton;
    private final JLabel progressLabel;
    private int numberOfDice = 5;

    public int getNumberOfDice() {
        return this.numberOfDice;
    }

    public void setNumberOfDice(int numberOfDice) {
        this.numberOfDice = numberOfDice;
    }

    public DiceSimulationWorker(
            int numberOfDice,
            int totalRolls,
            JProgressBar progressBar,
            List<String> partialCriteria,
            List<String> fullCriteria,
            List<String> critCriteria,
            JTextArea resultsArea,
            JButton simulateButton,
            JButton cancelButton,
            JLabel progressLabel
    ) {
        this.numberOfDice = numberOfDice;
        this.totalRolls = totalRolls;
        this.progressBar = progressBar;
        this.partialCriteria = partialCriteria;
        this.fullCriteria = fullCriteria;
        this.critCriteria = critCriteria;
        this.resultsArea = resultsArea;
        this.simulateButton = simulateButton;
        this.cancelButton = cancelButton;
        this.progressLabel = progressLabel;
    }

    @Override
    protected String doInBackground() throws Exception {
        return DiceSimulator.runSimulation(numberOfDice, totalRolls, progressBar, partialCriteria, fullCriteria, critCriteria);
    }

    @Override
    protected void done() {
        try {
            resultsArea.setText(get());
        } catch (Exception e) {
            resultsArea.setText("Error occurred during simulation.");
        } finally {
            progressLabel.setText("");
            progressBar.setValue(0);
            simulateButton.setEnabled(true);
            cancelButton.setEnabled(false);
        }
    }
}
