import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainPage {
    private JFrame frame;

    public MainPage() {
        frame = new JFrame("110조 DB 팀프로젝트");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        JLabel titleLabel = new JLabel("110조 DB 팀프로젝트 입니다!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 24));
        titleLabel.setBounds(100, 50, 600, 50);
        frame.add(titleLabel);

        JButton addEmployeeButton = createButton("직원 추가하기", 100, 150, Color.CYAN);
        JButton averageSalaryButton = createButton("그룹별 평균급여구하기", 100, 250, Color.MAGENTA);
        JButton reportPageButton = createButton("전체 출력 및 삭제", 100, 350, Color.ORANGE);
        JButton searchAndUpdateButton = createButton("조건검색 및 수정하기", 100, 450, Color.GREEN);

        addEmployeeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AddEmployeePage();
            }
        });

        averageSalaryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AverageSalaryPage();
            }
        });

        reportPageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ReportAndDeletePage();
            }
        });

        searchAndUpdateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SearchAndUpdateEmployeePage();
            }
        });

        frame.add(addEmployeeButton);
        frame.add(averageSalaryButton);
        frame.add(reportPageButton);
        frame.add(searchAndUpdateButton);

        frame.setVisible(true);
    }

    private JButton createButton(String text, int x, int y, Color color) {
        JButton button = new JButton(text);
        button.setBounds(x, y, 600, 50);
        button.setBackground(color);
        button.setFont(new Font("Serif", Font.BOLD, 18));
        return button;
    }

    public static void main(String[] args) {
        new MainPage();
    }
}