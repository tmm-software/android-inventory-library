package org.flyve.example_java;

import android.Manifest;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.GrantPermissionRule;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import org.flyve.inventory.InventoryTask;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Collection;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.runner.lifecycle.Stage.RESUMED;
import static org.junit.Assert.*;

public class MainActivityTest {

    private Context instrumentationContext;
    private Activity activity;

    @Rule public GrantPermissionRule externalStorage = GrantPermissionRule.grant(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
    @Rule public GrantPermissionRule readExternalStorage = GrantPermissionRule.grant(android.Manifest.permission.READ_EXTERNAL_STORAGE);
    @Rule public GrantPermissionRule camera = GrantPermissionRule.grant(android.Manifest.permission.CAMERA);


    @Before
    public void  setup() {
        instrumentationContext = getInstrumentation().getContext();
        activity = getCurrentActivity();

    }

    private static Activity getCurrentActivity() {
        final Activity[] currentActivity = {null};
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                Collection resumedActivities = ActivityLifecycleMonitorRegistry.getInstance()
                        .getActivitiesInStage(RESUMED);
                if (resumedActivities.iterator().hasNext()) {
                    currentActivity[0] = (Activity) resumedActivities.iterator().next();
                }
            }
        });
        return currentActivity[0];
    }




    @Test
    public void execInventoryXMLTask() throws Exception {
        InventoryTask inventoryTask = new InventoryTask(instrumentationContext, "example-app-java", true);
        inventoryTask.getXML(new InventoryTask.OnTaskCompleted() {
            @Override
            public void onTaskSuccess(String data) {
            }

            @Override
            public void onTaskError(Throwable error) {
                Assert.fail("Should have thrown error on get XML inventory -> "+ error.getMessage());
            }
        });
    }

    @Test
    public void execInventoryJSONTask() throws Exception {
        InventoryTask inventoryTask = new InventoryTask(instrumentationContext, "example-app-java", true);
         inventoryTask.getJSON(new InventoryTask.OnTaskCompleted() {
            @Override
            public void onTaskSuccess(String data) {
            }

            @Override
            public void onTaskError(Throwable error) {
                Assert.fail("Should have thrown error on get JSON inventory -> "+ error.getMessage());
            }
        });
    }


}