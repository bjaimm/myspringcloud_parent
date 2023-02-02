package com.herosoft.user.handler;

import com.google.common.collect.Maps;
import com.herosoft.user.annotations.HandlerType;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
@SuppressWarnings("unchecked")
public class HandlerProcessor implements BeanFactoryPostProcessor, ResourceLoaderAware, EnvironmentAware {
    private final String HANDLER_PACKAGE="com.herosoft.user.handler";
    private final Integer HANDLER_MAP_SIZE=2;

    private ResourceLoader resourceLoader;

    private Environment environment;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        Map<String,Class> handlerMap = Maps.newHashMapWithExpectedSize(HANDLER_MAP_SIZE);

        System.out.println("HandlerProcessor->postProcessBeanFactory is called...");
        //创建scanner
        ClassPathScanningCandidateComponentProvider scanner =getScanner();
        scanner.setResourceLoader(resourceLoader);

        //设置scanner过滤条件
        AnnotationTypeFilter annotationTypeFilter = new AnnotationTypeFilter(HandlerType.class);
        scanner.addIncludeFilter(annotationTypeFilter);

        Set<BeanDefinition> candidateComponents = scanner.findCandidateComponents(HANDLER_PACKAGE);
        candidateComponents.forEach(candidateComponent -> {
            try {
                Class clazz = Class.forName(candidateComponent.getBeanClassName());

                handlerMap.put(clazz.getSimpleName(),clazz);

            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });

        HandlerContext context = new HandlerContext(handlerMap);

        configurableListableBeanFactory.registerSingleton(HandlerContext.class.getName(),context);

    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public ClassPathScanningCandidateComponentProvider getScanner(){
        return new ClassPathScanningCandidateComponentProvider(false,environment){

            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                boolean isCandidate=false;

                //判断是一个独立的类
                if(beanDefinition.getMetadata().isIndependent()){
                    //判断不是一个注解类型的类
                    if(!beanDefinition.getMetadata().isAnnotation()){
                        isCandidate = true;
                    }
                }
                return isCandidate;
            }
        };
    }
}
