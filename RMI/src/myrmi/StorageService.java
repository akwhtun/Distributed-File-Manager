package myrmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface StorageService extends Remote {

 void uploadChunk(String chunkId, byte[] chunkData) throws RemoteException;

 byte[] downloadChunk(String chunkId) throws RemoteException;
}