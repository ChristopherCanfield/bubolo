package bubolo.graphics;

import bubolo.util.GameException;

/**
 * Thrown to indicate that the height or width of a texture doesn't meet the texture split
 * requirements.
 * @see TextureUtil
 *
 * @author BU CS673 - Clone Productions
 */
public class TextureDimensionException extends GameException
{

	/**
	 * Used in serialization / de-serialization.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a TextureFormatException object with the specified message.
	 *
	 * @param message
	 *            the exception's detail message.
	 */
	public TextureDimensionException(String message)
	{
		super(message);
	}

	/**
	 * Constructs a TextureFormatException object from the specified exception.
	 *
	 * @param exception
	 *            the exception to wrap.
	 */
	public TextureDimensionException(Throwable exception)
	{
		super(exception);
	}
}
