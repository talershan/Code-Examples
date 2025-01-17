package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LiarsDice {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LiarsDice::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Liar's Dice Simulator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Liar's Dice Simulator", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel rollsLabel = new JLabel("Enter number of rolls:");
        rollsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField rollsInput = new JTextField("10000", 10);
        rollsInput.setMaximumSize(new Dimension(200, 30));

        JButton simulateButton = new JButton("Simulate");
        simulateButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        cancelButton.setEnabled(false);

        JTextArea resultsArea = new JTextArea(10, 30);
        resultsArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultsArea);

        JLabel progressLabel = new JLabel("", JLabel.CENTER);
        progressLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);

        @SuppressWarnings("unchecked")
        SwingWorker<String, Void>[] currentWorker = new SwingWorker[1];

        ActionListener simulateAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int totalRolls = Integer.parseInt(rollsInput.getText());
                    progressBar.setValue(0);
                    progressLabel.setText("Rolling your dice...");
                    simulateButton.setEnabled(false);
                    cancelButton.setEnabled(true);

                    currentWorker[0] = new SwingWorker<>() {
                        @Override
                        protected String doInBackground() throws Exception {
                            return DiceSimulator.runSimulation(totalRolls, progressBar);
                        }

                        @Override
                        protected void done() {
                            try {
                                resultsArea.setText(get());
                            } catch (Exception ex) {
                                resultsArea.setText("Error occurred during simulation.");
                            } finally {
                                progressLabel.setText("");
                                progressBar.setValue(0);
                                simulateButton.setEnabled(true);
                                cancelButton.setEnabled(false);
                            }
                        }
                    };
                    currentWorker[0].execute();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        simulateButton.addActionListener(simulateAction);
        rollsInput.addActionListener(simulateAction);

        cancelButton.addActionListener(e -> {
            if (currentWorker[0] != null) {
                currentWorker[0].cancel(true);
                progressLabel.setText("Simulation canceled.");
                progressBar.setValue(0);
                simulateButton.setEnabled(true);
                cancelButton.setEnabled(false);
            }
        });

        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(rollsLabel);
        panel.add(rollsInput);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(simulateButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(scrollPane);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(progressLabel);
        panel.add(progressBar);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(cancelButton);

        frame.add(panel);
        frame.setVisible(true);
    }
}