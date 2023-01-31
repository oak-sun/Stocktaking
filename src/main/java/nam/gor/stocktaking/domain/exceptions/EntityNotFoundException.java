package nam.gor.stocktaking.domain.exceptions;

public class EntityNotFoundException extends StocktakingException {

    private static final long serialVersionUID = 1L;

    public EntityNotFoundException(final String message,
                                   Object... args) {
        super(message, args);
    }
}
