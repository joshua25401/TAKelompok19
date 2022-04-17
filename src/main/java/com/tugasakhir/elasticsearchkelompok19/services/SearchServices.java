package com.tugasakhir.elasticsearchkelompok19.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tugasakhir.elasticsearchkelompok19.model.PDFDocument;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.ScoreSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class SearchServices {
    /*Logger*/
    final Logger log = LoggerFactory.getLogger(SearchServices.class);

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
    public List<PDFDocument> fullTextSearch(String query) {

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
        Map<String, Float> fields = new HashMap<>();
        fields.put("attachment.content", 0.50f);
        fields.put("attachment.content._2gram", 0.10f);
        fields.put("attachment.content._3gram", 0.05f);
        fields.put("title", 0.0f);

        MultiMatchQueryBuilder multiMatchQueryBuilder = new MultiMatchQueryBuilder(query)
                .fields(fields)
                .type(MultiMatchQueryBuilder.Type.BOOL_PREFIX)
                .fuzziness(Fuzziness.AUTO);


        HighlightBuilder highlightBuilder = new HighlightBuilder()
                .preTags("<b>")
                .postTags("</b>")
                .field(new HighlightBuilder.Field("attachment.content"));

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(multiMatchQueryBuilder)
                .highlighter(highlightBuilder)
                .sort(new ScoreSortBuilder().order(SortOrder.DESC));

        SearchRequest searchRequest = new SearchRequest()
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
            int counter = 0;
            if (searchHits.length > 0) {
                for (SearchHit hit : searchHits) {
                    Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                    ObjectMapper mapper = new ObjectMapper();
                    pdfDocuments.add(mapper.convertValue(sourceAsMap, PDFDocument.class));
                    Map<String,HighlightField> highlightfields = hit.getHighlightFields();
                    HighlightField field = highlightfields.get("attachment.content");
                    Text[] fragments = field.fragments();
                    for(Text fragment : fragments){
                        pdfDocuments.get(counter).getHighlight().add(fragment.string());
                    }
                    counter++;
                }
                return pdfDocuments;
            } else {
                return null;
            }
        } catch (IOException e) {
            log.error("Error while searching for documents {}", e.getMessage());
            return null;
        }
    }

    /*
     * Fungsi searchAll
     * akan melakukan pencarian secara menyeluruh terhadap sebuah index
     * Kemudian fungsi ini akan mengembalikan list dari hasil pencarian jika ada
     * Namun, jika tidak ada hasil pencarian yang ditemukan maka fungsi ini akan mengembalikan nilai NULL
     * */

    public List<PDFDocument> sarchAll() {
        List<PDFDocument> pdfDocuments = new ArrayList<>();
        try {
            SearchRequest searchRequest = new SearchRequest(indexName);
//            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//            searchSourceBuilder.query(QueryBuilders.);
//            searchRequest.source(searchSourceBuilder);

            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits hits = searchResponse.getHits();
            SearchHit[] searchHits = hits.getHits();
            if (searchHits.length > 0) {
                for (SearchHit hit : searchHits) {
                    Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                    ObjectMapper mapper = new ObjectMapper();
                    pdfDocuments.add(mapper.convertValue(sourceAsMap    , PDFDocument.class));
                }
                return pdfDocuments;
            } else {
                return null;
            }
        } catch (IOException e) {
            log.error("Error while searching for documents {}", e.getMessage());
            return null;
        }
    }
}
