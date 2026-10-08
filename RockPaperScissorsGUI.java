import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.Random;

public class RockPaperScissorsGUI extends JFrame {
    // Theme Colors
    private static final Color BG_DARK = new Color(17, 24, 39);        // Slate 900
    private static final Color CARD_BG = new Color(31, 41, 55);        // Slate 800
    private static final Color ACCENT_BLUE = new Color(59, 130, 246);   // Blue 500
    private static final Color ACCENT_GREEN = new Color(34, 197, 94);   // Green 500
    private static final Color ACCENT_RED = new Color(239, 68, 68);     // Red 500
    private static final Color ACCENT_AMBER = new Color(245, 158, 11);  // Amber 500
    private static final Color TEXT_MUTED = new Color(156, 163, 175);   // Gray 400
    private static final Color TEXT_WHITE = new Color(243, 244, 246);

    private final String[] moves = {"rock", "paper", "scissors"};
    private final String[] icons = {"🪨", "📄", "✂️"};
    private final Random random = new Random();

    private int userScore = 0;
    private int compScore = 0;
    private int roundCount = 0;

    // UI elements
    private JLabel userScoreLabel;
    private JLabel compScoreLabel;
    private JLabel userPickIcon;
    private JLabel compPickIcon;
    private JLabel statusBanner;
    private DefaultListModel<String> historyModel;

