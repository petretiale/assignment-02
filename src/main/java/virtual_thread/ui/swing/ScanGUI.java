package virtual_thread.ui.swing;

import common.Accumulator;
import virtual_thread.ui.GUI;
import virtual_thread.VtController;

import javax.swing.*;
import java.awt.*;

public class ScanGUI extends JFrame implements GUI {

    private final JTextField pathField;
    private final JTextArea statsArea;
    private final JButton startBtn;
    private final JButton stopBtn;

    private VtController controller;
    private long lastNotificationTime = 0;

    public ScanGUI(){
        // 1. Configurazione base della finestra principale
        setTitle("File System Scanner");
        setSize(550, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Pannello Superiore (Input del Percorso e Bottoni di Controllo)
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));

        pathField = new JTextField(System.getProperty("user.home"), 20);
        startBtn = new JButton("Start Scan");
        stopBtn = new JButton("Stop");
        stopBtn.setEnabled(false); // All'inizio il pulsante Stop è disattivato perché non c'è nessuna scansione

        topPanel.add(new JLabel("Cartella:"));
        topPanel.add(pathField);
        topPanel.add(startBtn);
        topPanel.add(stopBtn);

        add(topPanel, BorderLayout.NORTH);

        // Pannello Centrale (Area di testo per le statistiche)
        statsArea = new JTextArea();
        statsArea.setEditable(false);
        statsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        statsArea.setText("Pronto per la scansione. Inserisci un percorso valido e premi 'Start Scan'.\n");

        JScrollPane scrollPane = new JScrollPane(statsArea);
        add(scrollPane, BorderLayout.CENTER);

        startBtn.addActionListener(e -> {
            controller.startScan(pathField.getText(), 20000, 4);
        });

        stopBtn.addActionListener(e -> {
            controller.stopScan();
        });

    }

    @Override
    public void setController(VtController controller) {
        this.controller = controller;
    }

    @Override
    public void setScanRunningState(boolean isRunning) {
        SwingUtilities.invokeLater(() -> {
            startBtn.setEnabled(!isRunning);
            stopBtn.setEnabled(isRunning);
        });
    }

    @Override
    public void updateStatusArea(String message) {
        SwingUtilities.invokeLater(() -> {
            statsArea.append(message);
            statsArea.setCaretPosition(statsArea.getDocument().getLength());
        });
    }

    @Override
    public void displayLiveStats(Accumulator currentAcc) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastNotificationTime >= 500) {
            this.lastNotificationTime = currentTime;

            SwingUtilities.invokeLater(() -> {
                statsArea.setText("--> Scanning in progress... Total files counted: " + currentAcc.getTotalFiles());
            });
        }
    }

    @Override
    public void displayFinalReport(boolean cancelled, Accumulator finalAcc) {
        SwingUtilities.invokeLater(() -> {
            startBtn.setEnabled(true);
            stopBtn.setEnabled(false);

            long maxFS = finalAcc.getMaxFS();
            int nb = finalAcc.getNb();
            long[] bands = finalAcc.getBands();
            long bandWidth = maxFS / nb;

            StringBuilder sb = new StringBuilder();
            sb.append("--- SCAN RESULTS ---\n");
            sb.append("===========================================\n");
            sb.append("      REPORT STATISTICHE FILE SYSTEM       \n");
            sb.append("===========================================\n");
            sb.append(String.format(" Totale file analizzati: %d%n", finalAcc.getTotalFiles()));

            for (int i = 0; i < nb; i++) {
                long low = i * bandWidth;
                long high = (i + 1) * bandWidth - 1;
                sb.append(String.format(" [%d - %d] byte: \t%d file%n", low, high, bands[i]));
            }

            sb.append(String.format(" [>= %d] byte: \t\t%d file%n", maxFS, bands[nb]));
            sb.append("===========================================\n");

            if (cancelled) {
                sb.append("\n[X] Scanning operation successfully cancelled.");
            } else {
                sb.append("\n[V] Scanning operation completed.");
            }
            statsArea.setText(sb.toString());
        });
    }
}
