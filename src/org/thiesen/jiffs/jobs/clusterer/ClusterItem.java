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

package org.thiesen.jiffs.jobs.clusterer;

import com.google.common.collect.ImmutableList;

public class ClusterItem {

	private final String _uri;
	private final ImmutableList<String> _words;
	
	ClusterItem(String uri, ImmutableList<String> words) {
		super();
		_uri = uri;
		_words = words;
	}
	
	String getUri() {
		return _uri;
	}

	ImmutableList<String> getWords() {
		return _words;
	}

	
	
}
