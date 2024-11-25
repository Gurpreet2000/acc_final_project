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

            String path = directory + "/dropbox_plans.csv";

            File file = new File(path);
            file.getParentFile().mkdirs();

            // Prepare CSV file for writing
            FileWriter csvWriter = new FileWriter(path);
            csvWriter.append(
                    "Provider,Plan Name,Price per annum,Price per month,Capacity,File types supported,Platform compatibility,URL,Contact Email,Contact Number,Special features\n");

            // Define the capacities for the plans
            String[] capacities = { "2 TB", "3 TB", "9 TB", "15 TB" };
            // Define special features for each plan
            String[] specialFeatures = {
                    "Transfer files up to 50 GB - Edit PDFs and get signatures",
                    "Transfer files up to 100 GB - Edit PDFs and get signatures",
                    "3+ users - Admin-managed file access",
                    "Transfer files up to 250 GB - End-to-end encryption"
            };

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

            // Write all data to the CSV file
            for (int i = 0; i < planNames.length; i++) {
                csvWriter.append(String.format("Dropbox,%s,%s,%s,%s,all,all,https://www.dropbox.com/plans,,,%s\n",
                        planNames[i],
                        yearlyPricesArray[i],
                        monthlyPricesArray[i],
                        capacities[i],
                        specialFeatures[i]));
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
