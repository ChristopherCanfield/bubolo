package bubolo.graphics;

import com.badlogic.gdx.graphics.Color;

public enum TeamColor {
	Blue(Color.valueOf("5394EFFF")),
	Cyan(Color.valueOf("00CCCCFF")),
	Green(Color.valueOf("31A500FF")),
	Purple(Color.valueOf("B677D8FF")),
	Red(Color.valueOf("E54949FF")),
	Pink(Color.valueOf("FF8EA1FF")),
	Orange(Color.valueOf("FF7700FF")),
	Yellow(Color.valueOf("CCC833FF")),
	Gold(Color.valueOf("B49B57FF")),
	Tan(Color.valueOf("D8BA93FF")),
	Brown(Color.valueOf("A55E08FF")),
	Black(Color.valueOf("7F7F7FFF")),
	Neutral(new Color(215.0f/255, 215.0f/255, 215.0f/255, 1), false);

	/** The underlying color. */
	public final Color color;

	/** Whether this color can be selected by players. */
	public final boolean selectableByPlayers;

	TeamColor(Color color, boolean selectableByPlayers) {
		this.color = color;
		this.selectableByPlayers = selectableByPlayers;
	}

	TeamColor(Color color) {
		this(color, true);
	}
}
