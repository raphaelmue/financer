package org.financer.shared.domain.model.value.objects;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("unit")
public class CategoryClassTest {

    @Test
    public void testIsFixed() {
        assertThat(new CategoryClass(CategoryClass.Values.FIXED_REVENUE).isFixed()).isTrue();
        assertThat(new CategoryClass(CategoryClass.Values.FIXED_EXPENSES).isFixed()).isTrue();
        assertThat(new CategoryClass(CategoryClass.Values.VARIABLE_REVENUE).isFixed()).isFalse();
        assertThat(new CategoryClass(CategoryClass.Values.VARIABLE_EXPENSES).isFixed()).isFalse();
    }

    @Test
    public void testIsRevenue() {
        assertThat(new CategoryClass(CategoryClass.Values.FIXED_REVENUE).isRevenue()).isTrue();
        assertThat(new CategoryClass(CategoryClass.Values.FIXED_EXPENSES).isRevenue()).isFalse();
        assertThat(new CategoryClass(CategoryClass.Values.VARIABLE_REVENUE).isRevenue()).isTrue();
        assertThat(new CategoryClass(CategoryClass.Values.VARIABLE_EXPENSES).isRevenue()).isFalse();
    }

    @Test
    public void testGetAll() {
        assertThat(CategoryClass.getAll()).hasSize(4);
    }
}