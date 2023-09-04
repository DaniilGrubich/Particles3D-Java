
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.Random;

import java.util.Timer;
import java.util.TimerTask;

public class Main extends JFrame {
    static int FPS = 60;

    static JFrame window;
    static Particle particles[];
    static double attraction = 1;
    static JLabel lblInfo;
    static int size = 5;
    static int scaledSize0;
    static int x, y;
    static boolean InfiniteBorders = true;
    static boolean horizontalBorders = true;
    static boolean trace = false;
    static int psrticleShape = 6; //0 - circle, 1 - point, 2 - line, 3 - triangle, 4 - square, 5 - pentagon, 6 - hexagon
    static boolean fill = true;
    static boolean fullScrean = false;
    static int nParticles;

    static double scale = .1;
    static double scaleo = scale;

    //Color variables
    static float colorStep = (float) .0001;
    static Color particleColor = Color.white;
    static float colorCounter = 0;

    static double r = 0;
    static double g = 0;
    static double b = 1;

    static boolean rb = true;
    static boolean gb = false;
    static boolean bb = false;


    static boolean BWMOde = false;

    static Timer timer = new Timer();
    static BufferStrategy bs;


    static Thread particlesProcessI;
    static Thread particlesProcessII;
    static Thread particlesProcessIII;

    static double xAngle = 0;
    static double yAngle = 0;
    static double zAngle = 0;

    static Point originOfTouch;
    static boolean btn1Pressed = false;
    static boolean axis = true;

