package geometry.objects3D;

public interface Object3D {

    /**
     * @return Минимальная прямоугольная область, содержащая объект
     */
    Region3D getRegion();
}
