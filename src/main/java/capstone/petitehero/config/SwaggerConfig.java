package capstone.petitehero.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.*;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;

@EnableSwagger2
@Configuration
public class SwaggerConfig {

    @Bean
    public Docket api(){
        return new Docket(DocumentationType.SWAGGER_2)
                .select() // select return an instance of API selector builder
                .apis(RequestHandlerSelectors.basePackage("capstone.petitehero.controllers")) // choose specific class to be in swagger
                .paths(PathSelectors.any()) // with method to be in swagger (@Path annotation in controller)
                .build()  // builder docket
                .apiInfo(apiInfo())
                .securitySchemes(Arrays.asList(apiKey()))
                .securityContexts(Arrays.asList(securityContext()));
        // use endpoint localhost:8080/swagger-resources to check with springfox endpoint
        // in this case springfox url endpoint is localhost:8080/v2/api-docs
    }

    private AuthorizationScope[] scopes() {
        AuthorizationScope[] scopes = {
                new AuthorizationScope("PetiteHero", "Access Petite Hero API") };
        return scopes;
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(
                        Arrays.asList(new SecurityReference("JWT Token", scopes())))
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("REST API")
                .description("The REST API for petite hero.").termsOfServiceUrl("")
                .contact(new Contact("Petite Hero", "", "petitehero@gmail.com"))
                .version("0.0.1")
                .build();
    }

    private ApiKey apiKey() {
        return new ApiKey("JWT Token", "Authorization", "header");
    }
}
