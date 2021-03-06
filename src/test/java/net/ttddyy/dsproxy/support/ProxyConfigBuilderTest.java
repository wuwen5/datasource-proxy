package net.ttddyy.dsproxy.support;

import net.ttddyy.dsproxy.listener.CompositeProxyDataSourceListener;
import net.ttddyy.dsproxy.listener.ProxyDataSourceListener;
import net.ttddyy.dsproxy.proxy.ProxyConfig;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * @author Tadaya Tsuyukubo
 */
public class ProxyConfigBuilderTest {

    @Test
    public void multipleQueryListeners() {
        ProxyConfig proxyConfig;
        List<ProxyDataSourceListener> listeners;
        CompositeProxyDataSourceListener chainListener;

        ProxyDataSourceListener listener1 = mock(ProxyDataSourceListener.class);
        ProxyDataSourceListener listener2 = mock(ProxyDataSourceListener.class);
        ProxyDataSourceListener listener3 = mock(ProxyDataSourceListener.class);

        // specify listeners directly one by one
        proxyConfig = ProxyConfig.Builder.create().listener(listener1).listener(listener2).build();
        listeners = proxyConfig.getListeners().getListeners();
        assertThat(listeners).hasSize(2).contains(listener1, listener2);

        // with empty CompositeProxyDataSourceListener
        proxyConfig = ProxyConfig.Builder.create().listener(new CompositeProxyDataSourceListener()).build();
        listeners = proxyConfig.getListeners().getListeners();
        assertThat(listeners).isEmpty();

        // with CompositeProxyDataSourceListener containing some listeners
        chainListener = new CompositeProxyDataSourceListener();
        chainListener.addListener(listener1);
        chainListener.addListener(listener2);

        proxyConfig = ProxyConfig.Builder.create().listener(chainListener).build();
        listeners = proxyConfig.getListeners().getListeners();
        assertThat(listeners).hasSize(2).contains(listener1, listener2);

        // with adding a chain listener and a listener
        chainListener = new CompositeProxyDataSourceListener();
        chainListener.addListener(listener1);
        chainListener.addListener(listener2);

        proxyConfig = ProxyConfig.Builder.create().listener(chainListener).listener(listener3).build();
        listeners = proxyConfig.getListeners().getListeners();
        assertThat(listeners).hasSize(3).contains(listener1, listener2, listener3);


    }

    @Test
    public void multipleMethodListeners() {
        ProxyConfig proxyConfig;
        List<ProxyDataSourceListener> listeners;
        CompositeProxyDataSourceListener compositeListener;

        ProxyDataSourceListener listener1 = mock(ProxyDataSourceListener.class);
        ProxyDataSourceListener listener2 = mock(ProxyDataSourceListener.class);
        ProxyDataSourceListener listener3 = mock(ProxyDataSourceListener.class);

        // specify listeners directly one by one
        proxyConfig = ProxyConfig.Builder.create().listener(listener1).listener(listener2).build();
        listeners = proxyConfig.getListeners().getListeners();
        assertThat(listeners).hasSize(2).contains(listener1, listener2);

        // with empty CompositeProxyDataSourceListener
        proxyConfig = ProxyConfig.Builder.create().listener(new CompositeProxyDataSourceListener()).build();
        listeners = proxyConfig.getListeners().getListeners();
        assertThat(listeners).isEmpty();

        // with CompositeProxyDataSourceListener containing some listeners
        compositeListener = new CompositeProxyDataSourceListener();
        compositeListener.addListener(listener1);
        compositeListener.addListener(listener2);

        proxyConfig = ProxyConfig.Builder.create().listener(compositeListener).build();
        listeners = proxyConfig.getListeners().getListeners();
        assertThat(listeners).hasSize(2).contains(listener1, listener2);

        // with adding a chain listener and a listener
        compositeListener = new CompositeProxyDataSourceListener();
        compositeListener.addListener(listener1);
        compositeListener.addListener(listener2);

        proxyConfig = ProxyConfig.Builder.create().listener(compositeListener).listener(listener3).build();
        listeners = proxyConfig.getListeners().getListeners();
        assertThat(listeners).hasSize(3).contains(listener1, listener2, listener3);


    }
}
