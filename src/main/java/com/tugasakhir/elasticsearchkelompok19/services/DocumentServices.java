package com.tugasakhir.elasticsearchkelompok19.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tugasakhir.elasticsearchkelompok19.model.PDFDocument;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.ingest.GetPipelineRequest;
import org.elasticsearch.action.ingest.GetPipelineResponse;
import org.elasticsearch.action.ingest.PutPipelineRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.ScoreSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class DocumentServices {

    /*Logger*/
    final Logger log = LoggerFactory.getLogger(DocumentServices.class);

    /*Elasticsearch Configuration*/
    @Value("${elasticsearch.index}")
    private String indexName;
    @Value("${elasticsearch.pipeline}")
    private String pipeLine;
    @Value("${elasticsearch.type}")
    private String type;
    @Value("${elasticsearch.number_of_shards}")
    private Integer number_of_shards;
    @Value("${elasticsearch.number_of_replicas}")
    private Integer number_of_replicas;

    /*PATH Value*/
    @Value("${pdf.upload.path}")
    private String pdfLocalPath;

    @Autowired
    private RestHighLevelClient client;


    /*
    * fungsi uploadPdf(MultipartFile file) digunakan untuk mengupload file pdf ke disk dan mengindeks file ke elasticsearch
    * fungsi ini akan mengembalikan nilai boolean ketika proses upload sukses atau tidak
    * fungsi ini juga akan mendapat exception ketika terjadi kesalahan (IOException, ElasticsearchException)
    *
    *   1. IOException : Exception yang akan ditangkap ketika terjadi kesalahan pada proses upload file
    *   2. ElasticsearchException : Exception yang akan ditangkap ketika terjadi kesalahan pada proses indexing file
    * */
    public boolean upload(MultipartFile file) throws IOException, ElasticsearchException {
        if(file.isEmpty()) {
            log.info("File is empty");
            return false;
        }else{
            /*Ini untuk Upload file ke Disk*/
            /*
            * Path Directory :
            *   - /resources/uploaded-pdf/
            * */
            String fileName = renameFile(Objects.requireNonNull(file.getOriginalFilename()).toLowerCase());
            Path path = Paths.get("").toAbsolutePath().resolve(pdfLocalPath + fileName);
            file.transferTo(new File(path.toString()));
        }

        Map<String, Object> mapping = new HashMap<>();
        mapping.put("data", Base64.getEncoder().encodeToString(file.getBytes()));

        if(!indexExists(indexName)){

            CreateIndexRequest request = new CreateIndexRequest(indexName);
            request.settings(Settings.builder()
                    .put("index.number_of_shards", number_of_shards)
                    .put("index.number_of_replicas", number_of_replicas));
            CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);
            log.info("Create index {} with response {}", indexName, response.isAcknowledged());
        }else{
            log.info("Index {} already exists", indexName);
        }

        if(!pipelineExists(pipeLine)){
                String source =
                        "{"
                        + "\"description\": \"Extract attachment information\","
                        + "\"processors\": ["
                        + "{"
                            +"\"attachment\":"
                                +"{"
                                    +"\"field\": \"data\""
                                +"}"
                        +"},{"
                                +"\"remove:{"
                                    +"\"field\": \"data\""
                                +"}]"
                    +"}";

                PutPipelineRequest pipelineRequest = new PutPipelineRequest(pipeLine, new BytesArray(source.getBytes(StandardCharsets.UTF_8)), XContentType.JSON);
                AcknowledgedResponse pipelineResponse = client.ingest().putPipeline(pipelineRequest, RequestOptions.DEFAULT);
                log.info("Create pipeline {} with response {}", pipeLine, pipelineResponse.isAcknowledged());
        }else{
            log.info("Pipeline {} already exists", pipeLine);
        }

        IndexRequest indexRequest = new IndexRequest()
                .index(indexName)
                .id(file.getOriginalFilename())
                .setPipeline(pipeLine);
        indexRequest.source(mapping, XContentType.JSON);

        IndexResponse response = client.index(indexRequest, RequestOptions.DEFAULT);
        log.info("Indexing file {} with response {}", file.getOriginalFilename(), response.status());
        return true;
    }

    private boolean pipelineExists(String pipeLine) throws IOException {
        GetPipelineRequest request = new GetPipelineRequest(pipeLine);
        GetPipelineResponse response = client.ingest().getPipeline(request, RequestOptions.DEFAULT);
        log.info("Check pipeline {} with response {}", pipeLine, response.isFound());
        return response.isFound();
    }

    private boolean indexExists(String indexName) throws IOException {
        GetIndexRequest request = new GetIndexRequest(indexName);
        boolean exists = client.indices().exists(request,RequestOptions.DEFAULT);
        log.info("Index {} exists", indexName);
        return exists;
    }

    private String renameFile(String filename){
        String pdfExtention = filename.substring(filename.lastIndexOf(".") + 1);
        String encode = Base64.getEncoder().encodeToString(filename.substring(filename.lastIndexOf(".")).getBytes(StandardCharsets.UTF_8));
        String newFilename = encode + System.currentTimeMillis() +"." + pdfExtention;
        log.info("New filename {}", newFilename);
        return newFilename;
    }

}
