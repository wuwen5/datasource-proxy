package net.ttddyy.dsproxy.proxy;

import net.ttddyy.dsproxy.listener.QueryExecutionContext;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.listener.ProxyDataSourceListener;
import org.mockito.ArgumentCaptor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

/**
 * @author Tadaya Tsuyukubo
 */
public class MockTestUtils {

    // TODO: this needs clean up

    @SuppressWarnings("unchecked")
    public static void verifyListenerForBatch(ProxyDataSourceListener listener, String dataSourceName, String query,
                                              Map<String, Object>... expectedQueryParamsArray) {
        ArgumentCaptor<QueryExecutionContext> executionContextCaptor = ArgumentCaptor.forClass(QueryExecutionContext.class);

        verify(listener).afterQuery(executionContextCaptor.capture());


        final int expectedBatchSize = expectedQueryParamsArray.length;

        QueryExecutionContext queryContext = executionContextCaptor.getValue();
        assertThat(queryContext.getMethod()).isNotNull();
        assertThat(queryContext.getMethod().getName()).isEqualTo("executeBatch");

        assertThat(queryContext.getMethodArgs()).isNull();
        assertThat(queryContext.getDataSourceName()).isEqualTo(dataSourceName);
        assertThat(queryContext.getThrowable()).isNull();
        assertThat(queryContext.isBatch()).isTrue();
        assertThat(queryContext.getBatchSize()).isEqualTo(expectedBatchSize);

        List<QueryInfo> queryInfoList = queryContext.getQueries();
        assertThat(queryInfoList).hasSize(1).as("for prepared/callable statement, batch query size is always 1");
        QueryInfo queryInfo = queryInfoList.get(0);
        assertThat(queryInfo.getQuery()).isEqualTo(query);
        assertThat(queryInfo.getParameterSetOperations()).hasSize(expectedBatchSize);

        for (int i = 0; i < expectedBatchSize; i++) {
            Map<String, Object> expectedQueryArgs = expectedQueryParamsArray[i];
            Map<String, Object> actualQueryArgs = new HashMap<String, Object>();
            ParameterSetOperations operations = queryInfo.getParameterSetOperations().get(i);
            for (ParameterSetOperation operation : operations.getOperations()) {
                Object[] args = operation.getArgs();
                actualQueryArgs.put(args[0].toString(), args[1]);
            }


            assertThat(actualQueryArgs).hasSize(expectedQueryArgs.size());
            for (Map.Entry<String, Object> entry : expectedQueryArgs.entrySet()) {
                assertThat(actualQueryArgs).containsEntry(entry.getKey(), entry.getValue());
            }
        }

        // TODO: change
//        for (int i = 0; i < expectedBatchSize; i++) {
//            Object[] queryArgs = expectedQueryArgsList[i];
//
//            verifyQueryInfo(queryInfo, query, queryArgs);
//        }

    }

//    private static void verifyQueryInfo(QueryInfo queryInfo, String query, Object... args) {
//        assertThat(queryInfo.getQuery(), is(equalTo(query)));
//
//        List<?> queryArgs = queryInfo.getQueryArgsList();
//        assertThat(queryArgs.size(), is(args.length));
//
//        for (int i = 0; i < queryArgs.size(); i++) {
//            Object value = queryArgs.get(i);
//            Object expected = args[i];
//
//            assertThat(value, is(expected));
//        }
//
//    }

}
