package com.tugasakhir.elasticsearchkelompok19.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.elasticsearch.common.text.Text;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@ToString
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PDFDocument implements Serializable {
    @SerializedName("title")
    private String title;

    @SerializedName("file_name")
    private String file_name;

    @SerializedName("authors")
    private List<String> authors;

    @SerializedName("author")
    private String author;

    @SerializedName("prodi")
    private String prodi;

    @SerializedName("attachment")
    private Attachment attachment;

    private List<String> highlight;

    private double took;

    public PDFDocument(){
        this.highlight = new ArrayList<String>();
        this.took = 0.00f;
    }
}
