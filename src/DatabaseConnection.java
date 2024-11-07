import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection { // DatabaseConnection 클래스를 정의

    private static final String URL = "jdbc:mysql://localhost:3306/mydb?useSSL=false&serverTimezone=UTC";
    //데이터베이스 url인데 저는 로컬에서 mydb라는 db를 만들고 실습 파일을 db에 넣은 상태로 진행했습니다!
    private static final String USER = "root"; // 본인걸로 바꿔주세요 !
    private static final String PASSWORD = "spider20!"; // 본인걸로 바꿔주세요 !

    public static Connection getConnection() { // 데이터베이스 연결
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // MySQL JDBC 드라이버 사용하기
            conn = DriverManager.getConnection(URL, USER, PASSWORD); // DB 연결
        } catch (ClassNotFoundException | SQLException e) { // 연결 잘 안됬을때 예외처
            e.printStackTrace();
        }
        return conn;
    }
}