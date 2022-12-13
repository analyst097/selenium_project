import io.netty.util.concurrent.Promise;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;

public class SeleniumDriver {

    static String filename = "output.txt";
    static File myObj = new File(filename);

    static FileWriter myWriter;

    static {
        try {
            myWriter = new FileWriter(myObj);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static WebDriver driver = new ChromeDriver();
    static WebDriverWait webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(60));

    public SeleniumDriver() throws IOException {
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        runDriver();
        myWriter.close();
    }

    static void runDriver() throws InterruptedException {
        String url = "https://saas-eu.whitesourcesoftware.com/Wss/WSS.html#!product;id=288863";

        String email = "";
        String usernameEl = "okta-signin-username";
        String pwdEl = "okta-signin-password";
        String pwd = "";

        driver.get(url);

        String signin = "/html/body/div[4]/table/tbody/tr[2]/td/div[2]/table/tbody/tr/td[1]/table/tbody/tr[9]/td/a";
        webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath(signin)));
        driver.findElement(By.xpath(signin)).click();

        String emailXpath = "/html/body/div[4]/table/tbody/tr[2]/td/div[3]/table/tbody/tr/td/table/tbody/tr[4]/td/input";
        driver.findElement(By.xpath(emailXpath))
                .sendKeys(email);

        String btnXpath = "/html/body/div[4]/table/tbody/tr[2]/td/div[3]/table/tbody/tr/td/table/tbody/tr[6]/td/button";
        driver.findElement(By.xpath(btnXpath)).click();

        webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.id(usernameEl)));
        webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.id(pwdEl)));

        driver.findElement(By.id(usernameEl)).sendKeys(email);
        driver.findElement(By.id(pwdEl)).sendKeys(pwd, Keys.RETURN);

        String adCloudXpath = "/html/body/div[7]/div[1]/div/div[3]/div/div/table/tbody/tr/td/table/tbody/tr[2]/td/table/tbody/tr/td[1]/div/div/table/tbody/tr[3]/td/div[2]/div/div/div[4]/div/div[3]/div/div[2]/div/div/table/tbody/tr[2]/td[1]/div/a";
        webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath(adCloudXpath)));
        driver.findElement(By.xpath(adCloudXpath)).click();


        String libraryAlertsXpath = "/html/body/div[7]/div[1]/div/div[3]/div/div/table/tbody/tr[3]/td/div/table/tbody/tr[1]/td/div/table/tbody/tr/td[1]/div/div/div/table/tbody/tr[3]/td/div[2]/div/table/tbody/tr[3]/td[6]/div/table/tbody/tr[3]/td";
        webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath(libraryAlertsXpath)));
        driver.findElement(By.xpath(libraryAlertsXpath)).click();

        String filterBtnXpath = "/html/body/div[7]/div[1]/div/div[3]/div/div/table/tbody/tr/td/div/div/table/tbody/tr[2]/td/div/table/tbody/tr[1]/td/a/table/tbody/tr/td[1]/img";
        webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath(filterBtnXpath)));
        driver.findElement(By.xpath(filterBtnXpath)).click();

        String filterSelectXpath = "/html/body/div[7]/div[1]/div/div[3]/div/div/table/tbody/tr/td/div/div/table/tbody/tr[2]/td/div/table/tbody/tr[2]/td/div/div/table[1]/tbody/tr/td[1]/select";
        webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath(filterSelectXpath)));
        driver.findElement(By.xpath(filterSelectXpath)).sendKeys("Severity");

        String valueInputXpath = "/html/body/div[7]/div[1]/div/div[3]/div/div/table/tbody/tr/td/div/div/table/tbody/tr[2]/td/div/table/tbody/tr[2]/td/div/div/table[1]/tbody/tr/td[2]/div[2]/input";
        webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath(valueInputXpath)));
        driver.findElement(By.xpath(valueInputXpath)).sendKeys("high", Keys.RETURN);

        String allRowsSelector = "td.FIB tr.EK, td.FIB tr.DL";
        webDriverWait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(allRowsSelector)));

        Thread.sleep(2000);
        String paginationXpath = "/html/body/div[7]/div[1]/div/div[3]/div/div/table/tbody/tr/td/div/div/table/tbody/tr[3]/td/div[2]/div/div[4]/table/tbody/tr/td[3]/div";
        webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(paginationXpath)));
        var txt = driver.findElement(By.xpath(paginationXpath)).getText();
        if(txt.length() > 0){
            var allrows = Integer.parseInt(txt.split("of")[1].trim().replace(",", ""));

            int iterations = (int)Math.floor(allrows / 100);

            System.out.println(iterations);

            getRowsAndTransitiveDeps(iterations);
        }


    }

    static void getRowsAndTransitiveDeps(int iterations) {
        var allRowsSelector = "td.FIB tr.EK, td.FIB tr.DL";
        var transitiveLinkSelector = "td:nth-child(8) a";

        var allrows = driver.findElements(By.cssSelector(allRowsSelector));
        System.out.println("all rows"+allrows.size());


        var i = 0;
        for (WebElement row : allrows) {
            var res = row.getText();
            if (res.contains("Transitive")) {
                var lib = res.split("\n")[0];
                var project = res.split("\n")[2];
                var t = row.findElement(By.cssSelector(transitiveLinkSelector));
                 getDependencyName(t, lib, project);
            }

        }

        if (iterations > 0) {
            var nextPageSelector = "img[aria-label=\"Next page\"]";
            driver.findElement(By.cssSelector(nextPageSelector)).click();
            webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(allRowsSelector)));
            getRowsAndTransitiveDeps(--iterations);
        }
    }

    static boolean getDependencyName(WebElement result, String lib, String project) {
        try {
            result.click();
            webDriverWait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".CY .gwt-Anchor")));
            var deps = driver.findElements(By.cssSelector(".CY .gwt-Anchor"));
            deps.forEach(d -> {
                 var dep = d.getText();
                writeToFile(lib + "," + project + "," + dep + "\n");
                System.out.println(lib + " " + project + " " + dep);
            });
            var dialogOkBtnCss = ".gwt-DialogBox .gwt-Button";
            webDriverWait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(dialogOkBtnCss)));
            driver.findElement(By.cssSelector(dialogOkBtnCss)).click();
            System.out.println("get dependency call");
            return true;

        } catch (Exception e) {
            System.out.println("caught exception" + e);
        }
        return false;

    }

    static void writeToFile(String data){
        try {
                System.out.println("File already exists.");
                myWriter.append(data);

        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}


