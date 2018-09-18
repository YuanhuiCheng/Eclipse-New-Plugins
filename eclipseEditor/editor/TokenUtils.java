package ca.yuanhuicheng.tools.eclipse.plugin.ui.editor;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * {@link TokenUtils} provides Eclipse parse token utility methods.
 */
public final class TokenUtils
{
	static Token createToken(final int red, final int green, final int blue)
	{
		return new Token(new TextAttribute(new Color(Display.getDefault(), red, green, blue)));
	}

	static Token createUndefinedToken()
	{
		// yellow to stand out in case the token should be mistakenly displayed in the editor
		final int nonDisplayedRedValue = 255, nonDisplayedGreenValue = 255, nonDisplayedBlueValue = 0;
		return new Token(new TextAttribute(new Color(Display.getDefault(), nonDisplayedRedValue,
				nonDisplayedGreenValue, nonDisplayedBlueValue)))
		{
			@Override
			public boolean isUndefined()
			{
				return true;
			}
		};
	}

	private TokenUtils()
	{
	}
}