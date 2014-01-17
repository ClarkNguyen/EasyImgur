package sg.vinova.easy_imgur.utilities;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

public class TextRefineUtil {
	public static String refineString(String str) {
		if (str == null)
			str = "";
		return str;
	}
	
	/**
	 * Create a string with two text styles and two text colors
	 * 
	 * @param allText
	 * @param textBold
	 * @param textNormal
	 * @return
	 */
	public static SpannableString generateSpannableString(Context context, String allText,
			String textBold, String textNormal) {
		if (allText == null)
			return new SpannableString("");
		
		allText = TextRefineUtil.refineString(allText);
		textBold = TextRefineUtil.refineString(textBold);
		textNormal = TextRefineUtil.refineString(textNormal);

		SpannableString spannable = new SpannableString(allText);

		// set spannable for text bold - First row
		int startBold = allText.indexOf(textBold);
		if (startBold == -1) {
			return spannable;
		}

		int endBold = startBold + textBold.length();
		spannable.setSpan(new ForegroundColorSpan(Color.BLACK), startBold,
				endBold, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

		// set spannable for text normal - Second row
		int startNormal = allText.indexOf(textNormal);
		if (startNormal == -1) {
			return spannable;
		}
		int endNormal = startNormal + textNormal.length();
		spannable.setSpan(new StyleSpan(Typeface.NORMAL), startNormal,
				endNormal, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		spannable.setSpan(new ForegroundColorSpan(Color.GRAY), startNormal,
				endNormal, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		spannable.setSpan(new RelativeSizeSpan(0.8f), startNormal, endNormal,
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		/*spannable.setSpan(
				new SoshiokTypeSpan(FontUtility.get(context,
						FontUtility.OSWALD_LIGHT)), startNormal, startNormal,
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);*/

		return spannable;
	}
}
