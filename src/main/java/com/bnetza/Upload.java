package com.acme;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Path("/upload/{projectId}")
public class Upload {
    private final String DIR = "/tmp/uploads/";

    @POST
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    public Response uploadFile(@PathParam("projectId") String projectId,
                               @HeaderParam("fileName") String fileName,
                               @HeaderParam("index") int i,
                               @HeaderParam("totalChunks") int totalChunks,
                               InputStream fileStream) throws IOException {
        // TODO Check if directory exists
        String dir = DIR + projectId;

        java.nio.file.Path path = Paths.get(dir, fileName + ".part" + i);

        Files.copy(fileStream, path, StandardCopyOption.REPLACE_EXISTING);

        if (i == totalChunks - 1) mergeFile(dir, fileName, totalChunks);

        return Response.ok().build();
    }

    private void mergeFile(String dir, String fileName, int totalChunks) throws IOException {
        // Check if all parts were transmitted
        java.nio.file.Path path = Paths.get(dir, fileName);

        try (OutputStream out = new FileOutputStream(path.toFile(), true)) {
            for (int i = 0; i < totalChunks; i++) {
                java.nio.file.Path chunkPath = Paths.get(dir, fileName + ".part" + i);
                Files.copy(chunkPath, out);
                Files.delete(chunkPath);
            }
        }
    }
}
