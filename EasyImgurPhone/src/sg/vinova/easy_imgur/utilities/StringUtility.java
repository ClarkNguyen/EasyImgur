package sg.vinova.easy_imgur.utilities;

public class StringUtility {
	/**
	 * Remove string str to positions of string pos with content, return new
	 * string after inserting
	 * 
	 * @param extra
	 * @param content
	 * @return
	 */
	public static String removeString(String extra, String content) {
		String contents = content;
		StringBuilder sb = new StringBuilder();

		while (contents.indexOf(extra) != -1) {
			String cutted = contents.substring(0, contents.indexOf(extra));
			contents = contents.substring(contents.indexOf(extra) + extra.length(),
					contents.length());

			sb.append(cutted);
		}

		sb.append(contents);

		return sb.toString();
	}
}
