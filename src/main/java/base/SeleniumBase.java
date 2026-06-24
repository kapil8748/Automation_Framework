package base;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import java.time.Duration;

public class SeleniumBase {

    private static WebDriver driver;

    /**
     * Factory method to setup and fetch a tailored WebDriver instance
     * @param browser "chrome" or "firefox"
     * @param headless true to run execution in the background without UI
     */
    public static WebDriver initializeDriver(String browser, boolean headless) {
        String cleanBrowser = browser.trim().toLowerCase();

        System.out.println("Step 1: SeleniumLoader initializing " + cleanBrowser + " driver...");

        switch (cleanBrowser) {
            case "chrome":
                System.setProperty("webdriver.chrome.driver", "/usr/bin/chromedriver");
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.setBinary("/usr/bin/chromium"); 
                if (headless) {
                    chromeOptions.addArguments("--headless=new");
                }
                chromeOptions.addArguments("--no-sandbox");
                chromeOptions.addArguments("--disable-dev-shm-usage");
                driver = new ChromeDriver(chromeOptions);
                break;

            case "firefox":
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                if (headless) {
                    firefoxOptions.addArguments("-headless");
                }
                driver = new FirefoxDriver(firefoxOptions);
                break;

            default:
                throw new IllegalArgumentException("Unsupported browser framework: " + browser);
        }

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();

        System.out.println("Step 2: Browser window generated and ready for automation.");
        return driver;
    }
    public static void getUrl(WebDriver driver, String url) {
        driver.get(url);
    }

    /**
     * Close and cleanup the driver instance
     */
    public static void closeDriver() {
        if (driver != null) {
            driver.quit();
            System.out.println("Step 3: Browser closed and driver quit successfully.");
            driver = null;
        }
    }
}
