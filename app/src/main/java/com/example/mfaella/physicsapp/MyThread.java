package com.example.mfaella.physicsapp;

import android.util.Log;

import com.google.fpl.liquidfun.Fixture;
import com.google.fpl.liquidfun.RayCastCallback;
import com.google.fpl.liquidfun.Vec2;


/**
 * Created by mfaella on 08/02/16.
 */
public class MyThread extends Thread {
    public volatile int counter;
    private GameWorld gw;

    public MyThread(GameWorld gw)
    {
        this.gw = gw;
    }

    private void testRayCasting() {
        Log.i("MyThread", "Objects across the short middle line:");
        RayCastCallback listener = new RayCastCallback() {
          @Override
          public float reportFixture(Fixture f, Vec2 point, Vec2 normal, float fraction) {
              Log.i("MyThread", ((GameObject)f.getBody().getUserData()).name + " (" + fraction + ")");
              return 1;
          }
        };
        gw.world.rayCast(listener, -10, 0, 10, 0);
    }

    @Override
    public void run() {

        while (true) {
            try {
                sleep(3000);
                counter++;
                Log.i("MyThread", "counter: " + counter);
                // inverts gravity
                /* float gravity_x = -4 + 8*(counter%2),
                        gravity_y = 0;
                   gw.setGravity(gravity_x, gravity_y); */
                testRayCasting();
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}
