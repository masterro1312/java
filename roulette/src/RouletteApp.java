import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class RouletteApp extends JFrame {
    private Account account = new Account();
    private JLabel balanceLabel;
    private JTextField betField;
    private JComboBox<String> colorChoice;
    private JLabel resultLabel;
    private RouletteWheel wheelPanel;
    private Random random = new Random();

    public RouletteApp() {
        setTitle("Ruletka");
        setSize(500, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Panel sterowania (góra)
        JPanel controlsPanel = new JPanel();
        controlsPanel.setLayout(new GridBagLayout());
        controlsPanel.setBackground(new Color(34, 139, 34)); // Zielone tło
        controlsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Ruletka", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        controlsPanel.add(titleLabel, gbc);

        balanceLabel = new JLabel("Saldo: " + account.getBalance() + " żetonów");
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        balanceLabel.setForeground(Color.YELLOW);
        gbc.gridy = 1;
        controlsPanel.add(balanceLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 2;
        gbc.gridx = 0;
        controlsPanel.add(new JLabel("Kwota zakładu:", SwingConstants.RIGHT), gbc);
        betField = new JTextField("10");
        gbc.gridx = 1;
        controlsPanel.add(betField, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        controlsPanel.add(new JLabel("Wybierz kolor:", SwingConstants.RIGHT), gbc);
        String[] colors = {"Czerwony", "Czarny", "Zielony"};
        colorChoice = new JComboBox<>(colors);
        gbc.gridx = 1;
        controlsPanel.add(colorChoice, gbc);

        JButton spinButton = new JButton("Zakręć kołem!");
        spinButton.setBackground(Color.RED);
        spinButton.setForeground(Color.WHITE);
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        controlsPanel.add(spinButton, gbc);

        JButton depositButton = new JButton("Doładuj bilans");
        depositButton.setBackground(Color.BLUE);
        depositButton.setForeground(Color.WHITE);
        gbc.gridy = 5;
        controlsPanel.add(depositButton, gbc);

        // Koło ruletki (środek)
        wheelPanel = new RouletteWheel();

        // Etykieta wyniku (dół)
        resultLabel = new JLabel("Wynik pojawi się tutaj", SwingConstants.CENTER);
        resultLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        // Układ
        add(controlsPanel, BorderLayout.NORTH);
        add(wheelPanel, BorderLayout.CENTER);
        add(resultLabel, BorderLayout.SOUTH);

        // Akcje przycisków
        spinButton.addActionListener(e -> spinRoulette());
        depositButton.addActionListener(e -> deposit());

        setVisible(true);
    }

    private void deposit() {
        String amountStr = JOptionPane.showInputDialog(this, "Podaj kwotę do doładowania:");
        try {
            double amount = Double.parseDouble(amountStr);
            if (amount > 0) {
                account.deposit(amount);
                balanceLabel.setText("Saldo: " + account.getBalance() + " żetonów");
            } else {
                JOptionPane.showMessageDialog(this, "Kwota musi być dodatnia!", "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Wprowadź poprawną liczbę!", "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void spinRoulette() {
        try {
            double bet = Double.parseDouble(betField.getText());
            if (bet <= 0 || bet > account.getBalance()) {
                JOptionPane.showMessageDialog(this, "Nieprawidłowa kwota zakładu!", "Błąd", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String chosenColor = colorChoice.getSelectedItem().toString().toLowerCase();

            if (account.withdraw(bet)) {
                int winningNumber = random.nextInt(37);
                wheelPanel.startAnimation(winningNumber, () -> finishSpin(bet, chosenColor, winningNumber));
            } else {
                JOptionPane.showMessageDialog(this, "Niewystarczający bilans!", "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Wprowadź poprawną kwotę!", "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void finishSpin(double bet, String chosenColor, int result) {
        String color = (result == 0) ? "zielony" : (result % 2 == 0) ? "czarny" : "czerwony";
        resultLabel.setText("Wynik: " + result + " (" + color + ")");

        if (chosenColor.equals(color)) {
            double win = bet * 2;
            account.deposit(win);
            JOptionPane.showMessageDialog(this, "Trafiłeś kolor! Wygrana: " + win + " żetonów.");
        } else {
            JOptionPane.showMessageDialog(this, "Nietrafione. Tracisz " + bet + " żetonów.");
        }
        balanceLabel.setText("Saldo: " + account.getBalance() + " żetonów");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RouletteApp());
    }
}