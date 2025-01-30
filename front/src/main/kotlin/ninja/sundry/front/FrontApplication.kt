package ninja.sundry.front

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FrontApplication

fun main(args: Array<String>) {
	runApplication<FrontApplication>(*args)
}
