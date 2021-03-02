package geometry.objects3D;

import geometry.objects2D.Matrix2;
import utils.throwables.ImpossibleMatrixException;

import java.util.Arrays;

public class Matrix3 {
    private final double[][] matrix;

    public Matrix3(double[][] matrix) {
        if(matrix.length != 3 || matrix[0].length != 3)
            throw new ImpossibleMatrixException();
        this.matrix = matrix.clone();
    }

    public Matrix3(){
        matrix = new double[3][3];
    }

    public double det(){
        return matrix[0][0]*matrix[1][1]*matrix[2][2]
                +matrix[0][2]*matrix[1][0]*matrix[2][1]
                +matrix[2][0]*matrix[0][1]*matrix[1][2]
                -matrix[0][2]*matrix[1][1]*matrix[2][0]
                -matrix[0][0]*matrix[2][1]*matrix[1][2]
                -matrix[2][2]*matrix[0][1]*matrix[1][0];
    }

    public void multiply(double k){
        for(int i = 0; i < 3; i++)
            for(int j = 0; j < 3; j++)
                matrix[i][j] *= k;
    }

    public Matrix2 minor(int i, int j){
        double[][] matrix2 = new double[2][2];
        for(int i2 = 0; i2 < 3; i2++){
            for(int j2 = 0; j2 < 3; j2++){
                if(i2 == i || j2 == j)
                    continue;
                int i3 = i2 < i ? i2 : i2-1;
                int j3 = j2 < j ? j2 : j2-1;
                matrix2[i3][j3] = matrix[i2][j2];
            }
        }

        return new Matrix2(matrix2);
    }

    public void transpose(){
        for(int i = 0; i < 3; i++){
            for (int j = 0; j < i; j++) {
                matrix[i][j] += matrix[j][i];
                matrix[j][i] = matrix[i][j] - matrix[j][i];
                matrix[i][j] = matrix[i][j] - matrix[j][i];
            }
        }
    }

    public Matrix3 reversedMatrix(){
        double d = det();
        if(d == 0)
            throw new ImpossibleMatrixException();
        Matrix3 m = new Matrix3();
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                m.matrix[j][i] = ((i+j) % 2 != 0 ? -1 : 1)*minor(i,j).det();
            }
        }
        m.multiply(1/d);
        return m;
    }



    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(double[] d : matrix){
            sb.append(Arrays.toString(d)).append("\n");
        }
        return sb.toString();
    }
}
