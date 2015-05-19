/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.test.integration;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import org.mule.api.MuleException;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.config.spring.TestLifecycleObject;
import org.mule.tck.junit4.FunctionalTestCase;

import org.junit.Test;

public class FailedLifecyclePhaseDoesNotPropagateTestCase extends FunctionalTestCase
{

    @Test
    public void failedInitialiseDoesNotInvokeDispose() throws Exception
    {
        FailingTestLifecycleObject lifecycleObject = new FailingTestLifecycleObject();
        lifecycleObject.setFailOnInitialise(true);

        try
        {
            muleContext.getRegistry().registerObject("key", lifecycleObject);
            fail("was expecting an exception");
        }
        catch (Exception e)
        {
            assertThat(lifecycleObject.getInitialise(), is(1));
            assertThat(lifecycleObject.getDispose(), is(0));
        }
    }

    @Override
    protected String[] getConfigFiles()
    {
        return new String[]{};
    }

    public static class FailingTestLifecycleObject extends TestLifecycleObject
    {

        private boolean failOnInitialise = false;
        private boolean failOnStart = false;
        private boolean failOnStop = false;
        private boolean failOnDispose = false;

        @Override
        public void initialise() throws InitialisationException
        {
            super.initialise();
            fail(failOnInitialise);
        }

        @Override
        public void start() throws MuleException
        {
            super.start();
            fail(failOnStart);
        }

        @Override
        public void stop() throws MuleException
        {
            super.stop();
            fail(failOnStop);
        }

        @Override
        public void dispose()
        {
            super.dispose();
            fail(failOnDispose);
        }

        public void setFailOnInitialise(boolean failOnInitialise)
        {
            this.failOnInitialise = failOnInitialise;
        }

        public void setFailOnStart(boolean failOnStart)
        {
            this.failOnStart = failOnStart;
        }

        public void setFailOnStop(boolean failOnStop)
        {
            this.failOnStop = failOnStop;
        }

        public void setFailOnDispose(boolean failOnDispose)
        {
            this.failOnDispose = failOnDispose;
        }

        private void fail(boolean shouldFail)
        {
            if (shouldFail)
            {
                throw new RuntimeException();
            }
        }
    }
}
