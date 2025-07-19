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
        String metadataHost = "172.16.3.44"; 
        int metadataPort = 1099;

        try {
            File dir = new File(dataDirectory);
            if (!dir.exists()) {
                dir.mkdirs();
                System.out.println("Created data directory: " + dataDirectory);
            }
            
            StorageService storageService = new StorageServiceImpl(serverName, dataDirectory); 
            Registry registry = LocateRegistry.getRegistry(); 
            registry.rebind(serverName, storageService);
            System.out.println("Storage Server " + serverName + " is Ready!");

            // Look up Metadata Service
            MetadataService metadataService = (MetadataService) Naming.lookup("rmi://" + metadataHost + ":" + metadataPort + "/MetadataService");
            
            // Register with Metadata Service
            metadataService.registerStorageServer(serverName, "172.16.3.44", metadataPort); 
            
          
            System.out.println("Scanning data directory '" + dataDirectory + "' for existing chunks...");
            File[] chunkFiles = dir.listFiles();
            if (chunkFiles != null) {
                for (File chunkFile : chunkFiles) {
                    if (chunkFile.isFile()) { 
                        String chunkName = chunkFile.getName();
                        metadataService.registerChunk(chunkName, serverName);
                    }
                }
            }
            System.out.println("Finished scanning and registering existing chunks for " + serverName + ".");
          

        } catch (Exception e) {
            System.err.println("Storage Server exception (" + serverName + "): " + e.toString());
            e.printStackTrace();
        }
    }
}