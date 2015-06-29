package br.ufrj.cos.redes.receiver;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import br.ufrj.cos.redes.fileAccess.Chunk;

public class Player {
	private OutputStream ostream;
	
	public Player(OutputStream ostream) {
		this.ostream = ostream; 
	}
	
	public void play(Chunk chunk) throws IOException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSz");
		System.out.println(formatter.format(Calendar.getInstance().getTime()) + ": Playing chunk with seqNum " + chunk.getSeqNum());
		ostream.write(chunk.getBytes(), 0, chunk.getActualChunkLength());
	}
}
