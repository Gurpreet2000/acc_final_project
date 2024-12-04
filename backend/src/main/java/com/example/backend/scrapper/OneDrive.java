package com.example.backend.scrapper;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OneDrive {

        private static final String MICROSOFT = "Microsoft";

        public static void main(String[] args) {
                OneDrive scraper = new OneDrive();
                scraper.init("./temp");
        }

        public void init(String directory) {

                // Initialize WebDriver
                WebDriver driver = new ChromeDriver();
                List<CloudService> cloudServices = new ArrayList<>();

                // Navigate to the OneDrive page
                driver.get("https://www.microsoft.com/en-ca/microsoft-365/onedrive/compare-onedrive-plans");

                WebElement baharKaContainer = driver.findElement(
                                By.cssSelector("div.sku-cards.grid.g-col-12.g-start-1.aem-GridColumn.aem-GridColumn--default--12"));

                CloudService cloudService = new CloudService();
                cloudService.setProvider(MICROSOFT);
                cloudService.setPlanName(baharKaContainer.findElement(By.cssSelector(
                                "div.sku-title.oc-product-title.px-4.text-center.g-col-12.g-start-1.g-col-sm-6.g-col-md-5.g-col-lg-3.g-start-sm-1.g-start-md-2.g-start-lg-1"))
                                .findElement(By.tagName("span")).getText());
                cloudService.setPricePerAnnum(baharKaContainer.findElement(By.cssSelector(
                                "div.sku-card-price.px-4.text-center.g-col-12.g-start-1.g-col-sm-6.g-col-md-5.g-col-lg-3.g-start-sm-1.g-start-md-2.g-start-lg-1"))
                                .findElement(By.cssSelector("span.oc-list-price.font-weight-semibold.text-primary"))
                                .getText());
                cloudService.setPricePerMonth(
                                baharKaContainer.findElement(By.cssSelector("div.w-col-7.w-md-col-10.mx-auto"))
                                                .findElement(By.cssSelector("span.oc-token.oc-list-price")).getText());
                cloudService.setCapacity(baharKaContainer.findElement(By.cssSelector(".card-body"))
                                .findElement(By.xpath("//*[@id=\"custom-list-item-oce04e\"]/div/p/span")).getText());
                cloudService.setSpecialFeatures(
                                "\"- " + (baharKaContainer
                                                .findElement(By.xpath(
                                                                "//*[@id=\"custom-list-item-oca298\"]/div/p/span"))
                                                .getText()) + "\"");
                cloudService.setPlatformCompatibility(
                                (baharKaContainer
                                                .findElement(By.xpath(
                                                                "//*[@id=\"custom-list-item-oc4ada\"]/div/p/span"))
                                                .getText()));

                cloudService.setUrl(driver.getCurrentUrl());
                cloudServices.add(cloudService);

                CloudService cloudService1 = new CloudService();
                cloudService1.setProvider(MICROSOFT);
                cloudService1.setPlanName(baharKaContainer.findElement(By.cssSelector(
                                "div.sku-title.oc-product-title.px-4.text-center.g-col-12.g-start-1.g-col-sm-6.g-col-md-5.g-col-lg-3.g-start-sm-7.g-start-md-7.g-start-lg-4"))
                                .findElement(By.tagName("span")).getText());
                cloudService1.setPricePerAnnum("CAD $79.00");
                cloudService1.setPricePerMonth((baharKaContainer.findElement(By.xpath(
                                "/html/body/div[3]/div/div[2]/main/div/div/div/div[3]/div/div/div/div/section/div/div[2]/div/div/div[1]/div/div/div/div/div/div[1]/div[2]/div[1]/div[2]/div[6]/div/div/div[2]/a/div[2]/div/div/div/span/span"))
                                .getText()));
                cloudService1.setCapacity(baharKaContainer.findElement(By.cssSelector(".card-body"))
                                .findElement(By.xpath("//*[@id=\"custom-list-item-ocda3c\"]/div/p/span")).getText());
                cloudService1.setSpecialFeatures(
                                "\"- " + (baharKaContainer
                                                .findElement(By.xpath(
                                                                "//*[@id=\"custom-list-item-oca298\"]/div/p/span"))
                                                .getText()) + "\"");
                cloudService1.setPlatformCompatibility(
                                (baharKaContainer
                                                .findElement(By.xpath(
                                                                "//*[@id=\"custom-list-item-oc4ada\"]/div/p/span"))
                                                .getText()));

                cloudService1.setUrl(driver.getCurrentUrl());
                cloudServices.add(cloudService1);

                CloudService cloudService2 = new CloudService();
                cloudService2.setProvider(MICROSOFT);
                cloudService2.setPlanName(baharKaContainer.findElement(By.cssSelector(
                                "div.sku-title.oc-product-title.px-4.text-center.g-col-12.g-start-1.g-col-sm-6.g-col-md-5.g-col-lg-3.g-start-sm-1.g-start-md-2.g-start-lg-7"))
                                .findElement(By.tagName("span")).getText());
                cloudService1.setPricePerAnnum("CAD $8.00");
                cloudService2.setPricePerMonth((baharKaContainer.findElement(By.xpath(
                                "/html/body/div[3]/div/div[2]/main/div/div/div/div[3]/div/div/div/div/section/div/div[2]/div/div/div[1]/div/div/div/div/div/div[1]/div[2]/div[2]/div[1]/div[6]/div/div/ul/li[1]/a/div[2]/div/div/div/span[1]/span"))
                                .getText()));
                cloudService2.setCapacity(baharKaContainer.findElement(By.cssSelector(".card-body"))
                                .findElement(By.xpath("//*[@id=\"custom-list-item-oc147e\"]/div/p/span")).getText());
                cloudService2.setSpecialFeatures(
                                "\"- " + (baharKaContainer
                                                .findElement(By.xpath(
                                                                "//*[@id=\"custom-list-item-oc26f8\"]/div/p/span"))
                                                .getText()) + "\"");
                cloudService2.setPlatformCompatibility(
                                (baharKaContainer
                                                .findElement(By.xpath(
                                                                "//*[@id=\"custom-list-item-oc4ada\"]/div/p/span"))
                                                .getText()));

                cloudService2.setUrl(driver.getCurrentUrl());
                cloudServices.add(cloudService2);

                CloudService cloudService3 = new CloudService();
                cloudService3.setProvider(MICROSOFT);
                cloudService3.setPlanName(baharKaContainer.findElement(By.cssSelector(
                                "div.sku-title.oc-product-title.px-4.text-center.g-col-12.g-start-1.g-col-sm-6.g-col-md-5.g-col-lg-3.g-start-sm-7.g-start-md-7.g-start-lg-10"))
                                .findElement(By.tagName("span")).getText());
                cloudService1.setPricePerAnnum((baharKaContainer
                                .findElement(By.xpath("//*[@id=\"sku-card-oc0cb3\"]/div[5]/div[1]/div/p[2]/span[2]"))
                                .getText()));
                cloudService3.setPricePerMonth((baharKaContainer
                                .findElement(By.xpath("//*[@id=\"sku-card-oc0cb3\"]/div[5]/div[1]/div/p[2]/span[2]"))
                                .getText()));
                cloudService3.setCapacity(baharKaContainer.findElement(By.cssSelector(".card-body"))
                                .findElement(By.xpath("//*[@id=\"custom-list-item-oc5bb0\"]/div/p/span")).getText());
                cloudService3.setSpecialFeatures(
                                "\"- " + (baharKaContainer
                                                .findElement(By.xpath(
                                                                "//*[@id=\"custom-list-item-oc2279\"]/div/p/span"))
                                                .getText()) + "\"");
                cloudService3.setPlatformCompatibility(
                                (baharKaContainer
                                                .findElement(By.xpath(
                                                                "//*[@id=\"custom-list-item-oc0e28\"]/div/p/span"))
                                                .getText()));

                cloudService3.setUrl(driver.getCurrentUrl());
                cloudServices.add(cloudService3);

                // Write the data to a CSV file
                writeToCsv(cloudServices, directory);

                // Close the driver
                driver.quit();
        }

        private static void writeToCsv(List<CloudService> cloudServices, String directory) {
                String path = directory + "/oneDrive.csv";
                // Prepare the CSV file
                File file = new File(path);
                file.getParentFile().mkdirs();

                try (FileWriter csvWriter = new FileWriter(path)) {
                        // Write the CSV headers
                        csvWriter.append(
                                        "Provider,Plan Name,Price per annum,Price per month,Capacity,File types supported,Special features,Platform compatibility,URL,Contact Email,Contact Number\n");

                        // Write each CloudService object to the CSV
                        for (CloudService service : cloudServices) {
                                csvWriter.append(service.getProvider()).append(",");
                                csvWriter.append(service.getPlanName()).append(",");
                                csvWriter.append(service.getPricePerAnnum()).append(",");
                                csvWriter.append(service.getPricePerMonth()).append(",");
                                csvWriter.append(service.getCapacity()).append(",");
                                csvWriter.append(",");
                                csvWriter.append(service.getSpecialFeatures()).append(",");
                                csvWriter.append(service.getPlatformCompatibility()).append(",");
                                csvWriter.append(service.getUrl()).append(",");
                                csvWriter.append(",\n");
                        }

                        System.out.println("Data successfully written to CloudServices.csv");
                } catch (IOException e) {
                        System.err.println("Error while writing to CSV file: " + e.getMessage());
                }
        }
}

