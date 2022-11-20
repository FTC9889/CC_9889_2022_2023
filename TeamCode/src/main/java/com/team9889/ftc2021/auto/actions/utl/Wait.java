package com.team9889.ftc2021.auto.actions.utl;

import com.qualcomm.robotcore.util.ElapsedTime;
import com.team9889.ftc2021.auto.actions.Action;

/**
 * Created by joshua9889 on 8/28/2018.
 */

public class Wait extends Action {
    private ElapsedTime t;
    private int timeToWaitMilli;

    /**
     * @param waitTime Milliseconds
     */
    public Wait(int waitTime){
        timeToWaitMilli = waitTime;
    }

    @Override
    public void start() {
        t = new ElapsedTime();
    }

    @Override
    public void update() {

    }

    @Override
    public boolean isFinished() {
        try {
            return t.milliseconds()>timeToWaitMilli;
        } catch (Exception e){
            return false;
        }
    }

    @Override
    public void done() {}
}
