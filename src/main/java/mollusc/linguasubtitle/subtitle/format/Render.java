package mollusc.linguasubtitle.subtitle.format;

import mollusc.linguasubtitle.subtitle.Subtitle;

/**
 * Created with IntelliJ IDEA.
 * User: mollusc <MolluscLab@gmail.com>
 * Date: 07.09.13
 */
public abstract class Render {

    protected final Subtitle subtitle;

	protected Render(Subtitle subtitle) {
		this.subtitle = subtitle;
	}

	public abstract void save(String pathToSave);

}
