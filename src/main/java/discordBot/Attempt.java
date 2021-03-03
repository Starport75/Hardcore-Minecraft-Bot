package discordBot;

import java.util.Calendar;

public class Attempt {
	private long attemptNumber;
	private String runKiller;
	private long startTime;
	private long deathTime;
	private long successTime;
	
	public Attempt(int nAttemptNumber) {
		attemptNumber = nAttemptNumber;
		runKiller = "-";
		startTime = System.currentTimeMillis();
		deathTime = -1;
		successTime = -1;
	}
	
	public Attempt(int nAttemptNumber, String nRunKiller, long nStartTime, long nDeathTime, long nSuccessTime) {
		attemptNumber = nAttemptNumber;
		runKiller = nRunKiller;
		startTime = nStartTime;
		deathTime = nDeathTime;
		successTime = nSuccessTime;
	}
	
	public long getAttemptNumber() {
		return attemptNumber;
	}
	
	public String getRunKiller() {
		return runKiller;
	}
	
	public long getStartTime() {
		return startTime;
	}
	
	public long getDeathTime() {
		return deathTime;
	}
	
	public long getSuccessTime() {
		return successTime;
	}
	
	private String getTimeString(long millis) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(millis);
		
		String fString = "%d/%d at %d:%d";
		return String.format(fString, calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
	}
	
	public String getStartTimeString() {
		return getTimeString(getStartTime());
	}
	
	public String getDeathTimeString() {
		return getTimeString(getDeathTime());
	}
	
	public String getSuccessTimeString() {
		return getTimeString(getSuccessTime());
	}
	
	public void endRun(String nRunKiller) {
		runKiller = nRunKiller;
		deathTime = System.currentTimeMillis();
	}
	
	public void success() {
		successTime = System.currentTimeMillis();
	}
	
}
