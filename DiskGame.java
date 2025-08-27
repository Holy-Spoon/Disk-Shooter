import ecs100.*;
import java.awt.Color;
import java.util.*;
import java.nio.file.*;
import java.io.*;

public class DiskGame{
    private static final double GAME_WIDTH = 500;
    private static final double SHOOTING_RANGE_Y = 150; // lowest point that a disk can be
    private static final double GUN_X = GAME_WIDTH/2;   // current x position of the gun
    private static final double GUN_Y = 300;
    private static final double SHOOTING_CIRCLE = GUN_Y-SHOOTING_RANGE_Y;

    //Constants for game logic
    private static final int DEFAULT_NUMBER_OF_SHOTS = 30;
    private static final int DEFAULT_NUMBER_OF_DISKS = 30;
    private int numShots = DEFAULT_NUMBER_OF_SHOTS;
    private int numDisks = DEFAULT_NUMBER_OF_DISKS;

    //Fields for the game state
    private double score = 0;                        // current score
    private int shotsRemaining = this.numShots;      // How many shots are left

    private ArrayList <Disk> disks = new ArrayList<Disk>();
    private ArrayList<Disk> explodedDisks = new ArrayList<Disk>();

    /**
     * Sets up the user interface:
     * Set up the sliders, buttons and mouselistener
     */
    public void setupGUI(){
        /*# YOUR CODE HERE */
        UI.addSlider("Disks", 2, 61, DEFAULT_NUMBER_OF_DISKS, this::setNumDisks);
        UI.addSlider("Shots", 2, 61, DEFAULT_NUMBER_OF_SHOTS, this::setNumShots);
        UI.addButton("Restart", this::startGame);
        UI.addButton("Load Game", this::loadGame);
        UI.addButton("Save Game", this::saveGame);
        UI.addButton("Quit", UI::quit);
        UI.setMouseListener(this::doMouse);
        UI.setDivider(0);
    }

    /**
     * Set the number of disks for the next game
     * Hint: Remember to cast to an int
     */
    public void setNumDisks(double value){
        /*# YOUR CODE HERE */
        this.numDisks = (int)value;
    }

    /**
     * Set the number of shots for the next game
     * Hint: Remember to cast to an int
     */
    public void setNumShots(double value){
        /*# YOUR CODE HERE */
        this.numShots = (int)value;
    }

    /**
     * Set the fields of the game to their initial values,
     * Create a new list of disks
     * redraw the game
     */
    public void startGame(){ 
        this.shotsRemaining = this.numShots;
        this.score = 0;
        this.disks.clear();
        this.explodedDisks.clear();
        this.initialiseDisks();
        this.redraw();
        updateScore();
    }

    /**
     * Make a new ArrayList of disks with new disks at random positions
     * within the shooting range.
     * Remember to use the CONSTANTS
     * B-grade level: ensure than none of them are overlapping.
     */
     public void initialiseDisks(){
        Random rand = new Random();
        int attempts = 0;
        int maxAttempts = 1000;
        
        for (int i=0; i<this.numDisks; i++){
            double x, y;
            boolean overlapping;
            Disk newDisk;
            
            do {
                overlapping = false;
                x = rand.nextDouble() * (GAME_WIDTH - 10) ;
                y = rand.nextDouble() * (SHOOTING_RANGE_Y - 10);
                newDisk = new Disk(x, y);
                
                for (Disk d : disks){
                    if (d.isOverlapping(newDisk)){
                        overlapping = true;
                        break;
                    }
                }
                attempts++;
            } while (overlapping && attempts < maxAttempts);
            
            if (attempts < maxAttempts){
                disks.add(newDisk);
            }
        }
    }

    /**
     * Respond to the mouse
     */
    public void doMouse(String action, double x, double y){
        /*# YOUR CODE HERE */     
        if (action.equals("released")){
            if (this.shotsRemaining <= 0){
                UI.println("No shots left! Press Restart to play again.");
                return;
            }
            
            if (this.isWithinFiringZone(x, y)){
                this.fireShot(x, y);
            }
        }
    }

    /**
     * Is the given position within the firing zone
     */
    public boolean isWithinFiringZone(double x, double y){
        // an easy approximation is to pretend it is the enclosing rectangle.
        // It is nicer to do a little bit of geometry and get it right
        return (x >= GUN_X-SHOOTING_CIRCLE/2) && (y >= GUN_Y-SHOOTING_CIRCLE/2)
        && (x <= GUN_X + SHOOTING_CIRCLE/2) && (y <= GUN_Y);
    }

   
    public void fireShot(double x, double y){
        this.shotsRemaining--;
        double shotPosX = GUN_X;
        double shotPosY = GUN_Y;
        double step_X = (GUN_X-x)/(y-GUN_Y);
        UI.setColor(Color.black);
        
        while (!this.isShotOffScreen(shotPosX, shotPosY)){ 
            shotPosY -= 1;
            shotPosX += step_X;
            UI.drawLine(GUN_X, GUN_Y, shotPosX, shotPosY);
            
            /*# YOUR CODE HERE */        
            Disk hitDisk = this.getHitDisk(shotPosX, shotPosY);
            if (hitDisk != null){
                hitDisk.damage();
                if (hitDisk.isBroken()){
                    hitDisk.explode();
                    this.damageNeighbours(hitDisk);
                    disks.remove(hitDisk);
                    explodedDisks.add(hitDisk);
                }
                break;
            }
            UI.sleep(1);
        }
        
        this.redraw();
        this.updateScore();
        
        if ((this.haveAllDisksExploded() || this.shotsRemaining < 1)){
            UI.setColor(Color.red);
            UI.setFontSize(24);
            UI.drawString("Your final score: " + this.score, GAME_WIDTH*1.0/3.0, SHOOTING_RANGE_Y*1.3);
        }
    }

