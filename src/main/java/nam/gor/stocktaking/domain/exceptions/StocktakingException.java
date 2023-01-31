package nam.gor.stocktaking.domain.exceptions;

import static java.lang.String.format;

public class StocktakingException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public StocktakingException(final String message,
                                Object... args) {
        super(format(message, args));
    }
}
