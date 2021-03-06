package net.sourceforge.pmd.lang.java.rule.stanly.Util;

import java.util.Scanner;
import java.util.StringTokenizer;

public final class MacroFunctions {

	/**
	 *
	 * @since 2013. 2. 15.오전 2:35:57
	 * @author JeongSeungsu
	 * @param obj
	 * @return NULL->true 아니면 false
	 */
	final public static boolean NULLTrue(Object obj)
	{
		if(obj == null)
			return true;
		else
			return false;
	}
	final public static String NotRegularExpressionReplaceFirst(String source,String change, String changed)
	{
		String res = "";

		int start =source.indexOf(change);
		int end = start + change.length();
		if(start != -1)
		{
			res = source.substring(0,start);
			res += changed;
			res += source.substring(end,source.length());
		}
		else
			return source;
				
		return res;
	}
}
