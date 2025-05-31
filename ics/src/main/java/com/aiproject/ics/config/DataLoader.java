package com.aiproject.ics.config;

import jakarta.annotation.PostConstruct;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

@Component

@Profile("!test")
public class DataLoader {
    @Autowired
    private final VectorStore vectorStore;
    @Autowired
    private final JdbcClient jdbcClient;

    public DataLoader(@Lazy VectorStore vectorStore, JdbcClient jdbcClient) {
        this.vectorStore = vectorStore;
        this.jdbcClient = jdbcClient;
    }
    @Value("classpath:/Acadia_Hotel_Brochure.pdf")
    private Resource pdfResource;

    @PostConstruct
    private void init(){
        Integer count=jdbcClient.sql(" SELECT COUNT (*) from vector_store")
                .query(Integer.class).single();
        System.out.println("no of records is "+count);

        if(count==0){
            PdfDocumentReaderConfig config= PdfDocumentReaderConfig.builder()
                    .withPagesPerDocument(1)
                    .build();
            PagePdfDocumentReader reader=new PagePdfDocumentReader(pdfResource,config);
            var textSplitter=new TokenTextSplitter();
            vectorStore.accept(textSplitter.apply(reader.get()));
        }
    }


}
