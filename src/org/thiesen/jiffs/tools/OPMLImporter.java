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

package org.thiesen.jiffs.tools;

import java.io.File;
import java.net.URI;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;
import org.joda.time.DateTime;
import org.thiesen.jiffs.data.db.dao.SubscriptionDAO;
import org.thiesen.jiffs.data.db.dbo.SubscriptionDBO;
import org.thiesen.jiffs.data.enums.SubscriptionState;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class OPMLImporter {

	private static final Function<Element, SubscriptionDBO> TO_DBO_FUNCTION = new Function<Element, SubscriptionDBO>() {

		@Override
		public SubscriptionDBO apply(Element outlineElement) {
			final SubscriptionDBO retval = new SubscriptionDBO();
			
			retval.setNextCheck( new DateTime() );
			retval.setState( SubscriptionState.SUBSCRIBED );
			retval.setTitle( outlineElement.getAttributeValue("title") );
			retval.setXmlUrl( URI.create( outlineElement.getAttributeValue("xmlUrl") ) );
			retval.setHtmlUrl( URI.create( outlineElement.getAttributeValue("htmlUrl") ) );
			
			return retval;
		}

	
	};

	@SuppressWarnings("unchecked")
	public static void main( final String... args ) throws Exception {
		final SAXBuilder builder = new SAXBuilder( "org.apache.xerces.parsers.SAXParser", false );
		builder.setFeature( "http://apache.org/xml/features/nonvalidating/load-external-dtd", true );

		final Document document = builder.build( new File( args[0] ) );

		XPath path = XPath.newInstance("//outline[@xmlUrl]");
		
		final List<Element> nodes = path.selectNodes( document );
		
		System.out.println("Found " + nodes.size() + " Feeds to import!" );
		
		final SubscriptionDAO subscriptionDAO = new SubscriptionDAO();
		 
		subscriptionDAO.insert( Lists.transform( nodes, TO_DBO_FUNCTION ) );

		System.out.println("Done");
	}

}
