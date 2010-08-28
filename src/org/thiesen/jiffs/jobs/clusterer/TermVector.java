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

import gnu.trove.TIntDoubleHashMap;

public class TermVector {

	private int _length = 0;
	private TIntDoubleHashMap _content; 

	public TermVector( int length ) {
		_length = length;
		_content = new TIntDoubleHashMap();
	}

	public void put(int i, double value) {
		if ( i < 0 || i > _length ) {
			throw new RuntimeException("Illegal index");
		}

		if ( value == 0.0 ) {
			_content.remove( i );
		} else {
			_content.put(i, value);
		}
	}

	public double get(int i) {
		if (i < 0  || i > _length ) {
			throw new RuntimeException("Illegal index");
		}
		if (_content.contains(i)) {
			return _content.get(i);
		} 
		return 0.0;
	}

	public int nnz() {
		return _content.size();
	}

	public int size() {
		return _length;
	}

	public double dot(TermVector b) {
		TermVector a = this;
		if (a._length != b._length) {
			throw new RuntimeException("Vector lengths disagree");
		}
		double sum = 0.0;

		if (a._content.size() <= b._content.size()) {
			for (int i : a._content.keys() )
				if (b._content.contains(i)) sum += a.get(i) * b.get(i);
		} else {
			for (int i : b._content.keys() )
				if (a._content.contains(i)) sum += a.get(i) * b.get(i);
		}
		return sum;
	}

	public double norm() {
		final TermVector a = this;
		return Math.sqrt(a.dot(a));
	}
	
	public void normalize() {
		final double norm = norm();
		if ( norm != 0.0D ) {
			div( norm );
		}
		
	}

	private void div(double value) {
		for ( final int key : _content.keys() ) {
			_content.put( key, _content.get( key ) / value );
		}
	}



}