    private static void createGUI(int w, int h, boolean full){
        window = new JFrame("Particles");
        window.setSize(w, h);
        if(full){
            window.setExtendedState(JFrame.MAXIMIZED_BOTH);
            window.setUndecorated(true);
        }

        window.setLayout(new BorderLayout());
        window.setBackground(Color.BLACK);
        window.add(lblInfo = new JLabel(""), BorderLayout.SOUTH);

        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);

    }

    public static void main(String[] args) throws InterruptedException, ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        try {
            nParticles = Integer.parseInt(JOptionPane.showInputDialog(null, "Number of particles: "));
        }catch (Exception e){
            nParticles = 100;
        }

        if(JOptionPane.showInputDialog(null, "Fullscrean?(-/+):").equals("+"))
            fullScrean = true;
        else
            fullScrean = false;


        createGUI(720, 500, fullScrean);
        particles = new Particle[nParticles];
        Random random = new Random();

        for(int i = 0; i<particles.length; i++)
        {
            particles[i] = new Particle((random.nextInt(window.getWidth())-window.getWidth()/2)/scale,
                    (random.nextInt(window.getHeight())-window.getHeight()/2)/scale,
                    (random.nextInt(window.getWidth())-window.getWidth()/2)/scale);
        }

        //myCanvas canvas;
        Canvas canvas = new Canvas();
        canvas.setFocusable(false);
        window.add(canvas, BorderLayout.CENTER);
        canvas.createBufferStrategy(2);
        bs = canvas.getBufferStrategy();

        size/=scale;
        scaledSize0 = size;

        canvas.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if(e.getWheelRotation()>0){
                    scale/=2;
                }else{
                    scale*=2;
                }
            }
        });

        canvas.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if(btn1Pressed){
                    double dx = e.getX() - originOfTouch.x;
                    double dy = e.getY() - originOfTouch.y;
                    double r = Math.sqrt(dx*dx+dy*dy)*100;
                    if(r==0)
                        r = 1;

                    xAngle+=Math.toDegrees(Math.asin(dy/r));
                    yAngle+=Math.toDegrees(Math.asin(dx/r));
                }
            }});

        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getButton() == 1){
                    btn1Pressed = true;
                    originOfTouch = new Point(e.getX(), e.getY());
                }else if(e.getButton() == 2)
                    scale = .1;
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if(e.getButton() == 1){
                    btn1Pressed = true;
                    originOfTouch = new Point(e.getX(), e.getY());
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if(e.getButton() == 1){
                    btn1Pressed = false;
                    originOfTouch = null;
                }
            }
        });

        window.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if(e.getKeyChar()=='+')
                    size += 5/scale;
                else if(e.getKeyChar()=='-')
                    size -= 5/scale;
                else if(e.getKeyChar()=='B'||e.getKeyChar()=='b')
                    InfiniteBorders = !InfiniteBorders;
                else if(e.getKeyChar() == 't' || e.getKeyChar() == 'T')
                    trace = !trace;
                else if(e.getKeyChar() == 'm' || e.getKeyChar() == 'M')
                    BWMOde = !BWMOde;

            }

            @Override
            public void keyPressed(KeyEvent e) {
            if(e.getKeyCode() == KeyEvent.VK_F2)
                    axis = !axis;

//                if(e.getKeyCode() == KeyEvent.VK_1)
//                    size = (int) (1/scale);
//                else if(e.getKeyCode() == KeyEvent.VK_2)
//                    size = (int) (5/scale);
//                else if(e.getKeyCode() == KeyEvent.VK_3)
//                    size = (int) (10/scale);

                if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
                    System.exit(0);
                else if (e.getKeyCode() == KeyEvent.VK_Q)
                    FPS+=10;
                else if (e.getKeyCode() == KeyEvent.VK_A)
                    FPS-=10;
                else if (e.getKeyCode() == KeyEvent.VK_W)
                    attraction = attraction + .5;
                else if (e.getKeyCode() == KeyEvent.VK_S)
                    attraction = attraction - .5;
                else if(e.getKeyCode() == KeyEvent.VK_E)
                    attraction = attraction + 1;
                else if(e.getKeyCode() == KeyEvent.VK_D)
                    attraction = attraction - 1;


                if (e.getKeyCode() == KeyEvent.VK_H){
                    if(psrticleShape == 6)
                        psrticleShape = 0;
                    else
                        psrticleShape++;
                } else if(e.getKeyCode() == KeyEvent.VK_F)
                    fill = !fill;
                else if (e.getKeyCode()==KeyEvent.VK_Z)
                    attraction = 0;
                else if (e.getKeyCode() == KeyEvent.VK_C) {
                    for (int i = 0; i < 180; i++) {
                        double z = i-90;
                        for (int j = i*particles.length/180; j < (i*particles.length/180)+(particles.length/180); j+=1) {
                            double randomTheta = Math.toRadians(random.nextInt(361));
                            Particle p = particles[j];
                            double zFactor = (z/90)* Math.cos(randomTheta);
                            double leftOver = 1-Math.abs(z);
                            double xFactor = leftOver * Math.cos(randomTheta);
                            double yFactor = leftOver * Math.sin(randomTheta);

                            p.x = x + xFactor *(300+random.nextInt(10))/scaleo;
                            p.y = y + yFactor *(300+random.nextInt(10))/scaleo;
//                            p.z = zFactor *(300+random.nextInt(10))/scaleo;
                            p.z = z*(300+random.nextInt(10))/scaleo;
                            System.out.println(p.z);
                            p.oldY = p.y;
                            p.oldX = p.x;
                            p.oldZ = p.z;
                        }

                    }
                } else if (e.getKeyCode() == KeyEvent.VK_R) {
                    for (Particle p : particles) {
                        p.x = random.nextInt((int) (window.getWidth()/scaleo)) - window.getWidth()/2.0/scaleo;
                        p.y = random.nextInt((int) (window.getHeight()/scaleo)) - window.getHeight()/2.0/scaleo;
                        p.z = random.nextInt((int) (window.getWidth()/scaleo)) - window.getWidth()/2.0/scaleo;
                        p.oldX = p.x;
                        p.oldY = p.y;
                        p.oldZ = p.z;
                        size = scaledSize0;



                        InfiniteBorders = true;
                        trace = false;
                    }
                } else if(e.getKeyCode() == KeyEvent.VK_N){
                    horizontalBorders = !horizontalBorders;
                }else if(e.getKeyCode() == KeyEvent.VK_X) {
                    xAngle = 0;
                    yAngle = 0;
                    zAngle = 0;
                }else if(e.getKeyCode() == KeyEvent.VK_F1){
                    String message = "Attaraction: \n" +
                            "'Q': SlowRate, 'A': IncreaseRate \n" +
                            "'W': -.5, 'S': +.5 \n" +
                            "'E': -1, 'D': +1 \n" +
                            "'Z': attraction = 1 \n" +
                            "---\n" +
                            "Particle: \n" +
                            "Scroll: change size, 'H': change shape\n" +
                            "'F': on/off fill \n" +
                            "---- \n" +
                            "'R': Reset, 'M': Black-White mode \n" +
                            "'X': Angles reset \n" +
                            "'T': on/off trace, 'B': on/off Borders \n" +
                            "'C': Circle order \n" +
                            "'Esc': exit";

                    JOptionPane.showMessageDialog(null, message);
                }

            }
        });



//        float red = 1;
//        float green = (float) .1;
//        float blue = (float) .1;
//        double colorStep = .01;



        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                colorCounter+=colorStep;
                if(colorCounter>1){
                    colorCounter=colorCounter-1;
                }

                if (!BWMOde)
                    particleColor = Color.getHSBColor(colorCounter, 1, 1);
                else
                    particleColor = Color.BLACK;


                Graphics2D g2 = (Graphics2D)bs.getDrawGraphics();
                if(!trace) {
                    if(!BWMOde)
                        g2.setColor(Color.black);
                    else
                        g2.setColor(Color.WHITE);

                    g2.fillRect(0, 0, window.getWidth(), window.getHeight());
                }

                g2.translate(window.getWidth()/2, window.getHeight()/2);

                //draw axis
               if(axis){
                   double[] rotatedXo = Matrix.rotateXYZpoint(xAngle, yAngle, zAngle, -window.getWidth()/2.0, 0, 0);
                   double[] rotatedXf = Matrix.rotateXYZpoint(xAngle, yAngle, zAngle, window.getWidth()/2.0, 0, 0);
                   g2.setColor(new Color(70, 174, 74));
                   g2.drawLine((int)rotatedXo[0], (int)rotatedXo[1], (int)rotatedXf[0], (int)rotatedXf[1]);
                   ////
                   double[] rotatedYo = Matrix.rotateXYZpoint(xAngle, yAngle, zAngle,0 , -window.getHeight()/2.0, 0);
                   double[] rotatedYf = Matrix.rotateXYZpoint(xAngle, yAngle, zAngle, 0, window.getHeight()/2.0, 0);
                   g2.setColor(new Color(245, 64, 44));
                   g2.drawLine((int)rotatedYo[0], (int)rotatedYo[1], (int)rotatedYf[0], (int)rotatedYf[1]);
                   ///
                   double[] rotatedZo = Matrix.rotateXYZpoint(xAngle, yAngle, zAngle, 0, 0, -window.getWidth()/2.0);
                   double[] rotatedZf = Matrix.rotateXYZpoint(xAngle, yAngle, zAngle, 0, 0, window.getWidth()/2.0);
                   g2.setColor(new Color(16, 146, 244));
                   g2.drawLine((int)rotatedZo[0], (int)rotatedZo[1], (int)rotatedZf[0], (int)rotatedZf[1]);
               }

                g2.scale(scale, scale);

                x = MouseInfo.getPointerInfo().getLocation().x - window.getX() - window.getWidth()/2;
                y = MouseInfo.getPointerInfo().getLocation().y - window.getY() - window.getHeight()/2;

