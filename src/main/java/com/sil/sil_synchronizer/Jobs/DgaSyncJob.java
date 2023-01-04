package com.sil.sil_synchronizer.Jobs;

import com.sil.sil_synchronizer.Variables;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
public class DgaSyncJob {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    private Variables variables;

    @Bean
    public Job DgaSync(Flow dgaSyncFlow) {
        return jobBuilderFactory.get("dgaSync")
                .incrementer(new RunIdIncrementer())
                .listener(getDgaSyncListener())
                .start(dgaSyncFlow)
                .end()
                .build();
    }

    public DgaSyncListener getDgaSyncListener() {
        return new DgaSyncListener(variables);
    }
}
