import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SearchEmployeePage {
    private JFrame frame;
    private JTable resultTable;
    private JCheckBox nameCheckBox, ssnCheckBox, bdateCheckBox, addressCheckBox, sexCheckBox, salaryCheckBox, supervisorCheckBox, departmentCheckBox;
    private DefaultTableModel tableModel;
    private JComboBox<String> searchConditionBox;
    private JTextField searchValueField;
    private JComboBox<String> departmentBox, sexBox;
    private JLabel messageLabel;

    public SearchEmployeePage() {
        frame = new JFrame("Search Employees");
        frame.setSize(1200, 700);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(null);

        JLabel searchConditionLabel = new JLabel("Search Condition:");
        searchConditionLabel.setBounds(20, 20, 150, 25);
        frame.add(searchConditionLabel);

        searchConditionBox = new JComboBox<>(new String[]{"Department", "Sex", "Salary", "Fname", "Lname", "Minit", "Ssn", "Bdate", "Address", "SuperSsn"});
        searchConditionBox.setBounds(150, 20, 150, 25);
        frame.add(searchConditionBox);

        searchValueField = new JTextField();
        searchValueField.setBounds(310, 20, 150, 25);
        frame.add(searchValueField);

        departmentBox = new JComboBox<>();
        departmentBox.setBounds(310, 20, 150, 25);
        departmentBox.setVisible(false);
        frame.add(departmentBox);

        sexBox = new JComboBox<>(new String[]{"M", "F"});
        sexBox.setBounds(310, 20, 150, 25);
        sexBox.setVisible(false);
        frame.add(sexBox);

        populateDepartmentBox();

        searchConditionBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedCondition = (String) searchConditionBox.getSelectedItem();
                searchValueField.setVisible(false);
                departmentBox.setVisible(false);
                sexBox.setVisible(false);
                if ("Department".equals(selectedCondition)) {
                    departmentBox.setVisible(true);
                } else if ("Sex".equals(selectedCondition)) {
                    sexBox.setVisible(true);
                } else {
                    searchValueField.setVisible(true);
                }
            }
        });

        JLabel selectFieldsLabel = new JLabel("Select Fields to Display:");
        selectFieldsLabel.setBounds(20, 60, 200, 25);
        frame.add(selectFieldsLabel);

        nameCheckBox = new JCheckBox("Name");
        nameCheckBox.setBounds(20, 90, 100, 25);
        frame.add(nameCheckBox);

        ssnCheckBox = new JCheckBox("SSN");
        ssnCheckBox.setBounds(120, 90, 100, 25);
        frame.add(ssnCheckBox);

        bdateCheckBox = new JCheckBox("Bdate");
        bdateCheckBox.setBounds(220, 90, 100, 25);
        frame.add(bdateCheckBox);

        addressCheckBox = new JCheckBox("Address");
        addressCheckBox.setBounds(320, 90, 100, 25);
        frame.add(addressCheckBox);

        sexCheckBox = new JCheckBox("Sex");
        sexCheckBox.setBounds(420, 90, 100, 25);
        frame.add(sexCheckBox);

        salaryCheckBox = new JCheckBox("Salary");
        salaryCheckBox.setBounds(520, 90, 100, 25);
        frame.add(salaryCheckBox);

        supervisorCheckBox = new JCheckBox("Supervisor");
        supervisorCheckBox.setBounds(620, 90, 100, 25);
        frame.add(supervisorCheckBox);

        departmentCheckBox = new JCheckBox("Department");
        departmentCheckBox.setBounds(720, 90, 150, 25);
        frame.add(departmentCheckBox);

        JButton searchButton = new JButton("Search");
        searchButton.setBounds(470, 20, 100, 25);
        frame.add(searchButton);

        messageLabel = new JLabel("");
        messageLabel.setBounds(20, 550, 1140, 25);
        frame.add(messageLabel);

        tableModel = new DefaultTableModel() {
            @Override
            public Class<?> getColumnClass(int column) {
                return column == 0 ? Boolean.class : String.class;
            }
        };
        resultTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(resultTable);
        scrollPane.setBounds(20, 130, 1140, 400);
        frame.add(scrollPane);

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tableModel.setRowCount(0); // Clear the table
                List<String> selectedFields = new ArrayList<>();
                if (nameCheckBox.isSelected()) selectedFields.add("CONCAT(Fname, ' ', Minit, ' ', Lname) AS Name");
                if (ssnCheckBox.isSelected()) selectedFields.add("Ssn");
                if (bdateCheckBox.isSelected()) selectedFields.add("Bdate");
                if (addressCheckBox.isSelected()) selectedFields.add("Address");
                if (sexCheckBox.isSelected()) selectedFields.add("Sex");
                if (salaryCheckBox.isSelected()) selectedFields.add("Salary");
                if (supervisorCheckBox.isSelected()) selectedFields.add("(SELECT Fname FROM EMPLOYEE WHERE Ssn = e.Super_ssn) AS Supervisor");
                if (departmentCheckBox.isSelected()) selectedFields.add("(SELECT Dname FROM DEPARTMENT WHERE Dnumber = e.Dno) AS Department");

                if (selectedFields.isEmpty()) {
                    messageLabel.setText("디스플레이 할 수 있도록 적어도 하나의 필드를 선택해주세요!");
                    return;
                }

                String condition = (String) searchConditionBox.getSelectedItem();
                String value = "";
                if ("Department".equals(condition)) {
                    value = (String) departmentBox.getSelectedItem();
                } else if ("Sex".equals(condition)) {
                    value = (String) sexBox.getSelectedItem();
                } else {
                    value = searchValueField.getText();
                }

                String query = buildQuery(selectedFields, condition, value);
                if (query != null) {
                    loadTableData(query, selectedFields);
                } else {
                    messageLabel.setText("Search failed! Please check the input value.");
                }
            }
        });

        frame.setVisible(true);
    }

    private void populateDepartmentBox() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Dname FROM DEPARTMENT")) {
            while (rs.next()) {
                departmentBox.addItem(rs.getString("Dname"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String buildQuery(List<String> selectedFields, String condition, String value) {
        StringBuilder query = new StringBuilder("SELECT ");
        for (int i = 0; i < selectedFields.size(); i++) {
            query.append(selectedFields.get(i));
            if (i < selectedFields.size() - 1) {
                query.append(", ");
            }
        }
        query.append(" FROM EMPLOYEE e WHERE ");
        if ("Department".equals(condition)) {
            query.append("e.Dno = (SELECT Dnumber FROM DEPARTMENT WHERE Dname = '").append(value).append("')");
        } else if ("Sex".equals(condition)) {
            query.append("e.Sex = '").append(value).append("'");
        } else if ("Salary".equals(condition)) {
            query.append("e.Salary >= ").append(value);
        } else if ("Fname".equals(condition)) {
            query.append("e.Fname = '").append(value).append("'");
        } else if ("Lname".equals(condition)) {
            query.append("e.Lname = '").append(value).append("'");
        } else if ("Minit".equals(condition)) {
            query.append("e.Minit = '").append(value).append("'");
        } else if ("Ssn".equals(condition)) {
            query.append("e.Ssn = '").append(value).append("'");
        } else if ("Bdate".equals(condition)) {
            query.append("e.Bdate = '").append(value).append("'");
        } else if ("Address".equals(condition)) {
            query.append("e.Address = '").append(value).append("'");
        } else if ("SuperSsn".equals(condition)) {
            query.append("e.Super_ssn = '").append(value).append("'");
        } else {
            return null;
        }
        return query.toString();
    }

    private void loadTableData(String query, List<String> selectedFields) {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            tableModel.setRowCount(0);
            tableModel.setColumnCount(0);

            tableModel.addColumn("Select");
            for (String field : selectedFields) {
                if (field.contains(" AS ")) {
                    tableModel.addColumn(field.split(" AS ")[1]);
                } else {
                    tableModel.addColumn(field);
                }
            }

            boolean hasResults = false;
            while (rs.next()) {
                hasResults = true;
                Object[] row = new Object[selectedFields.size() + 1];
                row[0] = false; // Checkbox column
                for (int i = 0; i < selectedFields.size(); i++) {
                    row[i + 1] = rs.getObject(i + 1);
                }
                tableModel.addRow(row);
            }

            resultTable.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    JCheckBox checkBox = new JCheckBox();
                    checkBox.setSelected((Boolean) value);
                    checkBox.setHorizontalAlignment(JLabel.LEFT);
                    return checkBox;
                }
            });
            resultTable.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(new JCheckBox()));

            if (hasResults) {
                messageLabel.setText("검색 성공!");
            } else {
                messageLabel.setText("조건에 맞는 결과가 없습니다!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            messageLabel.setText("검색 실패! 입력값을 다시 확인해주세요!");
        }
    }

    public static void main(String[] args) {
        new SearchEmployeePage();
    }
}