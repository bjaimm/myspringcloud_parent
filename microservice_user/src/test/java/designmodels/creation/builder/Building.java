package designmodels.creation.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Building {

    private List<String> building = new ArrayList<>();

    public void setRoof(String roof){
        building.add(roof);
    }

    public void setBasement(String basement){
        building.add(basement);
    }

    public void setWall(String wall){
        building.add(wall);
    }

    @Override
    public String toString(){
        return this.building.stream().collect(Collectors.joining());
    }

}
