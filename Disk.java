// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP102 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP102 - 2025T1, Assignment 7
 * Name: Matthew McGowan
 * Username: mcgowamatt1
 * ID: 300672872
 */

import ecs100.*;
import java.awt.Color;
import java.util.*;
import java.io.*;

/**
 * Disks for the disk shooting game.
 * Disks are created at a given position, and are drawn as ovals
 * Disks have a measure of damage, rising from 0 to 3, at which point they
 *  are considered broken. Their colour changes as they take more damage.
 * Disks have the following methods:
 *   - draw()        Redraws the disk
 *   - damage()      Increases the damage level of the disk
 *   - isBroken()    Whether the disk is broken yet, or not
 *   - explode()     Make the disk explode (visual effect only)
 *   - score()       How much this is disk worth
 *   - isOn(x,y)                Whether the point (x,y) is on top of the disk
 *   - isOverlapping(otherDisk) Whether this disk overlaps the other disk.
 *   - isWithinRange(otherDisk) Whether this disk is within the damage range of the other disk.
 */
public class Disk {
    public static final int DIAMETER = 20;

    //Damage level at which a disk will explode.
    private static final int DAMAGE_TO_BREAK = 3;

    //maximum distance between 2 disks for one exploding to damage the other
    private static final int EXPLOSION_RANGE = 50; 

    private double centerX;
    private double centerY;
    private int damage = 0; // Current damage level of this disk (ranging from 0 to 3)

    /**
     * Construct a new Disk object.
     * Parameters are the coordinates of the center of the disk.
     */
    public Disk(double x, double y) {
        this.centerX = x;
        this.centerY = y;
    }

    /** B-GRADE LEVEL
     *
     * Construct a new Disk object.
     * Parameters are the coordinates of the center of the disk, and its damage.
     */
    public Disk(double x, double y, int d) {
        /*# YOUR CODE HERE */
        this.centerX = x;
        this.centerY = y;
        this.damage = d;
    }

    /** B-GRADE LEVEL
     *
     * Return a string describing the disk: the coordinates of the center of the disk,
     * and its damage.
     * Must mirror the second constructor
     */
    public String toString(){
        /*# YOUR CODE HERE */
        return this.centerX + " " + this.centerY + " " + this.damage;
    }

    /**
     * Draw the disk.
     * The colour ranges from red, to orange, to yellow as the damage increases
     */
    public void draw() {
        if (!isBroken()) {
            if (this.damage == 0){ UI.setColor(Color.red); }
            else if (this.damage == 1) { UI.setColor(Color.orange); }
            else if (this.damage >= 2) { UI.setColor(Color.yellow); }
            UI.fillOval(this.centerX - DIAMETER/2, this.centerY-DIAMETER/2, DIAMETER, DIAMETER);
            UI.setColor(Color.darkGray);
            UI.drawOval(this.centerX - DIAMETER/2, this.centerY-DIAMETER/2, DIAMETER, DIAMETER);
        }
    }

    /**
     * Increase the damage level of the disk  
     */
    public void damage() {
        this.damage++;
    }

    /**
     * Is the disk broken.
     */
    public boolean isBroken() {
        return this.damage >= DAMAGE_TO_BREAK;
    }

    /** 
     * Is the given point on the disk? 
     */
    public boolean isOn(double x, double y) {
        if (isBroken()) return false;
        double dx = this.centerX - x;
        double dy = this.centerY - y;
        return ((dx*dx + dy*dy) < (DIAMETER/2 * DIAMETER/2));
    }

    /**
     *  Is this disk overlapping with the other disk?
     *  If either disk is broken, then they aren't overlapping
     */
    public boolean isOverlapping(Disk other) {
        if (this.isBroken() || other.isBroken()) return false;
        double dx = other.centerX - this.centerX;
        double dy = other.centerY - this.centerY;
        return (Math.hypot(dx,dy) < DIAMETER);
    }

    /**
     *  Is this disk withing range of explosion damage from the other disk 
     */
    public boolean isWithinRange(Disk other) {
        double dx = other.centerX - this.centerX;
        double dy = other.centerY - this.centerY;
        return (Math.hypot(dx,dy) < EXPLOSION_RANGE);
    }

    /**
     * Make a visual effect of the disk exploding
     */
    public void explode() {
        UI.setColor(Color.red);
        UI.setLineWidth(8);
        UI.invertOval(this.centerX - EXPLOSION_RANGE+4, this.centerY-EXPLOSION_RANGE+4,
            EXPLOSION_RANGE*2-8, EXPLOSION_RANGE*2-8);
        UI.sleep(100);
        UI.invertOval(this.centerX - EXPLOSION_RANGE+4, this.centerY-EXPLOSION_RANGE+4,
            EXPLOSION_RANGE*2-8, EXPLOSION_RANGE*2-8);
        UI.eraseOval(this.centerX - DIAMETER/2, this.centerY-DIAMETER/2, DIAMETER, DIAMETER);
        UI.setLineWidth(1);
    }

    /**
     * How much this disk is worth 
     * Score is 150 for exploded disks, 50 for disks with 2 hits, and 20 for disks with 1 hit.
     */
    public int score() {
        if (isBroken()) {
            return 150;
        }
        else if (this.damage == 2) {
            return 50;
        }
        else if (this.damage == 1) {
            return 20;
        }
        else {
            return 0;
        }
    }
}
