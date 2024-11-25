package com.example.backend.scrapper;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Amazon {

    public static void main(String[] args) {
        Amazon scraper = new Amazon();
        scraper.init("./temp");
    }

    public void init(String directory) {
        // Initialize the WebDriver
        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        String path = directory + "/amazon_s3_pricing_table.csv";

        File file = new File(path);
        file.getParentFile().mkdirs();

        try (FileWriter csvWriter = new FileWriter(path)) {
            // Write the required CSV headers
            csvWriter.append(
                    "Provider,Plan Name,Yearly Price,Monthly Price,Capacity,Types Supported,Special Features,Platform Compatibility,URL,Contact Name,Contact Email\n");

            // Step 1: Open the Amazon S3 page and get the Provider Name
            driver.get("https://aws.amazon.com/s3/");
            String providerName = "Amazon S3";

            // Step 2: Check for Popup and Close It
            try {
                WebElement closeButton = wait.until(ExpectedConditions.elementToBeClickable(
                        By.cssSelector(".popup-close-button"))); // Replace with the actual selector for the popup's
                                                                 // close button
                closeButton.click();
                System.out.println("Popup closed successfully.");
            } catch (Exception e) {
                System.out.println("No popup detected or popup not interactable.");
            }

            // Step 3: Navigate to the Pricing Page
            System.out.println("Navigating to the Pricing page...");
            WebElement pricingLink = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("/html/body/header/div[3]/div/div/div[2]/a[4]")));
            pricingLink.click();

            // Extract Plan Names (strong elements)
            List<WebElement> planNameElements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                    By.xpath(".//b | .//strong"))); // This will grab all 'strong' or 'b' elements

            // Extract Capacity using the provided XPaths
            String[] capacityXpaths = {
                    "/html/body/div[2]/main/div[3]/div/div[2]/ul[2]/li[2]/div/div[3]/div/main/div/table/tbody/tr[2]/td[1]",
                    "/html/body/div[2]/main/div[3]/div/div[2]/ul[2]/li[2]/div/div[3]/div/main/div/table/tbody/tr[3]/td[1]",
                    "/html/body/div[2]/main/div[3]/div/div[2]/ul[2]/li[2]/div/div[3]/div/main/div/table/tbody/tr[4]/td[1]",
                    "/html/body/div[2]/main/div[3]/div/div[2]/ul[2]/li[2]/div/div[3]/div/main/div/table/tbody/tr[7]/td[1]",
                    "/html/body/div[2]/main/div[3]/div/div[2]/ul[2]/li[2]/div/div[3]/div/main/div/table/tbody/tr[8]/td[1]",
                    "/html/body/div[2]/main/div[3]/div/div[2]/ul[2]/li[2]/div/div[3]/div/main/div/table/tbody/tr[9]/td[1]"
            };

            // Use a Set to ensure unique plan names
            Set<String> uniquePlanNames = new HashSet<>();

            // Step 4: Scraping Data and Writing to CSV
            int rowsToProcess = planNameElements.size() - 1; // Exclude the last row
            for (int i = 0; i < rowsToProcess; i++) {
                // Replace "AWS re:Invent" in the first row with the second row's value
                String planName;
                if (i == 0) {
                    planName = planNameElements.get(1).getText().trim();
                } else {
                    planName = planNameElements.get(i + 1).getText().trim();
                }

                // Skip empty or duplicate plan names
                if (planName.isEmpty() || !uniquePlanNames.add(planName)) {
                    continue;
                }

                // Monthly price is always empty
                String monthlyPrice = "";

                // Capacity extraction
                String capacity = "";
                try {
                    WebElement capacityElement = driver.findElement(By.xpath(capacityXpaths[i]));
                    String rawCapacity = capacityElement.getText().trim();

                    if (rawCapacity.contains("TB")) {
                        String numericPart = rawCapacity.replaceAll("[^0-9]", "");
                        if (!numericPart.isEmpty()) {
                            capacity = numericPart + " TB";
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Error extracting capacity for row " + i);
                }

                // Special Features extraction
                String specialFeatures = "N/A";
                try {
                    if (i == 0) {
                        // For the first row, extract special features from the provided XPath
                        WebElement specialFeatureElement = driver.findElement(By.xpath(
                                "/html/body/div[2]/main/div[3]/div/div[2]/ul[2]/li[2]/div/div[3]/div/main/div/table/tbody/tr[1]/td[1]"));
                        specialFeatures = specialFeatureElement.getText().trim();
                    } else if (i == 1) {
                        // For the second row, concatenate the two special feature parts with a colon
                        WebElement specialFeaturePart1 = driver.findElement(By.xpath(
                                "/html/body/div[2]/main/div[3]/div/div[2]/ul[2]/li[2]/div/div[3]/div/main/div/table/tbody/tr[2]/td[1]"));
                        WebElement specialFeaturePart2 = driver.findElement(By.xpath(
                                "/html/body/div[2]/main/div[3]/div/div[2]/ul[2]/li[2]/div/div[3]/div/main/div/table/tbody/tr[2]/td[2]"));
                        specialFeatures = specialFeaturePart1.getText().trim() + " : "
                                + specialFeaturePart2.getText().trim();
                    } else if (i == 2) {
                        // For the third row, concatenate the two special feature parts with a colon
                        WebElement specialFeaturePart1 = driver.findElement(By.xpath(
                                "/html/body/div[2]/main/div[3]/div/div[2]/ul[2]/li[2]/div/div[3]/div/main/div/table/tbody/tr[3]/td[1]"));
                        WebElement specialFeaturePart2 = driver.findElement(By.xpath(
                                "/html/body/div[2]/main/div[3]/div/div[2]/ul[2]/li[2]/div/div[3]/div/main/div/table/tbody/tr[3]/td[2]"));
                        specialFeatures = specialFeaturePart1.getText().trim() + " : "
                                + specialFeaturePart2.getText().trim();
                    } else if (i == 3) {
                        // For the fourth row, concatenate the two special feature parts with a colon
                        WebElement specialFeaturePart1 = driver.findElement(By.xpath(
                                "/html/body/div[2]/main/div[3]/div/div[2]/ul[2]/li[2]/div/div[3]/div/main/div/table/tbody/tr[3]/td[1]"));
                        WebElement specialFeaturePart2 = driver.findElement(By.xpath(
                                "/html/body/div[2]/main/div[3]/div/div[2]/ul[2]/li[2]/div/div[3]/div/main/div/table/tbody/tr[4]/td[2]"));
                        specialFeatures = specialFeaturePart1.getText().trim() + " : "
                                + specialFeaturePart2.getText().trim();
                    } else {
                        // For other rows, set special features to "N/A"
                        specialFeatures = "N/A";
                    }
                } catch (Exception e) {
                    System.out.println("Error extracting special features for row " + i);
                }

                // Write to CSV
                csvWriter.append(
                        String.format("%s,%s,%s,%s,%s,All,\"%s\",ALL,https://aws.amazon.com/s3/?nc=sn&loc=1,,\n",
                                providerName, planName, "", monthlyPrice, capacity, specialFeatures));
            }

            csvWriter.flush();
            System.out.println("Data successfully written to amazon_s3_pricing_table.csv");

        } catch (IOException e) {
            System.out.println("Error writing to CSV file: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error during web scraping: " + e.getMessage());
        } finally {
            driver.quit();
        }
    }
}
