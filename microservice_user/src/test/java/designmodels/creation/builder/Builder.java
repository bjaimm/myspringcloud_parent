package designmodels.creation.builder;

public interface Builder {
    void buildRoof();
    void buildBasement();
    void buildWall();
    Building getBuilding();
}
