package com.acme;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.RestForm;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Path("/upload")
public class Upload {
    @POST
    @Path("/init")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response initialize(List<String> fileName){
        ClickHouse session = new ClickHouse("transfer");
        String id = Utils.generateUUID(true);

        session.query("INSERT INTO session (id, user_id, created_at, updated_at, status) VALUES ('" + id + "', 1, now(), null, 0);");

        for (String name : fileName){
            String fileId = Utils.generateUUID(true);
            session.query("INSERT INTO file (id, session_id, path, total_chunks, status) VALUES ('" + fileId + "', '" + id + "', '" + name + "', 10, 0);");
        }

        return Response.ok(id).build();
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(
            @PathParam("projectId") String projectId,
            @RestForm("meta") String meta,
            @RestForm("data") InputStream data) throws JsonProcessingException {
        // TODO Check if directory exists

        Chunk c = new ObjectMapper().readValue(meta, Chunk.class);

        try {
            String DIR = "/tmp/uploads/";
            java.nio.file.Path dir = Paths.get(DIR, projectId);
            // Files.createDirectories(dir);

            java.nio.file.Path chunkPath = dir.resolve(c.getFilename() + c.getIndex() + ".chunk");
            Files.write(chunkPath, data.readAllBytes());

            if(c.getIndex() == c.getTotalChunks() - 1) mergeFile(dir, c.getFilename(), c.getTotalChunks());

            return Response.ok("Chunk " + c.getIndex() + " hochgeladen").build();
        } catch (Exception e) {
            return Response.serverError().entity("Fehler beim Hochladen: " + e.getMessage()).build();
        }
    }

    private void mergeFile(java.nio.file.Path dir, String fileName, int totalChunks) throws IOException {
        // Check if all parts were transmitted

        java.nio.file.Path file = dir.resolve(fileName);

        try (OutputStream out = new FileOutputStream(file.toFile(), true)) {
            for (int i = 0; i < totalChunks; i++) {
                java.nio.file.Path chunkPath = dir.resolve(fileName + i + ".chunk");
                Files.copy(chunkPath, out);
                Files.delete(chunkPath);
            }
        }
    }
}
