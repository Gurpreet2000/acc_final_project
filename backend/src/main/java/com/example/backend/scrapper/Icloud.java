package com.example.backend.scrapper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class Icloud {
    public static void main(String[] args) {
        Icloud scraper = new Icloud();
        try {
            scraper.init("./temp");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void init(String directory) {
        WebDriver driver = new ChromeDriver();

        try {
            String path = directory + "/icloud_plans.csv";
            // Prepare the CSV file
            FileWriter csvWriter = new FileWriter(path);
            csvWriter.append(
                    "Provider,Plan Name,Price per annum,Price per month,Capacity,File types supported,Platform compatibility,URL,Contact Email,Contact Number,Special features\n");

            File file = new File(path);
            file.getParentFile().mkdirs();

            // Navigate to the iCloud page
            driver.get("https://www.apple.com/ca/icloud/");

            // Automation Tasks: Navigate through the website
            WebElement storeLink = driver
                    .findElement(By.xpath("//*[@id='globalnav-list']/li[2]/div/div/div[1]/ul/li[1]/a"));
            storeLink.click();
            Thread.sleep(3000);

            WebElement imageElement = driver.findElement(
                    By.xpath("//*[@id='shelf-1_section']/div/div[1]/div/div/div[2]/div/div/div/div[1]/img"));
            imageElement.click();
            Thread.sleep(3000);

            WebElement buyButton = driver.findElement(
                    By.xpath("//*[@id='shelf-1_section']/div[2]/div[1]/div/div/div[1]/div/div/div/div[3]/div/a"));
            buyButton.click();
            Thread.sleep(3000);

            driver.navigate().back();
            Thread.sleep(3000);
            driver.navigate().back();
            Thread.sleep(3000);
            driver.navigate().back();
            Thread.sleep(3000);

            // Automation Tasks: Scroll to the plans section
            JavascriptExecutor js = (JavascriptExecutor) driver;
            WebElement scrollToElement = driver.findElement(By.className("hero-compare-grid-item"));
            js.executeScript("arguments[0].scrollIntoView(true);", scrollToElement);
            Thread.sleep(2000);

            // Web Scraping: Extract plan details
            List<WebElement> plans = driver.findElements(By.className("hero-compare-grid-item"));
            for (WebElement plan : plans) {
                String planName = plan.findElement(By.className("typography-caption")).getText();
                String pricePerMonth = plan.findElement(By.className("hero-compare-price")).getText()
                        .replaceAll("[^0-9.]", ""); // Clean price text
                String capacity = plan.findElement(By.className("hero-compare-plan")).getText();
                String specialFeatures = "\"" + plan.findElement(By.className("hero-compare-copy")).getText()
                        .replaceAll("\n", " ") // Replace newline characters with spaces
                        .trim() + "\""; // Remove leading and trailing whitespace
                String planURL = plan.findElement(By.tagName("a")).getAttribute("href");

                // Add data to the CSV
                csvWriter.append(String.format(
                        "iCloud,%s,,%s,%s,all,all,%s,,,%s\n",
                        planName, pricePerMonth, capacity, planURL, specialFeatures));
            }

            csvWriter.flush();
            csvWriter.close();
            System.out.println("CSV file 'icloud_plans.csv' has been created successfully!");

        } catch (IOException e) {
            System.out.println("Error writing to CSV file: " + e.getMessage());
        } catch (InterruptedException e) {
            System.out.println("An error occurred: " + e.getMessage());
        } finally {
            driver.quit();
        }
    }
}
