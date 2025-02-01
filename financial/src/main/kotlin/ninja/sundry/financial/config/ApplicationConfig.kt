package ninja.sundry.financial.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.querydsl.jpa.JPQLTemplates
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.annotation.PostConstruct
import jakarta.persistence.EntityManager
import mu.two.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import java.io.FileInputStream
import java.io.IOException
import java.time.Clock
import java.util.Optional

@ConfigurationPropertiesScan
@EnableJpaAuditing
@Configuration
class ApplicationConfig(
    @Value("\${app.firebase-configuration-file}")
    private val firebaseConfigPath: String,
) : AuditorAware<Long> {
    private val log = KotlinLogging.logger {}

    @Bean
    fun clock(): Clock {
        return Clock.systemUTC()
    }



    @Bean
    fun jpaQueryFactory(em: EntityManager): JPAQueryFactory {
        return JPAQueryFactory(JPQLTemplates.DEFAULT, em)
    }


    @PostConstruct
    fun initialize() {
        try {
            val options =
                FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(FileInputStream(firebaseConfigPath)))
                    .build()
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options)
                log.info("Firebase application has been initializaed")
            }
        } catch (e: IOException) {
            log.error(e.message)
        }
    }

    override fun getCurrentAuditor(): Optional<Long> {
        TODO("Not yet implemented")
    }
}
