import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SearchAndUpdateEmployeePage {
    private JFrame frame;
    private JTable resultTable;
    private JCheckBox nameCheckBox, ssnCheckBox, bdateCheckBox, addressCheckBox, sexCheckBox, salaryCheckBox, supervisorCheckBox, departmentCheckBox;
    private DefaultTableModel tableModel;
    private JComboBox<String> searchConditionBox, updateColumnBox;
    private JTextField searchValueField, updateValueField;
    private JComboBox<String> departmentBox, sexBox;
    private JLabel messageLabel;

    public SearchAndUpdateEmployeePage() {
        frame = new JFrame("Search and Update Employees");
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

        JButton selectAllButton = new JButton("Select All");
        selectAllButton.setBounds(880, 90, 100, 25);
        frame.add(selectAllButton);

        selectAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nameCheckBox.setSelected(true);
                ssnCheckBox.setSelected(true);
                bdateCheckBox.setSelected(true);
                addressCheckBox.setSelected(true);
                sexCheckBox.setSelected(true);
                salaryCheckBox.setSelected(true);
                supervisorCheckBox.setSelected(true);
                departmentCheckBox.setSelected(true);
            }
        });

        JButton searchButton = new JButton("Search");
        searchButton.setBounds(470, 20, 100, 25);
        frame.add(searchButton);

        messageLabel = new JLabel("");
        messageLabel.setBounds(20, 550, 1140, 25);
        frame.add(messageLabel);

        tableModel = new DefaultTableModel() {
            @Override
            public Class<?> getColumnClass(int column) {
                return String.class;
            }
        };
        resultTable = new JTable(tableModel);
        resultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Allow only single selection
        JScrollPane scrollPane = new JScrollPane(resultTable);
        scrollPane.setBounds(20, 130, 1140, 400);
        frame.add(scrollPane);

        JLabel updateColumnLabel = new JLabel("Update Column:");
        updateColumnLabel.setBounds(20, 600, 150, 25);
        frame.add(updateColumnLabel);

        updateColumnBox = new JComboBox<>(new String[]{"Fname", "Minit", "Lname", "Ssn", "Bdate", "Address", "Sex", "Salary", "Super_ssn", "Dno"});
        updateColumnBox.setBounds(150, 600, 150, 25);
        frame.add(updateColumnBox);

        updateValueField = new JTextField();
        updateValueField.setBounds(310, 600, 150, 25);
        frame.add(updateValueField);

        JButton updateButton = new JButton("Update");
        updateButton.setBounds(470, 600, 100, 25);
        frame.add(updateButton);

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                executeSearch("검색 성공!");
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = resultTable.getSelectedRow();
                if (selectedRow == -1) {
                    messageLabel.setText("수정할 행을 선택해주세요!");
                    return;
                }

                if (resultTable.getSelectedRowCount() > 1) {
                    messageLabel.setText("수정 실패! 하나의 행만 선택할 수 있습니다!");
                    return;
                }

                String updateColumn = (String) updateColumnBox.getSelectedItem();
                String updateValue = updateValueField.getText();

                if ("Ssn".equals(updateColumn)) {
                    messageLabel.setText("테이블의 기본키는 변경할 수 없습니다!");
                    return;
                }

                if ("Bdate".equals(updateColumn) && !isValidDate(updateValue)) {
                    messageLabel.setText("유효한 날짜를 입력해주세요! (형식: YYYY-MM-DD)");
                    return;
                }

                if ("Sex".equals(updateColumn) && !isValidSex(updateValue)) {
                    messageLabel.setText("유효한 성별을 입력해주세요! (M 또는 F)");
                    return;
                }

                if ("Salary".equals(updateColumn) && !isValidSalary(updateValue)) {
                    messageLabel.setText("유효한 급여를 입력해주세요! (숫자)");
                    return;
                }

                if ("Address".equals(updateColumn) && !isValidAddress(updateValue)) {
                    messageLabel.setText("유효한 주소를 입력해주세요!");
                    return;
                }

                if (("Fname".equals(updateColumn) && !isValidFname(updateValue)) ||
                        ("Minit".equals(updateColumn) && !isValidMinit(updateValue)) ||
                        ("Lname".equals(updateColumn) && !isValidLname(updateValue))) {
                    messageLabel.setText("유효한 이름을 입력해주세요!");
                    return;
                }

                if ("Dno".equals(updateColumn) && !isValidDno(updateValue)) {
                    messageLabel.setText("없는 Dno입니다. 입력값을 다시 확인해주세요!");
                    return;
                }

                if (("Fname".equals(updateColumn) || "Lname".equals(updateColumn) || "Ssn".equals(updateColumn)) && (updateValue == null || updateValue.trim().isEmpty())) {
                    messageLabel.setText("입력값이 필요합니다!");
                    return;
                }

                String ssn = (String) resultTable.getValueAt(selectedRow, getColumnIndex("Ssn")); // Get SSN column index
                if (ssn != null) {
                    if ("Super_ssn".equals(updateColumn) && !isValidSuperSsn(updateValue)) {
                        messageLabel.setText("없는 SuperSsn입니다. 입력값을 다시 확인해주세요!");
                        return;
                    }
                    updateDatabase(updateColumn, updateValue, ssn);
                }

                executeSearch("수정 성공!"); // 테이블을 다시 로드하고 메시지 설정
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
        query.append(" FROM EMPLOYEE e");
        if (condition != null && !condition.isEmpty()) {
            query.append(" WHERE ");
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
            }
        }
        return query.toString();
    }

    private void loadTableData(String query, List<String> selectedFields) {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            tableModel.setRowCount(0);
            tableModel.setColumnCount(0);

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
                Object[] row = new Object[selectedFields.size()];
                for (int i = 0; i < selectedFields.size(); i++) {
                    row[i] = rs.getObject(i + 1);
                }
                tableModel.addRow(row);
            }

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

    private void updateDatabase(String column, String value, String ssn) {
        String query = "UPDATE EMPLOYEE SET " + column + " = ? WHERE Ssn = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            if (value == null || value.isEmpty()) {
                pstmt.setNull(1, Types.VARCHAR);
            } else {
                pstmt.setString(1, value);
            }
            pstmt.setString(2, ssn);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            messageLabel.setText("업데이트 실패! 입력값을 다시 확인해주세요!");
        }
    }

    private int getColumnIndex(String columnName) {
        for (int i = 0; i < resultTable.getColumnCount(); i++) {
            if (resultTable.getColumnName(i).equals(columnName)) {
                return i;
            }
        }
        return -1;
    }

    private boolean isValidSuperSsn(String superSsn) {
        String query = "SELECT COUNT(*) FROM EMPLOYEE WHERE Ssn = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, superSsn);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isValidDno(String dno) {
        String query = "SELECT COUNT(*) FROM DEPARTMENT WHERE Dnumber = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, Integer.parseInt(dno));
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isValidDate(String date) {
        try {
            java.sql.Date.valueOf(date);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private boolean isValidSex(String sex) {
        return "M".equals(sex) || "F".equals(sex);
    }

    private boolean isValidSalary(String salary) {
        try {
            Double.parseDouble(salary);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isValidAddress(String address) {
        return address != null && !address.trim().isEmpty();
    }

    private boolean isValidFname(String fname) {
        return fname != null && fname.length() <= 15;
    }

    private boolean isValidMinit(String minit) {
        return minit != null && minit.length() <= 1;
    }

    private boolean isValidLname(String lname) {
        return lname != null && lname.length() <= 15;
    }

    private void executeSearch(String message) {
        List<String> selectedFields = new ArrayList<>();
        if (nameCheckBox.isSelected()) selectedFields.add("CONCAT(Fname, ' ', Minit, ' ', Lname) AS Name");
        if (ssnCheckBox.isSelected()) selectedFields.add("Ssn");
        if (bdateCheckBox.isSelected()) selectedFields.add("Bdate");
        if (addressCheckBox.isSelected()) selectedFields.add("Address");
        if (sexCheckBox.isSelected()) selectedFields.add("Sex");
        if (salaryCheckBox.isSelected()) selectedFields.add("Salary");
        if (supervisorCheckBox.isSelected()) selectedFields.add("(SELECT Fname FROM EMPLOYEE WHERE Ssn = e.Super_ssn) AS Supervisor");
        if (departmentCheckBox.isSelected()) selectedFields.add("(SELECT Dname FROM DEPARTMENT WHERE Dnumber = e.Dno) AS Department");

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
        loadTableData(query, selectedFields);
        messageLabel.setText(message);
    }

    public static void main(String[] args) {
        new SearchAndUpdateEmployeePage();
    }
}