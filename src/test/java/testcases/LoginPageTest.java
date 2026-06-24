package testcases;
import pages.LoginPage;
import base.SeleniumBase;
import base.BaseTest;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import base.BaseTest;
import org.junit.jupiter.api.Test;
import utils.PropertiesLoader;
import java.util.Map;
import java.util.Properties;
import static org.junit.jupiter.api.Assertions.*;


public class LoginPageTest extends BaseTest{

    @Test
    public void loadUrl(){
        Map<String, Properties> myProps = PropertiesLoader.loadAll();
        String url2= myProps.get("loginpage").getProperty("url");
        SeleniumBase.getUrl(driver,url2);
        LoginPage.login(driver,"Kapil8748","Kapil_8748");
        
        
    }
}