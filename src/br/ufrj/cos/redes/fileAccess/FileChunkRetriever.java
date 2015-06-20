package br.ufrj.cos.redes.fileAccess;

import java.io.IOException;

public interface FileChunkRetriever {
	boolean getNextChunk(Chunk chunk) throws IOException;
	boolean hasNext();
}
