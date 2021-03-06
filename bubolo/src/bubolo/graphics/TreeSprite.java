package bubolo.graphics;

import java.util.Random;

import com.badlogic.gdx.graphics.Texture;

import bubolo.world.Tree;

/**
 * The graphical representation of a Tree.
 *
 * @author BU673 - Clone Industries
 */
class TreeSprite extends AbstractEntitySprite<Tree>
{
	private Texture image;

	private float rotation;

	/** The file name of the texture. */
	private static final String TEXTURE_FILE = "tree.png";

	/**
	 * Constructor for the TreeSprite. This is Package-private because sprites should not
	 * be directly created outside of the graphics system.
	 *
	 * @param tree
	 *            Reference to the Tree that this TreeSprite represents.
	 */
	TreeSprite(Tree tree)
	{
		super(DrawLayer.TerrainImprovements, tree);

		image = Graphics.getTexture(TEXTURE_FILE);
		Random rand = new Random();
		rotation = (float) (rand.nextInt(4) * (Math.PI/2));
	}

	@Override
	public void draw(Graphics graphics)
	{
		drawTexture(graphics, image);
	}

	@Override
	public float getRotation()
	{
		return rotation;
	}
}
