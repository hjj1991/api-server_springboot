package com.hjj.apiserver.service.impl

import com.hjj.apiserver.util.logger
import org.openqa.selenium.By
import org.openqa.selenium.PageLoadStrategy
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class WebScrappingService(
    private val pushService: PushService,
) {
    private val log = logger()

    fun liivMateTodayQuizAnswerFind() {
        // 세션 시작

        // 세션 시작
        val options = ChromeOptions()
        // 페이지가 로드될 때까지 대기
        // Normal: 로드 이벤트 실행이 반환 될 때 까지 기다린다.
        // 페이지가 로드될 때까지 대기
        // Normal: 로드 이벤트 실행이 반환 될 때 까지 기다린다.
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL)
//        options.setHeadless(true)
        options.addArguments("no-sandbox")
        options.addArguments("disable-dev-shm-usage")

        var driver: WebDriver = ChromeDriver(options)

        driver["https://google.com"]

        val todaySearchValue = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")) + "리브메이트"

        // 검색창에 입력값 대입

        // 검색창에 입력값 대입
        driver.findElement(By.xpath("/html/body/div[1]/div[3]/form/div[1]/div[1]/div[1]/div/div[2]/input"))
            .sendKeys(todaySearchValue)

        driver.findElement(By.xpath("/html/body/div[1]/div[3]/form/div[1]/div[1]/div[3]/center/input[1]")).click()

        val webElement = driver.findElements(By.className("yuRUbf"))
        val urlList: MutableList<String> = ArrayList()

        val stringBuilder = StringBuilder()
        stringBuilder.append(
            """
                리브메이트 ${
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"))
            }정답 
                
            """.trimIndent(),
        )

        for (element in webElement) {
            urlList.add(element.findElement(By.tagName("a")).getAttribute("href"))
        }

        for (url in urlList) {
            try {
                driver.navigate().to(url)
                val searchList =
                    driver.findElements(By.xpath("//*[contains(text(),'정답은') or contains(text(), '정답 :')]"))
                for (searchDetail in searchList) {
                    stringBuilder.append(searchDetail.text)
                    stringBuilder.append("\n")
                }
            } catch (e: Exception) {
                log.error(e.message)
                driver = ChromeDriver(options)
            }
        }
        driver.quit()

        pushService.pushLineNoti(stringBuilder.toString())
    }
}
