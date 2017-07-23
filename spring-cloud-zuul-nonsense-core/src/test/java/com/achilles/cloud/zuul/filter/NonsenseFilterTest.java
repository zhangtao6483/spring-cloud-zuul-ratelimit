package com.achilles.cloud.zuul.filter;

import com.netflix.zuul.context.RequestContext;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.Assert.assertEquals;
import static org.springframework.http.HttpStatus.OK;

/**
 * @author zhangtao
 * @date 2017/7/21.
 */
public class NonsenseFilterTest {
    protected NonsenseFilter filter;

    protected MockHttpServletRequest request;
    protected MockHttpServletResponse response;

    protected RequestContext context = RequestContext.getCurrentContext();

    @Before
    public void setUp() {
        this.request = new MockHttpServletRequest();
        this.response = new MockHttpServletResponse();
        this.filter = new NonsenseFilter();
        this.context.clear();
        this.context.setRequest(this.request);
        this.context.setResponse(this.response);
    }

    @Test
    public void testNonsense() throws Exception {
        this.filter.run();
        assertEquals(OK.value(), this.response.getStatus());
    }
}
