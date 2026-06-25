package service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExemploJUnitTest {

    @Test
    void deveSomarCorretamente() {
        int resultado = 2 + 3;

        assertEquals(5, resultado);
    }
}