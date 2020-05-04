package com.example.mfaella.physicsapp;

/** An unordered pair of game objects.
 *
 */
public class Collision {
    GameObject a, b;

    public Collision(GameObject a, GameObject b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public int hashCode() {
        return a.hashCode() ^ b.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Collision))
            return false;
        Collision otherCollision = (Collision) other;
        return (a.equals(otherCollision.a) && b.equals(otherCollision.b)) ||
               (a.equals(otherCollision.b) && b.equals(otherCollision.a));
    }
}
