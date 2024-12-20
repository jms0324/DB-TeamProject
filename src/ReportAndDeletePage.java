import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportAndDeletePage {
    private JFrame frame;
    private JTable resultTable;
    private JCheckBox nameCheckBox, ssnCheckBox, bdateCheckBox, addressCheckBox, sexCheckBox, salaryCheckBox, supervisorCheckBox, departmentCheckBox;
    private DefaultTableModel tableModel;
    private JTextArea selectedNamesArea;
    private JComboBox<String> sortFieldComboBox;
    private boolean isAscending = true; // 기본 정렬은 오름차순으로

    public ReportAndDeletePage() {
        frame = new JFrame("Employee Report");
        frame.setSize(1200, 700); // 화면 크기를 더 크게 설정
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(null);

        JLabel selectFieldsLabel = new JLabel("Select Fields to Display:");
        selectFieldsLabel.setBounds(20, 20, 200, 25);
        frame.add(selectFieldsLabel);

        nameCheckBox = new JCheckBox("Name");
        nameCheckBox.setBounds(20, 50, 100, 25);
        frame.add(nameCheckBox);

        ssnCheckBox = new JCheckBox("SSN");
        ssnCheckBox.setBounds(120, 50, 100, 25);
        ssnCheckBox.setSelected(true); // 기본적으로 체크된 상태로 설정
        frame.add(ssnCheckBox);

        bdateCheckBox = new JCheckBox("Bdate");
        bdateCheckBox.setBounds(220, 50, 100, 25);
        frame.add(bdateCheckBox);

        addressCheckBox = new JCheckBox("Address");
        addressCheckBox.setBounds(320, 50, 100, 25);
        frame.add(addressCheckBox);

        sexCheckBox = new JCheckBox("Sex");
        sexCheckBox.setBounds(420, 50, 100, 25);
        frame.add(sexCheckBox);

        salaryCheckBox = new JCheckBox("Salary");
        salaryCheckBox.setBounds(520, 50, 100, 25);
        frame.add(salaryCheckBox);

        supervisorCheckBox = new JCheckBox("Supervisor");
        supervisorCheckBox.setBounds(620, 50, 100, 25);
        frame.add(supervisorCheckBox);

        departmentCheckBox = new JCheckBox("Department");
        departmentCheckBox.setBounds(720, 50, 150, 25);
        frame.add(departmentCheckBox);

        JLabel sortFieldLabel = new JLabel("Sort by:");
        sortFieldLabel.setBounds(20, 90, 100, 25);
        frame.add(sortFieldLabel);

        sortFieldComboBox = new JComboBox<>(new String[] {"Name", "Ssn", "Bdate", "Address", "Sex", "Salary"});
        sortFieldComboBox.setBounds(100, 90, 130, 25);
        frame.add(sortFieldComboBox);

        JButton sortAscButton = new JButton("오름차순 정렬");
        sortAscButton.setBounds(240, 90, 130, 25);
        frame.add(sortAscButton);

        JButton sortDescButton = new JButton("내림차순 정렬");
        sortDescButton.setBounds(380, 90, 130, 25);
        frame.add(sortDescButton);

        JButton searchButton = new JButton("검색");
        searchButton.setBounds(520, 90, 100, 25);
        frame.add(searchButton);

        JButton backButton = new JButton("뒤로가기");
        backButton.setBounds(1050, 20, 120, 25);
        frame.add(backButton);

        tableModel = new DefaultTableModel() {
            @Override
            public Class<?> getColumnClass(int column) {
                return column == 0 ? Boolean.class : String.class;
            }
        };
        resultTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(resultTable);
        scrollPane.setBounds(20, 130, 1140, 400); // 테이블 크기 조정
        frame.add(scrollPane);

        selectedNamesArea = new JTextArea();
        selectedNamesArea.setBounds(20, 550, 980, 50);
        frame.add(selectedNamesArea);

        JButton deleteButton = new JButton("Delete Selected");
        deleteButton.setBounds(1020, 550, 140, 25);
        frame.add(deleteButton);

        // 검색 버튼 클릭 시
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                executeSearch();
            }
        });

        // 오름차순 정렬 버튼 클릭 시
        sortAscButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isAscending = true;
                executeSearch();
            }
        });

        // 내림차순 정렬 버튼 클릭 시
        sortDescButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isAscending = false;
                executeSearch();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedEmployees();
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

    private void executeSearch() {
        List<String> selectedFields = new ArrayList<>();
        if (nameCheckBox.isSelected()) selectedFields.add("CONCAT(Fname, ' ', Minit, ' ', Lname) AS Name");
        if (ssnCheckBox.isSelected()) selectedFields.add("Ssn");
        if (bdateCheckBox.isSelected()) selectedFields.add("Bdate");
        if (addressCheckBox.isSelected()) selectedFields.add("Address");
        if (sexCheckBox.isSelected()) selectedFields.add("Sex");
        if (salaryCheckBox.isSelected()) selectedFields.add("Salary");
        if (supervisorCheckBox.isSelected()) selectedFields.add("(SELECT Fname FROM EMPLOYEE WHERE Ssn = e.Super_ssn) AS Supervisor");
        if (departmentCheckBox.isSelected()) selectedFields.add("(SELECT Dname FROM DEPARTMENT WHERE Dnumber = e.Dno) AS Department");

        String query = buildQuery(selectedFields);
        loadTableData(query, selectedFields);
    }

    private String buildQuery(List<String> selectedFields) {
        StringBuilder query = new StringBuilder("SELECT ");
        for (int i = 0; i < selectedFields.size(); i++) {
            query.append(selectedFields.get(i));
            if (i < selectedFields.size() - 1) {
                query.append(", ");
            }
        }
        query.append(" FROM EMPLOYEE e");
        // 정렬 조건
        String sortField = (String) sortFieldComboBox.getSelectedItem();
        if (sortField != null) {
            query.append(" ORDER BY ").append(sortField);
            query.append(isAscending ? " ASC" : " DESC");
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

            while (rs.next()) {
                Object[] row = new Object[selectedFields.size() + 1];
                row[0] = false; // Checkbox column
                for (int i = 0; i < selectedFields.size(); i++) {
                    row[i + 1] = rs.getObject(i + 1);
                }
                tableModel.addRow(row);
            }

            // Checkbox 왼쪽 정렬 및 렌더러 설정
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

            // Checkbox 선택 이벤트 추가
            resultTable.getModel().addTableModelListener(e -> updateSelectedNamesArea());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteSelectedEmployees() {
        List<String> selectedSSNs = new ArrayList<>();
        List<Integer> selectedRows = new ArrayList<>();
        int ssnColumnIndex = getColumnIndex("Ssn"); // SSN 열 인덱스 동적으로 찾기

        if (ssnColumnIndex == -1) {
            JOptionPane.showMessageDialog(frame, "SSN 열은 직원 삭제시 꼭 필요합니다 다시 시도해주세요.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        for (int i = 0; i < resultTable.getRowCount(); i++) {
            if ((Boolean) resultTable.getValueAt(i, 0)) {
                selectedSSNs.add((String) resultTable.getValueAt(i, ssnColumnIndex));
                selectedRows.add(i);
            }
        }

        for (String ssn : selectedSSNs) {
            deleteEmployee(ssn);
        }

        for (int i = selectedRows.size() - 1; i >= 0; i--) {
            tableModel.removeRow(selectedRows.get(i));
        }

        updateSelectedNamesArea();
    }

    private void deleteEmployee(String ssn) {
        String query = "DELETE FROM EMPLOYEE WHERE Ssn = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, ssn);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Employee with SSN " + ssn + " deleted successfully.");
            } else {
                System.out.println("No employee found with SSN " + ssn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
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

    private void updateSelectedNamesArea() {
        StringBuilder selectedNames = new StringBuilder("Selected Names: ");
        for (int i = 0; i < resultTable.getRowCount(); i++) {
            if ((Boolean) resultTable.getValueAt(i, 0)) {
                selectedNames.append(resultTable.getValueAt(i, 1)).append(", ");  // Assuming Name is the second column
            }
        }
        selectedNamesArea.setText(selectedNames.toString());
    }

    public static void main(String[] args) {
        new MainPage();
    }
}