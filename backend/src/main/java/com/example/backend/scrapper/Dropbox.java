package com.example.backend.scrapper;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Dropbox {
    public static void main(String[] args) {
        Dropbox scraper = new Dropbox();
        scraper.init("./temp");
    }

    public void init(String directory) {
        // Initialize WebDriver
        WebDriver driver = new ChromeDriver();

        try {
            String filePath = directory + "/dropbox_plans.csv";
            File file = new File(filePath);

            if (!file.exists()) {
                file.getParentFile().mkdirs();
            }
            // Prepare CSV file for writing
            FileWriter csvWriter = new FileWriter(filePath);
            csvWriter.append(
                    "Provider,Plan Name,Price per annum,Price per month,Capacity,File types supported,Special features,Platform compatibility,URL,Contact Email,Contact Number\n");

            // Extract yearly plan details
            driver.get("https://www.dropbox.com/plans?billing=yearly");
            List<WebElement> yearlyPlans = driver.findElements(By.cssSelector("[data-testid='plan_name_test_id']"));
            List<WebElement> yearlyPrices = driver.findElements(By.cssSelector("[data-testid='price_test_id']"));

            // Store yearly data for reuse with monthly
            String[] planNames = new String[yearlyPlans.size()];
            String[] yearlyPricesArray = new String[yearlyPlans.size()];
            for (int i = 0; i < yearlyPlans.size(); i++) {
                planNames[i] = yearlyPlans.get(i).getText();
                yearlyPricesArray[i] = yearlyPrices.get(i).getText().replaceAll("[^0-9.]", ""); // Remove extra text
            }

            // Extract monthly plan details
            driver.get("https://www.dropbox.com/plans?billing=monthly");
            List<WebElement> monthlyPrices = driver.findElements(By.cssSelector("[data-testid='price_test_id']"));

            String[] monthlyPricesArray = new String[monthlyPrices.size()];
            for (int i = 0; i < monthlyPrices.size(); i++) {
                monthlyPricesArray[i] = monthlyPrices.get(i).getText().replaceAll("[^0-9.]", ""); // Remove extra text
            }

            // Define Capacity XPaths
            String[] capacityXPaths = {
                    "/html/body/div[1]/div/div/main/div[2]/div/div/div/div[1]/div/div/div/div[3]/div/ul/li[2]/span[2]",
                    "/html/body/div[1]/div/div/main/div[2]/div/div/div/div[2]/div/div/div/div[4]/div/ul/li[2]/span[2]",
                    "/html/body/div[1]/div/div/main/div[2]/div/div/div/div[3]/div/div/div[2]/div[4]/div/ul/li[2]/span[2]/span",
                    "/html/body/div[1]/div/div/main/div[2]/div/div/div/div[4]/div/div/div/div[4]/div/ul/li[2]/span[2]/span"
            };

            // Extract Capacity for each plan
            String[] capacities = new String[capacityXPaths.length];
            for (int i = 0; i < capacityXPaths.length; i++) {
                try {
                    WebElement capacityElement = driver.findElement(By.xpath(capacityXPaths[i]));
                    String rawText = capacityElement.getText();
                    String capacityValue = rawText.replaceAll("[^0-9]", "") + " TB"; // Extract numeric value and append
                                                                                     // "TB"
                    capacities[i] = capacityValue; // Store in array
                    System.out.println("Row " + (i + 1) + " Capacity: " + capacityValue); // Debug output
                } catch (Exception e) {
                    System.err.println("Capacity not found for row " + (i + 1) + ": " + e.getMessage());
                    capacities[i] = "null"; // Use "null" if capacity is not found
                }
            }

            // Define Special Features XPaths
            String[][] specialFeaturesXPaths = {
                    { "/html/body/div[1]/div/div/main/div[2]/div/div/div/div[1]/div/div/div/div[3]/div/ul/li[4]/span[2]",
                            "/html/body/div[1]/div/div/main/div[2]/div/div/div/div[1]/div/div/div/div[3]/div/ul/li[5]/span[2]" },
                    { "/html/body/div[1]/div/div/main/div[2]/div/div/div/div[2]/div/div/div/div[4]/div/ul/li[4]/span[2]",
                            "/html/body/div[1]/div/div/main/div[2]/div/div/div/div[2]/div/div/div/div[4]/div/ul/li[5]/span[2]" },
                    { "/html/body/div[1]/div/div/main/div[2]/div/div/div/div[3]/div/div/div[2]/div[4]/div/ul/li[4]/span[2]",
                            "/html/body/div[1]/div/div/main/div[2]/div/div/div/div[3]/div/div/div[2]/div[4]/div/ul/li[5]/span[2]" },
                    { "/html/body/div[1]/div/div/main/div[2]/div/div/div/div[4]/div/div/div/div[4]/div/ul/li[4]/span[2]",
                            "/html/body/div[1]/div/div/main/div[2]/div/div/div/div[4]/div/div/div/div[4]/div/ul/li[5]/span[2]" }
            };

            // Extract Special Features for each plan
            String[] specialFeatures = new String[specialFeaturesXPaths.length];
            for (int i = 0; i < specialFeaturesXPaths.length; i++) {
                try {
                    WebElement feature1 = driver.findElement(By.xpath(specialFeaturesXPaths[i][0]));
                    WebElement feature2 = driver.findElement(By.xpath(specialFeaturesXPaths[i][1]));
                    String feature1Text = feature1.getText();
                    String feature2Text = feature2.getText();
                    specialFeatures[i] = "\"" + "- " + feature1Text + "\n- " + feature2Text + "\""; // Combine both
                                                                                                    // features with a
                    // hyphen
                    System.out.println("Row " + (i + 1) + " Special Features: " + specialFeatures[i]); // Debug output
                } catch (Exception e) {
                    System.err.println("Special Features not found for row " + (i + 1) + ": " + e.getMessage());
                    specialFeatures[i] = "null"; // Use "null" if features are not found
                }
            }

            // URL to be added for each row
            String url = "https://www.dropbox.com/plans?billing=monthly";

            // Write all data to the CSV file
            for (int i = 0; i < planNames.length; i++) {
                csvWriter.append(String.format(
                        "Dropbox,%s,%s,%s,%s,,%s,,%s,,\n",
                        planNames[i],
                        yearlyPricesArray[i],
                        (i < monthlyPricesArray.length) ? monthlyPricesArray[i] : "null",
                        (i < capacities.length) ? capacities[i] : "null",
                        specialFeatures[i], // Add Special Features in the correct column position (11th column)
                        url// Add URL in the correct column position (8th column)
                ));
            }

            csvWriter.flush();
            csvWriter.close();
            System.out.println("CSV file 'dropbox_plans.csv' has been created successfully!");

        } catch (IOException e) {
            System.out.println("Error writing to CSV file: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        } finally {
            // Close the browser
            driver.quit();
        }
    }
}
