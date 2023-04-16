package designmodels.creation.builder;

public class ApartmentBuilder implements Builder{

    private Building house;

    public ApartmentBuilder(){
        house=new Building();
    }

    @Override
    public void buildRoof() {
        house.setRoof("搭建别墅屋顶");
    }

    @Override
    public void buildBasement() {
        house.setBasement("打桩别墅地基");
    }

    @Override
    public void buildWall() {
        house.setWall("垒别墅墙");
    }

    @Override
    public Building getBuilding() {
        return house;
    }
}