//                double[] rotatedXYmouse = Matrix.rotateXYZpoint(0, 0, 0, x/scale, y/scale, x/scale);
//                SwingUtilities.updateComponentTreeUI(window);

                for(Particle p : particles)
                {
                    p.attract(x/scale, y/scale, x/scale , attraction);
                    p.integrate(InfiniteBorders);
                    g2.setColor(particleColor);

                    displayParticle(g2,p,psrticleShape, fill);


                }
//                particlesProcessI.start();
//                particlesProcessII.start();
//                particlesProcessIII.start();


                   lblInfo.setText("#Particels: " + nParticles + "   Attraction: " + attraction + "   Size:" + size + "   ∞ Borders: " + InfiniteBorders +
                           "   Trace: " + !trace + "   Shape: " + psrticleShape + "   Fill: " + fill + "   F1 - help" + " Axis: " + axis + "\t\t SCALE: " + scale);
                
                g2.dispose();
                bs.show();

            }
        }, 1000/FPS/2, 1000/FPS/2);



    }

    private static void displayParticle(Graphics2D g, Particle p, int type, boolean fill) {
        double[] location = Matrix.rotateXYZpoint(xAngle, yAngle, zAngle, p.x, p.y, p.z);
        int localX = (int) location[0];
        int localY = (int) location[1];

        if (type == 1)
            g.drawLine((int)localX, (int)localY, (int)localX, (int)localY);
        else if (type == 2)
            g.drawLine((int)localX, (int)localY, (int)localX+size, (int)localY);
        else {

            if (type == 0) {
                if(fill)
                    g.fillOval((int) localX - size / 2, (int) localY - size / 2, size, size);
                else
                    g.drawOval((int)localX - size/2, (int)localY-size/2, size, size);
            }
            else if (type == 3){
                int xs[] = {(int)localX, (int)localX-size, (int)localX-size/2};
                int ys[] = {(int)localY, (int)localY, (int)localY-size};

                if(fill)
                    g.fillPolygon(xs, ys, 3);
                else
                    g.drawPolygon(xs, ys, 3);
            }
            else if (type == 4) {
                if(fill)
                    g.fillRect((int) localX, (int) localY, size, size);
                else
                    g.drawRect((int)localX, (int)localY, size, size);
            }
            else if (type == 5){
                int xs[] = {(int)localX, (int)localX+size/4, (int)localX+size*3/4, (int)localX+size, (int)localX+size/2};
                int ys[] = {(int)localY, (int)localY-size/3, (int)localY-size/3, (int)localY, (int)localY+size/3};

                if(fill)
                    g.fillPolygon(xs, ys, 5);
                else
                    g.drawPolygon(xs, ys, 5);
            }
            else if (type == 6){
                int xs[] = {(int)localX, (int)localX+size/3, (int)localX+size*2/3, (int)localX+size, (int)localX+size*2/3, (int)localX+size/3};
                int ys[] = {(int)localY, (int)localY-size/3, (int)localY-size/3, (int)localY, (int)localY+size/3, (int)localY+size/3};

                if(fill)
                    g.fillPolygon(xs, ys, 6);
                else
                    g.drawPolygon(xs, ys, 6);
            }

        }
    }

//    public static class ParticlesThread implements Runnable{
//        private Particle[] particles;
//        Graphics2D g;
//
//        ParticlesThread(Particle[] particles, BufferStrategy bs){
//            this.particles = particles;
//            g = (Graphics2D)bs.getDrawGraphics();
//        }
//
//        @Override
//        public void run() {
//            for (Particle p :
//                    particles) {
//                p.attract(x, y, attraction);
//                p.integrate(InfiniteBorders);
//                g.setColor(particleColor);
//
//                displayParticle(g,p,psrticleShape, fill);
//                bs.show();
//            }
//        }
//    }
}
