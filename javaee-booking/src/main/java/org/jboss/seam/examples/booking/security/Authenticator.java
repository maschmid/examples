/* 
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
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
package org.jboss.seam.examples.booking.security;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.examples.booking.account.Authenticated;
import org.jboss.seam.examples.booking.i18n.DefaultBundleKey;
import org.jboss.seam.examples.booking.model.User;
import org.jboss.seam.international.status.Messages;
import org.jboss.logging.Logger;

/**
 * This implementation of <strong>Authenticator</strong> cross references the
 * values of the user's credentials against the database.
 * 
 * @author Dan Allen
 */
@Stateless
public class Authenticator
{
   @Inject
   private Instance<Logger> log;

   @PersistenceContext
   private EntityManager em;

   @Inject
   private Messages messages;

   @Inject
   private Credentials credentials;

   @Inject
   @Authenticated
   private Event<User> loginEventSrc;

   public boolean authenticate()
   {
      log.get().info("Logging in " + credentials.getUsername());
      if ((credentials.getUsername() == null) || (credentials.getPassword() == null))
      {
         messages.info(new DefaultBundleKey("identity_loginFailed"));
         return false;
      }

      User user = em.find(User.class, credentials.getUsername());
      if ((user != null) && user.getPassword().equals(credentials.getPassword()))
      {
         loginEventSrc.fire(user);
         messages.info(new DefaultBundleKey("identity_loggedIn"), user.getName());
         return true;
      }
      else
      {
         messages.info(new DefaultBundleKey("identity_loginFailed"));
         return false;
      }
   }

}
