
package com.quartercode.mocl.test.base.def;

import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import com.quartercode.mocl.base.FeatureDefinition;
import com.quartercode.mocl.base.FeatureHolder;
import com.quartercode.mocl.base.def.AbstractFeature;
import com.quartercode.mocl.base.def.AbstractFeatureDefinition;
import com.quartercode.mocl.base.def.DefaultFeatureHolder;
import com.quartercode.mocl.extra.Persistent;

public class DefaultFeatureHolderTest {

    private static FeatureDefinition<TestFeature1>    TEST_FEATURE_1;

    private static FeatureDefinition<AbstractFeature> TEST_FEATURE_2;

    @BeforeClass
    public static void setUpBeforeClass() {

        TEST_FEATURE_1 = new AbstractFeatureDefinition<TestFeature1>("testFeature1") {

            @Override
            public TestFeature1 create(FeatureHolder holder) {

                return new TestFeature1(getName(), holder);
            }

        };

        TEST_FEATURE_2 = new AbstractFeatureDefinition<AbstractFeature>("testFeature2") {

            @Override
            public AbstractFeature create(FeatureHolder holder) {

                return new AbstractFeature(getName(), holder);
            }

        };
    }

    private DefaultFeatureHolder featureHolder;

    @Before
    public void setUp() {

        featureHolder = new DefaultFeatureHolder();
    }

    @Test
    public void testGet() {

        Assert.assertEquals("Name of TEST_FEATURE_1", "testFeature1", featureHolder.get(TEST_FEATURE_1).getName());
        Assert.assertEquals("Name of TEST_FEATURE_2", "testFeature2", featureHolder.get(TEST_FEATURE_2).getName());
    }

    @Test
    public void testGetPersistentFeatures() {

        // Add feature objects
        featureHolder.get(TEST_FEATURE_1);
        featureHolder.get(TEST_FEATURE_2);

        Assert.assertTrue("Persistent features list doesn't contain TEST_FEATURE_1", featureHolder.getPersistentFeatures().contains(featureHolder.get(TEST_FEATURE_1)));
        Assert.assertFalse("Persistent features list contains TEST_FEATURE_2", featureHolder.getPersistentFeatures().contains(featureHolder.get(TEST_FEATURE_2)));
    }

    @Test
    public void testSetPersistentFeatures() {

        List<Object> features = new ArrayList<Object>();
        features.add(new AbstractFeature("testFeature", featureHolder));
        featureHolder.setPersistentFeatures(features);

        List<Object> actualFeatures = new ArrayList<Object>();
        for (Object feature : featureHolder) {
            actualFeatures.add(feature);
        }

        Assert.assertEquals("Added features", features, actualFeatures);
    }

    @Persistent
    private static class TestFeature1 extends AbstractFeature {

        public TestFeature1(String name, FeatureHolder holder) {

            super(name, holder);
        }

    }

}
