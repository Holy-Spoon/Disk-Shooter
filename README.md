# Disk-Shooter
     * The core mechanic of the game is to fire a shot.
     * - Update the number of shots remaining.
     * - Move the shot up the screen in the correct direction from the gun, step by step, until 
     *   it either goes off the screen or hits a disk.
     *   The shot is constantly redrawn as a line from the gun to its current position.
     * - If the shot hits a disk,
     *   - it damages the disk, 
     *   - If the disk is now broken, then
     *     it will damage its neighbours
     *     (ie, all the other disks within range will be damaged also)
     *   - it exits the loop.
     * - Redraw the game
     * - Finally, update the score,
     * - If the game is now over,  print out the score 
     * (You should define additional methods - don't do it all in one big method!)

<img width="990" height="642" alt="image" src="https://github.com/user-attachments/assets/7a086dc3-f8ed-43f5-b5c5-01e2f3bca464" />

     
  <img width="992" height="673" alt="image" src="https://github.com/user-attachments/assets/461382e0-d137-4ce8-a3ad-ce553b9c2d7b" />