class CloudService {
        private String provider;
        private String planName;
        private String pricePerAnnum;
        private String pricePerMonth;
        private String capacity;
        private String fileTypesSupported;
        private String specialFeatures;
        private String platformCompatibility;
        private String url;

        public String getProvider() {
                return provider;
        }

        public void setProvider(String provider) {
                this.provider = provider;
        }

        public String getPlanName() {
                return planName;
        }

        public void setPlanName(String planName) {
                this.planName = planName;
        }

        public String getPricePerAnnum() {
                return pricePerAnnum;
        }

        public void setPricePerAnnum(String pricePerAnnum) {
                this.pricePerAnnum = pricePerAnnum;
        }

        public String getPricePerMonth() {
                return pricePerMonth;
        }

        public void setPricePerMonth(String pricePerMonth) {
                this.pricePerMonth = pricePerMonth;
        }

        public String getCapacity() {
                return capacity;
        }

        public void setCapacity(String capacity) {
                // Regular expression to match storage sizes (e.g., "1 GB", "1 TB")
                Pattern pattern = Pattern.compile("(\\d+\\s?(GB|TB))");
                Matcher matcher = pattern.matcher(capacity);

                if (matcher.find()) {
                        this.capacity = matcher.group(1); // Extract the matched storage size
                } else {
                        this.capacity = ""; // Fallback value if no match is found
                }
        }

        public String getSpecialFeatures() {
                return specialFeatures;
        }

        public void setSpecialFeatures(String specialFeatures) {
                this.specialFeatures = specialFeatures;
        }

        public String getPlatformCompatibility() {
                return "\"- " + platformCompatibility.replace("Works on ", "").replace(",", "\n- ").replace("and ",
                                "") + "\"";
        }

        public void setUrl(String url) {
                this.url = url;
        }

        public String getUrl() {
                return url;
        }

        public void setPlatformCompatibility(String platformCompatibility) {
                this.platformCompatibility = platformCompatibility;
        }

        @Override
        public String toString() {
                return "CloudService{" +
                                "provider='" + provider + '\'' +
                                ", planName='" + planName + '\'' +
                                ", pricePerAnnum='" + pricePerAnnum + '\'' +
                                ", pricePerMonth='" + pricePerMonth + '\'' +
                                ", capacity='" + capacity + '\'' +
                                ", specialFeatures='" + specialFeatures + '\'' +
                                ", platformCompatibility='" + platformCompatibility + '\'' +
                                '}';
        }
}