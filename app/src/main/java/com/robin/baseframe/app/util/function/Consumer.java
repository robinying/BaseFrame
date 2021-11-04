package com.robin.baseframe.app.util.function;


import java.util.Objects;

@FunctionalInterface
public interface Consumer<T> {

    /**
     * Performs this operation on the given argument.
     *
     * @param t the input argument
     */
    void accept(T t);

    @FunctionalInterface
    interface Consumer2<A, B>{
        void accept(A a, B b);
    }

    @FunctionalInterface
    interface Consumer3<A, B, C>{
        void accept(A a, B b, C c);
    }

    @FunctionalInterface
    interface Consumer4<A, B, C, D>{
        void accept(A a, B b, C c, D d);
    }

    @FunctionalInterface
    interface Consumer5<A, B, C, D, E>{
        void accept(A a, B b, C c, D d, E e);
    }

    /**
     * Returns a composed {@code Consumer} that performs, in sequence, this
     * operation followed by the {@code after} operation. If performing either
     * operation throws an exception, it is relayed to the caller of the
     * composed operation.  If performing this operation throws an exception,
     * the {@code after} operation will not be performed.
     *
     * @param after the operation to perform after this operation
     * @return a composed {@code Consumer} that performs in sequence this
     * operation followed by the {@code after} operation
     * @throws NullPointerException if {@code after} is null
     */
    default Consumer<T> andThen(Consumer<? super T> after) {
        Objects.requireNonNull(after);
        return (T t) -> { accept(t); after.accept(t); };
    }
}
