package com.acme;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Chunk {
    private String filename;
    private int index;
    private int totalChunks;

    public Chunk() {
    }

    @JsonCreator
    public Chunk(@JsonProperty("filename") String filename,
                 @JsonProperty("index") int index,
                 @JsonProperty("totalChunks") int totalChunks) {
        this.filename = filename;
        this.index = index;
        this.totalChunks = totalChunks;
    }

    public String getFilename() {
        return this.filename;
    }

    public int getIndex() {
        return this.index;
    }

    public int getTotalChunks() {
        return this.totalChunks;
    }
}
