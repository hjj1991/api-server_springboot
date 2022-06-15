package com.hjj.apiserver.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebScrappingService {

    private final PushService pushService;

    public void liivMateTodayQuizAnswerFind() throws Exception {
        //세션 시작
        ChromeOptions options = new ChromeOptions();
        //페이지가 로드될 때까지 대기
        //Normal: 로드 이벤트 실행이 반환 될 때 까지 기다린다.
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);
        options.setHeadless(true);
        options.addArguments("no-sandbox");
        options.addArguments("disable-dev-shm-usage");

        WebDriver driver = new ChromeDriver(options);

        driver.get("https://google.com");

        String todaySearchValue = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")) + "리브메이트";

        /* 검색창에 입력값 대입 */
        driver.findElement(By.xpath("/html/body/div[1]/div[3]/form/div[1]/div[1]/div[1]/div/div[2]/input")).sendKeys(todaySearchValue);

        driver.findElement(By.xpath("/html/body/div[1]/div[3]/form/div[1]/div[1]/div[3]/center/input[1]")).click();


        List<WebElement> webElement = driver.findElements(By.className("yuRUbf"));
        List<String> urlList = new ArrayList<>();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("리브메이트 " + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")) + "정답 \n");


        for (WebElement element : webElement) {
            urlList.add(element.findElement(By.tagName("a")).getAttribute("href"));
        }

        for (String url : urlList) {
            try{
                driver.navigate().to(url);
                List<WebElement> searchList = driver.findElements(By.xpath("//*[contains(text(),'정답은') or contains(text(), '정답 :')]"));

                for (WebElement searchDetail : searchList) {
                    stringBuilder.append(searchDetail.getText());
                    stringBuilder.append("\n");
                }
            }catch (Exception e){
                log.error(e.getMessage());
                driver = new ChromeDriver(options);
            }
        }
        driver.quit();

        pushService.pushLineNoti(String.valueOf(stringBuilder));
    }
}
