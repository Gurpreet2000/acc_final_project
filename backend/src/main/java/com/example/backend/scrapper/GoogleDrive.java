package com.example.backend.scrapper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

public class GoogleDrive {
        public void init() throws IOException {
                // Create a new instance of the ChromeDriver, which will control Chrome
                WebDriver driver = new ChromeDriver();

                // Navigate to the Google Drive pricing page
                driver.get("https://one.google.com/about/plans");

                // scrapeDataFromHome(driver);

                // Get all the pricing cards on the page using a CSS selector
                List<WebElement> pricingList = driver.findElements(By.cssSelector("div.wp6rf > div"));

                // Wait until the list of pricing cards is not empty
                Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(2));
                wait.until(d -> !pricingList.isEmpty());

                // Open a CSV file to store the pricing data
                File file = new File("./data/google-drive.csv");
                file.getParentFile().mkdirs();

                FileWriter pricingFileWriter = new FileWriter("./data/google-drive.csv");

                // Write the header for the CSV file
                pricingFileWriter.append(
                                "Provider,Price per annum,Price per month,Capacity,File types supported,Special features,Platform compatibility,URL,Contact Email,Contact Number\n");

                // Iterate over each pricing card to extract data
                for (WebElement priceCard : pricingList) {
                        String provider = "google", pricePerAnnum = "",
                                        pricePerMonth = !priceCard.findElements(By.cssSelector("div.tKV7vb > span"))
                                                        .isEmpty()
                                                                        ? priceCard.findElement(By.cssSelector(
                                                                                        "div.tKV7vb > span")).getText()
                                                                        : "",
                                        capacity = priceCard.findElement(By.cssSelector("div.Qnu87d.CMqFSd")).getText(),
                                        fileTypesSupported = "",
                                        platformCompatibility = "", url = driver.getCurrentUrl(), contactEmail = "",
                                        contactNumber = "";

                        StringBuilder specialFeatures = new StringBuilder("\"");

                        List<WebElement> featureList = priceCard.findElements(By.cssSelector("ul.OWqi7c > li"));
                        for (WebElement feature : featureList) {
                                WebElement e = feature.findElement(By.cssSelector("span.ZI49d"));
                                String text = "";
                                if (!e.findElements(By.cssSelector("button > span")).isEmpty()) {
                                        WebElement btn = e.findElement(By.cssSelector("button"));
                                        text = btn.findElement(By.cssSelector("span")).getText();
                                        // System.out.println("Button: "
                                        // + text);
                                        // btn.click();
                                        // wait.until(d -> !driver.findElements(By.cssSelector("ul.ZBRIG")).isEmpty());
                                        // List<WebElement> list = driver.findElements(By.cssSelector("ul.ZBRIG > li"));
                                        // for (WebElement element : list) {
                                        // System.out.println(element.findElement(By.cssSelector(".VVs2zc"))
                                        // .getText());
                                        // }
                                } else {
                                        // System.out.println("Text: " + e.getText());
                                        text = e.getText();
                                }
                                specialFeatures.append("- " + text).append("\n");
                        }
                        specialFeatures.append("\"");

                        // extract yearly pricing
                        List<WebElement> tabs = driver
                                        .findElements(By.cssSelector(
                                                        "#upgrade > div.k7aPGc > c-wiz > div.S4aDh > div > button"));
                        if (!tabs.isEmpty()) {
                                tabs.get(1).click();

                                pricePerAnnum = !priceCard.findElements(By.cssSelector("div.tKV7vb > span"))
                                                .isEmpty()
                                                                ? priceCard.findElement(By.cssSelector(
                                                                                "div.tKV7vb > span")).getText()
                                                                : "";
                                tabs.get(0).click();
                                //
                        }

                        pricingFileWriter.append(provider + "," + pricePerAnnum + "," + pricePerMonth + "," + capacity
                                        + ","
                                        + fileTypesSupported + "," +
                                        specialFeatures.toString() + "," + platformCompatibility + "," + url + ","
                                        + contactEmail
                                        + "," + contactNumber);

                        pricingFileWriter.append("\n");
                }

                // Flush and close the CSV file to save the data
                pricingFileWriter.flush();
                pricingFileWriter.close();

                // Close the browser and end the session
                driver.quit();
        }

}