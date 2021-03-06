package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.ConnectionInfo;
import net.ttddyy.dsproxy.listener.MethodExecutionContext;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Uses {@link javax.sql.rowset.CachedRowSet} to provide repeatable read {@link ResultSet} proxy.
 *
 * {@link javax.sql.rowset.CachedRowSet} is disconnected scrollable {@link javax.sql.RowSet} which is a
 * subset of {@link ResultSet}. Using those feature to provide repeatable read resultset proxy.
 *
 * The default oracle jdk implementation {@code com.sun.rowset.CachedRowSetImpl} does not support {@code isClosed}
 * method. Thus, keep close status and return it when {@code isClosed()} is called.
 *
 * @author Tadaya Tsuyukubo
 * @see CachedRowSetResultSetProxyLogicFactory
 * @since 1.4.7
 */
public class CachedRowSetResultSetProxyLogic extends ProxyLogicSupport implements ResultSetProxyLogic {

    private ResultSet resultSet;  // original resultset
    private ResultSet cachedRowSet;
    private ConnectionInfo connectionInfo;
    private ProxyConfig proxyConfig;

    // default impl "com.sun.rowset.CachedRowSetImpl" does NOT support "isClosed()", so enable handling in this class
    protected boolean supportIsClosedMethod = true;
    protected boolean isClosed;

    public CachedRowSetResultSetProxyLogic(ResultSet resultSet, ResultSet cachedRowSet, ConnectionInfo connectionInfo, ProxyConfig proxyConfig) {
        this.resultSet = resultSet;
        this.cachedRowSet = cachedRowSet;
        this.connectionInfo = connectionInfo;
        this.proxyConfig = proxyConfig;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return proceedMethodExecution(this.proxyConfig, this.cachedRowSet, this.connectionInfo, proxy, method, args);
    }

    @Override
    protected Object performProxyLogic(Object proxy, Method method, Object[] args, MethodExecutionContext methodContext) throws Throwable {

        String methodName = method.getName();

        if (isToStringMethod(methodName)) {
            return handleToStringMethod(this.resultSet);
        } else if ("hashCode".equals(methodName)) {
            return this.resultSet.hashCode();  // returns original resultset hashcode
        } else if ("equals".equals(methodName)) {
            return this.resultSet.equals(args[0]);  // compare with original resultset
        }

        if ("close".equals(methodName)) {
            this.isClosed = true;
        } else if (isGetTargetMethod(methodName)) {
            return this.resultSet;  // ProxyJdbcObject interface has a method to return original object.
        }

        if (this.supportIsClosedMethod && "isClosed".equals(methodName)) {
            return this.isClosed;
        }

        // TODO: handle getStatement() method to return proxied statement

        try {
            return proceedExecution(method, this.cachedRowSet, args);
        } catch (Throwable throwable) {
            if (throwable instanceof SQLException) {
                throw throwable;
            }
            // convert any exception to SQLException.
            String reason = String.format("CachedRowSet threw exception: %s", throwable);
            throw new SQLException(reason, throwable);
        }
    }
}
