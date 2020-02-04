package ru.netology.webuitests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WebUITest {
    private WebDriver driver;
    private WebElement nameField;
    private WebElement phoneField;
    private WebElement checkbox;
    private WebElement button;

    @BeforeAll
    static void setupAll() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setUp() {
        driver = new ChromeDriver();
        driver.get("http://localhost:9999");
        nameField = driver.findElement(By.cssSelector("input[type=text]"));
        phoneField = driver.findElement(By.cssSelector("input[name=phone]"));
        checkbox = driver.findElement(By.cssSelector("label.checkbox span.checkbox__box"));
        button = driver.findElement(By.cssSelector("button[type=button]"));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/WebUITestData.csv", numLinesToSkip = 1)
    void checkAppOrderService(String name, String phone, String selector, String expected, String message) {
        nameField.sendKeys(name);
        phoneField.sendKeys(phone);
        checkbox.click();
        button.click();
        String text = driver.findElement(By.cssSelector(selector)).getText();
        assertEquals(expected, text.trim(), message);
    }

    @Test
    @DisplayName("Корректный ввод после ошибочного ввода")
    void correctInputAfterIncorrect() {
        button.click();
        String text1 = driver.findElement(By.cssSelector("span.input_invalid span.input__sub")).getText();
        assertEquals("Поле обязательно для заполнения", text1.trim());
        nameField.sendKeys("Нефедова Анастасия");
        button.click();
        String text2 = driver.findElement(By.cssSelector("span.input_invalid span.input__sub")).getText();
        assertEquals("Поле обязательно для заполнения", text2.trim());
        phoneField.sendKeys("+79040402204");
        button.click();
        WebElement element = driver.findElement(By.cssSelector("label.checkbox span.checkbox__text"));
        assertEquals("rgba(255, 92, 92, 1)", element.getCssValue("color"));
        checkbox.click();
        button.click();
        String text3 = driver.findElement(By.cssSelector("p.paragraph[data-test-id=order-success]")).getText();
        assertEquals("Ваша заявка успешно отправлена! Наш менеджер свяжется с вами в ближайшее время.",
                text3.trim());
    }

    @Test
    @DisplayName("Не заполнен чекбокс")
    void checkboxIsEmpty() {
        nameField.sendKeys("Нефедова Анастасия");
        phoneField.sendKeys("+79040402204");
        button.click();
        WebElement element = driver.findElement(By.cssSelector("label.checkbox span.checkbox__text"));
        assertEquals("rgba(255, 92, 92, 1)", element.getCssValue("color"));
    }

    @Test
    @DisplayName("256 символов в поле Имя")
    void input256CharsInNameField() {
        for (int i = 0; i < 13; i++) {
            nameField.sendKeys("Проверка");
        }
        phoneField.sendKeys("+79040402204");
        checkbox.click();
        button.click();
        String text = driver.findElement(By.cssSelector("span.input_invalid span.input__sub")).getText();
        assertEquals("Имя и Фамилия указаны неверно. Допустимы только русские буквы, пробелы и дефисы.",
                text.trim());
    }
}