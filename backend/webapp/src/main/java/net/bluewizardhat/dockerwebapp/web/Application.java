package net.bluewizardhat.dockerwebapp.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "net.bluewizardhat.dockerwebapp")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * Create a servlet container that serves ajp requests, so it can be served by apache
     */
//    @Bean
//    public EmbeddedServletContainerFactory ajpConnector() {
//        TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory();
//        Connector ajpConnector = new Connector("org.apache.coyote.ajp.AjpNioProtocol");
//        ajpConnector.setPort(8009);
//        tomcat.addAdditionalTomcatConnectors(ajpConnector);
//        return tomcat;
//    }
}
