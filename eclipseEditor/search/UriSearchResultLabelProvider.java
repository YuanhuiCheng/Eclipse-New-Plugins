package ca.yuanhuicheng.tools.eclipse.plugin.ui.search;

import java.text.MessageFormat;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.DecoratingStyledCellLabelProvider;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import com.google.common.base.CharMatcher;



public class UriSearchResultLabelProvider extends DelegatingStyledCellLabelProvider implements IStyledLabelProvider
{	
	public UriSearchResultLabelProvider(final UriSearchResultPage page)
	{
		super(new UriWorkbenchLabelProvider());
		this.page = page;
	}
	
	 @Override
	 public StyledString getStyledText(Object element)
	 {
		 if (element instanceof IFile)
		 {
            return this.getFileStyledText((IFile) element);
		 } 
		 else if (element instanceof UriLineResult)
		 {
            return this.getLineStyledText((UriLineResult) element);
         }

        return super.getStyledText(element);
    }
	 
	 private StyledString getFileStyledText(final IFile file)
	 {
		 String fileName = file.getName();
	     StyledString string = new StyledString(fileName);
	     return string;
	 }
	 
	 private StyledString getLineStyledText(final UriLineResult lineResult)
	 {
		 StyledString string = new StyledString();
		 UriReference uriReference = lineResult.getMatches().get(0).getUriReference();
		 int lineNumber = uriReference.getLineNumber();
		 String lineNumberString = MessageFormat.format("{0,number,integer}: ", lineNumber);
		 string.append(lineNumberString, StyledString.QUALIFIER_STYLER);
		 String fullLine = uriReference.getFullLine();
		 int trimStart = NON_WHITESPACE_MATCHER.indexIn(fullLine);
	     int trimEnd = NON_WHITESPACE_MATCHER.lastIndexIn(fullLine);
	     string.append(fullLine.substring(trimStart, trimEnd + 1));
	     string.append(String.format("%" + 20 + "s", "") + uriReference.getFullUri(), StyledString.DECORATIONS_STYLER);

		 return string;
	 }
	 
	 private static final class UriWorkbenchLabelProvider extends DecoratingStyledCellLabelProvider implements IStyledLabelProvider
	 {
		 private UriWorkbenchLabelProvider()
		 {
			 super(new WorkbenchLabelProvider(), PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator(), null);
		 }
		 
		 @Override
		 public StyledString getStyledText(final Object element) 
		 {
            return super.getStyledText(element);
		 }
	 }
	 
	 private final UriSearchResultPage page;
	 private static final CharMatcher NON_WHITESPACE_MATCHER = CharMatcher.WHITESPACE.negate();
	 private static final String HIGHLIGHT_BG_COLOR_NAME = "org.eclipse.search.ui.match.highlight";
	 private static final Styler HIGHLIGHT_STYLE = StyledString.createColorRegistryStyler(null, HIGHLIGHT_BG_COLOR_NAME);
}
