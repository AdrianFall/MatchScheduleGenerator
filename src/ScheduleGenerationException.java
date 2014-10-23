
public class ScheduleGenerationException extends Exception {
	public ScheduleGenerationException() {
        this("InsufficientAmountOfMatchesException");
    }

	public ScheduleGenerationException(String msg) {
		super(msg);
	}
}
