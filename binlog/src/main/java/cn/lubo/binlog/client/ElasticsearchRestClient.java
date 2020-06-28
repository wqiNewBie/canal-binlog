package cn.lubo.binlog.client;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ELASTICSEARCH
 * 连接客户端
 *
 * @author wq
 * @create 2020-6-24 16:39:56
 */
@Configuration
public class ElasticsearchRestClient {


    @Value("${elasticsearch.host}")
    private String esHostName;

    @Value("${elasticsearch.port}")
    private int esServerPort;

    @Bean(name = "RestHighLevelClient")
    public RestHighLevelClient highLevelClient(/*@Autowired RestClientBuilder restClientBuilder*/) {
        /** 用户认证对象 */
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        /** 设置账号密码 */
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials("elastic", "Lujs@com_Elastic2019"));
        /** 创建rest client对象 */
        RestClientBuilder builder = RestClient.builder(new HttpHost(esHostName, esServerPort))
                .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                    @Override
                    public HttpAsyncClientBuilder customizeHttpClient(
                            HttpAsyncClientBuilder httpClientBuilder) {
                        return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                    }
                });
        return new RestHighLevelClient(builder);
    }
}