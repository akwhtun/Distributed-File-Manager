package myrmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public interface MetadataService extends Remote {
//    String getStorageServer(String fileName, long offset) throws RemoteException;
    String getStorageServerForChunk(String chunkName) throws RemoteException;
    List<String> listAvailableFiles() throws RemoteException;
    void registerStorageServer(String name, String host, int port) throws RemoteException;
    void fileUploaded(String fileName, long fileSize, List<String> chunkNames) throws RemoteException;
    String getNextChunkServer() throws RemoteException;
    Map<String, List<String>> getFileChunks(String fileName) throws RemoteException;
    void chunkStored(String chunkName, String storageServerName) throws RemoteException;
    void registerChunk(String chunkName, String storageServerName) throws RemoteException; // Storage servers call this on startup
  
}