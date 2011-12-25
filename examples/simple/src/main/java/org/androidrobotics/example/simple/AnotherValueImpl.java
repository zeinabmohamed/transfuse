package org.androidrobotics.example.simple;

import android.widget.Button;
import org.androidrobotics.annotations.OnCreate;
import org.androidrobotics.annotations.View;

import javax.inject.Inject;

/**
 * @author John Ericksen
 */
public class AnotherValueImpl implements AnotherValue {

    private SubComponent subComponent;
    @Inject
    @View(R.id.secondActivity)
    private Button secondActivityButton;
    @Inject
    private GotoSecondActivity gotoSecondActivity;

    @Inject
    public AnotherValueImpl(SubComponent subComponent) {
        this.subComponent = subComponent;
    }

    @OnCreate
    public void registerSecondActivity() {
        secondActivityButton.setOnClickListener(gotoSecondActivity);
    }

    public Boolean doWork() {
        return true;
    }
}
