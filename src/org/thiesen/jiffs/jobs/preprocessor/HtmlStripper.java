/*
 * (c) Copyright 2010 Marcus Thiesen (marcus@thiesen.org)
 *
 *  This file is part of jiffs.
 *
 *  jiffs is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  jiffs is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with jiffs.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.thiesen.jiffs.jobs.preprocessor;

import java.io.IOException;
import java.io.StringReader;

import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

public class HtmlStripper extends HTMLEditorKit.ParserCallback {

	private final StringBuilder _builder = new StringBuilder();

	public String stripHtml(String in)  {
		final ParserDelegator delegator = new ParserDelegator();
		try {
			delegator.parse( new StringReader( in ), this, true );
		} catch (IOException e) {
			throw new RuntimeException( "IO Exception on a StringReader", e );
		}
		return _builder.toString();
	}

	@Override
	public void handleText(char[] text, int pos) {
		_builder.append(text);
	}


	
	
}
