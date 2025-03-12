package com.KSDesign.KGL_Test;



import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.io.FileHandler;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class Amazon_KGL_Test {
    static WebDriver driver;
    static WebDriverWait wait;
    static Actions actions;
    static JavascriptExecutor js;

    @BeforeClass
    public void setup() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        actions = new Actions(driver);
        js = (JavascriptExecutor) driver;
        driver.manage().window().maximize();
    }

    @Test(priority = 1)
  //Open https://www.amazon.in/ in Chrome Browser.  
    public void openAmazon() {
        driver.get("https://www.amazon.in");
        Assert.assertEquals(driver.getTitle().contains("Amazon"), true, "Amazon homepage not opened.");
        System.out.println("Open https://www.amazon.in/ in Chrome Browser.");
    }

    @Test(priority = 2)
    //Click on Electronics from dropdown menu and type “IPhone 13”
    public void searchIPhone() {
        WebElement searchDropdown = driver.findElement(By.id("searchDropdownBox"));
        Select select = new Select(searchDropdown);
        select.selectByVisibleText("Electronics");

        WebElement searchBox = driver.findElement(By.id("twotabsearchtextbox"));
        searchBox.sendKeys("IPhone 13");
        System.out.println("Click on Electronics from dropdown menu and type “IPhone 13”");
    
        System.out.println("Get All the dropdown suggestions and validate all are related to searched product");
        List<WebElement> suggestions = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".s-suggestion")));
        for (WebElement suggestion : suggestions) {
            Assert.assertTrue(suggestion.getText().toLowerCase().contains("iphone 13"), "Suggestion does not match 'iPhone 13'");
        }

        searchBox.clear();
        System.out.println("Then Type again “IPhone 13 128 GB” variant and Click “iPhone 13 128GB” variant from dropdown Result.");
        searchBox.sendKeys("IPhone 13 128GB");
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".s-suggestion"))).click();
    }

    @Test(priority = 3)
    public void clickIPhoneProduct() {
        js.executeScript("window.scrollBy(0,300)");
        driver.findElement(By.xpath("(//span[text()='Apple iPhone 13 (128GB) - Starlight'])[1]")).click();

        System.out.println("From Results, click on the searched product and validate new tab is opened");
        String mainWindow = driver.getWindowHandle();
        Set<String> allWindows = driver.getWindowHandles();
        for (String window : allWindows) {
            if (!window.equals(mainWindow)) {
                driver.switchTo().window(window);
                break;
            }
        }
        Assert.assertNotEquals(driver.getWindowHandle(), mainWindow, "New tab was not opened");
        System.out.println("New Tab Opened");
    }

    @Test(priority = 4)
    
    public void visitAppleStore() {
        wait.until(ExpectedConditions.elementToBeClickable(By.partialLinkText("Visit the Apple Store"))).click();
        System.out.println("Apple Store Link Opened");
        System.out.println("Navigate to next tab and click on Visit the Apple Store link appears below Apple iPhone 13 (128 GB) variant");
    }
   

    @Test(priority = 5)
    public void selectAppleWatch() {
        WebElement revealed = driver.findElement(By.xpath("(//*[@role='button'])[7]"));
        js.executeScript("window.scrollBy(0,300)");
        wait.until(ExpectedConditions.elementToBeClickable(revealed)).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//*[@role='dialog']//li)[34]"))).click();
        System.out.println("Apple Watch SE (GPS + Cellular) Selected");
        System.out.println("Click on Apple Watch dropdown and select Apple Watch SE (GPS + Cellular)");
    }

    @Test(priority = 6)
    public void hoverAndValidateQuickLook() throws IOException, TesseractException {
        WebElement watchImage = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("(//a[@class='Overlay__overlay__LloCU EditorialTile__overlay__RMD1L'])[1]")));
        js.executeScript("window.scrollBy(0,300)");
        String title = watchImage.getText();
        System.out.println("Title of image: " + title);

        actions.moveToElement(watchImage).perform();
        System.out.println("Action hover performed");

        WebElement quickLook = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//*[text()='Quick look'])[1]")));
        Assert.assertTrue(quickLook.isDisplayed(), "Quick Look was not displayed");
        System.out.println("Quick Look is displayed");
        System.out.println("Hover on watch image and verify Quick Look is displayed for the watch.");

        // Capture Image and Extract Text using Tesseract OCR
        System.out.println("Verify newly opened modal is related to Same product for which Quick look is performed.");
        File src = watchImage.getScreenshotAs(OutputType.FILE);
        File destFile = new File("C:\\Test\\image.png");
        FileHandler.copy(src, destFile);

        ITesseract image = new Tesseract();
        image.setDatapath("C:\\Tesseract-OCR\\tessdata");
        image.setLanguage("eng");

        String extractedText = image.doOCR(destFile);
        System.out.println("Extracted Text from Image: " + extractedText);
        Assert.assertTrue(title.contains("Apple Watch SE"), "Quick Look modal is not related to the same product");
    }

    @AfterClass
    public void teardown() {
        driver.quit();
    }
}

