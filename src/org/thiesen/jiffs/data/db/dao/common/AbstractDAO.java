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

package org.thiesen.jiffs.data.db.dao.common;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.thiesen.jiffs.data.types.DBO;

import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.WriteResult;

public class AbstractDAO<T extends DBO & DBObject> {

	private final static DB DB_CONNECTION;
	
	static {

		
		try {
			final Mongo m = new Mongo( "localhost" , 27017 );
		
			DB_CONNECTION = m.getDB( "jiffs" );
		
		} catch (UnknownHostException e) {
			throw new RuntimeException( e );
		} catch (MongoException e) {
			throw new RuntimeException( e );
		}

	}

	private final Class<T> _dboClass;
	private final DBCollection _collection;
	
	public AbstractDAO(Class<T> dboClass ) {
		_dboClass = dboClass;
		_collection = DB_CONNECTION.getCollection( toCollectionName( dboClass ) );
	}

	private static String toCollectionName(Class<? extends DBO> dboClass) {
		final String simpleName = dboClass.getSimpleName();
		return StringUtils.uncapitalize( simpleName ).replace( "DBO", "" );
	}

	@SuppressWarnings("unchecked")
	public void insert( T... values ) {
		WriteResult insert = _collection.insert( (List<DBObject>) Arrays.asList( values ) );
		
		insert.getLastError();
	}

	@SuppressWarnings("unchecked")
	public void insert( List<T> values ) {
		WriteResult insert = _collection.insert( (List<DBObject>)values );

		insert.getLastError();
	
	}
	
}
