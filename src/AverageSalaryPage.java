import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class AverageSalaryPage {
    private JFrame frame;
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> groupByBox;
    private JTextField supervisorSsnField;

    public AverageSalaryPage() {
        frame = new JFrame("그룹별 평균 월급: ");
        frame.setSize(1200, 700);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(null);

        JLabel groupByLabel = new JLabel("Group By:");
        groupByLabel.setBounds(20, 20, 150, 25);
        frame.add(groupByLabel);

        groupByBox = new JComboBox<>(new String[]{"Sex", "Department", "Supervisor"});
        groupByBox.setBounds(150, 20, 150, 25);
        frame.add(groupByBox);

        supervisorSsnField = new JTextField();
        supervisorSsnField.setBounds(320, 20, 150, 25);
        supervisorSsnField.setVisible(false);
        frame.add(supervisorSsnField);

        JButton searchButton = new JButton("Search");
        searchButton.setBounds(480, 20, 100, 25);
        frame.add(searchButton);

        JButton backButton = new JButton("뒤로가기");
        backButton.setBounds(1050, 20, 120, 25);
        frame.add(backButton);

        tableModel = new DefaultTableModel() {
            @Override
            public Class<?> getColumnClass(int column) {
                return String.class;
            }
        };
        resultTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(resultTable);
        scrollPane.setBounds(20, 60, 1140, 400);
        frame.add(scrollPane);

        groupByBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ("Supervisor".equals(groupByBox.getSelectedItem())) {
                    supervisorSsnField.setVisible(true);
                } else {
                    supervisorSsnField.setVisible(false);
                }
            }
        });

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String groupBy = (String) groupByBox.getSelectedItem();
                String query = buildQuery(groupBy);
                loadTableData(query, groupBy);
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

    private String buildQuery(String groupBy) {
        StringBuilder query = new StringBuilder("SELECT ");
        if ("Sex".equals(groupBy)) {
            query.append("Sex, AVG(Salary) AS AVG_Salary FROM EMPLOYEE GROUP BY Sex");
        } else if ("Department".equals(groupBy)) {
            query.append("(SELECT Dname FROM DEPARTMENT WHERE Dnumber = e.Dno) AS Department, AVG(Salary) AS AVG_Salary FROM EMPLOYEE e GROUP BY e.Dno");
        } else if ("Supervisor".equals(groupBy)) {
            String supervisorSsn = supervisorSsnField.getText();
            query.append("(SELECT CONCAT(Fname, ' ', Minit, ' ', Lname) FROM EMPLOYEE WHERE Ssn = '")
                 .append(supervisorSsn)
                 .append("') AS Supervisor, AVG(Salary) AS AVG_Salary FROM EMPLOYEE WHERE Super_ssn = '")
                 .append(supervisorSsn)
                 .append("' GROUP BY Super_ssn");
        }
        return query.toString();
    }

    private void loadTableData(String query, String groupBy) {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            tableModel.setRowCount(0);
            tableModel.setColumnCount(0);

            if ("Sex".equals(groupBy)) {
                tableModel.addColumn("Sex");
            } else if ("Department".equals(groupBy)) {
                tableModel.addColumn("Department");
            } else if ("Supervisor".equals(groupBy)) {
                tableModel.addColumn("Supervisor");
            }
            tableModel.addColumn("AVG_Salary");

            while (rs.next()) {
                Object[] row = new Object[2];
                row[0] = rs.getString(1);
                row[1] = rs.getDouble(2);
                tableModel.addRow(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new MainPage();
    }
}