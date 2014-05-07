package be.cegeka.batchers.taxcalculator.batch.integration;

import org.mockito.Mockito;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.reset;

public class MockResetter implements BeanPostProcessor {

    private Set<Object> mocks = new HashSet<>();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String s) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String s) throws BeansException {
        registerMockIfNecessary(bean);
        return bean;
    }

    public void resetMocks() {
        mocks.forEach(mock -> reset(mock));
    }

    private void registerMockIfNecessary(Object bean) {
        if(bean.getClass().getName().contains("Mockito")) {
            mocks.add(bean);
        }
    }
}
