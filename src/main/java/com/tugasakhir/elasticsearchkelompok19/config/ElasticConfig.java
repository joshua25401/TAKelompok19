package com.tugasakhir.elasticsearchkelompok19.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;


/*
 *   Anotasi @Configuration :
 *   - menandakan bahwa class ini merupakan konfigurasi
 * */
@Configuration
public class ElasticConfig extends AbstractElasticsearchConfiguration {

    Logger log = LoggerFactory.getLogger(ElasticConfig.class);

    /*  Anotasi @Value digunakan untuk mengambil value dari file application.properties
     *   dan telah dispesifikasikan sebelumnya seperti elasticsearch.url
     * */
    @Value("${elasticsearch.url}")
    public String elasticUrl;


    /*  @Bean digunakan untuk membuat object
     *   Dalam hal ini anotasi @Bean digunakan untuk membuat object client
     *   yang digunakan untuk koneksi terhadap elasticsearch*/
    @Bean
    @Override
    public RestHighLevelClient elasticsearchClient() {
        try {
            final ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                    .connectedTo(elasticUrl)
                    .withConnectTimeout(30_0000)
                    .withSocketTimeout(30_0000)
                    .build();
            log.info("Connected to Elasticsearch!");
            return RestClients.create(clientConfiguration).rest();
        } catch (Exception e) {
            log.error("Error connecting to Elasticsearch! : " + e.getMessage());
            return null;
        }
    }
}
