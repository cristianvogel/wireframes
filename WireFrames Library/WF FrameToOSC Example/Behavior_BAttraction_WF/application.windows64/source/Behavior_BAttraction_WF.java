import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import punktiert.math.Vec; 
import punktiert.physics.*; 
import oscP5.*; 
import netP5.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Behavior_BAttraction_WF extends PApplet {

// Punktiert is a particle engine based and thought as an extension of Karsten Schmidt's toxiclibs.physics code. 
// This library is developed through and for an architectural context. Based on my teaching experiences over the past couple years. (c) 2012 Daniel K\u00f6hler, daniel@lab-eds.org

//here: global attractor force connected to mouse position






OscP5 oscP5;

//FrameLength WireFrames
int frameLength;

// world object
VPhysics physics;

// attractor
BAttraction [] attr;

// number of particles in the scene
int amount = 200;

// kyma x and y changing variables
Float kx,ky;

public void setup() {
  
  noStroke();

 /* start oscP5, listening for incoming messages at port 12000 */
  oscP5 = new OscP5(this,12000);

// set up WireFrames

frameLength = 64;

//set up physics
  physics = new VPhysics();
  physics.setfriction(.4f);

  // new AttractionForce: (Vec pos, radius, strength)
  
  attr = new BAttraction[frameLength];
  for ( int y=0; y<frameLength; y++) {
  attr[y] = new BAttraction(new Vec(width * .5f, height * .5f), 64, .1f);
  physics.addBehavior(attr[y]);
  }

  for (int i = 0; i < amount; i++) {
    // val for arbitrary radius
    float rad = random(2, 20);
    // vector for position
    Vec pos = new Vec(random(rad, width - rad), random(rad, height - rad));
    // create particle (Vec pos, mass, radius)
    VParticle particle = new VParticle(pos, 4, rad);
    // add Collision Behavior
    particle.addBehavior(new BCollision());
    // add particle to world
    physics.addParticle(particle);
  }
}

public void draw() {

  background(255);

  physics.update();

  noFill();
  stroke(200, 0, 0);
  // set pos to mousePosition
  
  for (int i=0; i<frameLength;i++) {
  //attr[1].setAttractor(new Vec(mouseX, mouseY));
  ellipse(attr[i].getAttractor().x, attr[i].getAttractor().y, attr[i].getRadius(), attr[i].getRadius());


  }
    noStroke();
  fill(0, 255);
  for (VParticle p : physics.particles) {
    ellipse(p.x, p.y, p.getRadius() * 2, p.getRadius() * 2);
  }
}

/* incoming osc message are forwarded to the oscEvent method. */
public void oscEvent(OscMessage theOscMessage) {
  /* print the address pattern and the typetag of the received OscMessage */
  print("### received an osc message.");
  print(" addrpattern: "+theOscMessage.addrPattern());
  println(" typetag: "+theOscMessage.typetag());
  
  String addr = theOscMessage.addrPattern(); 
  Integer TrackIndex = parseInt (addr.replaceAll("\\D+",""));
 // attr[1].setAttractor(new Vec(mouseX, mouseY));
  println ("extract track number to:" + TrackIndex );
  if (addr.contains(".x")) {
  kx = theOscMessage.get(0).floatValue();
  }  
    if (addr.contains(".y")) {
  ky = theOscMessage.get(0).floatValue();
  } 
  attr[TrackIndex-1].setAttractor( new Vec(kx*width,(1-ky)*height));
  
  
}
  public void settings() {  size(800, 600); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Behavior_BAttraction_WF" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
