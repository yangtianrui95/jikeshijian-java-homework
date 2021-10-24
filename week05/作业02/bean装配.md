
### 1. 使用xml进行装配

```xml
    <bean name="simpleBean"
          class="spring.bean.SimpleBean">
        <property name="name" value="simpleBeanName"/>
        <property name="msg" value="simpleBeanMsg"/>
    </bean>
```

```java
final ClassPathXmlApplicationContext context=
    new ClassPathXmlApplicationContext("classpath:beans-test.xml");
final Object simpleBean = context.getBean("simpleBean");
    log.info("simpleBean: {}", simpleBean);
```

### 2. 使用@Bean进行装配
```java

final AnnotationConfigApplicationContext annoContext = new AnnotationConfigApplicationContext();
annoContext.register(SimpleBeanProvider.class);
annoContext.refresh();
final Object bean = annoContext.getBean("simpleBeanByProvider");
log.info("bean: {}", bean);
```

```java

@Slf4j
public class SimpleBeanProvider {

    public SimpleBeanProvider() {
        log.info("SimpleBeanProvider");
    }

    @Bean(name = "simpleBeanByProvider")
    public SimpleBean simpleBean() {
        log.info("simpleBean");
        final SimpleBean simpleBean = new SimpleBean();
        simpleBean.setName("simpleBeanProvider");
        return simpleBean;
    }
}
```


### 3. 使用FactoryBean接口进行装配


#### 3.1 使用FactoryBean接口
```xml
<bean name="createByFactory" class="spring.bean.SimpleBeanFactory"/>
```

```java
public class SimpleBeanFactory implements FactoryBean<SimpleBean> {
    @Override
    public SimpleBean getObject() throws Exception {
        final SimpleBean simpleBean = new SimpleBean();
        simpleBean.setName("SimpleBeanFactory");
        return simpleBean;
    }

    @Override
    public Class<?> getObjectType() {
        return SimpleBean.class;
    }
}
```


```java
final Object beanFactory = context.getBean("createByFactory");
log.info("bean: {}", beanFactory);
```

####3.2 使用factory-bean标签

1. 先定义一个Factory，并写一个工厂方法

```java

public class SimpleBeanFactory2 {

    public SimpleBean create() {
        final SimpleBean simpleBean = new SimpleBean();
        simpleBean.setName("factory2");
        return simpleBean;
    }

    public static SimpleBean createInStatic() {
        final SimpleBean simpleBean = new SimpleBean();
        simpleBean.setName("factory2");
        return simpleBean;
    }
}
```
2. xml中配置刚才定义的facotry

```xml
<!--先定义FactoryBean-->
<bean class="spring.bean.SimpleBeanFactory2" name="factory2"/>
<!--通过factory-bean配置对应的factory-->
<bean name="createByFactory2" factory-bean="factory2" factory-method="create"/>
```

#### 4.使用scan加载
```java
final AnnotationConfigApplicationContext annoContext = new AnnotationConfigApplicationContext();
// 扫描spring包下所有的Bean
annoContext.scan("spring.**.*");
annoContext.refresh();
final Object bean = annoContext.getBean("simpleBean");
log.info("bean: {}",bean );
```

```java
@Slf4j
@ToString
@Component("simpleBean")
public class SimpleBean implements BeanNameAware, ApplicationContextAware, BeanClassLoaderAware, InitializingBean {
//...
}
```

