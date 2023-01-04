package com.sil.sil_synchronizer.Jobs;

import com.sil.sil_synchronizer.Variables;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DgaSyncListener extends JobExecutionListenerSupport {

    private final Variables variables;

    public DgaSyncListener(Variables variables) {
        this.variables = variables;
    }

    @Override
    public void afterJob(JobExecution jobExecution) {

        String finalMessage = String.format("The process %s has finished with the status %s \n", jobExecution.getJobInstance().getJobName(), jobExecution.getStatus());
        finalMessage += String.format("Number of stations registered: \n %d \n\n", variables.getRegisteredStations());
        finalMessage += String.format("Number of records saved in DB: \n %d \n", variables.getSavedRegistries());

        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("-------------------------------------------------- The job {} has been completed --------------------------------------------------", jobExecution.getJobInstance().getJobName());
            log.info(finalMessage);
        } else {
            log.error("-------------------------------------------------- The job {} has failed  --------------------------------------------------", jobExecution.getJobInstance().getJobName());
            log.error(finalMessage);
        }
    }
}
