package org.financer.client.domain.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("unit")
public class RestApiImplTest {
    private final RestApiImpl restApi = new RestApiImpl();

    @BeforeEach
    public void setUp() {

    }

    @Test
    public void testCreateCategory() {

    }

    @Test
    public void testUpdateCategory() {

    }

    @Test
    public void testDeleteCategory() {
        restApi.deleteCategory(1L, result -> {
            assertThat(result).isNull();
        }).execute();
    }

}