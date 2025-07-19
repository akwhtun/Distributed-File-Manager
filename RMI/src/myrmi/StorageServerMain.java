package myrmi;

import java.io.File;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class StorageServerMain {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java myrmi.StorageServerMain <serverName> <dataDirectory>");
            System.exit(1);
        }

        String serverName = args[0];
        String dataDirectory = args[1];
        String metadataHost = "localhost"; 
        int metadataPort = 1099;

        try {
            // Ensure data directory exists
            File dir = new File(dataDirectory);
            if (!dir.exists()) {
                dir.mkdirs();
                System.out.println("Created data directory: " + dataDirectory);
            }

            // Bind StorageService
            // FIX: Pass both serverName and dataDirectory to the constructor
            StorageService storageService = new StorageServiceImpl(serverName, dataDirectory); 
            Registry registry = LocateRegistry.getRegistry(); // Get default registry on 1099
            registry.rebind(serverName, storageService);
            System.out.println("Storage Server " + serverName + " is Ready!");

            // Look up Metadata Service
            MetadataService metadataService = (MetadataService) Naming.lookup("rmi://" + metadataHost + ":" + metadataPort + "/MetadataService");
            
            // Register with Metadata Service
            metadataService.registerStorageServer(serverName, "localhost", metadataPort); // This line was fine
            
            // --- NEW: Scan and Register existing chunks ---
            System.out.println("Scanning data directory '" + dataDirectory + "' for existing chunks...");
            File[] chunkFiles = dir.listFiles();
            if (chunkFiles != null) {
                for (File chunkFile : chunkFiles) {
                    if (chunkFile.isFile()) { // Ensure it's a file, not a subdirectory
                        String chunkName = chunkFile.getName();
                        metadataService.registerChunk(chunkName, serverName);
                    }
                }
            }
            System.out.println("Finished scanning and registering existing chunks for " + serverName + ".");
            // --- END NEW ---

        } catch (Exception e) {
            System.err.println("Storage Server exception (" + serverName + "): " + e.toString());
            e.printStackTrace();
        }
    }
}