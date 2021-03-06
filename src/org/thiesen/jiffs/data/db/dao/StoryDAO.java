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

package org.thiesen.jiffs.data.db.dao;

import java.net.URI;

import org.thiesen.jiffs.data.db.dao.common.AbstractDAO;
import org.thiesen.jiffs.data.db.dbo.StoryDBO;

import com.mongodb.BasicDBObject;

public class StoryDAO extends AbstractDAO<StoryDBO> {

	public StoryDAO() {
		super( StoryDBO.class );
	}

	public StoryDBO findByUri(URI uri ) {
		return findOne( new BasicDBObject( StoryDBO.URI_PROPERTY,  uri.toString() ) );
		
	}

	public Iterable<StoryDBO> findWithoutLanguage() {
		return findWithoutProperty( StoryDBO.LANGUAGE_PROPERTY );
	}

	public Iterable<StoryDBO> findWithoutPreprocesing() {
		final BasicDBObject query = new BasicDBObject( StoryDBO.PREPROCESSED_TEXT_PROPERTY , doesNotExist() );
		
		query.put( StoryDBO.LANGUAGE_PROPERTY , exists() );
		
		return find( query );
	}

	public Iterable<StoryDBO> findForClustering() {
		return findWithProperty( StoryDBO.PREPROCESSED_TEXT_PROPERTY );
	}

	public Iterable<StoryDBO> findWithoutFullPage() {
		return findWithoutProperty( StoryDBO.FULLPAGE_PROPERTY );
	}

}
