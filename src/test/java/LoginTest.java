import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import lombok.val;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.sql.DriverManager;
import java.sql.SQLException;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class LoginTest {
    private SelenideElement login = $("[data-test-id=login] input");
    private SelenideElement password = $("[data-test-id=password] input");
    private SelenideElement button = $("[data-test-id=action-login]");
    private SelenideElement fieldSmsCode = $("[data-test-id= code] input");
    private SelenideElement buttonSmsCode = $("[data-test-id=\"action-verify\"]");
    private SelenideElement dashboard = $("[data-test-id=\"dashboard\"]");


    @Test
    void registrationTest() throws SQLException {
        open("http://localhost:9999");
        login.setValue("vasya");
        password.setValue("qwerty123");
        button.click();

        val smsCodeAll = "SELECT code FROM db.auth_codes WHERE Created = (SELECT MAX(Created) FROM db.auth_codes);";

        try (
                val conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/db?serverTimezone=Europe/Moscow", "yulia",
                        "1234"
                );
                val smsCode = conn.createStatement();

        ) {
            try (val rs = smsCode.executeQuery(smsCodeAll)) {

                while (rs.next()) {

                    val code = rs.getInt("code");
                    System.out.println(code);
                    fieldSmsCode.setValue(String.valueOf(code));

                }
            }
            buttonSmsCode.shouldBe(Condition.visible).click();
            dashboard.shouldBe(Condition.visible);

        }
    }


    @AfterEach
    void cleanTables() throws SQLException {
        Cleaner.cleanTables();
    }
}
