package designmodels.creation.builder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Director {
    private Builder builder;

    public Director(Builder builder){
        this.builder = builder;
    }

    public void setBuilder(Builder builder){
        this.builder = builder;
    }

    public Building direct(){
        log.info("第一步：打地桩");
        builder.buildBasement();

        log.info("第二步：垒墙体");
        builder.buildWall();

        log.info("第三步：盖房顶");
        builder.buildRoof();

        return builder.getBuilding();
    }
}
