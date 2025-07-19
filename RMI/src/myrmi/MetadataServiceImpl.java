package myrmi;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MetadataServiceImpl extends UnicastRemoteObject implements MetadataService {

    private Map<String, List<String>> fileToChunksMap; 
    private Map<String, List<String>> chunkLocations; 
    private Map<String, String> storageServers; 

    private AtomicInteger serverCounter = new AtomicInteger(0); 
    private String METADATA_FILE = "metadata.dat"; 

    public MetadataServiceImpl() throws RemoteException {
        super();
        fileToChunksMap = new ConcurrentHashMap<>();
        chunkLocations = new ConcurrentHashMap<>();
        storageServers = new ConcurrentHashMap<>();
        loadMetadata(); 
    }

    private void saveMetadata() {
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(Paths.get(METADATA_FILE)))) {
            oos.writeObject(fileToChunksMap);
            System.out.println("Metadata saved to " + METADATA_FILE);
        } catch (IOException e) {
            System.err.println("Error saving metadata: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void loadMetadata() {
        if (Files.exists(Paths.get(METADATA_FILE))) {
            try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(Paths.get(METADATA_FILE)))) {
                fileToChunksMap = (Map<String, List<String>>) ois.readObject();
                System.out.println("Metadata loaded from " + METADATA_FILE);
               
                chunkLocations.clear(); 
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error loading metadata: " + e.getMessage());
                
                fileToChunksMap = new ConcurrentHashMap<>();
            }
        } else {
            System.out.println("No existing metadata file found. Starting fresh.");
        }
    }

    @Override
    public void registerStorageServer(String name, String host, int port) throws RemoteException {
        String url = host + ":" + port;
        storageServers.put(name, url);
        System.out.println("Storage Server registered: " + name + " at " + url);
    }

    @Override
    public String getNextChunkServer() throws RemoteException {
        if (storageServers.isEmpty()) {
            throw new RemoteException("No storage servers available.");
        }
        List<String> serverNames = new ArrayList<>(storageServers.keySet());
       
        int currentSize = serverNames.size();
        int index = serverCounter.getAndIncrement() % currentSize;
        return serverNames.get(index);
    }

    @Override
    public void fileUploaded(String fileName, long fileSize, List<String> chunkNames) throws RemoteException {
        fileToChunksMap.put(fileName, chunkNames);
        System.out.println("File uploaded: " + fileName + " with chunks: " + chunkNames);
        saveMetadata(); 
    }

    @Override
    public Map<String, List<String>> getFileChunks(String fileName) throws RemoteException {
        if (!fileToChunksMap.containsKey(fileName)) {
            System.out.println("Attempted to retrieve non-existent file: " + fileName);
            return null;
        }
        
        List<String> chunks = fileToChunksMap.get(fileName);
        Map<String, List<String>> chunksWithLocations = new HashMap<>();

        for (String chunk : chunks) {
            List<String> locations = chunkLocations.get(chunk);
            if (locations != null && !locations.isEmpty()) {
                chunksWithLocations.put(chunk, new ArrayList<>(locations)); // Return a copy
            } else {
                System.err.println("Warning: Chunk " + chunk + " has no known locations on any active server.");
            }
        }
        return chunksWithLocations;
    }

    @Override
    public List<String> listAvailableFiles() throws RemoteException {
        return new ArrayList<>(fileToChunksMap.keySet());
    }

//    @Override
//    public String getStorageServer(String fileName, long offset) throws RemoteException {
//        if (storageServers.isEmpty()) {
//            return null;
//        }
//        List<String> serverNames = new ArrayList<>(storageServers.keySet());
//        Collections.shuffle(serverNames);
//        return serverNames.get(0);
//    }

    @Override
    public String getStorageServerForChunk(String chunkName) throws RemoteException {
        List<String> servers = chunkLocations.get(chunkName);
        if (servers == null || servers.isEmpty()) {
            return null;
        }
        return servers.get(0);
    }

    @Override
    public void chunkStored(String chunkName, String storageServerName) throws RemoteException {
        chunkLocations.computeIfAbsent(chunkName, k -> new Vector<>()).add(storageServerName);
        System.out.println("Chunk " + chunkName + " acknowledged on " + storageServerName);
    }

    @Override
    public void registerChunk(String chunkName, String storageServerName) throws RemoteException {
        List<String> locations = chunkLocations.computeIfAbsent(chunkName, k -> new Vector<>()); 
        if (!locations.contains(storageServerName)) {
            locations.add(storageServerName);
        }
        System.out.println("Storage Server " + storageServerName + " registered chunk: " + chunkName);
    }
}