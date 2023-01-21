package com.sil.sil_synchronizer.Jobs;

import com.sil.sil_synchronizer.Dtos.DgaRequiredInformationDto;
import com.sil.sil_synchronizer.Jobs.dgaSync.DgaSyncProcessor;
import com.sil.sil_synchronizer.Jobs.dgaSync.DgaSyncReader;
import com.sil.sil_synchronizer.Jobs.dgaSync.DgaSyncWriter;
import com.sil.sil_synchronizer.Repositories.IDgaRegistryLogDao;
import com.sil.sil_synchronizer.Repositories.IViewArchivedInformationDao;
import com.sil.sil_synchronizer.Services.DgaClientService;
import com.sil.sil_synchronizer.Variables;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DgaSyncJob {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    private Variables variables;

    @Autowired
    private DgaClientService dgaClientService;

    private final IDgaRegistryLogDao dgaRegistryLogDao;

    private final IViewArchivedInformationDao viewArchivedInformationDao;

    @Value("${job.batch.chunk.size}")
    private int jobChunkSize;

    @Bean
    public Job dgaSync() {
        return jobBuilderFactory.get("dgaSyncJob")
                .incrementer(new RunIdIncrementer())
                .listener(getDgaSyncListener())
                .start(dgaSyncStep())
                .build();
    }

    @Bean
    public Step dgaSyncStep() {
        return stepBuilderFactory.get("dgaSyncStep")
                .<Map<String, Object>, DgaRequiredInformationDto>chunk(jobChunkSize)
                .reader(getPropertiesReader())
                .processor(getPropertiesProcessor())
                .writer(getPropertiesWriter())
                .build();
    }


    public ItemReader<Map<String, Object>> getPropertiesReader() {
        return new DgaSyncReader(variables, viewArchivedInformationDao, dgaRegistryLogDao, dgaClientService);
    }

    public DgaSyncProcessor getPropertiesProcessor() {
        return new DgaSyncProcessor();
    }

    public DgaSyncWriter getPropertiesWriter() {
        return new DgaSyncWriter(variables, dgaRegistryLogDao, dgaClientService);
    }

    public DgaSyncListener getDgaSyncListener() {
        return new DgaSyncListener(variables);
    }
}
