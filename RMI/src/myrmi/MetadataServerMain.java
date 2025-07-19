package myrmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class MetadataServerMain {
 public static void main(String[] args) {
     try {
         MetadataServiceImpl metadataService = new MetadataServiceImpl();
         Registry registry = LocateRegistry.createRegistry(1099); 
  
         registry.rebind("MetadataService", metadataService);
         
         System.out.println("Metadata Server Ready!");
     } catch (Exception e) {
         System.err.println("Metadata Server exception: " + e.toString());
         e.printStackTrace();
     }
 }
}
