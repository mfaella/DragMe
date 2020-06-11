package com.example.mfaella.physicsapp;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.util.Log;

import com.google.fpl.liquidfun.BodyDef;
import com.google.fpl.liquidfun.BodyType;
import com.google.fpl.liquidfun.FixtureDef;
import com.google.fpl.liquidfun.ParticleFlag;
import com.google.fpl.liquidfun.ParticleGroup;
import com.google.fpl.liquidfun.ParticleGroupDef;
import com.google.fpl.liquidfun.ParticleGroupFlag;
import com.google.fpl.liquidfun.ParticleSystem;
import com.google.fpl.liquidfun.PolygonShape;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * A group of particles.
 *
 * Created by mfaella on 27/02/16.
 */
public class MarblesGO extends GameObject
{
    private static final int BYTESPERPARTICLE = 8;

    // Architecture-dependent parameters
    private static int bufferOffset;
    private static boolean isLittleEndian;

    private byte[] particlePositions;
    private ByteBuffer particlePositionsBuffer;
    private int particleCount;

    private final Canvas canvas;
    private final Paint paint = new Paint();
    private final ParticleSystem psys;
    private final ParticleGroup group;

    static {
        discoverEndianness();
    }

    public MarblesGO(GameWorld gw, float x, float y)
    {
        super(gw);

        this.canvas = new Canvas(gw.buffer);
        this.psys = gw.particleSystem;

        paint.setARGB(255, 0, 255, 0);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);

        PolygonShape box = new PolygonShape();
        box.setAsBox(1, 1);
        ParticleGroupDef groupDef = new ParticleGroupDef();
        groupDef.setShape(box);
        groupDef.setPosition(x, y);
        // NEW:
        groupDef.setGroupFlags(ParticleGroupFlag.solidParticleGroup);
        groupDef.setFlags(ParticleFlag.elasticParticle);
        group = gw.particleSystem.createParticleGroup(groupDef);
        this.particleCount = group.getParticleCount();

        particlePositionsBuffer = ByteBuffer.allocateDirect(particleCount * BYTESPERPARTICLE);
        particlePositions = particlePositionsBuffer.array();

        Log.d("DragMe", "Created " + group.getParticleCount() + " particles");

        // no body
        this.body = null;

        // clean up native objects
        groupDef.delete();
        box.delete();
    }

    @Override
    public void draw(Bitmap buffer, float _x, float _y, float _angle) {
        /* The SLOW way to draw particles, one at a time.

        for (int i=0; i<particleCount; i++) {
            float x = psys.getParticlePositionX(i), y = psys.getParticlePositionY(i);
            canvas.drawCircle(gw.toPixelsX(x), gw.toPixelsY(y), 6, paint);
        } */

        psys.copyPositionBuffer(0, particleCount, particlePositionsBuffer);

        for (int i = 0; i < particleCount; i++) {
            int xint, yint;
            if (isLittleEndian) {
                xint = (particlePositions[i * 8 + bufferOffset] & 0xFF) | (particlePositions[i * 8 + bufferOffset + 1] & 0xFF) << 8 |
                        (particlePositions[i * 8 + bufferOffset + 2] & 0xFF) << 16 | (particlePositions[i * 8 + bufferOffset + 3] & 0xFF) << 24;
                yint = (particlePositions[i * 8 + bufferOffset + 4] & 0xFF) | (particlePositions[i * 8 + bufferOffset + 5] & 0xFF) << 8 |
                        (particlePositions[i * 8 + bufferOffset + 6] & 0xFF) << 16 | (particlePositions[i * 8 + bufferOffset + 7] & 0xFF) << 24;
            } else {
                xint = (particlePositions[i * 8] & 0xFF) << 24 | (particlePositions[i * 8 + 1] & 0xFF) << 16 |
                        (particlePositions[i * 8 + 2] & 0xFF) << 8 | (particlePositions[i * 8 + 3] & 0xFF);
                yint = (particlePositions[i * 8 + 4] & 0xFF) << 24 | (particlePositions[i * 8 + 5] & 0xFF) << 16 |
                        (particlePositions[i * 8 + 6] & 0xFF) << 8 | (particlePositions[i * 8 + 7] & 0xFF);
            }

            float x = Float.intBitsToFloat(xint), y = Float.intBitsToFloat(yint);
            canvas.drawCircle(gw.toPixelsX(x), gw.toPixelsY(y), 6, paint);
        }
    }

    public static void discoverEndianness() {
        isLittleEndian = (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN);

        Log.d("DEBUG", "Build.FINGERPRINT=" + Build.FINGERPRINT);
        Log.d("DEBUG", "Build.PRODUCT=" + Build.PRODUCT);
        // An ugly trick, can we do better?
        // Not working with new emulator: generic, sdk
        /* if (Build.FINGERPRINT.contains("generic") ||
            Build.FINGERPRINT.contains("unknown") ||
            Build.PRODUCT.contains("sdk"))
                bufferOffset = 0; // emulator
            else
                bufferOffset = 4; // real device
                */
        bufferOffset = 4; // forced
    }
}