package br.ufrj.cos.redes.receiver;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import br.ufrj.cos.redes.fileAccess.Chunk;

public class Player {
	private OutputStream playerOStream;
	private PrintWriter timestampLogWriter;
	private PrintWriter latencyLogWriter;
	private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSz");
	
	private boolean isFirst;
	private Calendar firstChunkPlayedRealTimestamp;
	private long firstChunkPlayedTimestamp;
	
	public Player(OutputStream ostream, OutputStream timestampLogStream, OutputStream latencyLogStream) {
		this.playerOStream = ostream;
		this.timestampLogWriter = new PrintWriter(timestampLogStream);
		this.latencyLogWriter = new PrintWriter(latencyLogStream);
		
		this.isFirst = true;
		firstChunkPlayedTimestamp = 0;
		initLogs();
	}
	
	public void play(Chunk chunk) throws IOException {
	Calendar calendar = Calendar.getInstance();
	if (isFirst) {
		firstChunkPlayedRealTimestamp = calendar;
		chunk.getTimestampInfo().setPlayedTimeStamp(chunk.getTimestampInfo().getReceivedTimeStamp() +  calendar.getTimeInMillis() - chunk.getLatencyInfo().getPlayedLatency());
		firstChunkPlayedTimestamp = chunk.getTimestampInfo().getPlayedTimeStamp();
		isFirst = false;
	} else {
		chunk.getTimestampInfo().setPlayedTimeStamp(calendar.getTimeInMillis() - firstChunkPlayedRealTimestamp.getTimeInMillis() + firstChunkPlayedTimestamp);
	}

	chunk.getLatencyInfo().setPlayedLatency(chunk.getTimestampInfo().getPlayedTimeStamp() - chunk.getTimestampInfo().getReceivedTimeStamp());
	System.out.println(formatter.format(calendar.getTime()) + ": Playing chunk with seqNum " + chunk.getSeqNum());
	playerOStream.write(chunk.getBytes(), 0, chunk.getActualChunkLength());
	logTimestampInfo(chunk);
	logLatencyInfo(chunk);
}
	
	private void logLatencyInfo(Chunk chunk) {
		latencyLogWriter.println(chunk.getSeqNum() + "," + 
				  chunk.getLatencyInfo().getTransmitionLatency() + "," +
				  chunk.getLatencyInfo().getReceivedLatency() + "," + 
				  chunk.getLatencyInfo().getPlayedLatency());
		latencyLogWriter.flush();	
	}

	private void initLogs() {
		timestampLogWriter.println("seqNum,Transmitted timestamp,Received timestamp,Played timestamp");
		latencyLogWriter.println("seqNum,Transmitted latency,Received latency,Played latency");
	}
	
	private void logTimestampInfo(Chunk chunk) {
		timestampLogWriter.println(chunk.getSeqNum() + "," + 
						  chunk.getTimestampInfo().getTransmitionTimeStamp() + "," +
						  chunk.getTimestampInfo().getReceivedTimeStamp() + "," + 
						  chunk.getTimestampInfo().getPlayedTimeStamp());
		timestampLogWriter.flush();
	}

}
