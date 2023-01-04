package com.sil.sil_synchronizer.Jobs.dgaSync;

import com.sil.sil_synchronizer.Dtos.DgaRequiredInformationDto;
import com.sil.sil_synchronizer.Repositories.IDgaRegistryLogDao;
import com.sil.sil_synchronizer.Repositories.IViewArchivedInformationDao;
import com.sil.sil_synchronizer.Variables;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PropertiesStep {

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    private final Variables variables;


    private final IDgaRegistryLogDao dgaRegistryLogDao;

    private final IViewArchivedInformationDao viewArchivedInformationDao;


    @Value("${job.batch.chunk.size}")
    private int jobChunkSize;

    public Step dgaSync() {
        return stepBuilderFactory.get("properties")
                .<Map<String, Object>, DgaRequiredInformationDto>chunk(jobChunkSize)
                .reader(getPropertiesReader())
                .processor(getPropertiesProcessor())
                .writer(getPropertiesWriter())
                .build();
    }

    @Bean
    public Flow dgaSyncFlow() {
        return new FlowBuilder<SimpleFlow>("dgaSync")
                .start(dgaSyncFlow())
                .build();
    }

    public ItemReader<Map<String, Object>> getPropertiesReader() {
        return new PropertiesReader(variables, viewArchivedInformationDao, dgaRegistryLogDao);
    }

    public PropertiesProcessor getPropertiesProcessor() {
        return new PropertiesProcessor();
    }

    public PropertiesWriter getPropertiesWriter() {
        return new PropertiesWriter(variables);
    }
}
