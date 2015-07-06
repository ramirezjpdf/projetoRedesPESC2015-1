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
	
	
	public Player(OutputStream ostream, OutputStream timestampLogStream, OutputStream latencyLogStream) {
		this.playerOStream = ostream;
		this.timestampLogWriter = new PrintWriter(timestampLogStream);
		this.latencyLogWriter = new PrintWriter(latencyLogStream);
		initLogs();
	}
	
	public void play(Chunk chunk) throws IOException {
		Calendar calendar = Calendar.getInstance();
		long latency = calendar.getTimeInMillis() - chunk.getLatencyInfo().getPlayedLatency();
		chunk.getLatencyInfo().setPlayedLatency(latency);
		chunk.getTimestampInfo().setPlayedTimeStamp(chunk.getTimestampInfo().getReceivedTimeStamp() + latency);
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
		timestampLogWriter.println("seqNum,Transmited timestamp,Received timestamp,Played timestamp");
		latencyLogWriter.println("seqNum,Transmited latency,Received latency,Played latency");
	}
	
	private void logTimestampInfo(Chunk chunk) {
		timestampLogWriter.println(chunk.getSeqNum() + "," + 
						  chunk.getTimestampInfo().getTransmitionTimeStamp() + "," +
						  chunk.getTimestampInfo().getReceivedTimeStamp() + "," + 
						  chunk.getTimestampInfo().getPlayedTimeStamp());
		timestampLogWriter.flush();
	}
}