    /**
     * Is the shot out of the screen
     */
    public boolean isShotOffScreen(double x, double y) {
        return (x < 0 || y < 0 || x > GAME_WIDTH);
    }    

    /**
     * Does the given shot hit a disk? If yes, return that disk. Else return null
     * Useful when firing a shot
     * Hint: use the isOn method of the Disk class
     */
    public Disk getHitDisk(double shotX, double shotY){
        /*# YOUR CODE HERE */
        for (Disk d : disks){
            if (d.isOn(shotX, shotY)){
                return d;
            }
        }
        return null;
    }

    /**
     * Inflict damage on all the neighbours of the given disk
     * (ie, all disks that are within range of the disk, and are not already broken)
     */
    public void damageNeighbours(Disk initialDisk) {
        /*# YOUR CODE HERE */
        Queue<Disk> explosionQueue = new LinkedList<>();
        explosionQueue.add(initialDisk);
        
        while (!explosionQueue.isEmpty()) {
            Disk currentDisk = explosionQueue.poll();
            
            ArrayList<Disk> toDamage = new ArrayList<>();
            for (Disk d : disks) {
                if (d != currentDisk && currentDisk.isWithinRange(d)) {
                    toDamage.add(d);
                }
            }
            
            //iterative approach 
            for (Disk d : toDamage) {
                d.damage();
                if (d.isBroken()) {
                    d.explode();
                    disks.remove(d);
                    explodedDisks.add(d);
                    explosionQueue.add(d);
                }
            }
        }
    }
    
    /**
     * Are all the disks exploded?
     * Useful for telling whether the game is over.
     */
    public boolean haveAllDisksExploded(){
        /*# YOUR CODE HERE */
        return disks.isEmpty();
    }

    /**
     * Update the score field, by summing the scores of each disk
     * Score is 150 for exploded disks, 50 for disks with 2 hits, and 20 for disks with 1 hit.
     */
    public void updateScore(){
        this.score = 0;
        for (Disk d : disks){
            this.score += d.score();
        }
        for (Disk d : explodedDisks){
            this.score += d.score();
        }
        UI.println("Score: " + this.score + "  Shots remaining: " + this.shotsRemaining);
    }

    public void redraw(){
        UI.clearGraphics();
        //Redraw the boundary of the shooting range
        UI.setColor(Color.black);
        UI.drawRect(0,0, GAME_WIDTH, GUN_Y);
        UI.setColor(Color.gray);
        UI.drawLine(0, SHOOTING_RANGE_Y, GAME_WIDTH, SHOOTING_RANGE_Y);

        // Redraw the shooting zone in gray
        UI.setColor(Color.lightGray);
        UI.fillArc(GUN_X-SHOOTING_CIRCLE/2, GUN_Y-SHOOTING_CIRCLE/2, SHOOTING_CIRCLE, SHOOTING_CIRCLE, 0, 180);

        // Redraw the gun in black
        UI.setColor(Color.black);
        UI.fillRect(GUN_X-5, GUN_Y-5, 10, 10);

        // Redraw the disks, and
        // the pile of small red squares illustrating the remaining rounds
        for (Disk d : disks){
            d.draw();
        }
        
        UI.setColor(Color.red);
        for (int i=0; i<this.shotsRemaining; i++){
            UI.fillRect(10 + i*5, GUN_Y + 10, 4, 4);
        }
    }

    /** 
     * Ask the user for a file to open,
     * then read all the game attributes
     * (which must mirror what was saved in the saveGame method)
     * re-create the game
     */    
    public void loadGame(){
        /*# YOUR CODE HERE */
          try {
            String filename = UIFileChooser.open("Select file to load game");
            if (filename == null) return;
            
            Path path = Paths.get(filename);
            List<String> lines = Files.readAllLines(path);
            
            if (lines.size() < 2) return;
            
            // Read game state
            String[] gameState = lines.get(0).split(" ");
            this.score = Double.parseDouble(gameState[0]);
            this.shotsRemaining = Integer.parseInt(gameState[1]);
            
            // Clear current disks
            this.disks.clear();
            this.explodedDisks.clear();
            
            // Read disks
            for (int i=1; i<lines.size(); i++){
                String[] parts = lines.get(i).split(" ");
                if (parts.length >= 3){
                    double x = Double.parseDouble(parts[0]);
                    double y = Double.parseDouble(parts[1]);
                    int damage = Integer.parseInt(parts[2]);
                    Disk d = new Disk(x, y, damage);
                    if (d.isBroken()){
                        explodedDisks.add(d);
                    } else {
                        disks.add(d);
                    }
                }
            }
            
            this.redraw();
            this.updateScore();
        } catch (IOException e) {
            UI.println("Failed to load game: " + e);
        }
    }

    /**
     * Ask the user to select a file and save the current game to the selected file
     * You need to save:
     * - The current score and the number of remaining shots
     * - The coordinates and the damage of each disk
     *   Hint: use the toString method
     */
    public void saveGame(){
        try {
            String filename = UIFileChooser.save("Select file to save game");
            if (filename == null) return;
            
            PrintWriter pw = new PrintWriter(new File(filename));
            
            // Save game state
            pw.println(this.score + " " + this.shotsRemaining);
            
            // Save active disks
            for (Disk d : disks){
               pw.println(d.toString());
            }
            
            // Save exploded disks
            for (Disk d : explodedDisks){
                pw.println(d.toString());
            }
            
            pw.close();
        } catch (IOException e) {
            UI.println("Failed to save game: " + e);
        }
    }

    public static void main(String[] args){
        DiskGame dg = new DiskGame();
        dg.setupGUI();
        dg.startGame();
    }
}
