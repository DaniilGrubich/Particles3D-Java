class Matrix {
    static double[][] projection = new double[][]{
            {1, 0, 0},
            {0, 1, 0}
    };

    static double[][] MatrixMult(double[][] a, double[][]b){
        int colsA = a[0].length;
        int rowsA = a.length;
        int colsB = b[0].length;
        int rowsB = b.length;

        if(colsA != rowsB) {
            System.out.println("colsA != rowsB");
            return null;
        }

        double result[][] = new double[rowsA][colsB];

        for (int i = 0; i < rowsA; i++) {
            for (int j = 0; j < colsB; j++) {
                double sum = 0;
                for (int k = 0; k < colsA; k++) {
                    sum+=a[i][k] * b[k][j];
                }
                result[i][j] = sum;
            }
        }

        return result;
    }


    static double[][] vectToMat(PVector v){
        double[][] m = new double[3][1];

        m[0][0] = v.x;
        m[1][0] = v.y;
        m[2][0] = v.z;

        return m;
    }

    static PVector matToVect(double[][] m){
        PVector v = new PVector();

        v.x = m[0][0];
        v.y = m[1][0];

        if(m.length>2)
            v.z = m[2][0];

        return  v;
    }

    static double[][] rotateX(double angle){
        double r = Math.toRadians(angle);
        return new double[][]{
                {1, 0, 0},
                {0, Math.cos(r), -Math.sin(r)},
                {0, Math.sin(r), Math.cos(r)}
        };
    }

    static double[][] rotateY(double angle){
        double r = Math.toRadians(angle);
        return new double[][]{
                {Math.cos(r), 0, -Math.sin(r)},
                {0 , 1, 0},
                {Math.sin(r), 0, Math.cos(r)}
        };
    }

    static double[][] rotateZ(double angle){
        double r = Math.toRadians(angle);
        return new double[][]{
                {Math.cos(r), -Math.sin(r), 0},
                {Math.sin(r), Math.cos(r), 0},
                {0, 0, 1}
        };
    }

    static double[] rotateXYZpoint(double xAngle, double yAngle, double zAngle, double x, double y, double z){
        double[][] point3D = new double[][]{
                {x},
                {y},
                {z}
        };

        double[][] Rx = rotateX(xAngle);
        double[][] Ry = rotateY(yAngle);
        double[][] Rz = rotateZ(zAngle);

        point3D = MatrixMult(Rx, point3D);
        point3D = MatrixMult(Ry, point3D);
        point3D = MatrixMult(Rz, point3D);

        PVector rotatedPoint2D = matToVect(MatrixMult(projection, point3D));
        return new double[]{rotatedPoint2D.x, rotatedPoint2D.y};

    }
}



class PVector {
    double x,y,z;
    public PVector(double x, double y){
        this.x = x;
        this.y = y;
    }

    public PVector(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }



    PVector(){}
}

