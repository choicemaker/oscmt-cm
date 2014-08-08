/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
// package org.jboss.varia.autonumber;
package com.choicemaker.autonumber;

import java.rmi.RemoteException;
import java.util.Collection;

import javax.ejb.CreateException;
import javax.ejb.EJBHome;
import javax.ejb.FinderException;

/**
 * ???
 * 
 * @author <a href="mailto:michel.anke@wolmail.nl">Michel de Groot</a>
 * @version $Revision: 1.1 $
 */
public interface AutoNumberHome
   extends EJBHome
{
  String DEFAULT_EJB_REF_NAME = "ejb/AutoNumber";
  String DEFAULT_JNDI_COMP_NAME = "java:comp/env/" + DEFAULT_EJB_REF_NAME ;

   /**
    * Creates an AutoNumber of given name.
    * 
    * @param name the name of the AutoNumber
    */
   AutoNumber create(String name) throws CreateException, RemoteException;
   
   Collection<AutoNumber> findAll() throws RemoteException, FinderException;

   /**
    * Finds an AutoNumber by its name.
    */
   AutoNumber findByPrimaryKey(String name) throws FinderException, RemoteException;
}
