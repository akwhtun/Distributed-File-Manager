// FileTransferClient.java
package myrmi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class FileTransferClient {
    private static final int CHUNK_SIZE = 1024 * 1024; // 1MB chunk size
    private MetadataService metadataService;
    private Registry registry;
    private ExecutorService executorService; 

    public FileTransferClient(String host, int port) throws Exception {
        this.registry = LocateRegistry.getRegistry(host, port);
        this.metadataService = (MetadataService) registry.lookup("MetadataService");
        this.executorService = Executors.newCachedThreadPool(); 
        System.out.println("Connected to Metadata Service.");
    }

    public List<String> listAvailableFiles() throws Exception {
        
        return metadataService.listAvailableFiles();
    }

    public void uploadFile(File file, Consumer<Integer> progressCallback, Runnable onComplete, Consumer<Exception> onError) {
        executorService.submit(() -> {
            try {
               
                List<String> uploadedChunkNames = new ArrayList<>();
                long fileSize = file.length(); 

                _uploadFile(file, progressCallback, uploadedChunkNames);
                       
                metadataService.fileUploaded(file.getName(), fileSize, uploadedChunkNames); 
                System.out.println("File '" + file.getName() + "' uploaded successfully.");
                if (onComplete != null) {
                    onComplete.run();
                }
            } catch (Exception e) {
                System.err.println("Error uploading file: " + e.getMessage());
                e.printStackTrace();
                if (onError != null) {
                    onError.accept(e);
                }
            }
        });
    }

    public void downloadFile(String fileName, String outputDirectory, Consumer<Integer> progressCallback, Runnable onComplete, Consumer<Exception> onError) {
        executorService.submit(() -> {
            try {
                _downloadFile(fileName, outputDirectory, progressCallback);
                System.out.println("File '" + fileName + "' downloaded successfully.");
                if (onComplete != null) {
                    onComplete.run();
                }
            } catch (Exception e) {
                System.err.println("Error downloading file: " + e.getMessage());
                e.printStackTrace();
                if (onError != null) {
                    onError.accept(e);
                }
            }
        });
    }

    
    private void _uploadFile(File file, Consumer<Integer> progressCallback, List<String> uploadedChunkNames) throws Exception {
        long fileSize = file.length();
        int chunkIndex = 0;
        long totalBytesRead = 0;

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[CHUNK_SIZE];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                byte[] chunkData = new byte[bytesRead];
                System.arraycopy(buffer, 0, chunkData, 0, bytesRead);

                String storageServerId = metadataService.getNextChunkServer();
                StorageService storageService = (StorageService) registry.lookup(storageServerId);

                String chunkId = file.getName() + "_chunk_" + chunkIndex;
                storageService.uploadChunk(chunkId, chunkData);
                  
                uploadedChunkNames.add(chunkId);
                
                metadataService.chunkStored(chunkId, storageServerId); 

                totalBytesRead += bytesRead;
                int progress = (int) ((totalBytesRead * 100) / fileSize);
                if (progressCallback != null) {
                    progressCallback.accept(progress);
                }
                chunkIndex++;
            }
        }
    }

    private void _downloadFile(String fileName, String outputDirectory, Consumer<Integer> progressCallback) throws Exception {
      
        Map<String, List<String>> chunksWithLocations = metadataService.getFileChunks(fileName);
        
        if (chunksWithLocations == null || chunksWithLocations.isEmpty()) {
            throw new Exception("File '" + fileName + "' not found or no chunks registered.");
        }

        File outputFile = new File(outputDirectory, fileName);
        Path outputPath = outputFile.toPath();
        if (Files.exists(outputPath)) {
            
            Files.delete(outputPath); 
            // Option 2: Rename existing file to prevent accidental overwrite
            // int count = 0;
            // Path tempPath = outputPath;
            // while (Files.exists(tempPath)) {
            //     tempPath = Paths.get(outputFile.getParent(), fileName + "(" + (++count) + ")");
            // }
            // outputFile = tempPath.toFile();
            // outputPath = outputFile.toPath();
        }
        Files.createDirectories(outputPath.getParent()); 
      
        List<String> sortedChunkNames = new ArrayList<>(chunksWithLocations.keySet());
        sortedChunkNames.sort((c1, c2) -> {
            int index1 = Integer.parseInt(c1.substring(c1.lastIndexOf('_') + 1));
            int index2 = Integer.parseInt(c2.substring(c2.lastIndexOf('_') + 1));
            return Integer.compare(index1, index2);
        });

        long bytesDownloaded = 0;
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            for (int i = 0; i < sortedChunkNames.size(); i++) {
                String chunkName = sortedChunkNames.get(i);
                List<String> storageServerNames = chunksWithLocations.get(chunkName);
                
                if (storageServerNames == null || storageServerNames.isEmpty()) {
                    throw new Exception("No available storage server for chunk: " + chunkName);
                }
                
                String storageServerId = storageServerNames.get(0); 
                StorageService storageService = (StorageService) registry.lookup(storageServerId);

                byte[] chunkData = storageService.downloadChunk(chunkName); 
                fos.write(chunkData);
                
                bytesDownloaded += chunkData.length;
                int progress = (int) (((i + 1.0) / sortedChunkNames.size()) * 100); 
                if (progressCallback != null) {
                    progressCallback.accept(progress);
                }
            }
        }
    }
    
    // Remember to shut down the executor service when the application exits
    public void shutdown() {
        executorService.shutdown();
    }
}