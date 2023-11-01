import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Introduction_Screen {
    private JPanel panel1;
    private JRadioButton loadFileRadioButton;
    private JRadioButton loadTestCasesRadioButton;
    private JButton button1;
    private int selectedChoice;

    public Introduction_Screen() {
        // Main panel settings
        panel1 = new JPanel();
        panel1.setLayout(new BorderLayout(20, 20));
        panel1.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel1.setBackground(new Color(240, 248, 255));

        // Title label
        JLabel titleLabel = new JLabel("Welcome to the LMS for Valencia College");
        titleLabel.setFont(new Font("Verdana", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel1.add(titleLabel, BorderLayout.NORTH);

        // Description label
        JLabel descLabel = new JLabel("<html><div style='text-align: center;'>Please choose how you want to proceed.<br> You can either load a file or load test cases.</div></html>");
        descLabel.setFont(new Font("Verdana", Font.PLAIN, 16));
        descLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Radio buttons
        loadFileRadioButton = new JRadioButton("Load File");
        loadTestCasesRadioButton = new JRadioButton("Load Test Cases");
        ButtonGroup group = new ButtonGroup();
        group.add(loadFileRadioButton);
        group.add(loadTestCasesRadioButton);
        loadFileRadioButton.setFont(new Font("Verdana", Font.PLAIN, 18));
        loadTestCasesRadioButton.setFont(new Font("Verdana", Font.PLAIN, 18));

        // Radio button panel
        JPanel radioPanel = new JPanel();
        radioPanel.setLayout(new GridLayout(3, 1, 10, 10));
        radioPanel.setBorder(new TitledBorder(new EmptyBorder(10, 10, 10, 10), "Select Operation",
                TitledBorder.CENTER, TitledBorder.TOP, new Font("Verdana", Font.BOLD, 20), Color.DARK_GRAY));
        radioPanel.setBackground(new Color(245, 245, 245));
        radioPanel.add(descLabel);
        radioPanel.add(loadFileRadioButton);
        radioPanel.add(loadTestCasesRadioButton);

        // Continue button
        button1 = new JButton("Continue");
        button1.setFont(new Font("Verdana", Font.BOLD, 18));
        button1.setBackground(new Color(100, 149, 237));
        button1.setForeground(Color.WHITE);
        button1.setFocusPainted(false);
        button1.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Add to main panel
        panel1.add(radioPanel, BorderLayout.CENTER);
        panel1.add(button1, BorderLayout.SOUTH);

        // Button action
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (loadFileRadioButton.isSelected()) {
                    selectedChoice = 1;
                } else if (loadTestCasesRadioButton.isSelected()) {
                    selectedChoice = 2;
                }
                // Additional logic here
            }
        });
    }

    public JPanel getPanel1() {
        return panel1;
    }

    public int getSelectedChoice() {
        return selectedChoice;
    }
}
