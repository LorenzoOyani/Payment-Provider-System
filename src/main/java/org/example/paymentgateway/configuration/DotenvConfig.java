package org.example.paymentgateway.configuration;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!prod")
public class DotenvConfig {

    static  {
        Dotenv dotenv = Dotenv.configure()
                .directory("/")
                .filename(".env")
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();

        dotenv.entries().forEach(entry ->{
            System.setProperty(entry.getKey(), entry.getValue());
        });
    }
}
