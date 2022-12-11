import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.time.Duration;

public class SeleniumDriver {
    public static void main(String[] args) {
        runDriver();
    }

    static void runDriver() {
        WebDriver driver = new ChromeDriver();

        driver.get("https://www.google.com");

        System.out.println(driver.getTitle());

        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));

        WebElement tbox = driver.findElement(By.cssSelector("input.gLFyf"));

        tbox.sendKeys("top netflix shows", Keys.ENTER);

//        driver.quit();
    }
}
