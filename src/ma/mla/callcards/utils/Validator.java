package ma.mla.callcards.utils;

public interface Validator<T> {

	public boolean isValid(T e);

	public void accept(T e);

}
