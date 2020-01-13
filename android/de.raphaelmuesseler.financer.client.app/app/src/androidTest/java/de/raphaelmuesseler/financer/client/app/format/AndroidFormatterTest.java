package de.raphaelmuesseler.financer.client.app.format;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.raphaelmuesseler.financer.client.app.local.LocalStorageImpl;
import de.raphaelmuesseler.financer.shared.model.categories.BaseCategory;
import de.raphaelmuesseler.financer.shared.model.categories.Category;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class AndroidFormatterTest {

    private Context context;

    @BeforeClass
    public void setup() {
        context = InstrumentationRegistry.getTargetContext();
        LocalStorageImpl.setContext(context);
    }

    @Test
    public void testFormatCategoryName() {
        Category category = new Category();
        category.setCategoryClass(BaseCategory.CategoryClass.VARIABLE_EXPENSES);
        category.setName("Test Category");
        category.setPrefix("3.1");


        AndroidFormatter formatter = new AndroidFormatter(LocalStorageImpl.getInstance(), context);

        assertEquals("3.1 Test Category", formatter.formatCategoryName(category));

    }

}
