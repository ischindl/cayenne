/* ====================================================================
 * 
 * The ObjectStyle Group Software License, version 1.1
 * ObjectStyle Group - http://objectstyle.org/
 * 
 * Copyright (c) 2002-2005, Andrei (Andrus) Adamchik and individual authors
 * of the software. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 * 
 * 3. The end-user documentation included with the redistribution, if any,
 *    must include the following acknowlegement:
 *    "This product includes software developed by independent contributors
 *    and hosted on ObjectStyle Group web site (http://objectstyle.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 * 
 * 4. The names "ObjectStyle Group" and "Cayenne" must not be used to endorse
 *    or promote products derived from this software without prior written
 *    permission. For written permission, email
 *    "andrus at objectstyle dot org".
 * 
 * 5. Products derived from this software may not be called "ObjectStyle"
 *    or "Cayenne", nor may "ObjectStyle" or "Cayenne" appear in their
 *    names without prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE OBJECTSTYLE GROUP OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 * 
 * This software consists of voluntary contributions made by many
 * individuals and hosted on ObjectStyle Group web site.  For more
 * information on the ObjectStyle Group, please see
 * <http://objectstyle.org/>.
 */
package org.objectstyle.cayenne.event;

import java.io.Serializable;
import java.util.Collection;

import javax.jms.Message;
import javax.jms.MessageFormatException;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import org.objectstyle.cayenne.util.IDUtil;

/**
 * Implementation of EventBridge that passes and receives events via JMS (Java Messaging
 * Service). JMSBridge uses "publish/subscribe" model for communication with external
 * agents.
 * 
 * @author Andrei Adamchik
 * @since 1.1
 */
public class JMSBridge extends EventBridge implements MessageListener {

    static final String VM_ID = new String(IDUtil.pseudoUniqueByteSequence16());
    static final String VM_ID_PROPERRTY = "VM_ID";

    protected String topicConnectionFactoryName;

    protected TopicConnection sendConnection;
    protected TopicSession sendSession;
    protected TopicConnection receivedConnection;
    protected TopicPublisher publisher;
    protected TopicSubscriber subscriber;

    public JMSBridge(EventSubject localSubject, String externalSubject) {
        super(localSubject, externalSubject);
    }

    /**
     * @since 1.2
     */
    public JMSBridge(Collection localSubjects, String externalSubject) {
        super(localSubjects, externalSubject);
    }

    /**
     * JMS MessageListener implementation. Injects received events to the EventManager
     * local event queue.
     */
    public void onMessage(Message message) {

        try {
            Object vmID = message.getObjectProperty(JMSBridge.VM_ID_PROPERRTY);
            if (JMSBridge.VM_ID.equals(vmID)) {
                return;
            }

            if (!(message instanceof ObjectMessage)) {
                return;
            }

            ObjectMessage objectMessage = (ObjectMessage) message;
            CayenneEvent event = messageObjectToEvent(objectMessage.getObject());
            if (event != null) {
                onExternalEvent(event);
            }

        }
        catch (MessageFormatException mfex) {
            // TODO: Andrus, 2/8/2006 logging... Log4J was removed to make this usable on
            // the client
        }
        catch (Exception ex) {
            // TODO: Andrus, 2/8/2006 logging... Log4J was removed to make this usable on
            // the client
        }
    }

    /**
     * @return Name of javax.jms.TopicConnectionFactory accessible via JNDI.
     */
    public String getTopicConnectionFactoryName() {
        return topicConnectionFactoryName;
    }

    public void setTopicConnectionFactoryName(String name) {
        this.topicConnectionFactoryName = name;
    }

    /**
     * Starts up JMS machinery for "publish/subscribe" model.
     */
    protected void startupExternal() throws Exception {
        Context jndiContext = new InitialContext();
        TopicConnectionFactory connectionFactory = (TopicConnectionFactory) jndiContext
                .lookup(topicConnectionFactoryName);

        Topic topic = null;

        try {
            topic = (Topic) jndiContext.lookup(externalSubject);
        }
        catch (NameNotFoundException ex) {
            // can't find topic, try to create it
            topic = topicNotFound(jndiContext, ex);

            if (topic == null) {
                throw ex;
            }
        }

        // config publisher
        if (receivesLocalEvents()) {
            this.sendConnection = connectionFactory.createTopicConnection();
            this.sendSession = sendConnection.createTopicSession(
                    false,
                    Session.AUTO_ACKNOWLEDGE);
            this.publisher = sendSession.createPublisher(topic);
        }

        // config subscriber
        if (receivesExternalEvents()) {
            this.receivedConnection = connectionFactory.createTopicConnection();
            this.subscriber = receivedConnection.createTopicSession(
                    false,
                    Session.AUTO_ACKNOWLEDGE).createSubscriber(topic);
            this.subscriber.setMessageListener(this);
            this.receivedConnection.start();
        }
    }

    /**
     * Attempts to create missing Topic. Since Topic creation is JMS-implementation
     * specific, this task is left to subclasses. Current implementation simply rethrows
     * the exception.
     */
    protected Topic topicNotFound(Context jndiContext, NamingException ex)
            throws Exception {
        throw ex;
    }

    /**
     * Closes all resources used to communicate via JMS.
     */
    protected void shutdownExternal() throws Exception {
        Exception lastException = null;

        if (publisher != null) {
            try {
                publisher.close();
            }
            catch (Exception ex) {
                lastException = ex;
            }
        }

        if (subscriber != null) {
            try {
                subscriber.close();
            }
            catch (Exception ex) {
                lastException = ex;
            }
        }

        if (receivedConnection != null) {
            try {
                receivedConnection.close();
            }
            catch (Exception ex) {
                lastException = ex;
            }
        }

        if (sendSession != null) {
            try {
                sendSession.close();
            }
            catch (Exception ex) {
                lastException = ex;
            }
        }

        if (sendConnection != null) {
            try {
                sendConnection.close();
            }
            catch (Exception ex) {
                lastException = ex;
            }
        }

        publisher = null;
        subscriber = null;
        receivedConnection = null;
        sendConnection = null;
        sendSession = null;

        if (lastException != null) {
            throw lastException;
        }
    }

    protected void sendExternalEvent(CayenneEvent localEvent) throws Exception {
        ObjectMessage message = sendSession
                .createObjectMessage(eventToMessageObject(localEvent));
        message.setObjectProperty(JMSBridge.VM_ID_PROPERRTY, JMSBridge.VM_ID);
        publisher.publish(message);
    }

    /**
     * Converts CayenneEvent to a serializable object that will be sent via JMS. Default
     * implementation simply returns the event, but subclasses can customize this
     * behavior.
     */
    protected Serializable eventToMessageObject(CayenneEvent event) throws Exception {
        return event;
    }

    /**
     * Converts a Serializable instance to CayenneEvent. Returns null if the object is not
     * supported. Default implementation simply tries to cast the object to CayenneEvent,
     * but subclasses can customize this behavior.
     */
    protected CayenneEvent messageObjectToEvent(Serializable object) throws Exception {
        return (object instanceof CayenneEvent) ? (CayenneEvent) object : null;
    }
}
