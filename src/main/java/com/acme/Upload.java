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
    private final String DIR = "/tmp/";

    @POST
    @Path("/init")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response initialize(List<Item> items){
        ClickHouse session = new ClickHouse("transfer");
        String sessionId = Utils.generateUUID(true);

        // user_id is DEMO 1
        session.query("INSERT INTO session (id, user_id, created_at, updated_at, status) VALUES ('" + sessionId + "', 1, now(), null, 0);");

        for (Item i : items){
            session.query("INSERT INTO file (id, session_id, path, file_name, total_chunks, status) VALUES ('" + i.getId() + "', '" + sessionId + "', '" + i.getPath() + "', '" + i.getFileName() + "', " + i.getTotalChunks() + ", 0);");
        }

        createTmpDir(sessionId);

        return Response.ok(sessionId).build();
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

    private void createTmpDir(String id) {
        try {
            java.nio.file.Path dir = Paths.get(DIR, id);
            Files.createDirectories(dir);
        } catch (Exception e){
            e.printStackTrace();
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
