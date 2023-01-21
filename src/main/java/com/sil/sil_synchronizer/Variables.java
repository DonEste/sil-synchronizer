package com.sil.sil_synchronizer;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@Data
public class Variables {
    //Endpoints variables
    @Value("${app.environment}")
    public String environment;

    @Value("${properties.filename}")
    private String propertiesFilename;

    @Value("${hours.regression.retry}")
    private int hoursRegressionRetry;

    @Value("${hours.regression.trigger}")
    private int hoursRegressionTrigger;

    @Value("${hours.search.offset}")
    private int hoursSearchOffset;

    @Value("${dga.webservice.max.attempts}")
    private int dgaWebServicesMaxAttempts;

    @Value("${dga.webservice.seconds.delay}")
    private int dgaWebServiceSecondsDelay;

    private int savedRegistries = 0;

    private int registeredStations = 0;

    public void setSavedRegistries(int vedRegistries) {
        this.savedRegistries += vedRegistries;
    }
}
