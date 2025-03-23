package com.acme;

import jakarta.ws.rs.FormParam;

public class Chunk {
    @FormParam("data")
    private byte[] data;

    @FormParam("fileName")
    private String fileName;

    @FormParam("index")
    private int index;

    @FormParam("totalChunks")
    private int totalChunks;

    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public int getIndex() {
        return index;
    }
    public void setIndex(int index) {
        this.index = index;
    }
    public int getTotalChunks() {
        return totalChunks;
    }
    public void setTotalChunks(int totalChunks) {
        this.totalChunks = totalChunks;
    }
    public byte[] getData() {
        return data;
    }
    public void setData(byte[] data) {
        this.data = data;
    }
}
