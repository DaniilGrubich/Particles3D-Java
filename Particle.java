
public class Particle {
    double x, y, z;
    double oldX, oldY, oldZ;

    public Particle(double x, double y, double z){
        this.x = x - Main.window.getWidth()/2;
        this.y = y - Main.window.getHeight()/2;
        this.z = x - Main.window.getWidth()/2;
        oldX = x;
        oldY = y;
        oldZ = x;
    }

    public void integrate(boolean borders) {
        double velX;
        double velY;
        double velZ;

        velX = x - oldX;
        velY = y - oldY;
        velZ = z = oldZ;

        oldX = x;
        oldY = y;
        oldZ = z;

        x+=velX;
        y+=velY;
        z+=velZ;

        if(!borders) {
            if (x > Main.window.getWidth())
                x -= Main.window.getWidth();
            if (x < 0)
                x += Main.window.getWidth();
            if(!Main.horizontalBorders){
                if (y > Main.window.getHeight())
                    y -= Main.window.getHeight();
                if (y < 0)
                    y += Main.window.getHeight();
            }
        }
    }

    public void attract(double x, double y, double z,  double attraction) {
//        System.out.println(x + "-" + this.x + "-" + y + "-" + this.y);
        double dx = x - this.x;
        double dy = y - this.y;
        double dz = z - this.z;

        double distance = Math.sqrt((dx*dx)+(dy*dy)+dz*dz);
//        System.out.println("distance: " + distance);

        this.x += dx/(distance*(1/attraction));
        this.y += dy/(distance*(1/attraction));
        this.z += dz/(distance*(1/attraction));
//        System.out.println("---" + this.x + "--" + this.y);
    }
}
