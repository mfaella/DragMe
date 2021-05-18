package com.example.mfaella.physicsapp;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import com.google.fpl.liquidfun.BodyDef;
import com.google.fpl.liquidfun.BodyType;
import com.google.fpl.liquidfun.FixtureDef;
import com.google.fpl.liquidfun.PolygonShape;

public class DynamicTriangleGO extends GameObject {
    private static final float semi_width = 2f, density = 0.5f;
    private static float screen_semi_width;
    private static int instances = 0;

    private final Canvas canvas;
    private final Paint paint = new Paint();

    public DynamicTriangleGO(GameWorld gw, float x, float y)
    {
        super(gw);

        instances++;

        this.canvas = new Canvas(gw.buffer); // Is this needed?
        this.screen_semi_width = gw.toPixelsXLength(semi_width);

        // a body definition: position and type
        BodyDef bdef = new BodyDef();
        bdef.setPosition(x, y);
        bdef.setType(BodyType.dynamicBody);
        // a body
        this.body = gw.world.createBody(bdef);
        body.setSleepingAllowed(false);
        this.name = "Triangle" + instances;
        body.setUserData(this);

        PolygonShape triangle = new PolygonShape();
        triangle.setAsTriangle(-semi_width, -semi_width, semi_width, -semi_width, 0, semi_width);
        FixtureDef fixturedef = new FixtureDef();
        fixturedef.setShape(triangle);
        fixturedef.setFriction(0.1f);       // default 0.2
        fixturedef.setRestitution(0.4f);    // default 0
        fixturedef.setDensity(density);     // default 0
        body.createFixture(fixturedef);

        int green = (int)(255*Math.random());
        int color = Color.argb(200, 255, green, 0);
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);

        // clean up native objects
        fixturedef.delete();
        bdef.delete();
        triangle.delete();
    }

    private Path path = new Path();

    @Override
    public void draw(Bitmap buffer, float x, float y, float angle) {
        canvas.save();
        canvas.rotate((float) Math.toDegrees(angle), x, y);
        path.reset();
        path.moveTo(x-screen_semi_width, y-screen_semi_width);
        path.lineTo(x+screen_semi_width, y-screen_semi_width);
        path.lineTo(x, y+screen_semi_width);
        path.lineTo(x-screen_semi_width, y-screen_semi_width);
        canvas.drawPath(path, paint);
        canvas.restore();
    }
}