    public RockPaperScissorsGUI() {
        setTitle("Rock Paper Scissors");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(780, 620);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DARK);
        setLayout(new BorderLayout(16, 16));
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(20, 24, 24, 24));

        initUI();
    }

    private void initUI() {
        // --- 1. Header & Scoreboard ---
        JPanel topPanel = new JPanel(new BorderLayout(0, 12));
        topPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("ROCK • PAPER • SCISSORS", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_MUTED);

        JPanel scoreCard = new RoundedPanel(16, CARD_BG);
        scoreCard.setLayout(new GridLayout(1, 3));
        scoreCard.setBorder(new EmptyBorder(12, 20, 12, 20));

        userScoreLabel = createScoreItem("YOU", "0", ACCENT_BLUE);
        JLabel vsBadge = new JLabel("VS", SwingConstants.CENTER);
        vsBadge.setFont(new Font("Segoe UI", Font.BOLD, 22));
        vsBadge.setForeground(TEXT_MUTED);
        compScoreLabel = createScoreItem("BOT", "0", ACCENT_RED);

        scoreCard.add(userScoreLabel);
        scoreCard.add(vsBadge);
        scoreCard.add(compScoreLabel);

        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(scoreCard, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // --- 2. Center Battle Display ---
        JPanel battlePanel = new JPanel(new GridLayout(1, 2, 16, 0));
        battlePanel.setOpaque(false);

        JPanel userCard = createFighterCard("YOUR CHOICE", userPickIcon = new JLabel("?", SwingConstants.CENTER));
        JPanel compCard = createFighterCard("COMPUTER", compPickIcon = new JLabel("?", SwingConstants.CENTER));

        battlePanel.add(userCard);
        battlePanel.add(compCard);

        JPanel centerContainer = new JPanel(new BorderLayout(0, 12));
        centerContainer.setOpaque(false);
        centerContainer.add(battlePanel, BorderLayout.CENTER);

        statusBanner = new JLabel("Select an option below to begin", SwingConstants.CENTER);
        statusBanner.setFont(new Font("Segoe UI", Font.BOLD, 17));
        statusBanner.setForeground(TEXT_WHITE);
        statusBanner.setBorder(new EmptyBorder(8, 0, 4, 0));
        centerContainer.add(statusBanner, BorderLayout.SOUTH);

        add(centerContainer, BorderLayout.CENTER);

        // --- 3. Right Sidebar: Round History ---
        JPanel historyCard = new RoundedPanel(16, CARD_BG);
        historyCard.setLayout(new BorderLayout(0, 8));
        historyCard.setPreferredSize(new Dimension(210, 0));
        historyCard.setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel historyTitle = new JLabel("Round Log", SwingConstants.CENTER);
        historyTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        historyTitle.setForeground(TEXT_MUTED);
        historyCard.add(historyTitle, BorderLayout.NORTH);

        historyModel = new DefaultListModel<>();
        JList<String> historyList = new JList<>(historyModel);
        historyList.setBackground(CARD_BG);
        historyList.setForeground(TEXT_WHITE);
        historyList.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        historyList.setSelectionBackground(new Color(55, 65, 81));
        
        JScrollPane scrollPane = new JScrollPane(historyList);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        historyCard.add(scrollPane, BorderLayout.CENTER);

        add(historyCard, BorderLayout.EAST);

        // --- 4. Controls at Bottom ---
        JPanel bottomContainer = new JPanel(new BorderLayout(0, 10));
        bottomContainer.setOpaque(false);

        JPanel actionsPanel = new JPanel(new GridLayout(1, 3, 14, 0));
        actionsPanel.setOpaque(false);
        actionsPanel.setPreferredSize(new Dimension(0, 70));

        actionsPanel.add(createActionButton("Rock", "🪨", 0));
        actionsPanel.add(createActionButton("Paper", "📄", 1));
        actionsPanel.add(createActionButton("Scissors", "✂️", 2));

        bottomContainer.add(actionsPanel, BorderLayout.CENTER);

        JButton resetButton = new JButton("Reset Game");
        resetButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        resetButton.setForeground(TEXT_MUTED);
        resetButton.setContentAreaFilled(false);
        resetButton.setBorderPainted(false);
        resetButton.setFocusPainted(false);
        resetButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        resetButton.addActionListener(e -> resetGame());
        bottomContainer.add(resetButton, BorderLayout.SOUTH);

        add(bottomContainer, BorderLayout.SOUTH);
    }

    private JLabel createScoreItem(String tag, String initialScore, Color color) {
        JLabel label = new JLabel(String.format("<html><center><span style='font-size:11px;color:#9CA3AF;'>%s</span><br><b style='font-size:22px;color:rgb(%d,%d,%d);'>%s</b></center></html>",
                tag, color.getRed(), color.getGreen(), color.getBlue(), initialScore), SwingConstants.CENTER);
        return label;
    }

    private JPanel createFighterCard(String title, JLabel iconLabel) {
        JPanel panel = new RoundedPanel(16, CARD_BG);
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(16, 12, 16, 12));

        JLabel titleLbl = new JLabel(title, SwingConstants.CENTER);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleLbl.setForeground(TEXT_MUTED);

        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 72));
        iconLabel.setForeground(TEXT_WHITE);

        panel.add(titleLbl, BorderLayout.NORTH);
        panel.add(iconLabel, BorderLayout.CENTER);
        return panel;
    }

    private JButton createActionButton(String label, String icon, int choiceIndex) {
        JButton btn = new JButton(icon + "  " + label) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2.dispose();
                super.paintComponent(g);
            }
        };

        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setForeground(TEXT_WHITE);
        btn.setBackground(CARD_BG);
        btn.setContentAreaFilled(false);
        btn.setBorder(new EmptyBorder(10, 10, 10, 10));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(55, 65, 81));
                btn.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(CARD_BG);
                btn.repaint();
            }
        });

        btn.addActionListener(e -> playRound(choiceIndex));
        return btn;
    }

    private void playRound(int userChoice) {
        int compChoice = random.nextInt(3);
        roundCount++;

        userPickIcon.setText(icons[userChoice]);
        compPickIcon.setText(icons[compChoice]);

        String roundResult;
        Color statusColor;

        if (userChoice == compChoice) {
            roundResult = "It's a Tie!";
            statusColor = ACCENT_AMBER;
            historyModel.add(0, String.format("R%d: Tie (%s)", roundCount, moves[userChoice]));
        } else if ((userChoice == 0 && compChoice == 2) ||
                   (userChoice == 1 && compChoice == 0) ||
                   (userChoice == 2 && compChoice == 1)) {
            userScore++;
            roundResult = "You Win this Round!";
            statusColor = ACCENT_GREEN;
            historyModel.add(0, String.format("R%d: Win (+1)", roundCount));
        } else {
            compScore++;
            roundResult = "Computer Takes It!";
            statusColor = ACCENT_RED;
            historyModel.add(0, String.format("R%d: Loss", roundCount));
        }

        statusBanner.setText(roundResult);
        statusBanner.setForeground(statusColor);

        // Update score indicators
        userScoreLabel.setText(String.format("<html><center><span style='font-size:11px;color:#9CA3AF;'>YOU</span><br><b style='font-size:22px;color:rgb(%d,%d,%d);'>%d</b></center></html>",
                ACCENT_BLUE.getRed(), ACCENT_BLUE.getGreen(), ACCENT_BLUE.getBlue(), userScore));
        compScoreLabel.setText(String.format("<html><center><span style='font-size:11px;color:#9CA3AF;'>BOT</span><br><b style='font-size:22px;color:rgb(%d,%d,%d);'>%d</b></center></html>",
                ACCENT_RED.getRed(), ACCENT_RED.getGreen(), ACCENT_RED.getBlue(), compScore));
    }

    private void resetGame() {
        userScore = 0;
        compScore = 0;
        roundCount = 0;
        userPickIcon.setText("?");
        compPickIcon.setText("?");
        statusBanner.setText("Game reset. Pick your move!");
        statusBanner.setForeground(TEXT_WHITE);
        historyModel.clear();

        userScoreLabel.setText(String.format("<html><center><span style='font-size:11px;color:#9CA3AF;'>YOU</span><br><b style='font-size:22px;color:rgb(%d,%d,%d);'>0</b></center></html>",
                ACCENT_BLUE.getRed(), ACCENT_BLUE.getGreen(), ACCENT_BLUE.getBlue()));
        compScoreLabel.setText(String.format("<html><center><span style='font-size:11px;color:#9CA3AF;'>BOT</span><br><b style='font-size:22px;color:rgb(%d,%d,%d);'>0</b></center></html>",
                ACCENT_RED.getRed(), ACCENT_RED.getGreen(), ACCENT_RED.getBlue()));
    }

    // Helper class for smooth rounded containers
    private static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color backgroundColor;

        public RoundedPanel(int radius, Color bgColor) {
            this.radius = radius;
            this.backgroundColor = bgColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(backgroundColor);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RockPaperScissorsGUI app = new RockPaperScissorsGUI();
            app.setVisible(true);
        });
    }
}