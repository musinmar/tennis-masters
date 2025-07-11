package tm.lib.domain.competition.exception;

public class UnresolvableCompetitionPathException extends RuntimeException {

    public UnresolvableCompetitionPathException(String message) {
        super(message);
    }

    public UnresolvableCompetitionPathException(String message, Throwable cause) {
        super(message, cause);
    }
}
