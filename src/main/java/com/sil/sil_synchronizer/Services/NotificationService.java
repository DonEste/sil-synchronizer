package com.sil.sil_synchronizer.Services;

import com.sil.sil_synchronizer.Dtos.DgaRequiredInformationDto;
import io.airbrake.javabrake.Config;
import io.airbrake.javabrake.Notice;
import io.airbrake.javabrake.Notifier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Component
public class NotificationService extends WebServiceGatewaySupport {

    @Value("${airbrake.project.id}")
    private int airbrajeProjectId;

    @Value("${airbrake.project.key}")
    private String airbrajeProjectKey;

    @Value("${app.environment}")
    private String environment;


    public void reportError(Exception exception) {

        log.info("Sending error to AirBrake: {}", exception.getMessage());

        Config config = new Config();

        config.projectId = airbrajeProjectId;
        config.projectKey = airbrajeProjectKey;

        Notifier notifier = new Notifier(config);

        notifier.addFilter(
                (Notice notice) -> {
                    notice.setContext("environment", environment);
                    return notice;
                });

        notifier.report(exception);
    }
}
