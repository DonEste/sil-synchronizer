package com.sil.sil_synchronizer.config;

import com.sil.sil_synchronizer.services.DgaClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Configuration
public class WebServiceConfiguration {

    private Environment env = null;

    @Autowired
    public void setEnvironment(Environment env) {
        this.env = env;
    }

    @Bean
    public Jaxb2Marshaller marshaller() throws Exception {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();

        marshaller.setContextPath("com.sil.sil_synchronizer.webservices.wsdl");
        marshaller.afterPropertiesSet();
        return marshaller;
    }

    @Bean
    public DgaClientService webServiceClient(Jaxb2Marshaller marshaller) {
        DgaClientService client = new DgaClientService();
        client.setDefaultUri(env.getProperty("dga.service.url"));
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        return client;
    }
}

