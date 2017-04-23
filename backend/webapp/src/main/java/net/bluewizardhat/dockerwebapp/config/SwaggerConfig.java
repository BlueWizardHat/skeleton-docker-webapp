package net.bluewizardhat.dockerwebapp.config;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
				.genericModelSubstitutes(DeferredResult.class, ResponseEntity.class)
				.directModelSubstitute(LocalDate.class, String.class)
				.directModelSubstitute(OffsetDateTime.class, String.class)
				.directModelSubstitute(ZonedDateTime.class, String.class)
				.select()
				.apis(RequestHandlerSelectors.any())
				.paths(PathSelectors.any())
				.build();
	}
}
