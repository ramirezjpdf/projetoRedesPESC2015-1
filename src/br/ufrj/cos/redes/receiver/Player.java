package br.ufrj.cos.redes.receiver;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import br.ufrj.cos.redes.fileAccess.Chunk;

public class Player {
	private OutputStream playerOStream;
	private PrintWriter logWriter;
	private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSz");
	
	public Player(OutputStream ostream, OutputStream logStream) {
		this.playerOStream = ostream;
		this.logWriter = new PrintWriter(logStream);
		initLog();
	}
	
	public void play(Chunk chunk) throws IOException {
		Calendar calendar = Calendar.getInstance();
		chunk.setPlayedTimeStamp(calendar.getTimeInMillis());
		System.out.println(formatter.format(calendar.getTime()) + ": Playing chunk with seqNum " + chunk.getSeqNum());
		playerOStream.write(chunk.getBytes(), 0, chunk.getActualChunkLength());
		log(chunk);
	}
	
	private void initLog() {
		logWriter.println("seqNum,transTimestamp,recvTimestamp,playedTimestamp");
	}
	
	private void log(Chunk chunk) {
		logWriter.println(chunk.getSeqNum() + "," + 
						  chunk.getTransTimeStamp() + "," +
						  chunk.getRecTimeStamp() + "," + 
						  chunk.getPlayedTimeStamp());
		logWriter.flush();
	}
}
