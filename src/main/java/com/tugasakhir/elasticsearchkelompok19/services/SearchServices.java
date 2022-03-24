package com.tugasakhir.elasticsearchkelompok19.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tugasakhir.elasticsearchkelompok19.model.PDFDocument;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.Fuzziness;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SearchServices {
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

    @Autowired
    private RestHighLevelClient client;

    /*
    * Fungsi fullTextSearch(String query)
    * akan menerima string query yang akan di-search
    * Kemudian fungsi ini akan mengembalikan list dari hasil pencarian jika ada
    * Namun, jika tidak ada hasil pencarian yang ditemukan maka fungsi ini akan mengembalikan nilai NULL
    * */
    public List<PDFDocument> fullTextSearch(String query)  {

        // List of PDFDocument hasil searching
        List<PDFDocument> pdfDocuments = new ArrayList<>();

        // Membuat SearchRequest berdasarkan rules yang digunakan.
        /*
        * Daftar Rules yang digunakan untuk pencarian :
        *   1. MultiMatchQueryBuilder : mencari berdasarkan kata yang dicari di beberapa field dengan menambah aturan fuzzy
        *    - Fuzziness : menentukan jika kata yang dicari tidak ditemukan, maka kata yang dicari akan dicari dengan kata yang lebih dekat.
        *
        *   2. SearchSourceBuilder : mengatur rules untuk mencari dan mengurutkan hasil pencarian
        *    - SortOrder : menentukan urutan hasil pencarian
        *    - ScoreSortBuilder : mengurutkan hasil pencarian berdasarkan score dari hasil pencarian
        *
        *   3. SearchRequest : membuat query hasil pencarian dengan rules yang telah ditentukan
        * */
        MultiMatchQueryBuilder multiMatchQueryBuilder = new MultiMatchQueryBuilder(query)
                .field("attachment.content")
                .fuzziness(Fuzziness.AUTO);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(multiMatchQueryBuilder)
                .sort(new ScoreSortBuilder().order(SortOrder.DESC));

        SearchRequest searchRequest = new SearchRequest(indexName)
                .source(searchSourceBuilder);

        try {

            /*
            *   Pada bagian ini akan dilakukan pencarian hasil pencarian dengan rules yang telah ditentukan
            *
            *   1. SearchResponse : mengambil hasil pencarian dari Elasticsearch dengan searchRequest yang sebelumnya telah dibuat
            *
            *   2. SearchHits : mengambil hasil pencarian dari SearchResponse yang sebelumnya telah dibuat
            *
            *   3. SearchHit : mengambil hasil pencarian dari SearchHits
            * */
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits hits = searchResponse.getHits();

            SearchHit[] searchHits = hits.getHits();


            if(searchHits.length > 0){
                for (SearchHit hit : searchHits) {
                    Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                    Map<String, Object> attachment = (HashMap<String, Object>) sourceAsMap.get("attachment");
                    ObjectMapper mapper = new ObjectMapper();
                    pdfDocuments.add(mapper.convertValue(attachment, PDFDocument.class));
                    log.debug("pdfDocumentsSize : {}", pdfDocuments.size());
                }
                return pdfDocuments;
            }else{
                return null;
            }
        } catch (IOException e) {
            log.error("Error while searching for documents {}", e.getMessage());
            return null;
        }
    }
}
