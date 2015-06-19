package br.ufrj.cos.redes.fileAccess;

import java.io.IOException;

public interface FileChunkRetriever {
	boolean getNextChunk(byte [] bytes) throws IOException;
	boolean hasNext();
}
