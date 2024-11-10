import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddEmployeePage {
    private JFrame frame;
    private JTextField fnameField, minitField, lnameField, ssnField, bdateField, addressField, salaryField, superSsnField, dnoField;
    private JRadioButton maleButton, femaleButton;
    private ButtonGroup sexGroup;
    private JLabel messageLabel;

    public AddEmployeePage() {
        frame = new JFrame("Add Employee");
        frame.setSize(400, 500);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(null);

        JLabel fnameLabel = new JLabel("First Name:");
        fnameLabel.setBounds(20, 20, 100, 25);
        frame.add(fnameLabel);

        fnameField = new JTextField();
        fnameField.setBounds(120, 20, 100, 25);
        frame.add(fnameField);

        JLabel minitLabel = new JLabel("Middle Initial:");
        minitLabel.setBounds(20, 60, 100, 25);
        frame.add(minitLabel);

        minitField = new JTextField();
        minitField.setBounds(120, 60, 100, 25);
        frame.add(minitField);

        JLabel lnameLabel = new JLabel("Last Name:");
        lnameLabel.setBounds(20, 100, 100, 25);
        frame.add(lnameLabel);

        lnameField = new JTextField();
        lnameField.setBounds(120, 100, 100, 25);
        frame.add(lnameField);

        JLabel ssnLabel = new JLabel("SSN:");
        ssnLabel.setBounds(20, 140, 100, 25);
        frame.add(ssnLabel);

        ssnField = new JTextField();
        ssnField.setBounds(120, 140, 100, 25);
        frame.add(ssnField);

        JLabel bdateLabel = new JLabel("Birth Date:");
        bdateLabel.setBounds(20, 180, 100, 25);
        frame.add(bdateLabel);

        bdateField = new JTextField();
        bdateField.setBounds(120, 180, 100, 25);
        frame.add(bdateField);

        JLabel addressLabel = new JLabel("Address:");
        addressLabel.setBounds(20, 220, 100, 25);
        frame.add(addressLabel);

        addressField = new JTextField();
        addressField.setBounds(120, 220, 100, 25);
        frame.add(addressField);

        JLabel sexLabel = new JLabel("Sex:");
        sexLabel.setBounds(20, 260, 100, 25);
        frame.add(sexLabel);

        maleButton = new JRadioButton("M");
        maleButton.setBounds(120, 260, 50, 25);
        femaleButton = new JRadioButton("F");
        femaleButton.setBounds(170, 260, 50, 25);

        sexGroup = new ButtonGroup();
        sexGroup.add(maleButton);
        sexGroup.add(femaleButton);

        frame.add(maleButton);
        frame.add(femaleButton);

        JLabel salaryLabel = new JLabel("Salary:");
        salaryLabel.setBounds(20, 300, 100, 25);
        frame.add(salaryLabel);

        salaryField = new JTextField();
        salaryField.setBounds(120, 300, 100, 25);
        frame.add(salaryField);

        JLabel superSsnLabel = new JLabel("Super SSN:");
        superSsnLabel.setBounds(20, 340, 100, 25);
        frame.add(superSsnLabel);

        superSsnField = new JTextField();
        superSsnField.setBounds(120, 340, 100, 25);
        frame.add(superSsnField);

        JLabel dnoLabel = new JLabel("Dno:");
        dnoLabel.setBounds(20, 380, 100, 25);
        frame.add(dnoLabel);

        dnoField = new JTextField();
        dnoField.setBounds(120, 380, 100, 25);
        frame.add(dnoField);

        JButton addButton = new JButton("Add Employee");
        addButton.setBounds(230, 380, 150, 25);
        frame.add(addButton);

        JButton backButton = new JButton("뒤로가기");
        backButton.setBounds(230, 420, 150, 25);
        frame.add(backButton);

        messageLabel = new JLabel("");
        messageLabel.setBounds(20, 460, 360, 25);
        frame.add(messageLabel);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String sex = maleButton.isSelected() ? "M" : "F";
                boolean success = addEmployee(
                        fnameField.getText(),
                        minitField.getText(),
                        lnameField.getText(),
                        ssnField.getText(),
                        bdateField.getText(),
                        addressField.getText(),
                        sex,
                        Double.parseDouble(salaryField.getText()),
                        superSsnField.getText(),
                        Integer.parseInt(dnoField.getText())
                );
                if (success) {
                    messageLabel.setText("삽입 성공!");
                } else {
                    messageLabel.setText("삽입 실패!");
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });

        frame.setVisible(true);
    }

    private boolean addEmployee(String fname, String minit, String lname, String ssn, String bdate, String address, String sex, double salary, String super_ssn, int dno) {
        String query = "INSERT INTO EMPLOYEE (Fname, Minit, Lname, Ssn, Bdate, Address, Sex, Salary, Super_ssn, Dno, created, modified) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, fname);
            pstmt.setString(2, minit);
            pstmt.setString(3, lname);
            pstmt.setString(4, ssn);
            pstmt.setDate(5, java.sql.Date.valueOf(bdate));
            pstmt.setString(6, address);
            pstmt.setString(7, sex);
            pstmt.setDouble(8, salary);
            pstmt.setString(9, super_ssn);
            pstmt.setInt(10, dno);

            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        new MainPage();
    }
}