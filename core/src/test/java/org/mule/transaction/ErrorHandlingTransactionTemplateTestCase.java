/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.transaction;

import org.hamcrest.core.Is;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mule.api.MessagingException;
import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mule.transaction.TransactionTemplateFactory.createExceptionHandlingTransactionTemplate;
import static org.mule.transaction.TransactionTemplateTestUtils.getEmptyTransactionCallback;

@RunWith(MockitoJUnitRunner.class)
public class ErrorHandlingTransactionTemplateTestCase
{
    private static final Object RETURN_VALUE = new Object();
    private MuleContext mockMuleContext = mock(MuleContext.class);
    @Mock
    private MessagingException mockMessagingException;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private MuleEvent mockEvent;


    @Test
    public void testSuccessfulExecution() throws Exception
    {
        TransactionTemplate transactionTemplate = createExceptionHandlingTransactionTemplate(mockMuleContext);
        Object result = transactionTemplate.execute(getEmptyTransactionCallback(RETURN_VALUE));
        assertThat(result, is(RETURN_VALUE));
    }

    @Test
    public void testFailureException() throws Exception
    {
        TransactionTemplate transactionTemplate = createExceptionHandlingTransactionTemplate(mockMuleContext);
        MuleEvent mockResultEvent = mock(MuleEvent.class);
        when(mockMessagingException.getEvent()).thenReturn(mockEvent).thenReturn(mockEvent).thenReturn(mockResultEvent);
        when(mockEvent.getFlowConstruct().getExceptionListener().handleException(mockMessagingException, mockEvent)).thenReturn(mockResultEvent);
        try
        {
            transactionTemplate.execute(TransactionTemplateTestUtils.getFailureTransactionCallback(mockMessagingException));
            fail("AlreadyHandledMessagingException must be thrown");
        }
        catch (MessagingException e)
        {
            assertThat(e, Is.is(mockMessagingException));
            verify(mockMessagingException).setProcessedEvent(mockResultEvent);
        }

    }

}