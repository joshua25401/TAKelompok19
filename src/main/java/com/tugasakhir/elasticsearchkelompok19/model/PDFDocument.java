package com.tugasakhir.elasticsearchkelompok19.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PDFDocument {

    @SerializedName("content_type")
    private String contentType;

    @SerializedName("content")
    private String content;

    @SerializedName("title")
    private String title;

    @SerializedName("author")
    private String author;
}
