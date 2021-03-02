package geometry.objects2D;

import utils.throwables.ImpossibleMatrixException;

import java.util.Arrays;

public class Matrix2 {
    public final double[][] matrix;

    public Matrix2(double[][] matrix) {
        if(matrix.length != 2 || matrix[0].length != 2)
            throw new ImpossibleMatrixException();
        this.matrix = matrix.clone();
    }

    public double det(){
        return matrix[0][0]*matrix[1][1]-matrix[1][0]*matrix[0][1];
    }

    public void multiply(double k){
        for(int i = 0; i < 2; i++)
            for(int j = 0; j < 2; j++)
                matrix[i][j] *= k;
    }

    public void transpose(){
        matrix[0][1] += matrix[1][0];
        matrix[1][0] = matrix[0][1] - matrix[1][0];
        matrix[0][1] = matrix[0][1] - matrix[1][0];
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
