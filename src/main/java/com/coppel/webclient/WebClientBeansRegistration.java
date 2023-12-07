package com.coppel.webclient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.Environment;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;

import java.util.List;

public class WebClientBeansRegistration implements BeanDefinitionRegistryPostProcessor {

    private final Logger logger = LogManager.getLogger(WebClientBeansRegistration.class.getName());

    public static final String PROPERTIES_PREFIX = "web-services";
    public static final String PROXY_PROPERTIES_PREFIX = "proxy";

    private final List<WebService> webServices;
    private final WebClient.Builder builder;

    private final String proxyHost;
    private final String proxyPort;

    public WebClientBeansRegistration(Environment environment, WebClient.Builder builder) {
        this.webServices = Binder.get(environment)
                .bind(PROPERTIES_PREFIX, Bindable.listOf(WebService.class))
                .orElseThrow(IllegalStateException::new);

        this.proxyHost = Binder.get(environment)
                .bind(PROXY_PROPERTIES_PREFIX+".ip", Bindable.of(String.class))
                .orElseThrow(IllegalStateException::new);
        this.proxyPort = Binder.get(environment)
                .bind(PROXY_PROPERTIES_PREFIX+".port", Bindable.of(String.class))
                .orElseThrow(IllegalStateException::new);

        this.builder = builder;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        for (WebService webService : webServices) {
            GenericBeanDefinition beanDefinition = new GenericBeanDefinition();

            beanDefinition.setBeanClass(WebClient.class);

            if (Boolean.TRUE.equals(webService.getUseProxy())) {
                HttpClient httpClient =
                        HttpClient.create()
                                .proxy(proxy -> proxy.type(ProxyProvider.Proxy.HTTP)
                                        .host(proxyHost)
                                        .port( Integer.parseInt(proxyPort) )
                                );

                ReactorClientHttpConnector conn = new ReactorClientHttpConnector(httpClient);
                beanDefinition.setInstanceSupplier(
                        () ->
                            builder
                                .baseUrl(webService.getUrl())
                                .clientConnector(conn)
                                .build()
                );
            } else {
                beanDefinition.setInstanceSupplier( () -> builder.baseUrl(webService.getUrl()).build() );
            }

            registry.registerBeanDefinition(webService.getName(), beanDefinition);
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        String msg;
        for (WebService webService : webServices) {
            msg = String.format("WebClient bean created with name: \"%s\"",webService.getName());
            logger.info(msg);
        }
    }
}
