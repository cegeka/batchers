package be.cegeka.batchers.taxcalculator.application.domain.email;

@FunctionalInterface
public interface FunctionWithException<T, R> {
    R apply(T t) throws Exception;
}
