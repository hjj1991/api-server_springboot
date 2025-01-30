package ninja.sundry.financial

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FinancialApplication

fun main(args: Array<String>) {
	runApplication<FinancialApplication>(*args)
}
