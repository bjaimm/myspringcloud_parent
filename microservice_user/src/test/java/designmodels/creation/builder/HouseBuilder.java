package designmodels.creation.builder;

public class HouseBuilder implements Builder{

    private Building house;

    public HouseBuilder(){
        house=new Building();
    }

    @Override
    public void buildRoof() {
        house.setRoof("搭建屋顶");
    }

    @Override
    public void buildBasement() {
        house.setBasement("打桩地基");
    }

    @Override
    public void buildWall() {
        house.setWall("垒墙");
    }

    @Override
    public Building getBuilding() {
        return house;
    }
}
