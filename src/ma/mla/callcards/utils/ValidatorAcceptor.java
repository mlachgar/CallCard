package ma.mla.callcards.utils;

public abstract class ValidatorAcceptor<T> implements Validator<T>, Acceptor<T> {

	public static <T> ValidatorAcceptor<T> from(final Validator<T> validator,
			final Acceptor<T> acceptor) {
		return new ValidatorAcceptor<T>() {

			@Override
			public void accept(T e) {
				acceptor.accept(e);
			}

			@Override
			public boolean isValid(T e) {
				return validator != null ? validator.isValid(e) : true;
			}
		};
	}
}
